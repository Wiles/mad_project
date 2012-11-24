package ca.setc.activities;

import com.parse.ParseObject;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import ca.setc.config.Preferences;
import ca.setc.dialogs.Twitter_Dialog;
import ca.setc.geocaching.R;
import ca.setc.parse.GeoLocation;

public class SignLogBook extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_log_book);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
    

	public void onClick(View v)
    {
    	if(v.getId() == R.id.btn_sign)
    	{
    		EditText message = (EditText)findViewById(R.id.ed_message);
    		String msg = message.getText().toString(); 
    		GeoLocation loc = new GeoLocation(Map.destination.getParseGeoPoint("location"));
    		if(msg.length() == 0)
    		{
				Toast.makeText(this, "Please enter a message.",
						Toast.LENGTH_SHORT).show();
				return;
    		}
    		if(!Preferences.getBoolean("twitter_disabled", false))
    		{
        		new Twitter_Dialog(this,"http://twitter.com/?status="+Uri.encode(
        				String.format("I just visited a Geocache a (%1$,.2f,%1$,.2f)",loc.getLatitude(), loc.getLongitude()))).show();
    		}
    		ParseObject parse = new ParseObject("LogEntry");
    		parse.put("user", Main.user.toParseUser());
    		parse.put("destination", Map.destination);
    		parse.put("message", msg);
    		parse.saveInBackground();
    		finish();
    	}
    }
}
