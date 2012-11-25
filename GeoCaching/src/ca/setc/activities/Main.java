package ca.setc.activities;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.Window;
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

		mSpinner = new ProgressDialog(this);
		mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mSpinner.setMessage(getString(R.string.loading));

		Preferences.setSharedPreferences(getSharedPreferences(
				"GeoCaching Preferences", MODE_PRIVATE));
		ConfigureLog4J.configure();

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
	 * On click.
	 * 
	 * @param v
	 *            the v
	 */
	public void onClick(View v) {

		boolean remember = ((CheckBox) findViewById(R.id.cb_remember))
				.isChecked();
		Preferences.setBoolean("Remember", remember);
		log.debug("Button Clicked. Id: {}", v.getId());
		if (v.getId() == R.id.btn_login) {
			Button login = (Button) findViewById(R.id.btn_login);
			Button signup = (Button) findViewById(R.id.btn_signup);

			login.setEnabled(false);
			signup.setEnabled(false);

			try {

				final EditText name = (EditText) findViewById(R.id.et_username);
				log.debug("Attempting to login as {}", name);
				EditText password = (EditText) findViewById(R.id.et_password);
				String username = name.getText().toString();
				String pass = password.getText().toString();
				Preferences.set("Username", username);
				if (remember) {
					Preferences.set(PASSWORD, pass);
				} else {
					Preferences.set(PASSWORD, null);
				}

				mSpinner.show();
				ParseUser.logInInBackground(username, pass,
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
									user.increment("access_count");
									user.saveInBackground();
									log.debug("Logged in as {} with id {}",
											name.getText().toString(),
											user.getObjectId());
									Preferences.setCurrentUser(new User(user));
									finish();
									showMapScreen();
									mSpinner.dismiss();
									finish();
								} else {
									log.error("Log attempt failed", e);
									Toast.makeText(null, R.string.login_error,
											Toast.LENGTH_SHORT).show();

									mSpinner.dismiss();
								}
							}
						});
			} catch (Exception e) {
				log.error("Log attempt failed", e);
				login.setEnabled(true);
				signup.setEnabled(true);
			}
		} else if (v.getId() == R.id.btn_signup) {
			Button login = (Button) findViewById(R.id.btn_login);
			Button signup = (Button) findViewById(R.id.btn_signup);

			login.setEnabled(false);
			signup.setEnabled(false);
			EditText name = (EditText) findViewById(R.id.et_username);
			EditText password = (EditText) findViewById(R.id.et_password);
			EditText email = (EditText) findViewById(R.id.et_email);

			log.debug("Attempting to signup as {} with email {}", name
					.getText().toString(), email.getText().toString());

			final ParseUser pUser = new ParseUser();
			String username = name.getText().toString();
			String pass = password.getText().toString();
			Preferences.set("Username", username);
			if (remember) {
				Preferences.set(PASSWORD, pass);
			} else {

				Preferences.set(PASSWORD, null);
			}
			pUser.setUsername(username);
			pUser.setPassword(pass);
			pUser.setEmail(email.getText().toString());
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
						pUser.increment("access_count");
						pUser.saveInBackground();
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
		}
	}
}
