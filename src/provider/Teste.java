package provider;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;

public class Teste {

	
	//Passar estes parametros quando chamamos o provider
		//	java.security.Security.addProvider(new aiss.OurProvider());
		//
		//	Provider p = Security.getProvider("ProviderAIAC");
		//    
		//    System.out.println("ProviderAIAC provider name is " + p.getName());
		//    System.out.println("ProviderAIAC provider version # is " + p.getVersion());
		//    System.out.println("ProviderAIAC provider info is " + p.getInfo());
		//
		//    testSha2();
		//super("ProviderAIAC", 1.0D, "ProviderAIAC provider v1.0, implementing SHA-256");
	
	public static void main(String... args) {
	
		Teste teste = new Teste();
		
		java.security.Security.addProvider(new aiss.CRYIOProvider("ProviderAISS", 1.0, "ProviderAISS(Sign and Cipher)"));
		Provider p = Security.getProvider("ProviderAISS");
		
		
		System.out.println("ProviderAIAC provider name is " + p.getName());
		System.out.println("ProviderAIAC provider version # is " + p.getVersion());
		System.out.println("ProviderAIAC provider info is " + p.getInfo());

		 for (String key: p.stringPropertyNames())
			    System.out.println("\t" + key + "\t" + p.getProperty(key));
		
		try {
			Signature s = Signature.getInstance("CRYIOSigner", "ProviderAISS");
			
			
			KeyPairGenerator keyGen = null;
					try {
						keyGen = KeyPairGenerator.getInstance("RSA");
					} catch (NoSuchAlgorithmException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			        keyGen.initialize(1024);
			        KeyPair pair = keyGen.genKeyPair();
			        PublicKey pubKey = pair.getPublic();
			        PrivateKey privKey = pair.getPrivate();
			
			s.initSign(privKey);
			
			byte[] bytes = s.sign();
			
			System.out.println(bytes);
			
		
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		 
	}	
	
	
}
