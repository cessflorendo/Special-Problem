package userInterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
		
		displayPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(displayPane, "cell 0 0, grow");		

		inputPanel = new JPanel();
		displayPane.addTab("Input Data", inputPanel);
		inputPanel.setLayout(new MigLayout("wrap 2", "[][grow]", "[][grow][]"));
		{
			JButton openFileButton = new JButton();
			openFileButton.setText("Open CSV File");
			inputPanel.add(openFileButton);
			
			JTextField fileNameTextField = new JTextField();
			fileNameTextField.setText("Open CSV dfjsadfjkdgfkfkjsdhgf");
			inputPanel.add(fileNameTextField, "growx, span");
			
			JTextField preview = new JTextField();
			inputPanel.add(preview, "grow,span");
			
			JButton nextButton = new JButton();
			nextButton.setText("Next");
			inputPanel.add(nextButton, "tag right, span");
			
		}
		
		constraintsPanel = new JPanel();
		displayPane.addTab("Constraints", constraintsPanel);
		constraintsPanel.setLayout(new MigLayout("", "grow", "[][]"));
		{
			JPanel formulation = new JPanel();
			formulation.setLayout(new MigLayout("", "[grow][grow][grow][grow]", "[][]"));
			formulation.setBackground(Color.white);
			constraintsPanel.add(formulation);
			
			JLabel formulationLabel = new JLabel();
			formulationLabel.setText("Choose formulation to use: ");
			formulation.add(formulationLabel, "growx, span");
			
			JCheckBox basic = new JCheckBox("Basic Formulation");
			formulation.add(basic);
			
			JCheckBox commonIntervals = new JCheckBox("Common Intervals");
			formulation.add(commonIntervals);
			
			JCheckBox maxGap = new JCheckBox("Max Gap");
			formulation.add(maxGap);
			
			JCheckBox rWindows = new JCheckBox("r-Windows");
			formulation.add(rWindows);
		}
		
		resultsPanel = new JPanel();
		displayPane.addTab("Results",resultsPanel);
		resultsPanel.setLayout(new MigLayout("", "[grow]", "[grow]"));
		
	}
	
	public static void main(String[] args){
		UI window = new UI();
		window.frame.setVisible(true);
	}
}
