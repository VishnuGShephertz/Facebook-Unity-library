

using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System.Runtime.InteropServices;
using System;
using AssemblyCSharpfirstpass;

public class App42Fb : MonoBehaviour
{
	private static App42Fb mInstance = null;
	private static App42FbListener app42FbListener;

	public static void setFbReceiver ()
	{
		if (mInstance == null) {
			GameObject receiverObject = new GameObject ("App42Facebook");
			DontDestroyOnLoad (receiverObject);
			mInstance = receiverObject.AddComponent<App42Fb> ();
		}
	}
	public static void setApp42FbListener (App42FbListener listener)
	{
		app42FbListener = listener;
	}

	public void onAccessToken (String accessToken)
	{ 
		if (app42FbListener != null) {
			Debug.Log (accessToken);
			app42FbListener.onFbToken (accessToken);
		}
	}

	public void onError (String error)
	{ 
		if (app42FbListener != null) {
			Debug.Log (error);
			app42FbListener.onError (error);
		}
	}

	public static void authorizeFacebook (string appId)
	{   setFbReceiver ();
		using (
			AndroidJavaClass jc = new AndroidJavaClass("com.app42.fb.unity.FbHelper")) { 
			jc.CallStatic("authorizeFacebook", appId);
		}
	}

	private static string parsepermissions(string[] permissions){
		string fbPermissions = "";
		int lenghth = permissions.Length;
		for (int i=0; i<lenghth; i++) {
			if(i==(lenghth-1))
				fbPermissions+=permissions[i];
			else
			fbPermissions+=permissions[i]+",";
		}
		return fbPermissions;
		}
	public static void authorizeFacebook (string appId,string[] permissions)
	{   
		string fbPermissions = parsepermissions(permissions);

		setFbReceiver ();																																																																																																									
		using (
			AndroidJavaClass jc = new AndroidJavaClass("com.app42.fb.unity.FbHelper")) { 
			jc.CallStatic("authorizeFacebook", appId,fbPermissions);
		}
	}


}
	