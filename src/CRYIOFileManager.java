import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;


public class CRYIOFileManager {

    
    public static CRYIOFile readFile(Path path) {
	
	CRYIOFile objFile = null;
	
	try {
	       
	    objFile = (CRYIOFile) new ObjectInputStream(new FileInputStream(path.toFile())).readObject();
	   
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	}
	   
	   return objFile;
	   
    }
    
    public static void writeFile(Path path, CRYIOFile criofile) {
	
	try {
	       
	    new ObjectOutputStream(new FileOutputStream(path.toFile())).writeObject(criofile);
	   
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	
    }
    
}
