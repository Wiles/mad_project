package com.rtbsoft;

import com.rtbsoft.db.Con_Param;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.PowerManager;

public class GPSHandler extends HandlerThread {
	private PowerManager.WakeLock wl;
	private int mode;
	private Context context;
	private Handler hnd;
	private LocationManager lm;
	
	private LocationListener ll = new LocationListener() {    
		@Override
		public void onLocationChanged(Location l) {
	    	try{
	    		if (wl.isHeld()){
		    		WebService ws = new WebService(context);
		    		ws.doGPS(l, (mode & Con_Param.GPSMODE_QUEUEUPDATESFORSENDING)== Con_Param.GPSMODE_QUEUEUPDATESFORSENDING);
		    		hnd.removeCallbacks(r);
	    			wl.release();
	    		}
	    		quit();
	    	}catch(Exception e){
		        CrashHandler.getInstance().addException(context, "GPSTimer: onLocationChanged: Exception", e);
	    	}
			
			lm.removeUpdates(ll);
		}    
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}    
		@Override
		public void onProviderEnabled(String provider) {}    
		@Override
		public void onProviderDisabled(String provider) {}
	};

	private Runnable r = new Runnable(){
		@Override
		public void run() {
			if (wl.isHeld())
				wl.release();
			lm.removeUpdates(ll);
			quit();
		}
	};
	
	public GPSHandler(Context _context, String name, PowerManager.WakeLock _wl, int _mode) {
		super(name);
		this.context = _context;
		this.wl = _wl;
		this.mode = _mode;
		
		hnd = new Handler();
		
		if ((mode & Con_Param.GPSMODE_SENDATINTERVAL) == Con_Param.GPSMODE_SENDATINTERVAL){
			lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	    	lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, ll);
	    	hnd.postDelayed(r, 30000);
		}else
			quit();
	}
	
	@Override
	public void run(){
		super.run();
	}
}
