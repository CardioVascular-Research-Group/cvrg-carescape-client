package edu.jhu.cvrg.carescape.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class CarescapeTCPConnection {
	
	private Socket socket;
	private DataOutputStream outStream;

	public CarescapeTCPConnection(Socket socket){
		this.socket = socket;
		
		try {
			outStream = new DataOutputStream(this.socket.getOutputStream());
		} catch (IOException e) {

			e.printStackTrace();
		}
		
	}
	
	public boolean send(String content){
		try {
			outStream.writeBytes(content);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
