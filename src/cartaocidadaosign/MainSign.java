package cartaocidadaosign;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import pteidlib.PteidException;

public class MainSign {

    public static void main(String[] args){

	MainSign m = new MainSign();
	String inFile = "Inicial.txt";
	byte[] bFile = null;
	try {
	    bFile = Files.readAllBytes(Paths.get(inFile));
	} catch (IOException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}

	CRYIOSignature sign = null;
	try {
	    sign = CRYIOSignature.getInstance("SHA1withRSA");
	} catch (NoSuchAlgorithmException e) {
	    // TODO Auto-generated catch block
	    System.out.println("Fail to get instance");
	}

	try {
	    sign.initSign();
	} catch (PteidException e) {
	    System.out.println("Failed to initsign");

	}

	sign.update(bFile);
	byte[] signed = null;
	try {
	    signed = sign.sign();
	    Files.write(Paths.get(inFile + ".digest"), signed, StandardOpenOption.CREATE);   
	    //m.writeFile(inFile + ".digest", signed);
	    System.out.println(signed);
	} catch (Exception e) {
	    System.out.println(e);
	    System.out.println("Fail to sign");
	}

	////////////////VERIFICAR//////////////////////
	System.out.println("Verificar comeca");
	sign = null;
	try {
	    sign = CRYIOSignature.getInstance("SHA1withRSA");
	} catch (NoSuchAlgorithmException e) {
	    // TODO Auto-generated catch block
	    System.out.println("Fail to get instance");
	}

	System.out.println("criou instancia");


	try {
	    sign.initVerify();
	} catch (PteidException e) {
	    System.out.println("Failed to initsign");

	} catch (CertificateException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	System.out.println("Init verify passou");

	sign.update(bFile);
	boolean verified = false;
	try {
	    bFile = Files.readAllBytes(Paths.get(inFile + ".digest"));
	    verified = sign.verifiy(bFile);
	    System.out.println("Passed:?" + verified);
	} catch (Exception e) {
	    System.out.println("Fail to sign");
	}


    }

    public byte[] readFile(String path){

	FileInputStream fileInputStream=null;

	File file = new File(path);

	byte[] bFile = new byte[(int) file.length()];

	try {
	    //convert file into array of bytes
	    fileInputStream = new FileInputStream(file);
	    fileInputStream.read(bFile);
	    fileInputStream.close();


	    System.out.println("Done");
	    return bFile;
	}catch(Exception e){
	    e.printStackTrace();
	}
	return null;

    }

    public void writeFile(String path, byte[] data){

	FileOutputStream fileOutputStream=null;

	File file = new File(path);

	byte[] bFile = new byte[(int) file.length()];

	try {
	    //convert file into array of bytes
	    fileOutputStream = new FileOutputStream(file);
	    fileOutputStream.write(data);
	    fileOutputStream.close();


	    System.out.println("Done");
	}catch(Exception e){
	    e.printStackTrace();
	}

    }

}
