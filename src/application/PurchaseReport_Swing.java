package application;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDialog;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;

@SuppressWarnings("serial")
public class PurchaseReport_Swing extends JDialog {
	//JasperPrint jaserPrint;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			PurchaseReport_Swing dialog = new PurchaseReport_Swing();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public PurchaseReport_Swing() {
		setBounds(100, 100, 999, 763);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JButton btnSubmit = new JButton("Submit");
		getContentPane().add(btnSubmit, BorderLayout.CENTER);
	}
	
	public PurchaseReport_Swing(JasperPrint jaserPrint) {
		//this.jaserPrint = jaserPrint;
		this.getContentPane().add(new JRViewer(jaserPrint));
		this.pack();
	}
}
