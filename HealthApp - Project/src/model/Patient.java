package model;

import utils.DBHandler;

import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class Patient extends AbsUser{

    List<Pet> pets;
    List<Meal> meals;
    List<AbsRelation> relations;
    List<MedicalStaff> assignedStaff;
    HealthProfile healthProfile;

    public Patient() {
        this("enter id", "enter first name", "enter last name", "enter username", "enter password", LocalDate.now(), "enter room",
                "enter picture", new Contact());
    }

    public Patient(String userId, String firstName, String lastName, String username, String password, LocalDate birthday,
                   String room, String picture) {
        this(userId, firstName, lastName, username, password, birthday, room, picture, new Contact(), new LinkedList<Pet>(),
                new LinkedList<Meal>(), new LinkedList<AbsRelation>(), new LinkedList<MedicalStaff>(), new HealthProfile());
    }

    public Patient(String userId, String firstName, String lastName, String username, String password, LocalDate birthday,
                   String room, String picture, Contact contactInfo) {
        this(userId, firstName, lastName, username, password, birthday, room, picture, contactInfo, new LinkedList<Pet>(),
                new LinkedList<Meal>(), new LinkedList<AbsRelation>(), new LinkedList<MedicalStaff>(), new HealthProfile());
    }

    public Patient(AbsUser user, List<Pet> pets, List<Meal> meals, List<AbsRelation> relations,
                   List<MedicalStaff> assignedStaff, HealthProfile healthProfile) {
        this(user.getUserId(), user.getFirstName(), user.getLastName(), user.getUsername(), user.getPassword(),
                user.getBirthday(), user.getRoom(), user.getPicture(), user.getContactInfo(), pets, meals, relations, assignedStaff, healthProfile);
    }

    public Patient(String userId, String firstName, String lastName, String username, String password, LocalDate birthday,
                   String room, String picture, Contact contactInfo, List<Pet> pets, List<Meal> meals, List<AbsRelation> relations,
                   List<MedicalStaff> assignedStaff, HealthProfile healthProfile) {
        super(userId, firstName, lastName, username, password, birthday, room, picture, contactInfo);
        this.pets = pets;
        this.meals = meals;
        this.relations = relations;
        this.assignedStaff = assignedStaff;
        this.healthProfile = healthProfile;
    }

    public HealthProfile getHealthProfile() {
        return healthProfile;
    }

    public void setHealthProfile(HealthProfile healthProfile) {
        this.healthProfile = healthProfile;
    }

    public List<MedicalStaff> getAssignedStaff() {
        return assignedStaff;
    }

    public void setAssignedStaff(List<MedicalStaff> assignedStaff) {
        for (MedicalStaff m : assignedStaff)
            addAssignedStaff(m);
    }

    public void addAssignedStaff(MedicalStaff m) {
        this.assignedStaff.add(m);
        m.addPatient(this);
    }

    public List<AbsRelation> getRelations() {
        return relations;
    }

    public void setRelations(List<AbsRelation> relations) {
        for (AbsRelation rel : relations)
            addRelation(rel);
    }

    public void addRelation(AbsRelation relation) {
        this.relations.add(relation);
        relation.setUserId(userId);
    }

    public List<Pet> getPets() {
        return pets;
    }

    public void setPets(List<Pet> pets) {
        for (Pet p : pets)
            addPet(p);
    }

    public void addPet(Pet p) {
        this.pets.add(p);
        //TODO: p.setUserId(userId);
    }

    public List<Meal> getMeals() {
        return meals;
    }

    public void setMeals(List<Meal> meals) {
        for (Meal m : meals)
            addMeal(m);
    }

    public void addMeal(Meal m) {
        this.meals.add(m);
    }

    public boolean getIsNewPatient() {
        return userId.equals("enter id") && username.equals("enter username");
    }

    public boolean savePatient() {
        return DBHandler.getUniqueInstance().insertPatient(this);
    }

    public boolean updatePatient() {
        return DBHandler.getUniqueInstance().updatePatient(this);
    }


    public static String getUserType(){
        return "PATIENT";
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

}
