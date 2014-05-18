
import java.io.File;
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

    public static void writeFile(Path path, CRYIOFile cryio_file) {

	ObjectOutputStream os = null;
	
	try {

	    os =  new ObjectOutputStream(new FileOutputStream(path.toFile()));
	    os.writeObject(cryio_file);

	} catch (FileNotFoundException e) {
	    System.out.println(path.toString());

	    File file = new File(path.toString());
	    try {

		file.createNewFile();
		new ObjectOutputStream(new FileOutputStream(path.toFile())).writeObject(cryio_file);

	    } catch (IOException e1) {

		e1.printStackTrace();
	    
	    } finally {
		
		if(os != null){
		    try {
			os.close();
		    } catch (IOException e1) {
		
			e1.printStackTrace();
		    }
		}
	    
	    }

	} catch (IOException e) {
	
	    e.printStackTrace();
	
	} finally {
	    
	    if(os != null)
		try {
		    os.close();
		} catch (IOException e) {
		   
		    e.printStackTrace();
		}
	
	}

    }

}
