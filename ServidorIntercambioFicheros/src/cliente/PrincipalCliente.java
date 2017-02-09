//Nombre: Borja Jimeno Soto
//DNI: 18074126J
package cliente;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Timer;

import utils.Conexion;
import utils.Usuario;

public class PrincipalCliente {
	static Usuario u;
	static File d;

	public static void main(String[] args) {
		int opcion = 0, puerto;
		String fichero;
		Cliente c;
		String directorio = "";
		Scanner entrada = new Scanner(System.in);
		// Semaphore s;
		Timer t;
		do {
			System.out.println("¿Desea crear un directorio para almacenar las descargas? Y/N");
			directorio = entrada.nextLine();
		} while (!(directorio.equalsIgnoreCase("y") || directorio.equalsIgnoreCase("n")));
		if (directorio.equalsIgnoreCase("y")) {
			System.out.println("Escriba el directorio:");
			directorio = entrada.nextLine();
			d = new File(directorio);
			if (!d.exists()) {
				System.out.println("El directorio no existe. Será creado.");
			}
		} else {
			d = new File("./Compartidos");
		}
		d.mkdir();
		System.out.println("¿En que puerto desea atender peticiones?");
		puerto = entrada.nextInt();
		u = new Usuario(puerto, "localhost");
		try (Conexion con = new Conexion(new Socket("localhost", 6666))) {
			t = new Timer();
			c = new Cliente(u, d, con);
			t.schedule(new Temporizador(c), 5 * 60 * 10000);
			Server servidor = new Server(puerto, directorio, c);
			Thread ser = new Thread(servidor);
			ser.start();

			c.HandShake();
			c.getFiles();

			do {

				do {
					System.out.println("0: Salir");
					System.out.println("1: Descargar fichero");
					try {
						opcion = entrada.nextInt();
					} catch (InputMismatchException e) {
						opcion = 2;
					}
					switch (opcion) {
					case 1:
						c.listarFicheros();
						System.out.println("Introduzca el nombre del fichero que desea buscar:");
						entrada.nextLine();
						fichero = entrada.nextLine();
						c.Descargar(c.getFile(fichero));
						break;
					case 2:

						break;
					}

				} while (opcion < 0 && opcion > 1);
			} while (opcion != 0);
			c.Logout();
			c.close();
			ser.interrupt();
			entrada.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
