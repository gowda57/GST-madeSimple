package application;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JRViewer;

public class Purchase implements Initializable {
	@FXML ComboBox<String> cb;
	@FXML ComboBox<Integer> gst;
	@FXML TextField mobile, email, gstin, inv_no, item_no, item_name;
	@FXML TextField qty, price, cgst_amt, sgst_amt, tot_gst_amt, tot_amt, sum_total;
	@FXML DatePicker inv_date;
	@FXML TableView<Purchase> tv;
	@FXML TableColumn<Purchase, String> item_no1, item_name1;
	@FXML TableColumn<Purchase, Integer> qty1, gst1;
	@FXML TableColumn<Purchase, Double> price1, cgst_amt1, sgst_amt1, tot_gst_amt1, tot_amt1;
	Alert alert;
	SimpleStringProperty item_no2, item_name2;
	SimpleIntegerProperty qty2, gst2;
	SimpleDoubleProperty price2, cgst_amt2, sgst_amt2, tot_gst_amt2, tot_amt2;
	Double total_amt_variable;
	
	public Purchase(String item_no, String item_name, Integer qty, Integer gst, Double price, Double cgst_amt, Double sgst_amt,
			Double tot_gst_amt, Double tot_amt) {
		item_no2 = new SimpleStringProperty(item_no);
		item_name2 = new SimpleStringProperty(item_name);
		qty2 = new SimpleIntegerProperty(qty);
		gst2 = new SimpleIntegerProperty(gst);
		price2 = new SimpleDoubleProperty(price);
		cgst_amt2 = new SimpleDoubleProperty(cgst_amt);
		sgst_amt2 = new SimpleDoubleProperty(sgst_amt);
		tot_gst_amt2 = new SimpleDoubleProperty(tot_gst_amt);
		tot_amt2 = new SimpleDoubleProperty(tot_amt);
	}
	
	public Purchase() {}
	
	public String getItem_no2() {
		return item_no2.get();
	}

	public String getItem_name2() {
		return item_name2.get();
	}

	public Integer getQty2() {
		return qty2.get();
	}

	public Integer getGst2() {
		return gst2.get();
	}

	public Double getPrice2() {
		return price2.get();
	}

	public Double getCgst_amt2() {
		return cgst_amt2.get();
	}

	public Double getSgst_amt2() {
		return sgst_amt2.get();
	}

	public Double getTot_gst_amt2() {
		return tot_gst_amt2.get();
	}

	public Double getTot_amt2() {
		return tot_amt2.get();
	}

	ObservableList<String> ob = FXCollections.observableArrayList();
	ObservableList<Integer> ob1;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		String query = "select name from dealer";
		ResultSet rs;
		try {
			DBConnection dbc = new DBConnection();
			if(!dbc.connected) dbc.getConnection();
			Statement st = (dbc.con).createStatement();
			rs = st.executeQuery(query);
			
			while(rs.next())
				ob.add(rs.getString(1));
			
			cb.setItems(ob);
			
			st.close();
			(dbc.con).close();
		}
		catch(Exception e) {
			
		}
		
