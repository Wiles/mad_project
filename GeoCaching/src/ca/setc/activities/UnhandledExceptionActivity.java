package ca.setc.activities;

import com.parse.ParseException;
import com.parse.ParseObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import ca.setc.geocaching.R;

public class UnhandledExceptionActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unhandled_exception);
        TextView view = (TextView)findViewById(R.id.tv_error);
        view.setText(getIntent().getStringExtra("exception"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
    
    public void onClick(View v)
    {
    	if(v.getId() == R.id.btn_yes)
    	{
    		ParseObject object = new ParseObject("UnhandledError");
    		object.put("user", Main.user.toParseUser());
    		object.put("stacktrace", getIntent().getStringExtra("exception"));
    		try {
				object.save();
			} catch (ParseException ignore) {
				//ignore
			}
    	}
    	finish();
    }
}
