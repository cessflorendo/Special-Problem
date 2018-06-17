package javaBackend;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class MessageDialog {
	String title;
	String message;
	String type;
	Icon icon = null;
	
	public MessageDialog(String title, String message){
		this.title = title;
		this.message = message;
		this.type = "WARNING_MESSAGE";
	}
	
	public MessageDialog(String title, String message, String type){
		this.title = title;
		this.message = message;
		this.type = type;
	}
	
	public MessageDialog(String title, String message, String type, Icon icon){
		this.title = title;
		this.message = message;
		this.type = type;
		this.icon = icon;
	}
	
	public void createDialog() throws IOException{
		JFrame frame = new JFrame();
		frame.setIconImage(new ImageIcon(getResourceImg("icon1.png").getScaledInstance(200, 200, java.awt.Image.SCALE_SMOOTH)).getImage());
		frame.setFont(new Font("Dialog Input", Font.PLAIN, 16));
		UIManager.put("OptionPane.background", Color.decode("#F5F6F9"));
		UIManager.put("Panel.background", Color.decode("#F5F6F9"));
		
		if(type.equals("WARNING_MESSAGE")){
			if(icon!=null){
				JOptionPane.showMessageDialog(frame, message, title, JOptionPane.WARNING_MESSAGE, icon);
			}
			else JOptionPane.showMessageDialog(frame, message, title, JOptionPane.WARNING_MESSAGE, new ImageIcon(getResourceImg("warning.png").getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH)));
		} 
		else if (type.equals("ERROR_MESSAGE")){
			if(icon!=null){
				JOptionPane.showMessageDialog(frame, message, title, JOptionPane.ERROR_MESSAGE, icon);
			}
			else JOptionPane.showMessageDialog(frame, message, title, JOptionPane.ERROR_MESSAGE, new ImageIcon(getResourceImg("cancel.png").getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH)));
		}
		else if (type.equals("PLAIN_MESSAGE")){
			JOptionPane.showMessageDialog(frame, message, title, JOptionPane.PLAIN_MESSAGE, icon);
		}
		else if (type.equals("INFORMATION_MESSAGE")){
			if(icon!=null){
				JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE, icon);
			}
			else JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getResourceImg("info.png").getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH)));
		}
		else{
			JOptionPane.showMessageDialog(frame, message);
		}
	}
	
	public boolean createQuestionDialog() throws IOException{
		JFrame frame = new JFrame();
		frame.setIconImage(new ImageIcon(getResourceImg("icon1.png").getScaledInstance(200, 200, java.awt.Image.SCALE_SMOOTH)).getImage());
		frame.setFont(new Font("Dialog Input", Font.PLAIN, 16));
		UIManager.put("OptionPane.background", Color.decode("#F5F6F9"));
		UIManager.put("Panel.background", Color.decode("#F5F6F9"));
		
		int dialogButton = JOptionPane.YES_NO_OPTION;
		int dialogResult = JOptionPane.showConfirmDialog(frame, message, title, dialogButton);
		if(dialogResult == 0) {
		  return true;
		} else {
		  return false;
		} 
		
	}
	
	private Image getResourceImg(String name) throws IOException{
		InputStream logoInput = getClass().getClassLoader().getResourceAsStream(name);
		Image logoImg = new ImageIcon(ImageIO.read(logoInput)).getImage();
		return logoImg;
	}
}
