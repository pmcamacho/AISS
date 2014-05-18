package aiss;

import java.security.Provider;

public class CRYIOProvider extends Provider {

	private static final long serialVersionUID = 1L;

	public CRYIOProvider(String name, double version, String info) {
		super(name, version, info);
		put("Cipher.CRYIOCipher", "aiss.CRYIOCipher");
		put("Signature.CRYIOSigner", "aiss.CRYIOSigner");
	}

}
