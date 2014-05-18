import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;




public class CRYIOFile implements Serializable {

    private static final long serialVersionUID = 1L;

    private Byte[] file;
    private Byte[] fileSignature;

    private static CRYIO_HARDWARE_JNI p = new CRYIO_HARDWARE_JNI();

    public CRYIOFile(byte[] file , byte[] fileSignature) {
	this.file = wrap(file);
	this.fileSignature = wrap(fileSignature);
	
    }


    private Byte[] wrap(byte[] array){

	Byte[] bytes = new Byte[array.length];

	for(int i= 0;i < array.length;i++){
	    bytes[i] = array[i];
	}

	return bytes;

    }

    private byte[] unwrap(Byte[] array){

	byte[] bytes = new byte[array.length];

	for(int i= 0; i < array.length;i++){
	    bytes[i] = array[i].byteValue();
	}

	return bytes;

    }

    public byte[] getFile() {

	return unwrap(this.file);

    }


    public byte[] getFileSignature() {

	return unwrap(this.fileSignature);

    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
	stream.writeObject(this.file);
	stream.writeObject(this.fileSignature);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
	this.file = (Byte[]) stream.readObject();
	this.fileSignature = (Byte[]) stream.readObject();
    }




    private static byte[] update(byte[] data) throws IOException{

	char ret_code;
	ByteArrayInputStream in = new ByteArrayInputStream(data);
	ByteArrayOutputStream buffer = new ByteArrayOutputStream();


	int nRead;
	byte[] buffer_in = new byte[CRYIO_HARDWARE_JNI.MAX_DATA_IN];

	long[] n = new long[1];

	while ((nRead = in.read(buffer_in, 0, CRYIO_HARDWARE_JNI.MAX_DATA_IN)) != -1) {

	    byte[] buffer_out = new byte[CRYIO_HARDWARE_JNI.MAX_DATA_OUT];
	    ret_code = p.update(buffer_in,nRead,buffer_out,n);
	    
	    System.out.println(new String(buffer_in));
	    System.out.println(n[0]);
	    System.out.println(new String(buffer_out));

	    if(ret_code < 0 )	/*Send data blocks*/
	    {
		System.out.println(String.format("\n Error : Update()  %d \n", (int) ret_code));
		return null;
	    }

	    buffer.write(buffer_out,0, (int)n[0]);
	}


	byte[] buffer_out = new byte[CRYIO_HARDWARE_JNI.MAX_DATA_OUT];
	if( (ret_code  = p.doFinal(buffer_in,0,buffer_out,n) ) < 0 )
	{
	    System.out.println(String.format("\n Error : doFinal() %d \n", (int) ret_code));	/*Send last data blocks to finalize*/
	    return null;
	} 
	else {

	    buffer.write(buffer_out,0,(int)n[0]);
	}



	buffer.flush();

	return buffer.toByteArray();

    }


    private static byte[] cifra(byte[] data) throws IOException {

	byte[] IV = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	int mode = CRYIO_HARDWARE_JNI.ROUNDS_10 | CRYIO_HARDWARE_JNI.CBC_FLAG |CRYIO_HARDWARE_JNI.FIRST_FLAG| CRYIO_HARDWARE_JNI.ENCRYPT_FLAG;
	long keynum = CRYIO_HARDWARE_JNI.keynum;
	byte[] key = CRYIO_HARDWARE_JNI.key;
	
	p.init(keynum,mode, key, IV);
	
	//Faz cifra
	return update(data);
    }

    private static byte[] decifra(byte[] data) throws IOException {

	byte[] IV = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	int mode = CRYIO_HARDWARE_JNI.ROUNDS_10 | CRYIO_HARDWARE_JNI.CBC_FLAG |CRYIO_HARDWARE_JNI.FIRST_FLAG| CRYIO_HARDWARE_JNI.DECRYPT_FLAG;
	long keynum = CRYIO_HARDWARE_JNI.keynum;
	byte[] key = CRYIO_HARDWARE_JNI.key;

	p.init(keynum,mode, key, IV);

	//Faz decifra
	return update(data);

    }
    
    public static void main(String[] args) {
	
	
	
	
	String filename = args[0];
	Path p = Paths.get(filename);
	
	 System.out.println(args[0]);
	
	
	try {
	
	    byte [] data = Files.readAllBytes(p);
	    
	   
	    
	    byte [] d = null; 
		    
	    
	    if(args[1].equals("c")) {
	    
		System.out.println("Cifra");
		d = cifra(data);  
	    
	    }
	    else {
		
		System.out.println("Decifra");
		d = decifra(data); 
	    
	    }
		
	    
	    
	    
	    Files.write(p , d);
	
	} catch (IOException e) {
	    e.printStackTrace();
	}
	
    }
    
}