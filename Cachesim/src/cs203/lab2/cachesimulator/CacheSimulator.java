/*******************************************************************************************************************
 * Cache Simulator
 * use flags to run the code : 
 * '-i' for filename '-cs' for cache size '-bs' for block size '-w' for number of ways '-vs' for victim cache size
 * 
 ********************************************************************************************************************/
package cs203.lab2.cachesimulator;

import java.io.IOException;
import java.util.stream.IntStream;

public class CacheSimulator {
	
	private static final String FILE_NAME_FLAG = "-i";
	private static final String CACHE_SIZE_FLAG = "-cs";
	private static final String BLOCK_SIZE_FLAG = "-bs";
	private static final String NUMBER_OF_WAYS_FLAG = "-w";
	private static final String VICTIM_CACHE_SIZE_FLAG = "-vs";
	private static final String HYPHEN = "-";
	private static final int MIN_CACHE_SIZE = 1024;
	private static final int MAX_CACHE_SIZE = 4194304; //4194304 is 4096 KB i.e 2^22 B
	private static final int[] VALID_BLOCK_SIZES = {2, 4, 8, 16, 32, 64};
	private static final int[] VALID_NUMBER_OF_WAYS = {0, 1, 2, 4, 8, 16};
	private static final int[] VALID_VICTIM_CACHE_SIZES = {4, 8, 16};

	private static String fileName;
	private static int cacheSize, blockSize, associativeNum, victimCacheSize;
	private static boolean victimCacheEnabled;
	
	public static void validateArguments(String[] args) {
		int argLength = args.length;
		if(argLength != 8 && argLength != 10) {
			System.out.println("Incorrect arguments are provided..");
			System.out.println(" -i <filename> -cs <cache size> -bs <block size> -w <# of ways>");
			System.exit(0);
		}
		for(int i = 0; i < argLength; i++) {
			String arg = args[i];
			if(arg.equals(FILE_NAME_FLAG)) {
				String fileNm = args[++i];
				if(fileNm.startsWith(HYPHEN)) {
					System.out.println("Invalid file name: " + fileName);
					System.exit(0);
				} else fileName = fileNm;
			} else if(arg.equals(CACHE_SIZE_FLAG)) {
				try {
					cacheSize = Integer.parseInt(args[++i]);
					if(cacheSize < MIN_CACHE_SIZE || cacheSize > MAX_CACHE_SIZE) { 
						System.out.println("Invalid cache size! Valid cache size should be: 1024 bytes > cache size > 4194304 bytes");
						System.exit(0);
					}
				} catch(NumberFormatException nfex) {
					System.out.println("Invalid cache size: " + args[i]);
					System.exit(0);
				}
			} else if(arg.equals(BLOCK_SIZE_FLAG)) {
				try {
					blockSize = Integer.parseInt(args[++i]);
					if(!(IntStream.of(VALID_BLOCK_SIZES).anyMatch(x -> x == blockSize))) {
						System.out.println("Invalid block size! Valid block size should be from {2, 4, 8, 16, 32, 64}");
						System.exit(0);
					}
				} catch(NumberFormatException nfex) {
					System.out.println("Invalid block size: " + args[i]);
					System.exit(0);
				}
			} else if(arg.equals(NUMBER_OF_WAYS_FLAG)) {
				try {
					associativeNum = Integer.parseInt(args[++i]);
					if(!(IntStream.of(VALID_NUMBER_OF_WAYS).anyMatch(x -> x == associativeNum))) {
						System.out.println("Invalid number of ways! Valid number of ways should be from {0, 1, 2, 4, 8, 16}");
						System.exit(0);
					}
				} catch(NumberFormatException exception) {
					System.out.println("Invalid number of ways: " + args[i]);
					System.exit(0);
				}
			} else if(arg.equals(VICTIM_CACHE_SIZE_FLAG)) {
				victimCacheEnabled = true;
				try {
					victimCacheSize = Integer.parseInt(args[++i]);
					if(!(IntStream.of(VALID_VICTIM_CACHE_SIZES).anyMatch(x -> x == victimCacheSize))) {
						System.out.println("Invalid victim size cache! Valid victim cache size from {4, 8, 16}");
						System.exit(0);
					}
				} catch(NumberFormatException exception) {
					System.out.println("Invalid victim size cache: " + args[i]);
					System.exit(0);
				}
			} else {
				System.out.println("Not a valid argument: " + arg);
				System.exit(0);
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		victimCacheEnabled = false;
		validateArguments(args);
		Cache cache;
		if(victimCacheEnabled) cache = new Cache(cacheSize, blockSize, associativeNum, victimCacheSize);
		else cache = new Cache(cacheSize, blockSize, associativeNum);
		cache.findCacheHitMissRates(fileName);
		cache.displayResults();
	}
}