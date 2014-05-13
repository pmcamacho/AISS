import java.nio.ByteBuffer;


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


    static {

	System.load("C:\\Users\\Sergio\\workspace\\AISS_FINAL\\libcryio_hardware.dll");

    }

    public native void init(long keynum, long mode, byte[] key,  byte[] IV);
    public native char update(byte[] buffer_in, long data_len, byte[] buffer_out, long n);
    public native char doFinal(byte[] buffer_in, long data_len, byte[] buffer_out, long n);

    public static void main(String args[]) {

	CRYIO_HARDWARE_JNI p = new CRYIO_HARDWARE_JNI();

	ByteBuffer origkey = ByteBuffer.allocate(4*(8+8));

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



	long keynum = 0;

	byte[] key = origkey.array();


	long mode;
	boolean isInEncriptMode = args[0].equals("c");
	
	if(isInEncriptMode)
	    mode = ROUNDS_10 | CBC_FLAG |FIRST_FLAG| ENCRYPT_FLAG;
	else {
	    mode = ROUNDS_10 | CBC_FLAG |FIRST_FLAG| DECRYPT_FLAG;
	}

	byte[] IV = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

	p.init(keynum, mode, key, IV);

	//p.init(keynum, mode_dec, key, IV);

	byte[] in = "Sergio".getBytes();

	byte[] out = new byte[MAX_DATA_OUT];

	int n = 0;

	char ret = p.update(in,in.length, out, n);
	
	if(isInEncriptMode){
	    System.out.print(new String(out));
	    System.out.print(ret);
	} else {
	    System.out.print(new String(out));
	    System.out.print(ret);
	}

	ret = p.doFinal(null,0, out, n);
	
	
	if(isInEncriptMode){
	    System.out.print(new String(out));
	    System.out.print(ret);
	} else {
	    System.out.print(new String(out));
	    System.out.print(ret);
	}
	
    }

}