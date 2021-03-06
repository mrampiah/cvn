package controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Random;

import model.Caregiver;
import model.Contact;
import model.MainApp;
import model.Meal;
import model.MedicalStaff;
import model.Pet;


public class RandomGenerator {

	static String[] firstNames = { "AARON", "ABDUL", "ABE", "ABEL", "ABRAHAM", "ABRAM", "ADALBERTO", "ADAM", "BARRY",
			"BART", "BARTON", "BASIL", "BEAU", "BEN", "BENEDICT", "BRANDEN", "BRANDON", "BRANT", "BRENDAN", "CEDRIC",
			"CEDRICK", "CESAR", "CHAD", "CHADWICK", "CORTEZ", "CORY", "DEREK", "DERICK", "DONNIE", "DOUGLASS", "DOYLE",
			"FRITZ", "GABRIEL", "GAIL", "GENARO", "GENE", "GEOFFREY", "GEORGE", "Henry", "HERB", "HERBERT", "HERIBERTO",
			"HERMAN", "HERSCHEL", "IRVING", "IRWIN", "ISAAC", "ISAIAH", "JEAN", "JED", "JEFFEREY", "JEFFERSON",
			"JEFFERY", "JESS", "JESS", "MOHAMED", "NED", "NEIL", "NOEL", "NOLAN", "NORBERT", "NORBERTO", "NORMAN",
			"NORMAND", "NORRIS", "ODELL", "ODIS", "OLIN", "OLIVER", "ORVILLE", "OSCAR", "OSVALDO", "OSWALDO", "RANDELL",
			"RANDOLPH", "RANDY", "RAPHAEL", "RASHAD", "WYATT", "XAVIER", "YONG", "YOUNG", "ZACHARIAH", "ZACHARY",
			"ZACHERY", "ZACK", "ZACKARY", "ZANE" };

	static String[] lastNames = { "SMITH", "JOHNSON", "MARTINEZ", "THOMAS", "JACKSON", "LEE", "WALKER", "PEREZ", "HALL",
			"YOUNG", "ALLEN", "SANCHEZ", "WRIGHT", "KING", "SCOTT", "GREEN", "BAKER", "CAMPBELL", "MITCHELL", "ROBERTS",
			"CARTER", "PHILLIPS", "EVANS", "TURNER", "WARD", "COX", "DIAZ", "RICHARDSON" };

	static String[] phoneNumbers = { "(741) 951-5271", "(561) 742-3921", "(784) 287-1076", "(838) 727-6573",
			"(500) 244-7083", "(943) 570-2414", "(874) 381-4941", "(367) 226-2040", "(815) 825-6662" };

	static String[] emails = { "isi@tidur.org", "johu@codovo.org", "uj@orkimfah.com", "fapet@go.edu", "me@udaga.net",
			"kenna@vecbu.com", "gunob@moahu.net", "nashulu@it.gov", "opwankiw@seiteevo.io", "tifinpim@za.io",
			"nupedu@ta.edu", "juh@antof.com", "mewso@necuwnuk.io", "fuivif@daunen.gov", "hohzavi@paw.edu",
			"motjeni@muvu.io", "setosuf@oti.org", "lad@kupez.gov", "uggako@filse.net", "pivop@wewjur.com" };

	static String[] petTypes = {"Dog", "Cat", "Fish"};

	static String[] petNames = {"Bob", "Max", "Lucy", "Buddy", "Rocky"};

	static String[] foodNames = {"Pizza", "Mac & Cheese", "Wings", "Jollof"};
	
	static String[] relation = {"Mother", "Father", "Son", "Daughter", "Cousin", "Uncle", "Aunt"};
	
	static Boolean[] bool = { true, false};
	
	static Random randomNumber = getRandomNumber();

	public static Random getRandomNumber() {
		return new Random(System.currentTimeMillis());
	}

	public static String getRandomFirstName() {
		return firstNames[randomNumber.nextInt(firstNames.length)];
	}

