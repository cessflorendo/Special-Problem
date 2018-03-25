package userInterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

import net.miginfocom.swing.MigLayout;

public class UI {
	private JFrame frame;
	private JPanel menuPanel; 
	private JPanel inputPanel;
	private JPanel constraintsPanel;
	private JPanel resultsPanel;
	private JPanel fileInputPanel;
	private JPanel transformedDataPanel;
	private File filename;


	private JTabbedPane displayPane;
	private JFileChooser chooser;

	public UI(){
		initialize();
	}

	private void initialize(){
		frame = new JFrame();
		frame.setTitle("Approximate Gene Cluster Tool");
		frame.setMinimumSize(new Dimension(800,600));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new MigLayout("","[grow]","[grow]"));
		
		JPanel inputPanel = new JPanel();
		JPanel constraintsPanel = new JPanel();
		JPanel resultsPanel = new JPanel();
		
		displayPane = new JTabbedPane(JTabbedPane.TOP);
		displayPane.addTab("Input Data", inputPanel);
		displayPane.addTab("Constraints", constraintsPanel);
		displayPane.addTab("Results",resultsPanel);
		displayPane.setEnabledAt(1, false);
		displayPane.setEnabledAt(2, false);
		
		frame.getContentPane().add(displayPane, "cell 0 0, grow");		
		inputPanel.setLayout(new MigLayout("wrap 2", "[][grow]", "[][grow][]"));
		{
			JButton openFileButton = new JButton();
			JTextField fileNameTextField = new JTextField();
			JTextArea preview = new JTextArea();
			JButton nextButton = new JButton();
			
			openFileButton.setText("Open CSV File");
			inputPanel.add(openFileButton);
			openFileButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser jfc = new JFileChooser();
				    jfc.showDialog(null,"Please Select the File");
				    jfc.setVisible(true);
				    filename = jfc.getSelectedFile();
				    fileNameTextField.setText(filename.getAbsolutePath());
				    nextButton.setEnabled(true);
					
				}
			});

			fileNameTextField.setText("");
			inputPanel.add(fileNameTextField, "growx, span");
			preview.setLineWrap(true);
			JScrollPane jsp = new JScrollPane(preview);
			inputPanel.add(jsp, "grow, span");


			nextButton.setEnabled(false);
			nextButton.setText("Next");
			inputPanel.add(nextButton, "tag right, span");
			nextButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					displayPane.setEnabledAt(0, false);
					displayPane.setEnabledAt(1, true);
					displayPane.setEnabledAt(2, false);
					displayPane.setSelectedIndex(1);
				}
			});
		}

		constraintsPanel.setLayout(new MigLayout("", "[grow]", "[][][]"));
		{
			JPanel formulation = new JPanel();
			formulation.setLayout(new MigLayout("", "[grow][grow][grow][grow]", "[][]"));
			formulation.setBackground(Color.white);
			constraintsPanel.add(formulation, "growx, wrap");

			JLabel formulationLabel = new JLabel();
			formulationLabel.setText("Choose formulation to use: ");	
			formulation.add(formulationLabel, "growx, span");

			JCheckBox basic = new JCheckBox("Basic Formulation");
			formulation.add(basic, "grow");
			basic.setBackground(Color.white);

			JCheckBox commonIntervals = new JCheckBox("Common Intervals");
			formulation.add(commonIntervals, "grow");
			commonIntervals.setBackground(Color.white);

			JCheckBox maxGap = new JCheckBox("Max Gap");
			formulation.add(maxGap, "grow");
			maxGap.setBackground(Color.white);

			JCheckBox rWindows = new JCheckBox("r-Windows");
			formulation.add(rWindows, "grow, span");
			rWindows.setBackground(Color.white);

			JSeparator sep = new JSeparator();
			formulation.add(sep, "growx, span");

		}
		{
			JPanel constraints = new JPanel();
			constraints.setLayout(new MigLayout("", "[grow][grow][grow]", "[][]"));
			constraints.setBackground(Color.white);
			constraintsPanel.add(constraints, "growx, wrap");

			JLabel contraintsLabel = new JLabel();
			contraintsLabel.setText("Input constraints: ");	
			constraints.add(contraintsLabel, "growx, span");

			JLabel sizeRangeLabel = new JLabel();
			sizeRangeLabel.setText("Size Range: ");	
			constraints.add(sizeRangeLabel, "tag right");

			JSpinner from = new JSpinner();
			constraints.add(from, "grow");

			JSpinner to = new JSpinner();
			constraints.add(to, "grow, span");

			JLabel additionalWeightLabel = new JLabel();
			additionalWeightLabel.setText("Integer Weights (+): ");	
			constraints.add(additionalWeightLabel, "tag right");

			JSpinner additional = new JSpinner();
			constraints.add(additional, "grow, span");

			JLabel missingWeightLabel = new JLabel();
			missingWeightLabel.setText("Integer Weights (-): ");	
			constraints.add(missingWeightLabel, "tag right");

			JSpinner missing = new JSpinner();
			constraints.add(missing, "grow, span");

			JLabel gapSizeLabel = new JLabel();
			gapSizeLabel.setText("Gap Size: ");	
			constraints.add(gapSizeLabel, "tag right");

			JSpinner gapSize = new JSpinner();
			constraints.add(gapSize, "grow, span");

			JLabel rWindowSize = new JLabel();
			rWindowSize.setText("r Size: ");	
			constraints.add(rWindowSize, "tag right");

			JSpinner rWindow = new JSpinner();
			constraints.add(rWindow, "grow, span");
		}
		{
			JPanel buttons = new JPanel();
			buttons.setLayout(new MigLayout("", "[grow][grow]", "[grow]"));
			buttons.setBackground(Color.white);
			constraintsPanel.add(buttons, "growx, wrap");
			
			JButton backButton = new JButton();
			backButton.setText("Back");
			buttons.add(backButton, "tag left");
			backButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					displayPane.setEnabledAt(0, true);
					displayPane.setEnabledAt(1, false);
					displayPane.setEnabledAt(2, false);
					displayPane.setSelectedIndex(0);
				}
			});
		
			JButton nextButton = new JButton();
			nextButton.setText("Next");
			buttons.add(nextButton, "tag right, wrap");
			nextButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					displayPane.setEnabledAt(0, false);
					displayPane.setEnabledAt(1, false);
					displayPane.setEnabledAt(2, true);
					displayPane.setSelectedIndex(2);
				}
			});
		}
		
		resultsPanel.setLayout(new MigLayout("", "[grow]", "[grow]"));
		{

		}
	}

	public static void main(String[] args){
		UI window = new UI();
		window.frame.setVisible(true);
	}
}
