package model;

import java.time.LocalDate;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Caregiver {
	private StringProperty name;
	private StringProperty birthday;
	private StringProperty relation;
	private Contact contactInfo;
	private BooleanProperty inFamily;
	private int caregiverID;
	
	public Caregiver (String name, String birthday, String relation, Contact contactInfo, boolean isFamily){
		this.name = new SimpleStringProperty(name);
		this.birthday = new SimpleStringProperty(name);
		this.relation = new SimpleStringProperty(relation);
		this.contactInfo = contactInfo;
		this.inFamily = new SimpleBooleanProperty(isFamily);	
	}
	
	public Caregiver() {
		this.name.set("");
		this.birthday.set(LocalDate.now().toString());
		this.relation.set("");
		this.contactInfo = new Contact();
		this.inFamily.set(true);
	}
	
	public Contact getContactInfo() {
		return contactInfo;
	}

	public int getCaregiverID() {
		return caregiverID;
	}

	public void setCaregiverID(int caregiverID) {
		this.caregiverID = caregiverID;
	}

	public StringProperty nameProperty() {
		return name;
	}
	
    public final String getName() {
        return nameProperty().get();
    }

    public final void setName(String name) {
        nameProperty().set(name);
    }
    
	public StringProperty birthdayProperty() {
		return birthday;
	}
	
    public final String getBirthday() {
        return birthdayProperty().get();
    }

    public final void setBirthday(String birthday) {
        birthdayProperty().set(birthday);
    }
    
	public StringProperty relationProperty() {
		return relation;
	}
	
    public final String getRelation() {
        return relationProperty().get();
    }

    public final void setRelation(String relation) {
        relationProperty().set(relation);
    }
	
	public BooleanProperty inFamilyProperty() {
		return inFamily;
	}
	
    public final boolean getInFamily() {
        return inFamilyProperty().get();
    }

    public final void setInFamily(boolean inFamily) {
        inFamilyProperty().set(inFamily);
    }

}

