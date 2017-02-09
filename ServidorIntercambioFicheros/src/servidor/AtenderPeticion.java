//Nombre: Borja Jimeno Soto
//DNI: 18074126J
package servidor;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import utils.BadHandShakeException;
import utils.Conexion;
import utils.Fichero;
import utils.Usuario;

public class AtenderPeticion implements Runnable {
	Usuario u = null;
	Conexion con;

	public AtenderPeticion(Socket s) {
		con = new Conexion(s);
		System.out.println("Cliente: " + s.getInetAddress() + ":" + s.getPort());
	}

	private void logUser() {
		try {

			u = (Usuario) con.getObject();
			System.out.println(u);
			con.Write("OK");
			ArrayList<Fichero> ficheros = (ArrayList<Fichero>) con.getObject();
			System.out.println(ficheros);
			Tablas.AddUsuario(u, ficheros);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				con.Write("ERRORLOGUSER");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void handShake() throws BadHandShakeException {
		String linea;
		try {
			con.Write("HELLO");
			linea = con.getLine();
			System.out.println("******Recibo: " + linea);
			if (linea.equals("LOGUSER")) {
				con.Write("OK");
				logUser();
			} else {
				// throw new BadHandShakeException("Bad client: " +
				// s.getInetAddress() + ":" + s.getPort());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			String linea = con.getLine();
			System.out.println(linea);
			while (!linea.equals("QUIT")) {
				System.out.println(linea);
				if (linea.equals("HELLO")) {
					try {
						handShake();
					} catch (BadHandShakeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						try {
							con.Write("BADHANDSHAKE");
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

				} else if (linea.equals("GUETFILES")) {
					con.Write("OK");
					try {
						con.Listening("READY");
						Tablas.enviarTablaFicheros(con);
						con.Listening("OK");
						Tablas.enviarTablaNombres(con);
						con.Listening("OK");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						con.Write("ERROR");
					}
				} else if(linea.equals("GUETFILE")){
					con.Write("OK");
					con.Write(Tablas.getHash(con.getLine()));
				}
				else if (linea.equals("LOGOUT")) {
					con.Write("OK");
					Tablas.RemoveUsuario(u);
				} else {
					con.Write("ERRORBADREQUEST");
				}
				linea = con.getLine();
			}
			System.out.println("CLIENTE FUERA");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
