import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
	MD5 md5;
	
	public PasswordBruteForcer(List<String> hashes, int passwordLength, char[] chars, int firstCharIndex){
		this.chars = chars;
		this.hashes = hashes;
		this.firstCharIndex = firstCharIndex;
		this.passwordLength = passwordLength;
		this.md5 = new MD5();
		currentTestedPassword = new char[this.passwordLength];
		
		for(int i =0; i<this.currentTestedPassword.length;i++){
			// On démarre avec la première lettre voulue firstCharIndex,
			// puis on remplis le mot de passe de départ de 'a'
			if(i == 0) currentTestedPassword[i] = this.chars[firstCharIndex];
			else currentTestedPassword[i] = this.chars[0];
		}
		passwordTotalCount = (long) (Math.pow(new Double(this.chars.length), new Double(passwordLength-1)));
	}
	
	
	public void run() {
		System.out.print("\n Thread #" + Thread.currentThread().getId() + "\t Testing " + chars[this.firstCharIndex] + "\\w{"+ Integer.toString(passwordLength-1) + "} *******");

		// Démarrage du compteur de temps
		if(this.firstCharIndex == 0){ 
			Main.startPerPasswordSize[this.passwordLength] = System.currentTimeMillis(); 
		}
		
		long startedAt = System.currentTimeMillis();
		run((this.passwordLength == 1 ? 0 : 1), 0);
		System.out.print("\r" + currentTestedPasswordString  + " - " + passwordTestedCount/(float)passwordTotalCount*100 + "% - " + Long.toString(passwordTestedCount) + " en " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startedAt) + "s.");
		
		
		if(this.firstCharIndex == this.chars[this.chars.length - 1]){
			// Note : Il se peut que le dernier caractère à tester ne soit pas le dernier à terminer
			// Cependant il ne devrait pas en être très loin, donc on tolèrera cette amrge d'erreur ici !
			System.out.print("\nMots de passe de " + this.passwordLength + " caractères terminés en " + TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - Main.startPerPasswordSize[this.passwordLength]) + "m.\n");
		}
	}
	
	private void run(int position, int charPosition) {
		for(int j=charPosition;j<chars.length;j++){
			currentTestedPassword[position] = chars[j];
			currentTestedPasswordString = new String(currentTestedPassword);
			//System.out.print("\nHashing '" + password + "' - " + Integer.toString(password.length()) + ") [" + Integer.toString(passwordCharPosition) + "," + Integer.toString(i) + "]");
			//String digest = DigestUtils.md5Hex(currentTestedPasswordString);
		    try {
		    	md5.Init();
				md5.Update(currentTestedPasswordString, null);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		    String digest = md5.asHex();
		    
		    if(passwordTestedCount % 1000000 == 0){
		    	System.out.print("\r" + currentTestedPasswordString  + " - " + passwordTestedCount/(float)passwordTotalCount*100 + "% - " + Long.toString(passwordTestedCount));
			}
			
			int index = hashes.indexOf(digest);
			if(index >= 0){
				System.out.print("\n" + hashes.get(index) + " concorde avec " + currentTestedPasswordString + "\n");
				hashes.remove(index);
			}

			passwordTestedCount++;
			if(position < this.passwordLength-1) run(position+1, charPosition);
		}
	}
}
