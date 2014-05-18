package cartaocidadaosign;

import java.lang.reflect.InvocationTargetException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import pteidlib.PteidException;
import sun.security.pkcs11.wrapper.PKCS11Exception;

public class CRYIOSignature {

	private static AiacSigner signer = null;
	private static CRYIOSignature sign = null;

	private static String alghoritm = null;

	//Keys
	private static PublicKey k = null;
	private static X509Certificate cert = null;

	//Data
	private static byte[] signData = null;

	private CRYIOSignature(String algh) throws NoSuchAlgorithmException{

		if(algh.equals("SHA1withRSA"))
			alghoritm = algh;
		else throw new NoSuchAlgorithmException(algh);

	}

	public static CRYIOSignature getInstance(String algh) throws NoSuchAlgorithmException{
		if((sign == null) || (!algh.equals(alghoritm))){
			sign = new CRYIOSignature(algh);
		}

		k = null;
		cert = null;
		signData = null;

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


	public void initVerify(PublicKey key) throws PteidException, CertificateException{
		if(signer == null){
			signer = new AiacSigner();
			signer.startModule();
		}
		k = key;
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
		
		boolean hasKey = k != null;
		boolean hasCertificate = cert != null;
		
		if((hasKey && hasCertificate) && (k.equals(cert.getPublicKey())))
			return signer.authenticate(k, alghoritm, signData, signature);//could be certificate

		
		if(k != null)
			return signer.authenticate(k, alghoritm, signData, signature);
		else if(cert != null)
			return signer.authenticate(cert, alghoritm, signData, signature);
		else return false;

	}

	public X509Certificate getCurrentCardCertificate(){

		return cert;
	}

}
