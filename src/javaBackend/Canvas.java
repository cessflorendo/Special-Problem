package javaBackend;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;


public class Canvas {
	private JFrame frame;
	private JPanel panel;
	private JMenuBar menuBar;
	private Dimension screenSize;
	private int width,  height;

	public Canvas(String title, int width, int height) {
		frame = new JFrame();
		frame.setTitle(title);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(true);
		
		panel = new JPanel();
		frame.setContentPane(panel);
		

	}
}
