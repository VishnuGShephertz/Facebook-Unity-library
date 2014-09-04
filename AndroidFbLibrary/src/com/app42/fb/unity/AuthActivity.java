package com.app42.fb.unity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.facebook.android.App42Error;
import com.facebook.android.Constants;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.SessionStore;
import com.unity3d.player.UnityPlayer;

public class AuthActivity extends Activity {

	// public static final String keyId = "559704997458702";

	public Facebook mFacebook;// = ;new Facebook(mAPP_ID);
	private static final String GameObject = "App42Facebook";
	private static final String OnAccessToken = "onAccessToken";
	private static final String OnError = "onError";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		   requestWindowFeature(Window.FEATURE_NO_TITLE);
		    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
		                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		String fbAppId = getIntent().getStringExtra(Constants.FbAppID);
		String[] permissions=getIntent().getStringArrayExtra(Constants.KeyPermissions);
		if (fbAppId != null)
			authorizeFacebook(fbAppId,permissions);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mFacebook.authorizeCallback(requestCode, resultCode, data);
	}

	public void authorizeFacebook(String appId,String[] permissions) {
		System.out.println("Authorizing");
		mFacebook = new Facebook(appId);
		if (!mFacebook.isSessionValid()) {
			mFacebook.authorize(this, permissions ,
					new LoginDialogListener());
		} else {
			System.out.println(mFacebook.getAccessToken());
			UnityPlayer.UnitySendMessage(GameObject, OnAccessToken,
					mFacebook.getAccessToken());
			finish();
		}
	}

	public final class LoginDialogListener implements DialogListener {
		public void onComplete(Bundle values) {
			try {
				// code for get Profile
				SessionStore.save(mFacebook, AuthActivity.this);
				UnityPlayer.UnitySendMessage(GameObject, OnAccessToken,
						mFacebook.getAccessToken());
			} catch (Throwable error) {
				UnityPlayer.UnitySendMessage(GameObject, OnError,
						error.getMessage());
			}
			finish();
		}

		public void onFacebookError(App42Error error) {
			UnityPlayer.UnitySendMessage(GameObject, OnError,
					error.getMessage());
			finish();
		}

		public void onCancel() {
			Toast.makeText(UnityPlayer.currentActivity,
					"Something went wrong. Please try again.",
					Toast.LENGTH_LONG).show();
		}
	}

}