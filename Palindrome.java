import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.Random;

public class Palindrome {
    private static long P; //large prime
    private static long[] patternHash; //compute h_p(x) for all lengths 0 to n-1
    private static long[] patternHashRev; //same for the reversed string
    private static int r = 256;//base for Karp Rabin
    private static long[] RX; //compute h_p(r^x) for all lengths 0 to n - 1
    private static int len;
    private static String S;
    private static String reversed;
    //to compute any substring hash in O(1)
    public static void preprocess(String s){
        S = s;
        len = s.length();
        patternHash = new long[len + 1];
        patternHashRev = new long[len + 1];
        RX = new long[s.length()];
        reversed = new StringBuilder(s).reverse().toString();

        long temp = 1;
        for(int i = 0; i < len; i++){
            RX[i] = temp;
            temp = (r * temp) % P;
        }

        long h = 0;
        patternHash[0] = 0;
        for(int i = 0; i < s.length(); i++){
            h = Math.floorMod(r * h + s.charAt(i), P);
            patternHash[i + 1] = h;
        }


        h = 0;
        patternHash[0] = 0;
        for(int i = 0; i < reversed.length(); i++){
            h = Math.floorMod(r * h + reversed.charAt(i), P);
            patternHashRev[i + 1] = h;
        }
    }

    public static long revHash(int i , int j){
        return Math.floorMod(patternHashRev[j] - RX[j - i] * patternHashRev[i], P);
    }

    public static long hash(int i, int j){ //calculate hash S{i, ..., j]
        if (i == j) return S.charAt(i); //still O(1)
        if (j < i) return revHash(len - i, len - j);
        return Math.floorMod(patternHash[j] - RX[j - i] * patternHash[i], P);
    }


    public static void generateLargePrime(){
        P = BigInteger.probablePrime(30, new Random()).longValue();
    }


    public static boolean hashEquals(int l, int r, int radius){
        return hash((l - radius), l) == hash(r + radius, r);
    }


    public static int longestPaliRadius(int l, int r){
        //calculate radius
        int left = 1;
        int right = Math.min(len - r, l) + 1;
        int mid;

        //find longest palindrome radius for given l, r
        int maxRadius = 0;
        while(left < right){
            mid = (int) (Math.floor((left + right) / 2.0));
            if(hashEquals(l, r, mid)){
                maxRadius = mid;
                left = mid + 1;
            }
            else{
                right = mid;
            }
        }
       return maxRadius;
    }


    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String s = reader.readLine();
        int l = 1;
        int r = 1;
        int maxL = -1;
        int maxR = -1;
        int maxLength = 0;
        int maxRadius = 0;
        while(true) {
            generateLargePrime();
            preprocess(s);

            //main loop of the algorithm
            //go through each possible center point of the palindrome
            int maxIn = 0, maxOut = 0;
            while (l < len - 1 && r < len - 1) {
                int length = 0;
                int radius = longestPaliRadius(l, r);
                int outerRadius = 0;
                int innerRadius = radius;
                length = radius + (r - l) + radius;
                if (l - radius > 0 && r + radius < len) {
                    if (l - radius == 1 || r + radius == (len - 1)) {//only one character remaining to the left or to the right of pali
                        radius += 1;
                        length += 2; //one off characters at the left or right edge
                    } else {
                        outerRadius = longestPaliRadius(l - radius - 1, r + radius + 1);
                        radius += outerRadius;
                        radius += 1;//characters that are one off
                        length += outerRadius * 2;
                        length += 2;//characters that are one off
                    }
                }
                if (length > maxLength) {
                    maxIn = innerRadius;
                    maxOut = outerRadius;
                    maxRadius = radius;
                    maxLength = length;
                    maxL = l;
                    maxR = r;
                }
                if (r > l) l++;
                else r++;
            }
            String innerPart = s.substring(maxL - maxIn, maxR + maxIn);
            String reversedInner = new StringBuilder(innerPart).reverse().toString();
            if(maxOut == 0){
                if (innerPart.compareTo(reversedInner) == 0) break;
            }
            else{
                String leftOuterPart = s.substring(maxL - maxIn - 1 - maxOut, maxL - maxIn - 1);
                String rightOuterPart = s.substring(maxR + maxIn + 1, maxR + maxIn + 1 + maxOut);

                String reversedRightOuter = new StringBuilder(rightOuterPart).reverse().toString();
                if ((innerPart.compareTo(reversedInner) == 0) && (leftOuterPart.compareTo(reversedRightOuter) == 0)) {
                    break;
                }
            }
        }
        String answer = s.substring(maxL - maxRadius, maxR + maxRadius);
        System.out.println(answer);
    }
}

//I preprocess s such that I can query any substring hash in O(1), then I look at each possible palindromic center
//and calculate the maximum radius at that point. This is done using binary search, so it is O(log n).
// Since a one-off palindrome is just a palindrome with an inner part combined with the one-off characters, and the outer part,
// we first calculate the max Radius for each, then calculate
//the max outer radius for each palindromic center. Since we go through O(n) many possible centers, we do O(n log n) total work.
//To ensure the Las Vegas nature, we check at the very end that the inner and outer parts are indeed palindromes. If not, we start
//from the very beginning.