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
import java.util.Arrays;

public class InMessage extends CarescapeMessage {

	private String messageHeader, messageHeaderType, messageBody, messageXML, status, errorString;
	private int contentEncapsulationLength, xmlMessageLength;
	private ArrayList<String> boundaries;

	public InMessage() { //Base class for messages from Carescape Server, to generalize processing and interpretation

		setMessageHeaderType("");
		setMessageHeader("");
		setMessageBody("");
		setContentEncapsulationLength(0);
		setXmlMessageLength(0);
		setBoundaries(new ArrayList<String>());

	}

	public String getMessageHeader() {
		return messageHeader;
	}

	public void setMessageHeader(String messageHeader) {
		String boundaryValue = "";
		String headerType = getMessageHeaderType();
		if (headerType != null) {
			int pos1 = headerType.toLowerCase().indexOf("boundary");
			int pos2 = headerType.indexOf(";", pos1);
			if (pos2 < 0)
				pos2 = headerType.length();
			if ((pos1 > 0) && (pos2 > pos1))
				boundaryValue = headerType.substring(pos1+9, pos2);
		}
		if (boundaryValue.startsWith("\""))
			boundaryValue = boundaryValue.substring(1);
		if (boundaryValue.endsWith("\""))
			boundaryValue = boundaryValue.substring(0, boundaryValue.length()-1);
		boundaryValue = boundaryValue.trim();
		String[] boundaryArray = boundaryValue.split(",");
		setBoundaries(new ArrayList<String>(Arrays.asList(boundaryArray)));
		this.messageHeader = messageHeader;
	}

	public String getMessageHeaderType() {
		return messageHeaderType;
	}

	public void setMessageHeaderType(String messageHeaderType) {
		this.messageHeaderType = messageHeaderType;
	}

	public String getMessageBody() {
		return messageBody;
	}

	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}

	public String getMessageXML() {
		return messageXML;
	}

	public void setMessageXML(String messageXML) {
		this.messageXML = messageXML;		
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorString() {
		return errorString;
	}

	public void setErrorString(String errorString) {
		this.errorString = errorString;
	}

	public int getContentEncapsulationLength() {
		return contentEncapsulationLength;
	}

	public void setContentEncapsulationLength(int contentEncapsulationLength) {
		this.contentEncapsulationLength = contentEncapsulationLength;
	}

	public int getXmlMessageLength() {
		return xmlMessageLength;
	}

	public void setXmlMessageLength(int xmlMessageLength) {
		this.xmlMessageLength = xmlMessageLength;
	}

	public ArrayList<String> getBoundaries() {
		return boundaries;
	}

	public void setBoundaries(ArrayList<String> boundaries) {
		this.boundaries = boundaries;
	}

}
