package fbcmd4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import facebook4j.Facebook;
import facebook4j.Post;
import facebook4j.ResponseList;

import fbcmd4j.utils.Utils;

public class Main {
	static final Logger logger = LogManager.getLogger(Main.class);
	private static final String CONFIG_DIR = "config";
	private static final String CONFIG_FILE = "fbcmd4j.properties";

	public static void main(String[] args) {
		logger.info("Iniciando app");
		Properties props = null;

		try {
			props = Utils.loadConfigFile(CONFIG_DIR, CONFIG_FILE);
		} catch (IOException ex) {
			logger.error(ex);
		}
		
		int option = 1;
		try {
			Scanner scan = new Scanner(System.in);
			while(true) {
				Facebook fb = Utils.configFacebook(props);
				System.out.println("Aplicacion de Facebook en linea de comando\n\n"
								+  "Elige accion \n"
								+  "(0) Configurar Cliente \n"
								+  "(1) NewsFeed \n"
								+  "(2) Wall \n"
								+  "(3) Publicar Estado \n"
								+  "(4) Publicar Link \n"
								+  "(5) Salir \n"
								+  "\nPor favor ingrese numero de accion deseada:");
				try {
					option = scan.nextInt();
					scan.nextLine();
					switch (option) {
					case 0:
						Utils.configTokens(CONFIG_DIR, CONFIG_FILE, props, scan);
						props = Utils.loadConfigFile(CONFIG_DIR, CONFIG_FILE);
						break;
					case 1:
						System.out.println("Mostrando NewsFeed...");
						ResponseList<Post> newsFeed = fb.getFeed();
						for (Post p : newsFeed) {
							Utils.printPost(p);
						}
						askToSaveFile("NewsFeed", newsFeed, scan);

						break;
					case 2:
						System.out.println("Accediendo a Wall...");
						ResponseList<Post> wall = fb.getPosts();
						for (Post p : wall) {
							Utils.printPost(p);
						}		
						askToSaveFile("Wall", wall, scan);
						break;
					case 3:
						System.out.println("Ingresa tu estado: ");
						String estado = scan.nextLine();
						Utils.postStatus(estado, fb);
						break;
					case 4:
						System.out.println("Ingresa el link: ");
						String link = scan.nextLine();
						Utils.postLink(link, fb);
						break;

					case 5:
						System.out.println("Gracias por usar la aplicacion!");
						System.exit(0);
						break;
					default:
						break;
					}
				} catch (InputMismatchException ex) {
					System.out.println("Ocurrio un error, favor de revisar log.");
					logger.error("Opcion invalida. \n", ex.getClass());
				} catch (Exception ex) {
					System.out.println("Ocurrio un error, favor de revisar log.");
					logger.error(ex);
				}
				System.out.println();
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	public static void askToSaveFile(String fileName, ResponseList<Post> posts, Scanner scan) {
		System.out.println("Guardar resultados en un archivo de texto? Si/No");
		String option = scan.nextLine();
		
		if (option.contains("Si") || option.contains("Si")) {
			List<Post> ps = new ArrayList<>();
			int n = 0;

			while(n <= 0) {
				try {
					System.out.println("Cuantos posts deseas guardar?");
					n = Integer.parseInt(scan.nextLine());					
			
					if(n <= 0) {
						System.out.println("Favor de ingresar un numero valido");
					} else {
						for(int i = 0; i<n; i++) {
							if(i>posts.size()-1) break;
							ps.add(posts.get(i));
						}
					}
				} catch(NumberFormatException e) {
					logger.error(e);
				}
			}

			Utils.savePostsToFile(fileName, ps);
		}
	}
}
