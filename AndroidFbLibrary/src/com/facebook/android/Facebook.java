

package com.facebook.android;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieSyncManager;


public class Facebook {

    // Strings used in the authorization flow
 
//    protected static String GRAPH_BASE_URL =
//        "https://graph.facebook.com/";
//    protected static String RESTSERVER_URL =
//        "https://api.facebook.com/restserver.php";

    private String mAccessToken = null;
    private long mAccessExpires = 0;
    private String mAppId;
    private Activity mAuthActivity;
    private String[] mAuthPermissions;
    private int mAuthActivityCode;
    private DialogListener mAuthDialogListener;

    /**
     * Constructor for Facebook object.
     *
     * @param appId
     *          Your Facebook application ID. Found at
     *          www.facebook.com/developers/apps.php.
     */
    public Facebook(String appId) {
        if (appId == null) {
            throw new IllegalArgumentException(
                    "You must specify your application ID when instantiating " +
                    "a Facebook object. See README for details.");
        }
        mAppId = appId;
    }

    /**
     * Authorize method that grants custom permissions.
     *
     * See authorize() below for @params.
     */
    public void authorize(Activity activity, String[] permissions,
            final DialogListener listener) {
        mAuthDialogListener = listener;
        startDialogAuth(activity, permissions);
    }


	private void startDialogAuth(Activity activity, String[] permissions) {
        Bundle params = new Bundle();
        if (permissions.length > 0) {
            params.putString("scope", TextUtils.join(",", permissions));
        }
        CookieSyncManager.createInstance(activity);
        dialog(activity, Constants.Auth, params, new DialogListener() {

            public void onComplete(Bundle values) {
                // ensure any cookies set by the dialog are saved
                CookieSyncManager.getInstance().sync();
                setAccessToken(values.getString(Constants.AccessToken));
                setAccessExpiresIn(values.getString(Constants.ExpiresIn));
                if (isSessionValid()) {
                    Log.d("Facebook-authorize", "Login Success! access_token="
                            + getAccessToken() + " expires="
                            + getAccessExpires());
                    mAuthDialogListener.onComplete(values);
                } else {
                    mAuthDialogListener.onFacebookError(new App42Error(
                                    "Failed to receive access token."));
                }
            }

            public void onFacebookError(App42Error error) {
                Log.d("Facebook-authorize", "Login failed: " + error);
                mAuthDialogListener.onFacebookError(error);
            }

            public void onCancel() {
                Log.d("Facebook-authorize", "Login canceled");
                mAuthDialogListener.onCancel();
            }
        });
    }

 
    public void authorizeCallback(int requestCode, int resultCode, Intent data) {
        if (requestCode == mAuthActivityCode) {

            // Successfully redirected.
            if (resultCode == Activity.RESULT_OK) {

                // Check OAuth 2.0/2.10 error code.
                String error = data.getStringExtra("error");
                if (error == null) {
                    error = data.getStringExtra("error_type");
                }

                // A Facebook error occurred.
                if (error != null) {
                    if (error.equals(Constants.ServiceDisable)
                            || error.equals("AndroidAuthKillSwitchException")) {
                        Log.d("Facebook-authorize", "Hosted auth currently "
                            + "disabled. Retrying dialog auth...");
                        startDialogAuth(mAuthActivity, mAuthPermissions);
                    } else if (error.equals("access_denied")
                            || error.equals("OAuthAccessDeniedException")) {
                        Log.d("Facebook-authorize", "Login canceled by user.");
                        mAuthDialogListener.onCancel();
                    } else {
                        Log.d("Facebook-authorize", "Login failed: " + error);
                        mAuthDialogListener.onFacebookError(
                                new App42Error(error));
                    }

                // No errors.
                } else {
                    setAccessToken(data.getStringExtra(Constants.AccessToken));
                    setAccessExpiresIn(data.getStringExtra(Constants.ExpiresIn));
                    if (isSessionValid()) {
                        Log.d("Facebook-authorize",
                                "Login Success! access_token="
                                        + getAccessToken() + " expires="
                                        + getAccessExpires());
                        mAuthDialogListener.onComplete(data.getExtras());
                    } else {
                        mAuthDialogListener.onFacebookError(new App42Error(
                                        "Failed to receive access token."));
                    }
                }

            // An error occurred before we could be redirected.
            } else if (resultCode == Activity.RESULT_CANCELED) {

                // An Android error occured.
                if (data != null) {
                    Log.d("Facebook-authorize",
                            "Login failed: " + data.getStringExtra("error"));
                    mAuthDialogListener.onFacebookError(
                            new App42Error(
                                    data.getStringExtra("error"),"dialogError",
                                    data.getIntExtra("error_code", -1)
                                   ));

                // User pressed the 'back' button.
                } else {
                    Log.d("Facebook-authorize", "Login canceled by user.");
                    mAuthDialogListener.onCancel();
                }
            }
        }
    }
   
