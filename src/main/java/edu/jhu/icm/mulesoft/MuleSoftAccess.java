package edu.jhu.icm.mulesoft;

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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.jhu.cvrg.carescape.util.CarescapeUtilities;
import edu.jhu.icm.mulesoft.common.Identifier;
import edu.jhu.icm.mulesoft.common.PatientId;
import edu.jhu.icm.mulesoft.common.PhoneNumber;
import edu.jhu.icm.mulesoft.facilities.BeddedPatientMin;
import edu.jhu.icm.mulesoft.facilities.BeddedPatientsMin;
import edu.jhu.icm.mulesoft.patients.Address;
import edu.jhu.icm.mulesoft.patients.AdtLocationHistory;
import edu.jhu.icm.mulesoft.patients.CareProvider;
import edu.jhu.icm.mulesoft.patients.Contact;
import edu.jhu.icm.mulesoft.patients.EmploymentInformation;
import edu.jhu.icm.mulesoft.patients.LocationHistory;
import edu.jhu.icm.mulesoft.patients.MedicalResourceNumber;
import edu.jhu.icm.mulesoft.patients.NameComponents;

public class MuleSoftAccess {

	private HashMap<String, String> muleSoftVariables = new HashMap<String, String>();

	public static void main(String args[]) {

		new MuleSoftAccess("<String for hospitalName>", "<String for unitId>", "<String for bedNumber>");

	}

	public MuleSoftAccess() {

	}

