//Nombre: Borja Jimeno Soto
//DNI: 18074126J
package cliente;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import utils.Conexion;

public class Server implements Runnable {
	// public HTTPServer
	int puerto;
	String HOMEDIR;
	Cliente c;

	public Server(int puerto, String home, Cliente c) {
		this.puerto = puerto;
		HOMEDIR = home;
		this.c=c;
	}

	public void run() {
		ExecutorService pool = Executors.newCachedThreadPool();
		System.out.println("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq escuchando en "+puerto);
		try (ServerSocket ss = new ServerSocket(puerto)) {
			while (true && !Thread.currentThread().isInterrupted()) {
				try {
					pool.execute(new AtenderCliente(new Conexion(ss.accept()), HOMEDIR, c));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pool.shutdown();
	}

}
