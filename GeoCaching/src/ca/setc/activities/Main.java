package ca.setc.activities;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import ca.setc.config.Preferences;
import ca.setc.geocaching.Compass;
import ca.setc.geocaching.R;
import ca.setc.logging.Analytics;
import ca.setc.logging.ConfigureLog4J;
import ca.setc.parse.User;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * The Class Main.
 */
public class Main extends Activity {

	/** The splash dialog. */
	private Dialog mSplashDialog;

	/** The log. */
	private final Logger log = LoggerFactory.getLogger(Main.class);

	/** The Constant SPASH_DURATION. */
	private static final int SPASH_DURATION = 3000;

	/** The Constant PASSWORD. */
	private static final String PASSWORD = "Password";

	/** The spinner progress. */
	private ProgressDialog mSpinner;

	/** The original UncountExceptionHandler. */
	private UncaughtExceptionHandler originalUEH;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ConfigureLog4J.configure();

		mSpinner = new ProgressDialog(this);
		mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mSpinner.setMessage(getString(R.string.loading));
		Preferences.setSharedPreferences(getSharedPreferences(
				"GeoCaching Preferences", MODE_PRIVATE));

		originalUEH = Thread.getDefaultUncaughtExceptionHandler();

		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java
			 * .lang.Thread, java.lang.Throwable)
			 */
			public void uncaughtException(Thread thread, Throwable ex) {
				log.error("Unhandled Exception", ex);
				Preferences.setBoolean("UncleanShutdown", true);
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				ex.printStackTrace(pw);
				Preferences.set("error-to-log", sw.toString());
				originalUEH.uncaughtException(thread, ex);
			}
		});

		Thread.currentThread().setUncaughtExceptionHandler(
				Thread.getDefaultUncaughtExceptionHandler());
		log.info("Starting up");
		log.info("{} : {}", "Phone MANUFACTURER", android.os.Build.MANUFACTURER);
		log.info("{} : {}", "Phone Model", android.os.Build.MODEL);
		log.info("{} : {}", "Android Version", android.os.Build.VERSION.RELEASE);

		log.debug("Initializing Parse");
		Parse.initialize(this, "zzPUlt8jvi3xtl6bMFSNe40xS8ieh6h2gBquFbD3",
				"JqpTHaTBY2im5qxyHAOT0EYgwEFTcSyY1aWvlnaj");
		log.debug("Parse Initialized");
		
		Compass.getInstance().setSensorManager((SensorManager)getSystemService(Context.SENSOR_SERVICE));
		
		showSplashScreen();
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
	 * Removes the splash screen.
	 */
	protected void removeSplashScreen() {
		log.debug("removing splashscreen");
		if (mSplashDialog != null) {
			mSplashDialog.dismiss();
			mSplashDialog = null;
		}
	}

	/**
	 * Show map screen.
	 */
	protected void showMapScreen() {
		Intent intent = new Intent(this, Map.class);
		startActivity(intent);
	}

	/**
	 * Show login.
	 */
	protected void showLogin() {
		setContentView(R.layout.login);

		if (Preferences.getBoolean("UncleanShutdown", false)) {
			Preferences.setBoolean("UncleanShutdown", false);
			Intent intent = new Intent(this, UnhandledExceptionActivity.class);
			startActivity(intent);
		}

		if (Preferences.getBoolean("Remember", false)) {
			((CheckBox) findViewById(R.id.cb_remember)).setChecked(true);
			String username = Preferences.get("Username");
			if (username != null) {
				EditText name = (EditText) findViewById(R.id.et_username);
				name.setText(username);
			}

			String password = Preferences.get(PASSWORD);
			if (password != null) {
				EditText name = (EditText) findViewById(R.id.et_password);
				name.setText(password);
			}
		}
	}

	/**
	 * Show splash screen.
	 */
	protected void showSplashScreen() {
		log.debug("Showing splashscreen");
		mSplashDialog = new Dialog(this, R.layout.splashscreen);
		mSplashDialog.setContentView(R.layout.splashscreen);
		mSplashDialog.setCancelable(false);
		mSplashDialog.show();

		// Set Runnable to remove splash screen just in case
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Runnable#run()
			 */
			public void run() {
				removeSplashScreen();
				showLogin();
			}
		}, SPASH_DURATION);
	}

	/**
	 * button click handler
	 * 
	 * @param v
	 *            the v
	 */
	public void onClick(View v) {

		boolean remember = ((CheckBox) findViewById(R.id.cb_remember))
				.isChecked();
		Preferences.setBoolean("Remember", remember);
		log.debug("Button Clicked. Id: {}", v.getId());

		String name = ((EditText) findViewById(R.id.et_username)).getText()
				.toString();
		String password = ((EditText) findViewById(R.id.et_password)).getText()
				.toString();
		String email = ((EditText) findViewById(R.id.et_email)).getText()
				.toString();

		if (remember) {
			Preferences.set("Username", name);
			if (remember) {
				Preferences.set(PASSWORD, password);
			} else {
				Preferences.set(PASSWORD, null);
			}
		}

		if (v.getId() == R.id.btn_login) {
			login(name, password);
		} else if (v.getId() == R.id.btn_signup) {
			signup(name, password, email);
		}
	}

	/**
	 * Login a user
	 */
	private void login(final String username, String password) {

		try {

			log.debug("Attempting to login as {}", username);

			mSpinner.show();
			ParseUser.logInInBackground(username, password,
					new LogInCallback() {

						/*
						 * (non-Javadoc)
						 * 
						 * @see
						 * com.parse.LogInCallback#done(com.parse.ParseUser,
						 * com.parse.ParseException)
						 */
						@Override
						public void done(ParseUser user, ParseException e) {
							if (user != null) {
								Analytics.send("login");
								log.debug("Logged in as {} with id {}",
										username, user.getObjectId());
								Preferences.setCurrentUser(new User(user));
								showMapScreen();
								mSpinner.dismiss();
								finish();
							} else {
								log.error("Log attempt failed", e);
								Toast.makeText(Main.this, R.string.login_error,
										Toast.LENGTH_SHORT).show();

								mSpinner.dismiss();
							}
						}
					});
		} catch (Exception e) {
			log.error("Log attempt failed", e);
			Toast.makeText(Main.this, e.getMessage(), Toast.LENGTH_SHORT)
					.show();

			mSpinner.dismiss();
		}
	}

	/**
	 * Signup a new user
	 */
	private void signup(String username, String password, String email) {
		log.debug("Attempting to signup as {} with email {}", username, email);
		try {
			final ParseUser pUser = new ParseUser();
			pUser.setUsername(username);
			pUser.setPassword(password);
			pUser.setEmail(email);
			mSpinner.show();
			pUser.signUpInBackground(new SignUpCallback() {

				/*
				 * (non-Javadoc)
				 * 
				 * @see com.parse.SignUpCallback#done(com.parse.ParseException)
				 */
				@Override
				public void done(ParseException e) {
					if (e == null) {
						log.error("Signup attempt succeeded. Id: {}",
								pUser.getObjectId());
						showMapScreen();
						finish();
					} else {
						mSpinner.dismiss();
						log.error("Signup attempt failed", e);
						Toast.makeText(null, R.string.login_error,
								Toast.LENGTH_SHORT).show();
					}
				}
			});
		} catch (Exception e) {
			log.error("Log attempt failed", e);
			Toast.makeText(Main.this, e.getMessage(), Toast.LENGTH_SHORT)
					.show();

			mSpinner.dismiss();
		}
	}
}
