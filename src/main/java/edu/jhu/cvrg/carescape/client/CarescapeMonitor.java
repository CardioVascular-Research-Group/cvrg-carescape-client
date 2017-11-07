package edu.jhu.cvrg.carescape.client;

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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.jhu.cvrg.carescape.messages.BinDescriptorMessage;
import edu.jhu.cvrg.carescape.messages.BinEndMessage;
import edu.jhu.cvrg.carescape.messages.BinHeaderMessage;
import edu.jhu.cvrg.carescape.messages.BinaryDataMessage;
import edu.jhu.cvrg.carescape.messages.EndSessionUpdateRequestMessage;
import edu.jhu.cvrg.carescape.messages.EndSessionUpdateResponseMessage;
import edu.jhu.cvrg.carescape.messages.EndStreamRequestMessage;
import edu.jhu.cvrg.carescape.messages.EndStreamResponseMessage;
import edu.jhu.cvrg.carescape.messages.GenericResponseMessage;
import edu.jhu.cvrg.carescape.messages.GetSessionUpdateRequestMessage;
import edu.jhu.cvrg.carescape.messages.GetSessionUpdateResponseMessage;
import edu.jhu.cvrg.carescape.messages.GetStreamRequestMessage;
import edu.jhu.cvrg.carescape.messages.GetStreamResponseMessage;
import edu.jhu.cvrg.carescape.messages.HelloMessage;
import edu.jhu.cvrg.carescape.messages.HelloResponseMessage;
import edu.jhu.cvrg.carescape.messages.InMessage;
import edu.jhu.cvrg.carescape.messages.MessageProcessor;
import edu.jhu.cvrg.carescape.messages.OutMessage;
import edu.jhu.cvrg.carescape.messages.SessionUpdateMessage;
import edu.jhu.cvrg.carescape.util.CarescapeTCPConnection;
import edu.jhu.cvrg.carescape.util.CarescapeUtilities;
import edu.jhu.cvrg.carescape.util.EnumConnectionProtocol;
import edu.jhu.cvrg.sapphire.data.response.sessionupdate.Session;
import edu.jhu.cvrg.timeseriesstore.exceptions.OpenTSDBException;
import edu.jhu.cvrg.timeseriesstore.model.IncomingDataPoint;
import edu.jhu.cvrg.timeseriesstore.opentsdb.TimeSeriesStorer;
import edu.jhu.cvrg.sapphire.data.response.bindescriptor.Parameter;
import edu.jhu.cvrg.sapphire.data.response.bindescriptor.ParameterSet;
import edu.jhu.cvrg.sapphire.data.response.bindescriptor.SubParameterInfo;
import edu.jhu.cvrg.sapphire.data.response.sessionupdate.Service;
import edu.jhu.icm.mulesoft.MuleSoftAccess;
import edu.jhu.icm.mulesoft.facilities.BeddedPatientMin;
import edu.jhu.icm.mulesoft.patients.MedicalResourceNumber;

public class CarescapeMonitor {

	@SuppressWarnings("unused")
	private EnumConnectionProtocol connectionProtocol = EnumConnectionProtocol.TCP;
	private HashMap<String, String> carescapeADTTranslator = new HashMap<String, String>();

	public static void main(String[] args) {

		new CarescapeMonitor();

	}

	public CarescapeMonitor() {

		this("<Carescape Name for unitBed>");

	}

	public CarescapeMonitor(String bedToMonitor) {

		this(bedToMonitor, <Flag for waveform data>);

	}

	public CarescapeMonitor(String bedToMonitor, boolean highSpeed) {

		this("<IP address for Carescape Instance>", bedToMonitor, highSpeed); //production GE Carescape

	}

	public CarescapeMonitor(String targetAddress, String bedToMonitor, boolean highSpeed) {

		this(targetAddress, "<Web address for OpenTSDB instance>", bedToMonitor, highSpeed);

	}

	public CarescapeMonitor(String targetAddress, String openTSDBAddress, String bedToMonitor, boolean highSpeed) {

		this(targetAddress, openTSDBAddress, new HashMap<String, String>(), bedToMonitor, highSpeed); //initialize MuleSoft Variables

	}

	public CarescapeMonitor(String targetAddress, String openTSDBAddress, HashMap<String, String> muleSoftVariables, String bedToMonitor, boolean highSpeed) {

		this(targetAddress, openTSDBAddress, muleSoftVariables, bedToMonitor, highSpeed, <Flag for sessionList echo>);
	}

	public CarescapeMonitor(String targetAddress, String openTSDBAddress, HashMap<String, String> muleSoftVariables, String bedToMonitor, boolean highSpeed, boolean echoSessions) {

		this(targetAddress, openTSDBAddress, muleSoftVariables, bedToMonitor, highSpeed, echoSessions, <Minutes to wait before OpenTSDB data send>);

	}

