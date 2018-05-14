package userInterface;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.ColorUIResource;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import componentActions.TabActions;
import javaBackend.DataConverter;
import javaBackend.ExportCSV;
import javaBackend.ExportPDF;
import javaBackend.GeneSet;
import javaBackend.ILPFormulation;
import messageDialogs.MessageDialog;
import net.miginfocom.swing.MigLayout;
import rBackend.RConnector;

public class Main{
	private int additionalGeneWeight, missingGeneWeight, sizeRangeLower, sizeRangeHigher, maxGapSize, rWindowSize;
	private boolean basicFormulation, commonIntervals, maxGap, rWindows;
	private DataConverter dc;
	private ILPFormulation solve;
	private ArrayList<GeneSet> results;
	private RConnection r;

	private int numOfGenomes = 0;
	private JFrame frame;
	private JPanel homeScreen;
	private JPanel HS_title_panel;
	private JButton HS_start_button;
	private JButton HS_instructions_button;
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
	private JPanel MD_results_panel;
	private JButton MD_IP_openFile_button;
	private JButton MD_IP_next_btn;
	private JButton MD_IP_mainMenu_btn;
	private JTextField MD_IP_filename_text;
	private File MD_IP_filename;
	private JSeparator formulation_constraints_sep;
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
	private JPanel HS_details_panel;
	private JTextPane HS_name_lbl;
	private Font fontTitle;
	private Color tabPaneColor;
	private Font largeText;
	private JButton HS_about_button;
	private JPanel MD_preview_panel;
	private JLabel MD_PP_preview_lbl;
	private JEditorPane MD_PP_preview_text;
	private JEditorPane MD_PP_previewConverted_text;
	private JLabel MD_PP_previewConverted_lbl;
	private JScrollPane MD_PP_preview_scr;
	private JScrollPane MD_PP_previewConverted_scr;
	private JButton MD_PP_back_btn;
	private JButton MD_PP_next_btn;
	private JTextArea MD_IP_preview; 
	private JScrollPane MD_IP_preview_scr;
	private JLabel MD_VP_formulation_lbl;
	private JComboBox<String> MD_VP_group;
	private Container MD_variables_panel;
	private JLabel MD_VP_constraints_lbl;
	private JLabel MD_VP_sizeRange_lbl;
	private SpinnerNumberModel MD_VP_from_numberModel;
	private JSpinner MD_VP_from_spnr;
	private SpinnerNumberModel MD_VP_to_numberModel;
	private JSpinner MD_VP_to_spnr;
	private JLabel MD_VP_additionalWeight_lbl;
	private JSpinner MD_VP_additional_spnr;
	private JLabel MD_VP_missingWeight_lbl;
	private JSpinner MD_VP_missing_spnr;
	private JLabel MD_VP_gapSize_lbl;
	private JSpinner MD_VP_gapSize_spnr;
	private JLabel MD_VP_rWindowSize_Lbl;
	private JSpinner MD_VP_rWindowSize_spnr;
	private JButton MD_VP_back_btn;
	private JButton MD_VP_next_btn;
	private JPanel aboutScreen;
	private JButton AS_back_btn;
	private JTextArea AS_about_text;
	private Color mainColor;
	private JList<String> MD_results_list;
	private JProgressBar progressBar;
	private JProgressBar MD_input_progressBar;
	private JProgressBar MD_VP_progressBar;
	private JSeparator constraints_prog_sep;
	private Font fontPlainBold;
	private JEditorPane MD_RP_constraints_text;
	private JScrollPane MD_results_list_scr;
	private JLabel MD_RP_results_lbl;
	private JLabel MD_RP_positions_lbl;
	private JLabel MD_RP_constraints_lbl;
	private String[] res;
	public Main() throws IOException{
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
		fontTitle = new Font("Courier New", Font.BOLD, 80);
		fontButton = new Font("Dialog Input", Font.BOLD, 20);
		fontText = new Font("Dialog Input", Font.PLAIN, 22);
		fontPlain =  new Font("Dialog Input", Font.PLAIN, 16);
		fontPlainBold =  new Font("Dialog Input", Font.BOLD, 18);
		largeText = new Font("Dialog Input", Font.PLAIN, 22);

		mainColor = Color.decode("#55ACEE");
		backgroundColor = Color.decode("#F5F6F9");
		buttonColor = Color.decode("#292F33");
		buttonTextColor = Color.decode("#E1E8ED");
		tabPaneColor = Color.decode("#CCD6DD");
		UIManager.put("TabbedPane.unselectedBackground", new ColorUIResource(tabPaneColor));
		UIManager.put("TabbedPane.foreground", new ColorUIResource(buttonTextColor));
		UIManager.put("TabbedPane.selectedForeground", new ColorUIResource(mainColor));
		UIManager.put("TabbedPane.selected", new ColorUIResource(buttonColor));
		UIManager.put("TabbedPane.font", fontButton);
		UIManager.put("TabbedPane.tabInsets", new Insets(10,10,10,10));

		UIManager.put("ProgressBar.background", Color.decode("#66757F"));
		UIManager.put("ProgressBar.foreground", mainColor);
		UIManager.put("ProgressBar.selectionBackground", Color.decode("#55ACEE"));
		UIManager.put("ProgressBar.selectionForeground", Color.white);

		UIManager.put("List.background", Color.decode("#66757F"));
		UIManager.put("List.foreground", Color.black);
		UIManager.put("List.selectionBackground", mainColor);
		UIManager.put("List.selectionForeground", Color.white);



		UIManager.put("ComboBox.selectionBackground", Color.decode("#CCD6DD"));

		UIManager.put("ScrollPane.background", backgroundColor);
		UIManager.put("List.background", backgroundColor);
		UIManager.put("List.foreground", Color.black);

		UIManager.put("nimbusSelection", backgroundColor);
		UIManager.put("nimbusSelectionBackground", backgroundColor);
		UIManager.put("nimbusSelectedText", Color.BLACK);
		initialize();
	}


