package javaBackend;

import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;


public class ExportPDF {
	private File file;
	private PdfWriter writer;
	private PdfDocument pdf;
	private Document document;

	public ExportPDF(File file) throws IOException{
		try {
			this.setFile(file);
			writer = new PdfWriter(file);
			pdf = new PdfDocument(writer);
			pdf.setDefaultPageSize(PageSize.A4);
			document = new Document(pdf);
		} catch (FileNotFoundException e) {
			if(writer!=null) writer.close();
			MessageDialog md = new MessageDialog("Export PDF File", "Export failed. File may be opened in another application.", "WARNING_MESSAGE");
			md.createDialog();
			e.printStackTrace();

		}
	}  

	public void writeConstraints(ArrayList<String> words) throws IOException{
		if(writer!=null){
			PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
			document.add(new Paragraph("Dataset retrieved from: "+ words.get(0)).setFont(font));
			document.add(new Paragraph("Formulation used: "+ words.get(1)).setFont(font));
			document.add(new Paragraph("Reference gene sets range: ["+ words.get(2) + ", " + words.get(3) + "]").setFont(font));
			document.add(new Paragraph("Weight for missing genes = " + words.get(4)).setFont(font));
			document.add(new Paragraph("Weight for additional genes = " + words.get(5)).setFont(font));
			document.add(new Paragraph("Size of max gap = " + words.get(6)).setFont(font));
			document.add(new Paragraph("Size of k = " + words.get(7)).setFont(font));
			document.add(new Paragraph("\n\n"));
		}
	}

	public void writeResults(String word, ArrayList<String> words) throws IOException{
		if(writer!=null){
			PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
			Paragraph row = new Paragraph();
			row.setFont(font);
			row.add(word + ": ");
			for(int i=0; i<words.size(); i++){
				row.add(words.get(i) + " ");
			}
			document.add(row);
		}
	}

	public void writeResults(int num, ArrayList<ArrayList<String>> words) throws IOException{
		if(writer!=null){
			PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA);
			PdfFont bold = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
			PdfFont italic = PdfFontFactory.createFont(FontConstants.HELVETICA_OBLIQUE);
			

			int row = words.size()/num;
			System.out.println(words.size() + " / " + num + " = "  + row);
			for(int i=1; i<=num; i++){
				Table table = new Table(new float[words.get(i*row-row).size()]);
				table.setWidth(PageSize.A4.getWidth()-document.getLeftMargin()-document.getRightMargin());

				for(int j=(i*row - row); j<(i*row); j++){
					for(int k=0; k<words.get(j).size(); k++){
						if(k%10==0 && k!=0){
							table.startNewRow();					
							table.addCell(new Cell().setBorder(null));
						}
						if(j==(i*row - row)){
							Cell cell = new Cell().add(new Paragraph(words.get(j).get(k)));
							cell.setFont(bold);
							cell.setBorder(null);
							table.addCell(cell);
						} else{
							Cell cell = new Cell().add(new Paragraph(words.get(j).get(k)));
							if(k==0) cell.setFont(italic);
							cell.setFont(font);
							cell.setBorder(null);
							table.addCell(cell);
						}
					}
					table.startNewRow();
				}
				document.add(table);
				document.add(new Paragraph("\n"));
			}
		}

	}

	public boolean close() throws IOException{
		if(document!=null){
			MessageDialog md = new MessageDialog("Export PDF File", "PDF file created!", "INFORMATION_MESSAGE", new ImageIcon(getResourceImg("export-dialog.png").getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH)));
			md.createDialog();
			document.close();
			return true;
		} else return false;
	}


	public File getFile() {
		return file;
	}


	public void setFile(File file) {
		this.file = file;
	}
	
	private Image getResourceImg(String name) throws IOException{
		InputStream logoInput = getClass().getClassLoader().getResourceAsStream(name);
		Image logoImg = new ImageIcon(ImageIO.read(logoInput)).getImage();
		return logoImg;
	}

}