package com.facebook.android;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.android.Facebook.DialogListener;

//import com.kmiller.facebookintegration.R;

public class FbDialog extends Dialog {

	static final int FB_BLUE = 0xFF6D84B4;
	static final float[] DIMENSIONS_LANDSCAPE = { 400, 260 };
	static final float[] DIMENSIONS_PORTRAIT = { 280, 360 };
	static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.MATCH_PARENT);
//	static final int MARGIN = 4;
//	static final int PADDING = 2;
	static final String DISPLAY_STRING = "touch";
	// static final String FB_ICON = "icon.png";

	private String mUrl;
	private DialogListener mListener;
	private ProgressDialog mSpinner;
	private WebView mWebView;
	private LinearLayout mContent;
	private TextView mTitle;

	public FbDialog(Context context, String url, DialogListener listener) {
		super(context);
		mUrl = url;
		mListener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSpinner = new ProgressDialog(getContext());
		mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mSpinner.setMessage("Loading...");

		mContent = new LinearLayout(getContext());
		mContent.setOrientation(LinearLayout.VERTICAL);
		// setUpTitle();
		setUpWebView();
		Display display = getWindow().getWindowManager().getDefaultDisplay();
		final float scale = getContext().getResources().getDisplayMetrics().density;
		float[] dimensions = (display.getWidth() < display.getHeight()) ? DIMENSIONS_PORTRAIT
				: DIMENSIONS_LANDSCAPE;
//		addContentView(mContent, new FrameLayout.LayoutParams(
//				(int) (dimensions[0] * scale + 0.5f), (int) (dimensions[1]
//						* scale + 0.5f)));
		addContentView(mContent, new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, (int) (dimensions[1]
						* scale + 0.5f)));
//		 final Window window = this.getWindow();
//		    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
//		    window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//		    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
	}

	 private void setUpTitle() {
	 requestWindowFeature(Window.FEATURE_NO_TITLE);
	 // Drawable icon = getContext().getResources().getDrawable(
	 // R.drawable.facebook_icon);
	 mTitle = new TextView(getContext());
	 mTitle.setText("Facebook");
	 mTitle.setTextColor(Color.WHITE);
	 mTitle.setTypeface(Typeface.DEFAULT_BOLD);
	 mTitle.setBackgroundColor(FB_BLUE);
//	 mTitle.setPadding(MARGIN + PADDING, MARGIN, MARGIN, MARGIN);
//	 mTitle.setCompoundDrawablePadding(MARGIN + PADDING);
//	 mTitle.setCompoundDrawablesWithIntrinsicBounds(
//	 icon, null, null, null);
	 mContent.addView(mTitle);
	 }

	private void setUpWebView() {
		mWebView = new WebView(getContext());
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setWebViewClient(new FbDialog.App42FbClient());
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.loadUrl(mUrl);
		mWebView.setLayoutParams(FILL);
		mContent.addView(mWebView);
	}

	private class App42FbClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.d("Facebook-WebView", "Redirect URL: " + url);
			if (url.startsWith(Constants.RedirectUrl)) {
				Bundle values = Util.parseUrl(url);
				String error = values.getString("error");
				if (error == null) {
					error = values.getString("error_type");
				}
				if (error == null) {
					mListener.onComplete(values);
				} else if (error.equals("access_denied")
						|| error.equals("OAuthAccessDeniedException")) {
					mListener.onCancel();
				} else {
					mListener.onFacebookError(new App42Error(error));
				}

				FbDialog.this.dismiss();
				return true;
			} else if (url.startsWith(Constants.CanceLUrl)) {
				mListener.onCancel();
				FbDialog.this.dismiss();
				return true;
			} else if (url.contains(DISPLAY_STRING)) {
				return false;
			}
			// launch non-dialog URLs in a full browser
			getContext().startActivity(
					new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
			return true;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			mListener.onFacebookError(new App42Error(description, failingUrl,
					errorCode));
			FbDialog.this.dismiss();
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Log.d("Facebook-WebView", "Webview loading URL: " + url);
			super.onPageStarted(view, url, favicon);
			mSpinner.show();
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
//			String title = mWebView.getTitle();
//			if (title != null && title.length() > 0) {
//				mTitle.setText(title);
//			}
			mSpinner.dismiss();
		}

	}
}
