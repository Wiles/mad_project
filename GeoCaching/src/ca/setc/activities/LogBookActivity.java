package ca.setc.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import ca.setc.geocaching.R;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class LogBookActivity extends Activity {

	private List<ParseObject> entries = new ArrayList<ParseObject>(); 
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_book);
        

        
        ParseQuery query = new ParseQuery("LogEntry");
        query.whereEqualTo("destination", Map.destination);
        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback() {

			@Override
			public void done(List<ParseObject> list, ParseException e) {
				if(e != null)
				{
					Toast.makeText(getApplicationContext(),getString(R.string.retrieve_entry_failure),
							Toast.LENGTH_SHORT).show();
					return;
				}
				loadEntries(list);
			}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
    
    private void loadEntries(List<ParseObject> entryList)
    {
				entries.clear();
				ListView lv = (ListView)findViewById(R.id.lv_log_entries);

				String[] str = new String[entryList.size()];
				for(int i = 0; i < entryList.size(); ++i)
				{
					ParseObject obj = entryList.get(i);
					entries.add(obj);
					String username = getString(R.string.unknown_user);
					try {
						ParseUser user = obj.getParseUser("user").fetch();
						username = user.getUsername();
					} catch (ParseException ignore) {
						//ignore
					}
					str[i] = String.format("%s %s - %s", obj.getCreatedAt().toLocaleString(), username, obj.getString("message"));
				}
				ArrayAdapter<String> adapter  = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, str);
				lv.setAdapter(adapter);
    }
}
