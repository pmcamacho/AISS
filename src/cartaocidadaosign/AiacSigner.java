package cartaocidadaosign;

/**
 * <p>Title: pteidlib JNI Test</p>
 * <p>Description: Test pteidlib jni interface</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: Zetes</p>
 * @author not attributable
 * @version 1.0
 */

import java.io.*;

import pteidlib.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Formatter;

import javax.crypto.Cipher;
import javax.xml.bind.DatatypeConverter;

import sun.security.pkcs11.wrapper.*;




public class AiacSigner
{
	static
	{

		System.out.println(System.getProperty("java.library.path"));

		try
		{
			System.loadLibrary("pteidlibj");
		}
		catch (UnsatisfiedLinkError e)
		{
			System.err.println("Native code library failed to load.\n" + e);
			System.exit(1);
		}
	}
	////////////////////////////////////////////1103

	public void PrintIDData(PTEID_ID idData)
	{
		System.out.println("DeliveryEntity : " + idData.deliveryEntity);
		System.out.println("PAN : " + idData.cardNumberPAN);
		System.out.println("...");
	}

	public void PrintAddressData(PTEID_ADDR adData)

	{
		if("N".equals(adData.addrType))
		{
			System.out.println("Type : National");
			System.out.println("Street : " + adData.street);
			System.out.println("Municipality : " + adData.municipality);
			System.out.println("...");
		}
		else
		{
			System.out.println("Type : International");
			System.out.println("Address : " + adData.addressF);
			System.out.println("City : " + adData.cityF);
			System.out.println("...");
		}
	}


