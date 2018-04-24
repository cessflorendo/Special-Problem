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
	private int additionalGeneWeight, missingGeneWeight, sizeRangeLower, sizeRangeHigher, maxGapSize, rWindowSize;
	private boolean basicFormulation,  commonIntervals,  maxGap,  rWindows;
	private String output;

	public ILPFormulation(ArrayList<Genome> genomes, ArrayList<Gene> genes, MapStringArrayList map, int additionalGeneWeight, int missingGeneWeight, int sizeRangeLower, int sizeRangeHigher, int maxGapSize, int rWindowSize, boolean basicFormulation, boolean commonIntervals, boolean maxGap, boolean rWindows){
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
		this.rWindowSize = rWindowSize;

		this.setAdditionalGeneWeight(additionalGeneWeight);
		this.setMissingGeneWeight(missingGeneWeight);
		this.setSizeRangeHigher(sizeRangeHigher);
		this.setSizeRangeLower(sizeRangeLower);
		this.setMaxgap(maxGapSize);
	}

	public void generateGeneSets(){
		//replaceNonHomologs();
		if (commonIntervals){
			maxGapSize = 0;
			additionalGeneWeight = 1;
			missingGeneWeight = 1;
			//replaceNonHomologs();
		}

		else if(maxGap){
			additionalGeneWeight = 0;
			missingGeneWeight = 0;
			//replaceNonHomologs();
		}

		else if(rWindows){
			additionalGeneWeight = 0;
			missingGeneWeight = 1;
			//replaceNonHomologs();
		}

		else{
			//maxgap = infinity;
		}

		for(int i=0; i<genomes.size(); i++){
			ArrayList<GeneSet> partitions = partitionList(genomes.get(i), this.maxGapSize, this.sizeRangeLower, this.sizeRangeHigher);
			genomes.get(i).setPartitions(partitions);
			referenceGeneSet.addAll(partitions);
			intervals.add(partitions);
			totalNoOfIntervals += partitions.size();
		} printPartitions(referenceGeneSet);
		printIntervals();
		//printGenes();
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
					else if(commonIntervals || maxGap){
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
					else if(commonIntervals || maxGap){
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
		if(basicFormulation || commonIntervals || maxGap){
			StringBuilder ilp = new StringBuilder("");
			//ilp.append("install.packages('lpSolve')\n");
			ilp.append("library(lpSolve)\n");
			int[] costs = new int[referenceGeneSet.size()];

			try {
				for(int i=0; i<referenceGeneSet.size(); i++){
					StringBuilder obj = new StringBuilder("obj = c(");
					StringBuilder mat = new StringBuilder("mat = matrix(c(");
					StringBuilder dir = new StringBuilder("dir = c(");
					StringBuilder rhs = new StringBuilder("rhs = c(");
					StringBuilder lpS = new StringBuilder("ilp = ");
					int initial = 0;

					for(int j=0; j<intervals.size(); j++){
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
								
								int noOfGenes = c.eval(command.toString()).asInteger();
								//System.out.println("interval: " + noOfGenes);
								
								command.append("sum(intersection==1)\n");
								int cost = c.eval(command.toString()).asInteger();
								//System.out.println("cost: " + cost);
								//System.out.println(cost);
								if(cost <= maxGapSize){
								//if(noOfGenes - cost - maxGapSize >= sizeRangeLower){
									cost = 0;
									//System.out.println(intervals.get(j).get(k));
									
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
					costs[i] = (int) Math.round(Double.parseDouble(c.eval(finalSb).asString()));

					referenceGeneSet.get(i).print();
					System.out.println(costs[i]);
					System.out.println();
				}

			} catch (Exception x) {
				System.out.println("R code error: "+x.getMessage());
			};


			return getMinimalReferenceGeneSets(c, costs);
		}
		/*

		if(maxGap){
			StringBuilder ilp = new StringBuilder("");
			//ilp.append("install.packages('lpSolve')\n");
			ilp.append("library(lpSolve)\n");
			int[] costs = new int[referenceGeneSet.size()];

			try {
				for(int i=0; i<referenceGeneSet.size(); i++){
					StringBuilder obj = new StringBuilder("obj = c(");
					StringBuilder mat = new StringBuilder("mat = matrix(c(");
					StringBuilder dir = new StringBuilder("dir = c(");
					StringBuilder rhs = new StringBuilder("rhs = c(");
					StringBuilder lpS = new StringBuilder("ilp = ");
					int initial = 0;

					for(int j=0; j<intervals.size(); j++){
						for(int l=0; l<initial; l++){
							mat.append("0,");
						}

						for(int k=0; k<intervals.get(j).size(); k++){
							StringBuilder command = new StringBuilder("");
							command.append("refset = c(" + referenceGeneSet.get(i).toString() + ")\n");
							command.append("interval = c(" + intervals.get(j).get(k).toString() + ")\n");
							command.append("intersection = refset-interval\n");
							command.append("cost =  1*sum(intersection==-1)\n");
							int cost = c.eval(command.toString()).asInteger();
							if(cost > maxGapSize) cost -= maxGapSize;
							else if(cost == maxGapSize) cost = 0;

							obj.append(cost+",");
							mat.append("1,");
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
					referenceGeneSet.get(i).print();
					//System.out.println(referenceGeneSet.get(i).getStrSum());
					//System.out.println();
					//System.out.println(finalSb);
					costs[i] = (int) Math.round(Double.parseDouble(c.eval(finalSb).asString()));
					System.out.println(costs[i]);
				}

			} catch (Exception x) {
				System.out.println("R code error: "+x.getMessage());
			};



			return getMinimalReferenceGeneSets(c, costs);
//		} */

		else return null;
	}

	private ArrayList<GeneSet> getMinimalReferenceGeneSets(RConnection c, int [] costs) throws RserveException, REXPMismatchException{
		ArrayList<GeneSet> results = new ArrayList<GeneSet>();

		int minimum = costs[0];
		for(int i=1; i<costs.length; i++){
			if(minimum > costs[i]){
				minimum = costs[i];
			}
		}

		for(int i=0; i<costs.length; i++){
			if(basicFormulation){
				if(costs[i]==minimum){
					if(!results.contains(referenceGeneSet.get(i))){
						results.add(referenceGeneSet.get(i));
						referenceGeneSet.get(i).print();
					}
				}
			} else if(commonIntervals){
				if(costs[i]==0){
					if(!results.contains(referenceGeneSet.get(i))){
						results.add(referenceGeneSet.get(i));
						referenceGeneSet.get(i).print();
					}
				}
			} else if(maxGap){
				if(costs[i]==0){
					if(!results.contains(referenceGeneSet.get(i))){
						results.add(referenceGeneSet.get(i));
					}
				}
			}


		}
		String all = "";
		String format = "D- = " + this.sizeRangeLower + "\n" +
				"D+ = " + this.sizeRangeHigher + "\n" +
				"w- = " + this.missingGeneWeight + "\n" +
				"w+ = " + this.additionalGeneWeight + "\n";
		if(maxGap){
			format += "Max gap = " + this.maxGapSize + "\n";
		} else if(rWindows){
			format += "r = " + this.rWindowSize + "\n";
		}
		if(results.size() > 0){

			for(int i=0; i<results.size(); i++){
				String res = "X = " + results.get(i).toOrigString();

				for(int j=0; j<genomes.size(); j++){
					String currentGenome = genomes.get(j).getGenomeName() + ": ";
					int min = minimum; 
					String minimumPartition = "";

					for(int k=0; k<genomes.get(j).getPartitions().size(); k++){
						int cost = computeCost(c, results.get(i), genomes.get(j).getPartitions().get(k));
						if(cost < min){
							minimumPartition = "(" + genomes.get(j).getPartitions().get(k).getPositionStart() + ") " +  genomes.get(j).getPartitions().get(k).toOrigString();
							min = cost;
						} else if (cost == min){
							minimumPartition = "(" + genomes.get(j).getPartitions().get(k).getPositionStart() + ") " +  genomes.get(j).getPartitions().get(k).toOrigString();
						}
					}
					if(!minimumPartition.equals("")){
						res += currentGenome + minimumPartition;
					} else res = "";
				}
				all += res + "\n";
			}
		} else{
			all += "Nothing to show";
		}

		all = format + "\n" + all;
		setOutput(all);
		return results;		
	}

	private int computeCost(RConnection c, GeneSet geneSet, GeneSet geneSet2) throws RserveException, REXPMismatchException{
		StringBuilder command = new StringBuilder("");
		command.append("refset = c(" + geneSet.toString() + ")\n");
		command.append("interval = c(" + geneSet2.toString() + ")\n");
		command.append("intersection = refset-interval\n");
		command.append("cost = (" + this.missingGeneWeight + "*sum(intersection==1)) + (" + this.additionalGeneWeight + "*sum(intersection==-1))\n");
		return c.eval(command.toString()).asInteger();
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

	public int getrWindowSize() {
		return rWindowSize;
	}

	public void setrWindowSize(int rWindowSize) {
		this.rWindowSize = rWindowSize;
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
