package edu.jhu.icm.mulesoft.patients;

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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import edu.jhu.icm.mulesoft.common.PatientId;

public class MedicalResourceNumber {

	private ArrayList<Address> addresses;
	private ArrayList<String> aliases;
	private String bMCMRN;
	private ArrayList<CareProvider> careTeam;
	private String confidentialName, dateOfBirth, eMRN;
	private ArrayList<Contact> emergencyContacts;
	private EmploymentInformation employmentInformation;
	private String ethnicGroup, gUID, hCGMRN;
	private ArrayList<PatientId> historicalIds;
	private String homeDeployment, jHHMRN;
	private ArrayList<PatientId> patientIds;
	private String maritalStatus, name;
	private NameComponents nameComponents;
	private String nationalIdentifier;
	private ArrayList<String> races;
	private String rank, sex, sMHMRN, status;
	
	public MedicalResourceNumber() {
		
		setGUID("");
		
	}
	
	public ArrayList<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(ArrayList<Address> addresses) {
		this.addresses = addresses;
	}

	public ArrayList<String> getAliases() {
		return aliases;
	}

	public void setAliases(ArrayList<String> aliases) {
		this.aliases = aliases;
	}

	public ArrayList<CareProvider> getCareTeam() {
		return careTeam;
	}

	public void setCareTeam(ArrayList<CareProvider> careTeam) {
		this.careTeam = careTeam;
	}

	public String getConfidentialName() {
		return confidentialName;
	}

	public void setConfidentialName(String confidentialName) {
		this.confidentialName = confidentialName;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public ArrayList<Contact> getEmergencyContacts() {
		return emergencyContacts;
	}

	public void setEmergencyContacts(ArrayList<Contact> emergencyContacts) {
		this.emergencyContacts = emergencyContacts;
	}

	public EmploymentInformation getEmploymentInformation() {
		return employmentInformation;
	}

	public void setEmploymentInformation(EmploymentInformation employmentInformation) {
		this.employmentInformation = employmentInformation;
	}

	public String getEthnicGroup() {
		return ethnicGroup;
	}

	public void setEthnicGroup(String ethnicGroup) {
		this.ethnicGroup = ethnicGroup;
	}

	public ArrayList<PatientId> getHistoricalIds() {
		return historicalIds;
	}

	public void setHistoricalIds(ArrayList<PatientId> historicalIds) {
		this.historicalIds = historicalIds;
	}

	public String getHomeDeployment() {
		return homeDeployment;
	}

	public void setHomeDeployment(String homeDeployment) {
		this.homeDeployment = homeDeployment;
	}

	public ArrayList<PatientId> getPatientIds() {
		return patientIds;
	}

	public void setPatientIds(ArrayList<PatientId> patientIds) {
		for (PatientId patientId : patientIds) {
			switch (patientId.getType()) {
			case "BMCMRN":
				setBMCMRN(patientId.getId());
				break;
			case "EMRN":
				setEMRN(patientId.getId());
				break;
			case "HCGMRN":
				setHCGMRN(patientId.getId());
				break;
			case "JHHMRN":
				setJHHMRN(patientId.getId());
				break;
			case "SMHMRN":
				setSMHMRN(patientId.getId());
				break;
			}
		}
		this.patientIds = patientIds;
	}

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NameComponents getNameComponents() {
		return nameComponents;
	}

	public void setNameComponents(NameComponents nameComponents) {
		this.nameComponents = nameComponents;
	}

	public String getNationalIdentifier() {
		return nationalIdentifier;
	}

	public void setNationalIdentifier(String nationalIdentifier) {
		this.nationalIdentifier = nationalIdentifier;
	}

	public ArrayList<String> getRaces() {
		return races;
	}

	public void setRaces(ArrayList<String> races) {
		this.races = races;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBMCMRN() {
		return bMCMRN;
	}

	public void setBMCMRN(String bMCMRN) {
		this.bMCMRN = bMCMRN;
	}

	public String getEMRN() {
		return eMRN;
	}

	public void setEMRN(String eMRN) {
		this.eMRN = eMRN;
	}

	public String getHCGMRN() {
		return hCGMRN;
	}

	public void setHCGMRN(String hCGMRN) {
		this.hCGMRN = hCGMRN;
	}
	
	public String getJHHMRN() {
		return jHHMRN;
	}

	public void setJHHMRN(String jHHMRN) {
		this.jHHMRN = jHHMRN;
	}

	public String getSMHMRN() {
		return sMHMRN;
	}

	public void setSMHMRN(String sMHMRN) {
		this.sMHMRN = sMHMRN;
	}
	
	public String getGUID() {
		if (gUID.length() < 64)
			setGUID(setHash(getEMRN() + getNameComponents().getFirstName() + getNameComponents().getLastName() + getDateOfBirth()));
		return gUID;
	}

	public void setGUID(String gUID) {
		this.gUID = gUID;
	}


	/**
	 * @param hash the hash to set
	 */
	private static String setHash(String hash) {
		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-256");
			byte[] result =  sha.digest(hash.getBytes());
			hash = hexEncode(result);
			hash = escapeHtml(hash);
			//System.out.println("Message digest: " + hash);
		}
		catch ( NoSuchAlgorithmException ex ) {
			System.err.println(ex);
		}
		return hash;

	}

	/**
	 * The byte[] returned by MessageDigest does not have a nice
	 * textual representation, so some form of encoding is usually performed.
	 *
	 * This implementation follows the example of David Flanagan's book
	 * "Java In A Nutshell", and converts a byte array into a String
	 * of hex characters.
	 *
	 * Another popular alternative is to use a "Base64" encoding.
	 **/
	private static String hexEncode( byte[] aInput){
		StringBuilder result = new StringBuilder();
		char[] digits = {'0', '1', '2', '3', '4','5','6','7','8','9','a','b','c','d','e','f'};
		for ( int idx = 0; idx < aInput.length; ++idx) {
			byte b = aInput[idx];
			result.append( digits[ (b&0xf0) >> 4 ] );
			result.append( digits[ b&0x0f] );
		}
		return result.toString();
	} 

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 **/

	private static String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}

}
