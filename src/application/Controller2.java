package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Controller2 {
	
	@FXML TextField admin_id, password;
	@FXML Button back;
	@FXML Label status;

	public void changeScene(ActionEvent e) throws Exception {
		Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("Admin-login.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setScene(scene);
		stage.setTitle("Admin Login");
		stage.show();
	}
	
	public void loginClicked(ActionEvent e) throws Exception {
		if(admin_id.getText().equals("")) {
			status.setText("Admin Id cannot be empty.");
			return;
		}
		if(password.getText().equals("")) {
			status.setText("Password cannot be empty.");
			return;
		}
		
		Statement st;
		String query = "select adminid, pword from admin", pword = "";
		String adminid = "";
		DBConnection dbc = new DBConnection();
		if(!dbc.connected) dbc.getConnection();
		st = (dbc.con).createStatement();
		ResultSet rs = st.executeQuery(query);
		
		while(rs.next()) {
			if(rs.getString(1).equals(admin_id.getText())) {
			adminid = rs.getString(1);
			pword = rs.getString(2);
			break;
			//System.out.println(adminid);
			}
			
		}
		
		st.close();
		
		if((admin_id.getText()).equals(adminid) && (password.getText()).equals(pword)) {
			status.setText("Login Successful");
			
			query = "update admin set admin_logged = 1 where adminid = ?";
			PreparedStatement st1 = (dbc.con).prepareStatement(query);
			st1.setString(1, adminid);
			st1.executeUpdate();
			
			st1.close();
			(dbc.con).close();
			
			(new AdminDashboardController()).changeScene(e);
		}
		else
			status.setText("Login Failed. Check your AdminID/Password");
		
		(dbc.con).close();
		
	}
	
	public void backClicked(ActionEvent e) throws Exception {
		(new Controller1()).changeScene(e);
	}
}
