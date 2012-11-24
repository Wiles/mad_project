package ca.setc.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import ca.setc.geocaching.R;

public class LogBookActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_book);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_log_book, menu);
        return true;
    }
}
