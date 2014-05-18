package cryio;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

import cartaocidadao.CartaoCidadaoUtils;


public class Main {

	private static CartaoCidadaoUtils sign = null;
	private static CRYIOCipher cipher = null;

	public static void main(String[] args) {

		//opcao 1 - caminho local
		//opcao 2 - caminho dropbox

		String local_dir = args[0];
		String cloud_dir = args	[1];

		Scanner s = new Scanner(System.in);


		cipher = new CRYIOCipher();

		sign = new CartaoCidadaoUtils();

		System.out.println("Local Dir " + local_dir);
		System.out.println("Cloud Dir " + cloud_dir);

		while(true) {

			System.out.println("Commands:");
			System.out.println("get <file in cloud dir>");
			System.out.println("put <file in local dir>");
			System.out.println("exit");



			String [] option_splitted = s.nextLine().split("\\s");

			String option = option_splitted[0].replaceAll("\\s","").toLowerCase();


			if(option.equals("get")){

				if(option_splitted.length == 2){

					String file_path = option_splitted[1];
					get(local_dir,cloud_dir, file_path);

				} 
				else {
					System.out.println("file_path empty");
				}

			} 

			else if (option.equals("put")){


				if(option_splitted.length == 2){

					String file_path = option_splitted[1];
					put(local_dir,cloud_dir, file_path);

				}
				else {
					System.out.println("file_path empty");
				}


			}

			else if (option.equals("exit")){
				System.out.println("Good bye :'(");
				break;
			}

			else {
				System.out.println("Unknow command");
			}





		}

		s.close();

	}

	private static void put(String local_dir, String cloud_dir, String file_path) {


		byte[] file;

		try {

			Path p = Paths.get(local_dir, file_path);

			if(Files.notExists(p)){
				System.out.println("O ficheiro " +  p.toString() + " nao existe!");
				return;
			}

			file = Files.readAllBytes(p);

			byte[] signed = sign.sign(file);
			
			if(signed == null) {
				System.out.println("");
				return;
			}

			//Cifra ficheiro
			file = cipher.cifra(file);

			//Cifra assinatura
			signed = cipher.cifra(signed);
			
			

			CRYIOFile cryio_file = new CRYIOFile(file, signed);

			CRYIOFileManager.writeFile(Paths.get(cloud_dir, file_path), cryio_file);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void get(String local_dir, String cloud_dir, String file) {



		Path p = null;

		try {

			p = Paths.get(cloud_dir, file);
			if(Files.notExists(p)){
				System.out.println("O ficheiro " +  p.toString() + " nao existe!");
				return;
			}

		}

		catch(InvalidPathException e){
			System.out.println("O caminho foi mal especificado!");
			return;
		}


		try {

			CRYIOFile cryio_file =  CRYIOFileManager.readFile(p);

			byte[] file_bytes = cryio_file.getFile();
			byte[] file_sign = cryio_file.getFileSignature();




			//Decifra ficheiro
			file_bytes = cipher.decifra(file_bytes);
			//Decifra assinatura
			file_sign = cipher.decifra(file_sign);

			//Verifica assinatura

			boolean verified = sign.checkSignature(file_bytes, file_sign);

			if(verified){

				System.out.println("O ficheiro pode ser acedido " + file);

			} else {

				System.out.println("Nao pode aceder ao ficheiro " + file);
				return;
			}

			Files.write(Paths.get(local_dir, file), file_bytes, StandardOpenOption.CREATE);  


		} catch (IOException e) {

			System.out.println("Nao pode aceder ao ficheiro " + file);

		}

	}

}