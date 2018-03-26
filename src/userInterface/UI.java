package userInterface;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

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

import javaBackend.DataConverter;
import net.miginfocom.swing.MigLayout;

public class UI {
	private JFrame frame;
	private File filename;
	//private JTextArea preview;
	private JTabbedPane displayPane;

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
		inputPanel.setLayout(new MigLayout("wrap 2", "[][grow]", "[][][grow][][grow][]"));
		{
			JButton openFileButton = new JButton();
			JTextField fileNameTextField = new JTextField();
			JTextArea previewOriginal = new JTextArea();
			JScrollPane previewOriginalScr = new JScrollPane(previewOriginal);
			JTextArea previewConverted = new JTextArea();
			JScrollPane previewConvertedScr = new JScrollPane(previewConverted);
			JLabel previewOriginalLabel = new JLabel();
			JLabel previewConvertedLabel = new JLabel();
			
			JButton nextButton = new JButton();
			
			openFileButton.setText("Open CSV File");
			previewOriginalLabel.setText("Original data: ");
			previewConvertedLabel.setText("Converted data: ");
			nextButton.setEnabled(false);
			nextButton.setText("Next");
			//preview.setLineWrap(true);
			
			inputPanel.add(openFileButton);
			inputPanel.add(fileNameTextField, "growx, span");
			inputPanel.add(previewOriginalLabel, "growx, span");
			inputPanel.add(previewOriginalScr, "grow, span");
			inputPanel.add(previewConvertedLabel, "growx, span");
			inputPanel.add(previewConvertedScr, "grow, span");
			inputPanel.add(nextButton, "tag right, span");
			
			openFileButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser jfc = new JFileChooser();
				    jfc.showDialog(null,"Please Select the File");
				    jfc.setVisible(true);
				    filename = jfc.getSelectedFile();
				    fileNameTextField.setText(filename.getAbsolutePath());
				    nextButton.setEnabled(true);
				    
				    try {
						DataConverter dc = new DataConverter(filename.getAbsolutePath());
						previewOriginal.setText(dc.getAllGenomes());
						previewConverted.setText(dc.getAllConvertedGenomes());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}
			});
			
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

		constraintsPanel.setLayout(new MigLayout("", "[grow]", "[][][][grow][]"));
		//constraintsPanel.setBackground(Color.white);
		JPanel formulation = new JPanel();
		JLabel formulationLabel = new JLabel();
		JCheckBox basic = new JCheckBox("Basic Formulation");
		JCheckBox commonIntervals = new JCheckBox("Common Intervals");
		JCheckBox maxGap = new JCheckBox("Max Gap");
		JCheckBox rWindows = new JCheckBox("r-Windows");
		JSeparator sep1 = new JSeparator();
		JSeparator sep2 = new JSeparator();
		JPanel constraints = new JPanel();
		JLabel contraintsLabel = new JLabel();
		JLabel sizeRangeLabel = new JLabel();
		JSpinner from = new JSpinner();
		JSpinner to = new JSpinner();
		JLabel additionalWeightLabel = new JLabel();
		JSpinner additional = new JSpinner();
		JLabel missingWeightLabel = new JLabel();
		JSpinner missing = new JSpinner();
		JLabel gapSizeLabel = new JLabel();
		JSpinner gapSize = new JSpinner();
		JLabel rWindowSizeLabel = new JLabel();
		JSpinner rWindowSize = new JSpinner();
		JPanel buttons = new JPanel();
		JButton backButton = new JButton();
		JButton nextButton = new JButton();
		JTextArea sampleResults = new JTextArea();
		JLabel sampleResultsLabel = new JLabel();
		JScrollPane sampleResultsScr = new JScrollPane(sampleResults);
		
		from.setEnabled(false);
		to.setEnabled(false);
		missing.setEnabled(false);
		additional.setEnabled(false);
		gapSize.setEnabled(false);
		rWindowSize.setEnabled(false);
		
		{
			formulation.setLayout(new MigLayout("", "[grow][grow][grow][grow]", "[][]"));
			formulationLabel.setText("Choose formulation to use: ");

			constraintsPanel.add(formulation, "growx, wrap");
			formulation.add(formulationLabel, "growx, span");
			formulation.add(basic, "grow");
			formulation.add(commonIntervals, "grow");
			formulation.add(maxGap, "grow");
			formulation.add(rWindows, "grow, span");
			formulation.add(sep1, "growx, span");
			
			ActionListener formulationListener = new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					if (basic.isSelected() || commonIntervals.isSelected() || maxGap.isSelected() || rWindows.isSelected()){
						from.setEnabled(true);
						to.setEnabled(true);
						missing.setEnabled(true);
						additional.setEnabled(true);
					}
					else{
						from.setEnabled(false);
						to.setEnabled(false);
						missing.setEnabled(false);
						additional.setEnabled(false);
						gapSize.setEnabled(false);
						rWindowSize.setEnabled(false);
					}
					
					if(maxGap.isSelected()){
						gapSize.setEnabled(true);
					} else gapSize.setEnabled(false);
					
					if(rWindows.isSelected()){
						rWindowSize.setEnabled(true);
					} else rWindowSize.setEnabled(false);
				}
			};
			
			basic.addActionListener(formulationListener);
			commonIntervals.addActionListener(formulationListener);
			maxGap.addActionListener(formulationListener);
			rWindows.addActionListener(formulationListener);
		}
		{
			constraints.setLayout(new MigLayout("", "[grow][grow][grow]", "[][]"));
			contraintsLabel.setText("Input constraints: ");
			sizeRangeLabel.setText("Size Range: ");	
			additionalWeightLabel.setText("Integer Weights (+): ");
			missingWeightLabel.setText("Integer Weights (-): ");
			gapSizeLabel.setText("Gap Size: ");	
			rWindowSizeLabel.setText("r Size: ");
			
			constraintsPanel.add(constraints, "growx, wrap");
			constraints.add(contraintsLabel, "growx, span");
			constraints.add(sizeRangeLabel, "tag right");
			constraints.add(from, "grow");
			constraints.add(to, "grow, span");	
			constraints.add(additionalWeightLabel, "tag right");		
			constraints.add(additional, "grow, span");	
			constraints.add(missingWeightLabel, "tag right");
			constraints.add(missing, "grow, span");
			constraints.add(gapSizeLabel, "tag right");
			constraints.add(gapSize, "grow, span");	
			constraints.add(rWindowSizeLabel, "tag right");
			constraints.add(rWindowSize, "grow, span");
			constraints.add(sep2, "growx, span");
		}
		sampleResultsLabel.setText("Sample results: ");
		constraintsPanel.add(sampleResultsLabel, "grow, span");
		constraintsPanel.add(sampleResultsScr, "grow, span");
		{
			buttons.setLayout(new MigLayout("", "[grow][grow]", "[grow]"));
			backButton.setText("Back");
			nextButton.setText("Next");
			
			constraintsPanel.add(buttons, "growx, wrap");
			buttons.add(backButton, "tag left");
			buttons.add(nextButton, "tag right, wrap");
			
			backButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					displayPane.setEnabledAt(0, true);
					displayPane.setEnabledAt(1, false);
					displayPane.setEnabledAt(2, false);
					displayPane.setSelectedIndex(0);
				}
			});
		
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
		
		resultsPanel.setLayout(new MigLayout("", "[grow]", "[grow][]"));
		{
			JTextArea results = new JTextArea();
			JScrollPane resultsScr = new JScrollPane(results);
			JButton back = new JButton();
			JButton exportPDF = new JButton();
			JButton exportCSV = new JButton();
			
			back.setText("Back");
			exportPDF.setText("Export PDF");
			exportCSV.setText("Export CSV");
			
			resultsPanel.add(resultsScr, "grow, span");
			resultsPanel.add(back, "growx");
			resultsPanel.add(exportPDF, "growx");
			resultsPanel.add(exportCSV, "growx, span");
			
			back.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					displayPane.setEnabledAt(0, false);
					displayPane.setEnabledAt(1, true);
					displayPane.setEnabledAt(2, false);
					displayPane.setSelectedIndex(1);
				}
			});
		}
	}

	public static void main(String[] args){
		UI window = new UI();
		window.frame.setVisible(true);
		
	}
}

