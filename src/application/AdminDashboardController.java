package application;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AdminDashboardController implements Initializable {
	@FXML Label greetings;
	
	public void changeScene(ActionEvent e) throws Exception {
		Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("Dashboard - Admin.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setScene(scene);
		stage.setTitle("Admin DashBoard");
		stage.show();
	}
	
	public void userInfoClicked(ActionEvent e) throws Exception {
		(new UserInfo()).changeScene(e);
	}
	
	public void reportsClicked(ActionEvent e) throws Exception {
		(new ReportsAdmin()).changeScene(e);
	}
	
	public void returnsClicked(ActionEvent e) throws Exception {
		(new GstWeb()).changeScene();
	}
	
	public void logoutClicked(ActionEvent e) throws Exception {
		(new Controller2()).changeScene(e);

    	DBConnection dbc = new DBConnection();
    	
    	try {
    		if(!dbc.connected) dbc.getConnection();
    		PreparedStatement st = (dbc.con).prepareStatement("update admin set admin_logged = 0");
    		st.executeUpdate();
    		
    		st.close();
    		(dbc.con).close();
    	}
    	catch(Exception exc) {
    		
    	}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	    DBConnection dbc = new DBConnection();
	    
	    try {
	    	if(!dbc.connected) dbc.getConnection();
			Statement st = (dbc.con).createStatement();
			ResultSet rs = st.executeQuery("select name from admin where admin_logged = 1");
			
			rs.next();
			
			greetings.setText("Welcome, " + rs.getString(1));
			
			st.close();
			(dbc.con).close();
	    }
	    catch(Exception e) {
	    	
	    }
		
	}
}
