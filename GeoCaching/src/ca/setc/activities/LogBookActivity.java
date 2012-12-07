package ca.setc.activities;

import java.util.ArrayList;
import java.util.List;

import org.ocpsoft.pretty.time.PrettyTime;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import ca.setc.config.Preferences;
import ca.setc.geocaching.R;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * View the logbook
 */
public class LogBookActivity extends Activity {

	/** The entries. */
	private List<ParseObject> entries = new ArrayList<ParseObject>();

	/** The m spinner. */
	private ProgressDialog mSpinner;

	/** Date formatter for social media like formats */
	private PrettyTime prettyTime = new PrettyTime();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_book);

		mSpinner = ProgressDialog.show(this, "", getString(R.string.loading));

		ParseQuery query = new ParseQuery("LogEntry");
		query.whereEqualTo("destination", Preferences.getDestination());
		query.orderByDescending("createdAt");
		query.findInBackground(new FindCallback() {

			@Override
			public void done(List<ParseObject> list, ParseException e) {
				if (e != null) {
					Toast.makeText(getApplicationContext(),
							getString(R.string.retrieve_entry_failure),
							Toast.LENGTH_SHORT).show();
					return;
				}
				loadEntries(list);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	/**
	 * Load entries for viewing
	 * 
	 * @param entryList
	 *            the entry list
	 */
	private void loadEntries(List<ParseObject> entryList) {
		entries.clear();
		ListView lv = (ListView) findViewById(R.id.lv_log_entries);

		String[] str = new String[entryList.size()];
		for (int i = 0; i < entryList.size(); ++i) {
			ParseObject obj = entryList.get(i);
			entries.add(obj);
			String username = obj.getString("username");
			str[i] = String.format("%s %s%n---%n%s", username,
					prettyTime.format(obj.getCreatedAt()),
					obj.getString("message"));
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, str);
		lv.setAdapter(adapter);
		mSpinner.dismiss();
	}
}
