package javaBackend;
import java.util.ArrayList;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

public class ILPFormulation {
	private ArrayList<Genome> genomes;
	private ArrayList<Gene> genes;
	private ArrayList<GeneSet> referenceGeneSet;
	private ArrayList<ArrayList<GeneSet>> intervals;
	private MapStringArrayList map;
	private int totalNoOfIntervals;
	private int additionalGeneWeight, missingGeneWeight, sizeRangeLower, sizeRangeHigher, maxGapSize, kWindowSize;
	private boolean basicFormulation,  commonIntervals,  maxGap,  rWindows;
	private ArrayList<GeneSet> results = new ArrayList<GeneSet>();
	private String output;
	private ArrayList<ArrayList<String>> pdfOutput;
	private ArrayList<int[]> solutions;
	private int minimumCost;

	public ILPFormulation(ArrayList<Genome> genomes, ArrayList<Gene> genes, MapStringArrayList map, boolean isRelaxed, int additionalGeneWeight, int missingGeneWeight, int sizeRangeLower, int sizeRangeHigher, int maxGapSize, int kWindowSize, boolean basicFormulation, boolean commonIntervals, boolean maxGap, boolean rWindows){
		this.genomes = genomes;
		this.genes = genes;
		this.referenceGeneSet = new ArrayList<GeneSet>();
		this.intervals = new ArrayList<ArrayList<GeneSet>>();
		this.map = map;
		this.totalNoOfIntervals = 0;
		this.basicFormulation = basicFormulation;
		this.commonIntervals = commonIntervals;
		this.maxGap = maxGap;
		this.rWindows = rWindows;
		this.maxGapSize = maxGapSize;
		this.kWindowSize = kWindowSize;
		this.setPdfOutput(new ArrayList<ArrayList<String>>());
		this.minimumCost = 0;

		this.setAdditionalGeneWeight(additionalGeneWeight);
		this.setMissingGeneWeight(missingGeneWeight);
		this.setSizeRangeHigher(sizeRangeHigher);
		this.setSizeRangeLower(sizeRangeLower);
		this.setMaxgap(maxGapSize);
	}

	public void generateGeneSets(){
		System.out.println("Generating sets...");
		if (commonIntervals){
			maxGapSize = 0;
			additionalGeneWeight = 1;
			missingGeneWeight = 1;
		}

		else if(maxGap){
			additionalGeneWeight = 0;
			missingGeneWeight = 0;
		}

		else if(rWindows){
			additionalGeneWeight = 0;
			missingGeneWeight = 0;
			maxGapSize = sizeRangeHigher - kWindowSize;
		}

		System.out.println("Creating partitions...");
		for(int i=0; i<genomes.size(); i++){
			ArrayList<GeneSet> partitions = partitionList(genomes.get(i), this.maxGapSize, this.sizeRangeLower, this.sizeRangeHigher);
			genomes.get(i).setPartitions(partitions);
			referenceGeneSet.addAll(partitions);
			intervals.add(partitions);
			totalNoOfIntervals += partitions.size();
		} System.out.println("Partitions created!");
		printPartitions(referenceGeneSet);
		printIntervals();
	}

