package javaBackend;
import java.util.ArrayList;

public class GeneSetPair {
	private GeneSet set1;
	private GeneSet set2;
	private ArrayList<Integer> intersection;
	
	public GeneSetPair(GeneSet set1, GeneSet set2){
		this.set1 = set1;
		this.set2 = set2;
		//setIntersection();
	}
	
	public GeneSet getSet1() {
		return set1;
	}
	public void setSet1(GeneSet set1) {
		this.set1 = set1;
	}
	public GeneSet getSet2() {
		return set2;
	}
	public void setSet2(GeneSet set2) {
		this.set2 = set2;
	}
	
	/*
	public void setIntersection() {
		this.intersection = new ArrayList<Integer>();
		ArrayList<Integer> binGeneSet1 = set1.getBinGeneContent();
		ArrayList<Integer> binGeneSet2 = set2.getBinGeneContent();
		for(int i=0; i<set1.getBinGeneContent().size(); i++){
			if(binGeneSet1.get(i) == binGeneSet2.get(i)){
				intersection.add(binGeneSet1.get(i));
			} else intersection.add(0);
		}
	}*/
	
	public ArrayList<Integer> getIntersection(){
		return this.intersection;
	}
}