	public byte[] sign(byte [] nounce) throws PKCS11Exception, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {


		
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		System.loadLibrary("pteidlibj");
		PKCS11 pkcs11;
		String osName = System.getProperty("os.name");
		String javaVersion = System.getProperty("java.version");
		String libName = "pteidpkcs11.dylib";
		Class pkcs11Class = Class.forName("sun.security.pkcs11.wrapper.PKCS11");

		if (javaVersion.startsWith("1.5."))
		{
			Method getInstanceMethode = pkcs11Class.getDeclaredMethod("getInstance", new Class[] { String.class, CK_C_INITIALIZE_ARGS.class, boolean.class });
			pkcs11 = (PKCS11)getInstanceMethode.invoke(null, new Object[] { libName, null, false });
		}
		else
		{
			Method getInstanceMethode = pkcs11Class.getDeclaredMethod("getInstance", new Class[] { String.class, String.class, CK_C_INITIALIZE_ARGS.class, boolean.class });
			pkcs11 = (PKCS11)getInstanceMethode.invoke(null, new Object[] { libName, "C_GetFunctionList", null, false });
		}

		long p11_session = pkcs11.C_OpenSession(0, PKCS11Constants.CKF_SERIAL_SESSION, null, null);

		// Token login 
		pkcs11.C_Login(p11_session, 1, null);
		CK_SESSION_INFO info = pkcs11.C_GetSessionInfo(p11_session);
		
		// Get available keys 
		CK_ATTRIBUTE[] attributes = new CK_ATTRIBUTE[1];
		attributes[0] = new CK_ATTRIBUTE();
		attributes[0].type = PKCS11Constants.CKA_CLASS;
		attributes[0].pValue = new Long(PKCS11Constants.CKO_PRIVATE_KEY);

		pkcs11.C_FindObjectsInit(p11_session, attributes);
		long[] keyHandles = pkcs11.C_FindObjects(p11_session, 5);

		// points to auth_key 
		long signatureKey = keyHandles[0];		//test with other keys to see what you get
		pkcs11.C_FindObjectsFinal(p11_session);


		// initialize the signature method 
		CK_MECHANISM mechanism = new CK_MECHANISM();
		mechanism.mechanism = PKCS11Constants.CKM_SHA1_RSA_PKCS;
		mechanism.pParameter = null;
		pkcs11.C_SignInit(p11_session, mechanism, signatureKey);


		//...

		// sign
		byte[] signature = pkcs11.C_Sign(p11_session, nounce);
		if(signature == null)
			System.out.println("NULL SIGNATUREEEE!4");
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		//return pteid.SendAPDU(nounce);
		return signature;

	}
//107679788109673484080908988320885514708757674636582251559474809537456388182271981905458790676877466571440459946033835687897447171374945289933330122786027535950713806567247952469329535358149237394213091642242961539573346161013027261888159755925844088699448933179107707258451474593650110609582990704324800105251
//assinatura:  30 81 89 02 81 81 00 c2 16 09 6b 2f e4 8c 47 2b f1 a3 a6 ce dc e1 6b 7f d3 8d 54 01 ec 62 77 95 74 89 ce 97 60 26 ad c2 65 9c 6a f9 56 31 d7 fa 58 ba 0b 7a ac a7 fe 5a fa 4a 6f 7e ea ec f1 09 d5 12 60 77 05 ad 27 3d 8d 9d b4 f3 a0 3b d7 07 9a 54 eb c0 54 43 37 6f b6 94 1b 61 4e 16 da f1 6f 0f c8 e4 ac c0 90 fa 2b 54 35 e9 16 2b 7c c1 2e 8f ca e0 4a 17 a6 58 27 bf 3b ba 3c 26 f1 21 14 4f c6 39 2d eb 2f 02 03 01 00 01
//autenticacao:30 81 89 02 81 81 00 99 57 52 de 7c a4 2e b6 3c 6c b1 a9 35 a8 74 8b ab c1 90 d4 80 ac 28 2d 2a 55 ba 1f 1a fe 7d 9b a9 d7 b1 19 96 5f 6b fe 36 4b 23 6d b6 97 4e 65 31 ae a4 ee 78 f5 3c a5 ff f2 97 3d cd 16 6a 3a 14 af 55 cf f8 6d 0f 29 c6 81 4d 68 a0 3b f6 86 a0 63 7c 36 10 c1 ed e7 21 73 a4 44 c3 f4 d3 ad a4 ff e3 d3 a2 44 7a 61 77 fe c6 fc 65 7f a6 03 d4 b4 1f 6e 76 7f 8c 5f 10 13 b7 8d 43 1f 4b 23 02 03 01 00 01
	public boolean authenticate(X509Certificate cert, String alghoritm, byte[] data, byte[] signedData){

//		KeyPairGenerator keyGen = null;
//		try {
//			keyGen = KeyPairGenerator.getInstance("RSA");
//		} catch (NoSuchAlgorithmException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//        keyGen.initialize(1024);
//        KeyPair pair = keyGen.genKeyPair();
//        PublicKey pubKey = pair.getPublic();
//        PrivateKey privKey = pair.getPrivate();
		//chave de autenticacao da aplicacao
	//String publicCardKeyAuth = "30818902818100995752de7ca42eb63c6cb1a935a8748babc190d480ac282d2a55ba1f1afe7d9ba9d7b119965f6bfe364b236db6974e6531aea4ee78f53ca5fff2973dcd166a3a14af55cff86d0f29c6814d68a03bf686a0637c3610c1ede72173a444c3f4d3ada4ffe3d3a2447a6177fec6fc657fa603d4b41f6e767f8c5f1013b78d431f4b230203010001";
	//chave de assinatura da aplicacao
	//String publicCardKeySign = "30818902818100c216096b2fe48c472bf1a3a6cedce16b7fd38d5401ec6277957489ce976026adc2659c6af95631d7fa58ba0b7aaca7fe5afa4a6f7eeaecf109d512607705ad273d8d9db4f3a03bd7079a54ebc05443376fb6941b614e16daf16f0fc8e4acc090fa2b5435e9162b7cc12e8fcae04a17a65827bf3bba3c26f121144fc6392deb2f0203010001";
	//String publicKey = "107679788109673484080908988320885514708757674636582251559474809537456388182271981905458790676877466571440459946033835687897447171374945289933330122786027535950713806567247952469329535358149237394213091642242961539573346161013027261888159755925844088699448933179107707258451474593650110609582990704324800105251";
	                    
		try {

			//BigInteger i = new BigInteger(publicCardKeyAuth,16);
			//System.out.println("<<<<"+i+">>>>");
			//Gerar nounce
			//byte[] nounce = new byte[512];
			//SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			//random.nextBytes(nounce);
			//byte[] rsaForSignature = null;
			
			//byte[] array = cert.getPublicKey().getEncoded();
			//BASE64Encoder encoder = new BASE64Encoder();
			
			//System.out.println("Pub.Key:" + neencoder.encode(array));
			//Chama cliente
			//try{
//				Signature rsaForVerify = Signature.getInstance("SHA1withRSA");////
//				rsaForVerify.initSign(privKey);///////////////////////////////
//				rsaForVerify.update(nounce);////
//				rsaForSignature = rsaForVerify.sign();////////
				//rsaForSignature = sign(nounce);
			//Cliente assina
			//}catch(Exception e){
				
				//System.out.println("Exception");
			//}
			//Verificar

			Signature rsaForVerify = Signature.getInstance(alghoritm);
			//rsaForVerify.initVerify(pubKey);
			rsaForVerify.initVerify(cert);
			rsaForVerify.update(data);
			return rsaForVerify.verify(signedData);
			
			//System.out.println("Had success?: " + suc);
			//return suc;

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;


	}

	
	public X509Certificate getAuthCertificate() throws CertificateException, PteidException{
		
		PTEID_Certif[] certs = pteid.GetCertificates();

		System.out.println("Number of certs found: " + certs.length);

		CertificateFactory cf = CertificateFactory.getInstance("X.509");

		X509Certificate certificate;


		for(PTEID_Certif cert : certs){



			System.out.println(cert.certifLabel);//1103
			if(cert.certifLabel.equals("CITIZEN AUTHENTICATION CERTIFICATE")){
				certificate = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(cert.certif));
				//test.authenticate(certificate);
				//System.out.println("ok");
				return certificate;
			}
		}
		
		return null;
	}
	
	
    public void startModule() throws PteidException{
    	
    	
			// test.TestCVC();


			pteid.Init("");


			//test.TestChangeAddress();

			// Don't check the integrity of the ID, address and photo (!)
			pteid.SetSODChecking(false);

			int cardtype = pteid.GetCardType();
			switch (cardtype)
			{
			case pteid.CARD_TYPE_IAS07:
				System.out.println("IAS 0.7 card\n");
				break;
			case pteid.CARD_TYPE_IAS101:
				System.out.println("IAS 1.0.1 card\n");
				break;
			case pteid.CARD_TYPE_ERR:
				System.out.println("Unable to get the card type\n");
				break;
			default:
				System.out.println("Unknown card type\n");
			}
			
    	
    }

