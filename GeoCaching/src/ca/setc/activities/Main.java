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
import android.widget.EditText;
import android.widget.Toast;
import ca.setc.geocaching.R;
import ca.setc.logging.ConfigureLog4J;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class Main extends Activity {

	// TODO find better way to pass user around
	public static ParseUser user;
	protected Dialog mSplashDialog;
	private final Logger log = LoggerFactory.getLogger(Main.class);
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ConfigureLog4J.configure();
		/*
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			
			public void uncaughtException(Thread thread, Throwable ex) {
				log.error("Unhandled Exception", ex);	
			}
		} );
		*/
		log.info("{} : {}", "Phone MANUFACTURER", android.os.Build.MANUFACTURER);
		log.info("{} : {}", "Phone Model", android.os.Build.MODEL);
		log.info("{} : {}", "Android Version", android.os.Build.VERSION.RELEASE);
		
		Parse.initialize(this, "zzPUlt8jvi3xtl6bMFSNe40xS8ieh6h2gBquFbD3", "JqpTHaTBY2im5qxyHAOT0EYgwEFTcSyY1aWvlnaj");
		showSplashScreen();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_geo_caching, menu);
		return true;
	}

	protected void removeSplashScreen() {
		if (mSplashDialog != null) {
			mSplashDialog.dismiss();
			mSplashDialog = null;
		}
	}

	protected void showMapScreen() {
		Intent intent = new Intent(this, Map.class);
		try{
			startActivity(intent);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	protected void showLogin() {
		setContentView(R.layout.login);
	}

	protected void showSplashScreen() {
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
		Main.user = user;
	}

	public void onClick(View v) {

		if (v.getId() == R.id.btn_login) {
			Button login = (Button)findViewById(R.id.btn_login);
			Button signup = (Button)findViewById(R.id.btn_signup);

			login.setEnabled(false);
			signup.setEnabled(false);
			try {
				
				EditText name = (EditText) findViewById(R.id.et_username);
				EditText password = (EditText) findViewById(R.id.et_password);
				ParseUser.logInInBackground(name.getText().toString(), password
						.getText().toString(), new LogInCallback() {
					public void done(ParseUser user, ParseException e) {
						if (user != null) {
							setUser(user);
							showMapScreen();
						} else {
							Toast.makeText(null, R.string.login_error,
									Toast.LENGTH_SHORT).show();
						}
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
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
			user = new ParseUser();
			user.setUsername(name.getText().toString());
			user.setPassword(password.getText().toString());
			user.setEmail(email.getText().toString());
			user.signUpInBackground(new SignUpCallback() {
				@Override
				public void done(ParseException e) {
					if (e == null) {
						showMapScreen();
					} else {
						Toast.makeText(null, R.string.login_error,
								Toast.LENGTH_SHORT).show();

					}
				}
			});
		}
	}
}
