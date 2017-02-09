//Nombre: Borja Jimeno Soto
//DNI: 18074126J
package cliente;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import utils.Conexion;
import utils.Fichero;
import utils.HashSHA256;
import utils.Usuario;

public class Cliente {
	Usuario u = null;
	File directorio;
	Conexion con;
	private Hashtable<String, Vector<Usuario>> ficheros;
	private Hashtable<String, Fichero> nombres;
	private Hashtable<String, String> hash;

	public Cliente(Usuario u, File d, Conexion con) {
		this.u = u;
		directorio = d;
		this.con = con;
		ficheros = new Hashtable<String, Vector<Usuario>>();
		hash=new Hashtable<String,String>();
	}

	public ArrayList<Fichero> getFicehros() {
		ArrayList<Fichero> aux = new ArrayList<Fichero>();
		for (Enumeration<String> e = ficheros.keys(); e.hasMoreElements();) {
			// aux.add(ficheros.get(e.nextElement()));
		}
		return aux;
	}

	private ArrayList<Fichero> getFicherosDirectorio() {
		ArrayList<Fichero> aux = new ArrayList<Fichero>();
		Fichero fi;
		for (File f : directorio.listFiles()) {
			fi = new Fichero(HashSHA256.getHash(f), f.getName());
			aux.add(fi);
			hash.put(fi.getHash(), fi.getNombre());
			
			System.out.println("Fichero: " + f.toString());
		}
System.out.println("hashhashhahshahsahhashahshashahsah "+hash.toString());
		return aux;
	}

	private String getUrl(String f, Usuario u) {
		String url = "http://" + u.getIp() + ":" + u.getPuerto() + "/" + f;
		System.out.println(url);
		return url;
	}

	public void HandShake() throws IOException {
		System.out.println("Handshake");
		con.Write("HELLO");
		System.out.println("Listening");
		con.Listening("HELLO");
		con.Write("LOGUSER");
		con.Listening("OK");
		con.putObject(u);
		con.Listening("OK");
		con.putObject(getFicherosDirectorio());
	}

	public void Quit() throws IOException {
		System.out.println("Quit");
		con.Write("QUIT");
		// con.close();
	}

	public void Logout() throws IOException {
		System.out.println("Logout");
		con.Write("LOGOUT");
		con.Listening("OK");
		con.Write("QUIT");
		// con.close();
	}

	public void getFiles() throws IOException, ClassNotFoundException {
		System.out.println("getfiles");
		con.Write("GUETFILES");
		con.Listening("OK");
		con.Write("READY");
		Hashtable<String, Vector<Usuario>> aux = (Hashtable<String, Vector<Usuario>>) con.getObject();
		System.out.println(".................. " + aux.toString());
		con.Write("OK");
		Hashtable<String, Fichero> aux1 = (Hashtable<String, Fichero>) con.getObject();
		con.Write("OK");
		nombres = aux1;
		for (Enumeration<String> e = aux.keys(); e.hasMoreElements();) {
			for (Usuario u : aux.get(e.nextElement())) {
				System.out.println(u.toString());
			}
		}
		this.addFicheros(aux);

	}

	public String getFile(String nombre) throws IOException {
		con.Write("GUETFILE");
		con.Listening("OK");
		con.Write(nombre);
		return con.getLine();

	}

	private void addFicheros(Hashtable<String, Vector<Usuario>> fi) {
		Vector<Usuario> aux;
		String key;
		for (Enumeration<String> e = fi.keys(); e.hasMoreElements();) {
			key = e.nextElement();
			aux = fi.get(key);
			if (ficheros.get(key) == null) {
				ficheros.put(key, new Vector<Usuario>(aux));
			}
		}
	}

	public void close() {
		try {
			con.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void Descargar(String fichero) {
		if (fichero != null) {
			DescargadorConcurrente dc = new DescargadorConcurrente(directorio.getPath());
			System.out.println(ficheros == null);
			for (Usuario u : ficheros.get(fichero)) {
				System.out.println(u == null);
				try {
					dc.Descargar(getUrl(fichero, u));
					break;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			System.out.println("No se encuentre el fichero");
		}

	}

	public void Actualizar() {

	}

	public void listarFicheros() {
		for (Enumeration<String> e = nombres.keys(); e.hasMoreElements();) {
			System.out.println(e.nextElement());
		}

	}

	public String getFileFromHash(String s) {
		return hash.get(s);
	}
}
