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

public class UserDashboardController implements Initializable {
	@FXML Label greetings;
	
	public void changeScene(ActionEvent e) throws Exception {
		Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("Dashboard - User.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setScene(scene);
		stage.setTitle("User DashBoard");
		stage.show();
	}
	
    public void customerClicked(ActionEvent e) throws Exception {
    	(new Customer()).changeScene(e);
    }

    public void dealerClicked(ActionEvent e) throws Exception {
    	(new Dealer()).changeScene(e);
    }
    
    public void purchaseClicked(ActionEvent e) throws Exception {
    	(new Purchase()).changeScene(e);
    }

    public void itemsClicked(ActionEvent e) throws Exception {
    	(new Items()).changeScene(e);
    }

    public void billingClicked(ActionEvent e) throws Exception {
    	(new Billing()).changeScene(e);
    }

    public void reportsClicked(ActionEvent e) throws Exception {
    	(new Reports()).changeScene(e);
    }

    public void logoutClicked(ActionEvent e) throws Exception {
    	(new Controller3()).changeScene(e);
    	
    	DBConnection dbc = new DBConnection();
    	
    	try {
    		if(!dbc.connected) dbc.getConnection();
    		PreparedStatement st = (dbc.con).prepareStatement("update user_login set user_logged = 0");
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
			ResultSet rs = st.executeQuery("select name from user_login where user_logged = 1");
			
			rs.next();
			
			greetings.setText("Welcome, " + rs.getString(1));
			
			st.close();
			(dbc.con).close();
	    }
	    catch(Exception e) {
	    	
	    }
		
	}
    
}
