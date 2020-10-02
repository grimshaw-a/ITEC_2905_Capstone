import java.util.Date;
import java.util.ArrayList;

/** This class contains information for one employee.*/
public class Employee {
	private int id; // Employee IDs are six digits long.
	private String firstName;
	private String lastName;
	private int phone;
	private String email;
	private String addressFirstLine;
	private String addressSecondLine;
	private String city;
	private String state;
	private int zipcode;
	private Date dateHired;
	private Date dateTerminated;
	private String position;
	private double hourlyWage;
	private ArrayList<Incident> incidents = new ArrayList<>();
	
	//Constructors
	public Employee(int id) {
		this.id = id;
	}
	
	public Employee(int id, String firstName, String lastName) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	// Getters and setters
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public int getPhone() {
		return phone;
	}
	public void setPhone(int phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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
	public int getZipcode() {
		return zipcode;
	}
	public void setZipcode(int zipcode) {
		this.zipcode = zipcode;
	}
	public Date getDateHired() {
		return dateHired;
	}
	public void setDateHired(Date dateHired) {
		this.dateHired = dateHired;
	}
	public Date getDateTerminated() {
		return dateTerminated;
	}
	public void setDateTerminated(Date dateTerminated) {
		this.dateTerminated = dateTerminated;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public double getHourlyWage() {
		return hourlyWage;
	}
	public void setHourlyWage(double hourlyWage) {
		this.hourlyWage = hourlyWage;
	}
	
	// Other methods
	/** Creates a new Incident object stored in this employees history. For use in making notes about this employee.*/
	public void reportIncident(Date time, String notes) {
		Incident incident = new Incident(time, notes);
		incidents.add(incident);
	}
	
	// create method to view incidents

}

class Incident {
	private Date date;
	private String description;
	
	public Incident() {
		
	}
	
	public Incident(Date date, String notes) {
		this.date = date;
		this.description = notes;
	}
	
	// Getters and setters
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

}