	public static void main(String[] args)
	{
		//int ret = 0;
		//AiacSigner test = new AiacSigner();

		

		//try
		//{
			// test.TestCVC();


			//pteid.Init("");


			//test.TestChangeAddress();

			// Don't check the integrity of the ID, address and photo (!)
//			pteid.SetSODChecking(false);
//
//			int cardtype = pteid.GetCardType();
//			switch (cardtype)
//			{
//			case pteid.CARD_TYPE_IAS07:
//				System.out.println("IAS 0.7 card\n");
//				break;
//			case pteid.CARD_TYPE_IAS101:
//				System.out.println("IAS 1.0.1 card\n");
//				break;
//			case pteid.CARD_TYPE_ERR:
//				System.out.println("Unable to get the card type\n");
//				break;
//			default:
//				System.out.println("Unknown card type\n");
//			}

			// Read ID Data
			/*PTEID_ID idData = pteid.GetID();
      if (null != idData)
      {
        test.PrintIDData(idData);
      }

	  // Read Address
      PTEID_ADDR adData = pteid.GetAddr();
      if (null != adData)
      {
        test.PrintAddressData(adData);
      }

	  // Read Picture Data
      PTEID_PIC picData = pteid.GetPic();
      if (null != picData)
      {
        try
        {
           String photo = "photo.jp2";
           FileOutputStream oFile = new FileOutputStream(photo);
           oFile.write(picData.picture);
           oFile.close();
           System.out.println("Created " + photo);
         }
         catch (FileNotFoundException excep)
         {
           System.out.println(excep.getMessage());
         }
         catch(Exception e)
         {
           e.printStackTrace();
         }
      }
			 */
			// PIN operations
			//      int triesLeft = pteid.VerifyPIN((byte)0x83, null);
			//    triesLeft = pteid.ChangePIN((byte)0x83, null, null);

			// Read Certificates
			
/*
			PTEID_Certif[] certs = pteid.GetCertificates();

			System.out.println("Number of certs found: " + certs.length);

			CertificateFactory cf = CertificateFactory.getInstance("X.509");

			X509Certificate certificate;


			for(PTEID_Certif cert : certs){



				System.out.println(cert.certifLabel);//1103
				if(cert.certifLabel.equals("CITIZEN AUTHENTICATION CERTIFICATE")){
					certificate = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(cert.certif));
					//test.authenticate(certificate);
					System.out.println("ok");
				}
			}
*/
			/*
       CertificateFactory cf = CertificateFactory.getInstance("X.509");
       X509Certificate certificate = (X509Certificate)cf.generateCertificate(certs.);
       PublicKey pk = certificate.getPublicKey();
			 */


			// Read Pins
			//PTEID_Pin[] pins = pteid.GetPINs();

			// Read TokenInfo
			//PTEID_TokenInfo token = pteid.GetTokenInfo();

			// Read personal Data
			//byte[] filein = {0x3F, 0x00, 0x5F, 0x00, (byte)0xEF, 0x07};
			//byte[] file = pteid.ReadFile(filein, (byte)0x81);

			// Write personal data
			//   String data = "Hallo JNI";
			//  pteid.WriteFile(filein, data.getBytes(), (byte)0x81);

			//pteid.Exit(pteid.PTEID_EXIT_LEAVE_CARD);
//		}
//		catch (PteidException ex)
//		{
//			ex.printStackTrace();
//			//System.out.println(ex.getMessage());
//		}
//		catch (Exception ex)
//		{
//			ex.printStackTrace();
//			//System.out.println(ex.getMessage());
//		}
	}

