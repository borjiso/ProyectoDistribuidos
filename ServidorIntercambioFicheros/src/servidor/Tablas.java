package servidor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import utils.Conexion;
import utils.Fichero;
import utils.Usuario;

public class Tablas {
	private static Hashtable<String, Fichero> nombres;
	private static Hashtable<String, Vector<Usuario>> ficheros;
	private static Hashtable<Usuario, ArrayList<Fichero>> usuarios;
	
	static{
		nombres = new Hashtable<String, Fichero>();
		System.out.println("Primero: "+nombres.toString());
		ficheros = new Hashtable<String, Vector<Usuario>>();
		System.out.println(ficheros.toString());
		usuarios = new Hashtable<Usuario,ArrayList<Fichero>>();
		System.out.println(usuarios.toString());
	}
	public static void AddUsuario(Usuario u, ArrayList<Fichero> f) {
		usuarios.put(u, f);
		for(Fichero fi: f){
			nombres.put(fi.getNombre(), fi);
			AddFichero(fi.getHash(),u);
			System.out.println(f.toString());
		}
		System.out.println("nombres"+nombres.toString());
	}

	public static void AddFichero(String file, Usuario u) {
		if(ficheros.get(file)==null){
			ficheros.put(file, new Vector<Usuario>());
		}
		Vector<Usuario> aux =ficheros.get(file);
		aux.add(u);
		ficheros.put(file, aux);
		System.out.println("ficheros"+ficheros.toString());
	}

	public static void RemoveUsuario(Usuario u) {
		for (Fichero f : usuarios.get(u)) {
			ficheros.remove(f.getHash());
		}
		usuarios.remove(u);
	}

	public static void enviarTablaFicheros(Conexion con) throws IOException {
		con.putObject(ficheros);
	}
	public static String getHash(String nombre){
		return nombres.get(nombre).getHash();
	}

	public static void enviarTablaNombres(Conexion con) throws IOException {
		con.putObject(nombres);
		
	}
}
