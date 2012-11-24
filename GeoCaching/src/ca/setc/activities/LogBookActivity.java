package ca.setc.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
					Toast.makeText(getApplicationContext(), "Could not retrieve log entries",
							Toast.LENGTH_SHORT).show();
					return;
				}
				loadEntries(list);
			}
        });
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_log_book, menu);
        return true;
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
					String username = "Unknown";
					try {
						ParseUser user = obj.getParseUser("user").fetch();
						username = user.getUsername();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					str[i] = String.format("%s %s - %s", obj.getCreatedAt().toLocaleString(), username, obj.getString("message"));
				}
				ArrayAdapter<String> adapter  = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, str);
				lv.setAdapter(adapter);
				lv.setOnItemClickListener(new OnItemClickListener() {

					public void onItemClick(AdapterView<?> adater, View view,
							int position, long id) {
						
						finish();
					}
				});
    }
}