		ob1 = FXCollections.observableArrayList(0, 5, 12, 18, 28);
		gst.setItems(ob1);
		
	}

	public void changeScene(ActionEvent e) throws Exception {
		Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("Purchase.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setScene(scene);
		stage.setTitle("Purchases");
		stage.show();
	}
	
	public void backClicked(ActionEvent e) throws Exception {
		(new UserDashboardController()).changeScene(e);
	}
	
	public void dealerClicked(ActionEvent e) {
		//String cbName = cb.getValue();
		//System.out.println(cbName);
		String query = "select mobile_number, email, gstin from dealer where name = ?";
		ResultSet rs;
		
		try {
			DBConnection dbc = new DBConnection();
			if(!dbc.connected) dbc.getConnection();
			PreparedStatement st = (dbc.con).prepareStatement(query);
			st.setString(1, cb.getValue());
			rs = st.executeQuery();
			
			rs.next();
			
            //System.out.println(rs.getString(1));
			mobile.setText(rs.getString(1));
			email.setText(rs.getString(2));
			gstin.setText(rs.getString(3));
			
			st.close();
			(dbc.con).close();
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
		
	}
	
	public void gstClicked(ActionEvent e) {
		try {
			if(qty.getText().equals("")) {
				alert = new Alert(Alert.AlertType.ERROR);
				alert.setContentText("Please fill the Quantity field to calculate total amount.");
				alert.show();
				gst.setPromptText("Select");
				return;
			}
			
			if(price.getText().equals("")) {
				alert = new Alert(Alert.AlertType.ERROR);
				alert.setContentText("Please enter the price to calculate total amount.");
				alert.show();
				gst.setPromptText("Select");
				return;
			}
		}
		catch(Exception exc) {
			
		}
		
		Double total = (Integer.parseInt(qty.getText()) * Double.parseDouble(price.getText()) * gst.getValue()) / 100;
		Double half_total = total / 2;
		cgst_amt.setText(half_total.toString());
		sgst_amt.setText(half_total.toString());
		tot_gst_amt.setText(total.toString());
		total_amt_variable = (Integer.parseInt(qty.getText()) * Double.parseDouble(price.getText())) + total;
		tot_amt.setText(total_amt_variable.toString());
		
	}
	
	public void addClicked(ActionEvent e) {
		DBConnection dbc = null;
		
		
		if(inv_no.getText().equals("") ||
				item_no.getText().equals("") || item_name.getText().equals("") || qty.getText().equals("") ||
				price.getText().equals("")) {
			alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("No fields can be empty. Please check all the fields.");
			alert.show();
			return;
		}
		
		try {
			if(cb.getValue().equals(null) || inv_date.getValue().equals(null) || gst.getValue().equals(null)) {}
		}
		catch(Exception exc) {
			alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("No fields can be empty. Please check all the fields.");
			alert.show();
			return;
		}
		
		try {
			String query = "insert into purchase_controller values(?, ?, ?)";
			dbc = new DBConnection();
			if(!dbc.connected) dbc.getConnection();
			PreparedStatement st = (dbc.con).prepareStatement(query);
			st.setString(1, inv_no.getText());
			st.setString(2, inv_date.getValue().toString());
			st.setString(3, cb.getValue());
			st.executeUpdate();
			
			addToPurchase();
			
			st.close();
		}
		catch(Exception exc) {
			try {
				PreparedStatement st1 = (dbc.con).prepareStatement("select * from purchase_controller where invoice_no = ?");
				st1.setString(1, inv_no.getText());
				ResultSet rs = st1.executeQuery();
				rs.next();
				
				if((inv_date.getValue().toString()).equals(rs.getString(2)) && (cb.getValue()).equals(rs.getString(3)))
					addToPurchase();
				else {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Invoice No. alredy exists. Please check corresponding dealer name and Invoice date.");
					alert.show();
				}
				
				st1.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		try {
			(dbc.con).close();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	
	
	public void addToPurchase() {
		DBConnection dbc = new DBConnection();

		try {
			if(!dbc.connected) dbc.getConnection();
			String query = "{call purchase_proc(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			CallableStatement cs = (dbc.con).prepareCall(query);
			cs.setString(1, cb.getValue());
			cs.setString(2, gstin.getText());
			cs.setString(3, inv_no.getText());
			cs.setString(4, inv_date.getValue().toString());
			cs.setString(5, item_no.getText());
			cs.setString(6, item_name.getText());
			cs.setInt(7, Integer.parseInt(qty.getText()));
			cs.setDouble(8, Double.parseDouble(price.getText()));
			cs.setInt(9, gst.getValue());
			cs.setDouble(10, Double.parseDouble(cgst_amt.getText()));
			cs.setDouble(11, Double.parseDouble(sgst_amt.getText()));
			cs.setDouble(12, Double.parseDouble(tot_gst_amt.getText()));
			cs.setDouble(13, Double.parseDouble(tot_amt.getText()));
			cs.setString(14, "Purchase");
			
			cs.executeQuery();
			
			ResultSet rs;
			PreparedStatement st = (dbc.con).prepareStatement("select sum(total_amt) from purchase where invoice_no = ?");
			st.setString(1, inv_no.getText());
			rs = st.executeQuery();
			
			rs.next();
			
			sum_total.setText(rs.getString(1));
			
			alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setContentText("Item(s) added successfully.");
			alert.show();
			
			populateTable();
			clearTextFields();
			
			cs.close();
			st.close();
			
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
		
		try {
			(dbc.con).close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void populateTable() {
		String query = "select * from purchase where invoice_no = ?";		
		ResultSet rs;
		ObservableList<Purchase> ob2 = FXCollections.observableArrayList();
		
		try {
		DBConnection dbc = new DBConnection();
		if(!dbc.connected) dbc.getConnection();
		PreparedStatement st = (dbc.con).prepareStatement(query);
		st.setString(1, inv_no.getText());
		rs = st.executeQuery();
		
		while(rs.next()) {
			
			ob2.add(new Purchase(rs.getString(5), rs.getString(6), rs.getInt(7), rs.getInt(9), rs.getDouble(8),
					rs.getDouble(10), rs.getDouble(11), rs.getDouble(12), rs.getDouble(13)));
		}
		
		item_no1.setCellValueFactory(new PropertyValueFactory<Purchase, String>("item_no2"));
		item_name1.setCellValueFactory(new PropertyValueFactory<Purchase, String>("item_name2"));
		qty1.setCellValueFactory(new PropertyValueFactory<Purchase, Integer>("qty2"));
		gst1.setCellValueFactory(new PropertyValueFactory<Purchase, Integer>("gst2"));
		price1.setCellValueFactory(new PropertyValueFactory<Purchase, Double>("price2"));
		cgst_amt1.setCellValueFactory(new PropertyValueFactory<Purchase, Double>("cgst_amt2"));
		sgst_amt1.setCellValueFactory(new PropertyValueFactory<Purchase, Double>("sgst_amt2"));
		tot_gst_amt1.setCellValueFactory(new PropertyValueFactory<Purchase, Double>("tot_gst_amt2"));
		tot_amt1.setCellValueFactory(new PropertyValueFactory<Purchase, Double>("tot_amt2"));
		
		tv.setItems(ob2);
		
		st.close();
		(dbc.con).close();
	}
	catch(Exception e) {
		e.printStackTrace();
	}
		
	}
	
	public void clearTextFields() {
		item_no.clear();
		item_name.clear();
		qty.clear();
		price.clear();
		cgst_amt.clear();
		sgst_amt.clear();
		tot_gst_amt.clear();
		tot_amt.clear();
	}
	
	public void printClicked(ActionEvent e) {
		JasperPrint jasperPrint;
		try {
			JasperReport jasperReport = JasperCompileManager
		               .compileReport("C:\\Users\\gowth\\eclipse-workspace\\GST - Invoice and Billing\\src\\application\\Blank_A4.jrxml");
		 
		       
		       Map<String, Object> parameters = new HashMap<String, Object>();
		       parameters.put("invoice_no_para", inv_no.getText());
		       parameters.put("total_inv_amt", Double.parseDouble(sum_total.getText()));
		 
		       DBConnection dbc = new DBConnection();
		       if(!dbc.connected) dbc.getConnection();
		       
		       jasperPrint = JasperFillManager.fillReport(jasperReport,
		               parameters, dbc.con);
		 
		    
		       SwingNode swingNode = new SwingNode();
               swingNode.setContent(new JRViewer(jasperPrint));

               AnchorPane anchorPane = new AnchorPane();
               anchorPane.backgroundProperty();

               AnchorPane.setTopAnchor(swingNode,0.0);
               AnchorPane.setBottomAnchor(swingNode,0.0);
               AnchorPane.setLeftAnchor(swingNode,0.0);
               AnchorPane.setRightAnchor(swingNode,0.0);

               anchorPane.getChildren().add(swingNode);
               Scene scene = new Scene(anchorPane);
               Stage stage = new Stage();
               stage.setHeight(1000);
               stage.setWidth(1100);
               stage.setAlwaysOnTop(true);
               stage.setScene(scene);
               stage.setTitle("Print - Purchase Invoice");
               stage.showAndWait();
		      
		      }
		 catch (Exception e1) {
			   if(inv_no.getText().equals("")) {
				   alert = new Alert(Alert.AlertType.ERROR);
				   alert.setContentText("Invoice number cann't be empty.");
				   alert.show();
				   return;
			   }
			   
			   if(sum_total.getText().equals("")) {
				   alert = new Alert(Alert.AlertType.ERROR);
				   alert.setContentText("Sum Total cann't be empty.");
				   alert.show();
			   }
		}
	}
	
}
