package ca.setc.geocaching;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;

public class GeoCaching extends Activity {

	protected Dialog mSplashDialog;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_caching);
        showSplashScreen();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_geo_caching, menu);
        return true;
    }
    
    protected void removeSplashScreen() {
        if (mSplashDialog != null) {
            mSplashDialog.dismiss();
            mSplashDialog = null;
        }
    }
    
    protected void showSplashScreen() {
        mSplashDialog = new Dialog(this);
        mSplashDialog.setContentView(R.layout.splashscreen);
        mSplashDialog.setCancelable(false);
        mSplashDialog.show();
         
        // Set Runnable to remove splash screen just in case
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
          public void run() {
            removeSplashScreen();
          }
        }, 3000);
    }
}
