package aiss;

import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import cartaocidadaosign.CRYIOSignature;

public class CRYIOSigner extends Signature {
	
	byte[] data;
	CRYIOSignature signer;
	
	public CRYIOSigner() throws NoSuchAlgorithmException {
		super("CRYIOSigner");
		signer = CRYIOSignature.getInstance("SHA1withRSA");
	}

	@Override
	protected void engineInitVerify(PublicKey publicKey)
			throws InvalidKeyException {
		signer.initVerify(publicKey);
		
	}
	
	protected void engineInitVerify() {
		
	}

	@Override
	protected void engineInitSign(PrivateKey privateKey)
			throws InvalidKeyException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void engineUpdate(byte b) throws SignatureException {

		//Cartao de cidadao so suporta byte[]
	}

	@Override
	protected void engineUpdate(byte[] b, int off, int len)
			throws SignatureException {
		data = b;
		
	}

	@Override
	protected byte[] engineSign() throws SignatureException {
		
		
				try {
					return signer.sign();
				} catch (Exception e) {
					if((e != null) && (e.getMessage() != null))
						throw new SignatureException(e.getMessage());
					else
						throw new SignatureException("");

				}
			
	}

	@Override
	protected boolean engineVerify(byte[] sigBytes) throws SignatureException {
		return signer.verifiy(sigBytes);
	}

	@Override
	protected void engineSetParameter(String param, Object value)
			throws InvalidParameterException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Object engineGetParameter(String param)
			throws InvalidParameterException {
		// TODO Auto-generated method stub
		return null;
	}

}
