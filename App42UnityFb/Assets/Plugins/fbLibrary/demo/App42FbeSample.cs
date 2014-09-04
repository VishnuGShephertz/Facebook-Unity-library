using UnityEngine;
using System.Collections;
using System.IO;

using System;
using System.Net;
using AssemblyCSharpfirstpass;

public class App42FbeSample: MonoBehaviour, App42FbListener
{
	private string message = "AppHype Message";
	private int viewHeight = Screen.height / 10;
	private int viewWidth = (Screen.width * 3) / 4 ;
	private int leftGap = Screen.width / 10;
	private int fontSize = Screen.width / 20;

	void OnGUI ()
	{
		GUIStyle style = new GUIStyle ();
		style.fontSize = fontSize;

		GUI.Label (new Rect (leftGap, viewHeight, viewWidth, viewHeight), message, style);
		if (GUI.Button (new Rect (leftGap, viewHeight * 2, viewWidth, viewHeight), "Authorize")) {
			message = "Authorizing facebook";
			App42Fb.setApp42FbListener (this);
			App42Fb.authorizeFacebook ("559704997458702",new string[]{"friends_online_presence"});
		}
	}

	public void Start ()
	{
		DontDestroyOnLoad (transform.gameObject);

	}

	public void onFbToken (String accessToken)
	{
		message = accessToken;
	}

	public void onError (String error)
	{
		message = error;
	}
}



	
