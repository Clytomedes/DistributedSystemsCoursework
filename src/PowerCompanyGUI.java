import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * @author Conor Keegan
 *
 * This document is not meant for re-distribution
 */
public class PowerCompanyGUI {
	public JTextArea textArea;
	private JFrame tariffFrame;
	private JTextField textField;
	private Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	private PowerCompanyObject powerCompany;

	public PowerCompanyGUI(PowerCompanyObject object) {
		powerCompany = object;
		textArea = new JTextArea();
		textArea.setEditable(false);
		JFrame jFrame = new JFrame("PowerCompany - " + powerCompany.companyName);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setContentPane(getJPanel());
		jFrame.setMinimumSize(new Dimension(500, 500));
		jFrame.setResizable(false);
		jFrame.pack();
		jFrame.setVisible(true);
	}

	public JPanel getJPanel() {
		JPanel jPanel = new JPanel(new BorderLayout());

		jPanel.add(getMainPanel(), BorderLayout.CENTER);

		jPanel.add(getButtonPanel(), BorderLayout.SOUTH);

		return jPanel;
	}

	public JPanel getMainPanel() {
		JPanel jPanel = new JPanel(new GridLayout(1, 1));

		JScrollPane scrollPane = new JScrollPane(textArea);
		jPanel.add(scrollPane);

		return jPanel;
	}

	public JPanel getButtonPanel() {
		JPanel jPanel = new JPanel();

		JButton changeTariffButton = new JButton("Change Tariff Price");
		changeTariffButton.addActionListener(new ChangeTariffButtonListener());
		jPanel.add(changeTariffButton);

		return jPanel;
	}

	// Pop up window to change the tariff price
	public void changeTariff() {
		tariffFrame = new JFrame("Change Tariff Price");

		JPanel mainPanel = new JPanel(new GridLayout(2, 1));

		JPanel textPanel = new JPanel();
		textPanel.setLayout(new GridLayout(2, 2));

		JLabel tariffLabel = new JLabel("Previous Tariff Price Per Unit:   ");
		tariffLabel.setHorizontalAlignment(JLabel.RIGHT);
		textPanel.add(tariffLabel);

		textPanel.add(new JLabel(Float.toString(powerCompany.tariffInfo.getPricePerUnit()) + "p"));

		JLabel newTariffLabel = new JLabel("New Tariff Price Per Unit:   ");
		newTariffLabel.setHorizontalAlignment(JLabel.RIGHT);
		textPanel.add(newTariffLabel);

		textField = new JTextField();
		textPanel.add(textField);

		mainPanel.add(textPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2));

		JButton changeButton = new JButton("Change");
		changeButton.addActionListener(new ChangeButtonListener());
		JPanel changeButtonPanel = new JPanel();
		changeButtonPanel.add(changeButton);
		buttonPanel.add(changeButtonPanel);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new CancelButtonListener());
		JPanel cancelButtonPanel = new JPanel();
		cancelButtonPanel.add(cancelButton);
		buttonPanel.add(cancelButtonPanel);

		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		tariffFrame.getRootPane().setDefaultButton(changeButton);
		tariffFrame.setContentPane(mainPanel);
		tariffFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		Integer width = 400;
		Integer height = 150;

		tariffFrame.setLocation(dim.width / 2 - (width / 2), (dim.height / 3) - (height / 3));
		tariffFrame.setSize(width, height);
		tariffFrame.setResizable(false);
		tariffFrame.setVisible(true);
	}

	class ChangeTariffButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			changeTariff();
		}
	}

	class ChangeButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String text = textField.getText();
			float number;
			try {
				number = Float.valueOf(text);
			} catch (NumberFormatException e1) {
				textField.setText("You must enter a valid number");
				return;
			}

			powerCompany.changeTariffPrice(number);
			tariffFrame.dispose();
		}
	}

	class CancelButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			tariffFrame.dispose();
		}
	}
}