	////////////////////////////////// CVC ////////////////////////////////////

	// Modify these values for you test card!
	static String csCvcCertRole01 = "7F2181CD5F37818086B81455BCF0F508CF79FE9CAD574CA631EBC392D78869C4DC29DB193D75AC7E1BB1852AA57FA54C7E7FA97CBB536F2FA384C90C4FF62EAB119156016353AEAFD0F2E2B41BF89CCFE2C5F463A4A30DC38F2B9145DA3F12C40E2F394E7EE606A4C9377253D6E46D7B538B34C712B964F4A20A5724E0F6E88E0D5D1188C39B75A85F383C6917D61A07CFF92106D1885E393F68BA863520887168CE884242ED86F2F80397B42B883D931F8CCB141DC3579E5AB798B8CCF9A189B83B8D0001000142085054474F56101106"; // CVC cert for writing
	static String csCvcCertRole02 = "7F2181CD5F3781804823ED79D2F59E61E842ABE0A58919E63F362C9133E873CA77DD79AD01009247460DFE0294DD0ABAABE1D262E69A165F2F1AC6E953E8ABBE3BF1D2ACD6EB69EE83AB918D6F5116589BE0D40E780D5635238B78AA4290AD32F2A6316D24B417E06591DE6A775C38CFD918CA4FD11146EA20E06FE7F73CA7B3D3058FA259745D875F383C6917D61A07CFF92106D1885E393F68BA863520887168CE884242ED86F2F80397B42B883D931F8CCB141DC3579E5AB798B8CCF9A189B83B8D0001000142085054474F56101106"; // CVC cert for reading
	static String csCvcMod = "924557F6E1C2F1898B391D9255CC72FD7F11128BA148CFEBD1F58AF3F363778157E262FD72A76BCCA0AB43D8F5272E00D21B8B0EE4CC7DA86C8189DEC0DDC58C6A54A81BCE5E52076917D61A07CFF92106D1885E393F68BA863520887168CE884242ED86F2F80397B42B883D931F8CCB141DC3579E5AB798B8CCF9A189B83B8D"; // private key modulus
	static String csCvcExp = "3B35A8CAFE4E6C79D20AB7C6C1C67611D97AEEB7E8FCD175D353030187578F4BA368B7CB82BAF4EF2B66C89B2D79C3AC7F60B8E4B98771A258F202FE51B23441EB29C68569B608EF1F4B3CF15C68744AA7A3800E364739D3C6DCB078EFB81EA3197C843EE17BD9BCF1E0FEB4FFB6719F923C63105206A2F5A77A0437D762E781"; // private key exponent

	/**
	 * BigInteger -> bytes array, taking into account that a leading 0x00 byte
	 * can be added if the MSB is 1 (so we have to remove this 0x00 byte)
	 */
	byte[] ToBytes(BigInteger bi, int size)
	{
		byte[] b = bi.toByteArray();
		if (b.length == size)
			return b;
		else if (b.length != size + 1) {
			System.out.println("length = " + b.length + " instead of " + size);
			return null;
		}
		byte[] r = new byte[size];
		System.arraycopy(b, 1, r, 0, size);
		return r;
	}

