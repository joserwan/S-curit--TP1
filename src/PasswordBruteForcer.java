import java.math.BigInteger;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * 
 */

/**
 * @author ejossic
 *
 */
public class PasswordBruteForcer implements Runnable {
	int passwordLength = 0;
	char[] chars;
	char[] currentTestedPassword;
	String currentTestedPasswordString;
	int passwordTestedCount;
	Long passwordTotalCount;
	List<String> hashes;
	
	public PasswordBruteForcer(List<String> hashes, int passwordLength, char[] chars){
		this.chars = chars;
		this.hashes = hashes;
		this.passwordLength = passwordLength;
		currentTestedPassword = new char[this.passwordLength];
		for(int i =0; i<this.currentTestedPassword.length;i++){
			currentTestedPassword[i] = this.chars[0];
		}
		System.out.print(currentTestedPassword);
		passwordTotalCount = (long) (Math.pow(new Double(this.chars.length), new Double(passwordLength)));
	}
	public void run() {
		System.out.print("\n**** Testing " + Integer.toString(passwordLength) + " characters long passwords *******");
		testEachChar(0);
	}
	
	private void testEachChar(int position) {
		for(int j=0;j<chars.length;j++){
			currentTestedPassword[position] = chars[j];
			currentTestedPasswordString = new String(currentTestedPassword);
			//System.out.print("\nHashing '" + password + "' - " + Integer.toString(password.length()) + ") [" + Integer.toString(passwordCharPosition) + "," + Integer.toString(i) + "]");
			String digest = DigestUtils.md5Hex(currentTestedPasswordString);
			int index = hashes.indexOf(digest);
			if(index >= 0){
				System.out.print("\n" + hashes.get(index) + " concorde avec " + currentTestedPasswordString + "\n");
				hashes.remove(index);
			}

			passwordTestedCount++;
			
			if(passwordTestedCount%1000000 == 0){
				System.out.print("\r" + this.passwordLength + "\t" + passwordTestedCount/(float)passwordTotalCount*100 + "% - " + Long.toString(passwordTestedCount) + " passwords testés (" + currentTestedPasswordString  + ")[" + position + "," + j + "]");
			}
			if(position < this.passwordLength-1) testEachChar(position+1);
		}
	}
}
