# ITEC_2905_Capstone
<p>Programming Language: Java </p>
<p>Date: October 16, 2020 </p>
<p>Student: Adam Grimshaw </p>
<p>School: Southwest Technical College </p>

### Repository Description

This repository contains my Capstone Project for the Software Development certificate program at Southwest Technical College. This project is the culmination of what I've learned over the past year in my Java progamming classes. I designed an all-in-one software to help manage the day to day operations of a floral shop. It features a GUI built in JavaFX, and allows the user to manage inventory, keep a database of customer, complete sales transactions, and schedule deliveries.

### Personal Bio

- Software Development student at Southwest Technical College
- Commercial Photographer 2010-2020
  - Specialty in product and interiors. High level post-production.

### Program Goals

Learn the basics of programming and be prepared to enter the workforce in this industry.

# Floral Shop Software

## How to Run
The main method is located in CashierStationGUI. Launch the program from there. Additional classes, found in seperate Java files, are needed for the interface to run including: Customer, SKU, Transaction, and StoreSettings. 

As the primary function of this program is to collect, save, and organize information, it should be noted that all information entered into the program is exported to one of four .dat files. These files preserve the retailer's customer list, inventory, and transaction history. If these files are missing, the program will launch as a blank slate and will export new files upon closing. 

## Code Example
This software is designed to manage both checkout functionality and administrative tasks. As such, it made sense to password protect certain features. For this function, I wanted to create a simple module that I could use and reuse in every situation requiring a password to be entered. 

My solution was to create a method, passwordVerificationWindow(), that launches a new window prompting the user to enter the password. When this window launches, it puts the main window on pause using the initModality() and showAndWait() methods, and only after the password window has been closed, will the main window resume running. 

The passwordVerificationWindow() method returns a boolean value. To implement effectively, the method needs to be used as the conditional in an if/else statement. If the password is entered correctly, the method returns true. Otherwise, it returns false. Thus, any feature can easily be password protected by nesting it within a simple conditional statement.

```
public boolean passwordVerificationWindow(String message) {
		//Create layout elements
		PasswordField passwordField = new PasswordField();
		passwordField.setMaxWidth(160);
		Button btnCancel = new Button("Cancel");
		Button btnSubmit = new Button("Submit");
		Label warningLabel = new Label();
		warningLabel.setTextFill(Color.RED);
		HBox buttons = new HBox(20, btnCancel, btnSubmit);
		VBox rows = new VBox(20, new Label (message),new Label("Enter password to proceed: "), passwordField, buttons, warningLabel);
		StackPane pane = new StackPane(rows);
		pane.setPrefSize(460, 260);
		rows.setAlignment(Pos.CENTER);
		buttons.setAlignment(Pos.CENTER);
		
		//Load scene
		Stage passwordStage = new Stage();
		passwordStage.initModality(Modality.APPLICATION_MODAL);
		Scene scene = new Scene(pane);
		passwordStage.setTitle("Admin");
		passwordStage.setScene(scene);
		
		//Make buttons functional
		btnCancel.setOnAction((e) -> {
			myStore.setPasswordCorrect(false);
			passwordStage.close();
		});
		
		btnSubmit.setOnAction((e) -> {
			if(passwordField.getText().equals(myStore.getAdminPassword())) {
				System.out.println("Password is correct.");
				passwordStage.close();
				myStore.setPasswordCorrect(true);
			} else {
				System.out.println("Incorrect password.");
				warningLabel.setText("*Incorrect password");
				passwordField.setText("");
			}
		});
		
		passwordStage.showAndWait();
		return myStore.getPasswordCorrect();	
	}		
```

## Tests
The formatMailingAddress() method gets the contact information for a customer from a Customer object and formats it according to the standard U.S. convention for mailing addresses. The method first determines if the address requires 3 or 4 lines, and then concatenates a String with appropriate line feeds and returns it.

```
@Test
	public void testFormatMailingAddress() {
		Customer customer1 = new Customer();
		customer1.setFirstName("Sherry");
		customer1.setLastName("Higgins");
		customer1.setAddressFirstLine("123 Alphabet Street");
		customer1.setAddressSecondLine("Suite 3C");
		customer1.setCity("Generic Town");
		customer1.setState("SC");
		customer1.setZipcode("09573");
		
		Customer customer2 = new Customer();
		customer2.setFirstName("David");
		customer2.setLastName("Anderson");
		customer2.setAddressFirstLine("747 Flyer Street");
		customer2.setCity("Another City");
		customer2.setState("NC");
		customer2.setZipcode("10382");
		
		assertEquals(customer1.formatMailingAddress(), "Sherry Higgins\n123 Alphabet Street\nSuite 3C\nGeneric Town, SC 09573");
		assertEquals(customer2.formatMailingAddress(), "David Anderson\n747 Flyer Street\nAnother City, NC 10382");
	}
```
