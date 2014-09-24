Facebook-Unity-library
======================

App42 Facebook unity library for Android application.

1. How can you autthorize facebook in application and get the access-Token.
2. How can you fetch facebook profile using App42Unity SDK.
3. How can you fetch your friend list using App42Unity SDK.

# Running Sample
This is a sample unity facebook application that use App42 Social Service to get facebook friends and your profile.

1. [Register\Login] (https://apphq.shephertz.com/register) with App42 platform and create an app from App Manager Tab. 
2. [Download project] (https://github.com/VishnuGShephertz/Facebook-Unity-library/archive/master.zip) and unzip it.
3. Open project in Unity.
4. Open App42FbSample.cs file and replace your facebook App ID here.
5. Use api-key and secret-key to get profile and facebook friends using App42UnitySDk.
6. Add App42FbSample.cs file on MainCamera if not added.
7. Build your unity Android application and run it on your android device.


# Design Details:

__Authorize with Facebook__ To Authorize facebook in unity Android application one of these  method with permission or withour permission.
 
```
App42Fb.authorizeFacebook (FbAppId,prmissions);

                 OR

App42Fb.authorizeFacebook (FbAppId);

```
__Set App42FbListener to get Access Token or Error__Set App42FbListener to get AccessToken or error fro facebook while Authorizing .
 
```
App42Fb.setApp42FbListener (this);

```

__Get Fb Token__ Once you have registered successfully with Faceboom your get Facebook Token in :
 
```
public void onFbToken (String accessToken)
	{
		message = accessToken;
	}

```


