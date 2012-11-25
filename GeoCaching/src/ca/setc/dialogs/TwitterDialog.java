package ca.setc.dialogs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import ca.setc.geocaching.R;

/**
 * Dialog to tweet messages.
 * 
 * Code based on answer found on Stack-Overflow:
 * http://stackoverflow.com/questions/1782743/twitter-integration-on-android-app
 */
public class TwitterDialog extends Dialog {

	/** The log. */
	private final Logger log = LoggerFactory.getLogger(TwitterDialog.class);

	/** The Constant MAGIC. */
	private final static float MAGIC = 0.5f;
	
	/** The Constant BLUE. */
	static final int BLUE = 0xFF6D84B4;
	
	/** The Constant DIMENSIONS_DIFF_LANDSCAPE. */
	static final float[] DIMENSIONS_DIFF_LANDSCAPE = { 20, 60 };
	
	/** The Constant DIMENSIONS_DIFF_PORTRAIT. */
	static final float[] DIMENSIONS_DIFF_PORTRAIT = { 40, 60 };
	
	/** The Constant FILL. */
	static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT,
			ViewGroup.LayoutParams.FILL_PARENT);
	
	/** The Constant MARGIN. */
	static final int MARGIN = 4;
	
	/** The Constant PADDING. */
	static final int PADDING = 2;
	
	/** The Constant DISPLAY_STRING. */
	static final String DISPLAY_STRING = "touch";

	/** The  url. */
	private String mUrl;
	
	/** Them spinner. */
	private ProgressDialog mSpinner;
	
	/** The  web view. */
	private WebView mWebView;
	
	/** The  content. */
	private LinearLayout mContent;
	
	/** The title. */
	private TextView mTitle;

	/**
	 * Instantiates a new twitter dialog.
	 *
	 * @param context the context
	 * @param url the url
	 */
	public TwitterDialog(Context context, String url) {
		super(context);
		mUrl = url;
	}

	/* (non-Javadoc)
	 * @see android.app.Dialog#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSpinner = new ProgressDialog(getContext());
		mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mSpinner.setMessage("Loading…");

		mContent = new LinearLayout(getContext());
		mContent.setOrientation(LinearLayout.VERTICAL);
		setUpTitle();
		setUpWebView();
		Display display = getWindow().getWindowManager().getDefaultDisplay();
		final float scale = getContext().getResources().getDisplayMetrics().density;
		int orientation = getContext().getResources().getConfiguration().orientation;
		float[] dimensions = (orientation == Configuration.ORIENTATION_LANDSCAPE) ? DIMENSIONS_DIFF_LANDSCAPE
				: DIMENSIONS_DIFF_PORTRAIT;
		addContentView(
				mContent,
				new LinearLayout.LayoutParams(display.getWidth()
						- ((int) (dimensions[0] * scale + MAGIC)), display
						.getHeight() - ((int) (dimensions[1] * scale + MAGIC))));
	}

	/**
	 * Sets the title.
	 */
	private void setUpTitle() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Drawable icon = getContext().getResources().getDrawable(
				R.drawable.ic_action_search);
		mTitle = new TextView(getContext());
		mTitle.setText("Website");
		mTitle.setTextColor(Color.WHITE);
		mTitle.setTypeface(Typeface.DEFAULT_BOLD);
		mTitle.setBackgroundColor(BLUE);
		mTitle.setPadding(MARGIN + PADDING, MARGIN, MARGIN, MARGIN);
		mTitle.setCompoundDrawablePadding(MARGIN + PADDING);
		mTitle.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
		mContent.addView(mTitle);
	}

	/**
	 * Sets the web view.
	 */
	private void setUpWebView() {
		mWebView = new WebView(getContext());
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setWebViewClient(new TwitterDialog.DialogWebViewClient());
		mWebView.getSettings().setJavaScriptEnabled(true);
		log.info("mURL = {}", mUrl);

		mWebView.loadUrl(mUrl);
		mWebView.setLayoutParams(FILL);
		mContent.addView(mWebView);
	}

	/**
	 * WebClient
	 */
	private class DialogWebViewClient extends WebViewClient {

		/* (non-Javadoc)
		 * @see android.webkit.WebViewClient#shouldOverrideUrlLoading(android.webkit.WebView, java.lang.String)
		 */
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		/* (non-Javadoc)
		 * @see android.webkit.WebViewClient#onReceivedError(android.webkit.WebView, int, java.lang.String, java.lang.String)
		 */
		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			TwitterDialog.this.dismiss();
		}

		/* (non-Javadoc)
		 * @see android.webkit.WebViewClient#onPageStarted(android.webkit.WebView, java.lang.String, android.graphics.Bitmap)
		 */
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			mSpinner.show();
		}

		/* (non-Javadoc)
		 * @see android.webkit.WebViewClient#onPageFinished(android.webkit.WebView, java.lang.String)
		 */
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			String title = mWebView.getTitle();
			if (title != null && title.length() > 0) {
				mTitle.setText(title);
				if (title.equals("Twitter")) {
					// This will close the Dialog after tweeting
					TwitterDialog.this.dismiss();

				}
			}
			mSpinner.dismiss();
		}
	}

}