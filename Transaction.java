import java.util.Date;
import java.util.ArrayList;

public class Transaction implements java.io.Serializable {
	
	private static final long serialVersionUID = 3295596100494398304L;
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
	
	//Other methods
	public void processReturn() {
		
	}
	
	public void getTransactionHistory() {
		// user defines start and end time
	}
	
	public void cancelTransaction() {
		
	}
	
	public void appendToReceiptText(String additionalText) {
		this.receiptText = this.receiptText + additionalText;
	}
	
	@Override
	public String toString() {
		return (String.format(this.getReceiptText()));
	}

}