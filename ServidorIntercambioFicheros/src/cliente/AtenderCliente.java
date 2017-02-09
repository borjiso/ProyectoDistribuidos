//Nombre: Borja Jimeno Soto
//DNI: 18074126J
package cliente;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.net.URLConnection;
import java.util.Date;

import utils.Conexion;

public class AtenderCliente implements Runnable {
	Conexion con;
	String HOMEDIR;
	Cliente c;

	public AtenderCliente(Conexion con, String HOMEDIR, Cliente c) {
		super();
		this.con = con;
		this.HOMEDIR = HOMEDIR;
		this.c = c;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Nuevo cliente");
		try {
			String linea = con.getLine();
			if (linea != null) {
				File fichero = buscaFichero(linea);
				System.out.println(linea);
				if (!fichero.exists()) {
					System.out.println("no existe");
					con.Write("NOEXISTE");
					System.out.println("enviado error");
				} else {
					System.out.println("file:/" + fichero.getPath());
					if (linea.startsWith("GET")) {
						while (!linea.contains("Range") && linea.trim().length() != 0) {
							linea = con.getLine();
						}
						System.out.println("fuera: " + linea);
						System.out.println("1" + (linea == null) + "2" + (linea.trim() == ""));
						if (linea == null || linea.trim() == "") {
							System.out.println("a200");
							System.out.println("a200");
							sendMIMEHeading(con.getSocket().getOutputStream(), 200,
									URLConnection.guessContentTypeFromName(fichero.getName()), fichero.length(), 0, 0);

							enviarFichero(fichero);
						} else {
							System.out.println("206");
							String tratada = linea.substring(linea.indexOf("=") + 1, linea.length());
							int inicio = Integer.parseInt(tratada.substring(0, tratada.indexOf("-")));
							int fin = Integer.parseInt(tratada.substring(tratada.indexOf("-") + 1));
							sendMIMEHeading(con.getSocket().getOutputStream(), 206,
									URLConnection.guessContentTypeFromName(fichero.getName()), fichero.length(), inicio,
									fin);
							enviarParcial(fichero, inicio, fin);
						}

					} else if (linea.startsWith("HEAD")) {
						sendMIMEHeading(con.getSocket().getOutputStream(), 200,
								URLConnection.guessContentTypeFromName(fichero.getName()), fichero.length(), 0, 0);

					} else {
						con.Write("MAL");
					}
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendMIMEHeading(OutputStream os, int code, String cType, long fSize, long ini, long fin) {
		PrintStream dos = new PrintStream(os);
		dos.print("HTTP/1.1 " + code + " ");
		System.out.println(code);
		if (code == 200) {
			dos.print("OK\r\n");
			dos.print("Date: " + new Date() + "\r\n");
			dos.print("Server: Cutre http Server ver. -3.0\r\n");
			dos.print("Connection: close\r\n");
			dos.print("Content-length: " + fSize + "\r\n");
			dos.print("Content-type: " + cType + "\r\n");
			dos.print("\r\n");
		} else if (code == 404) {
			dos.print("File Not Found\r\n");
			dos.print("Date: " + new Date() + "\r\n");
			dos.print("Server: Cutre http Server ver. -3.0\r\n");
			dos.print("Connection: close\r\n");
			dos.print("Content-length: " + fSize + "\r\n");
			dos.print("Content-type: " + "text/html" + "\r\n");
			dos.print("\r\n");
		} else if (code == 501) {
			dos.print("Not Implemented\r\n");
			dos.print("Date: " + new Date() + "\r\n");
			dos.print("Server: Cutre http Server ver. -3.0\r\n");
			dos.print("Connection: close\r\n");
			dos.print("Content-length: " + fSize + "\r\n");
			dos.print("Content-type: " + "text/html" + "\r\n");
			dos.print("\r\n");
		} else if (code == 206) {
			dos.print("OK\r\n");
			dos.print("Date: " + new Date() + "\r\n");
			dos.print("Server: Cutre http Server ver. -3.0\r\n");
			dos.print("Connection: close\r\n");
			dos.print("Content-Range: bytes" + ini + "-" + fin + "/" + fSize + "\r\n");
			dos.print("Content-length: " + (fin - ini + 1) + "\r\n");
			dos.print("Content-type: " + cType + "\r\n");
			dos.print("\r\n");
		}
		dos.flush();
	}

	private File buscaFichero(String m) {
		String fileName = "";
		if (m.startsWith("GET ")) {
			// A partir de una cadena de mensaje (m) correcta (comienza por GET)
			fileName = m.substring(5, m.indexOf(" ", 5));
		}
		if (m.startsWith("HEAD ")) {
			// A partir de una cadena de mensaje (m) correcta (comienza por
			// HEAD)
			fileName = m.substring(6, m.indexOf(" ", 7));
		}
		System.out.println("sjashglsfhglskghlidfghdfligdfigul " + fileName);
		fileName = c.getFileFromHash(fileName);
		System.out.println(fileName);
		return new File(HOMEDIR, fileName);
	}

	private void enviarParcial(File f, int inicio, int fin) {

		try (RandomAccessFile raf = new RandomAccessFile(f, "r")) {
			raf.seek(inicio);
			byte[] buffer = new byte[fin - inicio + 1];
			raf.readFully(buffer);
			con.putData(buffer);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	private void enviarFichero(File f) {
		try (DataInputStream fis = new DataInputStream(new FileInputStream(f))) {
			int SIZE = 1024 * 1;
			byte[] buffer = new byte[SIZE];
			int leidos = fis.read(buffer);
			while (leidos != -1) {
				con.putData(buffer, 0, leidos);
				leidos = fis.read(buffer);
			}
			con.dataFlush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
