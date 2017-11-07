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

import edu.jhu.cvrg.sapphire.data.common.Header;
import edu.jhu.cvrg.sapphire.data.response.BinDescriptor;
import edu.jhu.cvrg.sapphire.data.response.bindescriptor.ParameterSet;
import edu.jhu.cvrg.sapphire.xmlparser.BinDescriptorXMLParser;

public class BinDescriptorMessage extends InMessage {

	private BinDescriptorXMLParser binDescriptorXMLParser;
	private BinDescriptor binDescriptor;
	private Header binDescriptorHeader;
	private ArrayList<ParameterSet> binDescriptorParameterSets;
	private boolean highSpeed;
	private String byteOrder;

	public BinDescriptorMessage() {

		this(true);

	}

	public BinDescriptorMessage(boolean highSpeed) {
		
		super();
		setBinDescriptor(new BinDescriptor());
		setHighSpeed(highSpeed);
		
	}
	
	@Override
	public void setMessageXML(String messageXML) {
		setBinDescriptorXMLParser(new BinDescriptorXMLParser(messageXML, highSpeed));
		setBinDescriptor(getBinDescriptorXMLParser().getBinDescriptor());
		setBinDescriptorHeader(getBinDescriptor().getHeader());
		setBinDescriptorParameterSets(getBinDescriptor().getBlock().getGroupBT().getParameters().getParameterSets());
		super.setMessageXML(messageXML);
	}

	public BinDescriptorXMLParser getBinDescriptorXMLParser() {
		return binDescriptorXMLParser;
	}

	public void setBinDescriptorXMLParser(BinDescriptorXMLParser binDescriptorXMLParser) {
		this.binDescriptorXMLParser = binDescriptorXMLParser;
	}

	public BinDescriptor getBinDescriptor() {
		return binDescriptor;
	}

	public void setBinDescriptor(BinDescriptor binDescriptor) {
		this.binDescriptor = binDescriptor;
	}

	public Header getBinDescriptorHeader() {
		return binDescriptorHeader;
	}

	public void setBinDescriptorHeader(Header binDescriptorHeader) {
		this.binDescriptorHeader = binDescriptorHeader;
	}

	public ArrayList<ParameterSet> getBinDescriptorParameterSets() {
		return binDescriptorParameterSets;
	}

	public void setBinDescriptorParameterSets(ArrayList<ParameterSet> binDescriptorParameterSets) {
		this.binDescriptorParameterSets = binDescriptorParameterSets;
	}

	public boolean isHighSpeed() {
		return highSpeed;
	}

	public void setHighSpeed(boolean highSpeed) {
		this.highSpeed = highSpeed;
	}

	public String getByteOrder() {
		return byteOrder;
	}

	public void setByteOrder(String byteOrder) {
		this.byteOrder = byteOrder;
	}

}
