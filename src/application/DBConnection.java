package application;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
        public boolean connected = false;
        //Statement st;
        Connection con;
        
        public void getConnection() throws Exception {
        	Class.forName("com.mysql.cj.jdbc.Driver");
        	String url = "jdbc:mysql://localhost:3308/gst_database?autoReconnect=true&useSSL=false";
        	String uname = "root";
   			String pass = "";
        	con = DriverManager.getConnection(url, uname, pass);
        	//st = con.createStatement();  
        	
        	connected = true;
		}
}
