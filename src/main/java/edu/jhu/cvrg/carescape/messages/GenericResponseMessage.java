package edu.jhu.cvrg.carescape.messages;

/*
Copyright 2017 Johns Hopkins University Institute for Computational Medicine

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
* @author Stephen Granite
* 
*/

import edu.jhu.cvrg.sapphire.data.common.Header;
import edu.jhu.cvrg.sapphire.xmlwriter.GenericResponseXMLWriter;

public class GenericResponseMessage extends OutMessage {
	
	private GenericResponseXMLWriter genericResponseXMLWriter;
	
	public GenericResponseMessage(Header header) {
		
		super(header.getSessionId().getValue());
		setGenericResponseXMLWriter(new GenericResponseXMLWriter(header));
		setMessageXML(getGenericResponseXMLWriter().getXmlString());
		setXmlMessageLength(getMessageXML().length());
		String boundary = "2f894a6f-2483-476a-a16f-916c9d2d5c05";
		String boundaryInQuotes = "\"" + boundary + "\"";
		String contentEncapsulation = "--" + boundary + "\r\n";
		contentEncapsulation += "Content-Type: application/x-sapphire+xml\r\n";
		contentEncapsulation += "Content-Transfer-Encoding: binary\r\n";
		contentEncapsulation += "Content-Length: "+ getXmlMessageLength() +"\r\n\r\n";
		contentEncapsulation += getMessageXML();
		contentEncapsulation += "\r\n--" + boundary + "--\r\n";
		setContentEncapsulationLength(contentEncapsulation.length());
		String mimeMessage = "Mime-Version: 1.0\r\n";
		mimeMessage += "Content-Type: multipart/mixed;boundary=" + boundaryInQuotes + "\r\n";
		mimeMessage += "Content-Length: "+ getContentEncapsulationLength() +"\r\n\r\n";
		setMessageBody(contentEncapsulation); 
		setMessageHeader(mimeMessage);

	}

	public GenericResponseXMLWriter getGenericResponseXMLWriter() {
		return genericResponseXMLWriter;
	}

	public void setGenericResponseXMLWriter(GenericResponseXMLWriter genericResponseXMLWriter) {
		this.genericResponseXMLWriter = genericResponseXMLWriter;
	}

}

