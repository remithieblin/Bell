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
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class Cloche extends Activity implements SensorEventListener{
    
	private SensorManager sensorMgr;
	private Sensor mAccelerometer;
    
    private ImageView cloche;
    private AnalyseurMouvement analyseurMvt;
    
    private final static int MVT_RAPIDE=1;
    private final static int MVT_MOYEN=2;
    private final static int MVT_FAIBLE=3;
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        
        setContentView(R.layout.main);
        cloche = (ImageView) findViewById(R.id.cloche);
        cloche.setBackgroundResource(R.drawable.animation);
        
        analyseurMvt = new AnalyseurMouvement();
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
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {
			
			switch(analyseurMvt.analyserMouvement(event)){
				case MVT_RAPIDE:
					animate(R.id.cloche, R.drawable.animation);break;
				case MVT_FAIBLE:
					animate(R.id.cloche, R.drawable.mvt_faible);break;
				case MVT_MOYEN:
					animate(R.id.cloche, R.drawable.mvt_moyen);break;
			}
	    }
	}

	private void animate(int imageViewId, int animationId){
		sensorMgr.unregisterListener(this);
		
		Runnable registerSensorRunnable = new Runnable(){
			@Override
			public void run() {
				sensorMgr.registerListener(Cloche.this, mAccelerometer, SensorManager.SENSOR_ACCELEROMETER);
			}
		};
		
		ImageView imgView = (ImageView)findViewById(imageViewId);
		imgView.setVisibility(ImageView.VISIBLE);
		imgView.setBackgroundResource(animationId);
		
		registerAnimation(imageViewId, registerSensorRunnable);
		
	}

	private void registerAnimation(int id, final Runnable cb){
		final ImageView imgView = (ImageView)findViewById(id);
		final CustomAnimationDrawable aniDrawable = new CustomAnimationDrawable((AnimationDrawable)imgView.getBackground());
		imgView.setBackgroundDrawable(aniDrawable);

		aniDrawable.setOnFinishCallback(cb);
		
		if(!aniDrawable.isRunning()){
			aniDrawable.start();
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