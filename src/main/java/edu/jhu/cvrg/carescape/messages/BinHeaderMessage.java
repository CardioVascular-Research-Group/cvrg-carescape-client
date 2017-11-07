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

import edu.jhu.cvrg.sapphire.data.common.Header;
import edu.jhu.cvrg.sapphire.xmlparser.BinHeaderXMLParser;

public class BinHeaderMessage extends InMessage {

	private String byteOrder;
	private BinHeaderXMLParser binHeaderXMLParser;
	private Header binHeaderHeader;

	public BinHeaderMessage() {

		super();
		setByteOrder("littleEndian");

	}

	@Override
	public void setMessageXML(String messageXML) {
		setBinHeaderXMLParser(new BinHeaderXMLParser(messageXML));
		setBinHeaderHeader(getBinHeaderXMLParser().getBinHeader().getHeader());
		setByteOrder(getBinHeaderXMLParser().getBinHeader().getBinaryFormat().getByteOrder());
		super.setMessageXML(messageXML);
	}

	public BinHeaderXMLParser getBinHeaderXMLParser() {
		return binHeaderXMLParser;
	}

	public void setBinHeaderXMLParser(BinHeaderXMLParser binHeaderXMLParser) {
		this.binHeaderXMLParser = binHeaderXMLParser;
	}

	public String getByteOrder() {
		return byteOrder;
	}

	public void setByteOrder(String byteOrder) {
		this.byteOrder = byteOrder;
	}
	
	public Header getBinHeaderHeader() {
		return binHeaderHeader;
	}

	public void setBinHeaderHeader(Header binHeaderHeader) {
		this.binHeaderHeader = binHeaderHeader;
	}

}
