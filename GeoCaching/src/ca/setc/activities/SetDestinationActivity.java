package ca.setc.activities;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import ca.setc.geocaching.GPS;
import ca.setc.geocaching.R;
import ca.setc.parse.GeoLocation;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class SetDestinationActivity extends Activity {
	

	private final Logger log = LoggerFactory.getLogger(SetDestinationActivity.class);

	private List<ParseObject> locations = new ArrayList<ParseObject>(); 
	
	private SetDestinationActivity that = this;
	
	private static final int MAX_DESTINATION = 10;
	

	private ProgressDialog mSpinner;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_destination);
        

		mSpinner = ProgressDialog.show(this, "", getString(R.string.loading));
        
        loadNear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
    
    public void onListItemClick(ListView l, View v, int position, long id) {
        log.info("Destination selected: {}", position);
    }
    
    private void dones()
    {
    	finish();
    }
        
    private void loadNear()
    {
    	ParseGeoPoint userLocation = Main.user.getCurrentLocation().toParseGeoPoint();
    	ParseQuery query = new ParseQuery("Destination");
    	query.whereNear("location", userLocation);
    	query.setLimit(MAX_DESTINATION);
    	query.findInBackground(new FindCallback() {

			@Override
			public void done(List<ParseObject> arg0, ParseException arg1) {
				locations.clear();
				if(arg1 != null)
				{
					Toast.makeText(null, arg1.getMessage(),
							Toast.LENGTH_LONG).show();
				}
				
				ListView lv = (ListView)findViewById(R.id.lv_destinations);

				String[] str = new String[arg0.size()];
				for(int i = 0; i < arg0.size(); ++i)
				{
					ParseObject obj = arg0.get(i);
					locations.add(obj);
					GeoLocation dest = new GeoLocation((ParseGeoPoint)obj.get("location"));
					GeoLocation curr = Main.user.getCurrentLocation();
					str[i] = String.format("%s %s - %s", GPS.distanceToText(curr.getDistance(dest)), GPS.bearingToString(curr.getBearing(dest)), obj.get("description"));
				}
				ArrayAdapter<String> adapter  = new ArrayAdapter<String>(that, android.R.layout.simple_list_item_1, android.R.id.text1, str);
				lv.setAdapter(adapter);
				lv.setOnItemClickListener(new OnItemClickListener() {

					public void onItemClick(AdapterView<?> adater, View view,
							int position, long id) {
						GPS.getInstance().setDestination(new GeoLocation ((ParseGeoPoint)locations.get(position).get("location")));
						Map.destination = locations.get(position);
						dones();
					}
				});
		    	mSpinner.dismiss();
			}
    	});
    }
}
