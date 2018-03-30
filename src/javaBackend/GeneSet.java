package javaBackend;
import java.util.ArrayList;

public class GeneSet {
	private ArrayList<Gene> geneSet;
	private ArrayList<Integer> geneContent;
	//private ArrayList<Integer> binGeneContent;
	private String strRep;

	public GeneSet(ArrayList<Gene> genes, ArrayList<Gene> allGenes){
		this.geneSet = genes;
		this.setGeneContent(genes);
		//this.setBinGeneContent(allGenes);
		this.setStrRep(allGenes);
	}

	public void add(Gene g){
		this.geneSet.add(g);
	}

	public void add(ArrayList<Gene> genes){
		this.geneSet.addAll(genes);
	}

	public int size(){
		return this.geneSet.size();
	}

	public void setGeneSet(ArrayList<Gene> genes){
		this.geneSet = genes;
	}

	public void setGeneContent(ArrayList<Gene> genes) {
		this.geneContent = new ArrayList<Integer>();
		for(int i=0; i<genes.size(); i++){
			if(!geneContent.contains(genes.get(i).getGeneNumberRep())){
				geneContent.add(genes.get(i).getGeneNumberRep());
			}
		}
	}
	
	public ArrayList<Integer> getGeneContent(){
		return this.geneContent;
	}
	
	public boolean equals(Object geneSet){
		if(this.geneContent.equals(((GeneSet) geneSet).getGeneContent())) return true;		
		return false;
	}
	
	/*
	public void setBinGeneContent(ArrayList<Gene> allGenes){
		this.binGeneContent = new ArrayList<Integer>();
		for(int i=0; i<allGenes.size(); i++){
			if(geneContent.contains(allGenes.get(i).getGeneNumberRep())){
				binGeneContent.add(1);
			} else {
				binGeneContent.add(0);
			}
		}
	}
	
	public ArrayList<Integer> getBinGeneContent(){
		return this.binGeneContent;
	}*/
	
	public void setStrRep(ArrayList<Gene> allGenes){
		this.strRep = "";
		for(int i=0; i<allGenes.size(); i++){
			if(geneContent.contains(allGenes.get(i).getGeneNumberRep())){
				this.strRep += "1,";
			} else {
				this.strRep += "0,";
			}
		}
		this.strRep = this.strRep.substring(0, strRep.length() - 1);
	}
	
	public String toString(){
		return strRep;
	}

	/*
	public ArrayList<Integer> getGeneContent(){
		for(int i=0; i<geneSet.size(); i++){
			if(!geneContent.contains(geneSet.get(i).getGeneNumberRep())){
				geneContent.add(geneSet.get(i).getGeneNumberRep());
			}
		}
		return geneContent;

	}*/

	public void print(){
		for(int i=0; i<geneSet.size(); i++){
			System.out.print(geneSet.get(i).getGeneNumberRep() + " ");
		} System.out.println();
	}
	

	/*
	public void print(){
		for(int i=0; i<binGeneContent.size(); i++){
			System.out.print(binGeneContent.get(i) + " ");
		} System.out.println();
		/*
		for(int i=0; i<geneContent.size(); i++){
			System.out.print(geneContent.get(i) + " ");
		}
		System.out.println();
		System.out.println();*
	}*/
	
	/*
	public ArrayList<Integer> getIntersection(GeneSet geneSet2){
		ArrayList<Integer> intersection = new ArrayList<Integer>();
		ArrayList<Integer> binGeneSet2 = geneSet2.getBinGeneContent();
		for(int i=0; i<binGeneContent.size(); i++){
			if(binGeneContent.get(i) == binGeneSet2.get(i)){
				intersection.add(binGeneContent.get(i));
			} else intersection.add(0);
		}
		return intersection;
		
	}*/
}