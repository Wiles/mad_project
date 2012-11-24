package ca.setc.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import ca.setc.geocaching.R;

import com.parse.ParseObject;

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
    		
    		if(msg.length() == 0)
    		{
				Toast.makeText(this, getString(R.string.no_message),
						Toast.LENGTH_SHORT).show();
				return;
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
