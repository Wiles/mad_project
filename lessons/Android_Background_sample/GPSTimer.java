package com.rtbsoft;

import com.rtbsoft.db.Con_Param;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public final class GPSTimer extends BroadcastReceiver {
	
	static PowerManager.WakeLock wl;
	
	static public void start(Activity activity){
		try{
			Con_Param cp = Settings.getInstance(activity).getConParam();
			Intent i = new Intent(activity, GPSTimer.class);
			i.putExtra("mode", cp.getUpdateMode());
			
			PendingIntent pi = PendingIntent.getBroadcast(activity, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
	
			AlarmManager am = (AlarmManager)activity.getSystemService(Activity.ALARM_SERVICE);
			am.cancel(pi);
			am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + cp.getUpdateIntv() * 60 * 1000, cp.getUpdateIntv() * 60 * 1000, pi);
	
			PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
			wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "GPSLock");
		}catch(Exception e){
			CrashHandler.getInstance().addException(activity, "GPSTimer: start: exception: ", e);
		}
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		wl.acquire();
		GPSHandler gh = new GPSHandler(context, "GPSHandler", wl, intent.getExtras().getInt("mode"));
		gh.start();
	}
}