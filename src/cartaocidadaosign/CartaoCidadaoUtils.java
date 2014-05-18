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
import java.security.cert.X509Certificate;

public class CartaoCidadaoUtils {

	public byte[] sign(byte[] data){


		CRYIOSignature sign = null;
		try {
			sign = CRYIOSignature.getInstance("SHA1withRSA");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Fail to get instance");
		}

		try {
			sign.initSign();
		} catch (PteidException e) {
			System.out.println("Failed to initsign");

		}

		sign.update(data);
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

		return signed;


	}

	public boolean checkSignature(byte[] data, byte[] signed){
		CRYIOSignature sign = null;
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

		sign.update(data);
		boolean verified = false;
		try {
			verified = sign.verifiy(signed);
			System.out.println("Passed:?" + verified);
		} catch (Exception e) {
			System.out.println("Fail to sign");
		}

		return verified;


	}


	public X509Certificate getCertificate(){

		CRYIOSignature sign = null;
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
		
		return sign.getCurrentCardCertificate();
	
	}




}
