package userInterface;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import componentActions.TabActions;
import componentActions.WindowActions;
import javaBackend.DataConverter;
import javaBackend.ExportCSV;
import javaBackend.ExportPDF;
import javaBackend.GeneSet;
import javaBackend.ILPFormulation;
import net.miginfocom.swing.MigLayout;

public class Main {
	private int additionalGeneWeight, missingGeneWeight, sizeRangeLower, sizeRangeHigher, maxGapSize, rWindowSize;
	private boolean basicFormulation, commonIntervals, maxGap, rWindows;
	private DataConverter dc;
	private ILPFormulation solve;
	private ArrayList<GeneSet> results;
	private RConnection r;

	private int numOfGenomes = 0;
	JFrame frame;
	private JPanel homeScreen;
	private JPanel HS_title_panel;
	private JButton HS_start_button;
	private JButton HS_instructions_button;
	private ImageIcon HS_logo;
	private JTabbedPane instructions_pane;
	private JLabel HS_pic_label;
	private JTabbedPane mainDisplay_pane;
	private JPanel IP_general;
	private JPanel IP_constraints;
	private JPanel IP_limitations;
	private JEditorPane IP_general_text;
	private JEditorPane IP_constraints_text;
	private JEditorPane IP_limitations_text;
	private JButton IP_mainMenu1;
	private JButton IP_mainMenu2;
	private JButton IP_mainMenu3;
	private ActionListener IP_mainMenu_listener;
	private JPanel MD_input_panel;
	private JPanel MD_variables_panel;
	private JPanel MD_results_panel;
	private JButton MD_IP_openFile_button;
	private JEditorPane MD_IP_preview_text;
	private JScrollPane MD_IP_preview_scr;
	private JEditorPane MD_IP_previewConverted_text;
	private JScrollPane MD_IP_previewConverted_scr;
	private JLabel MD_IP_preview_lbl;
	private JLabel MD_IP_previewConverted_lbl;
	private JButton MD_IP_next_btn;
	private JButton MD_IP_mainMenu_btn;
	private JTextField MD_IP_filename_text;
	private File MD_IP_filename;
	private JSeparator formulation_constraints_sep;
	private JPanel VP_FP_formulation;
	private JLabel VP_FP_formulation_lbl;
	private ButtonGroup VP_FP_group;
	private JRadioButton VP_FP_commonIntervals_rbtn;
	private JRadioButton VP_FP_basic_rbtn;
	private JRadioButton VP_FP_maxGap_rbtn;
	private JRadioButton VP_FP_rWindows_rbtn;
	private JLabel VP_CP_constraints_lbl;
	private JLabel VP_CP_sizeRange_lbl;
	private JSpinner VP_CP_from_spnr;
	private JSpinner VP_CP_to_spnr;
	private JLabel VP_CP_additionalWeight_lbl;
	private JSpinner VP_CP_additional_spnr;
	private JLabel VP_CP_missingWeight_lbl;
	private JSpinner VP_CP_missing_spnr;
	private JLabel VP_CP_gapSize_lbl;
	private JSpinner VP_CP_gapSize_spnr;
	private JLabel VP_CP_rWindowSize_Lbl;
	private JSpinner VP_CP_rWindowSize_spnr;
	private JPanel VP_CP_constraints;
	private JButton VP_RP_back_btn;
	private JButton VP_RP_next_btn;
	private JEditorPane MD_RP_results_text;
	private JScrollPane MD_RP_results_scr;
	private JButton MD_RP_exportCSV_btn;
	private JButton MD_RP_exportPDF_btn;
	private JButton MD_RP_back_btn;

	private Font fontPlain;
	private Font fontButton;
	private Color buttonColor;
	private Color backgroundColor;
	private Color buttonTextColor;
	private Font fontText;
	private JScrollPane IP_general_text_scr;
	private JScrollPane IP_constraints_text_scr;
	private JScrollPane IP_limitations_text_scr;
	private JPanel VP_RP_buttons;
	private JPanel HS_details_panel;
	private JTextPane HS_name_lbl;
	private Font fontTitle;
	private SpinnerNumberModel VP_CP_from_numberModel;
	private SpinnerNumberModel VP_CP_to_numberModel; 

	public Main(){
		this.setAdditionalGeneWeight(0);
		this.setMissingGeneWeight(0);
		this.setSizeRangeLower(0);
		this.setSizeRangeHigher(0);
		this.setMaxGapSize(0);
		this.setrWindowSize(0);
		this.setBasicFormulation(false);
		this.setCommonIntervals(false);
		this.setMaxGap(false);
		this.setrWindows(false);
		fontTitle = new Font("Lucida Sans Unicode", Font.PLAIN, 48);
		fontButton = new Font("Lucida Sans Unicode", Font.BOLD, 16);
		fontText = new Font("Lucida Sans Unicode", Font.PLAIN, 16);
		fontPlain =  new Font("Lucida Sans Unicode", Font.PLAIN, 16);
		backgroundColor = Color.decode("#BB649B");

		backgroundColor = Color.decode("#FEFCFC");
		buttonColor = Color.decode("#94206B");
		buttonTextColor = Color.decode("#FEFCFD");

		//buttonColor = Color.gray;
		initialize();
	}