	public byte[] SignChallenge(byte[] challenge)
	{
		BigInteger mod = new BigInteger("00" + csCvcMod, 16);
		BigInteger exp = new BigInteger("00" + csCvcExp, 16);
		BigInteger chall = new BigInteger(1, challenge);

		BigInteger signat = chall.modPow(exp, mod);

		return ToBytes(signat, 128);
	}

	public void TestCVC() throws Exception
	{
		/* Convert a hex string to byte[] by using the BigInteger class,
		 * taking into account that the MSB is taken to be the sign bit
		 * (hence adding the "00")
		 * CVC certs are always 209 bytes long */
		byte[] cert1 = ToBytes(new BigInteger("00" + csCvcCertRole01, 16), 209);
		byte[] cert2 = ToBytes(new BigInteger("00" + csCvcCertRole02, 16), 209);

		byte[] fileAddr = { 0x3F, 0x00, 0x5F, 0x00, (byte)0xEF, 0x05 };

		// Read the Address file

		pteid.Init("");

		pteid.SetSODChecking(false);

		byte[] challenge = pteid.CVC_Init(cert2);
		byte[] signat = SignChallenge(challenge);
		pteid.CVC_Authenticate(signat);

		//PTEID_ADDR addr = pteid.CVC_GetAddr();
		//String country = addr.country;
		//System.out.println("Reading address:");
		//System.out.println("  addrType = " + addr.addrType);
		//System.out.println("  country = " + country);

		pteid.Exit(pteid.PTEID_EXIT_UNPOWER);

		// Write to the Address file

		System.out.println("Changing country name to \"XX\"");

		pteid.Init("");
		pteid.SetSODChecking(false);

		challenge = pteid.CVC_Init(cert1);
		signat = SignChallenge(challenge);
		pteid.CVC_Authenticate(signat);

		//addr.country = "XX";
		//pteid.CVC_WriteAddr(addr);

		pteid.Exit(pteid.PTEID_EXIT_UNPOWER);

		System.out.println("  done");

		// Read the Address file again

		pteid.Init("");
		pteid.SetSODChecking(false);

		challenge = pteid.CVC_Init(cert2);
		signat = SignChallenge(challenge);
		pteid.CVC_Authenticate(signat);

		//addr = pteid.CVC_GetAddr();
		//System.out.println("Reading address again:");
		//System.out.println("  addrType = " + addr.addrType);
		//System.out.println("  country = " + country);

		pteid.Exit(pteid.PTEID_EXIT_UNPOWER);

		// Restore the previous address

		System.out.println("Restoring country name");

		pteid.Init("");
		pteid.SetSODChecking(false);

		challenge = pteid.CVC_Init(cert1);
		signat = SignChallenge(challenge);
		pteid.CVC_Authenticate(signat);

		//addr.country = country;
		//pteid.CVC_WriteAddr(addr);

		pteid.Exit(pteid.PTEID_EXIT_UNPOWER);

		System.out.println("  done");
	}

	private static char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	public static String ToHex(byte[] ba)
	{
		StringBuffer buf = new StringBuffer(3 * ba.length + 2);
		for (int i = 0; i < ba.length; i++)
		{
			int c = ba[i];
			if (c < 0)
				c += 256;
			buf.append(HEX[c / 16]);
			buf.append(HEX[c % 16]);
			buf.append(' ');
		}

		return new String(buf);
	}

	//Enche array de bytes
	private static byte[] makeBA(int len, byte val)
	{
		byte ret[] = new byte[len];
		for (int i = 0; i < len; i++)
			ret[i] = val;
		return ret;
	}

