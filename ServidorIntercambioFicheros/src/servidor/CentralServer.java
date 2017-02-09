//Nombre: Borja Jimeno Soto
//DNI: 18074126J
package servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CentralServer {

	private static ExecutorService pool = Executors.newCachedThreadPool();
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try(ServerSocket ss = new ServerSocket(6666)){
			System.out.println("Escuchando");
			while(true){
				try {
					atenderCliente(ss.accept());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			pool.shutdown();
		}

	}
	private static void atenderCliente(Socket s){
		Runnable r = new AtenderPeticion(s);
		pool.execute(r);
	}

}
