
package com.facebook.android;

public class App42Error extends Throwable {

	private static final long serialVersionUID = 1L;
	private int mErrorCode = 0;
    private String mErrorType;

    public App42Error(String message) {
        super(message);
    }
    public App42Error(String message, String type, int code) {
        super(message);
        mErrorType = type;
        mErrorCode = code;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public String getErrorType() {
        return mErrorType;
    }

}
