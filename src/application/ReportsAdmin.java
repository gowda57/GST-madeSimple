package application;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleDoubleProperty;
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

public class ReportsAdmin implements Initializable {
	@FXML ComboBox<String> cb;
	@FXML TextField total_amt;
	@FXML DatePicker from, to;
	@FXML TableView<ReportsAdmin> tv;
	@FXML TableColumn<ReportsAdmin, String> invoice_no, invoice_date;
	@FXML TableColumn<ReportsAdmin, Double> cgst_amt, sgst_amt, tot_gst_amt, tot_amt;
	Alert alert;
	SimpleStringProperty invoice_no2, invoice_date2;
	SimpleDoubleProperty cgst_amt2, sgst_amt2, tot_gst_amt2, tot_amt2;
	ObservableList<String> ob = FXCollections.observableArrayList("Purchase", "Billing");
	
	public ReportsAdmin(String invoice_no2, String invoice_date2, Double cgst_amt2, Double sgst_amt2, Double tot_gst_amt2,
			Double tot_amt2) {
		super();
		this.invoice_no2 = new SimpleStringProperty(invoice_no2);
		this.invoice_date2 = new SimpleStringProperty(invoice_date2);
		this.cgst_amt2 = new SimpleDoubleProperty(cgst_amt2);
		this.sgst_amt2 = new SimpleDoubleProperty(sgst_amt2);
		this.tot_gst_amt2 = new SimpleDoubleProperty(tot_gst_amt2);
		this.tot_amt2 = new SimpleDoubleProperty(tot_amt2);
	}
	
	public ReportsAdmin() {}

	public String getInvoice_no2() {
		return invoice_no2.get();
	}
	
