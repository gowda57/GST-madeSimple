package application;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

public class Controller1 implements Initializable {

	@FXML public ComboBox<String> login;
	@FXML public Button register;
	ObservableList<String> obList = FXCollections.observableArrayList("Admin Login", "User Login");
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		login.setItems(obList);
		
	}
	
	public void changeScene(ActionEvent e) throws Exception {
		Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setScene(scene);
		stage.setTitle("Welcome - Login or Register");
		stage.show();
	}
	
	public void registerUser(ActionEvent e) throws Exception {
		(new RegisterController()).changeScene(e);

	}
	
	public void userLogin(ActionEvent e) throws Exception {
		String source = login.getValue();
		
		if(source.equals("Admin Login")) {
			(new Controller2()).changeScene(e);
		}
		else {
			(new Controller3()).changeScene(e);
		}
	}
	
	public void exitApp(ActionEvent e) {
		System.exit(0);
	}
       
}
