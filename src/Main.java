import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import pteidlib.PteidException;
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



	//	String opcao = args[0];
	//	
	//	if(opcao.equals("d")){
	//
	//	    CRYIOFileManager.readFile(Paths.get("/simples.cifrado"));
	//	  
	//	}
	//	else if (opcao.equals("c")){
	//	    
	//	    byte [] hello_file =  "Hello".getBytes();
	//	    
	//	    CRYIOFile simples =  new CRYIOFile(hello_file, "Sergio Alves".getBytes());
	//	    CRYIOFileManager.writeFile(Paths.get("/simples.cifrado"), simples);
	//	    
	//	}


    }

    private static void put(String local_dir, String cloud_dir, String file_path) {


	byte[] file;

	try {

	    file = Files.readAllBytes(Paths.get(local_dir, file_path));
	    
	    //sign.initSign();
	    //sign.update(file);
	    //byte [] signed = sign.sign();
	    
	    byte[] signed = "Sergio Alves".getBytes();
	    
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
	    
	    
	    CRYIOFile cryio_file =  CRYIOFileManager.readFile(Paths.get(cloud_dir, file_path));
	    
//	    long mode_dec = CRYIO_HARDWARE_JNI.ROUNDS_10 
//		    	   | CRYIO_HARDWARE_JNI.CBC_FLAG 
//		    	   |CRYIO_HARDWARE_JNI.FIRST_FLAG
//		    	   | CRYIO_HARDWARE_JNI.DECRYPT_FLAG;
	    
	    
	    byte[] file = cryio_file.getFile();
	    byte[] file_sign = cryio_file.getFileSignature();
	    
	    //sign.update(file);
	    
	    //boolean verified = sign.verifiy(file_sign);
	    
	    //System.out.println("Passed:? " + verified);
	    
	    
	    
	    Files.write(Paths.get(local_dir, file_path),file, StandardOpenOption.CREATE);  
	    System.out.println("ok");
	    
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

}
