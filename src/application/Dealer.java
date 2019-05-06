
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

public class Dealer implements Initializable {
	
	@FXML TableView<Dealer> tv;
	@FXML TableColumn<Dealer, String> name, address, mobile, email, gstin;
	@FXML TextField name1, address1, mobile1, email1, gstin1;
	//@FXML Label status;
	SimpleStringProperty name2, address2, mobile2, email2, gstin2;
	Alert alert;
	String oldGstin;
	
	public Dealer(String name, String address, String mobile, String email, String gstin) {
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

	public Dealer() {}
	
	public void changeScene(ActionEvent e) throws Exception {
		Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("Dealer.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setScene(scene);
		stage.setTitle("Dealers");
		stage.show();
	}
	
	public void populateTable() {
		String query = "select * from dealer";		
		ResultSet rs;
		ObservableList<Dealer> ob = FXCollections.observableArrayList();
		
		try {
		DBConnection dbc = new DBConnection();
		if(!dbc.connected) dbc.getConnection();
		Statement st = (dbc.con).createStatement();
		rs = st.executeQuery(query);
		
		while(rs.next()) {
			
			ob.add(new Dealer(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)));
		}
		
		name.setCellValueFactory(new PropertyValueFactory<Dealer, String>("name2"));
		address.setCellValueFactory(new PropertyValueFactory<Dealer, String>("address2"));
		mobile.setCellValueFactory(new PropertyValueFactory<Dealer, String>("mobile2"));
		email.setCellValueFactory(new PropertyValueFactory<Dealer, String>("email2"));
		gstin.setCellValueFactory(new PropertyValueFactory<Dealer, String>("gstin2"));
		
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
		
		if(gstin1.getText().equals("")) {
			alert = new Alert(Alert.AlertType.ERROR);
		    alert.setContentText("GSTIN can't be empty.");
	     	alert.show();
			//status.setText("GSTIN can't be empty.");
			return;
		}
		
		String query = "insert into dealer values(?, ?, ?, ?, ?)";
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
			alert.setContentText("Dealer has been added successfully.");
			alert.show();
			
			clearTextFields();
			
			st.close();
			(dbc.con).close();
			
			populateTable();
		}
		catch(Exception exc) {
		    //status.setText("Entered GSTIN already exists.");
			alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("Couldn't add the dealer. Entered GSTIN already exists.");
			alert.show();
		}
		
	}
	
	public void deleteClicked(ActionEvent e) {
		
		Dealer c = tv.getSelectionModel().getSelectedItem();
		String query = "delete from dealer where gstin = ?";
		PreparedStatement st;
		
		try {
		DBConnection dbc = new DBConnection();
		if(!dbc.connected) dbc.getConnection();
		st = (dbc.con).prepareStatement(query);
		st.setString(1, c.getGstin2());
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
			alert.setContentText("First select a dealer from the table and then click 'Delete Dealer' button to delete.");
			alert.show();
		}
		
	}
	
	public void editClicked(ActionEvent e) throws Exception {
		try {
		Dealer c = tv.getSelectionModel().getSelectedItem();
		
		name1.setText(c.getName2());
		address1.setText(c.getAddress2());
		mobile1.setText(c.getMobile2());
		email1.setText(c.getEmail2());
		gstin1.setText(c.getGstin2());
		oldGstin = c.getGstin2();
		
		alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setContentText("After editing the fields, Click on 'Update' button to update the details.");
		alert.show();
		}
		catch(Exception exc) {
			alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("First select a dealer from the table and then click 'Edit Dealer' button to edit.");
			alert.show();
		}
	
	}
	
	public void updateClicked(ActionEvent e) {
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
		
		if(gstin1.getText().equals("")) {
			alert = new Alert(Alert.AlertType.ERROR);
		    alert.setContentText("GSTIN can't be empty.");
	     	alert.show();
			//status.setText("GSTIN can't be empty.");
			return;
		}
		
		
		String query = "update dealer set name=?, address=?, mobile_number=?, email=?, gstin=? where gstin=?";
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
			st.setString(6, oldGstin);
			st.executeUpdate();

			//status.setText("Custumer details updated successfully.");
			alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setContentText("Dealer details have been updated successfully.");
			alert.show();
			
			st.close();
			(dbc.con).close();
		
			clearTextFields();
			populateTable();
		}
		catch(Exception exc) {
			alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("Couldn't update dealer details. GSTIN already exists.");
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
