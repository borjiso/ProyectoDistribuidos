package utils;

import java.io.Serializable;

public class Fichero implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2795523120474679453L;
	private String hash, nombre;

	public Fichero(String hash, String nombre) {
		this.hash = hash;
		this.nombre = nombre;
	}

	public String getNombre() {
		return this.nombre;
	}

	public String getHash() {
		return this.hash;
	}

}
