
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;


public class CRYIO_HARDWARE_JNI {


    //Protocol.h
    public static int EBC_FLAG 	=  0x00000000;
    public static int CBC_FLAG 	= 0x00000200;

    public static int FIRST_FLAG = 0x00000400;

    public static int DECRYPT_FLAG = 0x00000100;

    public static int ENCRYPTION_MASK = 0x00000100;

    public static int ENCRYPT_FLAG = 0x00000000;


    public static int ROUNDS_10 = 0x0A;
    public static int ROUNDS_12 = 0x0C;
    public static int ROUNDS_14 = 0x0D;

    public static int AES_BLOCK_SIZE = 32;
    public static int MIN_BLOCK_NUM = 20;
    public static int MAX_BLOCK_NUM =  43;
    public static int PACKET_HEADER_SIZE = 6;



    public static int MIN_PACKET_DATA = (MIN_BLOCK_NUM*AES_BLOCK_SIZE);
    public static int MAX_PACKET_DATA = (MAX_BLOCK_NUM*AES_BLOCK_SIZE); /* 1514 - 6 (packet_t header) - 42 (ip/udp header) */
    public static int MAX_PACKET_RAW_DATA = (45*AES_BLOCK_SIZE + PACKET_HEADER_SIZE);
    public static int MAX_DATA_IN  = (MAX_PACKET_DATA*50);
    public static int MAX_DATA_OUT = (MAX_DATA_IN + AES_BLOCK_SIZE);



    public  static byte[] key = key();
    public static long keynum = 0;

    static {

	System.load("C:\\Users\\Pedro\\Documents\\GitHub\\AISS\\HARDWARE_JNI.dll");

    }

    public native void init(long keynum, long mode, byte[] key,  byte[] IV);
    public native char update(byte[] buffer_in, long data_len, byte[] buffer_out, long[] n);
    public native char doFinal(byte[] buffer_in, long data_len, byte[] buffer_out, long[] n);


    public CRYIO_HARDWARE_JNI(){



    }

    public static byte[] key(){

	ByteBuffer origkey = ByteBuffer.allocate(4*(8+8));
	origkey.order(ByteOrder.BIG_ENDIAN);

	origkey.putInt(3,0);
	origkey.putInt(2,0);
	origkey.putInt(1,1);
	origkey.putInt(0,0);
	origkey.putInt(4, 0);
	origkey.putInt(5, 0);
	origkey.putInt(6,0);
	origkey.putInt(7,0);

	origkey.putInt(3+8, 0x0c0d0e0f);
	origkey.putInt(2+8, 0x08090a0b);
	origkey.putInt(1+8, 0x04050607);
	origkey.putInt(0+8, 0x00010203);
	origkey.putInt(4+8, 0x10111213);
	origkey.putInt(5+8, 0x14151617);
	origkey.putInt(6+8, 0x18191a1b);
	origkey.putInt(7+8, 0x1c1d1e1f);

	origkey.flip();





	return Arrays.copyOf(origkey.array(),origkey.array().length);
    }

    public static void main(String args[]) {

	CRYIO_HARDWARE_JNI p = new CRYIO_HARDWARE_JNI();




	long mode;
	boolean isInEncriptMode = args[0].equals("c");

	if(isInEncriptMode)
	    mode = ROUNDS_10 | CBC_FLAG |FIRST_FLAG| ENCRYPT_FLAG;
	else {
	    mode = ROUNDS_10 | CBC_FLAG |FIRST_FLAG| DECRYPT_FLAG;
	}

	byte[] IV = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};


	//Cifrar
	mode = ROUNDS_10 | CBC_FLAG |FIRST_FLAG| ENCRYPT_FLAG;
	p.init(keynum, mode, key, IV);

	//p.init(keynum, mode_dec, key, IV);


	//byte [] in = Files.readAllBytes(Paths.get(""));

	byte[] in = "SergioSergioSergioSergio".getBytes();

	byte[] cifrado = new byte[MAX_DATA_OUT+1100];

	long [] nu = new long[1];
	long [] nf = new long[1];

	System.out.println(in.length);
	System.out.println(MAX_DATA_OUT);
	char ret = p.update(in,in.length, cifrado, nu);

	in = new byte[1];
	ret = p.doFinal(in, 0, cifrado, nf);
	System.out.println("nu: "+nu[0]);
	System.out.println("nf: " + nf[0]);


	//Decifrar
	mode = ROUNDS_10 | CBC_FLAG |FIRST_FLAG| DECRYPT_FLAG;
	p.init(keynum, mode, key, IV);

	in = new byte[MAX_DATA_OUT];

	long [] n = new long[1];


	ret = p.update(cifrado,nu[0]+nf[0], in, n);
	System.out.println("n decifra: " + n[0]);

	in = new byte[MAX_DATA_OUT];
	ret = p.doFinal(cifrado, 0, in, n);
	System.out.println("n decifra: " + n[0]);

	System.out.println("out:" + new String(in));



	//	if(isInEncriptMode){
	//	    System.out.println("Cifrado");
	//	    System.out.println("n:" + n[0]);
	//	    System.out.println("out:" + new String(out));
	//	    System.out.println("ret:" + (int)ret);
	//	
	//	} else {
	//	    System.out.println("Decifrado");
	//	    System.out.println("n:" + n[0]);
	//	    System.out.println("out:" + new String(out));
	//	    System.out.println("ret:" + (int)ret);
	//	}





	//	if(isInEncriptMode){
	//	    System.out.println("Cifrado");
	//	    System.out.println("n:" + n[0]);
	//	    System.out.println("out:" + new String(out));
	//	    System.out.println("ret:" + (int)ret);
	//	} else {
	//	    System.out.println("Decifrado");
	//	    System.out.println("n:" + n[0]);
	//	    System.out.println("out:" + new String(out));
	//	    System.out.println("ret:" + (int)ret);
	//	}

    }

}