package javaBackend;
import java.util.ArrayList;

public class ILPFormulation {
	private ArrayList<Genome> genomes;
	private ArrayList<Gene> genes;
	private ArrayList<GeneSet> referenceGeneSet;
	private ArrayList<ArrayList<GeneSet>> intervals;
	private int additionalGeneWeight, missingGeneWeight, sizeRangeLower, sizeRangeHigher, maxGapSize, rWindowSize;
	private boolean basicFormulation,  commonIntervals,  maxGap,  rWindows; 
	
	public ILPFormulation(ArrayList<Genome> genomes, ArrayList<Gene> genes, int additionalGeneWeight, int missingGeneWeight, int sizeRangeLower, int sizeRangeHigher, int maxGapSize, int rWindowSize, boolean basicFormulation, boolean commonIntervals, boolean maxGap, boolean rWindows){
		this.genomes = genomes;
		this.genes = genes;
		referenceGeneSet = new ArrayList<GeneSet>();
		intervals = new ArrayList<ArrayList<GeneSet>>();
		this.basicFormulation = basicFormulation;
		this.commonIntervals = commonIntervals;
		this.maxGap = maxGap;
		this.rWindows = rWindows;
		
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
		}
		
		else{
			//maxgap = infinity;
		}
		
		for(int i=0; i<genomes.size(); i++){
			ArrayList<GeneSet> partitions = partitionList(genomes.get(i), this.maxGapSize, this.sizeRangeLower, this.sizeRangeHigher);
			referenceGeneSet.addAll(partitions);
			intervals.add(partitions);
		} //printPartitions(referenceGeneSet);
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
			
				if( genome.size() - size < sizeRangeLower) {break; }	
				else if(size > sizeRangeHigher){ break;	}
				else if (size < sizeRangeLower && size < sizeRangeHigher){
					if(list.get(j)!=0 && gapCounter <= maxgap){
						genes.add(genome.getGene(j));
						continue;
					}
					else if(list.get(j) == 0 && gapCounter < maxgap){
						genes.add(genome.getGene(j));
						gapCounter++;
						continue;
					} 
					else {
						break;
					}
				}
				
				else if ((size >= sizeRangeLower && size <= sizeRangeHigher)){
			
					if(list.get(j)!=0 && gapCounter <= maxgap){
						genes.add(genome.getGene(j));
						allPartitions.add(new GeneSet(new ArrayList<Gene>(genes), this.genes));
						continue;
					}
					else if(list.get(j) == 0 && gapCounter < maxgap){
						genes.add(genome.getGene(j));
						allPartitions.add(new GeneSet(new ArrayList<Gene>(genes), this.genes));
						gapCounter++;
						continue;
					} 
					else {
						break;
					}
				}
			}
		}
		return allPartitions;
	}
	
	public void solve(){
	}
	
	/*
	private void printPartitions(ArrayList<GeneSet> allPartitions){
		System.out.println("---------");
		for(int i=0; i<allPartitions.size(); i++){
			allPartitions.get(i).print();
		}
	}*/
	
	private void printIntervals(){
		System.out.println("------INTERVALS------");
		for(int i=0; i<intervals.size(); i++){
			System.out.println("GENOME # " + (i+1));
			for(int j=0; j<intervals.get(i).size(); j++){
				intervals.get(i).get(j).print();
			} System.out.println();
		}
	}
	
	/*
	private void printGenes(){
		for(int i=0; i<genes.size(); i++){
			System.out.println(genes.get(i).getGeneName() + " : " + genes.get(i).getGeneNumberRep());
		}
	}*/

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
}
