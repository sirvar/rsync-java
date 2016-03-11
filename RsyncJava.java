/**
 * RsyncJava.java
 *
 * @author Rikin Katyal
 * @description A simple GUI interface for rsync UNIX command
 * @version 1.0
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

public class RsyncJava {

	// declare variables

	private static JFrame mainFrame;

	private static JPanel serverPanel;
	private static JPanel directoryPanel;
	private static JPanel syncPanel;

	private static JTextField serverAddress;
	private static JTextField serverUser;
	private static JTextField localDirectory;
	private static JTextField remoteDirectory;

	private static JButton localFileExplorer;
	private static JButton changeMode;
	private static JButton syncFiles;

	private static JFileChooser fileChooser;

	private static String title = "Rsync Java";
	private static String serverAddressText = "Server Address";
	private static String serverUserText = "Server Username";
	private static String localDirectoryText = "Local Directory/File";
	private static String remoteDirectoryText = "Remote Directory";

	private static String file = "details.txt";

	// 0 - upload
	// 1 - download
	private static int mode = 0;

	/**
	 * Main method reads the previous state, creates the frame, and makes it visible
	 */
	public static void main(String[] args) {
		// create new instance and show frame
		readDetails();
		createFrame();
		showFrame();
	}

	/**
	 * Creates the {@link JFrame} with the size and layout.
	 */
	public static void createFrame() {
		// create main GUI window
		mainFrame = new JFrame(title);
		mainFrame.setSize(550, 180);
		mainFrame.setLayout(new GridLayout(3, 1));
		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent) {
				writeDetails();
				System.exit(0);
			}
		});
		mainFrame.setResizable(false);
	}

	/**
	 * Creates the panels and sets the {@link JFrame} visible
	 */
	public static void showFrame() {
		// shows frame with all components
		createServerPanel();
		createDirectoryPanel();
		createSync();
		mainFrame.setVisible(true);
	}

	/**
	 * Creates the server {@link JPanel} containing the server information.
	 */
	public static void createServerPanel() {
		// creates control panel containing remote server info
		serverPanel = new JPanel();
		serverPanel.setLayout(new FlowLayout());

		// server address field
		serverAddress = new JTextField(14);
		serverAddress.setText(serverAddressText);

		// user field
		serverUser = new JTextField(14);
		serverUser.setText(serverUserText);

		// add components to server panel
		serverPanel.add(serverAddress);
		serverPanel.add(serverUser);

		// add server panel to main frame
		addPanel(serverPanel);
	}

	/**
	 * Creates the main directory {@link JPanel} containing all fields with the directory information. Add listeners to the buttons.
	 */
	public static void createDirectoryPanel() {
		// creates control panel containing directory info
		directoryPanel = new JPanel();
		directoryPanel.setLayout(new FlowLayout());

		// local directory
		localDirectory = new JTextField(15);
		localDirectory.setText(localDirectoryText);

		// file chooser
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		// open file explorer for local directory
		localFileExplorer = new JButton("...");
		localFileExplorer.setPreferredSize(new Dimension(50, 30));
		localFileExplorer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileChooser.showOpenDialog(null);
				// check if directory was selected
				try {
					localDirectory.setText(fileChooser.getSelectedFile().getAbsolutePath());
				} catch (NullPointerException npe) {
					System.out.println("No file or directory selected.");
				}
			}
		});

		// button to change mode to upload or download
		changeMode = new JButton();
		if (mode == 0) {
			changeMode.setText("→");
		} else {
			changeMode.setText("←");
		}
		changeMode.setPreferredSize(new Dimension(50, 30));
		changeMode.setVerticalAlignment(SwingConstants.CENTER);
		changeMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (mode == 0) {
					changeMode.setText("←");
					mode = 1;
				} else {
					changeMode.setText("→");
					mode = 0;
				}
			}
		});

		// remote directory
		remoteDirectory = new JTextField(15);
		remoteDirectory.setText(remoteDirectoryText);

		// add components to directory panel
		directoryPanel.add(localDirectory);
		directoryPanel.add(localFileExplorer);
		directoryPanel.add(changeMode);
		directoryPanel.add(remoteDirectory);

		// add directory panel to main frame
		addPanel(directoryPanel);

	}

	/**
	 * Creweats the sync {@link JPanel} and button. Adds click listener
	 */
	public static void createSync() {
		// create sync panel
		syncPanel = new JPanel();
		syncPanel.setLayout(new FlowLayout());

		// sync files button
		syncFiles = new JButton("Sync");
		syncFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isEmpty(localDirectory) || isEmpty(serverUser) || isEmpty(serverAddress) || isEmpty(remoteDirectory)) {
					JOptionPane.showMessageDialog(null, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
				} else {
					sync();
				}
			}
		});

		// add button to panel
		syncPanel.add(syncFiles);

		// add panel to main frame
		addPanel(syncPanel);
	}

	/**
	 * Runs the rsync command with the parameters from the fields
	 */
	public static void sync() {
		// create command based on upload or download and parameters
		String command;
		if (mode == 0) {
			command = "rsync -a " + localDirectory.getText() + " " + serverUser.getText() + "@" + serverAddress.getText() + ":" + remoteDirectory.getText();
		} else {
			command = "rsync -a " + serverUser.getText() + "@" + serverAddress.getText() + ":" + remoteDirectory.getText() + " " + localDirectory.getText();
		}
		// run command
		try {
			mainFrame.setTitle("Please wait. Running command.");
			Process p = Runtime.getRuntime().exec(command);
			p.waitFor();
			mainFrame.setTitle("Rsync Java");
			JOptionPane.showMessageDialog(null, "Success! The rsync command executed successfully.");
		} catch (IOException e) {
			System.out.println("Error while trying to rsync.");
		} catch (InterruptedException e) {
			System.out.println("Error while trying to rsync.");
		}
	}

	/**
	 * Adds panels to the main {@link JFrame}
	 * @param panel the panel to add
	 */
	public static void addPanel(JPanel panel) {
		// add JPanel to main JFrame
		mainFrame.add(panel);
	}

	/**
	 * Reads the state of the previous session from the data file
	 */
	public static void readDetails() {
		// read details from file and set text for inputs
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));

			serverAddressText = br.readLine();
			serverUserText = br.readLine();
			localDirectoryText = br.readLine();
			remoteDirectoryText = br.readLine();
			mode = Integer.parseInt(br.readLine());

		} catch (FileNotFoundException e) {
			System.out.println("The file " + file + " was not found.");
			System.exit(-1);
		} catch (IOException e) {
			System.out.println("There was a problem trying to read the file " + file);
			System.exit(-1);
		}
	}

	/**
	 * Saves the state of the fields into data file
	 */
	public static void writeDetails() {
		// write details to file
		try {
			PrintWriter writer = new PrintWriter(file);
			writer.println(serverAddress.getText());
			writer.println(serverUser.getText());
			writer.println(localDirectory.getText());
			writer.println(remoteDirectory.getText());
			if (changeMode.getText().equals("→")) {
				writer.println("0");
			} else {
				writer.println("1");
			}
			writer.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
		}
	}

	/**
	 * Checks if text field is empty
	 * @param  tf {@link JTextField} to check
	 * @return    boolean if its empty or not
	 */
	public static boolean isEmpty(JTextField tf) {
		return tf.getText().equals("");
	}

}