package ca.setc.geocaching;

import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.widget.EditText;
import ca.setc.geocaching.events.LocationChangedEvent;
import ca.setc.geocaching.events.LocationChangedListener;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.parse.Parse;
import com.parse.ParseObject;

public class GeoCaching extends MapActivity implements LocationChangedListener {

	protected Dialog mSplashDialog;
	protected MapController mc;
	GPS gps = GPS.getInstance();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Parse.initialize(this, "zzPUlt8jvi3xtl6bMFSNe40xS8ieh6h2gBquFbD3", "JqpTHaTBY2im5qxyHAOT0EYgwEFTcSyY1aWvlnaj");

    	Location destination = new Location("");
        destination.setLatitude(0.0);
        destination.setLongitude(0.0);
        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();

        showSplashScreen();
        setContentView(R.layout.activity_geo_caching);
        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mc = mapView.getController();
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        gps.AddLocationChangedListener(this);
        gps.setDestination(destination);
        gps.setLocationManager(lm);

		EditText destLat = (EditText)findViewById(R.id.destLat);
		EditText destLng = (EditText)findViewById(R.id.destLong);
		destLat.setText("0.0");
		destLng.setText("0.0");
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
        mSplashDialog = new Dialog(this, R.layout.splashscreen);
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

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	public void LocationChanged(LocationChangedEvent event) {
		mc.setCenter(event.getGeoPoint());
		
		EditText curLat = (EditText)findViewById(R.id.currentLat);
		EditText curLng = (EditText)findViewById(R.id.currentLong);
		
		curLat.setText(event.getLatitude().toString());
		curLng.setText(event.getLongitude().toString());
		
		EditText distance = (EditText)findViewById(R.id.distance);
		
		distance.setText(gps.getDistance(event.getLocation()).toString());
	}
}
