package cartaocidadaosign;

import java.lang.reflect.InvocationTargetException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import pteidlib.PteidException;
import sun.security.pkcs11.wrapper.PKCS11Exception;

public class CRYIOSignature {

	private static AiacSigner signer = null;
	private static CRYIOSignature sign = null;
	private String alghoritm = null;
	private X509Certificate cert = null;
	private byte[] signData = null;

	private CRYIOSignature(String algh) throws NoSuchAlgorithmException{

		if(algh.equals("SHA1withRSA"))
			alghoritm = algh;
		else throw new NoSuchAlgorithmException(algh);

	}

	public static CRYIOSignature getInstance(String algh) throws NoSuchAlgorithmException{
		if(sign == null){
			sign = new CRYIOSignature(algh);

		}
		return sign;

	} 

	public void initSign() throws PteidException{

		if(signer == null){
			signer = new AiacSigner();
			signer.startModule();
		}

	}

	public void initVerify() throws PteidException, CertificateException{
		if(signer == null){
			signer = new AiacSigner();
			signer.startModule();
		}
		cert = signer.getAuthCertificate();
	}

	public byte[] sign() throws Exception{

		try {
			return signer.sign(signData);
		} catch (ClassNotFoundException | NoSuchMethodException
				| SecurityException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| PKCS11Exception e) {

			throw new Exception(e.getMessage());
		}


	}

	public void update(byte[] data){

		signData = data;

	}

	public boolean verifiy(byte[] signature){


		return signer.authenticate(cert, alghoritm, signData, signature);
	}

}
