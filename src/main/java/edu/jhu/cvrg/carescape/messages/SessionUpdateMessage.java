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
import java.util.HashMap;

import edu.jhu.cvrg.sapphire.data.common.Header;
import edu.jhu.cvrg.sapphire.data.response.sessionupdate.Session;
import edu.jhu.cvrg.sapphire.xmlparser.SessionUpdateXMLParser;

public class SessionUpdateMessage extends InMessage {

	private SessionUpdateXMLParser sessionUpdateXMLParser;
	private HashMap<String,Session> sessions;
	private Header sessionUpdateHeader;

	public SessionUpdateMessage() {

		super();
		setSessions(new HashMap<String,Session>());

	}

	@Override
	public void setMessageXML(String messageXML) {
		setSessionUpdateXMLParser(new SessionUpdateXMLParser(messageXML));
		setSessionUpdateHeader(getSessionUpdateXMLParser().getSessionUpdate().getHeader());
		ArrayList<Session> localSessions = getSessionUpdateXMLParser().getSessionUpdate().getSessionList().getSessions();
		HashMap<String,Session> sessions = getSessions();
		for (Session session : localSessions) {
			//					if (session.getDeviceStatus().getAssignedLocation().getUnitName().contains("PICU")) 
			sessions.put(session.getDeviceStatus().getAssignedLocation().getUnitName()+"-"+session.getDeviceStatus().getAssignedLocation().getBedName(), session);
		}
		setSessions(sessions);
		super.setMessageXML(messageXML);
	}

	public SessionUpdateXMLParser getSessionUpdateXMLParser() {
		return sessionUpdateXMLParser;
	}

	public void setSessionUpdateXMLParser(SessionUpdateXMLParser sessionUpdateXMLParser) {
		this.sessionUpdateXMLParser = sessionUpdateXMLParser;
	}

	public HashMap<String,Session> getSessions() {
		return sessions;
	}

	public void setSessions(HashMap<String,Session> sessions) {
		this.sessions = sessions;
	}

	public Header getSessionUpdateHeader() {
		return sessionUpdateHeader;
	}

	public void setSessionUpdateHeader(Header header) {
		this.sessionUpdateHeader = header;
	}

}
