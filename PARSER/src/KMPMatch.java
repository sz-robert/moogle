
public class KMPMatch {

	  /**
     * Finds the first occurrence of the pattern in the text.
     */
    public int indexOf(byte[] data, byte[] pattern, int start_index) {
        int[] failure = computeFailure(pattern);
        
        int j = 0;
        if (data.length == 0) return -1;		//the length of the array is zero
        										//not good
        for (int i = start_index; i < data.length; i++) {
            while (j > 0 && pattern[j] != data[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == data[i]) { j++; }
            if (j == pattern.length) {
                return i - pattern.length + 1;
            }
        }
        return -1;
    }

    /**
     * Computes the failure function using a boot-strapping process,
     * where the pattern is matched against itself.
     */
    private int[] computeFailure(byte[] pattern) {
        int[] failure = new int[pattern.length];

        int j = 0;
        for (int i = 1; i < pattern.length; i++) {
            while (j > 0 && pattern[j] != pattern[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == pattern[i]) {
                j++;
            }
            failure[i] = j;
        }

        return failure;
    }
	
	
}
