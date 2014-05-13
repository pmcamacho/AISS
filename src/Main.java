import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import cartaocidadaosign.CRYIOSignature;


public class Main {

    private static CRYIOSignature sign = null;

    public static void main(String[] args) {

	//opcao 1 - caminho local
	//opcao 2 - caminho dropbox

	String local_dir = args[0];
	String cloud_dir = args	[1];

	Scanner s = new Scanner(System.in);



	try {
	    sign = CRYIOSignature.getInstance("SHA1withRSA");


	} catch (NoSuchAlgorithmException e) {
	    e.printStackTrace();
	    System.exit(1);
	}

	System.out.println("Local Dir " + local_dir);
	System.out.println("Cloud Dir " + cloud_dir);

	while(true){



	    System.out.println("Commands:");
	    System.out.println("1 - get <file_path>");
	    System.out.println("2 - put <file_path>");
	    System.out.println("3 - exit");



	    String [] option_splitted = s.nextLine().split("\\s");

	    String option = option_splitted[0].replaceAll("\\s","").toLowerCase();


	    if(option.equals("get")){

		if(option_splitted.length == 2){

		    String file_path = option_splitted[1];
		    get(local_dir,cloud_dir, file_path);

		} 
		else {
		    System.out.println("file_path empty");
		}

	    } 

	    else if (option.equals("put")){


		if(option_splitted.length == 2){

		    String file_path = option_splitted[1];
		    put(local_dir,cloud_dir, file_path);

		}
		else {
		    System.out.println("file_path empty");
		}


	    }

	    else if (option.equals("exit")){
		System.out.println("Good bye :'(");
		break;
	    }

	    else {
		System.out.println("Unknow command");
	    }





	}

	s.close();

    }

    private static void put(String local_dir, String cloud_dir, String file_path) {


	byte[] file;

	try {

	    Path p = Paths.get(local_dir, file_path);

	    if(Files.notExists(p)){
		System.out.println("O ficheiro " +  p.toString() + " nao existe!");
		return;
	    }

	    file = Files.readAllBytes(p);

	    sign.initSign();
	    sign.update(file);
	    byte [] signed = sign.sign();
	    
	    //byte [] signed = "sergio".getBytes();

	    //byte[] signed = "Sergio Alves".getBytes();

	    //	    long mode_enc = CRYIO_HARDWARE_JNI.ROUNDS_10 
	    //		    	   | CRYIO_HARDWARE_JNI.CBC_FLAG 
	    //		    	   |CRYIO_HARDWARE_JNI.FIRST_FLAG
	    //		    	   | CRYIO_HARDWARE_JNI.ENCRYPT_FLAG;


	    //Falta cifrar!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

	    CRYIOFile cryio_file = new CRYIOFile(file,signed);

	    CRYIOFileManager.writeFile(Paths.get(cloud_dir, file_path), cryio_file);



	} catch (IOException e) {
	    e.printStackTrace();
	} catch (Exception e) {
	    e.printStackTrace();
	}



    }

    private static void get(String local_dir, String cloud_dir, String file_path) {

	try {

	    Path p = Paths.get(cloud_dir, file_path);

	    if(Files.notExists(p)){
		System.out.println("O ficheiro " +  p.toString() + " nao existe!");
		return;
	    }

	    CRYIOFile cryio_file =  CRYIOFileManager.readFile(p);

	    //	    long mode_dec = CRYIO_HARDWARE_JNI.ROUNDS_10 
	    //		    	   | CRYIO_HARDWARE_JNI.CBC_FLAG 
	    //		    	   |CRYIO_HARDWARE_JNI.FIRST_FLAG
	    //		    	   | CRYIO_HARDWARE_JNI.DECRYPT_FLAG;


	    byte[] file = cryio_file.getFile();
	    byte[] file_sign = cryio_file.getFileSignature();

	    sign.initVerify();
	    sign.update(file);
	    boolean verified = sign.verifiy(file_sign);
	    System.out.println("Passed:? " + verified);



	    Files.write(Paths.get(local_dir, file_path),file, StandardOpenOption.CREATE);  
	    System.out.println("ok");

	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

}
