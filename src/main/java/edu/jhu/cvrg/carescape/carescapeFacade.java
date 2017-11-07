package edu.jhu.cvrg.carescape;

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
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.jhu.cvrg.carescape.client.CarescapeMonitor;

@Component
public class carescapeFacade {

	@Autowired
	private String carescapeIPAddress;

	@Autowired
	private String mulesoftAddress;

	@Autowired
	private String mulesoftClientId;

	@Autowired
	private String mulesoftClientSecret;

	@Autowired
	private String openTSDBAddress;

	@Autowired
	private String roomToCapture;

	@Autowired
	private String rootDirectory;	
	
	public void processRoom(String room, boolean high, boolean echo, int minutesToWait) throws IOException, ParseException {
		
		if (room.isEmpty()) room = roomToCapture;
		System.out.println("Root Directory to store data in: " + rootDirectory);
		System.out.println("Minutes to wait before writing data: " + minutesToWait);
		System.out.println("Room to Capture: " + room);
		System.out.println("High Speed: " + high);
		HashMap<String, String> muleSoftVariables = new HashMap<String, String>();
		muleSoftVariables.put("address", mulesoftAddress);
		muleSoftVariables.put("client_id", mulesoftClientId);
		muleSoftVariables.put("client_secret", mulesoftClientSecret);
		new CarescapeMonitor(carescapeIPAddress, openTSDBAddress, muleSoftVariables, room, high, echo, minutesToWait, rootDirectory);
		
	}

}
