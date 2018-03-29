package userInterface;
//import java.io.File;
import java.io.IOException;

import org.rosuda.REngine.Rserve.RConnection;

//import javax.swing.JFileChooser;

import javaBackend.DataConverter;
import javaBackend.ILPFormulation;
import rBackend.RConnector;

public class Main {

	public static void main(String[] args) throws IOException, CloneNotSupportedException {
		//JFileChooser jfc = new JFileChooser();
	    //jfc.showDialog(null,"Please Select the File");
	    //jfc.setVisible(true);
	    //File filename = jfc.getSelectedFile();
		//System.out.println("File name "+filename.getName());
	    
	    //DataConverter dc = new DataConverter(filename.getAbsolutePath());
		DataConverter dc = new DataConverter("C:\\Users\\Cess\\Documents\\sampledata.csv");
	    //dc.printAllGenomes();	
	    //dc.printAllConvertedGenomes();
	    dc.replaceNonHomologs();
	    dc.printAllConvertedGenomes();
	    int additionalGeneWeight = 2, missingGeneWeight = 1;
	    int sizeRangeLower = 3, sizeRangeHigher = 3;
	    int maxGapSize = 0, rWindowSize = 0;
	    boolean basicFormulation = false;
	    boolean commonIntervals = false;
	    boolean maxGap = false;
	    boolean rWindows = false;
	    
	    ILPFormulation solve = new ILPFormulation(dc.getGenomes(), dc.getGenes(), additionalGeneWeight, missingGeneWeight, sizeRangeLower, sizeRangeHigher, maxGapSize, rWindowSize, basicFormulation, commonIntervals, maxGap, rWindows);
	    solve.generateGeneSets();
	    
	    System.out.println("result="+RConnector.checkLocalRserve());
		try {
			RConnection c=new RConnection();
			solve.solve(c);
			c.shutdown();
		} catch (Exception x) {
			System.out.println("R code error: "+x.getMessage());
		};
	}

}
