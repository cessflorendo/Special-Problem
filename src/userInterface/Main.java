package userInterface;
//import java.io.File;
import java.io.IOException;

//import javax.swing.JFileChooser;

import javaBackend.DataConverter;
import javaBackend.ILPFormulation;

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
	    int additionalGeneWeight = 1, missingGeneWeight = 1;
	    int sizeRangeLower = 2, sizeRangeHigher = 4;
	    int maxGapSize = 0, rWindowSize = 0;
	    boolean basicFormulation = false;
	    boolean commonIntervals = false;
	    boolean maxGap = false;
	    boolean rWindows = false;
	    
	    ILPFormulation solve = new ILPFormulation(dc.getGenomes(), dc.getGenes(), additionalGeneWeight, missingGeneWeight, sizeRangeLower, sizeRangeHigher, maxGapSize, rWindowSize, basicFormulation, commonIntervals, maxGap, rWindows);
	    solve.generateGeneSets();
	}

}