	public static String getRandomLastName() {
		return lastNames[randomNumber.nextInt(lastNames.length)];
	}

	public static String getRandomPhoneNumber() {
		return phoneNumbers[randomNumber.nextInt(phoneNumbers.length)];
	}

	public static String getRandomEmail() {
		return emails[randomNumber.nextInt(emails.length)];
	}

	public static String getRandomAddress() {
		return "100 Institute Rd\nWorcester MA, 01609\n";
	}

	public static String createUsername(String firstname, String lastname) {
		return lastname.toLowerCase() + firstname.toLowerCase().charAt(0);
	}

	public static String getRandomUsername() {
		return lastNames[randomNumber.nextInt(lastNames.length)].toLowerCase()
				+ firstNames[randomNumber.nextInt(firstNames.length)].toLowerCase().charAt(0);
	}

	public static LocalDate getRandomBirthday() {
		String input = String.format("%4d-%02d-%02d", (randomNumber.nextInt(80) + 1930), randomNumber.nextInt(12) + 1,
				randomNumber.nextInt(27) + 1);
		DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
//		return new Date((randomNumber.nextInt(80) + 1930), randomNumber.nextInt(12) + 1,  randomNumber.nextInt(27) + 1);
		return LocalDate.parse(input, formatter);
	}

	public static String getRandomRoom() {
		return "Room " + (randomNumber.nextInt(20) + 1);
	}

	public static String getRandomRelation(){
		return relation[randomNumber.nextInt(relation.length)];
	}
	
	public static Boolean getRandomBoolean(){
		return bool[randomNumber.nextInt(bool.length)];
	}
	
	public static Contact getRandomContactInfo() {
		String[] types = { "personal", "work", "home" };
		LinkedList<String> phoneNumbers = new LinkedList<String>();
		LinkedList<String> emails = new LinkedList<String>();
		LinkedList<String> addresses = new LinkedList<String>();
		
		// add phone numbers
		for (String type : types) {
			phoneNumbers.add(getRandomPhoneNumber());
		}

		// add email addresses
		for (String type : types) {
			emails.add(getRandomEmail());
		}

		// add addresses
		for (String type : types) {
			addresses.add(getRandomAddress());
		}
		Contact contact = new Contact();
		contact.addPhoneList(phoneNumbers);
		contact.addEmailList(emails);
		contact.addAddressList(addresses);
		return contact;

	}
	
	public static Pet getRandomPet(){
		return new Pet(petNames[randomNumber.nextInt(petNames.length)], 
				petTypes[randomNumber.nextInt(petTypes.length)], 
				randomNumber.nextInt(10) < 5 ? true : false );
	}

	public static Meal getRandomMeal(){
		return new Meal(foodNames[randomNumber.nextInt(foodNames.length)],
				randomNumber.nextInt(10) + 1, randomNumber.nextInt(10), "" );
	}
	
	public static Caregiver getRandomCaregiver(){
        String firstname = getRandomFirstName();
        String lastname = getRandomLastName();
        String relation = getRandomRelation();
        String username = createUsername(firstname, lastname);
        LocalDate birthday = getRandomBirthday();
        Boolean isFamily = getRandomBoolean();
        String room = getRandomRoom();
        Contact contactInfo = getRandomContactInfo();
        return new Caregiver( firstname, birthday.toString(), relation, contactInfo, isFamily);
    }
	
	public static MedicalStaff getRandomMedicalStaff(){
        String firstname = getRandomFirstName();
        String lastname = getRandomLastName();
        String userId = MainApp.getDatabaseHandler().generateUserID(firstname, lastname, "Medical Staff");
        
        MedicalStaff med = new MedicalStaff(firstname, lastname, "nurse", userId, (int)getRandomID());
        med.setUserID(userId);
        return med;
	}
	
	public static long getRandomID(){
		return System.currentTimeMillis();
	}


}
