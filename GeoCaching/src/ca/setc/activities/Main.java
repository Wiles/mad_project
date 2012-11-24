
package ca.setc.activities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import ca.setc.config.Preferences;
import ca.setc.geocaching.R;
import ca.setc.logging.Analytics;
import ca.setc.logging.ConfigureLog4J;
import ca.setc.parse.User;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class Main extends Activity  {

	public static User user;
	protected Dialog mSplashDialog;
	private final Logger log = LoggerFactory.getLogger(Main.class);
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Preferences.setSharedPreferences(getSharedPreferences("GeoCaching Preferences", MODE_PRIVATE));
		ConfigureLog4J.configure();
		
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

			public void uncaughtException(Thread thread, Throwable ex) {
				log.error("Unhandled Exception", ex);

				Intent intent = new Intent(getApplicationContext(), UnhandledExceptionActivity.class);
				intent.putExtra("exception", ex.getMessage());
				startActivity(intent);
		    }
		});

		Thread.currentThread().setUncaughtExceptionHandler(Thread.getDefaultUncaughtExceptionHandler());
		log.info("Starting up");
		log.info("{} : {}", "Phone MANUFACTURER", android.os.Build.MANUFACTURER);
		log.info("{} : {}", "Phone Model", android.os.Build.MODEL);
		log.info("{} : {}", "Android Version", android.os.Build.VERSION.RELEASE);
		
		log.debug("Initializing Parse");
		Parse.initialize(this, "zzPUlt8jvi3xtl6bMFSNe40xS8ieh6h2gBquFbD3", "JqpTHaTBY2im5qxyHAOT0EYgwEFTcSyY1aWvlnaj");
		log.debug("Parse Initialized");
		showSplashScreen();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	protected void removeSplashScreen() {
		log.debug("removing splashscreen");
		if (mSplashDialog != null) {
			mSplashDialog.dismiss();
			mSplashDialog = null;
		}
	}

	protected void showMapScreen() {
		Intent intent = new Intent(this, Map.class);
		startActivity(intent);
	}

	protected void showLogin() {
		setContentView(R.layout.login);
		
		if(Preferences.getBoolean("Remember", false))
		{
			((CheckBox)findViewById(R.id.cb_remember)).setChecked(true);
			String username = Preferences.get("Username");
			if(username != null)
			{
				EditText name = (EditText) findViewById(R.id.et_username);
				name.setText(username);
			}
			
			String password = Preferences.get("Password");
			if(password != null)
			{
				EditText name = (EditText) findViewById(R.id.et_password);
				name.setText(password);
			}
		}
	}

	protected void showSplashScreen() {
		log.debug("Showing splashscreen");
		mSplashDialog = new Dialog(this, R.layout.splashscreen);
		mSplashDialog.setContentView(R.layout.splashscreen);
		mSplashDialog.setCancelable(false);
		mSplashDialog.show();

		// Set Runnable to remove splash screen just in case
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				removeSplashScreen();
				showLogin();
			}
		}, 3000);
	}

	public void setUser(ParseUser user) {
		Main.user = new User(user);
	}

	public void onClick(View v) {

		boolean remember = ((CheckBox)findViewById(R.id.cb_remember)).isChecked();
		Preferences.setBoolean("Remember", remember);
		log.debug("Button Clicked. Id: {}", v.getId());
		if (v.getId() == R.id.btn_login) {
			Button login = (Button)findViewById(R.id.btn_login);
			Button signup = (Button)findViewById(R.id.btn_signup);
			
			login.setEnabled(false);
			signup.setEnabled(false);
				
			try {
				
				final EditText name = (EditText) findViewById(R.id.et_username);
				log.debug("Attempting to login as {}", name);
				EditText password = (EditText) findViewById(R.id.et_password);
				String username = name.getText().toString();
				String pass = password.getText().toString();
				Preferences.set("Username", username);
				if(remember)
				{
					Preferences.set("Password", pass);
				}
				else
				{
					Preferences.set("Password", null);
				}
				ParseUser.logInInBackground(username, pass, new LogInCallback() {
					public void done(ParseUser user, ParseException e) {
						if (user != null) {
							Analytics.send("login");
							user.increment("access_count");
							user.saveInBackground();
							log.debug("Logged in as {} with id {}", name.getText().toString(), user.getObjectId());
							setUser(user);
							finish();
							showMapScreen();
							finish();
						} else {
							log.error("Log attempt failed", e);
							Toast.makeText(null, R.string.login_error, Toast.LENGTH_SHORT).show();
						}
					}
				});
			} catch (Exception e) {
				log.error("Log attempt failed", e);
				login.setEnabled(true);
				signup.setEnabled(true);
			}
		} else if (v.getId() == R.id.btn_signup) {
			Button login = (Button)findViewById(R.id.btn_login);
			Button signup = (Button)findViewById(R.id.btn_signup);

			login.setEnabled(false);
			signup.setEnabled(false);
			EditText name = (EditText) findViewById(R.id.et_username);
			EditText password = (EditText) findViewById(R.id.et_password);
			EditText email = (EditText) findViewById(R.id.et_email);
			
			log.debug("Attempting to signup as {} with email {}", name.getText().toString(), email.getText().toString());
			
			final ParseUser pUser = new ParseUser();
			String username = name.getText().toString();
			String pass = password.getText().toString();
			Preferences.set("Username", username);
			if(remember)
			{
				Preferences.set("Password", pass);
			}
			else
			{

				Preferences.set("Password", null);
			}
			pUser.setUsername(username);
			pUser.setPassword(pass);
			pUser.setEmail(email.getText().toString());
			pUser.signUpInBackground(new SignUpCallback() {
				@Override
				public void done(ParseException e) {
					if (e == null) {
						pUser.increment("access_count");
						pUser.saveInBackground();
						log.error("Signup attempt succeeded. Id: {}", pUser.getObjectId());
						showMapScreen();
						finish();
					} else {
						log.error("Signup attempt failed", e);
						Toast.makeText(null, R.string.login_error,
								Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	}
}