	public CarescapeMonitor(String targetAddress, String openTSDBAddress, HashMap<String, String> muleSoftVariables, String bedToMonitor, boolean highSpeed, boolean echoSessions, int minutesToWait) {

		this(targetAddress, openTSDBAddress, muleSoftVariables, bedToMonitor, highSpeed, echoSessions, minutesToWait, "<Flat file storage location>");		

	}

	public CarescapeMonitor(String targetAddress, String openTSDBAddress, HashMap<String, String> muleSoftVariables, String bedToMonitor, boolean highSpeed, boolean echoSessions, int minutesToWait, String rootDirectory) {

		HashMap<String, String> translator = new HashMap<String, String>();
		//HashMap to put Carescape to ADT unitBed mapping in
		setCarescapeADTTranslator(translator);

		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		CarescapeTCPConnection tcpConnection = null;

		try {
			String unit = bedToMonitor.split("-")[0].replaceAll("\\d", "");
			String bed = "0" + bedToMonitor.split("-")[1];
			String hospitalName = getCarescapeADTTranslator().get(unit).split("\\.")[0];
			String unitId = getCarescapeADTTranslator().get(unit).split("\\.")[1];

			MuleSoftAccess msa = new MuleSoftAccess();
			if (muleSoftVariables.isEmpty()) {
				muleSoftVariables = msa.getMuleSoftVariables();
			}
			msa.setMuleSoftVariables(muleSoftVariables);
			HashMap<String,Object> muleSoftCriteria = new HashMap<String, Object>();
			muleSoftCriteria.put("msa", msa);
			muleSoftCriteria.put("hospitalName", hospitalName);
			muleSoftCriteria.put("unitId", unitId);
			muleSoftCriteria.put("bed", bed);
			muleSoftCriteria.put("bedToMonitor", bedToMonitor);
			muleSoftCriteria.put("rootDirectory", rootDirectory);
			muleSoftCriteria.put("bPMin", new BeddedPatientMin());
			muleSoftCriteria.put("mrn", new MedicalResourceNumber());
			muleSoftCriteria.put("tcpConnection", tcpConnection);

			muleSoftCriteria = CarescapeUtilities.checkBedSubjectinADT(muleSoftCriteria);
			String filePath = (String) muleSoftCriteria.get("filePath");

			muleSoftCriteria = connectAndSetupIO(targetAddress, bedToMonitor, highSpeed, muleSoftCriteria);
			tcpConnection = (CarescapeTCPConnection) muleSoftCriteria.get("tcpConnection");
			String sessionId = (String) muleSoftCriteria.get("sessionId");
			BufferedWriter bw = (BufferedWriter) muleSoftCriteria.get("bw");
			BufferedReader br = (BufferedReader) muleSoftCriteria.get("br");
			
			SessionUpdateMessage sessionUpdateMessage = obtainSessionUpdate(sessionId, bw, br);
			echoCarescapeSessionInfo(sessionUpdateMessage.getSessions(), bedToMonitor, targetAddress, echoSessions);

			HashMap<String,String> tags = setTagsForBed(muleSoftCriteria);

			int requestId = 100000000;
			BinDescriptorMessage binDescriptorMessage = startStream(sessionUpdateMessage, bedToMonitor, highSpeed, requestId, bw, br);

			ArrayList<String> messages = new ArrayList<String>();
			ArrayList<String> messagesToProcess = new ArrayList<String>();
			ExecutorService executor = Executors.newSingleThreadExecutor();
			String output = "", fromServer = "";
			int messageLimit = 0, fiveMinutes = 5;
			int rate = binDescriptorMessage.getBinDescriptorXMLParser().getBinDescriptor().getBlock().getRate();
			switch (binDescriptorMessage.getBinDescriptorXMLParser().getBinDescriptor().getBlock().getUnits()) {
			case "Hz":
				messageLimit = 60;
				break;
			}
			if (!highSpeed) messageLimit /= 2;
			int checkInterval = messageLimit;
			messageLimit *= rate;
			messageLimit *= minutesToWait;
			boolean noData = false;
			int totalMessages = 0;
			System.out.println(myFormat.format(System.currentTimeMillis()) + ": Commencing Data Capture");
			while ((fromServer = br.readLine()) != null) {
				output += fromServer + "\n";
				if (output.endsWith("--\n")) {
					InMessage inMessage = new InMessage();
					inMessage = CarescapeUtilities.processInMessage(output, inMessage);
					if (inMessage.getXmlMessageLength() != 0) { //Then there's data to get
						if (!inMessage.getMessageXML().startsWith("<?xml")) {
							messages.add(output);
							output = "";
							if (messages.size() % (checkInterval) == 0) {
								totalMessages += checkInterval;
								if (totalMessages % (checkInterval * rate * fiveMinutes) == 0) {
									messagesToProcess = messages;
									System.out.println(myFormat.format(System.currentTimeMillis()) + ": Processing " + messagesToProcess.size() + " Messages");
									executor.execute(new ExtractData(messagesToProcess, binDescriptorMessage, openTSDBAddress, highSpeed, filePath, tags));
									messages = new ArrayList<String>();
									noData = false;
									muleSoftCriteria = checkSubjectInBed(muleSoftCriteria);
									if (!((Boolean)muleSoftCriteria.get("sameSubject")).booleanValue()) {
										tags = setTagsForBed(muleSoftCriteria);
										//endStream(sessionId, requestId++, bw, br); //endStream doesn't seem to work
										br.close();
										bw.close();
										tcpConnection.close();
										muleSoftCriteria = connectAndSetupIO(targetAddress, bedToMonitor, highSpeed, muleSoftCriteria);
										tcpConnection = (CarescapeTCPConnection) muleSoftCriteria.get("tcpConnection");
										sessionId = (String) muleSoftCriteria.get("sessionId");
										bw = (BufferedWriter) muleSoftCriteria.get("bw");
										br = (BufferedReader) muleSoftCriteria.get("br");
										sessionUpdateMessage = obtainSessionUpdate(sessionId, bw, br);
										binDescriptorMessage = startStream(sessionUpdateMessage, bedToMonitor, highSpeed, ++requestId, bw, br);
										filePath = (String) muleSoftCriteria.get("filePath");
									}
								}
							}
						} else { //check for a binDescriptor change and wipe output
							if (output.contains("<binDescriptor")) {
								messagesToProcess = messages;
								System.out.println(myFormat.format(System.currentTimeMillis()) + ": Processing " + messagesToProcess.size() + " Messages");
								executor.execute(new ExtractData(messagesToProcess, binDescriptorMessage, openTSDBAddress, highSpeed, filePath, tags));
								messages = new ArrayList<String>();
								noData = false;
								binDescriptorMessage = processBinDescriptorMessage(output, binDescriptorMessage.getByteOrder(), highSpeed, bedToMonitor, bw);
								if (binDescriptorMessage.getBinDescriptor().getParameterIntervals().size() < 1) {
									muleSoftCriteria = checkSubjectInBed(muleSoftCriteria);
									if (!((Boolean)muleSoftCriteria.get("sameSubject")).booleanValue()) {
										tags = setTagsForBed(muleSoftCriteria);
										//endStream(sessionId, requestId++, bw, br); //endStream doesn't seem to work
										br.close();
										bw.close();
										tcpConnection.close();
										muleSoftCriteria = connectAndSetupIO(targetAddress, bedToMonitor, highSpeed, muleSoftCriteria);
										tcpConnection = (CarescapeTCPConnection) muleSoftCriteria.get("tcpConnection");
										sessionId = (String) muleSoftCriteria.get("sessionId");
										bw = (BufferedWriter) muleSoftCriteria.get("bw");
										br = (BufferedReader) muleSoftCriteria.get("br");
										sessionUpdateMessage = obtainSessionUpdate(sessionId, bw, br);
										binDescriptorMessage = startStream(sessionUpdateMessage, bedToMonitor, highSpeed, ++requestId, bw, br);
										filePath = (String) muleSoftCriteria.get("filePath");
									}
								}
							}
							output = "";
						} 
					} else { //skip message and wipe output
						if (noData) System.err.println(myFormat.format(System.currentTimeMillis()) + ": Empty dataset.");
						output = "";
						noData = true;
					}
				}
				if (messages.size() == messageLimit) {
					messagesToProcess = messages;
					System.out.println(myFormat.format(System.currentTimeMillis()) + ": Processing " + messagesToProcess.size() + " Messages");
					executor.execute(new ExtractData(messagesToProcess, binDescriptorMessage, openTSDBAddress, highSpeed, filePath, tags));
					messages = new ArrayList<String>();
					noData = false;
				}
			}

			br.close();
			bw.close();
			tcpConnection.close();
		} catch (IOException e) {
			e.printStackTrace();
			tcpConnection.close(); 
		} catch (Exception e) {
			e.printStackTrace();
			tcpConnection.close(); 
		} finally {
			tcpConnection.close(); 
		}
	}