    public String getInvoice_date2() {
		return invoice_date2.get();
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

	public void changeScene(ActionEvent e) throws Exception {
		Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("ReportsAdmin.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setScene(scene);
		stage.setTitle("Reports");
		stage.show();
	}

	public void backClicked(ActionEvent e) throws Exception {
		(new AdminDashboardController()).changeScene(e);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		cb.setItems(ob);
		
		DBConnection dbc = new DBConnection();
		String query;
		
		try {
			if(!dbc.connected) dbc.getConnection();
			query = "{call reports_proc()}";
			CallableStatement cs = (dbc.con).prepareCall(query);
			ResultSet rs = cs.executeQuery();
			
			populateTable(rs);
			
			cs.close();
			(dbc.con).close();
		}
		catch(Exception e) {
		    e.printStackTrace();	
		}
	}
	
	public void populateTable(ResultSet rs) {
		Double total = 0.0;
		ObservableList<ReportsAdmin> ob1 = FXCollections.observableArrayList();
		
		try {
			while(rs.next()) {
				ReportsAdmin reports = new ReportsAdmin(rs.getString(1), rs.getString(2), rs.getDouble(3), 
						rs.getDouble(4), rs.getDouble(5), rs.getDouble(6));
				ob1.add(reports);
				
				total += rs.getDouble(6);
			}
		}
		catch(Exception e) {
			
		}
		
		invoice_no.setCellValueFactory(new PropertyValueFactory<ReportsAdmin, String>("invoice_no2"));
		invoice_date.setCellValueFactory(new PropertyValueFactory<ReportsAdmin, String>("invoice_date2"));
		cgst_amt.setCellValueFactory(new PropertyValueFactory<ReportsAdmin, Double>("cgst_amt2"));
		sgst_amt.setCellValueFactory(new PropertyValueFactory<ReportsAdmin, Double>("sgst_amt2"));
		tot_gst_amt.setCellValueFactory(new PropertyValueFactory<ReportsAdmin, Double>("tot_gst_amt2"));
		tot_amt.setCellValueFactory(new PropertyValueFactory<ReportsAdmin, Double>("tot_amt2"));
		
		tv.setItems(ob1);
		total_amt.setText(total.toString());
	}
	
	public void sortClicked(ActionEvent e) {
		String query = "";
		if(cb.getValue().equals("Purchase")) {
			query = "SELECT invoice_no, invoice_date, sum(cgst), "
					+ "sum(sgst), sum(total_gst_amt), sum(total_amt) FROM purchase GROUP by invoice_no";
		}
		else if(cb.getValue().equals("Billing")) {
			query = "SELECT invoice_no, invoice_date, sum(cgst), "
					+ "sum(sgst), sum(total_gst_amt), sum(total_amt) FROM billing GROUP by invoice_no";
		}
		
		DBConnection dbc = new DBConnection();
		Statement st;
		
		try {
			if(!dbc.connected) dbc.getConnection();
		    st = (dbc.con).createStatement();
		    ResultSet rs = st.executeQuery(query);
		    
		    populateTable(rs); 
		    
		    st.close();
			(dbc.con).close();
		}
		catch(Exception exc) {
			
		}
		
	}
	
	public void searchClicked(ActionEvent e) {
		DBConnection dbc = new DBConnection();
		String query = "";
		
		try {
			if(from.getValue().equals(null) || to.getValue().equals(null)) {}
		}
		catch(Exception exc) {
			alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("Please fill both the date fields before searching.");
			alert.show();
			return;
		}
		
		try {
			if(cb.getValue().equals("Purchase")) {
				query = "SELECT invoice_no, invoice_date, sum(cgst), "
						+ "sum(sgst), sum(total_gst_amt), sum(total_amt) FROM purchase where "
						+ "invoice_date between ? and ? GROUP by invoice_no";
			}
			else if(cb.getValue().equals("Billing")) {
				query = "SELECT invoice_no, invoice_date, sum(cgst), "
						+ "sum(sgst), sum(total_gst_amt), sum(total_amt) FROM billing where "
						+ "invoice_date between ? and ? GROUP by invoice_no";
			}
			
			if(!dbc.connected) dbc.getConnection();
			PreparedStatement st = (dbc.con).prepareStatement(query);
			st.setString(1, from.getValue().toString());
			st.setString(2, to.getValue().toString());
			ResultSet rs = st.executeQuery();
			
			populateTable(rs);
			
			st.close();
			(dbc.con).close();
		}
		catch(Exception exc) {
			searchWithoutSort();
		}
		
	}
	
	public void searchWithoutSort() {
		DBConnection dbc = new DBConnection();
		String query = "{call reports_proc_date(?, ?)}";
		
		try {
			if(!dbc.connected) dbc.getConnection();
			CallableStatement cs = (dbc.con).prepareCall(query);
			cs.setString(1, from.getValue().toString());
			cs.setString(2, to.getValue().toString());
			ResultSet rs = cs.executeQuery();
			
			populateTable(rs);
			
			cs.close();
			(dbc.con).close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void printClicked(ActionEvent e) {
		try {
			if(from.getValue().equals(null) || to.getValue().equals(null)) {}
		}
		catch(Exception exc) {
			try {
				cb.getValue().equals(null);
			}
			catch(Exception exc1) {
				printReport1();
				return;
			}
			printReport3();
			return;
		}
		try {
			cb.getValue().equals(null);
		}
		catch(Exception exc1) {
			printReport2();
			return;
		}
		printReport4();
		return;
	}
	
	public void printReport1() {                   // all 3 are empty
		JasperPrint jasperPrint;
		try {
			JasperReport jasperReport = JasperCompileManager
		               .compileReport("C:\\Users\\gowth\\eclipse-workspace\\GST - Invoice and Billing\\src\\application\\report1.jrxml");
		 
		       
		       Map<String, Object> parameters = new HashMap<String, Object>();
		       parameters.put("total_report_amt", total_amt.getText());
		 
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
               stage.setTitle("Print - End Reports");
               stage.showAndWait();
               
               (dbc.con).close();
		      
		      }
		 catch (Exception e1) {
			   if(total_amt.getText().equals("")) {
				   alert = new Alert(Alert.AlertType.ERROR);
				   alert.setContentText("Sum Total cann't be empty.");
				   alert.show();
			   }
			   e1.printStackTrace();
		}
	}
	
    public void printReport2() {                   // sort is empty
    	JasperPrint jasperPrint;
		try {
			JasperReport jasperReport = JasperCompileManager
		               .compileReport("C:\\Users\\gowth\\eclipse-workspace\\GST - Invoice and Billing\\src\\application\\report2.jrxml");
		 
		       
		       Map<String, Object> parameters = new HashMap<String, Object>();
		       parameters.put("total_report_amt", total_amt.getText());
		       parameters.put("from", from.getValue().toString());
		       parameters.put("to", to.getValue().toString());
		 
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
               stage.setTitle("Print - End Reports");
               stage.showAndWait();
               
               (dbc.con).close();
		      
		      }
		 catch (Exception e1) {
			   if(total_amt.getText().equals("")) {
				   alert = new Alert(Alert.AlertType.ERROR);
				   alert.setContentText("Sum Total cann't be empty.");
				   alert.show();
			   }
			   e1.printStackTrace();
		}
	}

    public void printReport3() {                   // dates are empty
    	JasperPrint jasperPrint;
		try {
			JasperReport jasperReport = JasperCompileManager
		               .compileReport("C:\\Users\\gowth\\eclipse-workspace\\GST - Invoice and Billing\\src\\application\\report3.jrxml");
		 
		       
		       Map<String, Object> parameters = new HashMap<String, Object>();
		       parameters.put("total_report_amt", total_amt.getText());
		       parameters.put("sort", cb.getValue());
		       
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
			   if(total_amt.getText().equals("")) {
				   alert = new Alert(Alert.AlertType.ERROR);
				   alert.setContentText("Sum Total cann't be empty.");
				   alert.show();
			   }
			   e1.printStackTrace();
		}
    }

    public void printReport4() {                   // all 3 are non - empty
    	JasperPrint jasperPrint;
		try {
			JasperReport jasperReport = JasperCompileManager
		               .compileReport("C:\\Users\\gowth\\eclipse-workspace\\GST - Invoice and Billing\\src\\application\\report4.jrxml");
		 
		       
		       Map<String, Object> parameters = new HashMap<String, Object>();
		       parameters.put("total_report_amt", total_amt.getText());
		       parameters.put("sort", cb.getValue());
		       parameters.put("from", from.getValue().toString());
		       parameters.put("to", to.getValue().toString());
		 
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
			   if(total_amt.getText().equals("")) {
				   alert = new Alert(Alert.AlertType.ERROR);
				   alert.setContentText("Sum Total cann't be empty.");
				   alert.show();
			   }
			   e1.printStackTrace();
		}
    }
	
}