	private ArrayList<GeneSet> partitionList (Genome genome, int maxgap, int sizeRangeLower, int sizeRangeHigher) {
		ArrayList<GeneSet> allPartitions = new ArrayList<GeneSet>();
		ArrayList<Integer> list = genome.getGenomeRep();

		for(int i=0; i<list.size(); i++){
			ArrayList<Gene> genes = new ArrayList<Gene>();
			int gapCounter = 0;

			for(int j=i; i<=j && j<genome.size(); j++){
				int size = j-i+1;
				if(size > sizeRangeHigher){
					break;	
				}
				else if (size < sizeRangeLower && size < sizeRangeHigher){

					if(basicFormulation){
						genes.add(genome.getGene(j));
						continue;
					}
					else if(commonIntervals || maxGap || rWindows){
						if(list.get(j)!=0 && gapCounter <= maxgap){
							genes.add(genome.getGene(j));
							continue;
						} else if(list.get(j) == 0 && gapCounter < maxgap){
							genes.add(genome.getGene(j));
							gapCounter++;
							continue;
						} else {
							break;
						}
					}
				}

				else if ((size >= sizeRangeLower && size <= sizeRangeHigher)){

					if(basicFormulation){
						genes.add(genome.getGene(j));
						GeneSet newGeneSet = new GeneSet(new ArrayList<Gene>(genes), this.genes, i);

						if(newGeneSet.getStrSum()>=sizeRangeLower){
							allPartitions.add(newGeneSet);
						}
						continue;
					}
					else if(commonIntervals || maxGap || rWindows){
						if(list.get(j)!=0 && gapCounter <= maxgap){
							genes.add(genome.getGene(j));
							GeneSet newGeneSet = new GeneSet(new ArrayList<Gene>(genes), this.genes, i);
							if(newGeneSet.getStrSum()>=sizeRangeLower - maxGapSize){
								allPartitions.add(newGeneSet);
							}
							continue;
						} else if(list.get(j) == 0 && gapCounter < maxgap){
							genes.add(genome.getGene(j));
							GeneSet newGeneSet = new GeneSet(new ArrayList<Gene>(genes), this.genes, i);
							if(newGeneSet.getStrSum()>=sizeRangeLower - maxGapSize){
								allPartitions.add(newGeneSet);
							}
							gapCounter++;
							continue;
						} else {
							break;
						}
					}
				}
			}
		}
		return allPartitions;
	}

	public void replaceNonHomologs(){
		for(int i=0; i<genomes.size(); i++){
			for(int j=0; j<genomes.get(i).getGenes().size(); j++){
				if(map.getMappingOccurence(genomes.get(i).getGenes().get(j).getGeneName()) == 1){
					genomes.get(i).getGenes().get(j).setGeneNumberRep(0);

				}
			}
		}
	}


