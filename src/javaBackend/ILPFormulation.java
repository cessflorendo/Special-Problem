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

	public ILPFormulation(ArrayList<Genome> genomes, ArrayList<Gene> genes, MapStringArrayList map, int additionalGeneWeight, int missingGeneWeight, int sizeRangeLower, int sizeRangeHigher, int maxGapSize, int kWindowSize, boolean basicFormulation, boolean commonIntervals, boolean maxGap, boolean rWindows){
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

		this.setAdditionalGeneWeight(additionalGeneWeight);
		this.setMissingGeneWeight(missingGeneWeight);
		this.setSizeRangeHigher(sizeRangeHigher);
		this.setSizeRangeLower(sizeRangeLower);
		this.setMaxgap(maxGapSize);
	}

	public void generateGeneSets(){
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
			missingGeneWeight = 1;
			maxGapSize = sizeRangeHigher - kWindowSize;
			sizeRangeLower = sizeRangeHigher;
		}

		for(int i=0; i<genomes.size(); i++){
			ArrayList<GeneSet> partitions = partitionList(genomes.get(i), this.maxGapSize, this.sizeRangeLower, this.sizeRangeHigher);
			genomes.get(i).setPartitions(partitions);
			referenceGeneSet.addAll(partitions);
			intervals.add(partitions);
			totalNoOfIntervals += partitions.size();
		} printPartitions(referenceGeneSet);
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
				if(size > sizeRangeHigher){ break;	}
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
							if(newGeneSet.getStrSum()>=sizeRangeLower){
								allPartitions.add(newGeneSet);
							}
							continue;
						} else if(list.get(j) == 0 && gapCounter < maxgap){
							genes.add(genome.getGene(j));
							GeneSet newGeneSet = new GeneSet(new ArrayList<Gene>(genes), this.genes, i);
							if(newGeneSet.getStrSum()>=sizeRangeLower){
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
		//ilp.append("install.packages('lpSolve')\n");
		ilp.append("library(lpSolve)\n");
		ArrayList<int[]> solutions = new ArrayList<int[]>();
		int minimumCost;
		if(basicFormulation) minimumCost = Integer.MAX_VALUE;
		else minimumCost = 0;
	
		try {
			for(int i=0; i<referenceGeneSet.size(); i++){
				StringBuilder obj = new StringBuilder("obj = c(");
				StringBuilder mat = new StringBuilder("mat = matrix(c(");
				StringBuilder dir = new StringBuilder("dir = c(");
				StringBuilder rhs = new StringBuilder("rhs = c(");
				StringBuilder lpS = new StringBuilder("ilp = ");
				int initial = 0;

				for(int j=0; j<intervals.size(); j++){
					if(intervals.get(j).size() == 0){
						System.out.println(intervals.get(j).size());
						results.clear(); 
						getOutputString(solutions);
						return results;
					} 
					for(int l=0; l<initial; l++){
						mat.append("0,");
					}

					for(int k=0; k<intervals.get(j).size(); k++){
						if(maxGap){
							StringBuilder command = new StringBuilder("");
							command.append("refset = c(" + referenceGeneSet.get(i).toString() + ")\n");
							command.append("interval = c(" + intervals.get(j).get(k).toString() + ")\n");
							command.append("intersection = refset-interval\n");
							command.append("sum(interval)\n");
							command.append("sum(intersection==1)\n");
							int cost = c.eval(command.toString()).asInteger();
							if(cost <= maxGapSize){
								cost = 0;
							}

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
				System.out.println(finalSb);
				
				int cost = (int) Math.round(Double.parseDouble(c.eval(finalSb).asString()));
				
				System.out.println(cost);
				if(cost < minimumCost){
					minimumCost = cost;
					results.clear();
					results.add(referenceGeneSet.get(i));
					int[] solution = new int[referenceGeneSet.size()];
					solution = c.eval("ilp$solution").asIntegers();
					solutions.add(solution);
				} else if(cost == minimumCost){
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
	
		getOutputString(solutions);
		return results;
	}
	
	private void getOutputString(ArrayList<int[]> solutions){
		String all = "";
		String format = "D- = " + this.sizeRangeLower + "<br>" +
				"D+ = " + this.sizeRangeHigher + "<br>" +
				"w- = " + this.missingGeneWeight + "<br>" +
				"w+ = " + this.additionalGeneWeight + "<br>";
		if(maxGap){
			format += "Max gap = " + this.maxGapSize + "<br>";
		} else if(rWindows){
			format += "r = " + this.kWindowSize + "<br>";
		}
		
		format += "<br>";
		if(results.size() > 0){
			for(int i=0; i<results.size(); i++){
				format += "<b>Reference Gene Set <i>X</i></b>: " + results.get(i).toOrigString() + "<br>";
				int genomeCount = 0;
				for(int j=0; j<solutions.get(i).length; j++){				
					if(solutions.get(i)[j] == 1){
						format 	+= "<i>" + genomes.get(genomeCount).getGenomeName() + "</i>: " 
								+ "(" + referenceGeneSet.get(j).getPositionStart() + ") "
								+ referenceGeneSet.get(j).toOrigString() + "<br>";
						genomeCount++;
					}
				} format += "<br>";
			}
		} else{
			all += "Nothing to show";
		}
		
		all = format + "\n" + all;
		setOutput(all);
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


	private void printGenes(){
		for(int i=0; i<genes.size(); i++){
			System.out.println(genes.get(i).getGeneName() + " : " + genes.get(i).getGeneNumberRep());
		}
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
}