import java.util.ArrayList;
import java.util.Date;

/** The customer class contains personal and billing information for one customer. Each customer 
 * is given a unique number id. Methods should use this id to access customers instead of by name in case of duplicate names.*/
public class Customer implements java.io.Serializable {
	//Personal and billing information for customer
	private static final long serialVersionUID = 635927;
	private int id;
	private String firstName;
	private String lastName;
	private String phone;
	private String email;
	private String addressFirstLine;
	private String addressSecondLine;
	private String city;
	private String state;
	private String zipcode;
	private String creditCard;
	private String paypal;
	private ArrayList<Integer> transactionHistory = new ArrayList<>();
	private ArrayList<Reminder> reminders = new ArrayList<>();
	
	//Constructors
	public Customer() {
	}
	
	//Getters and setters	
	public int getID() {
		return this.id;
	}
	
	public void setID(int i) {
		this.id = i;
	}
	
	public String getFirstName() {
		return this.firstName;
	}
	
	public void setFirstName(String n) {
		this.firstName = n;
	}
	
	public String getLastName() {
		return this.lastName;
	}
	
	public void setLastName(String n) {
		this.lastName = n;
	}
	
	public String getPhone() {
		return this.phone;
	}
	
	public void setPhone(String p) {
		this.phone = p;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public void setEmail(String e) {
		this.email = e;
	}
	
	public String getAddressFirstLine() {
		return this.addressFirstLine;
	}
	
	public void setAddressFirstLine(String a) {
		this.addressFirstLine = a;
	}
	
	public String getAddressSecondLine() {
		return this.addressSecondLine;
	}
	
	public void setAddressSecondLine(String a) {
		this.addressSecondLine = a;
	}
	
	public String getCity() {
		return this.city;
	}
	
	public void setCity(String c) {
		this.city = c;
	}
	
	public String getState() {
		return this.state;
	}
	
	public void setState(String s) {
		this.state = s;
	}
	
	public String getZipcode() {
		return this.zipcode;
	}
	
	public void setZipcode(String z) {
		this.zipcode = z;
	}
	
	public String getCreditCard() {
		return this.creditCard;
	}
	
	public void setCreditCard(String c) {
		this.creditCard = c;
	}
	
	public String getPaypal() {
		return this.paypal;
	}
	
	public void setPaypal(String p) {
		this.paypal = p;
	}

	public ArrayList<Integer> getTransactionHistory() {
		return transactionHistory;
	}

	public void addTransactionToHistory(Integer transactionID) {
		this.transactionHistory.add(transactionID);
	}
	
	public ArrayList<Reminder> getReminders() {
		return reminders;
	}
	
	public void addReminder(Reminder reminder) {
		this.reminders.add(reminder);
	}
	
	public void removeReminder(int index) {
		this.reminders.remove(index);
	}

	// Methods
	/** Creates string with mailing address*/
	public String formatMailingAddress() {
		if(this.addressSecondLine == null ) {
			return (this.firstName + " " + this.lastName + '\n' + this.addressFirstLine + '\n' + this.city + ", " + this.state + " " + this.zipcode);
		} else {
			return (this.firstName + " " + this.lastName + '\n' + this.addressSecondLine + '\n' + this.city + ", " + this.state + " " + this.zipcode);
		}
	}
	
}

class Reminder {
	private Date date;
	private boolean isOnceOnly;
	private boolean repeatsAnnually;
	private String occasionType;
	private String recipientName;
	
	public Reminder() {
		
	}
}
