/*****************************************************
 * Class for victim cache
 * 
 *****************************************************/
package cs203.lab2.cachesimulator;

import java.util.ArrayList;
import java.util.List;

public class VictimCache {

	private int cacheSize;
	private List<String> victimLines;
	 
	//initializes victim cache
	public VictimCache(int size) {
		
		cacheSize = size;
		victimLines = new ArrayList<>(size);
	}
	//check if victim cache has the object
	public int findTag(String tag) {
		return victimLines.indexOf(tag);
	}
	//return size of the victim cache
	public int getsize() {
		return victimLines.size();
	}
	//add to victim cache if it has space otherwise remove the least used item and then add
	public void addToVCache(String tag) {
		
		if(victimLines.size() == cacheSize) victimLines.remove(0);
		victimLines.add(tag);
	}
	//removes the item from victim cache
	public void removeTag(int index) {
		victimLines.remove(index);
	}
	
}