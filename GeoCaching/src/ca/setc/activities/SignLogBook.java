package ca.setc.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import ca.setc.config.Preferences;
import ca.setc.geocaching.GPS;
import ca.setc.geocaching.R;

import com.parse.ParseObject;

/**
 * Activity to sign the logbook
 */
public class SignLogBook extends Activity {

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_log_book);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

	/**
	 * Handles button clicks	
	 *
	 * @param v the v
	 */
	public void onClick(View v)
    {
    	if(v.getId() == R.id.btn_sign)
    	{
    		EditText message = (EditText)findViewById(R.id.ed_message);
    		String msg = message.getText().toString();
    		
    		if(msg.length() == 0)
    		{
				Toast.makeText(this, getString(R.string.no_message),
						Toast.LENGTH_SHORT).show();
				return;
    		}
    		
    		ParseObject parse = new ParseObject("LogEntry");
    		parse.put("user", Preferences.getCurrentUser().toParseUser());
    		parse.put("destination", GPS.getInstance().getDestination());
    		parse.put("message", msg);
    		parse.saveInBackground();
    		finish();
    	}
    }
}
