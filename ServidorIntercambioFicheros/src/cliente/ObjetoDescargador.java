//Nombre: Borja Jimeno Soto
//DNI: 18074126J
package cliente;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class ObjetoDescargador implements Runnable {
	String url;
	URL u;
	String directorio;
	long ini;
	long fin;
	CyclicBarrier f;

	public ObjetoDescargador(CyclicBarrier f, String url, String directorio, long ini, long fin) {
		super();
		this.url = url;
		this.directorio = directorio;
		this.ini = ini;
		this.fin = fin;
		this.f = f;
		try {
			u = new URL(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		int buffSize = 1024;
		HttpURLConnection con;
		try {
			con = (HttpURLConnection) u.openConnection();
			con.setRequestProperty("Range", "bytes="+ini + "-" + fin);
			String[] nombre = u.getFile().split("/");
			try (RandomAccessFile afile = new RandomAccessFile(directorio + "/" + nombre[nombre.length - 1], "rw");
					DataInputStream is = new DataInputStream(con.getInputStream())) {
				afile.seek(ini);
				byte buff[] = new byte[buffSize];
				int leidos = is.read(buff);
				while (leidos != -1) {
					afile.write(buff, 0, leidos);
					leidos = is.read(buff);
				}
				afile.close();
				try {
					con.disconnect();
					f.await();
				} catch (InterruptedException | BrokenBarrierException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

}
