import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TextFormatter.Change;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;
import java.util.*;
import java.util.function.*;
import javafx.collections.*;
import java.text.NumberFormat;
import java.text.DecimalFormat;

/**
 * <p>This program is intended for use at a checkout register in a floral shop. It is designed
 * to track inventory, manage a database of customers, and complete sales transactions. It 
 * implements several additional custom classes including Customer, SKU, StoreSettings, and 
 * Transaction.</p> <p>This class contains the main method, and is the single launch point
 * for this software.</p>
 * @author Adam Grimshaw<br>
 * Date: 10/14/2020<br>
 * Course: ITEC 2905, Capstone Project, Southwest Technical College 
 */
public class CashierStationGUI extends Application{
	//Screen size
	final int SCREEN_WIDTH = 800;
	final int SCREEN_HEIGHT = 600;
	
	//Colors to select from in SKU edit
	private List<String> colorList = Arrays.asList("Red", "Orange", "Yellow", "Green", "Blue", "Purple", "White", "Peach", "Cream", "Lavendar", "Periwinkle", "Pink", "Sea Foam", "Magenta", "Pistachio");
	ObservableList<String> color = FXCollections.observableArrayList(colorList);
	
	//Contains store specific information
	StoreSettings myStore = new StoreSettings();
	
	//Create empty array for holding customer database
	ArrayList<Customer> customers = new ArrayList<>();
	ObservableList<Customer> data = FXCollections.observableArrayList();
	
	//Create empty array to hold inventory information
	ArrayList<SKU> inventory = new ArrayList<>();
	ObservableList<SKU> inventoryData = FXCollections.observableArrayList();
	
	//Create empty map to hold transaction history
	HashMap<Long, Transaction> transactionHistory = new HashMap<>();
	
	/**
	 * The start method first imports all store specific settings from external .dat files, including
	 * store settings, customer database, inventory information, and transaction history. Next, it
	 * calls a method to send out reminder emails. Finally, it loads the home screen of the GUI.
	 */
	@Override
	public void start(Stage primaryStage) {
		//Import store settings, inventory data, customer data, and transactions
		try {
			myStore.readStoreSettingsFromFile();
			customers = readCustomerListFromFile();
			inventory = readInventoryFromFile();
			transactionHistory = readTransactionHistoryFromFile();
		} catch (Exception ex) { 
			System.out.println(ex);
		}
		data = FXCollections.observableArrayList(customers);
		inventoryData = FXCollections.observableArrayList(inventory);
		
		//Send out email reminders
		sendOutReminderEmails();

		//Load home screen
		loadHomeScreen(primaryStage);
	}
	
	/**
	 * The stop method is called when the window is closed. All store information, including inventory
	 * customer database, and transactions are exported to external files.
	 */
	@Override
	public  void stop() {
		try {
			myStore.saveStoreSettingsToFile();
			exportInventoryToFile(inventory);
			writeCustomerListToFile(customers);
			writeTransactionHistoryToFile(transactionHistory);
			System.out.println("Data exported successfully.");
		} catch (IOException ex) {
			System.out.println("Data export failed.");
		}
		System.out.println("Stage is closing.");
	}
	
	/**
	 * The main method. It immediately calls the start method.
	 * @param args Not used.
	 */
	public static void main(String[] args) {
		Application.launch(args);
	}
	
	/**
	 * This feature has not been fully developed yet. The idea is that customers can request reminders
	 * for upcoming or annual events. Currently, this feature combs the customer database and searches for
	 * reminders scheduled for the current month. It is called in the start method. In a fully functional 
	 * version, this method would send out a reminder email only once, two weeks before the date. And emails
	 * would be custom made to feature products that would be appealing to the specific customer. As for now, 
	 * the current method stands in as a place holder.
	 */
	public void sendOutReminderEmails() {
		LocalDateTime today = LocalDateTime.now();
		for(int i = 0; i < customers.size(); i++) {
			if(customers.get(i).getReminders().size() > 0) {
				for(int j = 0; j < customers.get(i).getReminders().size(); j++) {
					if(today.getMonth() == customers.get(i).getReminders().get(j).getDate().getMonth()) {
						System.out.println("Email reminder sent to " + customers.get(i).getFirstName() + " " + customers.get(i).getLastName() + " regarding upcoming " + customers.get(i).getReminders().get(j).getOccasionType() + " event on " + customers.get(i).getReminders().get(j).getDate() + ".");
					}
				}
			}
		}
		System.out.println();
	}
	
