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

public class BeddedPatient {

	private String accomodationCode, acuity, addressState, admitDateTime;
    private ArrayList<Diagnosis> admitDiagnoses;
    private String admitPoint, admitSource, admittingProvider, age, attendingProvider, bedName, bedStatus, cSN, departmentAbbr, departmentID, departmentName, dischargeDisp, eDDepartureTime, functionalUnit, gender, hAR;
    private ArrayList<Diagnosis> hospitalProblemList;
    private String hospitalAreaID, hospitalAreaName, hospitalService;
    private ArrayList<String> isolationLst;
    private String lengthOfStayDays, levelofCare;
    private ArrayList<MedicalHistory> medicalHistories;    
    private String medicalHistoryApptTime, medicalHistoryContactDate, patientClass;
    private ArrayList<PatientId> patientIDs;
    private String patientName;
    private ArrayList<Diagnosis> problemList;    
    private String roomName;
    private String specialNeeds;

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

	public ArrayList<Diagnosis> getAdmitDiagnoses() {
		return admitDiagnoses;
	}

	public void setAdmitDiagnoses(ArrayList<Diagnosis> admitDiagnoses) {
		this.admitDiagnoses = admitDiagnoses;
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

	public String getcSN() {
		return cSN;
	}

	public void setcSN(String cSN) {
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

	public String getDischargeDisp() {
		return dischargeDisp;
	}

	public void setDischargeDisp(String dischargeDisp) {
		this.dischargeDisp = dischargeDisp;
	}

	public String getEDDepartureTime() {
		return eDDepartureTime;
	}

	public void setEDDepartureTime(String eDDepartureTime) {
		this.eDDepartureTime = eDDepartureTime;
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

	public ArrayList<Diagnosis> getHospitalProblemList() {
		return hospitalProblemList;
	}

	public void setHospitalProblemList(ArrayList<Diagnosis> hospitalProblemList) {
		this.hospitalProblemList = hospitalProblemList;
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

	public ArrayList<MedicalHistory> getMedicalHistories() {
		return medicalHistories;
	}

	public void setMedicalHistories(ArrayList<MedicalHistory> medicalHistories) {
		this.medicalHistories = medicalHistories;
	}

	public String getMedicalHistoryApptTime() {
		return medicalHistoryApptTime;
	}

	public void setMedicalHistoryApptTime(String medicalHistoryApptTime) {
		this.medicalHistoryApptTime = medicalHistoryApptTime;
	}

	public String getMedicalHistoryContactDate() {
		return medicalHistoryContactDate;
	}

	public void setMedicalHistoryContactDate(String medicalHistoryContactDate) {
		this.medicalHistoryContactDate = medicalHistoryContactDate;
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

	public ArrayList<Diagnosis> getProblemList() {
		return problemList;
	}

	public void setProblemList(ArrayList<Diagnosis> problemList) {
		this.problemList = problemList;
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
	  
}
