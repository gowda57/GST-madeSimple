package application;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JRViewer;

public class Billing implements Initializable {
	@FXML ComboBox<String> cb, item_name;
	@FXML ComboBox<Integer> qty;
	@FXML TextField mobile, email, gstin, inv_no, item_no;
	@FXML TextField gst, price, cgst_amt, sgst_amt, tot_gst_amt, tot_amt, sum_total;
	@FXML DatePicker inv_date;
	@FXML TableView<Billing> tv;
	@FXML TableColumn<Billing, String> item_no1, item_name1;
	@FXML TableColumn<Billing, Integer> qty1, gst1;
	@FXML TableColumn<Billing, Double> price1, cgst_amt1, sgst_amt1, tot_gst_amt1, tot_amt1;
	Alert alert;
	SimpleStringProperty item_no2, item_name2;
	SimpleIntegerProperty qty2, gst2;
	SimpleDoubleProperty price2, cgst_amt2, sgst_amt2, tot_gst_amt2, tot_amt2;
	Double total_amt_variable;
	
	public void changeScene(ActionEvent e) throws Exception {
		Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("Billing.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setScene(scene);
		stage.setTitle("Billing");
		stage.show();
	}
	
	public void backClicked(ActionEvent e) throws Exception {
		(new UserDashboardController()).changeScene(e);
	}
	
	public Billing(String item_no, String item_name, Integer qty, Integer gst, Double price, Double cgst_amt, Double sgst_amt,
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
	
	public Billing() {}
	
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
	ObservableList<String> ob2 = FXCollections.observableArrayList();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		String query = "select name from customer";
		ResultSet rs;
		try {
			DBConnection dbc = new DBConnection();
			if(!dbc.connected) dbc.getConnection();
			Statement st = (dbc.con).createStatement();
			rs = st.executeQuery(query);
			
			while(rs.next())
				ob.add(rs.getString(1));
			
			cb.setItems(ob);
			
			query = "select * from billing_view";
			Statement st1 = (dbc.con).createStatement();
			ResultSet rs1 = st1.executeQuery(query);
			rs1.next();
			int count = rs1.getInt(1);
			count++;
			
			//inv_date.setValue(1998-12-11');
			if(count<10)
				inv_no.setText("INV000" + count);
			else if(count>9 && count<100)
				inv_no.setText("INV00" + count);
			else if(count>99 && count<1000)
				inv_no.setText("INV0" + count);
			else if(count>999 && count<10000)
				inv_no.setText("INV" + count);
			
			query = "select distinct item_name from items where type = ? order by item_name";
			PreparedStatement st2 = (dbc.con).prepareStatement(query);
			st2.setString(1, "Purchase");
			ResultSet rs2 = st2.executeQuery();
			//rs2.next();
			
			while(rs2.next())
				ob2.add(rs2.getString(1));
			
			item_name.setItems(ob2);
			
			st.close();
			st1.close();
			st2.close();
			(dbc.con).close();
			
			LocalDate date = LocalDate.now();
			inv_date.setValue(date);
			inv_date.setDisable(true);
		}
		catch(Exception e) {
			
		}
	}
	
	public void customerClicked(ActionEvent e) {
		//String cbName = cb.getValue();
		//System.out.println(cbName);
		String query = "select mobile_number, email, gstin from customer where name = ?";
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
	
	public void item_nameClicked(ActionEvent e) {
		String query = "select item_no, gst, price from items where item_name = ?";
		DBConnection dbc = new DBConnection();
		try {
			if(!dbc.connected) dbc.getConnection();
			PreparedStatement st = (dbc.con).prepareStatement(query);
			st.setString(1, item_name.getValue());
			ResultSet rs = st.executeQuery();
			rs.next();
			item_no.setText(rs.getString(1));
			gst.setText(rs.getString(2));
			price.setText(rs.getString(3));
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
		ObservableList<Integer> ob3 = FXCollections.observableArrayList();
		int temp1=0, temp2=0;
			
			try {
				query = "select sum(quantity) from items where item_name = ? and type = ?";
				PreparedStatement st1 = (dbc.con).prepareStatement(query);
				st1.setString(1, item_name.getValue());
				st1.setString(2, "Purchase");
				ResultSet rs1 = st1.executeQuery();
				rs1.next();
				
				//System.out.println(rs1.getInt(1));
				temp1 = rs1.getInt(1);
			}
			catch(Exception exc) {
				exc.printStackTrace();
			}
			
			try {
				query = "select sum(quantity) from items where item_name = ? and type = ?";
				PreparedStatement st2 = (dbc.con).prepareStatement(query);
				st2.setString(1, item_name.getValue());
				st2.setString(2, "Billing");
				ResultSet rs2 = st2.executeQuery();
				rs2.next();
				temp2 = rs2.getInt(1);
			}
			catch(Exception exc) {
				exc.printStackTrace();
			}
			
			int temp = temp1 - temp2;
			//System.out.println(temp);
			for(int i=1;i<=temp;i++)
				ob3.add(i);
			qty.setItems(ob3);
		
	}
	
	public void qtyClicked(ActionEvent e) {
		try {
			if(item_name.getValue().equals(null)) {}
		}
		catch(Exception exc) {
			alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("Please select an item from the drop down.");
			alert.show();
			qty.setPromptText("Select");
			return;
		}
		
		try {
			Double total = (qty.getValue() * Double.parseDouble(price.getText()) * Integer.parseInt(gst.getText())) / 100;
			Double half_total = total / 2;
			cgst_amt.setText(half_total.toString());
			sgst_amt.setText(half_total.toString());
			tot_gst_amt.setText(total.toString());
			total_amt_variable = (qty.getValue() * Double.parseDouble(price.getText())) + total;
			tot_amt.setText(total_amt_variable.toString());
		}
		catch(Exception exc) {
			
		}
		
	}
	
	public void addClicked(ActionEvent e) {
		
		if(item_no.getText().equals("") || gst.getText().equals("") ||
				price.getText().equals("")) {
			alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("No fields can be empty. Please check all the fields.");
			alert.show();
			return;
		}
		
		try {
			if(cb.getValue().equals(null) || qty.getValue().equals(null) || item_name.getValue().equals(null)) {}
		}
		catch(Exception exc) {
			alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("No fields can be empty. Please check all the fields.");
			alert.show();
			return;
		}
		
		DBConnection dbc = new DBConnection();

		try {
			if(!dbc.connected) dbc.getConnection();
			String query = "{call billing_proc(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			CallableStatement cs = (dbc.con).prepareCall(query);
			cs.setString(1, cb.getValue());
			cs.setString(2, mobile.getText());
			cs.setString(3, inv_no.getText());
			cs.setString(4, inv_date.getValue().toString());
			cs.setString(5, item_no.getText());
			cs.setString(6, item_name.getValue());
			cs.setInt(7, qty.getValue());
			cs.setDouble(8, Double.parseDouble(price.getText()));
			cs.setInt(9, Integer.parseInt(gst.getText()));
			cs.setDouble(10, Double.parseDouble(cgst_amt.getText()));
			cs.setDouble(11, Double.parseDouble(sgst_amt.getText()));
			cs.setDouble(12, Double.parseDouble(tot_gst_amt.getText()));
			cs.setDouble(13, Double.parseDouble(tot_amt.getText()));
			cs.setString(14, "Billing");
			
			cs.executeQuery();
			
			ResultSet rs;
			PreparedStatement st = (dbc.con).prepareStatement("select sum(total_amt) from billing where invoice_no = ?");
			st.setString(1, inv_no.getText());
			rs = st.executeQuery();
			
			rs.next();
			
			sum_total.setText(rs.getString(1));
			
			cb.setDisable(true);
			//inv_date.setDisable(true);
			
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
		} catch (SQLException exc) {
			exc.printStackTrace();
			//alert = new Alert(Alert.AlertType.ERROR);
			//alert.setContentText("Date cann't be empty.");
			//alert.show();
		}
	}
	
	public void populateTable() {
		String query = "select * from billing where invoice_no = ?";		
		ResultSet rs;
		ObservableList<Billing> ob2 = FXCollections.observableArrayList();
		
		try {
		DBConnection dbc = new DBConnection();
		if(!dbc.connected) dbc.getConnection();
		PreparedStatement st = (dbc.con).prepareStatement(query);
		st.setString(1, inv_no.getText());
		rs = st.executeQuery();
		
		while(rs.next()) {
			
			ob2.add(new Billing(rs.getString(5), rs.getString(6), rs.getInt(7), rs.getInt(9), rs.getDouble(8),
					rs.getDouble(10), rs.getDouble(11), rs.getDouble(12), rs.getDouble(13)));
		}
		
		item_no1.setCellValueFactory(new PropertyValueFactory<Billing, String>("item_no2"));
		item_name1.setCellValueFactory(new PropertyValueFactory<Billing, String>("item_name2"));
		qty1.setCellValueFactory(new PropertyValueFactory<Billing, Integer>("qty2"));
		gst1.setCellValueFactory(new PropertyValueFactory<Billing, Integer>("gst2"));
		price1.setCellValueFactory(new PropertyValueFactory<Billing, Double>("price2"));
		cgst_amt1.setCellValueFactory(new PropertyValueFactory<Billing, Double>("cgst_amt2"));
		sgst_amt1.setCellValueFactory(new PropertyValueFactory<Billing, Double>("sgst_amt2"));
		tot_gst_amt1.setCellValueFactory(new PropertyValueFactory<Billing, Double>("tot_gst_amt2"));
		tot_amt1.setCellValueFactory(new PropertyValueFactory<Billing, Double>("tot_amt2"));
		
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
		item_name.setPromptText("Select Item");
		qty.setPromptText("Select");
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
		               .compileReport("C:\\Users\\gowth\\eclipse-workspace\\GST - Invoice and Billing\\src\\application\\Billing.jrxml");
		 
		       
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
               stage.setTitle("Print - Billing Invoice");
               stage.showAndWait();
               
               (dbc.con).close();
		      
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
			  // e1.printStackTrace();
		}
	}
}
