package javaBackend;
import java.util.ArrayList;

public class Genome {
	private String genomeName;
	private int genomeNumber;
	private ArrayList<Gene> genes;
	private ArrayList<Integer> genomeRep;
	
	public Genome(String genomeName, int genomeNumber){
		this.genomeName = genomeName;
		this.genomeNumber = genomeNumber;
		this.genomeRep = new ArrayList<Integer>();
	}
	
	public void setGenomeName(String genomeName){
		this.genomeName = genomeName;
	}
	
	public void setGenomeNumber(int genomeNumber){
		this.genomeNumber = genomeNumber;
	}
	
	public void setGenes(ArrayList<Gene> genes){
		this.genes = genes;
	}
	
	public String getGenomeName(){
		return this.genomeName;
	}
	
	public int getGenomeNumber(){
		return this.genomeNumber;
	}
	
	public ArrayList<Gene> getGenes(){
		return this.genes;
	}
	
	public void addGene(Gene gene){
		this.genes.add(gene);
		this.genomeRep.add(gene.getGeneNumberRep());
	}
	
	public void printGenes(){
		for(int i=0; i<genes.size(); i++){
			System.out.print(genes.get(i).getGeneName() + " ");
		} System.out.println();
	}
	
	public void printConvertedGenes(){
		for(int i=0; i<genes.size(); i++){
			System.out.print(genes.get(i).getGeneNumberRep() + " ");
		} System.out.println();
	}

	public ArrayList<Integer> getGenomeRep() {
		for(int i=0; i<genes.size(); i++){
			genomeRep.add(genes.get(i).getGeneNumberRep());
		}
		
		return this.genomeRep;
	}

	public void setGenomeRep(ArrayList<Integer> genomeRep) {
		this.genomeRep = genomeRep;
	}
	
	public Gene getGene(int i){
		return genes.get(i);
	}

	public int size() {
		return genes.size();
	}
}
