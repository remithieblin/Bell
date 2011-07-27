package com.actimust;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

public class AnalyseurMouvement {
	
	private float x, y, z, azimuth, pitch, roll;
    private float last_x, last_y, last_z, last_azimuth, last_pitch, last_roll;
    private static final int HARD_SHAKE = 550;
    private static final int SOFT_SHAKE = 250;
    private long lastUpdate = -1;

    private final static int MVT_RAPIDE=1;
    private final static int MVT_MOYEN=2;
    private final static int MVT_FAIBLE=3;
    private final static int MVT_ROTATION_DROITE=4;
    private final static int MVT_ROTATION_GAUCHE=5;
    private final static int MVT_RETOUR_ROTATION_DROITE=6;
    private final static int MVT_RETOUR_ROTATION_GAUCHE=7;
    
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
					
					if (speed < HARD_SHAKE && speed > SOFT_SHAKE) {
						return MVT_FAIBLE;
					}else if(speed > HARD_SHAKE){
						return MVT_RAPIDE;
					}
			    }
	        break;
	        
        	case Sensor.TYPE_ORIENTATION:
				
        		azimuth = event.values[0];
        		pitch = event.values[1];
        		roll = event.values[2];
        		
        		if(0 < (azimuth - last_azimuth) && (azimuth - last_azimuth)< 90){
        			return MVT_ROTATION_DROITE;
        		}
        		
        		last_azimuth = azimuth;
        		last_pitch = pitch;
        		last_roll = roll;
        		
        break;

    }
		
		return 0;
		
	}

}
