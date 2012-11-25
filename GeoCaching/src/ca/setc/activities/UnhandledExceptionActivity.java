package ca.setc.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import ca.setc.config.Preferences;
import ca.setc.geocaching.R;

import com.parse.ParseObject;

public class UnhandledExceptionActivity extends Activity {

	private String errorMessage;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unhandled_exception);
        TextView view = (TextView)findViewById(R.id.tv_error);
        errorMessage = Preferences.get("error-to-log");
        view.setText(errorMessage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
    
    public void onClick(View v)
    {
    	if(v.getId() == R.id.btn_yes)
    	{
			Toast.makeText(this, getString(R.string.thank_you), Toast.LENGTH_SHORT).show();
    		try {		
	    		ParseObject object = new ParseObject("UnhandledError");
	    		object.put("stacktrace", errorMessage);
				object.save();
			} catch (Exception ignore) {
				//ignore
			}
        	finish();
    	}
    	else
    	{
        	finish();
    	}
    }
}
