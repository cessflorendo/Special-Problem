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
	    int additionalGeneWeight = 1, missingGeneWeight = 1;
	    int sizeRangeLower = 4, sizeRangeHigher = 4;
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
			//import
			//library(Rglpk)
			
			StringBuilder command = new StringBuilder("");
			command.append("library(lpSolve)");
			command.append("\n");
			command.append("obj = c(2,4,3)");
			command.append("\n");
			command.append("mat = matrix(c(3,2,1,4,1,3,2,2,2), nrow = 3, byrow = TRUE)");
			command.append("\n");
			command.append("dir = c('<=', '<=', '<=')");
			command.append("\n");
			command.append("rhs - c(60, 40, 80)");
			command.append("\n");
			command.append("Rglpk_solve_LP(obj, mat, dir, rhs, max=FALSE)");
			command.append("\n");
			double[][] result = c.eval(command.toString()).asDoubleMatrix();
			for(int i=0; i<result.length; i++){
				System.out.println(result[i][0]);
			}
			c.shutdown();
		} catch (Exception x) {
		};
	}

}
