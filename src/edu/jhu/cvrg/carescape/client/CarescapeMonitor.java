package edu.jhu.cvrg.carescape.client;

import edu.jhu.cvrg.carescape.util.CarescapeUtilities;
import edu.jhu.cvrg.carescape.util.EnumConnectionProtocol;

public class CarescapeMonitor {

	private EnumConnectionProtocol connectionProtocol = EnumConnectionProtocol.TCP;

	
	public static void main(String[] args) {
		String targetAddress = args[0];
		
		//arguments {targetAddress, protocol(TCP or UDP), sessionId1, sessionId2, sessionId3...}
		
		//set parameters from config if not provided in the command line
		CarescapeUtilities.openConnection(targetAddress);
		
		//Send Hello message
		
		//listen for response
		
		//handshake
		
		//switch to UDP if necessary
		
		//receive messages and send to appropriate XML parser
	}
}