import java.io.UnsupportedEncodingException;
import java.util.List;

import com.twmacinta.util.*;


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
	int firstCharIndex;
	char[] currentTestedPassword;
	String currentTestedPasswordString;
	int passwordTestedCount;
	Long passwordTotalCount;
	List<String> hashes;
	
	public PasswordBruteForcer(List<String> hashes, int passwordLength, char[] chars, int firstCharIndex){
		this.chars = chars;
		this.hashes = hashes;
		this.firstCharIndex = firstCharIndex;
		this.passwordLength = passwordLength;
		currentTestedPassword = new char[this.passwordLength];
		for(int i =0; i<this.currentTestedPassword.length;i++){
			if(i == 0) currentTestedPassword[i] = this.chars[firstCharIndex];
			else currentTestedPassword[i] = this.chars[0];
		}
		passwordTotalCount = (long) (Math.pow(new Double(this.chars.length), new Double(passwordLength-1)));
	}
	public void run() {
		System.out.print("\n Thread #" + Thread.currentThread().getId() + "\t Testing " + chars[this.firstCharIndex] + "\\w{"+ Integer.toString(passwordLength-1) + "} *******");
		try{
			run(this.passwordLength == 1 ? 0 : 1,0);
		}
		catch(Exception e){
			System.err.print("run(" + (this.passwordLength == 1 ? 0 : 1) + "," + 0 + ") -> " + e.getMessage());
		}
		System.out.print("\r" + this.passwordLength + "\t" + passwordTestedCount/(float)passwordTotalCount*100 + "% - " + Long.toString(passwordTestedCount) + " passwords testés (" + currentTestedPasswordString  + ")");
	}
	
	private void run(int position, int charPosition) {
		for(int j=charPosition;j<chars.length;j++){
			currentTestedPassword[position] = chars[j];
			currentTestedPasswordString = new String(currentTestedPassword);
			//System.out.print("\nHashing '" + password + "' - " + Integer.toString(password.length()) + ") [" + Integer.toString(passwordCharPosition) + "," + Integer.toString(i) + "]");
			//String digest = DigestUtils.md5Hex(currentTestedPasswordString);
			MD5 md5 = new MD5();
		    try {
				md5.Update(currentTestedPasswordString, null);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    String digest = md5.asHex();
			int index = hashes.indexOf(digest);
			if(index >= 0){
				System.out.print("\n" + hashes.get(index) + " concorde avec " + currentTestedPasswordString + "\n");
				hashes.remove(index);
			}

			passwordTestedCount++;
			
			if(passwordTestedCount%5000000 == 0){
				System.out.print("\r Thread #" + Thread.currentThread().getId() + " - " + this.passwordLength + "\t" + passwordTestedCount/(float)passwordTotalCount*100 + "% - " + Long.toString(passwordTestedCount) + " passwords testés (" + currentTestedPasswordString  + ")[" + position + "," + j + "]");
			}
			if(position < this.passwordLength-1) run(position+1, charPosition);
		}
	}
}
