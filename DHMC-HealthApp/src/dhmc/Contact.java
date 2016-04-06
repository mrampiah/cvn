package dhmc;
import java.util.LinkedList;

class Contact {
	private int id;
	private LinkedList<String> phone;
	private LinkedList<String> email;
	private LinkedList<String> address;

	public Contact(int id) {
		this.id = id;
		this.phone = new LinkedList<String>();
		this.email = new LinkedList<String>();
		this.address = new LinkedList<String>();
	}
	
	public int getID() {
		return this.id;
	}
	
	public LinkedList<String> getPhone() {
		return this.phone;
	}
	
	public LinkedList<String> getEmail() {
		return this.email;
	}
	
	public LinkedList<String> getAddress() {
		return this.address;
	}
	
	public String getPrimaryPhone() {
		return this.phone.getFirst();
	}
	
	public String getPrimaryEmail() {
		return this.email.getFirst();
	}
	
	public String getPrimaryAddress() {
		return this.address.getFirst();
	}
	
	public void addPhone(String phone) {
		if (this.phone.indexOf(phone) < 0) {
			this.phone.add(phone);
		}
	}
	
	public void addEmail(String email) {
		if (this.email.indexOf(email) < 0) {
			this.email.add(email);
		}
	}
	
	public void addAddress(String address) {
		if (this.address.indexOf(address) < 0) {
			this.address.add(address);
		}
	}
	
	public void removePhone(String phone) {
		if (this.phone.indexOf(phone) >= 0) {
			this.phone.remove(phone);
		}
	}
	
	public void removeEmail(String email) {
		if (this.email.indexOf(email) >= 0) {
			this.email.remove(email);
		}
	}
	
	public void removeAddress(String address) {
		if (this.address.indexOf(address) >= 0) {
			this.address.remove(address);
		}
	}
	
	public void makePrimaryPhone(String phone) {
		this.removePhone(phone);
		this.phone.addFirst(phone);
	}
	
	public void makePrimaryEmail(String email) {
		this.removeEmail(email);
		this.email.addFirst(email);
	}
	
	public void makePrimaryAddress(String address) {
		this.removeAddress(address);
		this.address.addFirst(address);
	}

}
