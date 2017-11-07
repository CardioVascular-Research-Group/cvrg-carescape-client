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

public class NameComponents {

	private String academic, firstName, givenNameInitials, lastName, lastNameFromSpouse, lastNamePrefix, middleName, preferredName, preferredNameType, spouseLastNameFirst, spouseLastNamePrefix, suffix, title;

	public String getAcademic() {
		return academic;
	}

	public void setAcademic(String academic) {
		this.academic = academic;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getGivenNameInitials() {
		return givenNameInitials;
	}

	public void setGivenNameInitials(String givenNameInitials) {
		this.givenNameInitials = givenNameInitials;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getLastNameFromSpouse() {
		return lastNameFromSpouse;
	}

	public void setLastNameFromSpouse(String lastNameFromSpouse) {
		this.lastNameFromSpouse = lastNameFromSpouse;
	}

	public String getLastNamePrefix() {
		return lastNamePrefix;
	}

	public void setLastNamePrefix(String lastNamePrefix) {
		this.lastNamePrefix = lastNamePrefix;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getPreferredName() {
		return preferredName;
	}

	public void setPreferredName(String preferredName) {
		this.preferredName = preferredName;
	}

	public String getPreferredNameType() {
		return preferredNameType;
	}

	public void setPreferredNameType(String preferredNameType) {
		this.preferredNameType = preferredNameType;
	}

	public String getSpouseLastNameFirst() {
		return spouseLastNameFirst;
	}

	public void setSpouseLastNameFirst(String spouseLastNameFirst) {
		this.spouseLastNameFirst = spouseLastNameFirst;
	}

	public String getSpouseLastNamePrefix() {
		return spouseLastNamePrefix;
	}

	public void setSpouseLastNamePrefix(String spouseLastNamePrefix) {
		this.spouseLastNamePrefix = spouseLastNamePrefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}