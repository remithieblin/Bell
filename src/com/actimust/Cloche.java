package com.actimust;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;

public class Cloche extends Activity implements SensorEventListener{
    
//	AnimationDrawable rocketAnimation;
//	AnimationDrawable mvtRapideAnim;
//	AnimationDrawable ralentissmentAnim;
//	AnimationDrawable moyenAnim;
//	AnimationDrawable faibleAnim;
//	AnimationDrawable droiteForteAnim;
//	AnimationDrawable gaucheForteAnim;
	
	private SensorManager sensorMgr;
	private Sensor mAccelerometer;
    private long lastUpdate = -1;
    private float x, y, z, azimuth, pitch, roll;
    private float last_x, last_y, last_z, last_azimuth, last_pitch, last_roll;
    private static final int SHAKE_THRESHOLD = 450;
    
    private ImageView cloche;

	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        cloche = (ImageView) findViewById(R.id.cloche);
        cloche.setBackgroundResource(R.drawable.animation);
        
        
        startMotionDetection();
      }

      public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
        	AnimationDrawable rocketAnimation = (AnimationDrawable) cloche.getBackground();
        	rocketAnimation.start();
          return true;
        }
        return super.onTouchEvent(event);
      }

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {
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
	    				if (speed > SHAKE_THRESHOLD) {
	    				    // yes, this is a shake action! Do something about it!
	    					
	    					animate(R.id.cloche, R.drawable.animation);
//	    					desactivateSensorFor(300);
////	    			        cloche.post(new Runnable(){
//	    			        Cloche.this.runOnUiThread(new Runnable(){
//	    			        AnimationDrawable mvtRapideAnim = (AnimationDrawable) cloche.getBackground();
//
//								@Override
//								public void run() {
////									mvtRapideAnim.start();
//									animate(R.id.cloche, R.drawable.mvt_rapide);
//								}
//	    			        	
//	    			        });
//	    			        
////	    			        mvtRapideAnim.start();
//	    			        
////	    					AnimationDrawable mvtRapideAnim = AnimationUtils.loadAnimation(Cloche.this, R.drawable.mvt_rapide);
////	    			        cloche.startAnimation(mvtRapideAnim);
	    			        
	    			        
	    				}
	    				last_x = x;
	    				last_y = y;
	    				last_z = z;
	    				//commentaire
	    		    }
		        break;
		        
		        case Sensor.TYPE_ORIENTATION:
	    				
//		        		azimuth = event.values[0];
//		        		pitch = event.values[1];
//		        		roll = event.values[2];
//		        		
//		        		if(80 < (azimuth - last_azimuth) && (azimuth - last_azimuth)< 100){
//		        			cloche.setBackgroundResource(R.drawable.droite_forte);
//	    			        droiteForteAnim = (AnimationDrawable) cloche.getBackground();
//	    			        droiteForteAnim.start();
//		        		}
//		        		
//		        		last_azimuth = azimuth;
//		        		last_pitch = pitch;
//		        		last_roll = roll;
		        		
		        break;
	 
	        }
	    }
	}

	private void desactivateSensorFor(int tmp) {
		try {
			sensorMgr.unregisterListener(this);
			this.wait(tmp);
			sensorMgr.registerListener(this, mAccelerometer, SensorManager.SENSOR_ACCELEROMETER);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void animate(int imageView, int animation){
//		sensorMgr.unregisterListener(this);
//		desactivateSensorFor(300);
		ImageView imgView = (ImageView)findViewById(imageView);
		imgView.setVisibility(ImageView.VISIBLE);
		imgView.setBackgroundResource(animation);
		AnimationDrawable frameAnimation = (AnimationDrawable) imgView.getBackground();
		if (frameAnimation.isRunning()){
			frameAnimation.stop();
//			sensorMgr.registerListener(this, mAccelerometer, SensorManager.SENSOR_ACCELEROMETER);
		}
		else{
			frameAnimation.stop();
			frameAnimation.start();
		}
	}

	
	private void startMotionDetection() {
		// start motion detection
		sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccelerometer = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		boolean accelSupported = sensorMgr.registerListener(this, mAccelerometer, 
			SensorManager.SENSOR_ACCELEROMETER);
	 
		if (!accelSupported) {
		    // on accelerometer on this device
		    sensorMgr.unregisterListener(this);
		}
	}
	
	protected void onPause() {
		if (sensorMgr != null) {
		    sensorMgr.unregisterListener(this);
		    sensorMgr = null;
	        }
		super.onPause();
	}
	
	protected void onResume(){
		sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorMgr.registerListener(this, mAccelerometer, 
				SensorManager.SENSOR_ACCELEROMETER);
		super.onResume();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
}