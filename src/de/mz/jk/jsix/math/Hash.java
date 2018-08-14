package de.mz.jk.jsix.math;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

/**
 * <h3>Hash</h3>
 * common hashing functions
 * @author Joerg Kuharev
 * @version 12.01.2011 12:25:23
 */
public class Hash 
{
	public static void main(String[] args) throws Exception 
	{
		String s = "hallo";
		System.out.println( s + " = " + getMurmur2(s) );
	}
	
	public static String getSHA1(String text)  
    { 
    	try{
		    MessageDigest md = MessageDigest.getInstance("SHA-1");
		    byte[] sha1data = new byte[40];
		    md.update(text.getBytes("iso-8859-1"), 0, text.length());
		    sha1data = md.digest();
		    
		    StringBuffer buf = new StringBuffer();
	        for (int i = 0; i < sha1data.length; i++) { 
	            int halfbyte = (sha1data[i] >>> 4) & 0x0F;
	            int two_halfs = 0;
	            do { 
                    buf.append(
                    	((0 <= halfbyte) && (halfbyte <= 9)) 
                    	? (char)('0' + halfbyte) 
                    	: (char)('a' + (halfbyte - 10))
                    );
	                halfbyte = sha1data[i] & 0x0F;
	            } while(two_halfs++ < 1);
	        } 
	        return buf.toString();
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		return "";
    	}
    }
    
    /** Pearson, Peter K. (June 1990), "Fast Hashing of Variable-Length Text Strings", Communications of the ACM 33 (6): 677, doi:10.1145/78973.78978 */
    private static char[] PEARSON_PERMUTATION_TABLE = new char[]{
		39,159,180,252,71,6,13,164,232,35,226,155,98,120,154,69,
    	157,24,137,29,147,78,121,85,112,8,248,130,55,117,190,160,
    	176,131,228,64,211,106,38,27,140,30,88,210,227,104,84,77,
    	75,107,169,138,195,184,70,90,61,166,7,244,165,108,219,51,
    	9,139,209,40,31,202,58,179,116,33,207,146,76,60,242,124,
    	254,197,80,167,153,145,129,233,132,48,246,86,156,177,36,187,
    	45,1,96,18,19,62,185,234,99,16,218,95,128,224,123,253,
    	42,109,4,247,72,5,151,136,0,152,148,127,204,133,17,14,
    	182,217,54,199,119,174,82,57,215,41,114,208,206,110,239,23,
    	189,15,3,22,188,79,113,172,28,2,222,21,251,225,237,105,
    	102,32,56,181,126,83,230,53,158,52,59,213,118,100,67,142,
    	220,170,144,115,205,26,125,168,249,66,175,97,255,92,229,91,
    	214,236,178,243,46,44,201,250,135,186,150,221,163,216,162,43,
    	11,101,34,37,194,25,50,12,87,198,173,240,193,171,143,231,
    	111,141,191,103,74,245,223,20,161,235,122,63,89,149,73,238,
    	134,68,93,183,241,81,196,49,192,65,212,94,203,10,200,47
    };
    /**
     * fast hashing of variable length text string,
     * @see {@link http://en.wikipedia.org/wiki/Pearson_hashing}
     * @param text
     * @return number between 0 and 255
     */
    public static int getPearson(String text)
    {
    	char h = 0; 
    	for(int i=0; i<text.length(); i++)
    	{
    		char c = text.charAt(i);
    		h = PEARSON_PERMUTATION_TABLE[h ^ c];
    	}
    	return (int)h;
    }
    
    /**
     * @see {@link http://murmurhash.googlepages.com}
     * @param text
     * @return
     */
    public static int getMurmur2(String text) 
    {
    	byte[] data = new byte[]{};
		try {
			data = text.getBytes("iso-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	int seed = 147; // just constant
    	
    	// 'm' and 'r' are mixing constants generated offline.
        // They're not really 'magic', they just happen to work well.
        int m = 0x5bd1e995;
        int r = 24;

        // Initialize the hash to a 'random' value
        int len = data.length;
        int h = seed ^ len;

        int i = 0;
        while (len  >= 4) 
        {
            int k = data[i + 0] & 0xFF;
            k |= (data[i + 1] & 0xFF) << 8;
            k |= (data[i + 2] & 0xFF) << 16;
            k |= (data[i + 3] & 0xFF) << 24;

            k *= m;
            k ^= k >>> r;
            k *= m;

            h *= m;
            h ^= k;

            i += 4;
            len -= 4;
        }

        switch (len) 
        {
	        case 3: h ^= (data[i + 2] & 0xFF) << 16;
	        case 2: h ^= (data[i + 1] & 0xFF) << 8;
	        case 1: h ^= (data[i + 0] & 0xFF);
	                h *= m;
        }

        h ^= h >>> 13;
        h *= m;
        h ^= h >>> 15;

        return h;
      }
}