	public ArrayList<GeneSet> solve(RConnection c) throws RserveException, REXPMismatchException{
		StringBuilder ilp = new StringBuilder("");
		ilp.append("library(lpSolve)\n");
		solutions = new ArrayList<int[]>();
		int minimumCost;
		if (commonIntervals || maxGap ) minimumCost = 0;
		else minimumCost = Integer.MAX_VALUE;


		try {

			for(int i=0; i<referenceGeneSet.size() && referenceGeneSet.get(i).getStrSum() >= (sizeRangeLower - maxGapSize) 
					; i++){
				referenceGeneSet.get(i).print();

				StringBuilder obj = new StringBuilder("obj = c(");
				StringBuilder mat = new StringBuilder("mat = matrix(c(");
				StringBuilder dir = new StringBuilder("dir = c(");
				StringBuilder rhs = new StringBuilder("rhs = c(");
				StringBuilder lpS = new StringBuilder("ilp = ");
				int initial = 0;

				for(int j=0; j<intervals.size(); j++){
					if(intervals.get(j).size() == 0){
						results.clear(); 
						return results;
					} 
					for(int l=0; l<initial; l++){
						mat.append("0,");
					}

					for(int k=0; k<intervals.get(j).size(); k++){
						if(maxGap){
							StringBuilder command = new StringBuilder("");
							command.append("refset = c(" + referenceGeneSet.get(i).toStringWithoutZero() + ")\n");
							command.append("interval = c(" + intervals.get(j).get(k).toStringWithoutZero() + ")\n");
							command.append("intersection = refset+interval\n");
							command.append("sum(intersection==2)\n");
							int common = c.eval(command.toString()).asInteger();
							int cost=0;
							if(referenceGeneSet.get(i).size() >= intervals.get(j).get(k).size()){
								if(common == referenceGeneSet.get(i).size()) cost = 0;
								else if(common >= referenceGeneSet.get(i).size() - maxGapSize) cost = 0;
								else cost = referenceGeneSet.get(i).size() - maxGapSize;
							} else{
								if(common == intervals.get(j).get(k).size()) cost = 0;
								else if(common >= intervals.get(j).get(k).size() - maxGapSize) cost = 0;
								else cost = intervals.get(j).get(k).size() - maxGapSize;
							}
							obj.append(cost+",");
							mat.append("1,");
						}

						else if(rWindows){
							StringBuilder command = new StringBuilder("");
							command.append("refset = c(" + referenceGeneSet.get(i).toStringWithoutZero() + ")\n");
							command.append("interval = c(" + intervals.get(j).get(k).toStringWithoutZero() + ")\n");
							command.append("intersection = refset+interval\n");
							command.append("sum(intersection==2)\n");
							int cost = c.eval(command.toString()).asInteger();
							if(kWindowSize <= cost) cost = 0;
							else cost = kWindowSize - cost;
							obj.append(cost+",");
							mat.append("1,");
						}

						else{
							StringBuilder command = new StringBuilder("");
							command.append("refset = c(" + referenceGeneSet.get(i).toString() + ")\n");
							command.append("interval = c(" + intervals.get(j).get(k).toString() + ")\n");  
							command.append("intersection = refset-interval\n");
							command.append("cost = (" + this.missingGeneWeight + "*sum(intersection==1)) + (" + this.additionalGeneWeight + "*sum(intersection==-1))\n");
							int cost = c.eval(command.toString()).asInteger();
							obj.append(cost+",");
							mat.append("1,");
						}
					}
					initial += intervals.get(j).size();

					for(int l=0; l<(totalNoOfIntervals-initial); l++){
						mat.append("0,");
					}

					dir.append("'=',");
					rhs.append("1,");

				} 

				obj.deleteCharAt(obj.length()-1);
				obj.append(")\n");

				mat.deleteCharAt(mat.length()-1);
				mat.append("),nrow=" + intervals.size() + ", byrow=TRUE)\n");

				dir.deleteCharAt(dir.length()-1);
				dir.append(")\n");

				rhs.deleteCharAt(rhs.length()-1);
				rhs.append(")\n");

				lpS.append("lp('min', obj, mat, dir, rhs, all.int=TRUE)\n");
				lpS.append("ilp$objval");

				String finalSb = new String();
				finalSb = ilp.toString() + obj.toString() + mat.toString() + dir.toString() + rhs.toString() + lpS.toString();
				//System.out.println(finalSb);

				int cost = (int) Math.round(Double.parseDouble(c.eval(finalSb).asString()));

				System.out.println(cost);
				if(cost < minimumCost){
					//if(cost < minimumCost){
					this.minimumCost = cost;
					minimumCost = cost;
					results.clear();
					solutions.clear();
					results.add(referenceGeneSet.get(i));
					int[] solution = new int[referenceGeneSet.size()];
					solution = c.eval("ilp$solution").asIntegers();
					solutions.add(solution);
				} else if(cost == minimumCost){
					//else if(cost == minimumCost){
					if(!results.contains(referenceGeneSet.get(i))){
						results.add(referenceGeneSet.get(i));
						int[] solution = new int[referenceGeneSet.size()];
						solution = c.eval("ilp$solution").asIntegers();
						solutions.add(solution);
					}
				}
			}

		} catch (Exception x) {
			System.out.println("R code error: "+x.getMessage());
		};
		return results;
	}



