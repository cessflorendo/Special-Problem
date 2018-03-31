package javaBackend;
import java.util.ArrayList;

public class GeneSet {
	private ArrayList<Gene> geneSet;
	private ArrayList<Integer> geneContent;
	private String strRep;

	public GeneSet(ArrayList<Gene> genes, ArrayList<Gene> allGenes){
		this.geneSet = genes;
		this.setGeneContent(genes);
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
	
	public String toOrigString(){
		String res = "";
		for(int i=0; i<this.geneSet.size(); i++){
			res += geneSet.get(i).getGeneName() + " ";
		} return res + "\n";
	}

	public void print(){
		for(int i=0; i<geneSet.size(); i++){
			System.out.print(geneSet.get(i).getGeneName() + " ");
		} System.out.println();
	}
}