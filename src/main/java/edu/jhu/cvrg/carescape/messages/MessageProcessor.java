package edu.jhu.cvrg.carescape.messages;

/*
Copyright 2016-2017 Johns Hopkins University Institute for Computational Medicine

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

/**
* @author Christian Jurado, Stephen Granite
* 
*/

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import edu.jhu.cvrg.sapphire.data.response.bindescriptor.Block;
import edu.jhu.cvrg.sapphire.data.response.bindescriptor.GroupBT;
import edu.jhu.cvrg.sapphire.data.response.bindescriptor.Parameter;
import edu.jhu.cvrg.sapphire.data.response.bindescriptor.ParameterSet;
import edu.jhu.cvrg.sapphire.data.response.bindescriptor.Parameters;
import edu.jhu.cvrg.sapphire.data.response.bindescriptor.SubParameterInfo;

import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.nio.ByteBuffer;
import java.lang.Math;

public class MessageProcessor {

	private BinDescriptorMessage binDescriptorMessage;
	private BinaryDataMessage binaryDataMessage;
	private HashMap<String, ArrayList<Double>> actualData;
	private int blockSQN;
	private Date startDateTime;
	private enum BitSize {Byte(8),UnsignedByte(8),Short(16),UnsignedShort(16),Int(32),UnsignedInt(32),Long(64),UnsignedLong(64),Float(32),Double(64),Boolean(8),UTF8(8),NTP64(64),NTP32(32),BPTR16(16),Unsigned128(128),Signednn(0),Unsignednn(0),Nullnn(0),Voidnn(0),Padnn(0),Bool1(1),Bool8(8),Bool16(16);

		private int value;

		BitSize(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static boolean contains(String s) {
			for(BitSize bitSize: values())
				if (bitSize.name().equals(s)) 
					return true;
			return false;
		} 

	};

	private enum messageByteOrder {bigEndian(ByteOrder.BIG_ENDIAN),littleEndian(ByteOrder.LITTLE_ENDIAN);

		private ByteOrder byteOrder;

		messageByteOrder(ByteOrder byteOrder) {
			this.byteOrder = byteOrder;	
		}

		public ByteOrder getByteOrder() {
			return byteOrder;
		}

	};

	public MessageProcessor(BinDescriptorMessage binDescriptorMessage, BinaryDataMessage binaryDataMessage) {

		setBinDescriptorMessage(binDescriptorMessage);
		setBinaryDataMessage(binaryDataMessage);
		setActualData(new HashMap<String, ArrayList<Double>>());
	}

	public BinDescriptorMessage getBinDescriptorMessage() {
		return binDescriptorMessage;
	}

	public void setBinDescriptorMessage(BinDescriptorMessage binDescriptorMessage) {
		this.binDescriptorMessage = binDescriptorMessage;
	}

	public BinaryDataMessage getBinaryDataMessage() {
		return binaryDataMessage;
	}

	public void setBinaryDataMessage(BinaryDataMessage binaryDataMessage) {
		this.binaryDataMessage = binaryDataMessage;
	}

	public HashMap<String, ArrayList<Double>> getActualData() {
		if (actualData.isEmpty()) processBinaryDataMessage();
		return actualData;
	}

	public void setActualData(HashMap<String, ArrayList<Double>> actualData) {
		this.actualData = actualData;
	}

	public int getBlockSQN() {
		return blockSQN;
	}

	public void setBlockSQN(int blockSQN) {
		this.blockSQN = blockSQN;
	}

	public Date getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(Date startDateTime) {
		this.startDateTime = startDateTime;
	}

