/**
 * 
 */

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

// http://archive.apache.org/dist/commons/codec/binaries/
import org.apache.commons.codec.digest.*;

/**
 * @author ejossic
 *
 */
public class Main {
	static char[] chars = "abcdefghijklmnopqrstuvwxyz0123456789!@#$%&*".toCharArray();
	static int maxPasswordLength = 8;
	static char[] currentTestedPassword = new char[maxPasswordLength];
	static String currentTestedPasswordString;
	static int passwordTestedCount = 0;
	static long[] startPerPasswordSize = new long[maxPasswordLength+1]; // On va inscrire le timestamp en milisecondes à l'index 1 pour les mots de passe de 1 caractère, par exemple. Pour fins de mesure de durée. 
	
	static List<String> hashes;
	static List<String> clearPasswords;
	static List<Thread> threads;
	static int procs = Runtime.getRuntime().availableProcessors();
	static ExecutorService threadExecutor = Executors.newFixedThreadPool( procs * 2 );

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		validateArguments(args);
		
		System.out.print("Utilisation de " + procs + " threads\n");
		
		String md5HashesPath = args[0];
		String dictionnaryPath = args[1];

		hashes = fileLinesToArray(md5HashesPath, true);
		clearPasswords = fileLinesToArray(dictionnaryPath, false);

		dictionnaryAttack(hashes, clearPasswords);
		bruteForceAttack(hashes); // Les hashs trouvés ont été enlevés
	}

	private static void validateArguments(String[] args) {
		if(args.length != 2){
			displayUsage();
			System.exit(1);
		}
		
		for(int i=0; i<args.length; i++){
			File f = new File(args[i]);
			if(!f.exists()){
				System.out.printf("%s n'existe pas !\n", f.toPath().toString());
				displayUsage();
				System.exit(2);
			}
		}
	}

	private static void displayUsage() {
		System.out.println("Usage:\njava -jar Main.jar path/to/md5_list path/to/password_directory");		
	}

	/*
	 * Attaque de type "Brute-force" multi-threads.
	 * Chaque Thread est chargé de tester toutes les mots de passe possibles
	 * de taille T et commençant par une lettre L 
	 */
	private static void bruteForceAttack(List<String> hashes) {
		System.out.print("\n\nIl reste " + Integer.toString(hashes.size()) + " MD5 à trouver ...\n\n");
		for(int i=0; i<hashes.size(); i++){
			System.out.print(hashes.get(i) + "\n");
		}
		
		for(int i=1; i <= maxPasswordLength; i++){
			for(int j=0; j < chars.length; j++){
				PasswordBruteForcer bruteForcer = new PasswordBruteForcer(hashes, i, chars, j);
				
				// Pour moins de 5 caractères, ça ne faut pas la peine de prendre le temps d'initialiser des threads
				if(i < 5){
					bruteForcer.run();
				}
				else{
					threadExecutor.execute(bruteForcer);
				}
			}
		}
		threadExecutor.shutdown();
		return;
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
	 * 
	 * http://www.java-samples.com/showtutorial.php?tutorialid=1299
	 */
	public static List<String> fileLinesToArray(String file_path, boolean isMD5){
		List<String> listOfString = new ArrayList<String>();
		FileInputStream fstream;
		Pattern md5Pattern = Pattern.compile("^[a-z0-9]{32}$");
		try {
			fstream = new FileInputStream(file_path);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
		  	String strLine;
		  	while ((strLine = br.readLine()) != null)   {
		  		if(!isMD5 || md5Pattern.matcher(strLine).matches()){
		  			listOfString.add(strLine);
		  		}
		  		else{
		  			System.err.print("Erreur avec " + strLine);
		  		}
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
