import java.awt.*;
import javax.swing.*;

/**
 * @author Conor Keegan
 *
 * This document is not meant for re-distribution
 */
public class ServerGUI {
	public JTextArea textArea;

	// Basic console output
	public ServerGUI() {
		textArea = new JTextArea();
		textArea.setEditable(false);
		JFrame jFrame = new JFrame("Server");
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setContentPane(getJPanel());
		jFrame.setMinimumSize(new Dimension(500, 500));
		jFrame.pack();
		jFrame.setVisible(true);
	}

	public JPanel getJPanel() {
		JPanel jPanel = new JPanel(new GridLayout(1, 1));

		JScrollPane scrollPane = new JScrollPane(textArea);
		jPanel.add(scrollPane);

		return jPanel;
	}
}
