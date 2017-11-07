package edu.jhu.icm.mulesoft.facilities;

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

import java.util.ArrayList;

import edu.jhu.icm.mulesoft.common.PatientId;

public class BeddedPatientMin {

	private String accomodationCode, acuity, addressState, admitDateTime, admitPoint, admitSource, admittingProvider, age, attendingProvider, bedName, bedStatus, cSN, departmentAbbr, departmentID, departmentName, dischargeDest, dischargeDisp, expectedDischargeDate, functionalUnit, gender, hAR, hospitalAreaID, hospitalAreaName, hospitalService;
    private ArrayList<String> isolationLst;
    private String lengthOfStayDays, levelofCare, patientClass;
    private ArrayList<PatientId> patientIDs;
    private String patientName;
    private String roomName;
    private String specialNeeds;

    public BeddedPatientMin() {
    	
        setAccomodationCode("");
    	setAcuity("");
    	setAddressState("");
    	setAdmitDateTime("");
    	setAdmitPoint("");
    	setAdmitSource("");
    	setAdmittingProvider("");
    	setAge("");
    	setAttendingProvider("");
    	setBedName("");
    	setBedStatus("");
    	setCSN("");
    	setDepartmentAbbr("");
    	setDepartmentID("");
    	setDepartmentName("");
    	setDischargeDest("");
    	setDischargeDisp("");
    	setExpectedDischargeDate("");
    	setFunctionalUnit("");
    	setGender("");
    	setHAR("");
    	setHospitalAreaID("");
    	setHospitalAreaName("");
    	setHospitalService("");
    	setIsolationLst(new ArrayList<String>());
    	setLengthOfStayDays("");
    	setLevelofCare("");
    	setPatientClass("");
    	setPatientIDs(new ArrayList<PatientId>());
    	setPatientName("");
    	setRoomName("");
    	setSpecialNeeds("");
    	
    }
    
    public String getAccomodationCode() {
		return accomodationCode;
	}
	
    public void setAccomodationCode(String accomodationCode) {
		this.accomodationCode = accomodationCode;
	}

	public String getAcuity() {
		return acuity;
	}

	public void setAcuity(String acuity) {
		this.acuity = acuity;
	}

	public String getAddressState() {
		return addressState;
	}

	public void setAddressState(String addressState) {
		this.addressState = addressState;
	}

	public String getAdmitDateTime() {
		return admitDateTime;
	}

	public void setAdmitDateTime(String admitDateTime) {
		this.admitDateTime = admitDateTime;
	}

	public String getAdmitPoint() {
		return admitPoint;
	}

	public void setAdmitPoint(String admitPoint) {
		this.admitPoint = admitPoint;
	}

	public String getAdmitSource() {
		return admitSource;
	}

	public void setAdmitSource(String admitSource) {
		this.admitSource = admitSource;
	}

	public String getAdmittingProvider() {
		return admittingProvider;
	}

	public void setAdmittingProvider(String admittingProvider) {
		this.admittingProvider = admittingProvider;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getAttendingProvider() {
		return attendingProvider;
	}

	public void setAttendingProvider(String attendingProvider) {
		this.attendingProvider = attendingProvider;
	}

	public String getBedName() {
		return bedName;
	}

	public void setBedName(String bedName) {
		this.bedName = bedName;
	}

	public String getBedStatus() {
		return bedStatus;
	}

	public void setBedStatus(String bedStatus) {
		this.bedStatus = bedStatus;
	}

	public String getCSN() {
		return cSN;
	}

	public void setCSN(String cSN) {
		this.cSN = cSN;
	}

	public String getDepartmentAbbr() {
		return departmentAbbr;
	}

	public void setDepartmentAbbr(String departmentAbbr) {
		this.departmentAbbr = departmentAbbr;
	}

	public String getDepartmentID() {
		return departmentID;
	}

	public void setDepartmentID(String departmentID) {
		this.departmentID = departmentID;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getDischargeDest() {
		return dischargeDest;
	}

	public void setDischargeDest(String dischargeDest) {
		this.dischargeDest = dischargeDest;
	}

	public String getDischargeDisp() {
		return dischargeDisp;
	}

	public void setDischargeDisp(String dischargeDisp) {
		this.dischargeDisp = dischargeDisp;
	}

	public String getExpectedDischargeDate() {
		return expectedDischargeDate;
	}

	public void setExpectedDischargeDate(String expectedDischargeDate) {
		this.expectedDischargeDate = expectedDischargeDate;
	}

	public String getFunctionalUnit() {
		return functionalUnit;
	}

	public void setFunctionalUnit(String functionalUnit) {
		this.functionalUnit = functionalUnit;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getHAR() {
		return hAR;
	}

	public void setHAR(String hAR) {
		this.hAR = hAR;
	}

	public String getHospitalAreaID() {
		return hospitalAreaID;
	}

	public void setHospitalAreaID(String hospitalAreaID) {
		this.hospitalAreaID = hospitalAreaID;
	}

	public String getHospitalAreaName() {
		return hospitalAreaName;
	}

	public void setHospitalAreaName(String hospitalAreaName) {
		this.hospitalAreaName = hospitalAreaName;
	}

	public String getHospitalService() {
		return hospitalService;
	}

	public void setHospitalService(String hospitalService) {
		this.hospitalService = hospitalService;
	}

	public ArrayList<String> getIsolationLst() {
		return isolationLst;
	}

	public void setIsolationLst(ArrayList<String> isolationLst) {
		this.isolationLst = isolationLst;
	}

	public String getLengthOfStayDays() {
		return lengthOfStayDays;
	}

	public void setLengthOfStayDays(String lengthOfStayDays) {
		this.lengthOfStayDays = lengthOfStayDays;
	}

	public String getLevelofCare() {
		return levelofCare;
	}

	public void setLevelofCare(String levelofCare) {
		this.levelofCare = levelofCare;
	}


	public String getPatientClass() {
		return patientClass;
	}

	public void setPatientClass(String patientClass) {
		this.patientClass = patientClass;
	}

	public ArrayList<PatientId> getPatientIDs() {
		return patientIDs;
	}

	public void setPatientIDs(ArrayList<PatientId> patientIDs) {
		this.patientIDs = patientIDs;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getSpecialNeeds() {
		return specialNeeds;
	}

	public void setSpecialNeeds(String specialNeeds) {
		this.specialNeeds = specialNeeds;
	}
	
	public String toString() {
		
		String output = accomodationCode + "\n";
		output += acuity + "\n";
		output += addressState + "\n";
		output += admitDateTime + "\n";
		output += admitPoint + "\n";
		output += admitSource + "\n";
		output += admittingProvider + "\n";
		output += age + "\n";
		output += attendingProvider + "\n";
		output += bedName + "\n";
		output += bedStatus + "\n";
		output += cSN + "\n";
		output += departmentAbbr + "\n";
		output += departmentID + "\n";
		output += departmentName + "\n";
		output += dischargeDest + "\n";
		output += dischargeDisp + "\n";
		output += expectedDischargeDate + "\n";
		output += functionalUnit + "\n";
		output += gender + "\n";
		output += hAR + "\n";
		output += hospitalAreaID + "\n";
		output += hospitalAreaName + "\n";
		output += hospitalService + "\n";
		output += lengthOfStayDays + "\n";
		output += levelofCare + "\n";
		output += patientClass + "\n";
		output += patientIDs.toString() + "\n";
		output += patientName + "\n";
		output += roomName + "\n";
		output += specialNeeds + "\n";
		return output;
	}
	  
}
