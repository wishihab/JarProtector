import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.jar.JarInputStream;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import java.awt.Choice;

import javax.swing.JCheckBox;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JSpinner;

/**
 * The GUI for the application with buttons and stuff...
 * 
 * @author Burn3diC3 
 * */
public class GUI extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField fileToEncryptField;
	private JTextField keyField;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	public static GUI instance; //current UI instance, so other classes can use this instance as parent for messageboxes (such as an displaying error)

	public static void main(String[] args) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {

		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				GUI g = new GUI();
				g.setVisible(true);
			}
		});
	}

	private GUI() {
		initialize(); //setup ui
	}

	private void initialize() {
		instance = this;
		setIconImage(new ImageIcon(GUI.class.getResource("protect.png"))
				.getImage());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);

		setSize(359, 279);
		setLocationRelativeTo(null);
		setTitle("JarProtector by Burn3diC3 [1.1.1]");
		setBackground(Color.BLACK);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		final Choice choice = new Choice();

		JButton btnOpenjarTo = new JButton("Open .jar to protect");
		btnOpenjarTo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser f = new JFileChooser();
				int i = f.showOpenDialog(f);
				if (i == JFileChooser.APPROVE_OPTION) {
					fileToEncryptField.setText(f.getSelectedFile().getAbsolutePath());
					try {
						JarInputStream jis = new JarInputStream(
								new FileInputStream(f.getSelectedFile()));
						List<String> classes = Utils.getClasses(jis); //get list of all classes from jar
						String main = Utils.getMainClassFromJar(jis); //main class from jar
						
						choice.removeAll(); //clean classes list

						for (String s : classes)
							choice.add(s.replace("/", "."));

						choice.select(main);
						jis.close();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		btnOpenjarTo.setIcon(new ImageIcon(GUI.class.getResource("open.png")));

		fileToEncryptField = new JTextField();
		fileToEncryptField.setColumns(10);

		final JSpinner spinner = new JSpinner();


		final JCheckBox chckbxEnableDelay = new JCheckBox(
				"Enable runtime delay");

		final JRadioButton rdbtnOnlyEncryptClasses = new JRadioButton(
				"Only encrypt classes");
		buttonGroup.add(rdbtnOnlyEncryptClasses);
		rdbtnOnlyEncryptClasses.setSelected(true);

		JRadioButton rdbtnEncryptClasses = new JRadioButton(
				"Encrypt classes + reources");
		buttonGroup.add(rdbtnEncryptClasses);

		JLabel lblMainClass = new JLabel("Main class:");

		
		final JCheckBox chckbxCompressjar = new JCheckBox("Compress .jar with GZIP");
		
		JButton btnProtect = new JButton("Protect");
		btnProtect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JFileChooser f = new JFileChooser();
				int i = f.showSaveDialog(f); 
				if (i == JFileChooser.APPROVE_OPTION) {
					File output = f.getSelectedFile();
					try {
						String to = output.getAbsolutePath();
						String from = fileToEncryptField.getText();
						boolean run = true;
						if (from.length() == 0) {
							JOptionPane
									.showMessageDialog(
											GUI.instance,
											"This program is useless if you haven't selected a file to protect first...!",
											"Nope!",
											JOptionPane.WARNING_MESSAGE);
							run = false;
						}
						if (to.equals(from) && run) {
							JOptionPane.showMessageDialog(GUI.instance,
									"Cannot use the same outpath as input!",
									"Nope!", JOptionPane.ERROR_MESSAGE);
							run = false;
						}
						if (chckbxEnableDelay.isSelected()
								&& ((int)spinner.getValue()) <= 0 && run) {
							JOptionPane.showMessageDialog(GUI.instance,
									"Invalid memory execution delay...",
									"Nope!", JOptionPane.ERROR_MESSAGE);
							run = false;
						}
						String randomJarEntry = Utils.randomName(5);

						StringBuilder config = new StringBuilder();
						config.append(choice.getSelectedItem() + "\n");
						config.append(randomJarEntry + "\n");

						config.append(chckbxEnableDelay.isSelected() + "\n");
						config.append(spinner.getValue() + "\n");
						config.append(chckbxCompressjar.isSelected() + "\n");
						config.append(keyField.getText() + "\n");
						boolean ext = true;
						if (!to.endsWith(".jar") && run) {
							ext = JOptionPane
									.showConfirmDialog(
											GUI.instance,
											"Maybe saving is as a .jar would be smart?",
											"Weird extension indeed",
											JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION;
						}

						if (run && ext)
							JarBuilder.buildOutput(from, to,
										rdbtnOnlyEncryptClasses.isSelected(),
										config.toString(), keyField
												.getText().getBytes(),
										randomJarEntry, chckbxCompressjar.isSelected());
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(GUI.instance,
								"Error: " + ex.getMessage(), "Error!",
								JOptionPane.ERROR_MESSAGE);
						ex.printStackTrace();
					}
				}
			}
		});
		btnProtect.setIcon(new ImageIcon(GUI.class.getResource("protect.png")));

		JButton btnRandomEncryptionKey = new JButton("Random encryption key");
		btnRandomEncryptionKey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				keyField.setText(Utils.generateEncryptionKey(JarBuilder.KEY_SIZE));
			}
		});
		btnRandomEncryptionKey.setIcon(new ImageIcon(GUI.class
				.getResource("key.png")));

		keyField = new JTextField();
		keyField.setColumns(10);
		keyField.setEnabled(false); //disable editing for key 
		keyField.setText(Utils.generateEncryptionKey(JarBuilder.KEY_SIZE));

		JLabel lblSeconds = new JLabel("seconds");
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(5)
							.addComponent(btnOpenjarTo)
							.addGap(10)
							.addComponent(fileToEncryptField, GroupLayout.PREFERRED_SIZE, 173, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(15)
							.addComponent(lblMainClass)
							.addGap(6)
							.addComponent(choice, GroupLayout.PREFERRED_SIZE, 264, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(5)
							.addComponent(btnRandomEncryptionKey, GroupLayout.PREFERRED_SIZE, 173, GroupLayout.PREFERRED_SIZE)
							.addGap(10)
							.addComponent(keyField, GroupLayout.PREFERRED_SIZE, 149, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(84)
							.addComponent(rdbtnEncryptClasses))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(101)
							.addComponent(rdbtnOnlyEncryptClasses))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(45)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
								.addComponent(chckbxCompressjar)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(chckbxEnableDelay)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(spinner, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE)))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblSeconds, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(122)
							.addComponent(btnProtect)))
					.addContainerGap(6, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(6)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(btnOpenjarTo, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(1)
							.addComponent(fileToEncryptField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addGap(6)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(5)
							.addComponent(lblMainClass))
						.addComponent(choice, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(10)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(btnRandomEncryptionKey, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(1)
							.addComponent(keyField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(rdbtnOnlyEncryptClasses)
					.addGap(3)
					.addComponent(rdbtnEncryptClasses)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(chckbxEnableDelay)
							.addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(3)
							.addComponent(lblSeconds)))
					.addGap(7)
					.addComponent(chckbxCompressjar)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnProtect, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
					.addGap(62))
		);
		contentPane.setLayout(gl_contentPane);

	}
}
