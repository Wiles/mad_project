package ca.setc.geocaching;

import java.text.DecimalFormat;

import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ca.setc.geocaching.events.LocationChangedEvent;
import ca.setc.geocaching.events.LocationChangedListener;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.ParseException;
import com.parse.SignUpCallback;

public class GeoCaching extends MapActivity implements LocationChangedListener  {

	private ParseUser user;
	protected Dialog mSplashDialog;
	protected MapController mc;
	GPS gps = GPS.getInstance();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Parse.initialize(this, "zzPUlt8jvi3xtl6bMFSNe40xS8ieh6h2gBquFbD3", "JqpTHaTBY2im5qxyHAOT0EYgwEFTcSyY1aWvlnaj");

        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();

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
    
    protected void showMapScreen()
    {
    	Location destination = new Location("");
        destination.setLatitude(0.0);
        destination.setLongitude(0.0);
        setContentView(R.layout.activity_geo_caching);
        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mc = mapView.getController();
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        gps.AddLocationChangedListener(this);
        gps.setDestination(destination);
        gps.setLocationManager(lm);

        TextView destLat = (TextView)findViewById(R.id.destLat);
        TextView destLng = (TextView)findViewById(R.id.destLong);
		destLat.setText("0.0");
		destLng.setText("0.0");
    }
    
    protected void showLogin()
    {
    	setContentView(R.layout.login);
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
            showLogin();
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
		
		TextView curLat = (TextView)findViewById(R.id.currentLat);
		TextView curLng = (TextView)findViewById(R.id.currentLong);
		
		curLat.setText(event.getLatitude().toString());
		curLng.setText(event.getLongitude().toString());
		
		TextView distance = (TextView)findViewById(R.id.distance);
		
		double metres = gps.getDistance(event.getLocation());
		String bearing = GPS.bearingToString(gps.getBearing(event.getLocation()));
		String distanceText;
		if(metres >= 1000)
		{
			distanceText = new DecimalFormat("#.#").format(metres/1000) + " km " + bearing;
		} 
		else 
		{
			distanceText = new DecimalFormat("#.#").format(metres) + " m " + bearing;
		}
		
		distance.setText(distanceText);
	}

	public void onClick(View v) {
		
		EditText name = (EditText)findViewById(R.id.et_username);
		EditText password = (EditText)findViewById(R.id.et_password);
		EditText password2 = (EditText)findViewById(R.id.et_password2);
		EditText email = (EditText)findViewById(R.id.et_email);
		if(v.getId() == R.id.btn_login)
		{
			ParseUser.logInInBackground(name.getText().toString(), password.getText().toString(), new LogInCallback() {
				  public void done(ParseUser user, ParseException e) {
				    if (user != null) {
				      showMapScreen();
				    } else {
				      Toast.makeText(null, "Login failed", Toast.LENGTH_SHORT).show();
				    }
				  }
				});
		}
		else if (v.getId() == R.id.btn_signup)
		{
			user = new ParseUser();
			user.setUsername(name.getText().toString());
			user.setPassword(password.getText().toString());
			user.setEmail(email.getText().toString());
			user.signUpInBackground(new SignUpCallback() {
				@Override
				public void done(ParseException e) {
					if (e == null) {
					      showMapScreen();	
				    } else {
					      Toast.makeText(null, "Signup failed", Toast.LENGTH_SHORT).show();
				    }
					
				}
			});
		}
	}
}
