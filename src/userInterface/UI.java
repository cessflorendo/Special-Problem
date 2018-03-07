package userInterface;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

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
	
	private JButton chooseFileButton;
	private JTextField fileNameField;
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
		frame.getContentPane().add(displayPane, "cell 0 0,grow");		

		inputPanel = new JPanel();
		displayPane.addTab("Input Data", inputPanel);
		inputPanel.setLayout(new MigLayout("", "[grow]", "[grow]"));
		{
			fileInputPanel = new JPanel();
			inputPanel.add(fileInputPanel, "cell 0 0, grow");
			fileInputPanel.setLayout(new MigLayout("", "grow", "grow 30"));
			{
				chooseFileButton = new JButton();
				chooseFileButton.setText("Open File");
				fileInputPanel.add(chooseFileButton, "wrap");
				
				fileNameField = new JTextField();
				fileInputPanel.add(fileNameField, "grow");
			}
			
			transformedDataPanel = new JPanel();
			inputPanel.add(fileInputPanel, "grow");
			transformedDataPanel.setLayout(new MigLayout("", "grow", "grow"));
			
			
		}
		
		constraintsPanel = new JPanel();
		displayPane.addTab("Constraints", constraintsPanel);
		
		resultsPanel = new JPanel();
		displayPane.addTab("Results",resultsPanel);
		resultsPanel.setLayout(new MigLayout("", "[grow]", "[grow]"));
		
	}
	
	public static void main(String[] args){
		UI window = new UI();
		window.frame.setVisible(true);
	}
}