	private void setCarescapeADTTranslator(HashMap<String, String> carescapeADTTranslator) {
		this.carescapeADTTranslator = carescapeADTTranslator;
	}

	private HashMap<String, String> getCarescapeADTTranslator() {
		return carescapeADTTranslator;
	}

	
	private HashMap<String, Object> checkSubjectInBed(HashMap<String, Object> muleSoftCriteria) throws Exception {

		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		System.out.println(myFormat.format(System.currentTimeMillis()) + ": Checking MuleSoft for subject in bed " + ((String) muleSoftCriteria.get("bedToMonitor")));	
		MedicalResourceNumber mrnLocal = (MedicalResourceNumber) muleSoftCriteria.get("mrn");
		muleSoftCriteria = CarescapeUtilities.checkBedSubjectinADT(muleSoftCriteria);
		MedicalResourceNumber mrnAdt = (MedicalResourceNumber) muleSoftCriteria.get("mrn");
		muleSoftCriteria.put("sameSubject", new Boolean(mrnLocal.getGUID().equalsIgnoreCase(mrnAdt.getGUID())));

		return muleSoftCriteria;
	}

	private HashMap<String, Object> connectAndSetupIO(String targetAddress, String bedToMonitor, boolean highSpeed, HashMap<String, Object> muleSoftCriteria) throws Exception {
		
		//set parameters from config if not provided in the command line
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		CarescapeTCPConnection tcpConnection = (CarescapeTCPConnection) muleSoftCriteria.get("tcpConnection");
		tcpConnection =	CarescapeUtilities.openConnection(targetAddress);
		System.out.println("Connected to " + tcpConnection.getSocket().getInetAddress() 
				+ " on port "  + tcpConnection.getSocket().getPort() + " from port " 
				+ tcpConnection.getSocket().getLocalPort() + " of " 
				+ tcpConnection.getSocket().getLocalAddress());
		System.out.println("Carescape Unit Bed requested to monitor:\t" + bedToMonitor);
		System.out.print("Frequency of data requested:\t\t\t");
		if (highSpeed) {
			System.out.println("High");
		} else {
			System.out.println("Low");			
		}
		System.out.println();

		InetAddress address = InetAddress.getLocalHost();
		NetworkInterface nwi = NetworkInterface.getByInetAddress(address);
		byte mac[] = nwi.getHardwareAddress();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mac.length; i++) {
			sb.append(String.format("%02X", mac[i]));
			if (i == mac.length/2 - 1) sb.append("FFFE");
		}
		Date now = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		c.add(Calendar.YEAR, -70);
		long timeInSeconds = c.getTimeInMillis()/1000;		
		String sessionId = (sb.toString() + Integer.toHexString((int)timeInSeconds)).toUpperCase();
		OutputStreamWriter writer = new OutputStreamWriter(tcpConnection.getSocket().getOutputStream());
		BufferedWriter bw = new BufferedWriter(writer);

