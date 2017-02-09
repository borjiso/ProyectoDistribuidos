//Nombre: Borja Jimeno Soto
//DNI: 18074126J
package cliente;

import java.io.IOException;
import java.util.TimerTask;

public class Temporizador extends TimerTask{
	Cliente c;
	
	public Temporizador(Cliente c){
		this.c=c;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			c.HandShake();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
