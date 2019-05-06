package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class GstWeb implements Initializable {
	
	@FXML WebView wv;
	private WebEngine we;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		we = wv.getEngine();
		
		we.load("https://www.incometaxindiaefiling.gov.in/home");
		
	}
	
	public void changeScene() throws Exception {
		Stage stage = new Stage();
		Parent root = FXMLLoader.load(getClass().getResource("GstWeb.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setScene(scene);
		stage.setTitle("eFiling Portal - Income Tax of India");
		stage.show();
	}
}
