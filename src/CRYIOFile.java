import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class CRYIOFile implements Serializable {

    private static final long serialVersionUID = 1L;

    Byte[] file;
    Byte[] fileSignature;

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

	for(int i= 0;i < array.length;i++){
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

}