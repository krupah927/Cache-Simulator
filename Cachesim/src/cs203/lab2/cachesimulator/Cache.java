
/*************************************************************************
 * Class for cache
 * Contains methods for cache operations
 ***********************************************************************/

package cs203.lab2.cachesimulator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Cache {
	
	private static final int ADDRESS_BITS = 32;
	
	private int cacheSize, blockSize, associativeNum, numSets, numBlocks;
	private boolean isFullyAssociative;
	private int indexBits, tagBits, offsetBits;
	private List<List<String>> sets;
	private long numHits, numMiss, numAccesses;
	private double hitRate, missRate;
	private String fileName;
	private VictimCache vCache;
	//set cache object
	public Cache(int cacheSize, int blockSize, int associativeNum) {
		this.cacheSize = cacheSize;
		this.blockSize = blockSize;
		this.associativeNum = associativeNum;
		numBlocks = this.cacheSize / this.blockSize;
		if(this.associativeNum == 0) {
			isFullyAssociative = true;
			numSets = 1;
			//Reset associativeNum to number of blocks
			this.associativeNum = numBlocks;
		} else {
			isFullyAssociative = false;
			numSets = numBlocks / this.associativeNum;
		}
		
		sets = new ArrayList<List<String>>(numSets);
		for(int i = 0; i < numSets; i++) {
			sets.add(new ArrayList<String>(associativeNum));
		}
		numHits = numMiss = numAccesses = 0;
		vCache = null;
	}
	 
	//for victim cache
	public Cache(int cacheSize, int blockSize, int associativeNum, int victimCacheSize) {
		this(cacheSize, blockSize, associativeNum);
			vCache = new VictimCache(victimCacheSize);
	}
	
	//calculate misses and hits
	public void findCacheHitMissRates(String fileName) throws IOException {
		this.fileName = fileName;
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line;
		offsetBits = (int) (Math.log(blockSize)/Math.log(2));
		indexBits = (int) (Math.log(numSets)/Math.log(2));
		tagBits = ADDRESS_BITS - offsetBits - indexBits;
		while((line = reader.readLine()) != null) {
			numAccesses++;
			String[] tokens = line.split(" ");
			String address = tokens[2];
					
			//address given is 44 bits so take 32 bits by truncating 12 MSB and add offset to the address. 
			
			BigInteger addr = new BigInteger(address, 16);// BigInteger read address in hex format and converts it to an integer into BigInteger object.
			address= addr.toString(2); // converted to binary
			
			address = (address.length() == ADDRESS_BITS ? address : getValidAddress(address)); //truncated --32 
			
			addr=new BigInteger(address, 2); 
			
			
			BigInteger offset = new BigInteger(Integer.toString(Integer.parseInt(tokens[1])));
			addr = addr.add(offset);
			
			address = addr.toString(2);// BigIntger's toString(2) is used to return the binary string 
			// representation of it.
			
			address = (address.length() == ADDRESS_BITS ? address : getValidAddress(address));
			
			// Calculate offset, index and tag bits.
		
		
			String tag = address.substring(0, tagBits);
			String index = address.substring(tagBits, indexBits + tagBits);
			int setNum;
			if(index.isEmpty()) setNum = 0; 
			else { 
				setNum = Integer.parseInt(index, 2);
				//System.out.println("setnum in else"+(setNum%numSets));
			}
			checkTagInSet(tag, setNum, (vCache != null));
		}
		hitRate = (double) numHits / numAccesses;
		//missRate = (double) numMiss / numAccesses;
		missRate = 1 - hitRate;
		reader.close();
	}
	
	//append 0s in msb if required or truncate 
	private String getValidAddress(String addr) {
		int length = addr.length();
		if(length < ADDRESS_BITS) {
			int toAdd = ADDRESS_BITS - length;
			StringBuilder builder = new StringBuilder();
			for(int i = 0; i < toAdd; i++) {
				builder.append(0);
			}
			builder.append(addr);
			return builder.toString();
		} else {
			int toRemove = length - ADDRESS_BITS;
			
			return addr.substring(toRemove, length);
		}
	}
	
	//check if address exists in cache add otherwise. --> uses LRU replacement if cache is full
	private void checkTagInSet(String tag, int setNum, boolean useVictimCache) {
		List<String> list = sets.get(setNum);
		
		if(useVictimCache && list.size()==associativeNum) {
			int foundTagInCache = findTag(setNum, tag);
			if(foundTagInCache != -1) {
				list.remove(foundTagInCache);
				list.add(tag);
				numHits++;
			} else { //check in victim cache
					int foundTagInVictim = vCache.findTag(tag);
					if(foundTagInVictim != -1) {
					//Swap this element with LRU element in CPU cache		
					
						String temp = list.remove(0);
						list.add(tag);
						vCache.removeTag(foundTagInVictim);
						vCache.addToVCache(temp);
						numHits++;
			}else { //Fetch from main memory into CPU cache and the block evicted from CPU cache
					// should be stored in victim cache
					if(list.size() == associativeNum) {
						vCache.addToVCache(list.remove(0));
						
					}
					list.add(tag);
					numMiss++;
				}
			}
		} else { //Not to use Victim Cache
			// Find the tag in the list and if found, remove it from its place and then insert it at the 
			// end of list.
			int foundTagInCache = findTag(setNum, tag);
			if(foundTagInCache != -1) {
				list.remove(foundTagInCache);
				numHits++;
			} else { //insert the tag in the list
				if(list.size() == associativeNum) { // Use LRU policy to remove the oldest tag and insert new tag
					// at the end of list
					list.remove(0);
				}
				numMiss++;
			}
			list.add(tag);
		}
	}
	
	//find if address exists in cache
	private int findTag(int setNum, String tag) {
		List<String> list = sets.get(setNum);
		int i=list.indexOf(tag);
		if(i>=0)
			return i;
		return -1;
	}
	
	//display results.
	public void displayResults() {
		System.out.println("For file '" + fileName + "', following are the details:");
		System.out.println("Number of hits is: " + numHits);
		System.out.println("Number of miss is: " + numMiss);
		System.out.println("Total memory accesses is: " + numAccesses);
		System.out.println("Hit rate is: " + hitRate);
		System.out.println("Miss rate is: " + missRate);
		System.out.println("Cache size is: " + cacheSize);
		System.out.println("Block size is: " + blockSize);
		if(isFullyAssociative) System.out.println("Associativity number is: 0"); //because we had reset the value
		// of associativeNum.
		else System.out.println("Associativity number is: " + associativeNum);
		System.out.println("Number of sets is: " + numSets);
		System.out.println("Number of offset bits is: " + offsetBits);
		System.out.println("Number of index bits is: " + indexBits);
		System.out.println("Number of tag bits is: " + tagBits);
	}
	
}