	public ArrayList<GeneSet> solveRelax(RConnection c) throws RserveException, REXPMismatchException{
		StringBuilder ilp = new StringBuilder("");
		ilp.append("library(lpSolveAPI)\n");
		solutions = new ArrayList<int[]>();
		int minimumCost;
		//if(basicFormulation) 
		minimumCost = Integer.MAX_VALUE;
		//minimumCost = 0;

		try {
			for(int i=0; i<referenceGeneSet.size() && referenceGeneSet.get(i).getStrSum() >= (sizeRangeLower - maxGapSize); i++){
				StringBuilder lpr = new StringBuilder("lpr = make.lp(0," + referenceGeneSet.size() + ")\n");
				StringBuilder obj = new StringBuilder("set.objfn(lpr, c(" );
				StringBuilder lpS = new StringBuilder("ilp = ");
				StringBuilder mat = new StringBuilder("");
				int initial = 0;

				String bounds = "set.bounds(lpr, ";
				String lowerBound = "c(";
				String upperBound = "c(";
				String columns = "c(";

				for(int j=0; j<intervals.size(); j++){
					StringBuilder constraint = new StringBuilder("add.constraint(lpr, c(");

					if(intervals.get(j).size() == 0){
						results.clear(); 
						//getOutputString(solutions);
						return results;
					} 
					for(int l=0; l<initial; l++){
						constraint.append("0,");
					}

					for(int k=0; k<intervals.get(j).size(); k++){
						lowerBound += "0,";
						upperBound += "1,";
						columns += k+1 + ","; 
						
						if(maxGap){
							StringBuilder command = new StringBuilder("");
							command.append("refset = c(" + referenceGeneSet.get(i).toStringWithoutZero() + ")\n");
							command.append("interval = c(" + intervals.get(j).get(k).toStringWithoutZero() + ")\n");
							command.append("intersection = refset+interval\n");
							command.append("sum(intersection==2)\n");
							int common = c.eval(command.toString()).asInteger();
							int cost=0;
							if(referenceGeneSet.get(i).size() >= intervals.get(j).get(k).size()){
								if(common == referenceGeneSet.get(i).size()) cost = 0;
								else if(common >= referenceGeneSet.get(i).size() - maxGapSize) cost = 0;
								else cost = referenceGeneSet.get(i).size() - maxGapSize;
							} else{
								if(common == intervals.get(j).get(k).size()) cost = 0;
								else if(common >= intervals.get(j).get(k).size() - maxGapSize) cost = 0;
								else cost = intervals.get(j).get(k).size() - maxGapSize;
							}

							obj.append(cost+",");
							constraint.append("1,");
						}
						else if(rWindows){
							StringBuilder command = new StringBuilder("");
							command.append("refset = c(" + referenceGeneSet.get(i).toStringWithoutZero() + ")\n");
							command.append("interval = c(" + intervals.get(j).get(k).toStringWithoutZero() + ")\n");
							command.append("intersection = refset+interval\n");
							command.append("sum(intersection==2)\n");
							int cost = c.eval(command.toString()).asInteger();
							if(kWindowSize <= cost) cost = 0;
							else cost = kWindowSize - cost;
							obj.append(cost+",");
							mat.append("1,");
						}

						else{
							StringBuilder command = new StringBuilder("");
							command.append("refset = c(" + referenceGeneSet.get(i).toString() + ")\n");
							command.append("interval = c(" + intervals.get(j).get(k).toString() + ")\n");
							command.append("intersection = refset-interval\n");
							command.append("cost = (" + this.missingGeneWeight + "*sum(intersection==1)) + (" + this.additionalGeneWeight + "*sum(intersection==-1))\n");
							int cost = c.eval(command.toString()).asInteger();
							obj.append(cost+",");
							constraint.append("1,");
						}
					}
					initial += intervals.get(j).size();

					for(int l=0; l<(totalNoOfIntervals-initial); l++){
						constraint.append("0,");
					}
					constraint.deleteCharAt(constraint.length()-1);
					constraint.append("), '=', 1)\n");
					mat.append(constraint.toString());
				} 
				lowerBound = lowerBound.substring(0, lowerBound.length()-1) + "), ";
				upperBound = upperBound.substring(0, upperBound.length()-1) + "), ";
				columns = columns.substring(0, columns.length()-1) + ")";
				bounds += lowerBound + upperBound + columns + ")\n";
				obj.deleteCharAt(obj.length()-1);
				obj.append("))\n");

				lpS.append("solve(lpr)\n");
				lpS.append("get.objective(lpr)\n");
				String finalSb = new String();
				finalSb = ilp.toString() + lpr.toString() + obj.toString() + mat.toString() + bounds + lpS.toString();
				System.out.println(finalSb);
				int cost = (int) Math.round(Double.parseDouble(c.eval(finalSb).asString()));
				System.out.println(cost);

				if(cost < minimumCost){
					this.minimumCost = cost;
					minimumCost = cost;
					results.clear();
					solutions.clear();
					results.add(referenceGeneSet.get(i));
					int[] solution = new int[referenceGeneSet.size() + genomes.size() + 1];
					solution = c.eval("get.primal.solution(lpr)").asIntegers();
					System.out.println(finalSb);
					
					solutions.add(solution);
				} else if(cost == minimumCost){
					if(!results.contains(referenceGeneSet.get(i))){
						results.add(referenceGeneSet.get(i));
						int[] solution = new int[referenceGeneSet.size() + genomes.size() + 1];
						solution = c.eval("get.primal.solution(lpr)").asIntegers();
						System.out.println(finalSb);
					
						solutions.add(solution);
					}
				}
				System.out.println("min cost " + this.minimumCost);
			}

		} catch (Exception x) {
			System.out.println("R code error: "+x.getMessage());
			x.printStackTrace();
		};

		return results;
	}


