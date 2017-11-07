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

import edu.jhu.cvrg.sapphire.xmlparser.EndStreamResponseXMLParser;

public class EndStreamResponseMessage extends InMessage {

	private boolean ack;
	private EndStreamResponseXMLParser endStreamResponseXMLParser;

	
	public EndStreamResponseMessage() {

		super();
		setAck(false);

	}

	@Override
	public void setMessageXML(String messageXML) {
		setEndStreamResponseXMLParser(new EndStreamResponseXMLParser(messageXML));
		setStatus(getEndStreamResponseXMLParser().getEndStreamResponse().getStatus());
		setErrorString(getEndStreamResponseXMLParser().getEndStreamResponse().getErrorString());
		if (getStatus().equalsIgnoreCase("ack")) setAck(true);
		super.setMessageXML(messageXML);
	}

	public EndStreamResponseXMLParser getEndStreamResponseXMLParser() {
		return endStreamResponseXMLParser;
	}

	public void setEndStreamResponseXMLParser(EndStreamResponseXMLParser endStreamResponseXMLParser) {
		this.endStreamResponseXMLParser = endStreamResponseXMLParser;
	}
	
	public boolean isAck() {
		return ack;
	}

	public void setAck(boolean ack) {
		this.ack = ack;
	}
	
}