	private void processBinaryDataMessage() {

		Block block = getBinDescriptorMessage().getBinDescriptor().getBlock();
		byte[] messageInBytes = getBinaryDataMessage().getMessageXML().getBytes();
		ByteArrayInputStream is = new ByteArrayInputStream(messageInBytes);
		BufferedInputStream dataBis = new BufferedInputStream(is);
		ByteOrder localByteOrder = messageByteOrder.valueOf(getBinaryDataMessage().getByteOrder()).getByteOrder();

		int formatIDSize = processBinaryType(block.getFormatId().getBinaryType());
		formatIDSize /= 8;
		ByteBuffer formatId = parse(formatIDSize, dataBis, localByteOrder, "formatId");
		@SuppressWarnings("unused")
		int formatIdInt = formatId.getInt();

		int sessionIDBTSize = processBinaryType(block.getSessionId_BT().getBinaryType());
		sessionIDBTSize *= new Integer(block.getSessionId_BT().getArraySizeBinaryType()).intValue();
		sessionIDBTSize /= 8;
		@SuppressWarnings("unused")
		ByteBuffer sessionIdbT = parse(sessionIDBTSize, dataBis, localByteOrder, "sessionIdbT");

		int blockSQNSize = processBinaryType(block.getBlockSQN().getBinaryType());
		blockSQNSize /= 8;
		ByteBuffer blockSQN = parse(blockSQNSize, dataBis, localByteOrder, "blockSQN");
		int blockSQNInt = blockSQN.getInt();
		setBlockSQN(blockSQNInt);

		int blockLengthSize = processBinaryType(block.getBlockLength().getBinaryType());
		blockLengthSize /= 8;
		ByteBuffer blockLength = parse(blockLengthSize, dataBis, localByteOrder, "blockLength");
		@SuppressWarnings("unused")
		short blockLengthShort = blockLength.getShort();

		int startDateTimeSize = processBinaryType(block.getStartDateTime().getBinaryType());
		startDateTimeSize /= 8;
		ByteBuffer startDateTime = parse(startDateTimeSize, dataBis, localByteOrder, "startDateTime");
		int startDateTimeInt = startDateTime.getInt();
		long startDateTimeLong = getUnsignedInt(startDateTimeInt);
		Date dateValue = new Date(startDateTimeLong*1000);
		Calendar c = Calendar.getInstance();
		c.setTime(dateValue);
		c.add(Calendar.YEAR, -70);
		dateValue.setTime(c.getTimeInMillis());
		setStartDateTime(dateValue);

		@SuppressWarnings("unused")
		int headerTotal = formatIDSize + sessionIDBTSize + blockSQNSize + blockLengthSize + startDateTimeSize;
		GroupBT groupBT = block.getGroupBT();
		@SuppressWarnings("unused")
		int groupBTSize = 0;
		HashMap<String, ArrayList<Double>> localActualData = new HashMap<String, ArrayList<Double>>();
		Parameters localParameters = groupBT.getParameters();
		String lastVariableName = "";
		//		HashMap<String, ArrayList<SubParameterInfo>> episodicVariableLists = new HashMap<String, ArrayList<SubParameterInfo>>();
		//		HashMap<String, String> episodicVariableNames = new HashMap<String, String>();
		//		int measurementsArraySize = 0, measurementsOffset = 0, measurementsArrayPosition = 0;
		//		ArrayList<SubParameterInfo> measurementsInfo = new ArrayList<SubParameterInfo>();
		ArrayList<ParameterSet> localParameterSets = localParameters.getParameterSets();
		for (ParameterSet localParameterSet : localParameterSets) {
			ArrayList<Parameter> localParameterArray = localParameterSet.getParameters();
			for (Parameter localParameter : localParameterArray) {
				ArrayList<SubParameterInfo> localSubParameterArray = localParameter.getSubParameterInfo();
				for (SubParameterInfo localSubParameterInfo : localSubParameterArray) {
					if (!localSubParameterInfo.getSubParameterInfoType().contains(":")) {
						if (!(localActualData.containsKey(localSubParameterInfo.getSubParameterInfoType()))) localActualData.put(localSubParameterInfo.getSubParameterInfoType(), new ArrayList<Double>());
						ArrayList<Double> temp = localActualData.get(localSubParameterInfo.getSubParameterInfoType());
						int binarySize = processBinaryType(localSubParameterInfo.getBinaryType());
						int arraySize = new Integer(localSubParameterInfo.getArraySizeBinaryType()).intValue();
						int localSubParameterInfoSize = binarySize * arraySize;
						groupBTSize += localSubParameterInfoSize;
						localSubParameterInfoSize /= 8;
						ByteBuffer subParameterInfo = parse(localSubParameterInfoSize, dataBis, localByteOrder, localSubParameterInfo.getSubParameterInfoType());
						for (int i = 0; i < arraySize; i++) {
							int localValue = 0;
							switch(binarySize) {
							case 8:
								localValue = subParameterInfo.get();
								break;
							case 16:
								localValue = subParameterInfo.getShort();							
								break;
							}
							if (!(localSubParameterInfo.getInvalidAttributes().contains(new Integer(localValue).toString()))) {
								double localScaledValue = localValue * new Double(localSubParameterInfo.getScaleFactor()).doubleValue();
								if(localSubParameterInfo.getSubParameterInfoType().endsWith("pleth.unitless.perMs")) localScaledValue /= 10;
								localScaledValue = Math.round(localScaledValue * 100.0)/100.0;
								temp.add(new Double(localScaledValue));
							}
						}
						localActualData.put(localSubParameterInfo.getSubParameterInfoType(),temp);
						lastVariableName = localSubParameterInfo.getSubParameterInfoType();
						//					} else {
						//						if (!(episodicVariableNames.containsKey(localSubParameterInfo.getSubParameterInfoType().split(":")[1]))) episodicVariableNames.put(localSubParameterInfo.getSubParameterInfoType().split(":")[1], lastVariableName);						
						//						if (!(episodicVariableLists.containsKey(localSubParameterInfo.getSubParameterInfoType().split(":")[1]))) episodicVariableLists.put(localSubParameterInfo.getSubParameterInfoType().split(":")[1], new ArrayList<SubParameterInfo>());
						//						ArrayList<SubParameterInfo> eVariables = episodicVariableLists.get(localSubParameterInfo.getSubParameterInfoType().split(":")[1]);
						//						eVariables.add(localSubParameterInfo);
						//						episodicVariableLists.put(localSubParameterInfo.getSubParameterInfoType().split(":")[1], eVariables);
					}
				}
			}
		}
		//		for (String episodicVariableList : episodicVariableLists.keySet()) {
		//			int arraySize = localActualData.get(episodicVariableNames.get(episodicVariableList)).size();
		//			int offset = localActualData.get(episodicVariableNames.get(episodicVariableList)).remove(arraySize - 1).intValue();
		//			if (!(offset == -1)) {
		//				messageInBytes = getBinaryDataMessage().getMessageXML().getBytes();
		//				is = new ByteArrayInputStream(messageInBytes);
		//				dataBis = new BufferedInputStream(is);
		//				parse(offset, dataBis, localByteOrder, "offset");
		//				offset += BitSize.valueOf("Short").getValue()/8;
		//				arraySize = parse(BitSize.valueOf("Short").getValue()/8, dataBis, localByteOrder, "arraySize").getShort();
		//				for (int i = 0; i < arraySize; i++) {
		//					for (SubParameterInfo localSubParameterInfo : episodicVariableLists.get(episodicVariableList)) {
		//						if (!(localActualData.containsKey(localSubParameterInfo.getSubParameterInfoType()))) localActualData.put(localSubParameterInfo.getSubParameterInfoType(), new ArrayList<Double>());
		//						ArrayList<Double> temp = localActualData.get(localSubParameterInfo.getSubParameterInfoType());
		//						int binarySize = processBinaryType(localSubParameterInfo.getBinaryType());
		//						groupBTSize += binarySize;
		//						binarySize /= 8;
		//						ByteBuffer subParameterInfo = parse(binarySize, dataBis, localByteOrder, localSubParameterInfo.getSubParameterInfoType());
		//						int localValue = 0;
		//						switch(binarySize) {
		//						case 8:
		//							localValue = subParameterInfo.get();
		//							break;
		//						case 16:
		//							localValue = subParameterInfo.getShort();							
		//							break;
		//						case 32:
		//							localValue = subParameterInfo.getShort();							
		//							break;
		//						}
		//						if (!(localSubParameterInfo.getInvalidAttributes().contains(new Integer(localValue).toString()))) {
		//							double localScaledValue = localValue * new Double(localSubParameterInfo.getScaleFactor()).doubleValue();
		//							if(localSubParameterInfo.getSubParameterInfoType().endsWith("pleth.unitless.perMs")) localScaledValue /= 10;
		//							localScaledValue = Math.round(localScaledValue * 100.0)/100.0;
		//							temp.add(new Double(localScaledValue));
		//						}
		//						localActualData.put(localSubParameterInfo.getSubParameterInfoType(),temp);
		//					}
		//				}
		//			}
		//		}
		setActualData(localActualData);
	}

