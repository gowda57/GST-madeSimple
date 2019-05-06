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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class UserInfo implements Initializable {
	
	@FXML TableView<UserInfo> tv;
	@FXML TableColumn<UserInfo, String> name, address, mobile, email, userid;
	//@FXML TextField name1, address1, mobile1, email1, userid1;
	//@FXML Label status;
	SimpleStringProperty name2, address2, mobile2, email2, userid2;
	Alert alert;
	String oldId;
	
	public UserInfo(String name, String address, String mobile, String email, String userid) {
		this.name2 = new SimpleStringProperty(name);
		this.address2 = new SimpleStringProperty(address);
		this.mobile2 = new SimpleStringProperty(mobile);
		this.email2 = new SimpleStringProperty(email);
		this.userid2 = new SimpleStringProperty(userid);
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

	public String getUserid2() {
		return userid2.get();
	}

	public UserInfo() {}
	
	public void changeScene(ActionEvent e) throws Exception {
		Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("UserInfo.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setScene(scene);
		stage.setTitle("Users Details");
		stage.show();
	}
	
	public void populateTable() {
		String query = "select * from user_login";		
		ResultSet rs;
		ObservableList<UserInfo> ob = FXCollections.observableArrayList();
		
		try {
		DBConnection dbc = new DBConnection();
		if(!dbc.connected) dbc.getConnection();
		Statement st = (dbc.con).createStatement();
		rs = st.executeQuery(query);
		
		while(rs.next()) {
			
			ob.add(new UserInfo(rs.getString(2), rs.getString(5), rs.getString(3), rs.getString(4), rs.getString(1)));
		}
		
		name.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("name2"));
		address.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("address2"));
		mobile.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("mobile2"));
		email.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("email2"));
		userid.setCellValueFactory(new PropertyValueFactory<UserInfo, String>("userid2"));
		
		tv.setItems(ob);
		
		st.close();
		(dbc.con).close();
	}
	catch(Exception e) {
		
	}
		
	}
	
	public void backClicked(ActionEvent e) throws Exception {
		(new AdminDashboardController()).changeScene(e);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
			populateTable();
	}
	
public void deleteClicked(ActionEvent e) {
		
		UserInfo c = tv.getSelectionModel().getSelectedItem();
		String query = "delete from user_login where userid = ?";
		PreparedStatement st;
		
		try {
		DBConnection dbc = new DBConnection();
		if(!dbc.connected) dbc.getConnection();
		st = (dbc.con).prepareStatement(query);
		st.setString(1, c.getUserid2());
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
			alert.setContentText("First select a user from the table and then click 'Delete User' button to delete.");
			alert.show();
		}
		
	}

}
