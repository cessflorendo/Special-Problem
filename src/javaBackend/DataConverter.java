package javaBackend;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DataConverter {
	private ArrayList<Genome> allGenomes;
	private ArrayList<Genome> convertedAllGenomes;
	private ArrayList<Gene> allGenes;
	private MapStringArrayList map;
	private int maxGenomeSize; 

	public DataConverter(String filePath) throws IOException{
		System.out.println("Converting data...");
		allGenomes = new ArrayList<Genome>();
		convertedAllGenomes = new ArrayList<Genome>();
		allGenes = new ArrayList<Gene>();
		map = new MapStringArrayList();
		setMaxGenomeSize(0);

		String line = "";
		int id = 1;
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(new FileReader(filePath));

		for(int i = 0; (line = br.readLine()) != null; i++){
			String[] temp = line.split(",");
			Genome genome = new Genome(temp[0], i);
			ArrayList<Gene> genes = new ArrayList<Gene>();

			if(maxGenomeSize < (temp.length-1)){
				this.maxGenomeSize = temp.length-1;
			}
			for(int j=1; j<temp.length; j++){
				if(temp[j].equals("0")){
					ArrayList<Integer> newArr = new ArrayList<Integer>();
					newArr.add(id);
					newArr.add(1);
					map.add(Integer.toString(id), newArr);
					Gene gene = new Gene(Integer.toString(id), id);
					id++;
					genes.add(gene);
					allGenes.add(gene);
				}
				
				else if(!map.containsKey(temp[j])){
					ArrayList<Integer> newArr = new ArrayList<Integer>();
					newArr.add(id);
					newArr.add(1);
					map.add(temp[j], newArr);
					Gene gene = new Gene(temp[j], id);
					id++;
					genes.add(gene);
					allGenes.add(gene);
				} else{
					int existingId = map.getIntegerMapping(temp[j]);
					Gene gene = new Gene(temp[j], existingId);
					genes.add(gene);
					map.increaseOccurence(temp[j]);
				}
			}
			genome.setGenes(genes);
			allGenomes.add(genome);
		}
		//printStuff();
		//printAllGenes();
		replaceNonHomologs();
		System.out.println("Data converted!");
	}
	
	public void replaceNonHomologs(){
		for(int i=0; i<allGenomes.size(); i++){
			for(int j=0; j<allGenomes.get(i).getGenes().size(); j++){
				if(map.getMappingOccurence(allGenomes.get(i).getGenes().get(j).getGeneName()) == 1){
					allGenomes.get(i).getGenes().get(j).setGeneNumberRep(0);

				}
			}
		}
	}
	
	public MapStringArrayList getMap(){
		return this.map;
	}

	public ArrayList<Genome> getGenomes(){
		return this.allGenomes;
	}
	
	public int getNumberOfGenomes(){
		return this.allGenomes.size();
	}
	
	public ArrayList<Genome> getConvertedGenomes(){
		return this.convertedAllGenomes;
	}

	public ArrayList<Gene> getGenes(){
		return this.allGenes;
	}

	public void printStuff(){
		for(String name: map.keySet()){
			String key = name.toString();
			ArrayList<Integer> values = map.getValues(key);
			System.out.println(key + ": (" + values.get(0) + "," + values.get(1) + ")");    
		} 
	}

	public void printAllGenomes(){
		for(int i=0; i<allGenomes.size(); i++){
			System.out.print(allGenomes.get(i).getGenomeName() + ": ");
			allGenomes.get(i).printGenes();
		}
	}
	
	/*
	public String getAllGenomes(){
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<allGenomes.size(); i++){
			sb.append(allGenomes.get(i).getGenomeName() + ": ");
			for(int j=0; j<allGenomes.get(i).size(); j++){
				sb.append(allGenomes.get(i).getGene(j).getGeneName() + " ");
			} sb.append("\n");
		}
		return sb.toString();
	}*/
	
	public String getAllGenomes(){
		String res = "";
		for(int i=0; i<allGenomes.size(); i++){
			res += "<b>" + allGenomes.get(i).getGenomeName() + "</b> : ";
			for(int j=0; j<allGenomes.get(i).size(); j++){
				res += allGenomes.get(i).getGene(j).getGeneName() + " ";
			} res += "<br>";
		}
		return res;
	}
	
	/*
	public String getAllConvertedGenomes(){
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<allGenomes.size(); i++){
			sb.append(allGenomes.get(i).getGenomeName() + ": ");
			for(int j=0; j<allGenomes.get(i).size(); j++){
				sb.append(allGenomes.get(i).getGene(j).getGeneNumberRep() + " ");
			} sb.append("\n");
		}
		return sb.toString();
	}*/
	
	public String getAllConvertedGenomes(){
		String res = "";
		for(int i=0; i<allGenomes.size(); i++){
			res += "<b>" + allGenomes.get(i).getGenomeName() + "</b> : ";
			for(int j=0; j<allGenomes.get(i).size(); j++){
				res += allGenomes.get(i).getGene(j).getGeneNumberRep() + " ";
			} res += "<br>";
		}
		return res;
	}

	public void printAllConvertedGenomes(){
		for(int i=0; i<allGenomes.size(); i++){
			System.out.print(allGenomes.get(i).getGenomeName() + ": ");
			allGenomes.get(i).printConvertedGenes();
		}
	}

	private void printAllGenes(){
		System.out.println("All Genes");
		for(int i=0; i< allGenes.size(); i++){
			System.out.println(allGenes.get(i).getGeneName() + " : " + allGenes.get(i).getGeneNumberRep());
		}
	}

	public int getMaxGenomeSize() {
		return maxGenomeSize;
	}

	public void setMaxGenomeSize(int maxGenomeSize) {
		this.maxGenomeSize = maxGenomeSize;
	}
	


	/*
	public void printKeyValuePairs(){
		System.out.println("Gene Name - Integer Representation");
		for(String name: geneMap.keySet()){
			String key =name.toString();
			String value = geneMap.get(name).toString();  
			System.out.println(key + " " + value);  
		} 
	}*/
}
