import java.util.ArrayList;
import java.util.Collection;

/**
 * <p>The SKU class is designed to represent both a type of item that a store might sell and a 
 * specific item at checkout. For instance, a SKU is used on the inventory page to
 * represent Red Roses. It stores data about how many are in stock, what the retail price is,
 * etc. Alternately, a SKU is used in checkout to represent the specific roses purchased by
 * the customer: how many were purchased, what color, the retail price, etc.</p>
 * @author Adam Grimshaw<br>
 * Date: 10/14/2020<br>
 * Course: ITEC 2905, Capstone Project, Southwest Technical College 
 */
public class SKU implements java.io.Serializable {
	//Serializable id (DO NOT CHANGE THIS NUMBER. THE DATABASE WILL BECOME UNREADABLE.)
	private static final long serialVersionUID = 419910;
	
	//Properties
	private String id; // SKU numbers are 5 digits long.
	private String name;
	private double wholesaleCost;
	private double retailPrice;
	private int shelfLifeInDays;
	private int unitsInStock;
	private int quantitySold;
	private ArrayList<String> colors = new ArrayList<>();
	
	//Constructors
	public SKU() {
	}
	
	public SKU(String id) {
		this.id = id;
	}
	
	public SKU(String id, String name, double wholesaleCost, double retailPrice, int shelfLife) {
		this.id = id;
		this.name = name;
		this.wholesaleCost = wholesaleCost;
		this.retailPrice = retailPrice;
		this.shelfLifeInDays = shelfLife;
	}
	
	//Getters and setters
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public double getWholesaleCost() {
		return wholesaleCost;
	}
	
	public void setWholesaleCost(double wholesaleCost) {
		this.wholesaleCost = wholesaleCost;
	}
	
	public double getRetailPrice() {
		return retailPrice;
	}
	
	public void setRetailPrice(double retailPrice) {
		this.retailPrice = retailPrice;
	}
	
	public int getShelfLifeInDays() {
		return shelfLifeInDays;
	}
	
	public void setShelfLifeInDays(int shelfLifeInDays) {
		this.shelfLifeInDays = shelfLifeInDays;
	}
	
	public int getUnitsInStock() {
		return unitsInStock;
	}
	
	public void setUnitsInStock(int numberInStock) {
		this.unitsInStock = numberInStock;
	}
	
	public int getQuantitySold() {
		return quantitySold;
	}
	
	public void setQuantitySold(int quantitySold) {
		this.quantitySold = quantitySold;
	}
	
	public ArrayList<String> getColors() {
		return colors;
	}
	
	public void addColor(String color) {
		colors.add(color);
	}
	
	public void setColors(Collection<String> colorList) {
		colors.clear();
		colors.addAll(colorList);
	}
	
	//Additional methods
	/**
	 * This method is intended to be used at checkout to update the number units in inventory by
	 * subtracting the number of units purchased by a customer in a sales transaction.
	 * @param quantity An integer representing the number of units to subtract from inventory.
	 */
	public void reduceUnitsInStock(int quantity) {
		this.unitsInStock -= quantity;
	}	
}