package application;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class Items implements Initializable {
	@FXML TableView<Items> tv;
	@FXML TableColumn<Items, String> item_no, item_name;
	@FXML TableColumn<Items, Double> price;
	@FXML TableColumn<Items, Integer> qty, gst;
	SimpleStringProperty item_no1, item_name1;
	SimpleDoubleProperty price1;
	SimpleIntegerProperty qty1, gst1;
	ObservableList<Items> ob = FXCollections.observableArrayList();
	
	public Items() {}
	
	public Items(String item_no, String item_name, Integer qty, Double price, Integer gst) {
		this.item_no1 = new SimpleStringProperty(item_no);
		this.item_name1 = new SimpleStringProperty(item_name);
		this.qty1 = new SimpleIntegerProperty(qty);
		this.price1 = new SimpleDoubleProperty(price);
		this.gst1 = new SimpleIntegerProperty(gst);
	}
	
	
	
	public String getItem_no1() {
		return item_no1.get();
	}

	public String getItem_name1() {
		return item_name1.get();
	}

	public Double getPrice1() {
		return price1.get();
	}

	public Integer getQty1() {
		return qty1.get();
	}

	public Integer getGst1() {
		return gst1.get();
	}

	public void changeScene(ActionEvent e) throws Exception {
		Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("Items.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setScene(scene);
		stage.setTitle("Items");
		stage.show();
	}

	public void backClicked(ActionEvent e) throws Exception {
		(new UserDashboardController()).changeScene(e);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		DBConnection dbc = new DBConnection();
		PreparedStatement st1;
		PreparedStatement st2 = null;
		PreparedStatement st3 = null;
		PreparedStatement st4 = null;
		String query;
		//int temp1 = itemsInStock1();
		//int temp2 = itemsInStock2();
		
		try {
			if(!dbc.connected) dbc.getConnection();
			query = "select distinct item_no from items where type = ? order by item_name";
			st1 = (dbc.con).prepareStatement(query);
			st1.setString(1, "Purchase");
			ResultSet rs1 = st1.executeQuery();
			
			while(rs1.next()) {
				query = "select sum(quantity) from items where type = ? and item_no = ?";
				st2 = (dbc.con).prepareStatement(query);
				st2.setString(1, "Purchase");
				st2.setString(2, rs1.getString(1));
				ResultSet rs2 = st2.executeQuery();
				
				query = "select sum(quantity) from items where type = ? and item_no = ?";
				st3 = (dbc.con).prepareStatement(query);
				st3.setString(1, "Billing");
				st3.setString(2, rs1.getString(1));
				ResultSet rs3 = st3.executeQuery();
				
				query = "select item_name, price, gst from items where item_no = ?";
				st4 = (dbc.con).prepareStatement(query);
				st4.setString(1, rs1.getString(1));
				ResultSet rs4 = st4.executeQuery();
				
				rs2.next();
				rs3.next();
				rs4.next();
				
				Items items = new Items(rs1.getString(1), rs4.getString(1), rs2.getInt(1) - rs3.getInt(1), rs4.getDouble(2), rs4.getInt(3));
				ob.add(items);
				
			}
			
			item_no.setCellValueFactory(new PropertyValueFactory<Items, String>("item_no1"));
			item_name.setCellValueFactory(new PropertyValueFactory<Items, String>("item_name1"));
			qty.setCellValueFactory(new PropertyValueFactory<Items, Integer>("qty1"));
			price.setCellValueFactory(new PropertyValueFactory<Items, Double>("price1"));
			gst.setCellValueFactory(new PropertyValueFactory<Items, Integer>("gst1"));
			
			tv.setItems(ob);
			
			st1.close();
			st2.close();
			st3.close();
			st4.close();
			(dbc.con).close();
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