		HelloMessage hello = new HelloMessage(sessionId);
		OutMessage outMessage = (OutMessage) hello;
		CarescapeUtilities.sendOutMessage(bw, outMessage, false);

		BufferedReader br = new BufferedReader(new InputStreamReader(tcpConnection.getSocket().getInputStream()), 32768);
		String helloResponse = "";
		helloResponse = CarescapeUtilities.receiveInMessage(br, helloResponse, false);

		HelloResponseMessage helloResponseMessage = new HelloResponseMessage();
		InMessage inMessage = (InMessage) helloResponseMessage;
		helloResponseMessage = (HelloResponseMessage) CarescapeUtilities.processInMessage(helloResponse, inMessage);

		if (!(helloResponseMessage.isAck())) {
			throw new Exception(myFormat.format(System.currentTimeMillis()) + ": Hello Response " + helloResponseMessage.getStatus() + ": "+ helloResponseMessage.getErrorString());
		} 

		muleSoftCriteria.put("tcpConnection", tcpConnection);
		muleSoftCriteria.put("sessionId", sessionId);
		muleSoftCriteria.put("bw", bw);
		muleSoftCriteria.put("br", br);

		return muleSoftCriteria;
	}
	
	private SessionUpdateMessage obtainSessionUpdate(String sessionId, BufferedWriter bw, BufferedReader br) throws Exception {

		int requestId = 999999999;
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		GetSessionUpdateRequestMessage getSessionUpdateRequestMessage = new GetSessionUpdateRequestMessage(sessionId, new Integer(requestId).toString());
		OutMessage outMessage = (OutMessage) getSessionUpdateRequestMessage;
		CarescapeUtilities.sendOutMessage(bw, outMessage, false);

		String getSessionUpdateResponse = "";
		getSessionUpdateResponse =	CarescapeUtilities.receiveInMessage(br, getSessionUpdateResponse, false);

		GetSessionUpdateResponseMessage getSessionUpdateResponseMessage = new GetSessionUpdateResponseMessage();
		InMessage inMessage = (InMessage) getSessionUpdateResponseMessage;
		getSessionUpdateResponseMessage = (GetSessionUpdateResponseMessage) CarescapeUtilities.processInMessage(getSessionUpdateResponse, inMessage);

		if (!(getSessionUpdateResponseMessage.isAck())) {
			throw new Exception(myFormat.format(System.currentTimeMillis()) + ": GetSessionUpdate Response " + getSessionUpdateResponseMessage.getStatus() + ": "+ getSessionUpdateResponseMessage.getErrorString());
		} 

		String fromServer = "", output = "", sessionUpdate = "";
		boolean extraInfoFlag = false;
		while ((fromServer = br.readLine()) != null) {
			if (fromServer.startsWith("Content-Length")) extraInfoFlag = true;
			if (extraInfoFlag) {
				output += fromServer;
				if (output.length() > getSessionUpdateResponseMessage.getContentEncapsulationLength()) {
					sessionUpdate += fromServer + "\n";
				}
				if (sessionUpdate.endsWith("--\n")) break;
			}
		}


		EndSessionUpdateRequestMessage endSessionUpdateRequestMessage = new EndSessionUpdateRequestMessage(sessionId, new Integer(requestId).toString());
		outMessage = (OutMessage) endSessionUpdateRequestMessage;
		CarescapeUtilities.sendOutMessage(bw, outMessage, false);

		String endSessionUpdateResponse = "";
		endSessionUpdateResponse =	CarescapeUtilities.receiveInMessage(br, endSessionUpdateResponse, false);

		EndSessionUpdateResponseMessage endSessionUpdateResponseMessage = new EndSessionUpdateResponseMessage();
		inMessage = (InMessage) endSessionUpdateResponseMessage;
		endSessionUpdateResponseMessage = (EndSessionUpdateResponseMessage) CarescapeUtilities.processInMessage(endSessionUpdateResponse, inMessage);

		if (!(endSessionUpdateResponseMessage.isAck())) {
			throw new Exception(myFormat.format(System.currentTimeMillis()) + ": EndSessionUpdate Response " + endSessionUpdateResponseMessage.getStatus() + ": "+ endSessionUpdateResponseMessage.getErrorString());
		} 

		SessionUpdateMessage sessionUpdateMessage = new SessionUpdateMessage();
		inMessage = (InMessage) sessionUpdateMessage;
		sessionUpdateMessage = (SessionUpdateMessage) CarescapeUtilities.processInMessage(sessionUpdate, inMessage);

		GenericResponseMessage genericResponseMessage = new GenericResponseMessage(sessionUpdateMessage.getSessionUpdateHeader());
		outMessage = (OutMessage) genericResponseMessage;
		CarescapeUtilities.sendOutMessage(bw, outMessage, false);

		return sessionUpdateMessage;

	}
	
	private void echoCarescapeSessionInfo (HashMap<String,Session> sessions, String bedToMonitor, String targetAddress, boolean allBeds) throws Exception{

		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Set<String> keys = sessions.keySet();
		TreeSet<String> sortedKeys = new TreeSet<String>(keys);
		if (!sortedKeys.contains(bedToMonitor)) {
			throw new Exception(myFormat.format(System.currentTimeMillis()) + ": The bed you specified, " + bedToMonitor + ", does not exist.  Check your parameter for the bed to monitor.");
		} 

//		System.out.println("Active Carescape Sessions on " + targetAddress + ": " + sessions.size());
//
//		String unit = bedToMonitor.split("-")[0].replaceAll("\\d", "");
//		int areaCount = 0;
//		for (String key : sortedKeys) {
//			if (key.startsWith(unit)) areaCount++;
//		}
//		System.out.println("Active Carescape Sessions in " + unit + ": " + areaCount);
//		System.out.println();

		if (allBeds) {
			System.out.println("Unit|Bed:\tSession\tVariable Types\tNetwork\tPatient Status\tPatInfo Unit|Bed:\tFirst name\tLast Name");
			for (String key : sortedKeys) {
				System.out.print(key + ":\t" + sessions.get(key).getSessionId().getValue() + "\t");
				ArrayList<Service> services = sessions.get(key).getDeviceStatus().getServices().getServices();
				for (Service service : services) {
					System.out.print(service.getValue());
					if (!service.equals(services.get(services.size()-1))) System.out.print(",");
				}
				System.out.print("\t" + sessions.get(key).getDeviceStatus().getState().getNetwork() + "\t");
				System.out.print(sessions.get(key).getDeviceStatus().getState().getPatientStatus() + "\t");
				System.out.println("\t" + sessions.get(key).getPatientInfo().getVisit().getAssignedLocation().getUnitName() + "-" + sessions.get(key).getPatientInfo().getVisit().getAssignedLocation().getBedName() + ":\t\t" + sessions.get(key).getPatientInfo().getName().getGiven() + "\t" + sessions.get(key).getPatientInfo().getName().getFamily());
			}
			System.out.println();
			throw new Exception("This is just meant to see who is in what bed.  It is not intended for active use in data capture.");
		} else {
			for (String key : sortedKeys) {
				if (key.equalsIgnoreCase(bedToMonitor)) {
					System.out.println("Carescape Unit Bed:\t" + key);
					System.out.println("Session:\t\t" + sessions.get(key).getSessionId().getValue());
					ArrayList<Service> services = sessions.get(key).getDeviceStatus().getServices().getServices();
					System.out.print("Services:\t\t");
					for (Service service : services) {
						System.out.print(service.getValue());
						if (!service.equals(services.get(services.size()-1))) { 
							System.out.print(",");
						} else {
							System.out.println();
						}
					}
					System.out.println("Network:\t\t" + sessions.get(key).getDeviceStatus().getState().getNetwork());
					System.out.println("Patient Status:\t\t" +sessions.get(key).getDeviceStatus().getState().getPatientStatus());
				}
			}
			System.out.println();
		}
	}

	private HashMap<String,String> setTagsForBed(HashMap<String, Object> muleSoftCriteria) throws Exception {

		HashMap<String,String> tags = new HashMap<String,String>();
		String hospitalName = (String) muleSoftCriteria.get("hospitalName");
		String unitId = (String) muleSoftCriteria.get("unitId");
		String bedToMonitor = (String) muleSoftCriteria.get("bedToMonitor");
		BeddedPatientMin bPMin = (BeddedPatientMin) muleSoftCriteria.get("bPMin");
		MedicalResourceNumber mrn = (MedicalResourceNumber) muleSoftCriteria.get("mrn");

		String adtUnitBed = hospitalName + "." + unitId + "." + bPMin.getRoomName() + "." + bPMin.getBedName(); 

		tags.put("subjectGUID", mrn.getGUID());
		tags.put("subjectCSN", bPMin.getCSN());
		tags.put("carescapeUnitBed", bedToMonitor);
		tags.put("adtUnitBed", adtUnitBed); 

		return tags;
	}

	private BinDescriptorMessage startStream(SessionUpdateMessage sessionUpdateMessage, String bedToMonitor, boolean highSpeed, int requestId, BufferedWriter bw, BufferedReader br) throws Exception {

		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		GetStreamRequestMessage getStreamRequestMessage;
		if (highSpeed) {
			getStreamRequestMessage = new GetStreamRequestMessage(sessionUpdateMessage.getSessionUpdateHeader().getSessionId().getValue(), sessionUpdateMessage.getSessions().get(bedToMonitor).getSessionId().getValue(), "wav", new Integer(requestId).toString());
		} else { //lowSpeed
			getStreamRequestMessage = new GetStreamRequestMessage(sessionUpdateMessage.getSessionUpdateHeader().getSessionId().getValue(), sessionUpdateMessage.getSessions().get(bedToMonitor).getSessionId().getValue(), "(num|cfg)", new Integer(requestId).toString());			
		}
		OutMessage outMessage = (OutMessage) getStreamRequestMessage;
		CarescapeUtilities.sendOutMessage(bw, outMessage, false);

		String getStreamResponse = "", binHeader = "";
		GetStreamResponseMessage getStreamResponseMessage = new GetStreamResponseMessage();
		BinHeaderMessage binHeaderMessage = new BinHeaderMessage();
		InMessage inMessage = null;

		if (highSpeed) {

			while (!getStreamResponse.contains("<getStreamResponse")) {
				getStreamResponse = CarescapeUtilities.receiveInMessage(br, getStreamResponse, false);
			}
			inMessage = (InMessage) getStreamResponseMessage;
			getStreamResponseMessage = (GetStreamResponseMessage) CarescapeUtilities.processInMessage(getStreamResponse, inMessage);

			if (!(getStreamResponseMessage.isAck())) {
				throw new Exception(myFormat.format(System.currentTimeMillis()) + ": GetStream Response " + getStreamResponseMessage.getStatus() + ": "+ getStreamResponseMessage.getErrorString());
			} 

			while (!binHeader.contains("<binHeader")) {
				binHeader = CarescapeUtilities.receiveInMessage(br, binHeader, false);
			}
			inMessage = (InMessage) binHeaderMessage;
			binHeaderMessage = (BinHeaderMessage) CarescapeUtilities.processInMessage(binHeader, inMessage);

		} else {

			while (!binHeader.contains("<binHeader")) {
				binHeader = CarescapeUtilities.receiveInMessage(br, binHeader, false);
			}
			inMessage = (InMessage) binHeaderMessage;
			binHeaderMessage = (BinHeaderMessage) CarescapeUtilities.processInMessage(binHeader, inMessage);

			while (!getStreamResponse.contains("<getStreamResponse")) {
				getStreamResponse = CarescapeUtilities.receiveInMessage(br, getStreamResponse, false);
			}
			inMessage = (InMessage) getStreamResponseMessage;
			getStreamResponseMessage = (GetStreamResponseMessage) CarescapeUtilities.processInMessage(getStreamResponse, inMessage);

			if (!(getStreamResponseMessage.isAck())) {
				throw new Exception(myFormat.format(System.currentTimeMillis()) + ": GetStream Response " + getStreamResponseMessage.getStatus() + ": "+ getStreamResponseMessage.getErrorString());
			} 

		}

		GenericResponseMessage genericResponseMessage = new GenericResponseMessage(binHeaderMessage.getBinHeaderHeader());
		outMessage = (OutMessage) genericResponseMessage;
		CarescapeUtilities.sendOutMessage(bw, outMessage, false);

		String binDescriptor = "";
		while (!binDescriptor.contains("<binDescriptor")) {
			binDescriptor = CarescapeUtilities.receiveInMessage(br, binDescriptor, false);
		}

		BinDescriptorMessage binDescriptorMessage = processBinDescriptorMessage(binDescriptor, binHeaderMessage.getByteOrder(), highSpeed, bedToMonitor, bw);

		return binDescriptorMessage;
	}

	private BinDescriptorMessage processBinDescriptorMessage (String binDescriptor, String byteOrder, boolean highSpeed, String bedToMonitor, BufferedWriter bw) throws Exception {

		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		BinDescriptorMessage binDescriptorMessage = new BinDescriptorMessage(highSpeed);
		InMessage inMessage = (InMessage) binDescriptorMessage;
		binDescriptorMessage = (BinDescriptorMessage) CarescapeUtilities.processInMessage(binDescriptor, inMessage);
		binDescriptorMessage.setByteOrder(byteOrder);

		if (binDescriptorMessage.getBinDescriptor().getParameterIntervals().size() < 1) {
			System.err.println(myFormat.format(System.currentTimeMillis()) + ": No parameters to capture on bed " + bedToMonitor + ".  Check with physicians.");
		} else {
			System.out.println(myFormat.format(System.currentTimeMillis()) + ": number of possible parameters: " + binDescriptorMessage.getBinDescriptor().getParameterIntervals().size());

			//		System.out.println(binDescriptorMessage.getMessageXML());
			int acceptableParameters = 0;
			ArrayList<ParameterSet> localParameterSets = binDescriptorMessage.getBinDescriptor().getBlock().getGroupBT().getParameters().getParameterSets();
			for (ParameterSet localParameterSet : localParameterSets) {
				ArrayList<Parameter> localParameterArray = localParameterSet.getParameters();
				for (Parameter localParameter : localParameterArray) {
					ArrayList<SubParameterInfo> localSubParameterArray = localParameter.getSubParameterInfo();
					for (SubParameterInfo localSubParameterInfo : localSubParameterArray) {
						if (!localSubParameterInfo.getSubParameterInfoType().contains(":")) {
							acceptableParameters++;
						}
					}
				}
			}

			System.out.println(myFormat.format(System.currentTimeMillis()) + ": number of acceptable parameters: " + acceptableParameters);
		}


		GenericResponseMessage genericResponseMessage = new GenericResponseMessage(binDescriptorMessage.getBinDescriptorHeader());
		OutMessage outMessage = (OutMessage) genericResponseMessage;
		CarescapeUtilities.sendOutMessage(bw, outMessage, false);

		return binDescriptorMessage;
	}

	private void endStream(String sessionId, int requestId, BufferedWriter bw, BufferedReader br) throws Exception {

		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		EndStreamRequestMessage endStreamRequestMessage = new EndStreamRequestMessage(sessionId, new Integer(requestId).toString());
		OutMessage outMessage = (OutMessage) endStreamRequestMessage;
		CarescapeUtilities.sendOutMessage(bw, outMessage, false);

		String endStreamResponse = "";
		while (!endStreamResponse.contains("<endStreamResponse")) {
			endStreamResponse =	CarescapeUtilities.receiveInMessage(br, endStreamResponse, false);
		}

		EndStreamResponseMessage endStreamResponseMessage = new EndStreamResponseMessage();
		InMessage inMessage = (InMessage) endStreamResponseMessage;
		endStreamResponseMessage = (EndStreamResponseMessage) CarescapeUtilities.processInMessage(endStreamResponse, inMessage);

		if (!(endStreamResponseMessage.isAck())) {
			throw new Exception(myFormat.format(System.currentTimeMillis()) + ": EndSessionUpdate Response " + endStreamResponseMessage.getStatus() + ": "+ endStreamResponseMessage.getErrorString());
		}

		String binEnd = "";
		while (!binEnd.contains("<binEnd")) {
			binEnd = CarescapeUtilities.receiveInMessage(br, binEnd, false);
		}
		BinEndMessage binEndMessage = new BinEndMessage();
		inMessage = (InMessage) binEndMessage;
		binEndMessage = (BinEndMessage) CarescapeUtilities.processInMessage(binEnd, inMessage);

		GenericResponseMessage genericResponseMessage = new GenericResponseMessage(binEndMessage.getBinEndHeader());
		outMessage = (OutMessage) genericResponseMessage;
		CarescapeUtilities.sendOutMessage(bw, outMessage, false);

	}

}

