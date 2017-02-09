//Nombre: Borja Jimeno Soto
//DNI: 18074126J
package utils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;

public class Conexion implements Closeable {
	Writer wr;
	Socket s;
	BufferedReader br;
	ObjectInputStream ois;
	ObjectOutputStream oos;
	DataOutputStream dos;

	public Conexion(Socket s) {
		this.s = s;
		try {
			wr = new OutputStreamWriter(s.getOutputStream());
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			dos = new DataOutputStream(s.getOutputStream());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void Write(String msg) throws IOException {
		System.out.println("escribiendo "+msg);
		wr.write(msg);
		wr.write("\r\n");
		wr.flush();
	}

	public String getLine() throws IOException {
		String linea;
		linea=br.readLine();
		System.out.println("Leyendo"+linea);
		return linea;
		//return br.readLine();
	}

	public Object getObject() throws ClassNotFoundException, IOException {
		System.out.println("getObject ");
		ois = new ObjectInputStream(s.getInputStream());
		return ois.readObject();
	}

	public void putObject(Object o) throws IOException {
		System.out.println("putObject "+o.toString());
		oos = new ObjectOutputStream(s.getOutputStream());
		oos.flush();
		oos.writeObject(o);
		oos.flush();
	}
	public void putData(byte[] buffer) throws IOException{
		dos.write(buffer);
		dos.flush();
	}
	public void putData(byte[] buffer,int ini,int fin) throws IOException{
		dos.write(buffer,ini,fin);
	}
	public void dataFlush() throws IOException{
		dos.flush();
	}
	public void Listening(String s) throws IOException {
		System.out.println("Espero "+s);
		String linea = this.getLine();
		System.out.println("List "+linea);
		while (!linea.equals(s)) {
			System.out.println(linea);
			linea = this.getLine();
		}
	}

	@Override
	public void close() throws IOException {
		if (s != null) {
			try {
				s.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	public Socket getSocket(){
		return this.s;
	}
}
