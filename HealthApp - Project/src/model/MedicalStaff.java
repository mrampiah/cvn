package model;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class MedicalStaff extends AbsUser {
    List<Patient> assignedPatients;


    public MedicalStaff() {

    }

    public MedicalStaff(String userId, String firstName, String lastName, String username, String password,
                        LocalDate birthday, String room, String picture) {
        this(userId, firstName, lastName, username, password, birthday, room, picture, new Contact(), new LinkedList<Patient>());
    }

    public MedicalStaff(String userId, String firstName, String lastName, String username, String password,
                        LocalDate birthday, String room, String picture, Contact contactInfo) {
        this(userId, firstName, lastName, username, password, birthday, room, picture, contactInfo, new LinkedList<Patient>());
    }

    public MedicalStaff(String userId, String firstName, String lastName, String username, String password,
                        LocalDate birthday, String room, String picture, Contact contactInfo, List<Patient> assignedPatients) {
        super(userId, firstName, lastName, username, password, birthday, room, picture, contactInfo);
        this.assignedPatients = assignedPatients;
    }

    public List<Patient> getAssignedPatients() {
        return assignedPatients;
    }

    public void setAssignedPatients(List<Patient> assignedPatients) {
        this.assignedPatients = assignedPatients;
    }

    public void addPatient(Patient p) {
        assignedPatients.add(p);
    }

    @Override
    public AbsUser fromXMLString() {
        return null;
    }

    @Override
    public AbsUser fromCSVString() {
        return null;
    }

    @Override
    public AbsUser fromTSVString() {
        return null;
    }

    @Override
    public AbsUser fromSVString(String delimiter) {
        return null;
    }

    public static String getRole(){
        return "MED_STAFF";
    }
}
