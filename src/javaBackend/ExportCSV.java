package javaBackend;

import java.awt.Image;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ExportCSV {

	private FileWriter writer;

	public ExportCSV(File file) throws IOException{
		this.writer = new FileWriter(file);
	}

	public void write(ArrayList<String> words) throws IOException {
		String line = "";
		for(int i=0; i<words.size(); i++){
			line += words.get(i) + ",";
		} 
		line = line.substring(0, line.length()-1);
		line += "\n";
		
		writer.append(line);
	}

	public FileWriter getWriter() {
		return writer;
	}

	public void setWriter(FileWriter writer) {
		this.writer = writer;
	}

	public void close() {
		try {
			writer.flush();
			writer.close();
			MessageDialog md = new MessageDialog("Export CSV File", "CSV file created!", "INFORMATION_MESSAGE", new ImageIcon(getResourceImg("export-dialog.png").getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
			md.createDialog();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Image getResourceImg(String name) throws IOException{
		InputStream logoInput = getClass().getClassLoader().getResourceAsStream(name);
		Image logoImg = new ImageIcon(ImageIO.read(logoInput)).getImage();
		return logoImg;
	}
}
