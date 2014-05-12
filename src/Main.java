import java.nio.file.Paths;


public class Main {

    public static void main(String[] args) {
	
	
	//opcao arg = 0
	//
	
	String opcao = args[0];
	
	if(opcao.equals("d")){

	    CRYIOFileManager.readFile(Paths.get("/simples.cifrado"));
	  
	}
	else if (opcao.equals("c")){
	    
	    byte [] hello_file =  "Hello".getBytes();
	    
	    CRYIOFile simples =  new CRYIOFile(hello_file, "Sergio Alves".getBytes());
	    CRYIOFileManager.writeFile(Paths.get("/simples.cifrado"), simples);
	    
	}
	

    }

}
