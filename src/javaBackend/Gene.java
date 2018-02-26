package javaBackend;

public class Gene {
	private String geneName;
	private int geneOrder;
	private int geneRep;
	private boolean included;
	
	public Gene(String geneName, int geneNumberRep){
		this.geneName = geneName;
		this.geneRep = geneNumberRep;
		this.included = false;
	}
	
	public void setGeneName(String geneName){
		this.geneName = geneName;
	}
	
	public void setGeneOrder(int geneOrder){
		this.geneOrder = geneOrder;
	}
	
	public void setGeneNumberRep(int geneNumberRep){
		this.geneRep = geneNumberRep;
	}
	
	public String getGeneName(){
		return this.geneName;
	}
	
	public void activate(){
		this.included = true;
	}
	
	public void deactivate(){
		this.included = false;
	}
	
	public int getGeneOrder(){
		return this.geneOrder;
	}
	
	public int getGeneNumberRep(){
		return this.geneRep;
	}
	
	public boolean isActive(){
		return this.included;
	}
}