	public MuleSoftAccess(String hospitalName, String unitId, String roomToFind) {

		try {

			BeddedPatientMin bPMin = getBPMin(hospitalName, unitId, roomToFind);

			System.out.println("MuleSoft Facilities Unit Bed Min Search for " + hospitalName + " with unit Id " + unitId);
			System.out.println("Unit Room to Search for:\t" + unitId.split("-")[1] + bPMin.getRoomName());
			System.out.println("Unit Id:\t\t\t" + unitId.split("-")[1]);
			System.out.println("Department (ADT Unit) Name:\t" + bPMin.getDepartmentName());
			System.out.println("Room Name:\t\t\t" + bPMin.getRoomName());
			System.out.println("Bed Name:\t\t\t" + bPMin.getBedName());
			System.out.println("Actual Location:\t\t" + unitId.split("-")[1] + bPMin.getRoomName() + bPMin.getBedName());
			System.out.println("CSN:\t\t\t\t" + bPMin.getCSN());
			System.out.println("First Name:\t\t\t" + bPMin.getPatientName().split(",")[1].split("\\s+")[0]);
			if (bPMin.getPatientName().split(",")[1].split("\\s+").length > 1)
				System.out.println("Middle Name:\t\t\t" + bPMin.getPatientName().split(",")[1].split("\\s+")[1]);
			System.out.println("Last Name:\t\t\t" + bPMin.getPatientName().split(",")[0]);
			String mrnToFind = "";
			for (PatientId pId : bPMin.getPatientIDs()) {
				if (pId.getType().length() > 6) {
					System.out.print(pId.getType() + ":\t\t\t");
				} else {
					System.out.print(pId.getType() + ":\t\t\t\t");
				}
				System.out.println(pId.getId());
				if (pId.getType().equalsIgnoreCase("EMRN")) { 
					mrnToFind = pId.getId();
				}
			}
			System.out.println();

			MedicalResourceNumber mrn = getMRN(mrnToFind);
			System.out.println("MuleSoft Patients Epic MRN search for:\t" +  mrn.getEMRN());
			System.out.println("First Name:\t\t\t\t" + mrn.getNameComponents().getFirstName());
			System.out.println("Last Name:\t\t\t\t" + mrn.getNameComponents().getLastName());
			System.out.println("Date Of Birth:\t\t\t\t" + mrn.getDateOfBirth());
			System.out.println("Subject GUID:\t\t\t\t" + mrn.getGUID());
			System.out.println();

			AdtLocationHistory adtLocationHistory = getAdtLocationHistory(bPMin.getCSN());
			System.out.println("MuleSoft Patients AdtLocationHistory for Epic MRN:\t" + mrn.getEMRN() + " (actually CSN " + bPMin.getCSN() + ")");
			System.out.println("Date/Time\t\t\tUnit Name\t\tRoom Name\t\tEvent Type");
			for (LocationHistory locationHistory : adtLocationHistory.getLocationHistories()) {
				System.out.print(locationHistory.getEffectiveDateTime() + "\t");
				System.out.print(locationHistory.getUnitName() + "\t");
				if (locationHistory.getRoomName().length() == 3) {
					System.out.print(locationHistory.getRoomName() + "\t\t\t"); 
				} else {
					System.out.print(locationHistory.getRoomName() + "\t");					
				}
				System.out.println(locationHistory.getEventType());
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public BeddedPatientMin getBPMin(String hospitalName, String unitId, String roomToFind) throws MalformedURLException, IOException, JSONException {

		BeddedPatientMin bPMin = new BeddedPatientMin();

		try {
			String urlString = getMuleSoftVariables().get("address") + "internal/v2/clinical/facilities/hospital/" + hospitalName + "/beddedpatients/min?unitid=" + unitId;
			HttpURLConnection httpConnection = CarescapeUtilities.openHTTPConnection(urlString, CarescapeUtilities.HttpVerbs.GET, getMuleSoftVariables().get("client_id"), getMuleSoftVariables().get("client_secret"));
			String output = CarescapeUtilities.readHTTPConnection(httpConnection);
			JSONArray result = CarescapeUtilities.makeResponseJSONArray(output);
			BeddedPatientsMin bPatientsMin = new BeddedPatientsMin();
			ArrayList<BeddedPatientMin> beddedPatientsMin = new ArrayList<BeddedPatientMin>();
			for (int i = 0; i < result.length(); i++) {
				BeddedPatientMin beddedPatientMin = new BeddedPatientMin();
				JSONObject bPatientMin = result.getJSONObject(i);
				beddedPatientMin.setAccomodationCode(bPatientMin.getString("AccomodationCode"));
				beddedPatientMin.setAcuity(bPatientMin.getString("Acuity"));
				beddedPatientMin.setAddressState(bPatientMin.getString("AddressState"));
				beddedPatientMin.setAdmitDateTime(bPatientMin.getString("AdmitDateTime"));
				beddedPatientMin.setAdmitPoint(bPatientMin.getString("AdmitPoint"));
				beddedPatientMin.setAdmitSource(bPatientMin.getString("AdmitSource"));
				beddedPatientMin.setAdmittingProvider(bPatientMin.getString("AdmittingProvider"));
				beddedPatientMin.setAttendingProvider(bPatientMin.getString("AttendingProvider"));
				beddedPatientMin.setBedName(bPatientMin.getString("BedName"));
				beddedPatientMin.setBedStatus(bPatientMin.getString("BedStatus"));
				beddedPatientMin.setCSN(bPatientMin.getString("CSN"));
				beddedPatientMin.setDepartmentAbbr(bPatientMin.getString("DepartmentAbbr"));
				beddedPatientMin.setDepartmentID(bPatientMin.getString("DepartmentID"));
				beddedPatientMin.setDepartmentName(bPatientMin.getString("DepartmentName"));
				beddedPatientMin.setDischargeDisp(bPatientMin.getString("DischargeDisp"));
				beddedPatientMin.setFunctionalUnit(bPatientMin.getString("FunctionalUnit"));
				beddedPatientMin.setHAR(bPatientMin.getString("HAR"));
				beddedPatientMin.setHospitalAreaID(bPatientMin.getString("HospitalAreaID"));
				beddedPatientMin.setHospitalAreaName(bPatientMin.getString("HospitalAreaName"));
				beddedPatientMin.setHospitalService(bPatientMin.getString("HospitalService"));
				ArrayList<String> isolationLst = new ArrayList<String>();
				JSONArray iList = bPatientMin.getJSONArray("IsolationLst");
				for (int j = 0; j < iList.length(); j++) {
					if (!(iList.isNull(j))) {
						isolationLst.add(iList.getString(j));
					} else {
						isolationLst.add("");
					}
				}
				beddedPatientMin.setIsolationLst(isolationLst);
				beddedPatientMin.setLengthOfStayDays(bPatientMin.getString("LengthOfStayDays"));
				beddedPatientMin.setLevelofCare(bPatientMin.getString("LevelofCare"));
				beddedPatientMin.setPatientClass(bPatientMin.getString("PatientClass"));
				ArrayList<PatientId> patientIds = new ArrayList<PatientId>();
				JSONArray pIds = bPatientMin.getJSONArray("PatientIDs");
				for (int j = 0; j < pIds.length(); j++) {
					PatientId patientId = new PatientId();
					JSONObject pId = pIds.getJSONObject(j);
					patientId.setId(pId.getString("ID"));
					patientId.setType(pId.getString("Type"));
					patientIds.add(patientId);
				}
				beddedPatientMin.setPatientIDs(patientIds);
				beddedPatientMin.setPatientName(bPatientMin.getString("PatientName"));
				beddedPatientMin.setRoomName(bPatientMin.getString("RoomName"));
				beddedPatientMin.setSpecialNeeds(bPatientMin.getString("SpecialNeeds"));
				beddedPatientsMin.add(beddedPatientMin);
			}
			bPatientsMin.setBeddedPatientsMin(beddedPatientsMin);

			for (BeddedPatientMin bPatientMin : beddedPatientsMin) {
				if (bPatientMin.getRoomName().equalsIgnoreCase(roomToFind)) {
					bPMin = bPatientMin;
				}
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return bPMin;
	}

	public MedicalResourceNumber getMRN(String mrnToFind) {

		MedicalResourceNumber mrn = new MedicalResourceNumber();

		try {
			String urlString = getMuleSoftVariables().get("address") + "internal/v2/clinical/patients/mrn/" + mrnToFind;
			HttpURLConnection httpConnection = CarescapeUtilities.openHTTPConnection(urlString, CarescapeUtilities.HttpVerbs.GET, getMuleSoftVariables().get("client_id"), getMuleSoftVariables().get("client_secret"));
			String output = CarescapeUtilities.readHTTPConnection(httpConnection);
			JSONObject resultObject = CarescapeUtilities.makeResponseJSONObject(output);
			ArrayList<Address> addresses = new ArrayList<Address>();
			JSONArray mrnAddresses = resultObject.getJSONArray("Addresses");
			for (int i = 0; i < mrnAddresses.length(); i++) {
				Address address = new Address();
				JSONObject mrnAddress = mrnAddresses.getJSONObject(i);
				address.setCity(mrnAddress.getString("City"));
				address.setCountry(mrnAddress.getString("Country"));
				address.setCounty(mrnAddress.getString("County"));
				if (!(mrnAddress.isNull("District"))) {
					address.setDistrict(mrnAddress.getString("District"));
				} else {
					address.setDistrict("");					
				}
				ArrayList<String> email = new ArrayList<String>();
				JSONArray mrnEmail = mrnAddress.getJSONArray("Email");
				for (int j = 0; j < mrnEmail.length(); j++) {
					email.add(mrnEmail.getString(j));
				}
				address.setEmail(email);
				if (!(mrnAddress.isNull("HouseNumber"))) {
					address.setHouseNumber(mrnAddress.getString("HouseNumber"));
				} else {
					address.setHouseNumber("");
				}
				ArrayList<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();
				JSONArray mrnPhoneNumbers = mrnAddress.getJSONArray("PhoneNumbers");
				for (int j = 0; j < mrnPhoneNumbers.length(); j++) {
					PhoneNumber phoneNumber = new PhoneNumber();
					JSONObject mrnPhoneNumber = mrnPhoneNumbers.getJSONObject(j);
					phoneNumber.setId(mrnPhoneNumber.getString("Number"));
					phoneNumber.setType(mrnPhoneNumber.getString("Type"));					
					phoneNumbers.add(phoneNumber);
				}
				address.setPhoneNumbers(phoneNumbers);
				address.setPostalCode(mrnAddress.getString("PostalCode"));
				address.setState(mrnAddress.getString("State"));
				ArrayList<String> street = new ArrayList<String>();
				JSONArray mrnStreet = mrnAddress.getJSONArray("Street");
				for (int j = 0; j < mrnStreet.length(); j++) {
					street.add(mrnStreet.getString(j));
				}
				address.setStreet(street);
				address.setType(mrnAddress.getString("Type"));
				addresses.add(address);
			}
			mrn.setAddresses(addresses);
			ArrayList<String> aliases = new ArrayList<String>();
			JSONArray mrnAliases = resultObject.getJSONArray("Aliases");
			for (int i = 0; i < mrnAliases.length(); i++) {
				aliases.add(mrnAliases.getString(i));
			}
			mrn.setAliases(aliases);
			ArrayList<CareProvider> careTeam = new ArrayList<CareProvider>();
			JSONArray mrnCareTeams = resultObject.getJSONArray("CareTeam");
			for (int i = 0; i < mrnCareTeams.length(); i++) {
				CareProvider careProvider = new CareProvider();
				JSONObject mrnCareTeam = mrnCareTeams.getJSONObject(i);
				ArrayList<Identifier> identifiers = new ArrayList<Identifier>();
				JSONArray mrnIdentifiers = mrnCareTeam.getJSONArray("IDs");
				for (int j = 0; j < mrnIdentifiers.length(); j++) {
					Identifier identifier = new Identifier();
					JSONObject mrnIdentifier = mrnIdentifiers.getJSONObject(i);
					identifier.setId(mrnIdentifier.getString("ID"));
					identifier.setType(mrnIdentifier.getString("Type"));
					identifiers.add(identifier);
				}
				careProvider.setIdentifiers(identifiers);
				careProvider.setName(mrnCareTeam.getString("Name"));
				careProvider.setType(mrnCareTeam.getString("Type"));
				careTeam.add(careProvider);
			}
			mrn.setCareTeam(careTeam);
			if (!(resultObject.isNull("ConfidentialName"))) {
				mrn.setConfidentialName(resultObject.getString("ConfidentialName"));
			} else {
				mrn.setConfidentialName("");
			}
			mrn.setDateOfBirth(resultObject.getString("DateOfBirth"));
			ArrayList<Contact> emergencyContacts = new ArrayList<Contact>();
			JSONArray mrnEmergencyContacts = resultObject.getJSONArray("EmergencyContacts");
			for (int i = 0; i < mrnEmergencyContacts.length(); i++) {
				Contact contact = new Contact();					
				JSONObject mrnContact = mrnEmergencyContacts.getJSONObject(i);
				contact.setLegalGuardian(mrnContact.getString("LegalGuardian"));
				contact.setName(mrnContact.getString("Name"));
				ArrayList<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();
				JSONArray mrnPhoneNumbers = mrnContact.getJSONArray("PhoneNumbers");
				for (int j = 0; j < mrnPhoneNumbers.length(); j++) {
					PhoneNumber phoneNumber = new PhoneNumber();
					JSONObject mrnPhoneNumber = mrnPhoneNumbers.getJSONObject(j);
					phoneNumber.setId(mrnPhoneNumber.getString("Number"));
					phoneNumber.setType(mrnPhoneNumber.getString("Type"));					
					phoneNumbers.add(phoneNumber);
				}
				contact.setPhoneNumbers(phoneNumbers);
				emergencyContacts.add(contact);
			}
			mrn.setEmergencyContacts(emergencyContacts);
			EmploymentInformation employmentInformation = new EmploymentInformation();
			JSONObject mrnEmploymentInformation = resultObject.getJSONObject("EmploymentInformation");
			employmentInformation.setEmployerName(mrnEmploymentInformation.getString("EmployerName"));
			employmentInformation.setOccupation(mrnEmploymentInformation.getString("Occupation"));
			ArrayList<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();
			JSONArray mrnPhoneNumbers = mrnEmploymentInformation.getJSONArray("PhoneNumbers");
			for (int i = 0; i < mrnPhoneNumbers.length(); i++) {
				PhoneNumber phoneNumber = new PhoneNumber();
				JSONObject mrnPhoneNumber = mrnPhoneNumbers.getJSONObject(i);
				phoneNumber.setId(mrnPhoneNumber.getString("Number"));
				phoneNumber.setType(mrnPhoneNumber.getString("Type"));					
				phoneNumbers.add(phoneNumber);
			}
			employmentInformation.setPhoneNumbers(phoneNumbers);
			mrn.setEmploymentInformation(employmentInformation);
			mrn.setEthnicGroup(resultObject.getString("EthnicGroup"));
			ArrayList<PatientId> historicalIds = new ArrayList<PatientId>();
			JSONArray hIds = resultObject.getJSONArray("HistoricalIDs");
			for (int i = 0; i < hIds.length(); i++) {
				PatientId historicalId = new PatientId();
				JSONObject pId = hIds.getJSONObject(i);
				historicalId.setId(pId.getString("ID"));
				historicalId.setType(pId.getString("Type"));
				historicalIds.add(historicalId);
			}
			mrn.setHistoricalIds(historicalIds);
			if (!(resultObject.isNull("HomeDeployment"))) {
				mrn.setHomeDeployment(resultObject.getString("HomeDeployment"));
			} else {
				mrn.setHomeDeployment("");
			}
			ArrayList<PatientId> patientIds = new ArrayList<PatientId>();
			JSONArray pIds = resultObject.getJSONArray("IDs");
			for (int i = 0; i < pIds.length(); i++) {
				PatientId patientId = new PatientId();
				JSONObject pId = pIds.getJSONObject(i);
				patientId.setId(pId.getString("ID"));
				patientId.setType(pId.getString("Type"));
				patientIds.add(patientId);
			}
			mrn.setPatientIds(patientIds);
			mrn.setMaritalStatus(resultObject.getString("MaritalStatus"));
			mrn.setName(resultObject.getString("Name"));
			NameComponents nameComponents = new NameComponents();
			JSONObject mrnNameComponents = resultObject.getJSONObject("NameComponents");
			nameComponents.setAcademic(mrnNameComponents.getString("Academic").toUpperCase());
			nameComponents.setFirstName(mrnNameComponents.getString("FirstName").toUpperCase());
			nameComponents.setGivenNameInitials(mrnNameComponents.getString("GivenNameInitials").toUpperCase());
			nameComponents.setLastName(mrnNameComponents.getString("LastName").toUpperCase());
			nameComponents.setLastNameFromSpouse(mrnNameComponents.getString("LastNameFromSpouse").toUpperCase());
			nameComponents.setLastNamePrefix(mrnNameComponents.getString("LastNamePrefix").toUpperCase());
			nameComponents.setMiddleName(mrnNameComponents.getString("MiddleName").toUpperCase());
			nameComponents.setPreferredName(mrnNameComponents.getString("PreferredName").toUpperCase());
			nameComponents.setPreferredNameType(mrnNameComponents.getString("PreferredNameType").toUpperCase());
			nameComponents.setSpouseLastNameFirst(mrnNameComponents.getString("SpouseLastNameFirst").toUpperCase());
			nameComponents.setSpouseLastNamePrefix(mrnNameComponents.getString("SpouseLastNamePrefix").toUpperCase());
			nameComponents.setSuffix(mrnNameComponents.getString("Suffix").toUpperCase());
			nameComponents.setTitle(mrnNameComponents.getString("Title").toUpperCase());
			mrn.setNameComponents(nameComponents);
			mrn.setNationalIdentifier(resultObject.getString("NationalIdentifier"));
			ArrayList<String> races = new ArrayList<String>();
			JSONArray mrnRaces = resultObject.getJSONArray("Race");
			for (int i = 0; i < mrnRaces.length(); i++) {
				races.add(mrnRaces.getString(i));
			}
			mrn.setRaces(races);
			mrn.setRank(resultObject.getString("Rank"));
			mrn.setSex(resultObject.getString("Sex"));
			mrn.setStatus(resultObject.getString("Status"));

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return mrn;
	}

	public AdtLocationHistory getAdtLocationHistory(String cSNToFind) {

		AdtLocationHistory adtLocationHistory = new AdtLocationHistory();

		try {

			String urlString = getMuleSoftVariables().get("address") + "internal/v2/clinical/patients/adtlocationhistory?id=" + cSNToFind + "&type=CSN";
			HttpURLConnection httpConnection = CarescapeUtilities.openHTTPConnection(urlString, CarescapeUtilities.HttpVerbs.GET, getMuleSoftVariables().get("client_id"), getMuleSoftVariables().get("client_secret"));
			String output = CarescapeUtilities.readHTTPConnection(httpConnection);
			JSONArray result = CarescapeUtilities.makeResponseJSONArray(output);
			ArrayList<LocationHistory> locationHistories = new ArrayList<LocationHistory>();
			for (int i = 0; i < result.length(); i++) {
				LocationHistory locationHistory = new LocationHistory();
				JSONObject lHistory = result.getJSONObject(i);
				locationHistory.setCSN(lHistory.getString("CSN"));
				locationHistory.setEffectiveDateTime(lHistory.getString("EffectiveDateTime"));
				locationHistory.setEventType(lHistory.getString("EventType"));
				locationHistory.setRoomId(lHistory.getString("RoomId"));
				locationHistory.setRoomName(lHistory.getString("RoomName"));
				locationHistory.setUnitId(lHistory.getString("UnitId"));
				locationHistory.setUnitName(lHistory.getString("UnitName"));
				locationHistories.add(locationHistory);
			}
			adtLocationHistory.setLocationHistories(locationHistories);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return adtLocationHistory;
	}

	public HashMap<String, String> getMuleSoftVariables() {
		if (this.muleSoftVariables.isEmpty()) {
			HashMap<String, String> temp = new HashMap<String, String>();
			temp.put("address", "<address of the api in question>");
			temp.put("client_id", "<client_id for the api in question>");
			temp.put("client_secret", "<client_secret the api in question>");
			setMuleSoftVariables(temp);
		}
		return muleSoftVariables;
	}

	public void setMuleSoftVariables(HashMap<String, String> muleSoftVariables) {
		this.muleSoftVariables = muleSoftVariables;
	}

}