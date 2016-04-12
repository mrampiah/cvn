package model;
import java.util.LinkedList;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public class Patient extends User {
	// private LinkedList<Caregiver> caregivers;
	protected LinkedList<MedicalStaff> assignedStaff;
	private PatientProfile preferences;
	private LinkedList<Medication> medication;
	private LinkedList<HealthInfo> healthInfo;
	private int patientID;
	
	public Patient(String firstName, String lastName, String userID, int patientID) {
		super(firstName, lastName, userID, "Patient");
		this.assignedStaff = new LinkedList<MedicalStaff>();
		this.preferences = new PatientProfile();
		this.medication = new LinkedList<Medication>();
		this.patientID = patientID;
		this.healthInfo = new LinkedList<HealthInfo>();
	}
	
	public LinkedList<MedicalStaff> getAssignedStaff() {
		return this.assignedStaff;
	}
	
	public void addMedicalStaff(MedicalStaff staff) {
		if (this.assignedStaff.indexOf(staff) < 0) {
			this.assignedStaff.add(staff);
		}
	}
	
	public void removeMedicalStaff(MedicalStaff staff) {
		if (this.assignedStaff.indexOf(staff) >= 0) {
			this.assignedStaff.remove(staff);
		}
	}
	/*
	public LinkedList<Caregiver> getCaregivers() {
		return this.caregivers;
	}
	
	public void addCaregiver(Caregiver caregiver) {
		if (this.caregivers.indexOf(caregiver) < 0) {
			this.caregivers.add(caregiver);
		}
	}
	
	public void removeCaregiver(Caregiver caregiver) {
		if (this.caregivers.indexOf(caregiver) >= 0) {
			this.caregivers.remove(caregiver);
		}
	}
	*/
	public LinkedList<Medication> getMedication() {
		return this.medication;
	}
	
	public void addMedication(Medication medication) {
		if (this.medication.indexOf(medication) < 0) {
			this.medication.add(medication);
		}
	}
	
	public void removeMedication(Medication medication) {
		if (this.medication.indexOf(medication) >= 0) {
			this.medication.remove(medication);
		}
	}
	
	public void setPreferences(PatientProfile preferences) {
		this.preferences = preferences;
	}
	
	public PatientProfile getPreferences() {
		return this.preferences;
	}

	public int getPatientID() {
		return patientID;
	}

	public void setPatientID(int patientID) {
		this.patientID = patientID;
	}

	@Override
	public StringProperty getFirstNameProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringProperty getLastNameProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntegerProperty getUserIDProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	public LinkedList<HealthInfo> getHealthInfo() {
		return healthInfo;
	}

	public void setHealthInfo(LinkedList<HealthInfo> healthInfo) {
		this.healthInfo = healthInfo;
	}
	
	public void addHealthInfo(HealthInfo healthInfo) {
		this.healthInfo.add(healthInfo);
	}
}
