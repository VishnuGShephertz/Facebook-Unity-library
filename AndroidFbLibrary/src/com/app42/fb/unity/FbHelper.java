package com.app42.fb.unity;

import android.content.Intent;

import com.facebook.android.Constants;
import com.unity3d.player.UnityPlayer;

public class FbHelper {

	public static void authorizeFacebook(final String appId,final String permissions) {
		UnityPlayer.currentActivity.runOnUiThread(new Runnable() {
			public void run() {
				System.out.println("##################NNNNNNNNNNN###########");
				System.out.println(permissions);
				String[] permisionArr=permissions.split(",");
				for(int i=0;i<permisionArr.length;i++)
					System.out.println("&&-------"+permisionArr[i]);
				System.out.println(permisionArr.toString());
				Intent intent = new Intent(UnityPlayer.currentActivity,
						AuthActivity.class);
				intent.putExtra("fbAppId", appId);
				intent.putExtra(Constants.KeyPermissions, permisionArr);
				UnityPlayer.currentActivity.startActivity(intent);
				
			}
		});

	}
	public static void authorizeFacebook(final String appId) {
		UnityPlayer.currentActivity.runOnUiThread(new Runnable() {
			public void run() {
				Intent intent = new Intent(UnityPlayer.currentActivity,
						AuthActivity.class);
				intent.putExtra("fbAppId", appId);
				intent.putExtra(Constants.KeyPermissions,new String[]{""});
				UnityPlayer.currentActivity.startActivity(intent);
				
			}
		});

	}
}
