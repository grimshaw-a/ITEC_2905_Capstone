import java.util.Date;
import java.util.ArrayList;

public class TransactionControls {
	
	public void newTransaction() {
		// note date and time
		// note register or place of sale
		// note employee completing transaction
		// note name of customer
		// prompt to enter customer into database
		// note each product sold and update inventory
		// sum prices and tax, give total
		// note payment type
		// allow space for additional notes
	}
	
	public void processReturn() {
		
	}
	
	public void getTransactionHistory() {
		// user defines start and end time
	}
	
	public void cancelTransaction() {
		
	}

}

class Transaction {
	
	private int id; // Transaction numbers are 12 digits long and generated based on time of transaction.
	private Date time;
	private int register;
	private int employeeID;
	private int customerID;
	private ArrayList<SKU> items = new ArrayList<>();
	private String notes;
	private boolean isCancled;
	private boolean isReturned;
	

}