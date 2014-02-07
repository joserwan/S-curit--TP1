/**
 * 
 */

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

// http://archive.apache.org/dist/commons/codec/binaries/
import org.apache.commons.codec.digest.*;

/**
 * @author ejossic
 *
 */
public class Main {
	static char[] chars = new char[]{'e','s','a','i','t','n','b','c','d','f','g','h','j','k','l','m','o','p','q','r','u','v','w','x','y','z','!','@','#','$','%','&','*'};
	static int maxPasswordLength = 8;
	static char[] currentTestedPassword = new char[maxPasswordLength];
	static String currentTestedPasswordString;
	static int passwordTestedCount = 0;
	
	static List<String> hashes;
	static List<String> clearPasswords;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.print("Starting\n");
		String md5HashesPath = "/home/ejossic/Dropbox/documents/UQAR/Sécurité/hashed_passwords.txt";
		String dictionnaryPath = "/home/ejossic/Dropbox/documents/UQAR/Sécurité/mots-8-et-moins.txt";

		hashes = fileLinesToArray(md5HashesPath);
		clearPasswords = fileLinesToArray(dictionnaryPath);

		dictionnaryAttack(hashes, clearPasswords);
		System.out.print("\n\nIl reste " + Integer.toString(hashes.size()) + " MD5 à trouver ...");
		bruteForceAttack(hashes);
		System.out.print("==============[ Terminé ]===============");
	}

	private static void bruteForceAttack(List<String> hashes) {
		testEachCharNextTo(0);
		return;
	}

	private static void testEachCharNextTo(int passwordCharPosition) {
		if(passwordCharPosition >= maxPasswordLength){ return; }
		
		for(int i=0;i<chars.length;i++){
			currentTestedPassword[passwordCharPosition] = chars[i];
					
			currentTestedPasswordString = new String(currentTestedPassword,0,passwordCharPosition+1);
			//System.out.print("\nHashing '" + password + "' - " + Integer.toString(password.length()) + ") [" + Integer.toString(passwordCharPosition) + "," + Integer.toString(i) + "]");
			if(passwordTestedCount % 1000000 ==0) System.out.print("\r" +Integer.toString(passwordTestedCount) + " passwords testés (" + currentTestedPasswordString  + ")");
			String digest = DigestUtils.md5Hex(currentTestedPasswordString);
			int index = hashes.indexOf(digest);
			if(index >= 0){
				System.out.print("\n" + hashes.get(index) + " concorde avec " + currentTestedPasswordString + "\n");
				hashes.remove(index);
			}
			passwordTestedCount++;
			testEachCharNextTo(passwordCharPosition+1);
		}
	}
	/*
	 * Hashage des mots de passe en clair 
	 * et recherche le hash correspondant dans notre liste hash MD5 
	 */
	public static void dictionnaryAttack(List<String> hashes, List<String> clearPasswords){
		System.out.print("\n==========[ Attaque par dictionnaire ]==========\n\n");
		
		for(int i=0; i< clearPasswords.size();i++){
			String password = clearPasswords.get(i);
			String digest = DigestUtils.md5Hex(password);
			
			int index = hashes.indexOf(digest);
			if(index >= 0){
				System.out.print("\n" + hashes.get(index) + " concorde avec " + password);
				hashes.remove(index);
			}
			else{
//				System.out.print("\n" + password + " non trouvé\t:" + digest);
			}
		}
	}
	
	/*
	 * Génère une liste de Chaine de caractères 
	 * après lecture du fichier passé en paramètre
	 */
	public static List<String> fileLinesToArray(String file_path){
		List<String> listOfString = new ArrayList<String>();
		FileInputStream fstream;
		try {
			fstream = new FileInputStream(file_path);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
		  	String strLine;
		  	while ((strLine = br.readLine()) != null)   {
		  		// Print the content on the console
		  		listOfString.add(strLine);
		  	}
			  //Close the input stream
			in.close();
		}  catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listOfString;
	}

}