	private void initialize(){
		UIManager.put("TabbedPane.unselectedBackground", Color.decode("#C1B6CA"));
		UIManager.put("TabbedPane.selected", buttonColor);
		UIManager.put("TabbedPane.font", fontButton);
		UIManager.put("TabbedPane.tabInsets", new Insets(10,10,10,10));

		frame = new JFrame();
		WindowActions window = new WindowActions(r);
		frame.addWindowListener(window);
		frame.setTitle("Approximate Gene Cluster Tool");
		frame.setMinimumSize(new Dimension(800,600));
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(new ImageIcon("images/icon1.png").getImage());
		homeScreen = new JPanel();
		homeScreen.setBackground(backgroundColor);
		homeScreen.setLayout(new MigLayout("", "[grow][grow]", "[grow][grow][]"));
		frame.add(homeScreen);
		frame.setBackground(backgroundColor);
		{

			HS_title_panel = new JPanel();
			{
				HS_title_panel.setLayout(new MigLayout("", "[grow][grow]", "[grow]"));
				HS_title_panel.setBackground(backgroundColor);
				HS_pic_label = new JLabel();
				HS_logo = null;

				Image logoImg = new ImageIcon("images/icon1.png").getImage();
				logoImg = logoImg.getScaledInstance(200, 200, java.awt.Image.SCALE_SMOOTH);
				HS_logo= new ImageIcon(logoImg);
				HS_pic_label = new JLabel(HS_logo);

				HS_name_lbl = new JTextPane();
				HS_name_lbl.setEditable(false);
				HS_name_lbl.setMargin(new Insets(60, 20, 0, 0));
				HS_name_lbl.setBackground(backgroundColor);
				HS_name_lbl.setForeground(buttonColor);
				HS_name_lbl.setText("Approximate Gene Cluster Discovery Tool");
				HS_name_lbl.setFont(fontTitle);


				HS_title_panel.add(HS_pic_label, "grow");
				HS_title_panel.add(HS_name_lbl, "grow, span");
			}

			HS_details_panel = new JPanel();
			{
				Color details = Color.decode("#E99FA6");
				HS_details_panel.setBackground(details);

			}

			Image img = new ImageIcon("images/arrow-point-to-right.png").getImage();
			img = img.getScaledInstance(40, 40,  java.awt.Image.SCALE_SMOOTH);
			ImageIcon startIcon = new ImageIcon(img);
			HS_start_button = new JButton(startIcon);
			HS_start_button.setText("Start");
			
			
			HS_start_button.setFont(fontButton);
			HS_start_button.setForeground(buttonTextColor);
			HS_start_button.setBackground(buttonColor);

			HS_start_button.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					frame.remove(homeScreen);
					frame.setContentPane(mainDisplay_pane);
					frame.setResizable(true);
					frame.revalidate(); 
					frame.repaint();	
				}

			});

			Image instruImg = new ImageIcon("images/instructions.png").getImage();
			instruImg = instruImg.getScaledInstance(40, 40,  java.awt.Image.SCALE_SMOOTH);
			ImageIcon instruIcon = new ImageIcon(instruImg);
			HS_instructions_button = new JButton(instruIcon);
			HS_instructions_button.setText("Getting Started");
			HS_instructions_button.setFont(fontButton);
			HS_instructions_button.setForeground(buttonTextColor);
			HS_instructions_button.setBackground(buttonColor);

			HS_instructions_button.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					frame.remove(frame.getContentPane());
					frame.setContentPane(instructions_pane);
					frame.revalidate(); 
					frame.repaint();
				}
			});
		}

		homeScreen.add(HS_title_panel, "grow, span");
		homeScreen.add(HS_details_panel, "grow, span");

		homeScreen.add(HS_start_button, "grow");
		homeScreen.add(HS_instructions_button, "grow");
		//homeScreen.add(HS_buttons_panel, "grow");

		instructions_pane = new JTabbedPane(JTabbedPane.TOP);
		instructions_pane.setForeground(buttonTextColor);
		{
			instructions_pane.setFont(fontButton);
			IP_mainMenu_listener = new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {	
					frame.remove(mainDisplay_pane);
					frame.setContentPane(homeScreen);
					frame.revalidate(); 
					frame.repaint();
				}
			};

			IP_general = new JPanel();{
				IP_general.setLayout(new MigLayout("", "[grow]", "[grow][]"));
				IP_general.setBackground(backgroundColor);

				Image backImg = new ImageIcon("images/arrow-point-to-left.png").getImage();
				backImg = backImg.getScaledInstance(25, 25,  java.awt.Image.SCALE_SMOOTH);
				ImageIcon backIcon = new ImageIcon(backImg);
				IP_mainMenu1 = new JButton(backIcon);
				IP_mainMenu1.setText("Main Menu");
				IP_mainMenu1.setFont(fontButton);
				IP_mainMenu1.setBackground(buttonColor);
				IP_mainMenu1.setForeground(buttonTextColor);
				IP_mainMenu1.addActionListener(IP_mainMenu_listener);

				IP_general_text = new JEditorPane("text/html", "");
				IP_general_text.setMargin(new Insets(20,20,20,20));
				IP_general_text.setFont(fontPlain);
				try {
					BufferedReader br = new BufferedReader(new FileReader("text files\\how-to-use.txt"));
					StringBuilder sb = new StringBuilder();
					String line = br.readLine();
					while (line != null) {
						sb.append(line);
						sb.append(System.lineSeparator());
						line = br.readLine();
					}
					String everything = sb.toString();
					String fontfamily = IP_general_text.getFont().getFamily();
					IP_general_text.setText("<html><body style=\"font-family: " + fontfamily + "\"" + everything + "<html>");
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				IP_general_text.setEditable(false);
				IP_general_text_scr = new JScrollPane(IP_general_text);
				IP_general.add(IP_general_text_scr, "grow, span");
				IP_general.add(IP_mainMenu1, "tag left");

			}
			IP_constraints = new JPanel();{
				IP_constraints.setBackground(backgroundColor);
				IP_constraints.setLayout(new MigLayout("", "[grow]", "[grow][]"));

				Image backImg = new ImageIcon("images/arrow-point-to-left.png").getImage();
				backImg = backImg.getScaledInstance(25, 25,  java.awt.Image.SCALE_SMOOTH);
				ImageIcon backIcon = new ImageIcon(backImg);
				IP_mainMenu2 = new JButton(backIcon);
				IP_mainMenu2.setText("Main Menu");
				IP_mainMenu2.setFont(fontButton);
				IP_mainMenu2.setBackground(buttonColor);
				IP_mainMenu2.setForeground(buttonTextColor);
				IP_mainMenu2.addActionListener(IP_mainMenu_listener);

				IP_constraints_text = new JEditorPane("text/html", "");
				IP_constraints_text.setMargin(new Insets(20,20,20,20));
				IP_constraints_text.setFont(fontText);
				try {
					BufferedReader br = new BufferedReader(new FileReader("text files\\constraints.txt"));
					StringBuilder sb = new StringBuilder();
					String line = br.readLine();
					while (line != null) {
						sb.append(line);
						sb.append(System.lineSeparator());
						line = br.readLine();
					}
					String everything = sb.toString();
					String fontfamily = IP_constraints_text.getFont().getFamily();
					IP_constraints_text.setText("<html><body style=\"font-family: " + fontfamily + "\"" + everything + "<html>");
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				IP_constraints_text.setEditable(false);
				IP_constraints_text_scr = new JScrollPane(IP_constraints_text);
				IP_constraints.add(IP_constraints_text_scr, "grow, span");
				IP_constraints.add(IP_mainMenu2, "tag left");
			}
			IP_limitations = new JPanel();{
				IP_limitations.setBackground(backgroundColor);
				IP_limitations.setLayout(new MigLayout("", "[grow]", "[grow][]"));
				IP_limitations.setFont(fontPlain);

				Image backImg = new ImageIcon("images/arrow-point-to-left.png").getImage();
				backImg = backImg.getScaledInstance(25, 25,  java.awt.Image.SCALE_SMOOTH);
				ImageIcon backIcon = new ImageIcon(backImg);
				IP_mainMenu3 = new JButton(backIcon);
				IP_mainMenu3.setText("Main Menu");
				IP_mainMenu3.setFont(fontButton);
				IP_mainMenu3.setBackground(buttonColor);
				IP_mainMenu3.setForeground(buttonTextColor);
				IP_mainMenu3.addActionListener(IP_mainMenu_listener);

				IP_limitations_text = new JEditorPane("text/html", "");
				IP_limitations_text.setMargin(new Insets(20,20,20,20));
				try {
					BufferedReader br = new BufferedReader(new FileReader("text files\\limitations.txt"));
					StringBuilder sb = new StringBuilder();
					String line = br.readLine();
					while (line != null) {
						sb.append(line);
						sb.append(System.lineSeparator());
						line = br.readLine();
					}
					String everything = sb.toString();
					String fontfamily = IP_constraints_text.getFont().getFamily();
					IP_limitations_text.setText("<html><body style=\"font-family: " + fontfamily + "\"" + everything + "<html>");
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				IP_limitations_text.setEditable(false);
				IP_limitations_text_scr = new JScrollPane(IP_limitations_text);
				IP_limitations.add(IP_limitations_text_scr, "grow, span");
				IP_limitations.add(IP_mainMenu3, "tag left");
			}
			Image generalImg = new ImageIcon("images/how.png").getImage();
			generalImg = generalImg.getScaledInstance(15, 15,  java.awt.Image.SCALE_SMOOTH);
			ImageIcon generalIcon = new ImageIcon(generalImg);
			Image constraintsImg = new ImageIcon("images/constraints.png").getImage();
			constraintsImg = constraintsImg.getScaledInstance(20, 20,  java.awt.Image.SCALE_SMOOTH);
			ImageIcon constraintsIcon = new ImageIcon(constraintsImg);
			Image limitationsImg = new ImageIcon("images/limitations.png").getImage();
			limitationsImg = limitationsImg.getScaledInstance(20, 20,  java.awt.Image.SCALE_SMOOTH);
			ImageIcon limitationsIcon = new ImageIcon(limitationsImg);
			instructions_pane.addTab("How To Use Tool", IP_general);
			instructions_pane.addTab("Data Constraints", IP_constraints);
			instructions_pane.addTab("Limitations", IP_limitations);
			instructions_pane.setIconAt(0, generalIcon);
			instructions_pane.setIconAt(1, constraintsIcon);
			instructions_pane.setIconAt(2, limitationsIcon);
		}

		Image inputImg = new ImageIcon("images/input-data.png").getImage();
		inputImg = inputImg.getScaledInstance(20, 20,  java.awt.Image.SCALE_SMOOTH);
		ImageIcon inputIcon = new ImageIcon(inputImg);
		Image constraintsImg = new ImageIcon("images/constraints.png").getImage();
		constraintsImg = constraintsImg.getScaledInstance(20, 20,  java.awt.Image.SCALE_SMOOTH);
		ImageIcon constraintsIcon = new ImageIcon(constraintsImg);
		Image resultsImg = new ImageIcon("images/results.png").getImage();
		resultsImg = resultsImg.getScaledInstance(20, 20,  java.awt.Image.SCALE_SMOOTH);
		ImageIcon resultsIcon = new ImageIcon(resultsImg);

		mainDisplay_pane = new JTabbedPane(JTabbedPane.TOP);
		mainDisplay_pane.setForeground(buttonTextColor);
		mainDisplay_pane.setFont(fontButton);

		{
			MD_input_panel = new JPanel();{
				MD_input_panel.setLayout(new MigLayout("wrap 2", "[][grow]", "[][][grow][][grow][]"));
				MD_input_panel.setBackground(backgroundColor);

				MD_IP_filename = null;
				MD_IP_filename_text = new JTextField();

				MD_IP_filename_text.setFont(fontText);
				MD_IP_filename_text.setEditable(false);
				MD_IP_filename_text.setBackground(backgroundColor);

				Image openFileImg = new ImageIcon("images/folder.png").getImage();
				openFileImg = openFileImg.getScaledInstance(20, 20,  java.awt.Image.SCALE_SMOOTH);
				ImageIcon openFileIcon = new ImageIcon(openFileImg);
				MD_IP_openFile_button = new JButton(openFileIcon);
				MD_IP_openFile_button.setText("Open CSV File");
				MD_IP_openFile_button.setFont(fontButton);
				MD_IP_openFile_button.setBackground(buttonColor);
				MD_IP_openFile_button.setForeground(buttonTextColor);
				MD_IP_openFile_button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JFileChooser jfc = new JFileChooser();
						jfc.setCurrentDirectory(new File(System.getProperty("user.dir")));
						jfc.showDialog(null,"Please Select the File");
						jfc.setVisible(true);
						if(jfc.getSelectedFile()!=null){
							MD_IP_filename = jfc.getSelectedFile();
							MD_IP_filename_text.setText(MD_IP_filename.getAbsolutePath());
							MD_IP_next_btn.setEnabled(true);

							try {
								dc = new DataConverter(MD_IP_filename.getAbsolutePath());
								MD_IP_preview_text.setText(dc.getAllGenomes());
								MD_IP_previewConverted_text.setText(dc.getAllConvertedGenomes());
								System.out.println(dc.getMaxGenomeSize());
								VP_CP_from_numberModel.setMaximum(dc.getMaxGenomeSize());
								VP_CP_to_numberModel.setMaximum(dc.getMaxGenomeSize());
								if(dc.getNumberOfGenomes() == 2){
									VP_FP_rWindows_rbtn.setEnabled(true);
								} else {
									VP_FP_rWindows_rbtn.setEnabled(false);
								} VP_FP_rWindows_rbtn.setSelected(false);
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					}
				});

				
				MD_IP_preview_lbl = new JLabel();
				MD_IP_preview_lbl.setText("Original data: ");
				MD_IP_preview_lbl.setFont(fontButton);

				MD_IP_preview_text = new JEditorPane("text/html", "");
				MD_IP_preview_text.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
				MD_IP_preview_text.setFont(fontPlain);
				MD_IP_preview_text.setEditable(false);
				MD_IP_preview_scr = new JScrollPane(MD_IP_preview_text);

				MD_IP_previewConverted_text = new JEditorPane("text/html", "");
				MD_IP_previewConverted_text.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
				MD_IP_previewConverted_text.setFont(fontPlain);
				MD_IP_previewConverted_text.setEditable(false);
				MD_IP_previewConverted_scr = new JScrollPane(MD_IP_previewConverted_text);

				MD_IP_previewConverted_lbl = new JLabel();
				MD_IP_previewConverted_lbl.setText("Converted data: ");
				MD_IP_previewConverted_lbl.setFont(fontButton);

				Image img = new ImageIcon("images/arrow-point-to-right.png").getImage();
				img = img.getScaledInstance(25, 25,  java.awt.Image.SCALE_SMOOTH);
				ImageIcon nextIcon = new ImageIcon(img);
				MD_IP_next_btn = new JButton(nextIcon);
				MD_IP_next_btn.setEnabled(false);
				MD_IP_next_btn.setText("Next");
				MD_IP_next_btn.setHorizontalTextPosition(SwingConstants.LEFT);
				MD_IP_next_btn.setFont(fontButton);
				MD_IP_next_btn.setBackground(buttonColor);
				MD_IP_next_btn.setForeground(buttonTextColor);
				MD_IP_next_btn.addActionListener(new TabActions(mainDisplay_pane, 1));

				Image backImg = new ImageIcon("images/arrow-point-to-left.png").getImage();
				backImg = backImg.getScaledInstance(25, 25,  java.awt.Image.SCALE_SMOOTH);
				ImageIcon backIcon = new ImageIcon(backImg);
				MD_IP_mainMenu_btn = new JButton(backIcon);
				MD_IP_mainMenu_btn.setText("Main Menu");
				MD_IP_mainMenu_btn.setFont(fontButton);
				MD_IP_mainMenu_btn.setBackground(buttonColor);
				MD_IP_mainMenu_btn.setForeground(buttonTextColor);
				MD_IP_mainMenu_btn.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent arg0) {
						frame.remove(mainDisplay_pane);
						frame.setContentPane(homeScreen);
						frame.revalidate(); 
						frame.repaint();
					}
				});
	
				
				MD_input_panel.add(MD_IP_openFile_button);
				MD_input_panel.add(MD_IP_filename_text, "growx, span");
				
				
				MD_input_panel.add(MD_IP_preview_lbl, "growx, span");
				MD_input_panel.add(MD_IP_preview_scr, "grow, span");
				MD_input_panel.add(MD_IP_previewConverted_lbl, "growx, span");
				MD_input_panel.add(MD_IP_previewConverted_scr, "grow, span");
				MD_input_panel.add(MD_IP_mainMenu_btn, "tag left");
				MD_input_panel.add(MD_IP_next_btn, "tag right, span");
				
			}



			MD_variables_panel = new JPanel();{
				MD_variables_panel.setLayout(new MigLayout("", "[grow]", "[grow][grow][grow][]"));
				MD_variables_panel.setBackground(backgroundColor);

				VP_FP_formulation = new JPanel();{
					VP_FP_formulation.setLayout(new MigLayout("", "[grow][grow][grow][grow]", "[][]"));
					VP_FP_formulation.setBackground(backgroundColor);

					VP_FP_formulation_lbl = new JLabel();
					VP_FP_formulation_lbl.setFont(fontPlain);
					VP_FP_formulation_lbl.setText("Choose formulation to use: ");

					VP_FP_group = new ButtonGroup();

					VP_FP_basic_rbtn = new JRadioButton("General");
					VP_FP_basic_rbtn.setFont(fontPlain);
					VP_FP_basic_rbtn.setBackground(backgroundColor);
					VP_FP_commonIntervals_rbtn = new JRadioButton("Common Intervals");
					VP_FP_commonIntervals_rbtn.setFont(fontPlain);
					VP_FP_commonIntervals_rbtn.setBackground(backgroundColor);
					VP_FP_maxGap_rbtn = new JRadioButton("Max Gap");
					VP_FP_maxGap_rbtn.setFont(fontPlain);
					VP_FP_maxGap_rbtn.setBackground(backgroundColor);
					VP_FP_rWindows_rbtn = new JRadioButton("r-Windows");
					VP_FP_rWindows_rbtn.setFont(fontPlain);
					VP_FP_rWindows_rbtn.setBackground(backgroundColor);

					VP_FP_group.add(VP_FP_basic_rbtn);
					VP_FP_group.add(VP_FP_commonIntervals_rbtn);
					VP_FP_group.add(VP_FP_maxGap_rbtn);
					VP_FP_group.add(VP_FP_rWindows_rbtn);

					VP_FP_formulation.add(VP_FP_formulation_lbl, "growx, span");
					VP_FP_formulation.add(VP_FP_basic_rbtn, "grow");
					VP_FP_formulation.add(VP_FP_commonIntervals_rbtn, "grow");
					VP_FP_formulation.add(VP_FP_maxGap_rbtn, "grow");
					VP_FP_formulation.add(VP_FP_rWindows_rbtn, "grow, span");

					ActionListener formulationListener = new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e) {
							if (VP_FP_basic_rbtn.isSelected() || VP_FP_commonIntervals_rbtn.isSelected() || VP_FP_maxGap_rbtn.isSelected() || VP_FP_rWindows_rbtn.isSelected()){
								VP_CP_from_spnr.setEnabled(true);
								VP_CP_to_spnr.setEnabled(true);
								VP_CP_missing_spnr.setEnabled(true);
								VP_CP_additional_spnr.setEnabled(true);

								setBasicFormulation(false);
								setCommonIntervals(false);
								setMaxGap(false);
								setrWindows(false);
							}
							else{
								VP_CP_from_spnr.setEnabled(false);
								VP_CP_to_spnr.setEnabled(false);
								VP_CP_missing_spnr.setEnabled(false);
								VP_CP_additional_spnr.setEnabled(false);
								VP_CP_gapSize_spnr.setEnabled(false);
								VP_CP_rWindowSize_spnr.setEnabled(false);

							}

							if(VP_FP_basic_rbtn.isSelected()){
								setBasicFormulation(true);
							}

							if(VP_FP_commonIntervals_rbtn.isSelected()){
								VP_CP_missing_spnr.setEnabled(false);
								VP_CP_missing_spnr.setValue(1);
								VP_CP_additional_spnr.setEnabled(false);
								VP_CP_additional_spnr.setValue(1);
								setCommonIntervals(true);
							}

							if(VP_FP_maxGap_rbtn.isSelected()){
								VP_CP_missing_spnr.setEnabled(false);
								VP_CP_missing_spnr.setValue(0);
								VP_CP_additional_spnr.setEnabled(false);
								VP_CP_additional_spnr.setValue(0);
								VP_CP_gapSize_spnr.setEnabled(true);
								setMaxGap(true);
							} else VP_CP_gapSize_spnr.setEnabled(false);

							if(VP_FP_rWindows_rbtn.isSelected()){
								VP_CP_from_spnr.setEnabled(false);	
								VP_CP_to_spnr.setEnabled(true);
								VP_CP_missing_spnr.setEnabled(false);
								VP_CP_missing_spnr.setValue(1);
								VP_CP_additional_spnr.setEnabled(false);
								VP_CP_additional_spnr.setValue(0);
								VP_CP_rWindowSize_spnr.setEnabled(true);
								setrWindows(true);
							} else VP_CP_rWindowSize_spnr.setEnabled(false);
						}
					};

					VP_FP_basic_rbtn.addActionListener(formulationListener);
					VP_FP_commonIntervals_rbtn.addActionListener(formulationListener);
					VP_FP_maxGap_rbtn.addActionListener(formulationListener);
					VP_FP_rWindows_rbtn.addActionListener(formulationListener);

				}

				VP_CP_constraints = new JPanel();{
					VP_CP_constraints.setLayout(new MigLayout("", "[grow][grow][grow]", "[]"));
					VP_CP_constraints.setBackground(backgroundColor);

					VP_CP_constraints_lbl = new JLabel();
					VP_CP_constraints_lbl.setText("Input constraints: ");
					VP_CP_constraints_lbl.setFont(fontPlain);

					VP_CP_sizeRange_lbl = new JLabel();
					VP_CP_sizeRange_lbl.setText("Size Range: ");
					VP_CP_sizeRange_lbl.setFont(fontPlain);

					VP_CP_from_numberModel = new SpinnerNumberModel(2, 2, 2, 1);
					VP_CP_from_spnr = new JSpinner(VP_CP_from_numberModel);
					//VP_CP_from_spnr = new JSpinner();
					VP_CP_from_spnr.setFont(fontPlain);
					VP_CP_from_spnr.addChangeListener(new ChangeListener(){
						@SuppressWarnings("rawtypes")
						public void stateChanged(ChangeEvent arg0) {
							VP_CP_to_numberModel.setMinimum((Comparable) VP_CP_from_spnr.getValue());
							if((int)VP_CP_to_spnr.getValue() < (int)VP_CP_from_spnr.getValue()){
								VP_CP_to_spnr.setValue(VP_CP_from_spnr.getValue());
							}
						}
					});

					VP_CP_to_numberModel = new SpinnerNumberModel(2, 2, 2, 1);
					VP_CP_to_spnr = new JSpinner(VP_CP_to_numberModel);
					VP_CP_to_spnr.setFont(fontPlain);

					VP_CP_additionalWeight_lbl = new JLabel();
					VP_CP_additionalWeight_lbl.setText("Integer Weights (+): ");
					VP_CP_additionalWeight_lbl.setFont(fontPlain);

					VP_CP_additional_spnr = new JSpinner();
					VP_CP_additional_spnr.setFont(fontPlain);

					VP_CP_missingWeight_lbl = new JLabel();
					VP_CP_missingWeight_lbl.setText("Integer Weights (-): ");
					VP_CP_missingWeight_lbl.setFont(fontPlain);

					VP_CP_missing_spnr = new JSpinner();
					VP_CP_missing_spnr.setFont(fontPlain);

					VP_CP_gapSize_lbl = new JLabel();
					VP_CP_gapSize_lbl.setText("Gap Size: ");
					VP_CP_gapSize_lbl.setFont(fontPlain);

					VP_CP_gapSize_spnr = new JSpinner();
					VP_CP_gapSize_spnr.setFont(fontPlain);

					VP_CP_rWindowSize_Lbl = new JLabel();
					VP_CP_rWindowSize_Lbl.setText("k Size: ");
					VP_CP_rWindowSize_Lbl.setFont(fontPlain);

					VP_CP_rWindowSize_spnr = new JSpinner();

					VP_CP_from_spnr.setEnabled(false);
					VP_CP_to_spnr.setEnabled(false);
					VP_CP_missing_spnr.setEnabled(false);
					VP_CP_additional_spnr.setEnabled(false);
					VP_CP_gapSize_spnr.setEnabled(false);
					VP_CP_rWindowSize_spnr.setEnabled(false);


					VP_CP_constraints.add(VP_CP_constraints_lbl, "growx, span");
					VP_CP_constraints.add(VP_CP_sizeRange_lbl, "tag right");
					VP_CP_constraints.add(VP_CP_from_spnr, "grow");
					VP_CP_constraints.add(VP_CP_to_spnr, "grow, span");	
					VP_CP_constraints.add(VP_CP_additionalWeight_lbl, "tag right");		
					VP_CP_constraints.add(VP_CP_additional_spnr, "grow, span");	
					VP_CP_constraints.add(VP_CP_missingWeight_lbl, "tag right");
					VP_CP_constraints.add(VP_CP_missing_spnr, "grow, span");
					VP_CP_constraints.add(VP_CP_gapSize_lbl, "tag right");
					VP_CP_constraints.add(VP_CP_gapSize_spnr, "grow, span");	
					VP_CP_constraints.add(VP_CP_rWindowSize_Lbl, "tag right");
					VP_CP_constraints.add(VP_CP_rWindowSize_spnr, "grow, span");

					VP_CP_from_spnr.addChangeListener(new ChangeListener(){
						public void stateChanged(ChangeEvent arg0) {
							setSizeRangeLower((int)VP_CP_from_spnr.getValue());
						}
					});

					VP_CP_to_spnr.addChangeListener(new ChangeListener(){
						public void stateChanged(ChangeEvent e) {
							setSizeRangeHigher((int)VP_CP_to_spnr.getValue());
						}
					});

					VP_CP_additional_spnr.addChangeListener(new ChangeListener(){
						public void stateChanged(ChangeEvent e) {
							setAdditionalGeneWeight((int)VP_CP_additional_spnr.getValue());
						}
					});

					VP_CP_missing_spnr.addChangeListener(new ChangeListener(){
						public void stateChanged(ChangeEvent e) {
							setMissingGeneWeight((int)VP_CP_missing_spnr.getValue());
						}
					});

					VP_CP_gapSize_spnr.addChangeListener(new ChangeListener(){
						public void stateChanged(ChangeEvent e) {
							setMaxGapSize((int)VP_CP_gapSize_spnr.getValue());
						}
					});

					VP_CP_rWindowSize_spnr.addChangeListener(new ChangeListener(){
						public void stateChanged(ChangeEvent e) {
							setrWindowSize((int)VP_CP_rWindowSize_spnr.getValue());
						}
					});

				}


				VP_RP_buttons = new JPanel();{
					VP_RP_buttons.setLayout(new MigLayout("", "[grow][grow]", "[]"));
					VP_RP_buttons.setBackground(backgroundColor);

					Image backImg = new ImageIcon("images/arrow-point-to-left.png").getImage();
					backImg = backImg.getScaledInstance(25, 25,  java.awt.Image.SCALE_SMOOTH);
					ImageIcon backIcon = new ImageIcon(backImg);
					VP_RP_back_btn = new JButton(backIcon);
					VP_RP_back_btn.setText("Back");
					VP_RP_back_btn.setBackground(buttonColor);
					VP_RP_back_btn.setForeground(buttonTextColor);
					VP_RP_back_btn.setFont(fontButton);
					VP_RP_back_btn.addActionListener(new TabActions(mainDisplay_pane, 0));

					Image nextImg = new ImageIcon("images/arrow-point-to-right.png").getImage();
					nextImg = nextImg.getScaledInstance(25, 25,  java.awt.Image.SCALE_SMOOTH);
					ImageIcon nextIcon = new ImageIcon(nextImg);
					VP_RP_next_btn = new JButton(nextIcon);
					VP_RP_next_btn.setText("Solve");
					VP_RP_next_btn.setBackground(buttonColor);
					VP_RP_next_btn.setForeground(buttonTextColor);
					VP_RP_next_btn.setFont(fontButton);

					VP_RP_buttons.add(VP_RP_back_btn, "tag left");
					VP_RP_buttons.add(VP_RP_next_btn, "tag right, wrap");


					VP_RP_next_btn.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							mainDisplay_pane.setEnabledAt(0, false);
							mainDisplay_pane.setEnabledAt(1, false);
							mainDisplay_pane.setEnabledAt(2, true);
							mainDisplay_pane.setSelectedIndex(2);

							if(rWindows){
								VP_CP_from_spnr.setValue(VP_CP_to_spnr.getValue());
							}
							solve = new ILPFormulation(dc.getGenomes(), dc.getGenes(), dc.getMap(), additionalGeneWeight, missingGeneWeight, sizeRangeLower, sizeRangeHigher, getMaxGapSize(), getrWindowSize(), isBasicFormulation(), isCommonIntervals(), isMaxGap(), isrWindows());
							solve.generateGeneSets();
							results = new ArrayList<GeneSet>();
							VP_CP_from_spnr.setValue(VP_CP_from_numberModel.getMinimum());
							try {
								r = window.getConnection();
								results = solve.solve(r);
								MD_RP_results_text.setText(solve.getOutput());	
							} catch (RserveException | REXPMismatchException e1) {
								e1.printStackTrace();
							}						
						}
					});
				} 



				MD_variables_panel.add(VP_FP_formulation, "growx, wrap");
				formulation_constraints_sep = new JSeparator();
				MD_variables_panel.add(formulation_constraints_sep, "growx, wrap");
				MD_variables_panel.add(VP_CP_constraints, "growx, wrap");
				MD_variables_panel.add(VP_RP_buttons, "grow, wrap, tag bottom");


			}

			MD_results_panel = new JPanel();{
				MD_results_panel.setLayout(new MigLayout("", "[grow]", "[grow][]"));
				MD_results_panel.setBackground(backgroundColor);

				MD_RP_results_text = new JEditorPane("text/html", "");
				MD_RP_results_text.setEditable(false);
				MD_RP_results_scr = new JScrollPane(MD_RP_results_text);

				Image img = new ImageIcon("images/arrow-point-to-left.png").getImage();
				img = img.getScaledInstance(25, 25,  java.awt.Image.SCALE_SMOOTH);
				ImageIcon backIcon = new ImageIcon(img);
				MD_RP_back_btn = new JButton(backIcon);
				MD_RP_back_btn.setText("Back");
				MD_RP_back_btn.setFont(fontButton);
				MD_RP_back_btn.setBackground(buttonColor);
				MD_RP_back_btn.setForeground(buttonTextColor);
				MD_RP_back_btn.addActionListener(new TabActions(mainDisplay_pane,1));
				/*{
					@Override
					public void actionPerformed(ActionEvent e) {
						mainDisplay_pane.setEnabledAt(0, false);
						mainDisplay_pane.setEnabledAt(1, true);
						mainDisplay_pane.setEnabledAt(2, false);
						mainDisplay_pane.setSelectedIndex(1);
						MD_RP_results_text.setText("");
					}
				});*/

				Image exportImg = new ImageIcon("images/export.png").getImage();
				exportImg = exportImg.getScaledInstance(25, 25,  java.awt.Image.SCALE_SMOOTH);
				ImageIcon exportIcon = new ImageIcon(exportImg);

				MD_RP_exportPDF_btn = new JButton(exportIcon);
				MD_RP_exportPDF_btn.setText("Export PDF");
				MD_RP_exportPDF_btn.setBackground(buttonColor);
				MD_RP_exportPDF_btn.setFont(fontButton);
				MD_RP_exportPDF_btn.setForeground(buttonTextColor);

				MD_RP_exportCSV_btn = new JButton(exportIcon);
				MD_RP_exportCSV_btn.setText("Export CSV");
				MD_RP_exportCSV_btn.setBackground(buttonColor);
				MD_RP_exportCSV_btn.setForeground(buttonTextColor);
				MD_RP_exportCSV_btn.setFont(fontButton);

				MD_results_panel.add(MD_RP_results_scr, "grow, span");
				MD_results_panel.add(MD_RP_back_btn, "growx");
				MD_results_panel.add(MD_RP_exportPDF_btn, "growx");
				MD_results_panel.add(MD_RP_exportCSV_btn, "growx, span");

				MD_RP_exportPDF_btn.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						JFileChooser chooser = new JFileChooser(); 
						chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
						chooser.setDialogTitle("Save as");
						chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						chooser.setAcceptAllFileFilterUsed(false);

						JFileChooser fileChooser = new JFileChooser();
						FileNameExtensionFilter pdfFilter = new FileNameExtensionFilter("PDF files", ".pdf");
						fileChooser.setFileFilter(pdfFilter);
						if (fileChooser.showSaveDialog(fileChooser) == JFileChooser.APPROVE_OPTION) {
							File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
							System.out.println(fileChooser.getSelectedFile());
							try {
								ExportPDF exportPdf = new ExportPDF(file);
								ArrayList<String> words = new ArrayList<String>();	
								words.add(MD_IP_filename.getAbsolutePath());
								if(VP_FP_commonIntervals_rbtn.isSelected()) words.add("Common Intervals");
								else if(VP_FP_basic_rbtn.isSelected()) words.add("Basic Formulation");
								else if(VP_FP_maxGap_rbtn.isSelected()) words.add("Max-gap");
								else if(VP_FP_rWindows_rbtn.isSelected()) words.add("r-Windows");
								words.add(Integer.toString((int) VP_CP_from_spnr.getValue()));
								words.add(Integer.toString((int) VP_CP_to_spnr.getValue()));
								words.add(Integer.toString((int) VP_CP_additional_spnr.getValue()));
								words.add(Integer.toString((int) VP_CP_missing_spnr.getValue()));
								if(VP_FP_maxGap_rbtn.isSelected()){
									words.add(Integer.toString((int) VP_CP_gapSize_spnr.getValue()));
								} else if(VP_FP_rWindows_rbtn.isSelected()){
									words.add(Integer.toString((int) VP_CP_rWindowSize_spnr.getValue()));
								}
								exportPdf.writeConstraints(words);
								exportPdf.writeResults(results.size(), solve.getPdfOutput());
								exportPdf.close();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
							// save to file
						}
					}
				});

				MD_RP_exportCSV_btn.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						JFileChooser chooser = new JFileChooser(); 
						chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
						chooser.setDialogTitle("Save as");
						chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						chooser.setAcceptAllFileFilterUsed(false);
						JFileChooser fileChooser = new JFileChooser();
						FileNameExtensionFilter csvFilter = new FileNameExtensionFilter("CSV files", ".csv");
						fileChooser.setFileFilter(csvFilter);
						if (fileChooser.showSaveDialog(fileChooser) == JFileChooser.APPROVE_OPTION) {
							File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
							System.out.println(fileChooser.getSelectedFile());
							try {
								ExportCSV exportCsv = new ExportCSV(file);
								for(int i=0; i<results.size(); i++){
									ArrayList<String> write = new ArrayList<String>();
									write.add("Reference Gene Set #" + (i+1));
									write.addAll(results.get(i).getGeneContentStr());
									exportCsv.write(write);
								}
								exportCsv.close();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
							// save to file
						}
					}
				});
			}
			mainDisplay_pane.addTab("Input Data", MD_input_panel);
			mainDisplay_pane.addTab("Constraints", MD_variables_panel);
			mainDisplay_pane.addTab("Results",MD_results_panel);
			mainDisplay_pane.setIconAt(0, inputIcon);
			mainDisplay_pane.setIconAt(1, constraintsIcon);
			mainDisplay_pane.setIconAt(2, resultsIcon);
			mainDisplay_pane.setFont(fontButton);
			mainDisplay_pane.setEnabledAt(1, false);
			mainDisplay_pane.setEnabledAt(2, false);

		}
	}

	public int getAdditionalGeneWeight() {
		return additionalGeneWeight;
	}

	public void setAdditionalGeneWeight(int additionalGeneWeight) {
		this.additionalGeneWeight = additionalGeneWeight;
	}

	public boolean isBasicFormulation() {
		return basicFormulation;
	}

	public void setBasicFormulation(boolean basicFormulation) {
		this.basicFormulation = basicFormulation;
	}

	public boolean isCommonIntervals() {
		return commonIntervals;
	}

	public void setCommonIntervals(boolean commonIntervals) {
		this.commonIntervals = commonIntervals;
	}

	public boolean isMaxGap() {
		return maxGap;
	}

	public void setMaxGap(boolean maxGap) {
		this.maxGap = maxGap;
	}

	public boolean isrWindows() {
		return rWindows;
	}

	public void setrWindows(boolean rWindows) {
		this.rWindows = rWindows;
	}

	public int getMissingGeneWeight() {
		return missingGeneWeight;
	}

	public void setMissingGeneWeight(int missingGeneWeight) {
		this.missingGeneWeight = missingGeneWeight;
	}

	public int getSizeRangeLower() {
		return sizeRangeLower;
	}

	public void setSizeRangeLower(int sizeRangeLower) {
		this.sizeRangeLower = sizeRangeLower;
	}

	public int getSizeRangeHigher() {
		return sizeRangeHigher;
	}

	public void setSizeRangeHigher(int sizeRangeHigher) {
		this.sizeRangeHigher = sizeRangeHigher;
	}

	public int getMaxGapSize() {
		return maxGapSize;
	}

	public void setMaxGapSize(int maxGapSize) {
		this.maxGapSize = maxGapSize;
	}

	public int getrWindowSize() {
		return rWindowSize;
	}

	public void setrWindowSize(int rWindowSize) {
		this.rWindowSize = rWindowSize;
	}

	public ILPFormulation getSolve() {
		return solve;
	}

	public void setSolve(ILPFormulation solve) {
		this.solve = solve;
	}

	public static void main(String[] args){
		Main window = new Main();
		window.frame.setVisible(true);

	}


	public int getNumOfGenomes() {
		return numOfGenomes;
	}


	public void setNumOfGenomes(int numOfGenomes) {
		this.numOfGenomes = numOfGenomes;
	}


}

