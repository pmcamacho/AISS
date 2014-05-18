package cartaocidadao;

import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import pteidlib.PteidException;

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
		} catch (Exception e) {
			System.out.println("Fail to sign");
		}

		return signed;

	}

	public boolean checkSignature(byte[] data, byte[] signed){
		CRYIOSignature sign = null;
		try {
			sign = CRYIOSignature.getInstance("SHA1withRSA");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Fail to get instance");
		}

		try {
			sign.initVerify();
		} catch (PteidException e) {
			System.out.println("Failed to initsign");

		} catch (CertificateException e) {
			System.out.println("Certificate Exception");
		}

		sign.update(data);
		boolean verified = false;
		try {
			verified = sign.verifiy(signed);
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
			System.out.println("Fail to get instance");
		}

		try {
			sign.initVerify();
		} catch (PteidException e) {
			System.out.println("Failed to initsign");

		} catch (CertificateException e) {
			System.out.println("Certificate Exception");
		}	
		
		return sign.getCurrentCardCertificate();	
	}

}
