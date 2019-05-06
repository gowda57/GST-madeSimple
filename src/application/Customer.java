package application;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class Customer implements Initializable {
	
	@FXML TableView<Customer> tv;
	@FXML TableColumn<Customer, String> name, address, mobile, email, gstin;
	@FXML TextField name1, address1, mobile1, email1, gstin1;
	//@FXML Label status;
	SimpleStringProperty name2, address2, mobile2, email2, gstin2;
	Alert alert;
	String oldMobile;
	
	public Customer(String name, String address, String mobile, String email, String gstin) {
		this.name2 = new SimpleStringProperty(name);
		this.address2 = new SimpleStringProperty(address);
		this.mobile2 = new SimpleStringProperty(mobile);
		this.email2 = new SimpleStringProperty(email);
		this.gstin2 = new SimpleStringProperty(gstin);
	}
	
	public String getName2() {
		return name2.get();
	}

	public String getAddress2() {
		return address2.get();
	}

	public String getMobile2() {
		return mobile2.get();
	}

	public String getEmail2() {
		return email2.get();
	}

	public String getGstin2() {
		return gstin2.get();
	}

	public Customer() {}
	
	public void changeScene(ActionEvent e) throws Exception {
		Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("Customer.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setScene(scene);
		stage.setTitle("Customers");
		stage.show();
	}
	
	public void populateTable() {
		String query = "select * from customer";		
		ResultSet rs;
		ObservableList<Customer> ob = FXCollections.observableArrayList();
		
		try {
		DBConnection dbc = new DBConnection();
		if(!dbc.connected) dbc.getConnection();
		Statement st = (dbc.con).createStatement();
		rs = st.executeQuery(query);
		
		while(rs.next()) {
			
			ob.add(new Customer(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)));
		}
		
		name.setCellValueFactory(new PropertyValueFactory<Customer, String>("name2"));
		address.setCellValueFactory(new PropertyValueFactory<Customer, String>("address2"));
		mobile.setCellValueFactory(new PropertyValueFactory<Customer, String>("mobile2"));
		email.setCellValueFactory(new PropertyValueFactory<Customer, String>("email2"));
		gstin.setCellValueFactory(new PropertyValueFactory<Customer, String>("gstin2"));
		
		tv.setItems(ob);
		
		st.close();
		(dbc.con).close();
	}
	catch(Exception e) {
		
	}
		
	}
	
	public void backClicked(ActionEvent e) throws Exception {
		(new UserDashboardController()).changeScene(e);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
			populateTable();
	}
	

	public void addClicked(ActionEvent e) {
		//System.out.println("IFYTFTYTR");
		//status.setText("");
		
		if(name1.getText().equals("")) {
			alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("Name can't be empty.");
			alert.show();
			return;
		}
		
		if(address1.getText().equals("")) {
			alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("Address can't be empty.");
			alert.show();
			//status.setText("Address can't be empty.");
			return;
		}
		
		if(mobile1.getText().equals("")) {
			alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("Mob Number can't be empty.");
			alert.show();
			//status.setText("Mob number can't be empty.");
			return;
		}
		
		if(email1.getText().equals("")) {
			alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("Email can't be empty.");
			alert.show();
			//status.setText("Email can't be empty.");
			return;
		}
		
		String query = "insert into customer values(?, ?, ?, ?, ?)";
		PreparedStatement st;
		
		try {
			DBConnection dbc = new DBConnection();
			if(!dbc.connected) dbc.getConnection();
			st = (dbc.con).prepareStatement(query);
			st.setString(1, name1.getText());
			st.setString(2, address1.getText());
			st.setString(3, mobile1.getText());
			st.setString(4, email1.getText());
			st.setString(5, gstin1.getText());
			st.executeUpdate();
			//status.setText("Custumer has been added successfully.");
			alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setContentText("Custumer has been added successfully.");
			alert.show();
			
			clearTextFields();
			
			st.close();
			(dbc.con).close();
			
			populateTable();
		}
		catch(Exception exc) {
		    //status.setText("Entered GSTIN already exists.");
			alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("Couldn't add the customer. Entered Mobile Number already exists.");
			alert.show();
		}
		
	}
	
	public void deleteClicked(ActionEvent e) {
		
		Customer c = tv.getSelectionModel().getSelectedItem();
		String query = "delete from customer where mobile_number = ?";
		PreparedStatement st;
		
		try {
		DBConnection dbc = new DBConnection();
		if(!dbc.connected) dbc.getConnection();
		st = (dbc.con).prepareStatement(query);
		st.setString(1, c.getMobile2());
		st.executeUpdate();
		//tv.getItems().removeAll(tv.getSelectionModel().getSelectedItems());
		
		//status.setText(c.getName2() + " has been removed.");
		alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setContentText(c.getName2() + " has been removed successfully.");
		alert.show();
		
		st.close();
		(dbc.con).close();
		
		populateTable();
		
		}
		catch(Exception exc) {
			alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("First select a customer from the table and then click 'Delete Customer' button to delete.");
			alert.show();
		}
		
	}
	
	public void editClicked(ActionEvent e) throws Exception {
		try {
		Customer c = tv.getSelectionModel().getSelectedItem();
		
		name1.setText(c.getName2());
		address1.setText(c.getAddress2());
		mobile1.setText(c.getMobile2());
		email1.setText(c.getEmail2());
		gstin1.setText(c.getGstin2());
		oldMobile = c.getMobile2();
		
		alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setContentText("After editing the fields, Click on 'Update' button to update the details.");
		alert.show();
		}
		catch(Exception exc) {
			alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("First select a customer from the table and then click 'Edit Customer' button to edit.");
			alert.show();
		}
	
	}
	
	public void updateClicked(ActionEvent e) {
		try {
			if(name1.getText().equals("")) {
				alert = new Alert(Alert.AlertType.ERROR);
				alert.setContentText("Name can't be empty.");
				alert.show();
				return;
			}
			
			if(address1.getText().equals("")) {
				alert = new Alert(Alert.AlertType.ERROR);
				alert.setContentText("Address can't be empty.");
				alert.show();
				//status.setText("Address can't be empty.");
				return;
			}
			
			if(mobile1.getText().equals("")) {
				alert = new Alert(Alert.AlertType.ERROR);
				alert.setContentText("Mob Number can't be empty.");
				alert.show();
				//status.setText("Mob number can't be empty.");
				return;
			}
			
			if(email1.getText().equals("")) {
				alert = new Alert(Alert.AlertType.ERROR);
				alert.setContentText("Email can't be empty.");
				alert.show();
				//status.setText("Email can't be empty.");
				return;
			}
		}
		catch(Exception exc) {
			alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("First select a customer from the table and then click 'Update' button to update.");
			alert.show();
		}
		
		String query = "update customer set name=?, address=?, mobile_number=?, email=?, gstin=? where mobile_number=?";
		PreparedStatement st;
		
		try {
			DBConnection dbc = new DBConnection();
			if(!dbc.connected) dbc.getConnection();
			st = (dbc.con).prepareStatement(query);
			st.setString(1, name1.getText());
			st.setString(2, address1.getText());
			st.setString(3, mobile1.getText());
			st.setString(4, email1.getText());
			st.setString(5, gstin1.getText());
			st.setString(6, oldMobile);
			st.executeUpdate();

			//status.setText("Custumer details updated successfully.");
			alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setContentText("Custumer details has been updated successfully.");
			alert.show();
			
			st.close();
			(dbc.con).close();
		
			clearTextFields();
			populateTable();
		}
		catch(Exception exc) {
			alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("Couldn't update customer details. Changed Mobile Number already exists.");
			alert.show();
		}
	}
	
	public void clearTextFields() {

		name1.clear();
		address1.clear();
		mobile1.clear();
		email1.clear();
		gstin1.clear();
	}
	
}
