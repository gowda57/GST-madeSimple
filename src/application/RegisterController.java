package application;

import java.sql.PreparedStatement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {
	@FXML TextField name, mobile, email, address, userid;
	@FXML TextField password;
	@FXML Label status;
	
	public void changeScene(ActionEvent e) throws Exception {
		Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("Register.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setScene(scene);
		stage.setTitle("Registration");
		stage.show();
	}
	
	public void registerButton(ActionEvent e) {
		//int temp = -1;
		
		if(userid.getText().equals("")) {
			status.setText("UserID cannot be empty.");
			return;
		}
		if(name.getText().equals("")) {
			status.setText("Name cannot be empty.");
			return;
		}
		if(mobile.getText().equals("")) {
			status.setText("Mobile Number cannot be empty.");
			return;
		}
		if(email.getText().equals("")) {
			status.setText("Email ID cannot be empty.");
			return;
		}
		if(address.getText().equals("")) {
			status.setText("Address cannot be empty.");
			return;
		}
		if(password.getText().equals("")) {
			status.setText("Password cannot be empty.");
			return;
		}
		
		try {
		String query = "insert into user_login values(?,?,?,?,?,?,?)";
		DBConnection dbc = new DBConnection();
		if(!dbc.connected) dbc.getConnection();
		PreparedStatement st = (dbc.con).prepareStatement(query);
		st.setString(1, userid.getText());
		st.setString(2, name.getText());
		st.setString(3, mobile.getText());
		st.setString(4, email.getText());
		st.setString(5, address.getText());
		st.setInt(6, 0);
		st.setString(7, password.getText());
		st.executeUpdate();
		status.setText("Registered Successfully.");
		
		userid.clear();
		name.clear();
		mobile.clear();
		email.clear();
		address.clear();
		password.clear();
		//gstin.clear();
		
		st.close();
		(dbc.con).close();
		
		}
		catch(Exception exc) {
			status.setText("UserID already exists.");
			exc.printStackTrace();
		}
		
	}
	
	public void backClicked(ActionEvent e) throws Exception {
		(new Controller1()).changeScene(e);
	}

}
