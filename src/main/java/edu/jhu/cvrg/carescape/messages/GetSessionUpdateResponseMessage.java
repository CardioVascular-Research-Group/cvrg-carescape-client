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

import edu.jhu.cvrg.sapphire.xmlparser.GetSessionUpdateResponseXMLParser;

public class GetSessionUpdateResponseMessage extends InMessage {

	private boolean ack;
	private GetSessionUpdateResponseXMLParser getSessionUpdateResponseXMLParser;

	
	public GetSessionUpdateResponseMessage() {

		super();
		setAck(false);

	}

	@Override
	public void setMessageXML(String messageXML) {
		setGetSessionUpdateResponseXMLParser(new GetSessionUpdateResponseXMLParser(messageXML));
		setStatus(getGetSessionUpdateResponseXMLParser().getGetSessionUpdateResponse().getStatus());
		setErrorString(getGetSessionUpdateResponseXMLParser().getGetSessionUpdateResponse().getErrorString());
		if (getStatus().equalsIgnoreCase("ack")) setAck(true);
		super.setMessageXML(messageXML);
	}

	public GetSessionUpdateResponseXMLParser getGetSessionUpdateResponseXMLParser() {
		return getSessionUpdateResponseXMLParser;
	}

	public void setGetSessionUpdateResponseXMLParser(GetSessionUpdateResponseXMLParser getSessionUpdateResponseXMLParser) {
		this.getSessionUpdateResponseXMLParser = getSessionUpdateResponseXMLParser;
	}
	
	public boolean isAck() {
		return ack;
	}

	public void setAck(boolean ack) {
		this.ack = ack;
	}
	
}
