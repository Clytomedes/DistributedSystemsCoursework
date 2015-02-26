import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.*;

/**
 * @author Conor Keegan
 *
 * This document is not meant for re-distribution
 */
public class MeterGUI {
	private JFrame jFrame, dealFrame;
	private MeterObject meter;
	public JTextArea textArea;
	public JLabel readingText, companyText;
	private Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	private DealFrameAdapter windowListener;

	public MeterGUI(MeterObject object) {
		meter = object;
		textArea = new JTextArea();
		textArea.setEditable(false);
		readingText = new JLabel(String.format("%08d", meter.currentReading));
		readingText.setFont(readingText.getFont().deriveFont((float) 50));
		readingText.setHorizontalAlignment(JLabel.CENTER);
		String powerCompanyName = null;
		// Retrieve the name of the current Power Company
		try {
			powerCompanyName = meter.getCurrentPowerCompany();
		} catch (RemoteException e) {
		}
		if (powerCompanyName == null) {
			companyText = new JLabel("<html>Power Company: n/a</html>");
		} else {
			companyText = new JLabel("<html>Power Company: " + powerCompanyName + "</html>");
		}
		companyText.setFont(readingText.getFont().deriveFont((float) 20));
		companyText.setHorizontalAlignment(JLabel.CENTER);
		jFrame = new JFrame("Meter " + object.getSerial());
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setContentPane(getJPanel());
		jFrame.setMinimumSize(new Dimension(500, 500));
		jFrame.setResizable(false);
		jFrame.pack();
		jFrame.setVisible(true);
	}

	public JPanel getJPanel() {
		JPanel jPanel = new JPanel(new BorderLayout());
		jPanel.add(getTopPanel(), BorderLayout.NORTH);
		jPanel.add(getMainPanel(), BorderLayout.CENTER);
		jPanel.add(getButtonPanel(), BorderLayout.SOUTH);
		return jPanel;
	}

	public JPanel getTopPanel() {
		JPanel jPanel = new JPanel(new GridLayout(2, 1));

		jPanel.add(companyText);
		jPanel.add(readingText);

		return jPanel;
	}

	public JPanel getMainPanel() {
		JPanel jPanel = new JPanel(new GridLayout(1, 1));

		JScrollPane scrollPane = new JScrollPane(textArea);
		jPanel.add(scrollPane);

		return jPanel;
	}

	// All of the needed input buttons
	public JPanel getButtonPanel() {
		JPanel jPanel = new JPanel(new FlowLayout());

		JButton registerButton = new JButton("Register");
		registerButton.addActionListener(new registerButtonListener());
		jPanel.add(registerButton);

		JButton unregisterButton = new JButton("Unregister");
		unregisterButton.addActionListener(new unregisterButtonListener());
		jPanel.add(unregisterButton);

		JButton requestDealButton = new JButton("Request Deal");
		requestDealButton.addActionListener(new requestDealButtonListener());
		jPanel.add(requestDealButton);

		JButton sendResultsButton = new JButton("Send Results");
		sendResultsButton.addActionListener(new sendResultsButtonListener());
		jPanel.add(sendResultsButton);

		JButton tamperButton = new JButton("Tamper");
		tamperButton.addActionListener(new TamperButtonListener());
		jPanel.add(tamperButton);

		return jPanel;
	}

	// Separate window to show the offer
	public void showDealOffer(Deal deal) {
		dealFrame = new JFrame("Deal Offer");

		JPanel mainPanel = new JPanel(new BorderLayout());

		JPanel textPanel = new JPanel();

		if (meter.isRegistered()) {
			textPanel.setLayout(new GridLayout(4, 2));
		} else {
			textPanel.setLayout(new GridLayout(2, 2));
		}

		JLabel companyLabel = new JLabel("Company:   ");
		companyLabel.setHorizontalAlignment(JLabel.RIGHT);
		textPanel.add(companyLabel);

		textPanel.add(new JLabel(deal.getPowerCompany()));

		JLabel priceLabel = new JLabel("Price Per Unit:   ");
		priceLabel.setHorizontalAlignment(JLabel.RIGHT);
		textPanel.add(priceLabel);

		textPanel.add(new JLabel(Float.toString(deal.getPricePerUnit()) + "p"));

		if (meter.isRegistered()) {
			JLabel currentPriceLabel = new JLabel("Current Price Per Unit:   ");
			currentPriceLabel.setHorizontalAlignment(JLabel.RIGHT);
			textPanel.add(currentPriceLabel);

			textPanel.add(new JLabel(Float.toString(meter.getCurrentTariff().getPricePerUnit()) + "p"));

			JLabel savingAmountLabel = new JLabel("Saving Per Unit:   ");
			savingAmountLabel.setHorizontalAlignment(JLabel.RIGHT);
			textPanel.add(savingAmountLabel);

			textPanel.add(new JLabel(Float.toString(meter.getCurrentTariff().getPricePerUnit() - deal.getPricePerUnit()) + "p"));
		}

		mainPanel.add(textPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2));

		JButton acceptButton = new JButton("Accept");
		acceptButton.addActionListener(new AcceptButtonListener());
		buttonPanel.add(acceptButton);

		JButton rejectButton = new JButton("Reject");
		rejectButton.addActionListener(new RejectButtonListener());
		buttonPanel.add(rejectButton);

		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		dealFrame.getRootPane().setDefaultButton(acceptButton);
		dealFrame.setContentPane(mainPanel);
		dealFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		windowListener = new DealFrameAdapter();
		dealFrame.addWindowListener(windowListener);

		Integer width = 400;
		Integer height = 100;
		if (meter.isRegistered()) {
			height = 200;
		}
		dealFrame.setLocation(dim.width / 2 - (width / 2), (dim.height / 3) - (height / 3));
		dealFrame.setSize(width, height);
		dealFrame.setResizable(false);
		dealFrame.setVisible(true);
	}

	public void printToGUI(String message) {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aaa");
		Calendar calendar = Calendar.getInstance();
		String time = dateFormat.format(calendar.getTime());

		textArea.append(time + " - " + message);
		textArea.append("\n");
	}

	class registerButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String powerCompany = meter.getRandomPowerCompany();
			if (powerCompany != null) {
				try {
					meter.registerPowerCompany(powerCompany);
				} catch (RemoteException e1) {
				}
			} else {
				System.err.println("No Power Companies currently exist");
				printToGUI("No Power Companies currently exist");
			}
		}
	}

	class unregisterButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				meter.unregisterPowerCompany();
			} catch (RemoteException e1) {
			}
		}
	}

	class requestDealButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String broker = meter.getRandomBroker();
			if (broker != null) {
				meter.requestDeal(broker);
			} else {
				System.err.println("No Brokers currently exist");
				printToGUI("No Brokers currently exist");
			}
		}
	}

	class sendResultsButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			meter.sendReadings();
		}
	}

	class TamperButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			meter.sendAlert(new Alert());
		}
	}

	class AcceptButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			meter.acceptDeal();
			dealFrame.removeWindowListener(windowListener);
			dealFrame.dispose();
		}
	}

	class RejectButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			dealFrame.dispose();
		}
	}

	class DealFrameAdapter extends WindowAdapter {
		@Override
		public void windowClosed(WindowEvent e) {
			meter.rejectDeal();
		}
	}
}
