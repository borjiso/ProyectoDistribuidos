//Nombre: Borja Jimeno Soto
//DNI: 18074126J
package cliente;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DescargadorConcurrente {
	private String directorio = ".";
	public DescargadorConcurrente(String directorio){
		this.directorio = directorio;
	}
	public void Descargar(String url) throws IOException {
		URL u;
		final CyclicBarrier fin = new CyclicBarrier(4);
		long tam;
		long parte, mod;
		ObjetoDescargador o1, o2, o3;
		ExecutorService pool;
		try {
			u = new URL(url);
			HttpURLConnection con = (HttpURLConnection) u.openConnection();
			con.setRequestMethod("HEAD");
			tam = con.getContentLengthLong();
			parte = tam / 3;
			mod = tam % 3;
			pool = Executors.newFixedThreadPool(3);
			o1 = new ObjetoDescargador(fin, url, directorio, 0, parte - 1);
			o2 = new ObjetoDescargador(fin, url, directorio, parte, (2 * parte) - 1);
			o3 = new ObjetoDescargador(fin, url, directorio, (2 * parte), (3 * parte) - 1 + mod);
			pool.execute(o1);
			pool.execute(o2);
			pool.execute(o3);
			try {
				fin.await();
			} catch (InterruptedException | BrokenBarrierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pool.shutdown();
			System.out.println("Done");

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
