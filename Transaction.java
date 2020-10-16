import java.util.Date;
import java.util.ArrayList;
import java.time.LocalDateTime;

/**
 * <p>A Transaction object stores information about a sales transaction including the
 * time and date of sale, the customer, the total price of sale, a list of items purchased,
 * and information about delivery.</p>
 * @author Adam Grimshaw<br>
 * Date: 10/14/2020<br>
 * Course: ITEC 2905, Capstone Project, Southwest Technical College 
 */
public class Transaction implements java.io.Serializable {
	//Serializable id (DO NOT CHANGE THIS NUMBER. THE DATABASE WILL BECOME UNREADABLE.)
	private static final long serialVersionUID = 3295596100494398304L;
	
	//Properties
	private long id;
	private int register;
	private int employeeID;
	private int customerID;
	private ArrayList<SKU> items = new ArrayList<>();
	private double tax;
	private double totalSale;
	private String notes;
	private boolean isCanceled;
	private boolean isReturned;
	private String receiptText = "";
	
	//Properties related to deliveries
	private boolean isDelivery;
	private int hasBeenDelivered; // 1 = has not yet been delivered, 0 = was delivered successfully, -1 = could not be delivered
	private LocalDateTime deliveryTime;
	private String recipientFirstName;
	private String recipientLastName;
	private String recipientAddress1;
	private String recipientAddress2 = "";
	private String recipientCity;
	private String recipientState;
	private String recipientZipcode;
	private String recipientPhone;
	private String deliveryMessage;
	
	//Constructors
	public Transaction() {
		id = new Date().getTime();
	}
	
	public Transaction(int customerID, ArrayList<SKU> items) {
		this.id = new Date().getTime();
		this.customerID = customerID;
		this.items.addAll(items);
	}

	//Getters and setters
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getRegister() {
		return register;
	}

	public void setRegister(int register) {
		this.register = register;
	}

	public int getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(int employeeID) {
		this.employeeID = employeeID;
	}

	public int getCustomerID() {
		return customerID;
	}

	public void setCustomerID(int customerID) {
		this.customerID = customerID;
	}

	public ArrayList<SKU> getItems() {
		return items;
	}

	public void setItems(ArrayList<SKU> items) {
		this.items = items;
	}
	
	public double getTax() {
		return tax;
	}

	public void setTax(double tax) {
		this.tax = tax;
	}
	
	public double getTotalSale() {
		return totalSale;
	}

	public void setTotalSale(double totalSale) {
		this.totalSale = totalSale;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public boolean isCanceled() {
		return isCanceled;
	}

	public void setCanceled(boolean isCanceled) {
		this.isCanceled = isCanceled;
	}

	public boolean isReturned() {
		return isReturned;
	}

	public void setReturned(boolean isReturned) {
		this.isReturned = isReturned;
	}
	
	public String getReceiptText() {
		return receiptText;
	}

	public void setReceiptText(String text) {
		this.receiptText = text;
	}

	public long getSerialVersionUID() {
		return serialVersionUID;
	}
	
	//Getters and setters for delivery properties
	public boolean isDelivery() {
		return isDelivery;
	}

	public void setDelivery(boolean isDelivery) {
		this.isDelivery = isDelivery;
	}
	
	public int getHasBeenDelivered() {
		return hasBeenDelivered;
	}

	public void setHasBeenDelivered(int hasBeenDelivered) {
		this.hasBeenDelivered = hasBeenDelivered;
	}

	public LocalDateTime getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(LocalDateTime deliveryTime) {
		this.deliveryTime = deliveryTime;
	}
	
	public String getRecipientFirstName() {
		return recipientFirstName;
	}

	public void setRecipientFirstName(String recipientFirstName) {
		this.recipientFirstName = recipientFirstName;
	}

	public String getRecipientLastName() {
		return recipientLastName;
	}

	public void setRecipientLastName(String recipientLastName) {
		this.recipientLastName = recipientLastName;
	}

	public String getRecipientAddress1() {
		return recipientAddress1;
	}

	public void setRecipientAddress1(String recipientAddress1) {
		this.recipientAddress1 = recipientAddress1;
	}

	public String getRecipientAddress2() {
		return recipientAddress2;
	}

	public void setRecipientAddress2(String recipientAddress2) {
		this.recipientAddress2 = recipientAddress2;
	}

	public String getRecipientCity() {
		return recipientCity;
	}

	public void setRecipientCity(String recipientCity) {
		this.recipientCity = recipientCity;
	}

	public String getRecipientState() {
		return recipientState;
	}

	public void setRecipientState(String recipientState) {
		this.recipientState = recipientState;
	}

	public String getRecipientZipcode() {
		return recipientZipcode;
	}

	public void setRecipientZipcode(String recipientZipcode) {
		this.recipientZipcode = recipientZipcode;
	}

	public String getRecipientPhone() {
		return recipientPhone;
	}

	public void setRecipientPhone(String recipientPhone) {
		this.recipientPhone = recipientPhone;
	}
	
	public String getDeliveryMessage() {
		return deliveryMessage;
	}

	public void setDeliveryMessage(String deliveryMessage) {
		this.deliveryMessage = deliveryMessage;
	}
	
	//Other methods
	/**
	 * This method formats an address for delivery.
	 * @return Returns a String formatted as a street address, taking into consideration not all addresses have a second line.
	 */
	public String formatDeliveryAddress() {
		if(this.recipientAddress2.equals("")) {
			return (this.recipientFirstName + " " + this.recipientLastName + '\n' + this.recipientAddress1 + '\n' + this.recipientCity + ", " + this.recipientState + " " + this.recipientZipcode);
		} else {
			return (this.recipientFirstName + " " + this.recipientLastName + '\n' + this.recipientAddress1 + '\n' + this.recipientAddress2 + '\n' + this.recipientCity + ", " + this.recipientState + " " + this.recipientZipcode);
		}
	}
	
	public void processReturn() {
	}

	public void getTransactionHistory() {
		// user defines start and end time
	}
	
	public void cancelTransaction() {
	}
	
	/**
	 * This method helps create a text receipt for a sales transaction by concatenating additional
	 * text to the existing text.
	 * @param additionalText A String that will be concatenated to the existing receipt text.
	 */
	public void appendToReceiptText(String additionalText) {
		this.receiptText = this.receiptText + additionalText;
	}
	
	@Override
	public String toString() {
		return (String.format(this.getReceiptText()));
	}
}