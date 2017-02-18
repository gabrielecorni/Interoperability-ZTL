package impl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ModelClasses.SensorData;
import sofia_kp.KPICore;
import sofia_kp.SIBResponse;

public class Sensor extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	// sib instance
	private KPICore kpiCore;
	private SIBResponse resp;

	private JLabel lblPlate = new JLabel("Plate:");
	private JLabel lblAccessGate = new JLabel("Access Gate:");
	private JTextField txtPlate = new JTextField();
	private JComboBox<String> cboAccessGate = new JComboBox<String>();
	private JButton btnSubmit = new JButton("Enter Zone");

	private JPanel pan1 = new JPanel(new GridLayout(1, 2, 10, 10));
	private JPanel pan2 = new JPanel(new GridLayout(1, 2, 10, 10));
	private JPanel pan3 = new JPanel(new BorderLayout(10, 10));
	private JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

	public Sensor(String address, int port) {

		// init mock
		super("Sensor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(new Dimension(500, 140));
		setResizable(false);
		setLayout(new BorderLayout(10, 10));

		kpiCore = new KPICore(address, port, "ztl");
		resp = null;

		// join
		resp = kpiCore.join();
		if (!resp.isConfirmed()) {
			System.err.println("Error joining the SIB");
			System.exit(-1);
		}

		// populate sensor IDs
		cboAccessGate.addItem("1");
		cboAccessGate.addItem("2");
		cboAccessGate.setSelectedIndex(0);

		// handle button click
		btnSubmit.addActionListener(this);

		// graphics
		pan1.add(lblPlate);
		pan1.add(txtPlate);
		pan2.add(lblAccessGate);
		pan2.add(cboAccessGate);
		pan3.add(btnSubmit);

		mainPanel.add(pan1, BorderLayout.NORTH);
		mainPanel.add(pan2, BorderLayout.CENTER);
		mainPanel.add(pan3, BorderLayout.SOUTH);

		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		add(mainPanel);
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				resp = kpiCore.leave();
				if (!resp.isConfirmed()) {
					JOptionPane.showMessageDialog(null, "Error when leaving SIB");
				}else
					JOptionPane.showMessageDialog(null, "Disconnected.");
				
				System.exit(0);
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) { // button click
		SensorData sd = new SensorData(Integer.parseInt(cboAccessGate.getSelectedItem().toString()),
				System.currentTimeMillis(), txtPlate.getText());

		resp = kpiCore.insert(sd.toTriple());
		if (!resp.isConfirmed()) {
			JOptionPane.showMessageDialog(null, "ERROR");
		} else {
			JOptionPane.showMessageDialog(null, sd.toString());
		}

		this.txtPlate.setText("");
	}

}
