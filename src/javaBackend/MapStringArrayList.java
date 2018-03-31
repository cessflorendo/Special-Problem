
package javaBackend;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

public class MapStringArrayList{
	private Hashtable<String, ArrayList<Integer>> map;

	public MapStringArrayList(){
		map = new Hashtable<String, ArrayList<Integer>>();
	}
	
	public void add(String key, ArrayList<Integer> arr){
		map.put(key, arr);
	}

	public void add(String key, int value){
		if (map.containsKey(key)){
			ArrayList<Integer> values = (ArrayList<Integer>)map.get(key); 
			values.add(value);
		}

		else{
			ArrayList<Integer> values = new ArrayList<Integer>();
			values.add(value);
			map.put(key, values);
		}
	}

	public ArrayList<Integer> getValues(String key){
		return (ArrayList<Integer>)map.get(key);
	}

	public int getIntegerMapping(String key){
		return (int) map.get(key).get(0);
	}
	
	public int getMappingOccurence(String key){
		return (int) map.get(key).get(1);
	}

	public boolean containsKey(String key) {
		return map.containsKey(key);
	}
	
	public void increaseOccurence(String key){
		int inc = map.get(key).get(1);
		inc++;
		map.get(key).set(1, inc);
	}
 
	public Set<String> keySet(){
		return map.keySet();
	}
}