    public void dialog(Context context, String action,
            DialogListener listener) {
        dialog(context, action, new Bundle(), listener);
    }
    public void dialog(Context context, String action, Bundle parameters,
            final DialogListener listener) {

        String endpoint = Constants.DialogUrl + action;
        parameters.putString("display", "touch");
        parameters.putString("redirect_uri", Constants.RedirectUrl);

        if (action.equals(Constants.Auth)) {
            parameters.putString("type", "user_agent");
            parameters.putString("client_id", mAppId);
        } else {
            parameters.putString("app_id", mAppId);
        }

        if (isSessionValid()) {
            parameters.putString(Constants.AccessToken, getAccessToken());
        }
        String url = endpoint + "?" + Util.encodeUrl(parameters);
        if (context.checkCallingOrSelfPermission(Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            Util.showAlert(context, "Error",
                    "Application requires permission to access the Internet");
        } else {
            new FbDialog(context, url, listener).show();
        }
    }

    /**
     * @return boolean - whether this object has an non-expired session token
     */
    public boolean isSessionValid() {
        return (getAccessToken() != null) &&
                ((getAccessExpires() == 0) ||
                        (System.currentTimeMillis() < getAccessExpires()));
    }

    /**
     * Retrieve the OAuth 2.0 access token for API access: treat with care.
     * Returns null if no session exists.
     *
     * @return String - access token
     */
    public String getAccessToken() {
        return mAccessToken;
    }

    /**
     * Retrieve the current session's expiration time (in milliseconds since
     * Unix epoch), or 0 if the session doesn't expire or doesn't exist.
     *
     * @return long - session expiration time
     */
    public long getAccessExpires() {
        return mAccessExpires;
    }

    /**
     * Set the OAuth 2.0 access token for API access.
     *
     * @param token
     *            - access token
     */
    public void setAccessToken(String token) {
        mAccessToken = token;
    }

    /**
     * Set the current session's expiration time (in milliseconds since Unix
     * epoch), or 0 if the session doesn't expire.
     *
     * @param time
     *            - timestamp in milliseconds
     */
    public void setAccessExpires(long time) {
        mAccessExpires = time;
    }

    /**
     * Set the current session's duration (in seconds since Unix epoch).
     *
     * @param expiresIn
     *            - duration in seconds
     */
    public void setAccessExpiresIn(String expiresIn) {
        if (expiresIn != null && !expiresIn.equals("0")) {
            setAccessExpires(System.currentTimeMillis()
                    + Integer.parseInt(expiresIn) * 1000);
        }
    }

    public String getAppId() {
        return mAppId;
    }

    public void setAppId(String appId) {
        mAppId = appId;
    }

    /**
     * Callback interface for dialog requests.
     *
     */
    public static interface DialogListener {

        public void onComplete(Bundle values);

        public void onFacebookError(App42Error e);

        public void onCancel();

    }

}
