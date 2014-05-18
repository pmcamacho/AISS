import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class CRYIOCipher {
    
    
    private final CRYIO_HARDWARE_JNI p = new CRYIO_HARDWARE_JNI();
    
    public CRYIOCipher(){
	
    }
    
    private byte[] update(byte[] data) throws IOException{

	char ret_code;
	
	
	System.out.println("Length: " + data.length);
	
	
	
	ByteArrayInputStream in = new ByteArrayInputStream(data);
	ByteArrayOutputStream buffer = new ByteArrayOutputStream();


	int nRead;
	byte[] buffer_in = new byte[CRYIO_HARDWARE_JNI.MAX_DATA_IN];
	byte[] buffer_out = new byte[CRYIO_HARDWARE_JNI.MAX_DATA_OUT];
	 
	
	
	int size = 0;
	while ((nRead = in.read(buffer_in, 0, CRYIO_HARDWARE_JNI.MAX_DATA_IN)) != -1) {

	    long[] n = new long[1];
	    ret_code = p.update(buffer_in,nRead,buffer_out,n);
	    
	    //System.out.println(new String(buffer_in));
	  
	    //System.out.println(new String(buffer_out));

	    if(ret_code < 0 )	/*Send data blocks*/
	    {
		System.out.println(String.format("\n Error : Update()  %d \n", (int) ret_code));
		return null;
	    }

	    buffer.write(buffer_out,0, (int)n[0]);
	    
	    size += n[0];
	    
	    buffer_in = new byte[CRYIO_HARDWARE_JNI.MAX_DATA_IN];
	    buffer_out = new byte[CRYIO_HARDWARE_JNI.MAX_DATA_OUT];
	}
	
	

	
	
	
	System.out.println(size);
	buffer_out = new byte[CRYIO_HARDWARE_JNI.MAX_DATA_OUT];
	long[] n = new long[1];
	 
	if( (ret_code  = p.doFinal(buffer_in,0,buffer_out,n) ) < 0 )
	{
	    size += (int)n[0];
	    System.out.println(String.format("\n Error : doFinal() %d \n", (int) ret_code));	/*Send last data blocks to finalize*/
	    return null;
	} 
	else {
	    
	    size += (int)n[0];
	    buffer.write(buffer_out,0,(int)n[0]);
	}


	System.out.println(size);
	buffer.flush();

	return buffer.toByteArray();

    }


    public  byte[] cifra(byte[] data) throws IOException {

	byte[] IV = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	int mode = CRYIO_HARDWARE_JNI.ROUNDS_10 | CRYIO_HARDWARE_JNI.CBC_FLAG |CRYIO_HARDWARE_JNI.FIRST_FLAG| CRYIO_HARDWARE_JNI.ENCRYPT_FLAG;
	long keynum = CRYIO_HARDWARE_JNI.keynum;
	byte[] key = CRYIO_HARDWARE_JNI.key;
	
	p.init(keynum,mode, key, IV);
	
	//Faz cifra
	return update(data);
    }

    public  byte[] decifra(byte[] data) throws IOException {

	byte[] IV = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	int mode = CRYIO_HARDWARE_JNI.ROUNDS_10 | CRYIO_HARDWARE_JNI.CBC_FLAG |CRYIO_HARDWARE_JNI.FIRST_FLAG| CRYIO_HARDWARE_JNI.DECRYPT_FLAG;
	long keynum = CRYIO_HARDWARE_JNI.keynum;
	byte[] key = CRYIO_HARDWARE_JNI.key;

	p.init(keynum,mode, key, IV);

	//Faz decifra
	return update(data);

    }
    
    
    
    public static void main(String[] args) {
	
	
	CRYIOCipher c = new CRYIOCipher();
	
	String filename = args[0];
	Path p = Paths.get(filename);
	
	 System.out.println(args[0]);
	
	
	try {
	
	    byte [] data = Files.readAllBytes(p);
	    
	   
	    
	    byte [] d = null; 
		    
	    
	    if(args[1].equals("c")) {
	    
		System.out.println("Cifra");
		d = c.cifra(data);  
	    
	    }
	    else {
		
		System.out.println("Decifra");
		d = c.decifra(data); 
	    
	    }
		
	    
	    
	    
	    Files.write(p , d);
	
	} catch (IOException e) {
	    e.printStackTrace();
	}
	
    }
    

}