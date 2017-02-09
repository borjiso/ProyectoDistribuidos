package utils;

import java.io.Serializable;

public class Usuario implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7300983872727218571L;
	private int puerto;
	private String ip;

	public Usuario(int port, String ip) {
		this.ip = ip;
		this.puerto = port;
	}

	public int getPuerto() {
		return puerto;
	}

	public String getIp() {
		return ip;
	}
	@Override
	public String toString(){
		return ip+":"+puerto;
	}
}
