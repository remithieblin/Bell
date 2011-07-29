package com.actimust;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

public class AnalyseurMouvement {
	
	private float x, y, z;
    private float last_x, last_y, last_z;
    private static final int HARD_SHAKE = 550;
    private static final int SOFT_SHAKE = 150;
    private static final int INTERMEDIAIRE_SHAKE = 350;
    private long lastUpdate = -1;

    private final static int MVT_RAPIDE=1;
    private final static int MVT_MOYEN=2;
    private final static int MVT_FAIBLE=3;
    
	public int analyserMouvement(SensorEvent event){
		
		switch (event.sensor.getType()){
        	case Sensor.TYPE_ACCELEROMETER:
        		long curTime = System.currentTimeMillis();
        		// only allow one update every 100ms.
        		if ((curTime - lastUpdate) > 100) {
        			long diffTime = (curTime - lastUpdate);
        			lastUpdate = curTime;
		 
        			x = event.values[0];
        			y = event.values[1];
        			z = event.values[2];
		 
					float speed = Math.abs(x+y+z - last_x - last_y - last_z)
			                              / diffTime * 10000;
					last_x = x;
					last_y = y;
					last_z = z;
					
					if (speed < INTERMEDIAIRE_SHAKE && speed > SOFT_SHAKE) {
						return MVT_FAIBLE;
					}else if(speed < HARD_SHAKE && speed > INTERMEDIAIRE_SHAKE){
						return MVT_MOYEN;
					}else if(speed > HARD_SHAKE){
						return MVT_RAPIDE;
					}
			    }
	        break;
		}
		return 0;
	}

}