class ExtractData implements Runnable {

	private ArrayList<String> messages = new ArrayList<String>();
	private String byteOrder = "", filePath = "", urlString = "";
	private boolean highSpeed = false;
	private BinDescriptorMessage binDescriptorMessage = new BinDescriptorMessage();
	private HashMap<String,String> tags = new HashMap<String,String>();

	public ExtractData(ArrayList<String> messages, BinDescriptorMessage binDescriptorMessage, String urlString, boolean highSpeed, String filePath, HashMap<String,String> tags) {
		this.messages = messages;
		this.binDescriptorMessage = binDescriptorMessage;
		this.byteOrder = binDescriptorMessage.getByteOrder();
		this.urlString = urlString;
		this.highSpeed = highSpeed;
		this.filePath = filePath;
		this.tags = tags;
	}

	@Override
	public void run() {

		int lastBlockSQN = 0;
		Date lastStartDateTime = new Date(0);
		Date lowestStartDateTime = new Date(0);
		int lowestDateArraySize = 0;
		double interval = 0.0, offset = 0.0;
		File check;
		String dirPath = "";
		ArrayList<BinaryDataMessage> binaryDataMessages = new ArrayList<BinaryDataMessage>();
		for (String message : messages) {

			BinaryDataMessage binaryDataMessage = new BinaryDataMessage();
			InMessage inMessage = (InMessage) binaryDataMessage;
			binaryDataMessage = (BinaryDataMessage) CarescapeUtilities.processInMessage(message, inMessage);
			binaryDataMessage.setByteOrder(byteOrder);
			binaryDataMessages.add(binaryDataMessage);

			if (highSpeed && messages.indexOf(message) < 4) { //should only need to do this for the first highSpeed 4 messages
				MessageProcessor messageProcessor = new MessageProcessor(binDescriptorMessage, binaryDataMessage);
				messageProcessor.getActualData();
				if (lowestStartDateTime.getTime() == new Date(0).getTime()) lowestStartDateTime = messageProcessor.getStartDateTime();
				if (messageProcessor.getStartDateTime().getTime() == lowestStartDateTime.getTime()) lowestDateArraySize++;
			} else if (messages.indexOf(message) == 1) {
				MessageProcessor messageProcessor = new MessageProcessor(binDescriptorMessage, binaryDataMessage);
				messageProcessor.getActualData();
				if (lowestStartDateTime.getTime() == new Date(0).getTime()) lowestStartDateTime = messageProcessor.getStartDateTime();
				if (messageProcessor.getStartDateTime().getTime() == lowestStartDateTime.getTime()) lowestDateArraySize++;		
			}
		}

		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		int actualParametersProcessed = 0;

		try {
			for (BinaryDataMessage binaryDataMessage : binaryDataMessages) {
				MessageProcessor messageProcessor = new MessageProcessor(binDescriptorMessage, binaryDataMessage);
				HashMap<String,ArrayList<Double>> feeds = messageProcessor.getActualData();
//				System.out.println(myFormat.format(System.currentTimeMillis()) + " StartDateTime: " + myFormat.format(messageProcessor.getStartDateTime()));
				Set<String> keys = feeds.keySet();
				if (keys.size() > actualParametersProcessed) actualParametersProcessed = keys.size();
				TreeSet<String> sortedKeys = new TreeSet<String>(keys);
				if ((messageProcessor.getBlockSQN() > lastBlockSQN) && (messageProcessor.getStartDateTime().getTime() > lastStartDateTime.getTime())) { 
					lastBlockSQN = messageProcessor.getBlockSQN();
					lastStartDateTime = messageProcessor.getStartDateTime();
					offset = 0.0;
				}
				if ((messageProcessor.getStartDateTime().getTime() == lastStartDateTime.getTime()) && (messageProcessor.getBlockSQN() > lastBlockSQN)) 
					offset += 1000.0 / new Double(binDescriptorMessage.getBinDescriptor().getBlock().getRate()).doubleValue();
				if (lastStartDateTime.getTime() == lowestStartDateTime.getTime()) {
					offset = 1000.0 / new Double(binDescriptorMessage.getBinDescriptor().getBlock().getRate()).doubleValue();
					offset *= (binDescriptorMessage.getBinDescriptor().getBlock().getRate() - lowestDateArraySize--);
				}

				for (String key : sortedKeys) {
					if (feeds.get(key).size() > 0) { 
						String keyName = key.split(":")[0];
						dirPath = filePath + keyName + File.separator;
						check = new File(dirPath);
						CarescapeUtilities.checkDir(check);
						double time = new Double(messageProcessor.getStartDateTime().getTime()).doubleValue();
						time += offset;
						FileWriter fw = new FileWriter(dirPath + lowestStartDateTime.getTime() + ".csv", true);
						BufferedWriter bw = new BufferedWriter(fw);
						PrintWriter out = new PrintWriter(bw);
						interval = new Double(binDescriptorMessage.getBinDescriptor().getParameterIntervals().get(key)).doubleValue();
						ArrayList<IncomingDataPoint> dataPoints = new ArrayList<IncomingDataPoint>();
						ArrayList<Double> temp = feeds.get(key);
						for (Double value : temp) {
							long timeLong = new Double(Math.round(time)).longValue();
							out.print(myFormat.format(timeLong) + ",");
							out.println(value.doubleValue());
							IncomingDataPoint dataPoint = new IncomingDataPoint(keyName, timeLong, value.toString(), tags);
							dataPoints.add(dataPoint);
							time += interval;
						}
						TimeSeriesStorer.storeTimePoints(urlString, dataPoints);
						out.close();
					} else {
						actualParametersProcessed--;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OpenTSDBException e) {
			e.printStackTrace();
		}

		System.out.println(myFormat.format(System.currentTimeMillis()) + ": number of actual parameters: " + actualParametersProcessed);
		System.out.println(myFormat.format(System.currentTimeMillis()) + ": Processed Messages");
	}
}
