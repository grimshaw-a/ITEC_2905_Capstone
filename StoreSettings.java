import java.io.*;

public class StoreSettings {
	//Store information
	private String storeName;
	private String addressFirstLine;
	private String addressSecondLine;
	private String city;
	private String state;
	private String zipcode;
	private String phone;
	private String website;
	private String email;
	
	//Database variables
	private int nextSKUNumber;
	private int nextCustomerID;
	private int nextEmployeeID;
	private int nextIncidentID;
	private String adminPassword;
	private boolean passwordCorrect;
	
	//Getters and setters
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public String getAddressFirstLine() {
		return addressFirstLine;
	}
	public void setAddressFirstLine(String addressFirstLine) {
		this.addressFirstLine = addressFirstLine;
	}
	public String getAddressSecondLine() {
		return addressSecondLine;
	}
	public void setAddressSecondLine(String addressSecondLine) {
		this.addressSecondLine = addressSecondLine;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAdminPassword() {
		return adminPassword;
	}
	public void setAdminPassword(String password) {
		this.adminPassword = password;
	}
	public boolean getPasswordCorrect() {
		return passwordCorrect;
	}
	public void setPasswordCorrect(boolean password) {
		this.passwordCorrect = password;
	}
	public int getNextSKUNumber() {
		return nextSKUNumber;
	}
	public void setNextSKUNumber(int nextSKUNumber) {
		this.nextSKUNumber = nextSKUNumber;
	}
	public int getNextCustomerID() {
		return nextCustomerID;
	}
	public void setNextCustomerID(int nextCustomerID) {
		this.nextCustomerID = nextCustomerID;
	}
	public int getNextEmployeeID() {
		return nextEmployeeID;
	}
	public void setNextEmployeeID(int nextEmployeeID) {
		this.nextEmployeeID = nextEmployeeID;
	}
	public int getNextIncidentID() {
		return nextIncidentID;
	}
	public void setNextIncidentID(int nextIncidentID) {
		this.nextIncidentID = nextIncidentID;
	}
	
	//Additional methods
	public void advanceSKU() {
		this.nextSKUNumber++;
	}
	public void advanceCustomerID() {
		this.nextCustomerID++;
	}
	public void advanceEmployeeID() {
		this.nextEmployeeID++;
	}
	public void advanceIncidentID() {
		this.nextIncidentID++;
	}
	
	//Export and import file methods
	public void saveStoreSettingsToFile() throws IOException {
		try {
			//Create data output stream
			DataOutputStream output = new DataOutputStream(new FileOutputStream("StoreSettings.dat"));
			
			//Export store information
			output.writeUTF(getStoreName());
			output.writeUTF(getAddressFirstLine());
			output.writeUTF(getAddressSecondLine());
			output.writeUTF(getCity());
			output.writeUTF(getState());
			output.writeUTF(getZipcode());
			output.writeUTF(getPhone());
			output.writeUTF(getWebsite());
			output.writeUTF(getEmail());
			
			//Export next value for SKU numbers, customer IDs, etc.
			output.writeInt(getNextSKUNumber());
			output.writeInt(getNextCustomerID());
			output.writeInt(getNextEmployeeID());
			output.writeInt(getNextIncidentID());
			
			//Close output stream
			output.close();
			
		} catch (IOException ex) {
			System.out.println(ex);
		}
	}
	
	public void readStoreSettingsFromFile() throws IOException {
		try {
			//Create data input stream
			DataInputStream input = new DataInputStream(new FileInputStream("StoreSettings.dat"));
			
			//Set store settings to values read from file
			setStoreName(input.readUTF());
			setAddressFirstLine(input.readUTF());
			setAddressSecondLine(input.readUTF());
			setCity(input.readUTF());
			setState(input.readUTF());
			setZipcode(input.readUTF());
			setPhone(input.readUTF());
			setWebsite(input.readUTF());
			setEmail(input.readUTF());
			
			//Import next value for SKU numbers, customer IDs, etc.
			setNextSKUNumber(input.readInt());
			setNextCustomerID(input.readInt());
			setNextEmployeeID(input.readInt());
			setNextIncidentID(input.readInt());
			
			//Close input stream
			input.close();
			
		} catch (IOException ex) {
			System.out.println(ex);
		}
	}

}
