package edu.jhu.cvrg.carescape.util;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class CarescapeUtilities {
	
	public static CarescapeTCPConnection openConnection(String hostAddress){
		CarescapeTCPConnection connection = null;
		try {
			Socket socket = new Socket(hostAddress, 2007);
			connection = new CarescapeTCPConnection(socket);
			
		} catch (UnknownHostException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return connection;
	}

	public static boolean sendMessage(){
		return true;
	}
}