	/**
	 * This method acts as a gate keeper to restrict access to certain functions. When called, it
	 * launches a new window and freezes action on the main window. If the password is entered
	 * correctly, the window closes and the main window proceeds to call the requested method.
	 * Otherwise, when the window is closed, the main window is restored to full functionality, but
	 * the requested method is not called. To implement this method, use this as the conditional in
	 * an "if" statement. If this method returns true (password is entered correctly), then do x.
	 * Else (if the correct password is not entered), do nothing.
	 * @param message A custom message displayed in the new window. Describes what will happen if 
	 * the password is entered correctly.
	 * @return Returns a boolean.
	 */
	public boolean passwordVerificationWindow(String message) {
		myStore.setAdminPassword("1234");
		
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
	
	/**
	 * Loads the home screen of the GUI. This screen features six buttons that allow the user
	 * to navigate through all the features of the program.
	 * @param primaryStage The main window of the GUI.
	 */
	public void loadHomeScreen(Stage primaryStage) {
		//Define size of buttons
		final int BTN_WIDTH = 200;
		final int BTN_HEIGHT = 200;
		
		//Create root pane
		GridPane pane = new GridPane();
		pane.setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		pane.setAlignment(Pos.CENTER);
		pane.setHgap(20);
		pane.setVgap(20);
		
		//Create menu buttons
		Button btnCheckout = new Button("Checkout");
		Button btnInventory = new Button("Inventory");
		Button btnCustomers = new Button("Customers");
		Button btnDelivery = new Button("Deliveries");
		Button btnAdmin = new Button("Admin");
		Button btnReturns = new Button("Returns");
		
		//Set button size
		btnCheckout.setMinWidth(BTN_WIDTH);
		btnCheckout.setMinHeight(BTN_HEIGHT);
		btnInventory.setMinWidth(BTN_WIDTH);
		btnInventory.setMinHeight(BTN_HEIGHT);
		btnCustomers.setMinWidth(BTN_WIDTH);
		btnCustomers.setMinHeight(BTN_HEIGHT);
		btnDelivery.setMinWidth(BTN_WIDTH);
		btnDelivery.setMinHeight(BTN_HEIGHT);
		btnAdmin.setMinWidth(BTN_WIDTH);
		btnAdmin.setMinHeight(BTN_HEIGHT);
		btnReturns.setMinWidth(BTN_WIDTH);
		btnReturns.setMinHeight(BTN_HEIGHT);
		
		//Add buttons to grid-pane
		pane.add(btnInventory, 0, 0);
		pane.add(btnCustomers, 1, 0);
		pane.add(btnCheckout, 2, 0);
		pane.add(btnAdmin, 0, 1);
		pane.add(btnDelivery, 1, 1);
		pane.add(btnReturns, 2, 1);
		
		//Make buttons functional
		btnInventory.setOnAction((e) -> {
			viewInventory(primaryStage);
		});
		
		btnCustomers.setOnAction((e) -> {
			loadCustomers(primaryStage);
		});
		
		btnCheckout.setOnAction((e) -> {
			loadCheckout(primaryStage);
		});
		
		btnAdmin.setOnAction((e) -> {
			adminControls(primaryStage);
		});
		
		btnDelivery.setOnAction((e) -> {
			loadDeliveries(primaryStage);
		});
		
		//Load scene
		Scene scene = new Scene(pane);
		primaryStage.setTitle(myStore.getStoreName());
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	/**
	 * Loads the inventory screen. This screen features a table showing all SKUs, regardless
	 * of if they are currently in stock. Options to edit, add, or delete SKUs are provided.
	 * Clicking the edit or add button launches the edit inventory screen. Deletion requires
	 * password verification.
	 * @param primaryStage The main window of the GUI.
	 */
	public void viewInventory(Stage primaryStage) {
		//Create root pane and layout objects
		StackPane pane = new StackPane();
		pane.setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		VBox rows = new VBox(20);
		HBox buttonHolder = new HBox(20);
		rows.setPadding(new Insets(20, 20, 20, 20));
		
		//Create buttons
		Button btnBack = new Button("Back");
		Button btnAdd = new Button("Add");
		Button btnEdit = new Button("Edit");
		Button btnDelete = new Button("Delete");
		
		//Format table
		TableView<SKU> table = new TableView<>();
		TableColumn<SKU, String> colID = new TableColumn<>("SKU Number");
		colID.setMinWidth(100);
		colID.setCellValueFactory(new PropertyValueFactory<SKU, String>("id"));
		TableColumn<SKU, String> colName = new TableColumn<>("Name");
		colName.setMinWidth(100);
		colName.setCellValueFactory(new PropertyValueFactory<SKU, String>("name"));
		TableColumn<SKU, String> colColors = new TableColumn<>("Colors");
		colColors.setMinWidth(100);
		colColors.setCellValueFactory(new PropertyValueFactory<SKU, String>("colors"));
		TableColumn<SKU, Double> colWholesale = new TableColumn<>("Wholesale Cost");
		colWholesale.setMinWidth(100);
		colWholesale.setCellValueFactory(new PropertyValueFactory<SKU, Double>("wholesaleCost"));
		TableColumn<SKU, Double> colRetail = new TableColumn<>("Retail Price");
		colRetail.setMinWidth(100);
		colRetail.setCellValueFactory(new PropertyValueFactory<SKU, Double>("retailPrice"));
		TableColumn<SKU, Integer> colShelfLife = new TableColumn<>("Shelf Life (Days)");
		colShelfLife.setMinWidth(100);
		colShelfLife.setCellValueFactory(new PropertyValueFactory<SKU, Integer>("shelfLifeInDays"));
		TableColumn<SKU, Integer> colStock = new TableColumn<>("Units in Stock");
		colStock.setMinWidth(100);
		colStock.setCellValueFactory(new PropertyValueFactory<SKU, Integer>("unitsInStock"));
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		//Add elements to layout
		rows.getChildren().add(table);
		pane.getChildren().add(rows);
		table.getColumns().addAll(colID, colName, colColors, colWholesale, colRetail, colShelfLife, colStock);
		rows.getChildren().add(buttonHolder);
		buttonHolder.getChildren().addAll(btnBack, btnAdd, btnEdit, btnDelete);
		btnEdit.setDisable(true);
		btnDelete.setDisable(true);
		
		//Load data to table
		inventoryData = FXCollections.observableArrayList(inventory);
		table.setItems(inventoryData);
		
		//Format text in cells
		colID.setCellFactory(tc -> new TableCell<SKU, String>() {
		    @Override
		    protected void updateItem(String id, boolean empty) {
		        super.updateItem(id, empty);
		        if (empty) {
		            setText(null);
		        } else {
		        	setText(id);
		        }
		    }
		});
		
		NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
		colWholesale.setCellFactory(tc -> new TableCell<SKU, Double>() {
		    @Override
		    protected void updateItem(Double price, boolean empty) {
		        super.updateItem(price, empty);
		        this.setAlignment(Pos.CENTER_RIGHT);
		        if (empty) {
		            setText(null);
		        } else {
		            setText(currencyFormat.format(price));
		        }
		    }
		});
		
		colRetail.setCellFactory(tc -> new TableCell<SKU, Double>() {
		    @Override
		    protected void updateItem(Double price, boolean empty) {
		        super.updateItem(price, empty);
		        this.setAlignment(Pos.CENTER_RIGHT);
		        if (empty) {
		            setText(null);
		        } else {
		            setText(currencyFormat.format(price));
		        }
		    }
		});
		
		colShelfLife.setCellFactory(tc -> new TableCell<SKU, Integer>() {
		    @Override
		    protected void updateItem(Integer days, boolean empty) {
		        super.updateItem(days, empty);
		        this.setAlignment(Pos.CENTER_RIGHT);
		        if (empty) {
		            setText(null);
		        } else {
		            setText(Integer.toString(days));
		        }
		    }
		});
		
		colStock.setCellFactory(tc -> new TableCell<SKU, Integer>() {
		    @Override
		    protected void updateItem(Integer days, boolean empty) {
		        super.updateItem(days, empty);
		        this.setAlignment(Pos.CENTER_RIGHT);
		        if (empty) {
		            setText(null);
		        } else {
		            setText(Integer.toString(days));
		        }
		    }
		});
		
		//Make buttons functional
		btnBack.setOnAction((e) -> {
			loadHomeScreen(primaryStage);		
		});
		
		btnAdd.setOnAction((e) -> {
			editInventory(primaryStage, null);
		});
		
		btnEdit.setOnAction((e) -> {
			SKU selectedSKU = (SKU)table.getSelectionModel().getSelectedItem();
			editInventory(primaryStage, selectedSKU);
		});
		
		btnDelete.setOnAction((e) -> {
			SKU selectedSKU = (SKU)table.getSelectionModel().getSelectedItem();
			String message = "Are you sure you want to delete " + selectedSKU.getName() + " from the inventory?";
			if(passwordVerificationWindow(message)) {
				inventory.remove(selectedSKU);
				inventoryData = FXCollections.observableArrayList(inventory);
				table.setItems(inventoryData);
			} else {
				System.out.println("Password verification failed.");
			}
		});
		
		//Activate buttons on row selection
		ObservableList<SKU> selectedItems = table.getSelectionModel().getSelectedItems();
		selectedItems.addListener(new ListChangeListener<SKU>() {
			@Override
			public void onChanged(Change<? extends SKU> change) {
				btnEdit.setDisable(false);
				btnDelete.setDisable(false);
			}
		});
		
		//Load scene
		Scene scene = new Scene(pane);
		primaryStage.setTitle(myStore.getStoreName() + ": Inventory");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	/**
	 * Loads a screen to edit the attributes of a SKU. Features a fair bit of text field validation
	 * to ensure that essential information is entered and entered correctly. Prevents the user from
	 * creating duplicate SKU id numbers in the inventory. Additional regex needs to be applied to
	 * price fields.
	 * @param primaryStage The main window of the GUI.
	 * @param selectedSKU A SKU object. This argument can also be passed as a null value. If this 
	 * variable is null, all text fields will be blank (add new SKU). Otherwise, text fields will 
	 * be populated with the attributes of the selected SKU (edit SKU).
	 */
	public void editInventory(Stage primaryStage, SKU selectedSKU) {
		//Create layout elements
		StackPane pane = new StackPane();
		pane.setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		VBox rows = new VBox();
		rows.setPadding(new Insets(20, 20, 20, 20));
		GridPane grid1 = new GridPane();
		grid1.setAlignment(Pos.CENTER);
		grid1.setHgap(10);
		grid1.setVgap(15);
		
		//Create Labels
		Label editSKU = new Label("Edit SKU");
		editSKU.setFont(Font.font("Arial", FontWeight.BOLD, 20));
				
		//Create text-fields and combo-box
		TextField tfID = new TextField();
		Label skuWarning = new Label();
		skuWarning.setTextFill(Color.RED);
		TextField tfName = new TextField();
		Label nameWarning = new Label();
		nameWarning.setTextFill(Color.RED);
		TextField tfWholesale = new TextField();
		TextField tfRetail = new TextField();
		TextField tfShelfLife = new TextField();
		ListView<String> lvColors = new ListView<>(color);
		TextField tfCurrentStock = new TextField();
		
		//Create buttons
		Button btnCancel = new Button("Cancel");
		Button btnSubmit = new Button("Submit");
		
		//Add elements to layout
		pane.getChildren().add(rows);
		rows.getChildren().add(grid1);
		grid1.add(editSKU, 0, 0);
		grid1.add(new Label("SKU Number:"), 0, 1);
		grid1.add(tfID, 1, 1);
		grid1.add(skuWarning, 2, 1);
		grid1.add(new Label("Product Name:"), 0, 2);
		grid1.add(tfName, 1, 2);
		grid1.add(nameWarning, 2, 2);
		grid1.add(new Label("Wholesale Cost:"), 0, 3);
		grid1.add(tfWholesale, 1, 3);
		grid1.add(new Label("Retail Price:"), 0, 4);
		grid1.add(tfRetail, 1, 4);
		grid1.add(new Label("Shelf Life:"), 0, 5);
		grid1.add(tfShelfLife, 1, 5);
		grid1.add(new Label("Color:"), 0, 6);
		grid1.add(lvColors, 1, 6);
		grid1.add(new Label("Units in Stock: "), 0, 7);
		grid1.add(tfCurrentStock, 1, 7);
		grid1.add(btnCancel, 0, 8);
		grid1.add(btnSubmit, 1, 8);
		
		//Add column constraints
		grid1.getColumnConstraints().addAll(new ColumnConstraints(100), new ColumnConstraints(160), new ColumnConstraints(80), new ColumnConstraints(160));
		grid1.setColumnSpan(skuWarning, 2);
		grid1.setColumnSpan(nameWarning, 2);
		lvColors.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		//Formatting for currency fields
		NumberFormat decimalFormat = new DecimalFormat("#0.00");
		
		//Load select SKU information
		if(selectedSKU != null) {
			tfID.setText(selectedSKU.getId());
			tfName.setText(selectedSKU.getName());
			tfWholesale.setText(decimalFormat.format(selectedSKU.getWholesaleCost()));
			tfRetail.setText(decimalFormat.format(selectedSKU.getRetailPrice()));
			tfShelfLife.setText(Integer.toString(selectedSKU.getShelfLifeInDays()));
			tfCurrentStock.setText(Integer.toString(selectedSKU.getUnitsInStock()));
			for (int i = 0; i < selectedSKU.getColors().size(); i++) {
				lvColors.getSelectionModel().select(selectedSKU.getColors().get(i));
			}
		}
		
		//Filters
		UnaryOperator<Change> idFilter = change -> {
			String text = change.getText();
			String fullText = change.getControlNewText();
			if(fullText.length() == 5) {
				for(int i = 0; i < inventory.size(); i++) {
					if(fullText.equals(inventory.get(i).getId()) && !fullText.equals(selectedSKU.getId())) {
						skuWarning.setText("*SKU #" + fullText + " already exists");
						return null;
					}
					skuWarning.setText("");
				}
			}
			if(text.matches("[0-9]*") && fullText.length() <= 5) {
				return change;
			} else {
				return null;
			}
		};
		TextFormatter<String> idFormatter = new TextFormatter<>(idFilter);
		tfID.setTextFormatter(idFormatter);
		
		UnaryOperator<Change> shelfLifeFilter = change -> {
			String text = change.getText();
			String fullText = change.getControlNewText();
			if(text.matches("[0-9]*") && fullText.length() <= 3) {
				return change;
			} else {
				return null;
			}
		};
		TextFormatter<String> shelfLifeFormatter = new TextFormatter<>(shelfLifeFilter);
		tfShelfLife.setTextFormatter(shelfLifeFormatter);
		
		UnaryOperator<Change> currentStockFilter = change -> {
			String text = change.getText();
			String fullText = change.getControlNewText();
			if(text.matches("[0-9]*") && fullText.length() <= 5) {
				return change;
			} else {
				return null;
			}
		};
		TextFormatter<String> currentStockFormatter = new TextFormatter<>(currentStockFilter);
		tfCurrentStock.setTextFormatter(currentStockFormatter);
		
		UnaryOperator<Change> currencyFilter = change -> {
			String text = change.getText();
			String fullText = change.getControlNewText();
			if(text.matches("[0-9.]*") && fullText.length() <= 7) {
				return change;
			} else {
				return null;
			}
		};
		TextFormatter<String> currencyFormatter = new TextFormatter<>(currencyFilter);
		tfWholesale.setTextFormatter(currencyFormatter);
		
		UnaryOperator<Change> retailFilter = change -> {
			String text = change.getText();
			String fullText = change.getControlNewText();
			if(text.matches("[0-9.]*") && fullText.length() <= 7) {
				return change;
			} else {
				return null;
			}
		};
		TextFormatter<String> retailFormatter = new TextFormatter<>(retailFilter);
		tfRetail.setTextFormatter(retailFormatter);
		
		//Make buttons functional
		btnCancel.setOnAction((e) -> {
			viewInventory(primaryStage);
		});
				
		btnSubmit.setOnAction((e) -> {
			boolean abort = false;
			SKU sku = new SKU();
			if(tfID.getText().length() == 5) {
			} else {
				skuWarning.setText("*Invalid SKU Number");
				abort = true;
			}
			if(tfName.getText().length() == 0) {
				nameWarning.setText("*Please enter a name");
				abort = true;
			};
			if(!abort) {
				sku.setId(tfID.getText());
				sku.setName(tfName.getText());
				sku.setWholesaleCost(Double.parseDouble(tfWholesale.getText()));
				sku.setRetailPrice(Double.parseDouble(tfRetail.getText()));
				sku.setShelfLifeInDays(Integer.parseInt(tfShelfLife.getText()));
				sku.setUnitsInStock(Integer.parseInt(tfCurrentStock.getText()));
				sku.setColors(lvColors.getSelectionModel().getSelectedItems());
				if(selectedSKU != null) {
					inventory.remove(selectedSKU);
				}
				inventory.add(sku);				
				viewInventory(primaryStage);
			}
		});
		
		//Load scene
		Scene scene = new Scene(pane);
		primaryStage.setTitle(myStore.getStoreName() + ": Edit Inventory");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	/**
	 * Loads the first of two checkout screens. On this screen, the user selects a customer
	 * for checkout. If the customer is not currently in the system, they can be added by
	 * clicking the add customer button. This launches the edit customer screen. In a future
	 * update, the user will be returned to the checkout screen after adding a customer. Currently,
	 * the user must manually navigate back to checkout after adding a new customer. Additionally,
	 * a skip button exists, to allow for anonymous checkout. This feature is not yet functional,
	 * and the button does nothing.
	 * @param primaryStage The main window of the GUI.
	 */
	public void loadCheckout(Stage primaryStage) {
		//Create layout elements
		StackPane pane = new StackPane();
		pane.setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		VBox rows = new VBox(20);
		rows.setPadding(new Insets(20, 20, 20, 20));
		GridPane grid1 = new GridPane();
		grid1.setAlignment(Pos.CENTER);
		grid1.setHgap(10);
		grid1.setVgap(15);
		GridPane grid2 = new GridPane();
		grid2.setAlignment(Pos.CENTER);
		grid2.setHgap(10);
		grid2.setVgap(15);
				
		//Create Labels
		Label customerInfo = new Label("Select a Customer");
		customerInfo.setFont(Font.font("Arial", FontWeight.BOLD, 20));
						
		//Create text-fields and combo-box
		TextField tfPhoneSearch = new TextField();
				
		//Create buttons
		Button btnCancel = new Button("Cancel");
		Button btnSelect = new Button("Select Customer");
		btnSelect.setDisable(true);
		Button btnAdd = new Button("Add New Customer");
		Button btnSkip = new Button("Skip");
				
		//Add column constraints
		grid1.getColumnConstraints().addAll(new ColumnConstraints(120), new ColumnConstraints(140), new ColumnConstraints(80), new ColumnConstraints(160), new ColumnConstraints(230));
		grid1.setColumnSpan(customerInfo, 2);
		grid2.getColumnConstraints().addAll(new ColumnConstraints(120), new ColumnConstraints(140), new ColumnConstraints(80), new ColumnConstraints(160), new ColumnConstraints(230));
		grid2.setColumnSpan(btnSelect, 2);
		grid2.setColumnSpan(btnAdd, 2);
		
		//Update data
		ObservableList<Customer> searchData = FXCollections.observableArrayList(customers);
		ObservableList<Customer> tempData = FXCollections.observableArrayList();
				
		//Create table
		TableView<Customer> table = new TableView<>();
		TableColumn<Customer, String> colLastName = new TableColumn<>("Last Name");
		colLastName.setMinWidth(140);
		colLastName.setCellValueFactory(new PropertyValueFactory<Customer, String>("lastName"));
		TableColumn<Customer, String> colFirstName = new TableColumn<>("First Name");
		colFirstName.setMinWidth(100);
		colFirstName.setCellValueFactory(new PropertyValueFactory<Customer, String>("firstName"));
		TableColumn<Customer, String> colPhoneNumber = new TableColumn<>("Phone");
		colPhoneNumber.setMinWidth(110);
		colPhoneNumber.setCellValueFactory(new PropertyValueFactory<Customer, String>("phone"));
		TableColumn<Customer, String> colAddressFirstLine = new TableColumn<>("Street Address");
		colAddressFirstLine.setMinWidth(160);
		colAddressFirstLine.setCellValueFactory(new PropertyValueFactory<Customer, String>("addressFirstLine"));
		TableColumn<Customer, String> colCity = new TableColumn<>("City");
		colCity.setMinWidth(100);
		colCity.setCellValueFactory(new PropertyValueFactory<Customer, String>("city"));
		TableColumn<Customer, String> colEmail = new TableColumn<>("Email");
		colEmail.setMinWidth(160);
		colEmail.setCellValueFactory(new PropertyValueFactory<Customer, String>("email"));
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
				
		//Format text in cells
		colPhoneNumber.setCellFactory(tc -> new TableCell<Customer, String>() {
			@Override
			protected void updateItem(String phone, boolean empty) {
				super.updateItem(phone, empty);
				if (empty) {
					setText(null);
				} else {
					setText(String.format("(" + phone.substring(0,3) + ") " + phone.substring(3,6) + "-" + phone.substring(6)));
				}
			}
		});

		//Add elements to layout
		pane.getChildren().add(rows);
		rows.getChildren().add(grid1);
		grid1.add(customerInfo, 0, 0);
		grid1.add(new Label("Search Phone:"), 0, 1);
		grid1.add(tfPhoneSearch, 1, 1);
		rows.getChildren().add(table);
		table.getColumns().addAll(colLastName, colFirstName, colPhoneNumber, colAddressFirstLine, colCity, colEmail);
		table.setItems(searchData);
		table.setMaxHeight(200);
		rows.getChildren().add(grid2);
		grid2.add(btnSelect, 0, 0);
		grid2.add(btnAdd, 1, 0);
		grid2.add(btnCancel, 0, 1);
		grid2.add(btnSkip, 1, 1);
		
		//Filters
		UnaryOperator<Change> searchPhoneFilter = change -> {
			String text = change.getText();
			String fullText = change.getControlNewText();
			if(text.matches("[0-9]*") && fullText.length() <= 10) {
				return change;
			} else {
				return null;
			}
		};
		TextFormatter<String> searchPhoneFormatter = new TextFormatter<>(searchPhoneFilter);
		tfPhoneSearch.setTextFormatter(searchPhoneFormatter);
		
		//Search for customer by phone number, updates table as text input is received
		tfPhoneSearch.setOnKeyReleased( e -> {
			String fullText = tfPhoneSearch.getText();
			tempData.clear();
			for (int i = 0; i < searchData.size(); i++) {
				if (searchData.get(i).getPhone().substring(0, fullText.length()).equals(fullText)) {
					tempData.add(searchData.get(i));
				}
				table.setItems(tempData);
			}
		});
			
		//Activate buttons on row selection
		ObservableList<Customer> selectedItems = table.getSelectionModel().getSelectedItems();
		selectedItems.addListener(new ListChangeListener<Customer>() {
			@Override
			public void onChanged(Change<? extends Customer> change) {
				if(table.getSelectionModel().getSelectedItem() == null) {
					btnSelect.setDisable(true);
				} else {
					btnSelect.setDisable(false);
				}
			}
		});
		
		//Make buttons functional
		btnCancel.setOnAction((e) -> {
			loadHomeScreen(primaryStage);
		});
		
		btnSelect.setOnAction((e) -> {
			Customer selectedCustomer = (Customer)table.getSelectionModel().getSelectedItem();
			loadCheckout2(primaryStage, selectedCustomer);
		});
		
		btnAdd.setOnAction((e) -> {
			editCustomer(primaryStage, null);
		});
		
		//Load Scene
		Scene scene = new Scene(pane);
		primaryStage.setTitle(myStore.getStoreName() + ": Checkout");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	/**
	 * Loads the second of two checkout screens. Transaction can be completed by selecting
	 * items to be purchased and entering a quantity. Tax is automatically calculated. The
	 * purchase can be scheduled for delivery. On submission, a transaction object will be created
	 * and added to the transaction history map.
	 * @param primaryStage The main window of the GUI.
	 * @param customer A customer object. The customer id number is recorded in the transaction
	 * object.
	 */
	public void loadCheckout2(Stage primaryStage, Customer customer) {
		//Array list for checkout items
		ArrayList<CheckOutItem> itemsInCart = new ArrayList<>();
		
		//Options for hour combo box
		List<String> hourList = Arrays.asList("07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00");
		ObservableList<String> hours = FXCollections.observableArrayList(hourList);
		
		//Create layout elements
		ScrollPane pane = new ScrollPane();
		pane.setPrefSize(SCREEN_WIDTH, 800);
		VBox rows = new VBox(20);
		rows.setPadding(new Insets(20, 20, 20, 20));
		VBox checkoutItems = new VBox(20);
		GridPane grid1 = new GridPane();
		grid1.setAlignment(Pos.CENTER);
		grid1.setHgap(10);
		grid1.setVgap(15);
		GridPane grid2 = new GridPane();
		grid2.setAlignment(Pos.CENTER);
		grid2.setHgap(10);
		grid2.setVgap(15);
		GridPane grid3 = new GridPane();
		grid3.setAlignment(Pos.CENTER);
		grid3.setHgap(10);
		grid3.setVgap(15);
		GridPane grid4 = new GridPane();
		grid4.setAlignment(Pos.CENTER);
		grid4.setHgap(10);
		grid4.setVgap(15);
		GridPane buttonsHolder = new GridPane();
		buttonsHolder.setAlignment(Pos.CENTER);
		buttonsHolder.setHgap(10);
		buttonsHolder.setVgap(15);
		GridPane grid5 = new GridPane();
		grid5.setAlignment(Pos.CENTER);
		grid5.setHgap(10);
		grid5.setVgap(15);
				
		//Create Labels
		Label customerInfo = new Label("Customer Information");
		customerInfo.setFont(Font.font("Arial", FontWeight.BOLD, 20));
		Label customerName = new Label(customer.getLastName() + ", " + customer.getFirstName());
		Label customerAddress1 = new Label(customer.getAddressFirstLine());
		Label customerAddress2 = new Label(customer.getAddressSecondLine());
		Label customerCity = new Label(customer.getCity() + ", " + customer.getState() + " " + customer.getZipcode());
		Label allSKUs = new Label("Select Items From Menu to Add to Checkout");
		allSKUs.setFont(Font.font("Arial", FontWeight.BOLD, 20));
		Label checkoutInfo = new Label("Items for Checkout");
		checkoutInfo.setFont(Font.font("Arial", FontWeight.BOLD, 20));
		Label searchByName = new Label("Search by Name:");
		Label quantity = new Label("Quantity");
		Label subtotal = new Label("Subtotal");
		Label lbBottomSubTotal = new Label("Subtotal:");
		Label bottomSubTotal = new Label("$0.00");
		Label lbTax = new Label("Tax:");
		Label tax = new Label("$0.00");
		Label lbTotal = new Label("Total:");
		lbTotal.setFont(Font.font("Arial", FontWeight.BOLD, 16));
		Label total = new Label("$0.00");
		total.setFont(Font.font("Arial", FontWeight.BOLD, 16));
		CheckBox cbDelivery = new CheckBox("Deliver this order");
		Label lbDeliverTo = new Label("Deliver To:");
		lbDeliverTo.setFont(Font.font("Arial", FontWeight.BOLD, 20));
		Label lbDeliveryDate = new Label("On Date:");
		Label lbDeliveryTime = new Label("At Hour:");
		Label lbRecipientFirstName = new Label("First Name:");
		Label lbRecipientLastName = new Label("Last Name:");
		Label lbRecipientAddress = new Label("Address:");
		Label lbRecipientCity = new Label("City:");
		Label lbRecipientState = new Label("State:");
		Label lbRecipientZipcode = new Label("Zipcode:");
		Label lbRecipientPhone = new Label("Phone:");
		Label lbMessage = new Label("Message:");
		DatePicker dpDeliveryDate = new DatePicker();
		ComboBox cbHourPicker = new ComboBox(hours);
		TextField tfRecipientFirstName = new TextField();
		TextField tfRecipientLastName = new TextField();
		TextField tfRecipientAddress1 = new TextField();
		TextField tfRecipientAddress2 = new TextField();
		TextField tfRecipientCity = new TextField();
		TextField tfRecipientState = new TextField();
		TextField tfRecipientZipcode = new TextField();
		TextField tfRecipientPhone = new TextField();
		TextArea taMessage = new TextArea();
		taMessage.setWrapText(true);
		
		//Create text fields
		TextField tfNameSearch = new TextField();
		
		//Create buttons
		Button btnCancel = new Button("Cancel");
		Button btnSelect = new Button("Select Item");
		btnSelect.setDisable(true);
		Button btnSubmit = new Button("Submit");
				
		//Add column constraints
		grid1.getColumnConstraints().addAll(new ColumnConstraints(120), new ColumnConstraints(140), new ColumnConstraints(80), new ColumnConstraints(160), new ColumnConstraints(230));
		grid1.setColumnSpan(customerInfo, 2);
		grid1.setColumnSpan(customerName, 2);
		grid1.setColumnSpan(customerAddress1, 2);
		grid1.setColumnSpan(customerAddress2, 2);
		grid1.setColumnSpan(customerCity, 2);
		grid1.setColumnSpan(allSKUs, 5);
		grid2.getColumnConstraints().addAll(new ColumnConstraints(120), new ColumnConstraints(140), new ColumnConstraints(110), new ColumnConstraints(130), new ColumnConstraints(230));
		grid3.getColumnConstraints().addAll(new ColumnConstraints(70), new ColumnConstraints(150), new ColumnConstraints(150), new ColumnConstraints(100), new ColumnConstraints(60), new ColumnConstraints(100), new ColumnConstraints(80));
		grid3.setColumnSpan(checkoutInfo, 2);
		grid3.setColumnSpan(quantity, 2);
		grid4.getColumnConstraints().addAll(new ColumnConstraints(70), new ColumnConstraints(150), new ColumnConstraints(150), new ColumnConstraints(100), new ColumnConstraints(60), new ColumnConstraints(100), new ColumnConstraints(80));
		grid4.setColumnSpan(lbRecipientFirstName, 2);
		buttonsHolder.getColumnConstraints().addAll(new ColumnConstraints(70), new ColumnConstraints(150), new ColumnConstraints(150), new ColumnConstraints(100), new ColumnConstraints(60), new ColumnConstraints(100), new ColumnConstraints(80));
		buttonsHolder.setColumnSpan(cbDelivery, 2);
		grid5.getColumnConstraints().addAll(new ColumnConstraints(70), new ColumnConstraints(150), new ColumnConstraints(80), new ColumnConstraints(170), new ColumnConstraints(60), new ColumnConstraints(100), new ColumnConstraints(80));
		grid5.setColumnSpan(lbDeliverTo, 2);
		grid5.setColumnSpan(tfRecipientAddress1, 2);
		grid5.setColumnSpan(tfRecipientAddress2, 2);
		grid5.setColumnSpan(taMessage, 3);
		
		//Format table
		TableView<SKU> table = new TableView<>();
		TableColumn<SKU, String> colID = new TableColumn<>("SKU Number");
		colID.setMinWidth(100);
		colID.setCellValueFactory(new PropertyValueFactory<SKU, String>("id"));
		TableColumn<SKU, String> colName = new TableColumn<>("Name");
		colName.setMinWidth(100);
		colName.setCellValueFactory(new PropertyValueFactory<SKU, String>("name"));
		TableColumn<SKU, String> colColors = new TableColumn<>("Colors");
		colColors.setMinWidth(100);
		colColors.setCellValueFactory(new PropertyValueFactory<SKU, String>("colors"));
		TableColumn<SKU, Double> colRetail = new TableColumn<>("Retail Price");
		colRetail.setMinWidth(100);
		colRetail.setCellValueFactory(new PropertyValueFactory<SKU, Double>("retailPrice"));
		TableColumn<SKU, Integer> colStock = new TableColumn<>("Units in Stock");
		colStock.setMinWidth(100);
		colStock.setCellValueFactory(new PropertyValueFactory<SKU, Integer>("unitsInStock"));
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		//Load data to table
		ObservableList<SKU> searchData = FXCollections.observableArrayList(inventory);
		ObservableList<SKU> tempData = FXCollections.observableArrayList();
		table.setItems(searchData);
		
		//Format text in cells
		colID.setCellFactory(tc -> new TableCell<SKU, String>() {
		    @Override
		    protected void updateItem(String id, boolean empty) {
		        super.updateItem(id, empty);
		        if (empty) {
		            setText(null);
		        } else {
		        	setText(id);
		        }
		    }
		});
		
		NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
		colRetail.setCellFactory(tc -> new TableCell<SKU, Double>() {
		    @Override
		    protected void updateItem(Double price, boolean empty) {
		        super.updateItem(price, empty);
		        this.setAlignment(Pos.CENTER_RIGHT);
		        if (empty) {
		            setText(null);
		        } else {
		            setText(currencyFormat.format(price));
		        }
		    }
		});
		
		colStock.setCellFactory(tc -> new TableCell<SKU, Integer>() {
		    @Override
		    protected void updateItem(Integer days, boolean empty) {
		        super.updateItem(days, empty);
		        this.setAlignment(Pos.CENTER_RIGHT);
		        if (empty) {
		            setText(null);
		        } else {
		            setText(Integer.toString(days));
		        }
		    }
		});

		//Add elements to layout
		pane.setContent(rows);
		rows.getChildren().add(grid1);
		grid1.add(customerInfo, 0, 0);
		grid1.add(customerName, 0, 1);
		grid1.add(customerAddress1, 0, 2);
		//grid1.add(customerAddress2, 0, 3);
		grid1.add(customerCity, 0, 3);
		grid1.add(allSKUs, 0, 4);
		rows.getChildren().add(table);
		table.getColumns().addAll(colID, colName, colColors, colRetail, colStock);
		table.setItems(searchData);
		table.setMaxHeight(200);
		rows.getChildren().add(grid2);
		grid2.add(searchByName, 0, 0);
		grid2.add(tfNameSearch, 1, 0);
		grid2.add(btnSelect, 3, 0);
		rows.getChildren().add(grid3);
		grid3.add(checkoutInfo, 0, 0);
		grid3.add(new Label("#"), 0, 1);
		grid3.add(new Label("Name"), 1, 1);
		grid3.add(new Label("Color(s)"), 2, 1);
		grid3.add(quantity, 4, 1);
		grid3.add(subtotal, 5, 1);
		grid3.setHalignment(subtotal, HPos.RIGHT);
		rows.getChildren().add(checkoutItems);
		rows.getChildren().add(grid4);
		grid4.add(lbBottomSubTotal, 4, 0);
		grid4.setHalignment(lbBottomSubTotal, HPos.RIGHT);
		grid4.add(bottomSubTotal, 5, 0);
		grid4.setHalignment(bottomSubTotal, HPos.RIGHT);
		grid4.add(lbTax, 4, 1);
		grid4.setHalignment(lbTax, HPos.RIGHT);
		grid4.add(tax, 5, 1);
		grid4.setHalignment(tax, HPos.RIGHT);
		grid4.add(lbTotal, 4, 2);
		grid4.setHalignment(lbTotal, HPos.RIGHT);
		grid4.add(total, 5, 2);
		grid4.setHalignment(total, HPos.RIGHT);
		rows.getChildren().add(buttonsHolder);
		buttonsHolder.add(cbDelivery, 0, 0);
		buttonsHolder.add(btnCancel, 0, 1);
		buttonsHolder.add(btnSubmit, 1, 1);
		grid5.add(lbDeliverTo, 0, 0);
		grid5.add(lbDeliveryDate, 0, 1);
		grid5.add(dpDeliveryDate, 1, 1);
		grid5.add(lbDeliveryTime, 2, 1);
		grid5.add(cbHourPicker, 3, 1);
		grid5.add(lbRecipientFirstName, 0, 2);
		grid5.add(tfRecipientFirstName, 1, 2);
		grid5.add(lbRecipientLastName, 2, 2);
		grid5.add(tfRecipientLastName, 3, 2);
		grid5.add(lbRecipientAddress, 0, 3);
		grid5.add(tfRecipientAddress1, 1, 3);
		grid5.add(tfRecipientAddress2, 1, 4);
		grid5.add(lbRecipientCity, 0, 5);
		grid5.add(tfRecipientCity, 1, 5);
		grid5.add(lbRecipientState, 2, 5);
		grid5.add(tfRecipientState, 3, 5);
		grid5.add(lbRecipientZipcode, 4, 5);
		grid5.add(tfRecipientZipcode, 5, 5);
		grid5.add(lbRecipientPhone, 0, 6);
		grid5.add(tfRecipientPhone, 1, 6);
		grid5.add(lbMessage, 0, 7);
		grid5.setValignment(lbMessage, VPos.TOP);
		grid5.add(taMessage, 1, 7);
		
		//Narrow search results with text field input
		tfNameSearch.setOnKeyReleased( e -> {
			String fullText = tfNameSearch.getText();
			tempData.clear();
			for (int i = 0; i < searchData.size(); i++) {
				if (searchData.get(i).getName().substring(0, fullText.length()).equalsIgnoreCase(fullText)) {
					tempData.add(searchData.get(i));
				}
				table.setItems(tempData);
			}
		});
		
		//Activate buttons on row selection
		ObservableList<SKU> selectedItems = table.getSelectionModel().getSelectedItems();
		selectedItems.addListener(new ListChangeListener<SKU>() {
			@Override
			public void onChanged(Change<? extends SKU> change) {
				if(table.getSelectionModel().getSelectedItem() == null) {
					btnSelect.setDisable(true);
				} else {
					btnSelect.setDisable(false);
				}
			}
		});
		
		//Make check-box functional
		cbDelivery.setOnAction((e) -> {
			if(cbDelivery.isSelected()) {
				rows.getChildren().remove(buttonsHolder);
				rows.getChildren().add(grid5);
				rows.getChildren().add(buttonsHolder);
			} else {
				rows.getChildren().remove(grid5);
			}
		});
		
		//Make buttons functional
		btnCancel.setOnAction((e) -> {
			loadHomeScreen(primaryStage);
		});
		
		btnSelect.setOnAction((e) -> {
			SKU selectedSKU = (SKU)table.getSelectionModel().getSelectedItem();
			CheckOutItem item = new CheckOutItem(selectedSKU, checkoutItems, total, tax, bottomSubTotal, itemsInCart, myStore);
			itemsInCart.add(item);
			checkoutItems.getChildren().add(item.grid);
			item.setUpGrid(selectedSKU, total, tax, bottomSubTotal, itemsInCart, myStore);
			table.getSelectionModel().select(null);
		});
		
		btnSubmit.setOnAction((e) -> {
			Transaction transaction = new Transaction();
			transaction.appendToReceiptText("Date: " + LocalDateTime.now().toLocalDate() + "\n");
			transaction.appendToReceiptText("Time: " + LocalDateTime.now().toLocalTime() + "\n");
			transaction.appendToReceiptText("Transaction Number: " + transaction.getId() + "\n");
			transaction.setCustomerID(customer.getID());
			transaction.appendToReceiptText("Customer: " + customer.getFirstName() + " " + customer.getLastName() + "\n");
			transaction.setTax(myStore.getTaxRate());
			transaction.setTotalSale(Double.parseDouble(total.getText().substring(1)));
			transaction.appendToReceiptText("Products purchased:\n");
			for (int i = 0; i < itemsInCart.size(); i++) {
				//Create list of items purchased
				itemsInCart.get(i).sku.setQuantitySold(Integer.valueOf(itemsInCart.get(i).tfQuantity.getText()));
				transaction.getItems().add(itemsInCart.get(i).sku);
				transaction.appendToReceiptText(itemsInCart.get(i).sku.getQuantitySold() + " " + itemsInCart.get(i).sku.getName() + "\n");
				//Update units in stock in inventory
				inventory.get(inventory.indexOf(itemsInCart.get(i).sku)).reduceUnitsInStock(Integer.valueOf(itemsInCart.get(i).tfQuantity.getText()));
			}
			transaction.appendToReceiptText("Sales Tax Rate: " + myStore.getTaxRate() + "\nTotal: " + total.getText());
			if(cbDelivery.isSelected()) {
				transaction.setDelivery(true);
				transaction.setHasBeenDelivered(1);
				DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
				transaction.setDeliveryTime(LocalDateTime.parse(String.valueOf(dpDeliveryDate.getValue()) + " " + cbHourPicker.getValue(), timeFormatter));
				transaction.setRecipientFirstName(tfRecipientFirstName.getText());
				transaction.setRecipientLastName(tfRecipientLastName.getText());
				transaction.setRecipientAddress1(tfRecipientAddress1.getText());
				transaction.setRecipientAddress2(tfRecipientAddress2.getText());
				transaction.setRecipientCity(tfRecipientCity.getText());
				transaction.setRecipientState(tfRecipientState.getText());
				transaction.setRecipientZipcode(tfRecipientZipcode.getText());
				transaction.setRecipientPhone(tfRecipientPhone.getText());
				transaction.setDeliveryMessage(taMessage.getText());
				transaction.appendToReceiptText("\nScheduled for delivery to\n" + transaction.getRecipientFirstName() + " " + transaction.getRecipientLastName() + ", on " + transaction.getDeliveryTime());
			}
			//Add transaction to transaction history
			transactionHistory.put(transaction.getId(), transaction);
			System.out.println("Transaction completed successfully.\n" + transaction + "\n");
			loadHomeScreen(primaryStage);
		});
		
		//Load Scene
		Scene scene = new Scene(pane);
		primaryStage.setTitle(myStore.getStoreName() + ": Checkout");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	/**
	 * Loads a screen featuring a table showing all customers in the database. From this screen
	 * the user can add, edit, and delete customers. The add and edit buttons launch the edit
	 * customer screen. Deleting a customer requires a password verification.
	 * @param primaryStage The main window of the GUI.
	 */
	public void loadCustomers(Stage primaryStage) {
		//Create layout elements
		StackPane pane = new StackPane();
		pane.setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		VBox rows = new VBox(20);
		HBox buttonHolder = new HBox(20);
		rows.setPadding(new Insets(20, 20, 20, 20));
		
		//Create buttons
		Button btnBack = new Button("Back");
		Button btnAdd = new Button("Add");
		Button btnEdit = new Button("Edit");
		Button btnDelete = new Button("Delete");
		
		//Update data
		data = FXCollections.observableArrayList(customers);
		
		//Create table
		TableView<Customer> table = new TableView<>();
		TableColumn<Customer, String> colLastName = new TableColumn<>("Last Name");
		colLastName.setMinWidth(140);
		colLastName.setCellValueFactory(new PropertyValueFactory<Customer, String>("lastName"));
		TableColumn<Customer, String> colFirstName = new TableColumn<>("First Name");
		colFirstName.setMinWidth(100);
		colFirstName.setCellValueFactory(new PropertyValueFactory<Customer, String>("firstName"));
		TableColumn<Customer, String> colPhoneNumber = new TableColumn<>("Phone");
		colPhoneNumber.setMinWidth(110);
		colPhoneNumber.setCellValueFactory(new PropertyValueFactory<Customer, String>("phone"));
		TableColumn<Customer, String> colAddressFirstLine = new TableColumn<>("Street Address");
		colAddressFirstLine.setMinWidth(160);
		colAddressFirstLine.setCellValueFactory(new PropertyValueFactory<Customer, String>("addressFirstLine"));
		TableColumn<Customer, String> colCity = new TableColumn<>("City");
		colCity.setMinWidth(100);
		colCity.setCellValueFactory(new PropertyValueFactory<Customer, String>("city"));
		TableColumn<Customer, String> colEmail = new TableColumn<>("Email");
		colEmail.setMinWidth(160);
		colEmail.setCellValueFactory(new PropertyValueFactory<Customer, String>("email"));
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		//Format text in cells
		colPhoneNumber.setCellFactory(tc -> new TableCell<Customer, String>() {
			@Override
			protected void updateItem(String phone, boolean empty) {
				super.updateItem(phone, empty);
				if (empty) {
					setText(null);
				} else {
					setText(String.format("(" + phone.substring(0,3) + ") " + phone.substring(3,6) + "-" + phone.substring(6)));
				}
			}
		});
		
		//Create layout
		rows.getChildren().add(table);
		pane.getChildren().add(rows);
		table.getColumns().addAll(colLastName, colFirstName, colPhoneNumber, colAddressFirstLine, colCity, colEmail);
		table.setItems(data);
		rows.getChildren().add(buttonHolder);
		buttonHolder.getChildren().addAll(btnBack, btnAdd, btnEdit, btnDelete);
		btnEdit.setDisable(true);
		btnDelete.setDisable(true);
		
		//Make buttons functional
		btnBack.setOnAction((e) -> {
			loadHomeScreen(primaryStage);		
		});
		
		btnEdit.setOnAction((e) -> {
			Customer selectedCustomer = (Customer)table.getSelectionModel().getSelectedItem();
			editCustomer(primaryStage, selectedCustomer);
		});
		
		btnAdd.setOnAction((e) -> {
			editCustomer(primaryStage, null);
		});
		
		btnDelete.setOnAction((e) -> {
			Customer selectedCustomer = (Customer)table.getSelectionModel().getSelectedItem();
			String message = "Are you sure you want to delete " + selectedCustomer.getFirstName() + " " + selectedCustomer.getLastName() + " from the inventory?";
			if(passwordVerificationWindow(message)) {
				customers.remove(selectedCustomer);
				data = FXCollections.observableArrayList(customers);
				table.setItems(data);
			} else {
				System.out.println("Password validation failed.");
			}
		});
		
		//Activate buttons on row selection
		ObservableList<Customer> selectedItems = table.getSelectionModel().getSelectedItems();
		selectedItems.addListener(new ListChangeListener<Customer>() {
			@Override
			public void onChanged(Change<? extends Customer> change) {
				btnEdit.setDisable(false);
				btnDelete.setDisable(false);
			}
		});
		
		//Load scene
		Scene scene = new Scene(pane);
		primaryStage.setTitle(myStore.getStoreName() + ": Customers");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	/**
	 * Loads a screen to edit/add customer information. For new customers, the text fields are
	 * left blank. For existing customers, the text fields are populated with the customer's 
	 * information. Filters are applied to several of the text fields to ensure that information
	 * is entered correctly and completely.
	 * @param primaryStage The main window of the GUI.
	 * @param existingCustomer A customer object. This argument can also be a null value. If
	 * null, the text fields are left blank (add customer). Otherwise, the text fields are
	 * populated with the customer information (edit customer).
	 */
	public void editCustomer(Stage primaryStage, Customer existingCustomer) {
		//Reminders array
		ArrayList<ReminderFrame> remindersArray = new ArrayList<>();
		
		//Create layout elements
		ScrollPane scrollPane = new ScrollPane();
		VBox main = new VBox();
		main.setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		main.setAlignment(Pos.CENTER);
		main.setSpacing(20);
		
		GridPane pane = new GridPane();
		pane.setAlignment(Pos.CENTER);
		pane.setHgap(10);
		pane.setVgap(15);
		
		GridPane buttonsHolder = new GridPane();
		buttonsHolder.setAlignment(Pos.CENTER);
		buttonsHolder.setHgap(10);
		buttonsHolder.setVgap(15);
		
		//Create Labels
		Label customerInfo = new Label("Customer Information");
		customerInfo.setFont(Font.font("Arial", FontWeight.BOLD, 20));
		Label remindersLb = new Label("Reminders");
		remindersLb.setFont(Font.font("Arial", FontWeight.BOLD, 20));
		
		//Create TextFields
		TextField tfFirstName = new TextField();
		TextField tfLastName = new TextField();
		TextField tfPhoneNumber = new TextField();
		TextField tfEmail = new TextField();
		TextField tfAddressFirstLine = new TextField();
		TextField tfAddressSecondLine = new TextField();
		TextField tfCity = new TextField();
		TextField tfState = new TextField();
		TextField tfZipcode = new TextField();
		TextField tfCreditCard = new PasswordField();
		
		//Create Buttons
		Button btnAddReminder = new Button("+ Reminder");
		Button btnCancel = new Button("Cancel");
		Button btnSubmit = new Button("Submit");
		
		//Add TextFields and Labels to customer info GridPane
		pane.add(customerInfo, 0, 0);
		pane.add(new Label("First Name:"), 0, 1);
		pane.add(tfFirstName, 1, 1);
		pane.add(new Label("Last Name:"), 2, 1);
		pane.add(tfLastName, 3, 1);
		pane.add(new Label("Phone:"), 0, 2);
		pane.add(tfPhoneNumber, 1, 2);
		pane.add(new Label("Email:"), 0, 3);
		pane.add(tfEmail, 1, 3);
		pane.add(new Label("Billing Address:"), 0, 4);
		pane.add(tfAddressFirstLine, 1, 4);
		pane.add(tfAddressSecondLine, 1, 5);
		pane.add(new Label("City:"), 0, 6);
		pane.add(tfCity, 1, 6);
		pane.add(new Label("State:"), 2, 6);
		pane.add(tfState, 3, 6);
		pane.add(new Label("Zipcode:"), 0, 7);
		pane.add(tfZipcode, 1, 7);
		pane.add(new Label("Credit Card:"), 0, 8);
		pane.add(tfCreditCard, 1, 8);
		pane.add(remindersLb, 0, 9);
		
		//Add Buttons to GridPane
		buttonsHolder.add(btnAddReminder, 0, 0);
		buttonsHolder.add(btnCancel, 0, 1);
		buttonsHolder.add(btnSubmit, 1, 1);
		
		//Add ColumnConstraints
		pane.getColumnConstraints().addAll(new ColumnConstraints(100), new ColumnConstraints(160), new ColumnConstraints(80), new ColumnConstraints(160));
		buttonsHolder.getColumnConstraints().addAll(new ColumnConstraints(100), new ColumnConstraints(160), new ColumnConstraints(80), new ColumnConstraints(160));
		buttonsHolder.setColumnSpan(btnAddReminder, 2);
		pane.setColumnSpan(tfAddressFirstLine, 2);
		pane.setColumnSpan(tfAddressSecondLine, 2);
		pane.setColumnSpan(tfCreditCard, 2);
		pane.setColumnSpan(remindersLb, 2);
		pane.setColumnSpan(customerInfo, 2);
		
		//Add headers and grids to VBox
		main.getChildren().add(pane);
		if(existingCustomer != null) {
			for(int i = 0; i < existingCustomer.getReminders().size(); i++) {
				ReminderFrame reminder1 = new ReminderFrame();
				remindersArray.add(reminder1);
				reminder1.setUpGrid(main, remindersArray);
				reminder1.dpDate.setValue(existingCustomer.getReminders().get(i).getDate());
				reminder1.cbOccasion.setValue(existingCustomer.getReminders().get(i).getOccasionType());
				reminder1.tfFirstName.setText(existingCustomer.getReminders().get(i).getRecipientFirstName());
				reminder1.tfLastName.setText(existingCustomer.getReminders().get(i).getRecipientLastName());
				main.getChildren().add(reminder1.grid);
			}
		}
		main.getChildren().add(buttonsHolder);
		scrollPane.setContent(main);
		
		//Load selected customer (set default values for text fields)
		if(existingCustomer != null) {
			tfFirstName.setText(existingCustomer.getFirstName());
			tfLastName.setText(existingCustomer.getLastName());
			tfPhoneNumber.setText(existingCustomer.getPhone());
			tfEmail.setText(existingCustomer.getEmail());
			tfAddressFirstLine.setText(existingCustomer.getAddressFirstLine());
			tfAddressSecondLine.setText(existingCustomer.getAddressSecondLine());
			tfCity.setText(existingCustomer.getCity());
			tfZipcode.setText(existingCustomer.getZipcode());
			tfCreditCard.setText(existingCustomer.getCreditCard());
		}
		
		//Filters
		UnaryOperator<Change> phoneFilter = change -> {
			String text = change.getText();
			String fullText = change.getControlNewText();
			if(text.matches("[0-9]*") && fullText.length() <= 10) {
				return change;
			}
			return null;
		};
		TextFormatter<String> phoneFormatter = new TextFormatter<>(phoneFilter);
		tfPhoneNumber.setTextFormatter(phoneFormatter);
		
		UnaryOperator<Change> zipcodeFilter = change -> {
			String text = change.getText();
			String fullText = change.getControlNewText();
			if(text.matches("[0-9]*") && fullText.length() <= 5) {
				return change;
			}
			return null;
		};
		TextFormatter<String> zipcodeFormatter = new TextFormatter<>(zipcodeFilter);
		tfZipcode.setTextFormatter(zipcodeFormatter);
		
		UnaryOperator<Change> creditCardFilter = change -> {
			String text = change.getText();
			String fullText = change.getControlNewText();
			if(text.matches("[0-9]*") && fullText.length() <= 16) {
				return change;
			}
			return null;
		};
		TextFormatter<String> creditCardFormatter = new TextFormatter<>(creditCardFilter);
		tfCreditCard.setTextFormatter(creditCardFormatter);
		
		//Make buttons functional
		btnAddReminder.setOnAction((e) -> {
			ReminderFrame reminder1 = new ReminderFrame();
			remindersArray.add(reminder1);
			reminder1.setUpGrid(main, remindersArray);
			main.getChildren().remove(buttonsHolder);
			main.getChildren().add(reminder1.grid);
			main.getChildren().add(buttonsHolder);
		});
		
		btnCancel.setOnAction((e) -> {
			loadCustomers(primaryStage);
		});
		
		btnSubmit.setOnAction((e) -> {
			Customer customer = new Customer();
			if(existingCustomer == null) {
				customer.setID(myStore.getNextCustomerID());
				myStore.advanceCustomerID();
			} else {
				customer.setID(existingCustomer.getID());
			}
			customer.setFirstName(tfFirstName.getText());
			customer.setLastName(tfLastName.getText());
			customer.setPhone(tfPhoneNumber.getText());
			if(tfEmail.getText().matches("^(.+)@(.+)$")) {
				customer.setEmail(tfEmail.getText());
			} else {
				System.out.println("Invalid email");
			}
			customer.setAddressFirstLine(tfAddressFirstLine.getText());
			customer.setAddressSecondLine(tfAddressSecondLine.getText());
			customer.setCity(tfCity.getText());
			customer.setState(tfState.getText());
			customer.setZipcode(tfZipcode.getText());
			customer.setCreditCard(tfCreditCard.getText());
			for(int i = 0; i < remindersArray.size(); i++) {
				Reminder reminder = new Reminder();
				reminder.setDate(remindersArray.get(i).dpDate.getValue());
				reminder.setOccasionType((String)remindersArray.get(i).cbOccasion.getValue());
				reminder.setRecipientFirstName(remindersArray.get(i).tfFirstName.getText());
				reminder.setRecipientLastName(remindersArray.get(i).tfLastName.getText());
				customer.addReminder(reminder);
			}
			if(existingCustomer != null) {
				customers.remove(existingCustomer);
			}
			customers.add(customer);
			loadCustomers(primaryStage);
		});
		
		Scene scene = new Scene(scrollPane);
		primaryStage.setTitle(myStore.getStoreName() + ": Edit Customer");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	/**
	 * Loads a screen displaying basic information about the store. Password verification is
	 * required to edit store settings. Additional features may be added to this in the future.
	 * @param primaryStage The main window of the GUI.
	 */
	public void adminControls(Stage primaryStage) {
		//Create layout elements
		ScrollPane scrollPane = new ScrollPane();
		VBox main = new VBox();
		main.setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		main.setAlignment(Pos.CENTER);
		main.setSpacing(20);
		
		GridPane pane = new GridPane();
		pane.setAlignment(Pos.CENTER);
		pane.setHgap(10);
		pane.setVgap(15);
		
		GridPane buttonsHolder = new GridPane();
		buttonsHolder.setAlignment(Pos.CENTER);
		buttonsHolder.setHgap(10);
		buttonsHolder.setVgap(15);
		
		//Create Labels
		Label storeInfo = new Label("Store Information");
		storeInfo.setFont(Font.font("Arial", FontWeight.BOLD, 20));
		
		//Create Buttons
		Button btnBack = new Button("Back");
		Button btnEdit = new Button("Edit");
		
		//Display store information
		pane.add(storeInfo, 0, 0);
		pane.add(new Label(myStore.getStoreName()), 0, 1);
		pane.add(new Label(myStore.getAddressFirstLine()), 0, 2);
		pane.add(new Label(myStore.getAddressSecondLine()), 0, 3);
		pane.add(new Label(myStore.getCity() + ", " + myStore.getState() + " " + myStore.getZipcode()), 0, 4);
		pane.add(new Label(myStore.getPhone()), 0, 5);
		pane.add(new Label(myStore.getEmail()), 0, 6);
		pane.add(new Label(myStore.getWebsite()), 0, 7);
		
		//Add Buttons to GridPane
		buttonsHolder.add(btnBack, 0, 0);
		buttonsHolder.add(btnEdit, 1, 0);
		
		//Add ColumnConstraints
		pane.getColumnConstraints().addAll(new ColumnConstraints(300), new ColumnConstraints(60), new ColumnConstraints(80), new ColumnConstraints(60));
		buttonsHolder.getColumnConstraints().addAll(new ColumnConstraints(60), new ColumnConstraints(200), new ColumnConstraints(80), new ColumnConstraints(160));

		//Add headers and grids to VBox
		main.getChildren().add(pane);
		main.getChildren().add(buttonsHolder);
		scrollPane.setContent(main);
		
		//Make buttons functional
		btnBack.setOnAction((e) -> {
			loadHomeScreen(primaryStage);
		});
		
		btnEdit.setOnAction((e) -> {
			if(passwordVerificationWindow("Password required to edit store information.")) {
				editStoreSettings(primaryStage);
			}
		});
		
		Scene scene = new Scene(scrollPane);
		primaryStage.setTitle(myStore.getStoreName() + ": Admin Controls");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	/**
	 * Loads a screen that allows the user to edit basic information about the store including
	 * name, address, phone number, email, website, etc. In future iterations, this could also
	 * feature store hours and other useful information.
	 * @param primaryStage The main window of the GUI.
	 */
	public void editStoreSettings(Stage primaryStage) {
		//Create layout elements
		ScrollPane scrollPane = new ScrollPane();
		VBox main = new VBox();
		main.setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		main.setAlignment(Pos.CENTER);
		main.setSpacing(20);
		
		GridPane pane = new GridPane();
		pane.setAlignment(Pos.CENTER);
		pane.setHgap(10);
		pane.setVgap(15);
		
		GridPane buttonsHolder = new GridPane();
		buttonsHolder.setAlignment(Pos.CENTER);
		buttonsHolder.setHgap(10);
		buttonsHolder.setVgap(15);
		
		//Create Labels
		Label storeInfo = new Label("Store Information");
		storeInfo.setFont(Font.font("Arial", FontWeight.BOLD, 20));
		
		//Create TextFields
		TextField tfName = new TextField();
		TextField tfAddressFirstLine = new TextField();
		TextField tfAddressSecondLine = new TextField();
		TextField tfCity = new TextField();
		TextField tfState = new TextField();
		TextField tfZipcode = new TextField();
		TextField tfPhoneNumber = new TextField();
		TextField tfEmail = new TextField();
		TextField tfWebsite = new TextField();
		
		//Create Buttons
		Button btnCancel = new Button("Cancel");
		Button btnSubmit = new Button("Submit");
		
		//Add TextFields and Labels to customer info GridPane
		pane.add(storeInfo, 0, 0);
		pane.add(new Label("Store Name:"), 0, 1);
		pane.add(tfName, 1, 1);
		pane.add(new Label("Store Address:"), 0, 2);
		pane.add(tfAddressFirstLine, 1, 2);
		pane.add(tfAddressSecondLine, 1, 3);
		pane.add(new Label("City:"), 0, 4);
		pane.add(tfCity, 1, 4);
		pane.add(new Label("State:"), 2, 4);
		pane.add(tfState, 3, 4);
		pane.add(new Label("Zipcode:"), 0, 5);
		pane.add(tfZipcode, 1, 5);
		pane.add(new Label("Phone:"), 0, 6);
		pane.add(tfPhoneNumber, 1, 6);
		pane.add(new Label("Email:"), 0, 7);
		pane.add(tfEmail, 1, 7);
		pane.add(new Label("Website:"), 0, 8);
		pane.add(tfWebsite, 1, 8);

		
		//Add Buttons to GridPane
		buttonsHolder.add(btnCancel, 0, 0);
		buttonsHolder.add(btnSubmit, 1, 0);
		
		//Add ColumnConstraints
		pane.getColumnConstraints().addAll(new ColumnConstraints(100), new ColumnConstraints(160), new ColumnConstraints(80), new ColumnConstraints(160));
		buttonsHolder.getColumnConstraints().addAll(new ColumnConstraints(100), new ColumnConstraints(160), new ColumnConstraints(80), new ColumnConstraints(160));
		pane.setColumnSpan(tfAddressFirstLine, 2);
		pane.setColumnSpan(tfAddressSecondLine, 2);
		pane.setColumnSpan(storeInfo, 2);
		
		//Add headers and grids to VBox
		main.getChildren().add(pane);
		main.getChildren().add(buttonsHolder);
		scrollPane.setContent(main);
		
		//Load store information (set default values for text fields)
		tfName.setText(myStore.getStoreName());
		tfAddressFirstLine.setText(myStore.getAddressFirstLine());
		tfAddressSecondLine.setText(myStore.getAddressSecondLine());
		tfCity.setText(myStore.getCity());
		tfState.setText(myStore.getState());
		tfZipcode.setText(myStore.getZipcode());
		tfPhoneNumber.setText(myStore.getPhone());
		tfEmail.setText(myStore.getEmail());
		tfWebsite.setText(myStore.getWebsite());
		
		//Filters
		UnaryOperator<Change> phoneFilter = change -> {
			String text = change.getText();
			String fullText = change.getControlNewText();
			if(text.matches("[0-9]*") && fullText.length() <= 10) {
				return change;
			}
			return null;
		};
		TextFormatter<String> phoneFormatter = new TextFormatter<>(phoneFilter);
		tfPhoneNumber.setTextFormatter(phoneFormatter);
		
		UnaryOperator<Change> zipcodeFilter = change -> {
			String text = change.getText();
			String fullText = change.getControlNewText();
			if(text.matches("[0-9]*") && fullText.length() <= 5) {
				return change;
			}
			return null;
		};
		TextFormatter<String> zipcodeFormatter = new TextFormatter<>(zipcodeFilter);
		tfZipcode.setTextFormatter(zipcodeFormatter);
		
		//Make buttons functional
		btnCancel.setOnAction((e) -> {
			adminControls(primaryStage);
		});
		
		btnSubmit.setOnAction((e) -> {
			myStore.setStoreName(tfName.getText());
			myStore.setAddressFirstLine(tfAddressFirstLine.getText());
			myStore.setAddressSecondLine(tfAddressSecondLine.getText());
			myStore.setCity(tfCity.getText());
			myStore.setState(tfState.getText());
			myStore.setZipcode(tfZipcode.getText());
			myStore.setPhone(tfPhoneNumber.getText());
			myStore.setWebsite(tfWebsite.getText());
			myStore.setEmail(tfEmail.getText());
			adminControls(primaryStage);
		});
		
		Scene scene = new Scene(scrollPane);
		primaryStage.setTitle(myStore.getStoreName() + ": Edit Store Settings");
		primaryStage.setScene(scene);
		primaryStage.show();

	}
	
	/**
	 * Loads a screen showing a list of orders scheduled for delivery. This method is still being 
	 * written and needs refinement. Ideally, orders will be listed in chronological order.
	 * @param primaryStage The main window of the GUI.
	 */
	public void loadDeliveries(Stage primaryStage) {
		//Create layout elements
		ScrollPane pane = new ScrollPane();
		pane.setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		VBox rows = new VBox(20);
		rows.setPadding(new Insets(20, 20, 20, 20));
		GridPane grid1 = new GridPane();
		grid1.setAlignment(Pos.CENTER);
		grid1.setHgap(10);
		grid1.setVgap(15);
		GridPane grid2 = new GridPane();
		grid2.setAlignment(Pos.CENTER);
		grid2.setHgap(10);
		grid2.setVgap(15);
				
		//Create Labels
		Label lbDeliveries = new Label("Delivery Schedule");
		lbDeliveries.setFont(Font.font("Arial", FontWeight.BOLD, 20));
		Label lbNoDeliveries = new Label("No deliveries scheduled.");
						
		//Create text-fields and combo-box
		TextField tfPhoneSearch = new TextField();
				
		//Create buttons
		Button btnBack = new Button("Back");
				
		//Add column constraints
		grid1.getColumnConstraints().addAll(new ColumnConstraints(120), new ColumnConstraints(140), new ColumnConstraints(80), new ColumnConstraints(160), new ColumnConstraints(230));
		grid1.setColumnSpan(lbDeliveries, 2);
		grid1.setColumnSpan(lbNoDeliveries, 2);
		grid2.getColumnConstraints().addAll(new ColumnConstraints(120), new ColumnConstraints(140), new ColumnConstraints(80), new ColumnConstraints(160), new ColumnConstraints(230));
		
		//Add elements to layout
		pane.setContent(rows);
		rows.getChildren().add(grid1);
		grid1.add(lbDeliveries, 0, 0);
		
		//Create array of deliveries
		ArrayList<Transaction> deliveriesArray = new ArrayList<>();
		ArrayList<DeliveryFrame> deliveryFramesArray = new ArrayList<>();
		transactionHistory.forEach((id, transaction) -> {
			if(transaction.isDelivery() ) {//&& transaction.getHasBeenDelivered() >= 0) {
				deliveriesArray.add(transaction);
			}			
		});
		deliveriesArray.sort(new DeliveryTimeComparator());
		if(deliveriesArray.size() > 0) {
			for(int i = 0; i < deliveriesArray.size(); i++) {
				DeliveryFrame delivery1 = new DeliveryFrame(deliveriesArray.get(i));
				deliveryFramesArray.add(delivery1);
				rows.getChildren().add(delivery1.grid);
				delivery1.setUpGrid(rows, deliveryFramesArray);
			}
		} else {
			grid1.add(lbNoDeliveries, 0, 1);
		}
		rows.getChildren().add(grid2);
		grid2.add(btnBack, 0, 0);
		
		//Make buttons functional
		btnBack.setOnAction((e) -> {
			loadHomeScreen(primaryStage);
		});
		
		//Load scene
		Scene scene = new Scene(pane);
		primaryStage.setTitle(myStore.getStoreName() + ": Deliveries");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * This method is designed to take the array containing the customer database and write it to
	 * an external file so that this information is saved when the program is closed. This method
	 * is called in the stop method (when the window is closed).
	 * @param customerList An array containing the entire customer database.
	 * @throws IOException Catches and prints exception if thrown.
	 */
	public void writeCustomerListToFile(ArrayList<Customer> customerList) throws IOException {
		try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("CustomerDataBase.dat"));) {
			output.writeObject(customerList);
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	/**
	 * This method is called in the start method (at launch), and reads information from the
	 * customer database file. The information is returned as an array of customer objects.
	 * @return Returns an array of customer objects read from the customer database file. Returns 
	 * null if an exception is thrown.
	 * @throws ClassNotFoundException Catches and prints exception if thrown.
	 * @throws IOException Catches and prints exception if thrown.
	 * @throws FileNotFoundException Catches and prints exception if thrown.
	 */
	public ArrayList<Customer> readCustomerListFromFile() throws ClassNotFoundException, IOException, FileNotFoundException {
		File sourceFile = new File("CustomerDataBase.dat");
		ArrayList<Customer> customerList = new ArrayList<>();
		//Check to see if file exists, then try to read it
		if(sourceFile.exists()) {
			try (ObjectInputStream input = new ObjectInputStream(new FileInputStream("CustomerDataBase.dat"));) {
				customerList.addAll((ArrayList<Customer>)(input.readObject()));
			}  catch (Exception e) {
				System.out.println(e);
				return null;
			} 
			return customerList;
		} else {
			System.out.println("CustomerDataBase.dat could not be found.");
			return customerList;
		}
	}
	
	/**
	 * This method is called in the stop method (when window is closed). It exports all information
	 * about the store's inventory to an external .dat file so that all information is saved
	 * when the program is no longer running.
	 * @param skuList An array of SKU objects. Contains information about the store's inventory.
	 * @throws IOException Catches and prints exception if thrown.
	 */
	public void exportInventoryToFile(ArrayList<SKU> skuList) throws IOException {
		try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("Inventory.dat"));) {
			output.writeObject(skuList);
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	/**
	 * This method reads a file containing information about the store's inventory. It returns
	 * this information as an array of SKU objects. It is called in the start method (at launch).
	 * @return Returns an array of SKU objects. Or if an exception is thrown, returns null.
	 * @throws ClassNotFoundException Catches and prints exception if thrown.
	 * @throws IOException Catches and prints exception if thrown.
	 * @throws FileNotFoundException Catches and prints exception if thrown.
	 */
	public ArrayList<SKU> readInventoryFromFile() throws ClassNotFoundException, IOException, FileNotFoundException {
		File sourceFile = new File("Inventory.dat");
		ArrayList<SKU> skuList = new ArrayList<>();
		//Check to see if file exists, then try to read it
		if(sourceFile.exists()) {
			try (ObjectInputStream input = new ObjectInputStream(new FileInputStream("Inventory.dat"));) {
				skuList.addAll((ArrayList<SKU>)(input.readObject()));
			}  catch (Exception e) {
				System.out.println(e);
				return null;
			} 
			return skuList;
		} else {
			System.out.println("Inventory.dat could not be found.");
			return skuList;
		}
	}
	
	/**
	 * This method writes a map of transaction objects (the transaction history) to an external file
	 * so that this information is saved when the program is not running. It is called in the stop
	 * method (when the window is closed). 
	 * @param transactionHistory A hash map of transaction objects. The key is the transaction object's id.
	 * @throws IOException Catches and prints exception if thrown.
	 */
	public void writeTransactionHistoryToFile(HashMap<Long, Transaction> transactionHistory) throws IOException {
		try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("TransactionHistory.dat"));) {
			output.writeObject(transactionHistory);
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	/**
	 * This method imports the transaction history from an external file. It is returned as a
	 * hash map of transaction objects. It is called in the start method (at launch).
	 * @return Returns a hash map of transaction objects (the transaction history).
	 * @throws ClassNotFoundException Catches and prints exception if thrown.
	 * @throws IOException Catches and prints exception if thrown.
	 * @throws FileNotFoundException Catches and prints exception if thrown.
	 */
	public HashMap<Long, Transaction> readTransactionHistoryFromFile() throws ClassNotFoundException, IOException, FileNotFoundException {
		File sourceFile = new File("TransactionHistory.dat");
		HashMap<Long, Transaction> transactionHistory = new HashMap<>();
		//Check to see if file exists, then try to read it
		if(sourceFile.exists()) {
			try (ObjectInputStream input = new ObjectInputStream(new FileInputStream("TransactionHistory.dat"));) {
				transactionHistory.putAll((HashMap<Long, Transaction>)(input.readObject()));
			}  catch (Exception e) {
				System.out.println(e);
				return null;
			} 
			return transactionHistory;
		} else {
			System.out.println("TransactionHistory.dat could not be found.");
			return transactionHistory;
		}
	}
}

/**
 * The DeliveryFrame class is designed to be a modular graphic feature. Each DeliveryFrame displays
 * information about a transaction. The loadDeliveries method uses these frames to populate a 
 * delivery schedule in the window. Each frame contains buttons to update the status of the delivery.
 * @author Adam Grimshaw
 */
class DeliveryFrame {
	//Create graphic elements
	GridPane grid = new GridPane();
	Label lbDate = new Label();
	Label lbHour = new Label();
	Label lbShippingAddress = new Label();
	Label lbItemList = new Label("");
	Label lbHasMessage = new Label();
	Button btnRemove = new Button("Remove");
	Button btnDelivered = new Button("Delivered");
	Button btnCouldNot = new Button("Could Not Deliver");
	
	//Transaction to hold information about delivery
	Transaction transaction = new Transaction();

	//Constructors
	public DeliveryFrame() {
		
	}
	
	public DeliveryFrame(Transaction trns) {
		this.transaction = trns;
	}
	
	//Methods
	/**
	 * This method populates the labels with transaction information, and adds them to the
	 * grid pane. It places buttons and defines their functions. 
	 * @param main The VBox into which the deliveryFrame GridPane is being added. 
	 * @param array The array of DeliveryFrame objects used to keep track of this DeliveryFrame.
	 */
	public void setUpGrid(VBox main, ArrayList<DeliveryFrame> array) {
		//Set text in labels
		lbDate.setText(transaction.getDeliveryTime().toString());
		lbShippingAddress.setText(transaction.formatDeliveryAddress());
		for(int i = 0; i < transaction.getItems().size(); i++) {
			lbItemList.setText(lbItemList.getText() + transaction.getItems().get(i).getName());
		}
		if(transaction.getDeliveryMessage().equals("")) {
			lbHasMessage.setText("No message included.");
		} else {
			lbHasMessage.setText("Has message.");
		}
		
		//Populate grid pane
		grid.add(lbDate, 0, 0);
		grid.add(lbHour, 1, 0);
		grid.add(lbShippingAddress, 0, 1);
		grid.add(btnRemove, 0, 4);
		grid.add(btnCouldNot, 1, 4);
		grid.add(btnDelivered, 2, 4);
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(8);
		grid.getColumnConstraints().addAll(new ColumnConstraints(100), new ColumnConstraints(160), new ColumnConstraints(80), new ColumnConstraints(160), new ColumnConstraints(70), new ColumnConstraints(70), new ColumnConstraints(70));
		grid.setColumnSpan(lbDate, 2);
		btnRemove.setOnAction((e) -> {
			array.remove(this);
			main.getChildren().remove(this.grid);
		});
	}
}

/**
 * The ReminderFrame class is designed to be a modular graphic feature. Customers can request 
 * reminders for upcoming or reoccurring occasions. On the edit customer screen, there is a button
 * that reads Add Reminder. When clicked, it creates a new ReminderFrame and displays it on the 
 * screen. The ReminderFrame features text fields to collect information about the upcoming occasion.
 * @author Adam Grimshaw
 */
class ReminderFrame {
	//Options for combo box
	private List<String> occasionList = Arrays.asList("Anniversary", "Birthday", "Other");
	ObservableList<String> occasions = FXCollections.observableArrayList(occasionList);
		
	//Create graphic elements	
	GridPane grid = new GridPane();
	Label lbFirstName = new Label("First Name:");
	TextField tfFirstName = new TextField();
	Label lbLastName = new Label("Last Name:");
	TextField tfLastName = new TextField();
	Label lbDate = new Label("Date:");
	DatePicker dpDate = new DatePicker();
	Label lbOccasion = new Label("Occasion:");
	ComboBox cbOccasion = new ComboBox(occasions);
	Button btnRemove = new Button("Remove");
	Customer customer = new Customer();
	
	//Constructors
	public ReminderFrame() {
	}
	
	public ReminderFrame(Customer customer) {
		this.customer = customer;
	}
	
	//Methods
	/**
	 * This method attaches labels and text fields to the grid pane. It places the remove
	 * button and defines it function.
	 * @param main The VBox into which the ReminderFrame GridPane is being added.
	 * @param array The array of ReminderFrame objects used to keep track of this ReminderFrame.
	 */
	public void setUpGrid(VBox main, ArrayList<ReminderFrame> array) {
		grid.add(lbDate, 0, 0);
		grid.add(dpDate, 1, 0);
		grid.add(lbOccasion, 2, 0);
		grid.add(cbOccasion, 3, 0);
		grid.add(lbFirstName, 0, 1);
		grid.add(tfFirstName, 1, 1);
		grid.add(lbLastName, 2, 1);
		grid.add(tfLastName, 3, 1);
		grid.add(btnRemove, 0, 2);
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(15);
		grid.getColumnConstraints().addAll(new ColumnConstraints(100), new ColumnConstraints(160), new ColumnConstraints(80), new ColumnConstraints(160));
	
		btnRemove.setOnAction((e) -> {
			array.remove(this);
			main.getChildren().remove(this.grid);
		});
	}
}

/**
 * The CheckOutItem class is designed to be a modular graphic feature used in checkout. As each new
 * item is added to checkout, a CheckOutItem is created and displayed on the screen. It displays
 * information about the item, and allows the user to enter a quantity for that item. It also features
 * a button to remove that item from the checkout cart.
 * @author Adam Grimshaw
 *
 */
class CheckOutItem {
	//Layout elements
	GridPane grid = new GridPane();
	Label rowIndex = new Label();
	Label itemName = new Label();
	Label itemColor = new Label();
	Label itemQuantity = new Label("Enter Quantity:");
	TextField tfQuantity = new TextField("0");
	Label subtotal = new Label();
	Button btnRemove = new Button("Remove");
	
	//Properties
	SKU sku;
	double subValue = 0.0;
	double preTaxTotal;
	
	//Constructors
	public CheckOutItem() {
		
	}
	
	public CheckOutItem(SKU sku, VBox container, Label total, Label tax, Label sub, ArrayList<CheckOutItem> array,StoreSettings myStore) {
		this.sku = sku;
		itemName.setText(sku.getName());
		itemColor.setText(sku.getColors().get(0));
		
		btnRemove.setOnAction((e) -> {
			preTaxTotal = Double.parseDouble(sub.getText().substring(1)) - subValue;
			sub.setText(String.valueOf(currencyFormat.format(preTaxTotal)));
			container.getChildren().remove(this.grid);
			array.remove(this);
			for (int i = 0; i < array.size(); i++) {
				array.get(i).rowIndex.setText(String.valueOf(i + 1));
			}
			tax.setText(String.valueOf(currencyFormat.format((preTaxTotal) * myStore.getTaxRate())));
			total.setText(String.valueOf(currencyFormat.format((preTaxTotal) * (myStore.getTaxRate() + 1.0))));
		});
	}
	
	/**
	 * This method attaches the various visual elements (labels, text fields, buttons, etc.) to the
	 * grid pane. Additionally, this method calculates and updates subtotal, tax, and total values
	 * when the quantity field is changed.
	 * @param sku A SKU object. (The item being added to the checkout cart.)
	 * @param total A Label containing the total cost.
	 * @param tax A Label containing the cost of sales tax.
	 * @param sub A Label containing the subtotal before adding sales tax.
	 * @param array An array of CheckOutItem objects. (Used for referencing this CheckOutItem.)
	 * @param myStore A StoreSettings object. (Sales tax rate is saved in StoreSettings.)
	 */
	public void setUpGrid(SKU sku, Label total, Label tax, Label sub, ArrayList<CheckOutItem> array, StoreSettings myStore) {
		//Attach layout elements to grid
		rowIndex.setText(String.valueOf(array.indexOf(this) + 1));
		grid.add(rowIndex, 0, 0);
		grid.add(itemName, 1, 0);
		grid.add(itemColor, 2, 0);
		grid.add(itemQuantity, 3, 0);
		grid.add(tfQuantity, 4, 0);
		grid.add(subtotal, 5, 0);
		grid.add(btnRemove, 6, 0);
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(15);
		grid.getColumnConstraints().addAll(new ColumnConstraints(70), new ColumnConstraints(150), new ColumnConstraints(150), new ColumnConstraints(100), new ColumnConstraints(60), new ColumnConstraints(100), new ColumnConstraints(80));
		grid.setHalignment(subtotal, HPos.RIGHT);
		itemQuantity.setFont(Font.font("Arial", FontPosture.ITALIC, 13));
		
		//Constrain user input to numbers for quantity field
		tfQuantity.setTextFormatter(quantityFormatter);
		
		//Update totals based on quantity
		tfQuantity.setOnKeyReleased( e -> {
			preTaxTotal = Double.parseDouble(sub.getText().substring(1)) - subValue;
			if(tfQuantity.getText().equals("")) {
				subValue = 0.0;
			} else {
				subValue = Double.parseDouble(tfQuantity.getText()) * sku.getRetailPrice();
			}
			subtotal.setText(String.valueOf(currencyFormat.format(subValue)));
			sub.setText(String.valueOf(currencyFormat.format(preTaxTotal + subValue)));
			tax.setText(String.valueOf(currencyFormat.format((preTaxTotal + subValue) * myStore.getTaxRate())));
			total.setText(String.valueOf(currencyFormat.format((preTaxTotal + subValue) * (myStore.getTaxRate() + 1.0))));
		});
		
		tfQuantity.requestFocus();
		tfQuantity.selectAll();
	}
	
	//Formatting for currency fields
	NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
	
	//Limits text input to numbers
	UnaryOperator<Change> quantityFilter = change -> {
		String text = change.getText();
		String fullText = change.getControlNewText();
		if(text.matches("[0-9]*") && fullText.length() <= 4) {
			return change;
		} else {
			return null;
		}
	};
	TextFormatter<String> quantityFormatter = new TextFormatter<>(quantityFilter);
}

class DeliveryTimeComparator implements Comparator<Transaction>, java.io.Serializable {
	//Serializable id
	private static final long serialVersionUID = 2295596500494798334L;
	
	//Compares two Transaction objects based on scheduled delivery time
		public int compare(Transaction t1, Transaction t2) {
			LocalDateTime d1 = t1.getDeliveryTime();
			LocalDateTime d2 = t2.getDeliveryTime();
			
			if(d1.isBefore(d2)) {
				return -1;
			} else if (d1.isEqual(d2)) {
				return 0;
			} else {
				return 1;
			}
		}
}