	public String[] getAllResults(){
		String[] res = new String[results.size()];
		for(int i=0; i<results.size(); i++){
			res[i] = results.get(i).toOrigString(); 
		}
		return res;
	}

	/*
	private void getOutputString(ArrayList<int[]> solutions){
		String all = "", format = "";
		if(results.size() > 0){
			for(int i=0; i<results.size(); i++){
				format += "<b>Reference Gene Set <i>X</i></b>: " + results.get(i).toOrigString() + "<br>";

				ArrayList<String> outputLine = new ArrayList<String>();
				outputLine.add("Reference Gene Set # " + (i+1));
				outputLine.addAll(results.get(i).getGeneContentStr());
				pdfOutput.add(outputLine);
				int genomeCount = 0;
				for(int j=0; j<solutions.get(i).length; j++){
					if(solutions.get(i)[j] == 1){
						outputLine = new ArrayList<String>();
						outputLine.add(genomes.get(genomeCount).getGenomeName());
						outputLine.addAll(compareGenes(results.get(i).getGeneContentStr(), referenceGeneSet.get(j).getGeneContentStr()));

						pdfOutput.add(outputLine);
						format 	+= "<i>" + genomes.get(genomeCount).getGenomeName() + "</i>: " 
								+ "(" + referenceGeneSet.get(j).getPositionStart() + ") "
								+ referenceGeneSet.get(j).toOrigString() + "<br>";
						genomeCount++;
					} 
				} 
				format += "<br>";
			}
		} else{
			all += "Nothing to show";
		}

		all = format + "\n" + all;
		setOutput(all);
	}
	 */

	private ArrayList<String> compareGenes(ArrayList<String> x, ArrayList<String> genome){
		ArrayList<String> res = new ArrayList<String>();
		for(int i=0; i<x.size(); i++){
			if(genome.contains(x.get(i))){
				res.add(x.get(i));
			} else res.add("--");
		}
		return res;
	}

	private void printPartitions(ArrayList<GeneSet> allPartitions){
		System.out.println("---------PARTITIONS FOR REFERENCE GENE SET---------");
		for(int i=0; i<genes.size(); i++){
			System.out.print(genes.get(i).getGeneNumberRep()+ " ");
		} System.out.println();
		for(int i=0; i<allPartitions.size(); i++){
			allPartitions.get(i).print();
			System.out.println(allPartitions.get(i));
		}

		System.out.println("-----------------------END-------------------------");
		System.out.println();
	}

