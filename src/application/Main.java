package application;
	
import java.sql.PreparedStatement;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Welcome - Login or Register");
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void stop() throws Exception {
		DBConnection dbc = new DBConnection();
		if(!dbc.connected) dbc.getConnection();
		PreparedStatement st = (dbc.con).prepareStatement("update user_login set user_logged = 0");
		st.executeUpdate();
		st.close();
		
		PreparedStatement st1 = (dbc.con).prepareStatement("update admin set admin_logged = 0");
		st1.executeUpdate();
		st1.close();
		
		
		(dbc.con).close();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
