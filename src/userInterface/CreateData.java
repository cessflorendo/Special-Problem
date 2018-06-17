package userInterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class CreateData {
	public static void main(String[] args) throws IOException{
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(new FileReader(new File("C:\\Users\\Cess\\workspace\\Special Problem\\sample data\\EcoliBsubtilis.csv")));   	
		String line = br.readLine();
		String[] genomes = line.split(",");
		Genome[] allGenomes = new Genome[genomes.length-1];
		ArrayList<String> genes = new ArrayList<String>();

		for(int i=1; i<genomes.length; i++){
			Genome newGenome = new Genome();
			newGenome.setName(genomes[i]);
			allGenomes[i-1] = newGenome;
		}

		while ((line = br.readLine()) != null){
			String[] elems = line.split(",");
			genes.add(elems[0]);
			Gene g1 = new Gene(elems[0], Integer.parseInt(elems[1]));
			Gene g2 = new Gene(elems[0], Integer.parseInt(elems[2]));
			allGenomes[0].addToGenes(g1);
			allGenomes[1].addToGenes(g2);
		}
		
        PrintWriter writer = new PrintWriter("EcoliBsubtilis.csv", "UTF-8");
        for(int i=0; i<allGenomes.length; i++){
        	writer.print(allGenomes[i].getName());
        	writer.print(",");
        	int highest = allGenomes[i].getHighest();
    		int integers[] = new int[highest];
    		
    		for(int j=0; j<highest-1; j++){
    			integers[j] = 0;
    			writer.print(allGenomes[i].isContained(j+1));
    			writer.print(",");
    			
    		} writer.print(allGenomes[i].isContained(highest));
    		writer.println();
      }
        writer.close();
	}
	
	public static class Gene{
		private int position;
		private String name;
		
		public Gene(String name, int position){
			this.position = position;
			this.name  =name;
		}
		
		public String getName(){
			return this.name;
		}
		
		public int getPosition(){
			return this.position;
		}
	}

	public static class Genome {
		private String name;
		private ArrayList<Gene> genes = new ArrayList<Gene>();

		public void setName(String name){
			this.name = name;
		}
		
		public ArrayList<Gene> getGenes(){
			return this.genes;
		}
		
		public String isContained(int pos){
			for(int i=0; i<genes.size(); i++){
				if(pos == genes.get(i).getPosition()){
					System.out.println(genes.get(i).getName());
					return genes.get(i).getName();
				}
			} return "0";
		}
		
		public String getGeneNameAt(int pos){
			for(int i=0; i<genes.size(); i++){
				if(genes.get(i).getPosition() == pos){
					return genes.get(i).getName();
				}
			}
			return null;
		}
		
		public String getName(){
			return this.name;
		}

		public void addToGenes(Gene gene){
			this.genes.add(gene);
		}

		public int getHighest() {
			int highest = 0;
			for(int i=0; i<genes.size(); i++){
				if(genes.get(i).getPosition() > highest){
					highest = genes.get(i).getPosition();
				}
			}
			return highest;
		}

	}
	
}


