package javaBackend;
import java.util.ArrayList;

public class GeneSet {
	private ArrayList<Gene> geneSet;
	private ArrayList<String> geneContentStr;
	private ArrayList<Integer> geneContent;
	private ArrayList<String> refSetContent;
	private String strRep;
	private int strSum;
	private int positionStart;

	public GeneSet(ArrayList<Gene> genes, ArrayList<Gene> allGenes){
		this.geneSet = genes;
		this.setGeneContentStr(genes);
		this.setStrRep(allGenes);
	}

	public GeneSet(ArrayList<Gene> genes, ArrayList<Gene> allGenes, int positionStart){
		this.geneSet = genes;
		this.setGeneContentStr(genes);
		this.setStrRep(allGenes);
		this.positionStart = positionStart;
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

	public void setPositionStart(int positionStart){
		this.positionStart = positionStart;
	}

	public int getPositionStart(){
		return this.positionStart;
	}
	
	public void setGeneContentStr(ArrayList<Gene> genes){
		this.geneContentStr = new ArrayList<String>();
		this.geneContent = new ArrayList<Integer>();
		this.refSetContent = new ArrayList<String>();
		for(int i=0; i<genes.size(); i++){
			if(!geneContentStr.contains(genes.get(i).getGeneName())){
				geneContentStr.add(genes.get(i).getGeneName());
			}
			
			if(!geneContent.contains(genes.get(i).getGeneNumberRep())){
				geneContent.add(genes.get(i).getGeneNumberRep());
			}
			
			if(!refSetContent.contains(genes.get(i).getGeneName()) && genes.get(i).getGeneNumberRep()!=0){
				refSetContent.add(genes.get(i).getGeneName());
			}
		}
	}

	public ArrayList<String> getGeneContentStr(){
		return this.geneContentStr;
	}
	
	public boolean equals(Object geneSet){
		if(this.geneContentStr.equals(((GeneSet) geneSet).getGeneContentStr())) return true;		
		return false;
	} 

	public void setStrRep(ArrayList<Gene> allGenes){
		this.strRep = "";
		int strSum = 0;
		for(int i=0; i<allGenes.size(); i++){
			if(geneContent.contains(allGenes.get(i).getGeneNumberRep())){
				this.strRep += "1,";
				if(allGenes.get(i).getGeneNumberRep()!=0) strSum += 1;
			} else{
				this.strRep += "0,";
			}
		}
		
		this.setStrSum(strSum);
		this.strRep = this.strRep.substring(0, strRep.length() - 1);
	}

	public String toString(){
		return strRep;
	}
	
	public String getRefSetString(){
		String res = "";
		for(int i=0; i<refSetContent.size(); i++){
			res += refSetContent.get(i) + " ";
		} return res + "\n";
	}
	
	public ArrayList<String> getRefSetArr(){
		return this.refSetContent;
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

	public int getStrSum() {
		return strSum;
	}

	public void setStrSum(int strSum) {
		this.strSum = strSum;
	}

	public ArrayList<Integer> getGeneContent() {
		return geneContent;
	}

	public void setGeneContent(ArrayList<Integer> geneContent) {
		this.geneContent = geneContent;
	}
	
	public ArrayList<Gene> getGenes(){
		return this.geneSet;
	}
}