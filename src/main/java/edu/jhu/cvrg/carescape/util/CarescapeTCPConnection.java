package edu.jhu.cvrg.carescape.util;

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

import java.io.IOException;
import java.net.Socket;

public class CarescapeTCPConnection {
	
	private Socket socket;

	public CarescapeTCPConnection(Socket socket){
		this.socket = socket;
	}	
	
	public Socket getSocket() {
		return socket;
	}
	
	public void close() {
		
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
