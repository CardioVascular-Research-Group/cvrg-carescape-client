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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.jhu.cvrg.carescape.messages.InMessage;
import edu.jhu.cvrg.carescape.messages.OutMessage;
import edu.jhu.icm.mulesoft.MuleSoftAccess;
import edu.jhu.icm.mulesoft.common.PatientId;
import edu.jhu.icm.mulesoft.facilities.BeddedPatientMin;
import edu.jhu.icm.mulesoft.patients.MedicalResourceNumber;

public class CarescapeUtilities {

	public enum HttpVerbs {GET,	POST, PUT, DELETE}

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

	public static CarescapeUDPConnection openConnection(String hostAddress, int localPortNumber){
		CarescapeUDPConnection connection = null;
		try {
			Socket socket = new Socket();
			socket.bind(new InetSocketAddress(InetAddress.getLocalHost(), localPortNumber));
			socket.connect(new InetSocketAddress(hostAddress, 2007));
			connection = new CarescapeUDPConnection(socket);

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

	public static HttpURLConnection openHTTPConnection(String urlString, HttpVerbs verb, String clientId, String clientSecret) throws MalformedURLException, IOException{
		URL url = null;
		HttpURLConnection conn = null;

		url = new URL(urlString);
		conn = (HttpURLConnection) url.openConnection();

		switch(verb){
		case POST:
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);				
			break;
		case PUT:
			conn.setRequestMethod("PUT");		
			conn.setDoOutput(true);				
			break;
		case DELETE:	
			conn.setRequestMethod("DELETE");	
			break;
		default:		
			conn.setRequestMethod("GET");		
			break;
		}

		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Content-type", "application/json");
		conn.setRequestProperty("client_id", clientId);
		conn.setRequestProperty("client_secret", clientSecret);

		return conn;
	}

	public static String readHTTPConnection(HttpURLConnection conn) throws UnsupportedEncodingException, IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader br;

		br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line + "\n");
		}
		br.close();

		return sb.toString();
	}

	public static JSONArray makeResponseJSONArray(String data){

		JSONArray array = null;
		try{
			array = new JSONArray(data);
		} catch (JSONException e){
			array = new JSONArray();
			array.put(makeResponseJSONObject(data));
		}

		return array;
	}

	public static JSONObject makeResponseJSONObject(String data){

		JSONArray result = null;
		JSONObject ret = null;
		try{
			result = new JSONArray(data);
			ret = (JSONObject) result.get(0);
		} catch (JSONException e){
			e.printStackTrace();
		}

		return ret;
	}

	public static void sendOutMessage(BufferedWriter out, OutMessage outMessage, boolean echo) {

		try {
			String messageToSend = outMessage.getMessageHeader() + outMessage.getMessageBody();
			if (echo) System.out.println(messageToSend);
			out.write(messageToSend);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static String receiveInMessage(BufferedReader br, String inMessage, boolean echo) {

		String fromServer = "";
		try {
			if (echo) System.out.println("before while");
			while ((fromServer = br.readLine()) != null) {
				inMessage += fromServer + "\n";
				if (echo) System.out.println(fromServer);
				if (inMessage.endsWith("--\n")) break;
			}
			if (echo) System.out.println("after while");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return inMessage;
	}

	public static InMessage processInMessage(String response, InMessage inMessage) {

		SimpleMimeReader smr = new SimpleMimeReader(new ByteArrayInputStream(response.getBytes()));
		inMessage.setMessageHeaderType(smr.getMessageHeaderType());
		inMessage.setMessageHeader(smr.getMessageHeader() + "\n");
		inMessage.setContentEncapsulationLength(new Integer(smr.getMessageHeaderLength()));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		for (String boundary : inMessage.getBoundaries()) {
			smr.nextPart();
			inMessage.setMessageBody(inMessage.getMessageBody() + "--" + boundary + "\n");
			inMessage.setMessageBody(inMessage.getMessageBody() + "Content-Type: " + smr.getPartType() + "\n");
			inMessage.setMessageBody(inMessage.getMessageBody() + "Content-Transfer-Encoding: " + smr.getPartEncoding() + "\n");
			inMessage.setXmlMessageLength(new Integer(smr.getPartLength()));
			inMessage.setMessageBody(inMessage.getMessageBody() + "Content-Length: " + smr.getPartLength() + "\n");
			smr.getPartData(baos);
			inMessage.setMessageBody(inMessage.getMessageBody() + baos.toString() + "\n");
			inMessage.setMessageXML(baos.toString());
			baos.reset();
			inMessage.setMessageBody(inMessage.getMessageBody() + "--" + boundary + "--" + "\n");
		}

		return inMessage;
	}

	public static void checkDir(File dirToCheck) {
		if (!dirToCheck.exists()) {
			try{
				dirToCheck.mkdir();
			} 
			catch(SecurityException e){
				e.printStackTrace();
			}        
		}
	}

	public static HashMap<String,Object> checkBedSubjectinADT(HashMap<String,Object> muleSoftCriteria) throws Exception {

		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		int fiveMinutes = 5*60*1000;

		MuleSoftAccess msa = (MuleSoftAccess) muleSoftCriteria.get("msa");
		String hospitalName = (String) muleSoftCriteria.get("hospitalName");
		String unitId = (String) muleSoftCriteria.get("unitId");
		String bed = (String) muleSoftCriteria.get("bed");
		String rootDirectory = (String) muleSoftCriteria.get("rootDirectory");
		String adtUnitBed = "";

		BeddedPatientMin bPMinLocal = msa.getBPMin(hospitalName, unitId, bed);
		while (bPMinLocal.getCSN().equalsIgnoreCase("")) {
			System.err.println(myFormat.format(System.currentTimeMillis()) + ": Bed appears to be empty in the ADT.  Checking again in 5 minutes.");
			Thread.sleep(fiveMinutes);
			bPMinLocal = msa.getBPMin(hospitalName, unitId, bed);
		}

		if ((((BeddedPatientMin) muleSoftCriteria.get("bPMin")).getCSN().equalsIgnoreCase("")) ||
				(!bPMinLocal.getCSN().equalsIgnoreCase(((BeddedPatientMin) muleSoftCriteria.get("bPMin")).getCSN()))) {
			System.out.println(myFormat.format(System.currentTimeMillis()) + ": New Subject");
			adtUnitBed = hospitalName + "." + unitId + "." + bPMinLocal.getRoomName() + "." + bPMinLocal.getBedName(); 
			System.out.println("ADT Unit Bed:\t\t" + adtUnitBed);
			muleSoftCriteria.put("bPMin", bPMinLocal);
			System.out.println("CSN for Subject:\t" + bPMinLocal.getCSN());
			String mrnToFind = "";
			for (PatientId pId : bPMinLocal.getPatientIDs()) {
				if (pId.getType().equalsIgnoreCase("EMRN")) { 
					mrnToFind = pId.getId();
				}
			}

			MedicalResourceNumber mrnLocal = msa.getMRN(mrnToFind);
			muleSoftCriteria.put("mrn", mrnLocal);
			System.out.println("Subject GUID:\t\t" + mrnLocal.getGUID());
			System.out.println();

			String[] folderStructure = adtUnitBed.split("\\.");
			File check = new File(rootDirectory);
			if (!check.exists()) 
				throw new Exception(myFormat.format(System.currentTimeMillis()) + ": Root directory " + check.getAbsolutePath() + " does not exist.  Check the settings in the configuration file." );
			String filePath = rootDirectory;
			filePath += mrnLocal.getGUID() + File.separator;
			check = new File(filePath);
			CarescapeUtilities.checkDir(check);
			filePath += bPMinLocal.getCSN() + File.separator;
			check = new File(filePath);
			CarescapeUtilities.checkDir(check);
			for (String folder : folderStructure) {
				filePath += folder + File.separator;
				check = new File(filePath);
				CarescapeUtilities.checkDir(check);
			}
			muleSoftCriteria.put("filePath", filePath);
		} else {
			System.out.println(myFormat.format(System.currentTimeMillis()) + ": Same Subject");
		}

		return muleSoftCriteria;
	}
}