	private void printIntervals(){
		System.out.println("---------------------INTERVALS---------------------");
		for(int i=0; i<intervals.size(); i++){
			System.out.println("GENOME # " + (i+1));
			for(int j=0; j<intervals.get(i).size(); j++){
				//intervals.get(i).get(j).toString();
				System.out.println(intervals.get(i).get(j));
			} System.out.println();
		}
	}

	/*
	private void printGenes(){
		for(int i=0; i<genes.size(); i++){
			System.out.println(genes.get(i).getGeneName() + " : " + genes.get(i).getGeneNumberRep());
		}
	}*/

	public int getMinimumCost(){
		return this.minimumCost;
	}

	public int getkWindowSize() {
		return kWindowSize;
	}

	public void setkWindowSize(int kWindowSize) {
		this.kWindowSize = kWindowSize;
	}

	public ArrayList<GeneSet> getReferenceGeneSet() {
		return referenceGeneSet;
	}

	public void setReferenceGeneSet(ArrayList<GeneSet> referenceGeneSet) {
		this.referenceGeneSet = referenceGeneSet;
	}

	public int getAdditionalGeneWeight() {
		return additionalGeneWeight;
	}

	public void setAdditionalGeneWeight(int additionalGeneWeight) {
		this.additionalGeneWeight = additionalGeneWeight;
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

	public void setMaxgap(int maxGapSize) {
		this.maxGapSize = maxGapSize;
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

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public ArrayList<ArrayList<String>> getPdfOutput() {
		if(results.size() > 0){
			for(int i=0; i<results.size(); i++){
				ArrayList<String> outputLine = new ArrayList<String>();
				outputLine.add("Reference Gene Set # " + (i+1));
				outputLine.addAll(results.get(i).getGeneContentStr());
				pdfOutput.add(outputLine);
				int genomeCount = 0;
				for(int j=0; j<solutions.get(i).length; j++){
					if(solutions.get(i)[j] == 1){
						outputLine = new ArrayList<String>();
						outputLine.add(genomes.get(genomeCount).getGenomeName());
						outputLine.addAll(compareGenes(results.get(i).getGeneContentStr(), referenceGeneSet.get(j).getGeneContentStr()));
						pdfOutput.add(outputLine);

						genomeCount++;
					} 
				} 
			}
		} 

		return pdfOutput;
	}

	public void setPdfOutput(ArrayList<ArrayList<String>> pdfOutput) {
		this.pdfOutput = pdfOutput;
	}

	public String [] getPositions() {
		String res[] = new String[results.size()];
		if(results.size() > 0){
			for(int i=0; i<results.size(); i++){
				res[i] = "<b> X = " + results.get(i).toOrigString() + "</b><br>";
				int genomeCount = 0;
				for(int j=0; j<solutions.get(i).length; j++){
					if(solutions.get(i)[j] == 1){
						res[i] += "<i>" + genomes.get(genomeCount).getGenomeName() + "</i>: " 
								+ "(" + referenceGeneSet.get(j).getPositionStart() + ") "
								+ referenceGeneSet.get(j).toOrigString() + "<br>";
						genomeCount++;
					} 
				}
			}
		} else{
			res[0] += "Nothing to show";
		}
		return res;
	}

	public String [] getPositionsRelaxed() {
		String res[] = new String[results.size()];
		if(results.size() > 0){
			for(int i=0; i<results.size(); i++){
				res[i] = "<b> X = " + results.get(i).toOrigString() + "</b><br>";
				int genomeCount = 0;

				for(int j=genomes.size() + 1; j<solutions.get(i).length; j++){
					if(solutions.get(i)[j] == 1){
						res[i] += "<i>" + genomes.get(genomeCount).getGenomeName() + "</i>: " 
								+ "(" + referenceGeneSet.get(j-genomes.size()-1).getPositionStart() + ") "
								+ referenceGeneSet.get(j-genomes.size()-1).toOrigString() + "<br>";
						genomeCount++;
					} 
				}
			}
		} else{
			res[0] += "Nothing to show";
		}

		return res;
	}
}

