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

import java.util.ArrayList;

import edu.jhu.icm.mulesoft.common.PatientId;

public class ASAPPatientInformation {

	private String acuity, bed, bedStatus;
	private ArrayList<String> chiefComplaintLst;
	private String decisionToAdmit, eACUWorkflow, eDPrimaryCareArea, iLI; 
	private ArrayList<String> isolationLst;
	private String levelOfCare;
	private ArrayList<PatientId> patientIds;
	private String resultStatus, room, service, specialNeeds, transferTime, unit, workflowStatus;

	public String getAcuity() {
		return acuity;
	}
	
	public void setAcuity(String acuity) {
		this.acuity = acuity;
	}

	public String getBed() {
		return bed;
	}

	public void setBed(String bed) {
		this.bed = bed;
	}

	public String getBedStatus() {
		return bedStatus;
	}

	public void setBedStatus(String bedStatus) {
		this.bedStatus = bedStatus;
	}

	public ArrayList<String> getChiefComplaintLst() {
		return chiefComplaintLst;
	}

	public void setChiefComplaintLst(ArrayList<String> chiefComplaintLst) {
		this.chiefComplaintLst = chiefComplaintLst;
	}

	public String getDecisionToAdmit() {
		return decisionToAdmit;
	}

	public void setDecisionToAdmit(String decisionToAdmit) {
		this.decisionToAdmit = decisionToAdmit;
	}

	public String getEACUWorkflow() {
		return eACUWorkflow;
	}

	public void setEACUWorkflow(String eACUWorkflow) {
		this.eACUWorkflow = eACUWorkflow;
	}

	public String getEDPrimaryCareArea() {
		return eDPrimaryCareArea;
	}

	public void setEDPrimaryCareArea(String eDPrimaryCareArea) {
		this.eDPrimaryCareArea = eDPrimaryCareArea;
	}

	public String getILI() {
		return iLI;
	}

	public void setILI(String iLI) {
		this.iLI = iLI;
	}

	public ArrayList<String> getIsolationLst() {
		return isolationLst;
	}

	public void setIsolationLst(ArrayList<String> isolationLst) {
		this.isolationLst = isolationLst;
	}

	public String getLevelOfCare() {
		return levelOfCare;
	}

	public void setLevelOfCare(String levelOfCare) {
		this.levelOfCare = levelOfCare;
	}

	public ArrayList<PatientId> getPatientIds() {
		return patientIds;
	}

	public void setPatientIds(ArrayList<PatientId> patientIds) {
		this.patientIds = patientIds;
	}

	public String getResultStatus() {
		return resultStatus;
	}

	public void setResultStatus(String resultStatus) {
		this.resultStatus = resultStatus;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getSpecialNeeds() {
		return specialNeeds;
	}

	public void setSpecialNeeds(String specialNeeds) {
		this.specialNeeds = specialNeeds;
	}

	public String getTransferTime() {
		return transferTime;
	}

	public void setTransferTime(String transferTime) {
		this.transferTime = transferTime;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getWorkflowStatus() {
		return workflowStatus;
	}

	public void setWorkflowStatus(String workflowStatus) {
		this.workflowStatus = workflowStatus;
	}

}