	private int processBinaryType(String binaryType) {
		int value = 0;
		String baseBinaryType;
		if (binaryType.contains(":")) {
			baseBinaryType = StringUtils.capitalize(binaryType.split(":")[1].replaceAll("-", ""));
		} else {
			baseBinaryType = StringUtils.capitalize(binaryType.replaceAll("-", "").replaceAll("#","").replaceAll("\\s",""));
		}
		if (BitSize.contains(baseBinaryType)) {
			BitSize bitSize = BitSize.valueOf(baseBinaryType);
			value = bitSize.getValue();
		}
		return value;
	}

	private ByteBuffer parse(int arraySize, BufferedInputStream dataBis, ByteOrder byteOrder, String variableName) {

		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		byte[] byteArray = new byte[arraySize];
		ByteBuffer bbByteArray = null;
		try {
			int result = dataBis.read(byteArray);
			if (result != arraySize) {
				System.err.println(myFormat.format(System.currentTimeMillis()) + ": error occured while reading " + variableName + ".");
			}
			bbByteArray = ByteBuffer.wrap(byteArray);
			bbByteArray.order(byteOrder);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			try {
				dataBis.close();
			} catch (IOException e1) {
			}
		}
		return bbByteArray;
	}

	public static long getUnsignedInt(int x) {
		return x & 0x00000000ffffffffL;
	}

	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for ( int j = 0; j < bytes.length; j++ ) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
}