	/** Works only with when pteidlib is build with emulation code!!
	 */
	public void TestChangeAddress() throws Exception
	{
		System.out.println("\n*********************************************\n");

		// CVC_Init_SM101()
		byte[] ret = pteid.CVC_Init_SM101();
		System.out.println("CVC_Init_SM101: " + ToHex(ret));

		System.out.println("\n*********************************************\n");

		// CVC_Authenticate_SM101
		byte[] signedChallenge = {0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11};
		byte[] ifdSerialNr = { 0x11, 0x22, 0x33, 0x44, 0x11, 0x22, 0x33, 0x44};
		byte[] iccSerialNr = { 0x44, 0x33, 0x22, 0x11	, 0x44, 0x33, 0x22, 0x11 };

		byte[] keyIfd= makeBA(32, (byte) 0x01);
		byte[] encKey = makeBA(16, (byte) 0x02);
		byte[] macKey = makeBA(16, (byte) 0x03);

		ret = pteid.CVC_Authenticate_SM101(signedChallenge,
				ifdSerialNr, iccSerialNr, keyIfd, encKey, macKey);

		System.out.println("CVC_Authenticate_SM101: " + ToHex(ret));

		System.out.println("\n*********************************************\n");

		// CVC_R_Init()
		PTEID_DH_Params dhParams = pteid.CVC_R_Init();
		System.out.println("CVC_R_Init: G = " + ToHex(dhParams.G));
		System.out.println("CVC_R_Init: P = " + ToHex(dhParams.P));
		System.out.println("CVC_R_Init: Q = " + ToHex(dhParams.Q));

		System.out.println("\n*********************************************\n");

		// CVC_R_DH_Auth()
		byte[] Kidf = makeBA(8, (byte) 0x22);

		byte[] cvcCert = ToBytes(new BigInteger("00" + csCvcCertRole01, 16), 209);

		PTEID_DH_Auth_Response dhAuthResp = pteid.CVC_R_DH_Auth(Kidf, cvcCert);

		System.out.println("CVC_R_DH_Auth: Kicc = " + ToHex(dhAuthResp.Kicc));
		System.out.println("CVC_R_DH_Auth: challenge = " + ToHex(dhAuthResp.challenge));

		System.out.println("\n*********************************************\n");

		// CVC_R_ValidateSignature
		System.out.println("CVC_R_ValidateSignature: signedChallenge = " + ToHex(signedChallenge));
		pteid.CVC_R_ValidateSignature(signedChallenge);

		System.out.println("\n*********************************************\n");

		// SendAPDU()
		ret = pteid.SendAPDU(new byte[] {0x00, 0x20, 0x00, (byte) 0x81});
		System.out.println("Response to case 1 APDU: " + ToHex(ret));
		ret = pteid.SendAPDU(new byte[] { (byte)0x80, (byte)0x84, 0x00, 0x00, 0x08 });
		System.out.println("Response to case 2 APDU: " + ToHex(ret));
		ret = pteid.SendAPDU(new byte[] { 0x00, (byte)0xA4, 0x02, 0x0C, 0x02, 0x2F, 0x00 });
		System.out.println("Response to case 3 APDU: " + ToHex(ret));
		ret = pteid.SendAPDU(new byte[] { 0x00, (byte)0xA4, 0x02, 0x00, 0x02, 0x50, 0x31, 0x50 });
		System.out.println("Response to case 4 APDU: " + ToHex(ret));

		System.out.println("\n*********************************************\n");

		// ChangeAddress()
		byte[] serverCaCert = makeBA(1200, (byte) 0x05);
		PTEID_Proxy_Info proxyInfo = new PTEID_Proxy_Info();
		proxyInfo.proxy = "10.3.98.67";
		proxyInfo.port = 4444;
		proxyInfo.username = "userX";
		proxyInfo.password = "passwdX";
		pteid.ChangeAddress("https://www.test.com/ChangeAddress", serverCaCert,
				proxyInfo, "secretcode", "processcode");

		System.out.println("\n*********************************************\n");

		// GetChangeAddressProgress()
		int res = pteid.GetChangeAddressProgress();
		System.out.println("GetChangeAddressProgress(): returned " + res);

		System.out.println("\n*********************************************\n");

		// CancelChangeAddress()
		pteid.CancelChangeAddress();
		System.out.println("GetChangeAddressProgress(): done");

		System.out.println("\n*********************************************\n");

		// CAP_ChangeCapPin()
		pteid.CAP_ChangeCapPin("https://www.test.com/ChangeAddress", serverCaCert,
				proxyInfo, "1234", "123456");

		System.out.println("\n*********************************************\n");

		// CAP_GetCapPinChangeProgress
		res = pteid.CAP_GetCapPinChangeProgress();
		System.out.println("CAP_GetCapPinChangeProgress(): returned " + res);

		System.out.println("\n*********************************************\n");

		// CAP_CancelCapPinChange()
		pteid.CAP_CancelCapPinChange();
		System.out.println("CAP_CancelCapPinChange(): done");

		System.out.println("\n*********************************************\n");

		// GetLastWebErrorMessage()
		String msg = pteid.GetLastWebErrorMessage();
		System.out.println("GetLastWebErrorMessage(): returned " + msg);

		System.out.println("\n*********************************************\n");
	}
}