	private void initialize() throws IOException{		
		frame = new JFrame();
		frame.setBackground(mainColor);
		frame.setTitle("InteGene");
		frame.setMinimumSize(new Dimension(800,600));
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(new ImageIcon(getResourceImg("icon1.png").getScaledInstance(200, 200, java.awt.Image.SCALE_SMOOTH)).getImage());

		progressBar = new JProgressBar();
		progressBar.setValue(0);
		progressBar.setFont(fontPlainBold);
		progressBar.setBorderPainted(false);
		progressBar.setStringPainted(true);
		frame.addWindowListener(new WindowListener(){

			public void windowActivated(WindowEvent arg0) {}
			public void windowClosed(WindowEvent arg0) {}
			public void windowClosing(WindowEvent arg0) {
				if(r!=null){
					try {
						r.shutdown();
						MessageDialog md = new MessageDialog("Shutdown", "Shutdown succesful.", "INFORMATION_MESSAGE");
						md.createDialog();
					} catch (Exception x) {
						System.out.println("R code error: "+x.getMessage());
					}
				} else{
					try {
						MessageDialog md = new MessageDialog("Shutdown", "Shutdown succesful.", "INFORMATION_MESSAGE", new ImageIcon(getResourceImg("info.png").getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH)));
						md.createDialog();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}

			public void windowDeactivated(WindowEvent arg0) {}
			public void windowDeiconified(WindowEvent arg0) {}
			public void windowIconified(WindowEvent arg0) {}
			public void windowOpened(WindowEvent arg0) {
				progressBar.setIndeterminate(true);
				TaskOpenR openR = new TaskOpenR();
				try {
					openR.doInBackground();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		homeScreen = new JPanel();
		homeScreen.setBackground(backgroundColor);
		homeScreen.setLayout(new MigLayout("insets 50 25 80 50", "[grow][grow][grow]", "[grow][fill]15[fill]"));
		frame.add(homeScreen);
		frame.setBackground(backgroundColor);


		{
			HS_title_panel = new JPanel();
			{
				HS_title_panel.setLayout(new MigLayout("insets 0 100 0 100", "[grow][grow]", "[grow]"));
				HS_title_panel.setBackground(backgroundColor);

				HS_pic_label = new JLabel();
				HS_pic_label = new JLabel(new ImageIcon(getResourceImg("icon1.png").getScaledInstance(150, 150, java.awt.Image.SCALE_SMOOTH)));
				HS_pic_label.getInsets().set(0, 50, 0, 0);

				HS_name_lbl = new JTextPane();
				HS_name_lbl.setEditable(false);
				HS_name_lbl.setMargin(new Insets(130, 0, 0, 0));
				HS_name_lbl.setBackground(backgroundColor);
				HS_name_lbl.setForeground(mainColor);
				HS_name_lbl.setText("InteGene");
				HS_name_lbl.setFont(fontTitle);

				HS_title_panel.add(HS_pic_label, "grow");
				HS_title_panel.add(HS_name_lbl, "grow");
			}

			HS_details_panel = new JPanel();
			{
				Color details = Color.decode("#E99FA6");
				HS_details_panel.setBackground(details);

			}

			HS_start_button = new JButton(new ImageIcon(getResourceImg("arrow-point-to-right.png").getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
			HS_start_button.setEnabled(false);
			HS_start_button.setText("Start");
			HS_start_button.setFont(fontButton);
			HS_start_button.setForeground(buttonTextColor);
			HS_start_button.setBackground(buttonColor);
			HS_start_button.setMargin(new Insets(5,15,5,15));
			HS_start_button.setIconTextGap(10);
			HS_start_button.setFocusPainted(false);

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

			HS_instructions_button = new JButton(new ImageIcon(getResourceImg("how.png").getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
			HS_instructions_button.setEnabled(false);
			HS_instructions_button.setText("Getting Started");
			HS_instructions_button.setFont(fontButton);
			HS_instructions_button.setForeground(buttonTextColor);
			HS_instructions_button.setBackground(buttonColor);
			HS_instructions_button.setMargin(new Insets(5,15,5,15));
			HS_instructions_button.setIconTextGap(10);
			HS_instructions_button.setFocusPainted(false);

			HS_instructions_button.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					frame.remove(frame.getContentPane());
					frame.setContentPane(instructions_pane);
					frame.revalidate(); 
					frame.repaint();
				}
			});

			HS_about_button = new JButton(new ImageIcon(getResourceImg("info.png").getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
			HS_about_button.setEnabled(false);
			HS_about_button.setText("About");
			HS_about_button.setFont(fontButton);
			HS_about_button.setForeground(buttonTextColor);
			HS_about_button.setBackground(buttonColor);
			HS_about_button.setMargin(new Insets(5,15,5,15));
			HS_about_button.setIconTextGap(10);
			HS_about_button.setFocusPainted(false);
			HS_about_button.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					frame.remove(frame.getContentPane());
					frame.setContentPane(aboutScreen);
					frame.revalidate(); 
					frame.repaint();
				}
			});
		}

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
				IP_general.setLayout(new MigLayout("insets 20", "[grow]", "[grow]20[]"));
				IP_general.setBackground(backgroundColor);

				IP_mainMenu1 = new JButton(new ImageIcon(getResourceImg("arrow-point-to-left.png").getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
				IP_mainMenu1.setText("Main Menu");
				IP_mainMenu1.setFont(fontButton);
				IP_mainMenu1.setBackground(buttonColor);
				IP_mainMenu1.setForeground(buttonTextColor);
				IP_mainMenu1.addActionListener(IP_mainMenu_listener);
				IP_mainMenu1.setMargin(new Insets(5,15,5,15));

				IP_general_text = new JEditorPane("text/html", "");
				IP_general_text.setMargin(new Insets(20,20,20,20));
				IP_general_text.setFont(fontPlain);
				String fontfamily = IP_general_text.getFont().getFamily();
				IP_general_text.setText("<html><body style=\"font-family: " + fontfamily + "\"" + getResourceFileAsString("how-to-use.txt") + "<html>");
				IP_general_text.setEditable(false);
				IP_general_text_scr = new JScrollPane(IP_general_text);
				IP_general.add(IP_general_text_scr, "grow, span");
				IP_general.add(IP_mainMenu1, "tag left");

			}
			IP_constraints = new JPanel();{
				IP_constraints.setBackground(backgroundColor);
				IP_constraints.setLayout(new MigLayout("insets 20", "[grow]", "[grow]20[]"));

				IP_mainMenu2 = new JButton(new ImageIcon(getResourceImg("arrow-point-to-left.png").getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
				IP_mainMenu2.setText("Main Menu");
				IP_mainMenu2.setFont(fontButton);
				IP_mainMenu2.setBackground(buttonColor);
				IP_mainMenu2.setForeground(buttonTextColor);
				IP_mainMenu2.setMargin(new Insets(5,15,5,15));
				IP_mainMenu2.addActionListener(IP_mainMenu_listener);

				IP_constraints_text = new JEditorPane("text/html", "");
				IP_constraints_text.setMargin(new Insets(20,20,20,20));
				IP_constraints_text.setFont(fontText);

				String fontfamily = IP_constraints_text.getFont().getFamily();
				IP_constraints_text.setText("<html><body style=\"font-family: " + fontfamily + "\"" + getResourceFileAsString("constraints.txt") + "<html>");
				IP_constraints_text.setEditable(false);
				IP_constraints_text_scr = new JScrollPane(IP_constraints_text);
				IP_constraints.add(IP_constraints_text_scr, "grow, span");
				IP_constraints.add(IP_mainMenu2, "tag left");
			}
			IP_limitations = new JPanel();{
				IP_limitations.setBackground(backgroundColor);
				IP_limitations.setLayout(new MigLayout("insets 20", "[grow]", "[grow]20[]"));
				IP_limitations.setFont(fontPlain);

				IP_mainMenu3 = new JButton(new ImageIcon(getResourceImg("arrow-point-to-left.png").getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
				IP_mainMenu3.setText("Main Menu");
				IP_mainMenu3.setFont(fontButton);
				IP_mainMenu3.setBackground(buttonColor);
				IP_mainMenu3.setForeground(buttonTextColor);
				IP_mainMenu3.setMargin(new Insets(5,15,5,15));
				IP_mainMenu3.addActionListener(IP_mainMenu_listener);

				IP_limitations_text = new JEditorPane("text/html", "");
				IP_limitations_text.setMargin(new Insets(20,20,20,20));
				IP_limitations_text.setFont(fontText);

				String fontfamily = IP_limitations_text.getFont().getFamily();
				IP_limitations_text.setText("<html><body style=\"font-family: " + fontfamily + "\"" + getResourceFileAsString("limitations.txt") + "<html>");

				IP_limitations_text.setEditable(false);
				IP_limitations_text_scr = new JScrollPane(IP_limitations_text);
				IP_limitations.add(IP_limitations_text_scr, "grow, span");
				IP_limitations.add(IP_mainMenu3, "tag left");
			}

			instructions_pane.addTab("How To Use Tool", IP_general);
			instructions_pane.addTab("Data Constraints", IP_constraints);
			instructions_pane.addTab("Limitations", IP_limitations);
			instructions_pane.setIconAt(0, new ImageIcon(getResourceImg("how.png").getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
			instructions_pane.setIconAt(1, new ImageIcon(getResourceImg("constraints.png").getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
			instructions_pane.setIconAt(2, new ImageIcon(getResourceImg("limitations.png").getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
		}

		aboutScreen = new JPanel();{
			aboutScreen.setBackground(backgroundColor);
			aboutScreen.setLayout(new MigLayout("insets 50 50 50 50", "[grow]", "[grow]20[]"));

			AS_about_text = new JTextArea();
			AS_about_text.setForeground(backgroundColor);


			AS_back_btn = new JButton(new ImageIcon(getResourceImg("arrow-point-to-left.png").getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
			AS_back_btn.setText("Main Menu");
			AS_back_btn.setFont(fontButton);
			AS_back_btn.setBackground(buttonColor);
			AS_back_btn.setForeground(buttonTextColor);
			AS_back_btn.setMargin(new Insets(5,15,5,15));
			AS_back_btn.setIconTextGap(10);
			AS_back_btn.addActionListener(IP_mainMenu_listener);

			aboutScreen.add(AS_about_text, "grow, span");
			aboutScreen.add(AS_back_btn, "tag left");

		}

		homeScreen.add(HS_title_panel, "grow, span");
		homeScreen.add(HS_start_button, "growx");
		homeScreen.add(HS_instructions_button, "growx");
		homeScreen.add(HS_about_button, "growx, span");
		homeScreen.add(progressBar, "growx, span");
		mainDisplay_pane = new JTabbedPane(JTabbedPane.TOP);
		mainDisplay_pane.setFont(fontButton);

		{
			MD_input_panel = new JPanel();{
				MD_input_panel.setLayout(new MigLayout("insets 50", "[][grow]", "[]10[]10[grow]10[]"));

				JLabel label = new JLabel();
				label.setText("Select input data: ");
				label.setFont(largeText);
				label.setForeground(Color.black);

				MD_input_panel.setBackground(backgroundColor);

				MD_IP_filename = null;


				MD_IP_openFile_button = new JButton(new ImageIcon(getResourceImg("folder.png").getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
				MD_IP_openFile_button.setText("Open CSV File");
				MD_IP_openFile_button.setMargin(new Insets(5,10,5,10));
				MD_IP_openFile_button.setFont(fontButton);
				MD_IP_openFile_button.setBackground(buttonColor);
				MD_IP_openFile_button.setForeground(buttonTextColor);
				MD_IP_openFile_button.setIconTextGap(10);

				MD_IP_filename_text = new JTextField();
				MD_IP_filename_text.setMargin(new Insets(5,15,5,15));
				MD_IP_filename_text.setFont(fontPlain);
				MD_IP_filename_text.setEditable(false);
				MD_IP_filename_text.setBackground(backgroundColor);
				MD_IP_openFile_button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JFileChooser jfc = new JFileChooser();
						jfc.setCurrentDirectory(new File(System.getProperty("user.dir")));
						jfc.showDialog(null,"Please Select the File");
						jfc.setVisible(true);
						if(jfc.getSelectedFile()!=null){
							MD_IP_filename = jfc.getSelectedFile();
							MD_IP_filename_text.setText(MD_IP_filename.getAbsolutePath());

							TaskConvert task = new TaskConvert();
							try {
								MD_input_progressBar.setIndeterminate(true);
								task.doInBackground();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					}
				});


				MD_IP_preview = new JTextArea();
				MD_IP_preview.setFont(fontPlain);
				MD_IP_preview_scr = new JScrollPane(MD_IP_preview);
				MD_IP_preview.setMargin(new Insets(10,10,10,10));

				MD_IP_next_btn = new JButton(new ImageIcon(getResourceImg("arrow-point-to-right.png").getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
				MD_IP_next_btn.setEnabled(false);
				MD_IP_next_btn.setMargin(new Insets(5,15,5,15));
				MD_IP_next_btn.setText("Preview");
				MD_IP_next_btn.setHorizontalTextPosition(SwingConstants.LEFT);
				MD_IP_next_btn.setFont(fontButton);
				MD_IP_next_btn.setBackground(buttonColor);
				MD_IP_next_btn.setForeground(buttonTextColor);
				MD_IP_next_btn.setIconTextGap(10);
				MD_IP_next_btn.addActionListener(new TabActions(mainDisplay_pane, 1));

				MD_IP_mainMenu_btn = new JButton(new ImageIcon(getResourceImg("arrow-point-to-left.png").getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
				MD_IP_mainMenu_btn.setText("Main Menu");
				MD_IP_mainMenu_btn.setMargin(new Insets(5,15,5,15));
				MD_IP_mainMenu_btn.setFont(fontButton);
				MD_IP_mainMenu_btn.setBackground(buttonColor);
				MD_IP_mainMenu_btn.setForeground(buttonTextColor);
				MD_IP_mainMenu_btn.setIconTextGap(10);
				MD_IP_mainMenu_btn.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent arg0) {
						frame.remove(mainDisplay_pane);
						frame.setContentPane(homeScreen);
						frame.revalidate(); 
						frame.repaint();
					}
				});
				MD_input_progressBar = new JProgressBar();
				MD_input_progressBar.setBorderPainted(false);
				MD_input_progressBar.setFont(fontPlainBold);
				MD_input_progressBar.setStringPainted(true);

				MD_input_panel.add(label, "span");
				MD_input_panel.add(MD_IP_openFile_button);
				MD_input_panel.add(MD_IP_filename_text, "growx, span");
				MD_input_panel.add(MD_IP_preview_scr, "grow, span");
				MD_input_panel.add(MD_input_progressBar, "growx, span");

				MD_input_panel.add(MD_IP_mainMenu_btn, "tag bottom");
				MD_input_panel.add(MD_IP_next_btn, "tag right, span");
			}

			MD_preview_panel = new JPanel();
			{
				MD_preview_panel.setLayout(new MigLayout("insets 20, wrap 4", "[grow]", "[][150px, grow]10[][150px, grow]10[]"));
				MD_preview_panel.setBackground(backgroundColor);

				MD_PP_preview_lbl = new JLabel();
				MD_PP_preview_lbl.setText("Original data: ");
				MD_PP_preview_lbl.setFont(fontButton);

				MD_PP_preview_text = new JEditorPane("text/html", "");
				MD_PP_preview_text.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
				MD_PP_preview_text.setFont(fontPlain);
				MD_PP_preview_text.setEditable(false);
				MD_PP_preview_scr = new JScrollPane(MD_PP_preview_text);

				MD_PP_previewConverted_lbl = new JLabel();
				MD_PP_previewConverted_lbl.setText("Converted data: ");
				MD_PP_previewConverted_lbl.setFont(fontButton);

				MD_PP_previewConverted_text = new JEditorPane("text/html", "");
				MD_PP_previewConverted_text.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
				MD_PP_previewConverted_text.setFont(fontPlain);
				MD_PP_previewConverted_text.setEditable(false);
				MD_PP_previewConverted_scr = new JScrollPane(MD_PP_previewConverted_text);

				MD_PP_back_btn = new JButton(new ImageIcon(getResourceImg("arrow-point-to-left.png").getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
				MD_PP_back_btn.setText("Back");
				MD_PP_back_btn.setMargin(new Insets(5,15,5,15));
				MD_PP_back_btn.setHorizontalTextPosition(SwingConstants.RIGHT);
				MD_PP_back_btn.setFont(fontButton);
				MD_PP_back_btn.setBackground(buttonColor);
				MD_PP_back_btn.setForeground(buttonTextColor);
				MD_PP_back_btn.setIconTextGap(10);
				MD_PP_back_btn.addActionListener(new TabActions(mainDisplay_pane, 0));

				MD_PP_next_btn = new JButton(new ImageIcon(getResourceImg("arrow-point-to-right.png").getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
				MD_PP_next_btn.setText("Next");
				MD_PP_next_btn.setMargin(new Insets(5,15,5,15));
				MD_PP_next_btn.setHorizontalTextPosition(SwingConstants.LEFT);
				MD_PP_next_btn.setFont(fontButton);
				MD_PP_next_btn.setBackground(buttonColor);
				MD_PP_next_btn.setForeground(buttonTextColor);
				MD_PP_next_btn.setIconTextGap(10);
				MD_PP_next_btn.addActionListener(new TabActions(mainDisplay_pane, 2));

				MD_preview_panel.add(MD_PP_preview_lbl, "growx, span, wrap");
				MD_preview_panel.add(MD_PP_preview_scr, "grow, span, wrap");
				MD_preview_panel.add(MD_PP_previewConverted_lbl, "growx, span, wrap");
				MD_preview_panel.add(MD_PP_previewConverted_scr, "grow, span, wrap");
				MD_preview_panel.add(MD_PP_back_btn, "tag left");
				MD_preview_panel.add(MD_PP_next_btn, "tag right, span");
			}


			MD_variables_panel = new JPanel();{
				MD_variables_panel.setLayout(new MigLayout("insets 50", "[grow]", "[grow][grow]20[grow][grow][grow][grow][grow][grow][grow]20[]"));
				MD_variables_panel.setBackground(backgroundColor);

				MD_VP_formulation_lbl = new JLabel();
				MD_VP_formulation_lbl.setFont(largeText);
				MD_VP_formulation_lbl.setText("Choose formulation to use: ");

				String[] types = {"General", "Common Intervals", "Max Gap", "r-Windows"};
				MD_VP_group = new JComboBox<String>(types);
				MD_VP_group.setBackground(Color.white);
				MD_VP_group.setFont(fontPlain);
				MD_VP_group.setSelectedItem(0);
				setBasicFormulation(true);

				ActionListener formulationListener = new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						MD_VP_from_spnr.setEnabled(true);
						MD_VP_to_spnr.setEnabled(true);
						MD_VP_missing_spnr.setEnabled(true);
						MD_VP_additional_spnr.setEnabled(true);
						MD_VP_gapSize_spnr.setEnabled(false);
						MD_VP_rWindowSize_spnr.setEnabled(false);

						setCommonIntervals(false);
						setMaxGap(false);
						setrWindows(false);

						if (MD_VP_group.getSelectedIndex() == 0){
							setBasicFormulation(true);
						}
						else if(MD_VP_group.getSelectedIndex() == 1){
							MD_VP_missing_spnr.setEnabled(false);
							MD_VP_missing_spnr.setValue(1);
							MD_VP_additional_spnr.setEnabled(false);
							MD_VP_additional_spnr.setValue(1);
							setBasicFormulation(false);
							setCommonIntervals(true);
						}

						else if(MD_VP_group.getSelectedIndex() == 2){
							MD_VP_missing_spnr.setEnabled(false);
							MD_VP_missing_spnr.setValue(0);
							MD_VP_additional_spnr.setEnabled(false);
							MD_VP_additional_spnr.setValue(0);
							MD_VP_gapSize_spnr.setEnabled(true);
							setBasicFormulation(false);
							setMaxGap(true);
						}

						else if(MD_VP_group.getSelectedIndex() == 3){
							MD_VP_from_spnr.setEnabled(false);	
							MD_VP_to_spnr.setEnabled(true);
							MD_VP_missing_spnr.setEnabled(false);
							MD_VP_missing_spnr.setValue(1);
							MD_VP_additional_spnr.setEnabled(false);
							MD_VP_additional_spnr.setValue(0);
							MD_VP_rWindowSize_spnr.setEnabled(true);
							setBasicFormulation(false);
							setrWindows(true);
						}

					}
				};

				MD_VP_group.addActionListener(formulationListener);

				MD_VP_constraints_lbl = new JLabel();
				MD_VP_constraints_lbl.setText("Input constraints: ");
				MD_VP_constraints_lbl.setFont(largeText);

				MD_VP_sizeRange_lbl = new JLabel();
				MD_VP_sizeRange_lbl.setText("Size Range: ");
				MD_VP_sizeRange_lbl.setFont(fontPlain);

				MD_VP_from_numberModel = new SpinnerNumberModel(2, 2, 2, 1);
				MD_VP_from_spnr = new JSpinner(MD_VP_from_numberModel);
				MD_VP_from_spnr.setFont(fontPlain);
				MD_VP_from_spnr.addChangeListener(new ChangeListener(){
					@SuppressWarnings("rawtypes")
					public void stateChanged(ChangeEvent arg0) {
						MD_VP_to_numberModel.setMinimum((Comparable) MD_VP_from_spnr.getValue());
						if((int)MD_VP_to_spnr.getValue() < (int)MD_VP_from_spnr.getValue()){
							MD_VP_to_spnr.setValue(MD_VP_from_spnr.getValue());
						}
					}
				});

				MD_VP_to_numberModel = new SpinnerNumberModel(2, 2, 2, 1);
				MD_VP_to_spnr = new JSpinner(MD_VP_to_numberModel);
				MD_VP_to_spnr.setFont(fontPlain);

				MD_VP_additionalWeight_lbl = new JLabel();
				MD_VP_additionalWeight_lbl.setText("Integer Weights (+): ");
				MD_VP_additionalWeight_lbl.setFont(fontPlain);

				MD_VP_additional_spnr = new JSpinner();
				MD_VP_additional_spnr.setFont(fontPlain);

				MD_VP_missingWeight_lbl = new JLabel();
				MD_VP_missingWeight_lbl.setText("Integer Weights (-): ");
				MD_VP_missingWeight_lbl.setFont(fontPlain);

				MD_VP_missing_spnr = new JSpinner();
				MD_VP_missing_spnr.setFont(fontPlain);

				MD_VP_gapSize_lbl = new JLabel();
				MD_VP_gapSize_lbl.setText("Gap Size: ");
				MD_VP_gapSize_lbl.setFont(fontPlain);

				MD_VP_gapSize_spnr = new JSpinner();
				MD_VP_gapSize_spnr.setFont(fontPlain);

				MD_VP_rWindowSize_Lbl = new JLabel();
				MD_VP_rWindowSize_Lbl.setText("k Size: ");
				MD_VP_rWindowSize_Lbl.setFont(fontPlain);

				MD_VP_rWindowSize_spnr = new JSpinner();
				MD_VP_rWindowSize_spnr.setFont(fontPlain);

				MD_VP_gapSize_spnr.setEnabled(false);
				MD_VP_rWindowSize_spnr.setEnabled(false);


				MD_VP_from_spnr.addChangeListener(new ChangeListener(){
					public void stateChanged(ChangeEvent arg0) {
						setSizeRangeLower((int)MD_VP_from_spnr.getValue());
					}
				});

				MD_VP_to_spnr.addChangeListener(new ChangeListener(){
					public void stateChanged(ChangeEvent e) {
						setSizeRangeHigher((int)MD_VP_to_spnr.getValue());
					}
				});

				MD_VP_additional_spnr.addChangeListener(new ChangeListener(){
					public void stateChanged(ChangeEvent e) {
						setAdditionalGeneWeight((int)MD_VP_additional_spnr.getValue());
					}
				});

				MD_VP_missing_spnr.addChangeListener(new ChangeListener(){
					public void stateChanged(ChangeEvent e) {
						setMissingGeneWeight((int)MD_VP_missing_spnr.getValue());
					}
				});

				MD_VP_gapSize_spnr.addChangeListener(new ChangeListener(){
					public void stateChanged(ChangeEvent e) {
						setMaxGapSize((int)MD_VP_gapSize_spnr.getValue());
					}
				});

				MD_VP_rWindowSize_spnr.addChangeListener(new ChangeListener(){
					public void stateChanged(ChangeEvent e) {
						setrWindowSize((int)MD_VP_rWindowSize_spnr.getValue());
					}
				});


				MD_VP_back_btn = new JButton(new ImageIcon(getResourceImg("arrow-point-to-left.png").getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
				MD_VP_back_btn.setText("Back");
				MD_VP_back_btn.setMargin(new Insets(5,15,5,15));
				MD_VP_back_btn.setBackground(buttonColor);
				MD_VP_back_btn.setForeground(buttonTextColor);
				MD_VP_back_btn.setFont(fontButton);
				MD_VP_back_btn.setIconTextGap(10);
				MD_VP_back_btn.addActionListener(new TabActions(mainDisplay_pane, 1));

				MD_VP_next_btn = new JButton(new ImageIcon(getResourceImg("arrow-point-to-right.png").getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
				MD_VP_next_btn.setText("Solve");
				MD_VP_next_btn.setHorizontalTextPosition(SwingConstants.LEFT);
				MD_VP_next_btn.setMargin(new Insets(5,15,5,15));
				MD_VP_next_btn.setBackground(buttonColor);
				MD_VP_next_btn.setForeground(buttonTextColor);
				MD_VP_next_btn.setFont(fontButton);
				MD_VP_next_btn.setIconTextGap(10);
				
				ListSelectionListener listener = new ListSelectionListener(){
					@Override
					public void valueChanged(ListSelectionEvent e) {
						int selected = MD_results_list.getSelectedIndex();
						System.out.println(selected);
						MD_RP_results_text.setText(res[selected]);
					}
				};

				MD_VP_next_btn.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						if(rWindows){
							MD_VP_from_spnr.setValue(MD_VP_to_spnr.getValue());
						}
						MD_results_list.removeListSelectionListener(listener);
						MD_RP_results_text.removeAll();
						TaskSolve task = new TaskSolve();
						MD_results_list.clearSelection();
						try {
							MD_VP_progressBar.setIndeterminate(true);
							task.doInBackground();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						
						MD_results_list.addListSelectionListener(listener);
						

					}
				});

				MD_variables_panel.add(MD_VP_formulation_lbl, "growx, span");
				MD_variables_panel.add(MD_VP_group, "grow, span");
				formulation_constraints_sep = new JSeparator();
				MD_variables_panel.add(formulation_constraints_sep, "grow, span");
				MD_variables_panel.add(MD_VP_constraints_lbl, "growx, span");
				MD_variables_panel.add(MD_VP_sizeRange_lbl, "tag right");
				MD_variables_panel.add(MD_VP_from_spnr, "grow");
				MD_variables_panel.add(MD_VP_to_spnr, "grow, span");	
				MD_variables_panel.add(MD_VP_additionalWeight_lbl, "tag right");		
				MD_variables_panel.add(MD_VP_additional_spnr, "grow, span");	
				MD_variables_panel.add(MD_VP_missingWeight_lbl, "tag right");
				MD_variables_panel.add(MD_VP_missing_spnr, "grow, span");
				MD_variables_panel.add(MD_VP_gapSize_lbl, "tag right");
				MD_variables_panel.add(MD_VP_gapSize_spnr, "grow, span");	
				MD_variables_panel.add(MD_VP_rWindowSize_Lbl, "tag right");
				MD_variables_panel.add(MD_VP_rWindowSize_spnr, "grow, span");


				constraints_prog_sep = new JSeparator();
				MD_variables_panel.add(constraints_prog_sep, "grow, span");
				MD_VP_progressBar = new JProgressBar();
				MD_VP_progressBar.setBorderPainted(false);
				MD_VP_progressBar.setFont(fontPlainBold);
				MD_VP_progressBar.setValue(0);
				MD_VP_progressBar.setStringPainted(true);

				MD_variables_panel.add(MD_VP_progressBar, " grow, span");

				MD_variables_panel.add(MD_VP_back_btn, " tag left");
				MD_variables_panel.add(MD_VP_next_btn, " tag right, span");



			}

			MD_results_panel = new JPanel();{
				MD_results_panel.setLayout(new MigLayout("insets 20", "[grow]10[grow]", "[][grow]10[]10[]10[]"));
				MD_results_panel.setBackground(backgroundColor);

				MD_RP_results_lbl = new JLabel();
				MD_RP_results_lbl.setBackground(backgroundColor);
				MD_RP_results_lbl.setText("Resulting Clusters: ");
				MD_RP_results_lbl.setFont(largeText);

				MD_RP_positions_lbl = new JLabel();
				MD_RP_positions_lbl.setBackground(backgroundColor);
				MD_RP_positions_lbl.setText("Clusters in genomes: ");
				MD_RP_positions_lbl.setFont(largeText);

				MD_RP_constraints_lbl = new JLabel();
				MD_RP_constraints_lbl.setBackground(backgroundColor);
				MD_RP_constraints_lbl.setText("Input Constraints: ");
				MD_RP_constraints_lbl.setFont(largeText);


				MD_results_list = new JList<String>();
				MD_results_list.setFont(fontPlainBold);
				DefaultListCellRenderer renderer = (DefaultListCellRenderer)MD_results_list.getCellRenderer();
				renderer.setHorizontalAlignment(JLabel.CENTER);
				MD_results_list_scr = new JScrollPane(MD_results_list);
				MD_results_list.setBackground(backgroundColor);
				MD_results_list.setForeground(mainColor);

				MD_RP_results_text = new JEditorPane("text/html", "");
				MD_RP_results_text.setEditable(false);
				MD_RP_results_scr = new JScrollPane(MD_RP_results_text);
				MD_RP_results_text.setBackground(Color.decode("#E1E8ED"));

				MD_RP_constraints_text = new JEditorPane("text/html", "");
				MD_RP_constraints_text.setBackground(Color.decode("#CCD6DD"));
				MD_RP_constraints_text.setForeground(Color.white);

				JPanel buttons = new JPanel();
				buttons.setLayout(new MigLayout("insets 0", "[grow]10[grow]10[grow]", "[grow]"));
				buttons.setBackground(backgroundColor);

				MD_RP_back_btn = new JButton(new ImageIcon(getResourceImg("arrow-point-to-left.png").getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
				MD_RP_back_btn.setText("Back");
				MD_RP_back_btn.setMargin(new Insets(5,15,5,15));
				MD_RP_back_btn.setFont(fontButton);
				MD_RP_back_btn.setBackground(buttonColor);
				MD_RP_back_btn.setForeground(buttonTextColor);
				MD_RP_back_btn.setIconTextGap(10);
				MD_RP_back_btn.addActionListener(new TabActions(mainDisplay_pane,2));

				MD_RP_exportPDF_btn = new JButton(new ImageIcon(getResourceImg("export.png").getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
				MD_RP_exportPDF_btn.setText("Export PDF");
				MD_RP_exportPDF_btn.setMargin(new Insets(5,15,5,15));
				MD_RP_exportPDF_btn.setBackground(buttonColor);
				MD_RP_exportPDF_btn.setFont(fontButton);
				MD_RP_exportPDF_btn.setForeground(buttonTextColor);
				MD_RP_exportPDF_btn.setIconTextGap(10);

				MD_RP_exportCSV_btn = new JButton(new ImageIcon(getResourceImg("export.png").getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
				MD_RP_exportCSV_btn.setText("Export CSV");
				MD_RP_exportCSV_btn.setMargin(new Insets(5,15,5,15));
				MD_RP_exportCSV_btn.setBackground(buttonColor);
				MD_RP_exportCSV_btn.setForeground(buttonTextColor);
				MD_RP_exportCSV_btn.setFont(fontButton);
				MD_RP_exportCSV_btn.setIconTextGap(10);

				buttons.add(MD_RP_back_btn, "grow");
				buttons.add(MD_RP_exportPDF_btn, "grow");
				buttons.add(MD_RP_exportCSV_btn, "grow");

				MD_results_panel.add(MD_RP_results_lbl, "growx, span");
				MD_results_panel.add(MD_results_list_scr, "grow, span");
				MD_results_panel.add(MD_RP_positions_lbl, "growx");
				MD_results_panel.add(MD_RP_constraints_lbl, "grow, span");
				MD_results_panel.add(MD_RP_results_scr, "grow");
				MD_results_panel.add(MD_RP_constraints_text, "grow, span");
				MD_results_panel.add(buttons, "growx, span");
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
								if(MD_VP_group.getSelectedIndex()==1) words.add("Common Intervals");
								else if(MD_VP_group.getSelectedIndex()==0) words.add("Basic Formulation");
								else if(MD_VP_group.getSelectedIndex()==2) words.add("Max-gap");
								else if(MD_VP_group.getSelectedIndex()==3) words.add("r-Windows");
								words.add(Integer.toString((int) MD_VP_from_spnr.getValue()));
								words.add(Integer.toString((int) MD_VP_to_spnr.getValue()));
								words.add(Integer.toString((int) MD_VP_additional_spnr.getValue()));
								words.add(Integer.toString((int) MD_VP_missing_spnr.getValue()));
								if(MD_VP_group.getSelectedIndex()==2){
									words.add(Integer.toString((int) MD_VP_gapSize_spnr.getValue()));
								} else if(MD_VP_group.getSelectedIndex()==3){
									words.add(Integer.toString((int) MD_VP_rWindowSize_spnr.getValue()));
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
			mainDisplay_pane.addTab("Preview", MD_preview_panel);
			mainDisplay_pane.addTab("Constraints", MD_variables_panel);
			mainDisplay_pane.addTab("Results",MD_results_panel);
			mainDisplay_pane.setIconAt(0, new ImageIcon(getResourceImg("input-data.png").getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
			mainDisplay_pane.setIconAt(1, new ImageIcon(getResourceImg("preview.png").getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
			mainDisplay_pane.setIconAt(2, new ImageIcon(getResourceImg("constraints.png").getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
			mainDisplay_pane.setIconAt(3, new ImageIcon(getResourceImg("results.png").getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));

			mainDisplay_pane.setFont(fontButton);
			mainDisplay_pane.setEnabledAt(1, false);
			mainDisplay_pane.setEnabledAt(2, false);
			mainDisplay_pane.setEnabledAt(3, false);

		}
	}

	private Image getResourceImg(String name) throws IOException{
		InputStream logoInput = getClass().getClassLoader().getResourceAsStream(name);
		Image logoImg = new ImageIcon(ImageIO.read(logoInput)).getImage();
		return logoImg;
	}

	private String getResourceFileAsString(String fileName) {
		InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
		if (is != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			return reader.lines().collect(Collectors.joining(System.lineSeparator()));
		}
		return null;
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

	public static void main(String[] args) throws IOException{
		Main window = new Main();
		window.frame.setVisible(true);

	}
	public int getNumOfGenomes() {
		return numOfGenomes;
	}


	public void setNumOfGenomes(int numOfGenomes) {
		this.numOfGenomes = numOfGenomes;
	}

	class TaskConvert extends SwingWorker<Void, Void> {
		public Void doInBackground() throws IOException {
			Executor executor = java.util.concurrent.Executors.newSingleThreadExecutor();
			((ExecutorService) executor).submit(new Runnable() {

				public void run() { 
					try {
						MD_input_panel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						MD_input_progressBar.setString("Converting data...");
						MD_VP_progressBar.setValue(0);
						MD_VP_progressBar.setString("0%");
						dc = new DataConverter(MD_IP_filename.getAbsolutePath());
						BufferedReader br = new BufferedReader(new FileReader(MD_IP_filename));
						MD_IP_preview.setText("");
						String line = br.readLine();
						while(line != null){
							MD_IP_preview.append(line + "\n");
							line = br.readLine();
						} 
						MD_IP_preview.setCaretPosition(0);

						MD_PP_preview_text.setText(dc.getAllGenomes());
						MD_PP_preview_text.setCaretPosition(0);
						MD_PP_previewConverted_text.setText(dc.getAllConvertedGenomes());
						MD_PP_previewConverted_text.setCaretPosition(0);
						MD_VP_from_numberModel.setMaximum(dc.getMaxGenomeSize());
						MD_VP_to_numberModel.setMaximum(dc.getMaxGenomeSize());
						if(dc.getNumberOfGenomes() == 2){
							if(MD_VP_group.getItemCount()==3){
								MD_VP_group.addItem("r-Windows");
							}
						} else {
							MD_VP_group.removeItemAt(3);
						}
						this.done();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				private void done() {
					MD_input_panel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					MD_IP_next_btn.setEnabled(true);
					MD_input_progressBar.setValue(100);
					MD_input_progressBar.setIndeterminate(false);
					MD_input_progressBar.setString("Data Converted!");
					Toolkit.getDefaultToolkit().beep();

				} });

			return null;
		}

	}

	class TaskSolve extends SwingWorker<Void, Void> {
		long startTime = 0, endTime = 0;
		public Void doInBackground() throws IOException {
			Executor executor = java.util.concurrent.Executors.newSingleThreadExecutor();
			((ExecutorService) executor).submit(new Runnable() {


				public void run() { 
					try {
						System.out.println("computing");
						MD_variables_panel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						MD_variables_panel.setEnabled(false);
						MD_VP_progressBar.setString("Calculating...");
						startTime = System.nanoTime();

						solve = new ILPFormulation(dc.getGenomes(), dc.getGenes(), dc.getMap(), additionalGeneWeight, missingGeneWeight, sizeRangeLower, sizeRangeHigher, getMaxGapSize(), getrWindowSize(), isBasicFormulation(), isCommonIntervals(), isMaxGap(), isrWindows());
						solve.generateGeneSets();
						results = new ArrayList<GeneSet>();
						results = solve.solve(r);
						endTime = System.nanoTime();
						res = solve.getPositions();

						
						MD_results_list.setListData(solve.getAllResults());
						MD_results_list.setSelectedIndex(0);
					
						MD_RP_constraints_text.setFont(fontPlain);
						String fontfamily = MD_RP_constraints_text.getFont().getFamily();
						MD_RP_constraints_text.setText(""
								+ "<html><body><table style='font-family:"+fontfamily+";font-size: 12pt ;width: 100% ; border-spacing:0px ; padding:0px';>"
								+ "<tr>"
								+ "<td colspan='2'> File location: " + MD_IP_filename.getAbsolutePath() + "</td>"
								+ "</tr>"
								+ "<tr>"
								+ "<td> Number of results: " + results.size() + "</td>"
								+ "<td> Time elapsed: " +  (double)(endTime-startTime)/1000000000.0 + "s" + "</td>"
								+ "</tr>"
								+ "<tr>"
								+ "<td colspan='2'> Formulation: " + String.valueOf(MD_VP_group.getSelectedItem()) + "</td>"
								+ "</tr>"
								+ "<tr>"
								+ "<td> D- = " + sizeRangeLower + "</td>"
								+ "<td> D+ = " + sizeRangeHigher + "</td>"
								+ "</tr>"
								+ "<tr>"
								+ "<td> w- = " + missingGeneWeight + "</td>"
								+ "<td> w+ = " + additionalGeneWeight + "</td>"
								+ "</tr>"
								+ "<tr>"
								+ "<td> Gap size = " + getMaxGapSize() + "</td>"
								+ "<td> k size = " + getrWindowSize() + "</td>"
								+ "</tr>"
								+ "</table></body></html>");

						mainDisplay_pane.setEnabledAt(0, false);
						mainDisplay_pane.setEnabledAt(1, false);
						mainDisplay_pane.setEnabledAt(2, false);
						mainDisplay_pane.setEnabledAt(3, true);
						mainDisplay_pane.setSelectedIndex(3);

					} catch (RserveException e) {
						e.printStackTrace();
					} catch (REXPMismatchException e) {
						e.printStackTrace();
					}
					done();
				}

				private void done() {
					System.out.println("Done");
					MD_variables_panel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					MD_VP_progressBar.setString("Done!");
					MD_VP_progressBar.setValue(100);
					MD_VP_progressBar.setIndeterminate(false);
				} });			
			return null;
		}
	}

	class TaskOpenR extends SwingWorker<Void, Void> {
		public Void doInBackground() throws IOException {
			Executor executor = java.util.concurrent.Executors.newSingleThreadExecutor();
			((ExecutorService) executor).submit(new Runnable() {
				public void run() { 
					homeScreen.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					progressBar.setString("Loading dependencies...");
					while(r==null){
						System.out.println("result="+RConnector.checkLocalRserve());
						try {
							r = new RConnection();
							if(r.eval("require('lpSolve') == FALSE").asString().equals("TRUE")){
								System.out.println("Installing package...");
								r.eval("install.packages('lpSolve')\n");
							} else{
								System.out.println("Package already installed. ");
							}	
						} catch (Exception x) {
							System.out.println("R code error: "+x.getMessage());
						}
					}
					done();
				}

				private void done() {
					homeScreen.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					HS_start_button.setEnabled(true);
					HS_instructions_button.setEnabled(true);
					HS_about_button.setEnabled(true);
					progressBar.setString("Dependencies loaded.");
					progressBar.setValue(100);
					progressBar.setIndeterminate(false);
					Toolkit.getDefaultToolkit().beep();

				} });

			return null;
		}
	}

}

