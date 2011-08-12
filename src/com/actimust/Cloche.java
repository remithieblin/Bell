package com.actimust;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class Cloche extends Activity implements SensorEventListener{
    
	private SensorManager sensorMgr;
	private Sensor mAccelerometer;
    
    private ImageView cloche;
    private AnalyseurMouvement analyseurMvt;
    private MediaPlayer mediaPlayer;
    private MediaPlayer mediaPlayerMoyen;
    private MediaPlayer mediaPlayerSoft;
    
    private final static int MVT_RAPIDE=1;
    private final static int MVT_MOYEN=2;
    private final static int MVT_FAIBLE=3;
    
    private Vibrator vibrator;
	private long[] pattern_faible = {10,100};
	private long[] pattern_moyen = {10,100,100,100,100,100};
	private long[] pattern_fort = {10,100,100,100,100,100};
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        
        setContentView(R.layout.main);
        cloche = (ImageView) findViewById(R.id.cloche);
        cloche.setBackgroundResource(R.drawable.mvt_faible);
        
        analyseurMvt = new AnalyseurMouvement();
        mediaPlayer = MediaPlayer.create(this, R.raw.cloche_medium);
        mediaPlayerMoyen = MediaPlayer.create(this, R.raw.cloche_medium);
        mediaPlayerMoyen.setVolume(0.2f, 0.2f);
        mediaPlayerSoft = MediaPlayer.create(this, R.raw.touche);
        startMotionDetection();
        
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
      }

      public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
        	AnimationDrawable rocketAnimation = (AnimationDrawable) cloche.getBackground();
        	vibrator.vibrate(pattern_faible, -1);
        	rocketAnimation.start();
        	mediaPlayerSoft.start();
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
					mediaPlayer.start();
					vibrator.vibrate(pattern_fort, -1);
					animate(R.id.cloche, R.drawable.animation);
					break;
				case MVT_FAIBLE:
					animate(R.id.cloche, R.drawable.mvt_faible);break;
				case MVT_MOYEN:
					mediaPlayerMoyen.start();
					vibrator.vibrate(pattern_moyen, -1);
					animate(R.id.cloche, R.drawable.mvt_moyen);
					break;
			}
	    }
	}

	private void animate(int imageViewId, int animationId){
//		sensorMgr.unregisterListener(this);
		
		Runnable registerSensorRunnable = new Runnable(){
			@Override
			public void run() {
//				sensorMgr.registerListener(Cloche.this, mAccelerometer, SensorManager.SENSOR_ACCELEROMETER);
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
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
	      case R.id.autresApplisId:
	    	  showListApp();
	          return true;
	      case R.id.about:
	    	  showAbout();
	    	  return true;
	      default:
	        return super.onContextItemSelected(item);
      }
    }
    
    private void showListApp() {
    	final CharSequence[] items = {"Make Money", "Fingerprint Compatibility"};

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("Other Applications:");
    	builder.setItems(items, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	    	switch (item) {
				case 0:
					openAppDetailsPage("market://details?id=fr.first");
					break;
				case 1:
					openAppDetailsPage("market://details?id=com.actimust.seduction");
					break;
				default:
					break;
				}
    	    }
    	});
    	AlertDialog alert = builder.create();
    	alert.show();
		
	}
    
    private void openAppDetailsPage(String uri){
    	Intent intent = new Intent(Intent.ACTION_VIEW);
    	intent.setData(Uri.parse(uri));
    	startActivity(intent);
    }
    
    private void showAbout() {
    	Toast.makeText(getApplicationContext(), "By actimust.com", Toast.LENGTH_LONG).show();
		
	}
}