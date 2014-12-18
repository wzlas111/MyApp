package com.eastelsoft.util.http;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.AsyncSingleHttpClient;
import com.loopj.android.http.RequestParams;

public class HttpRestClient {

	private static AsyncHttpClient mClient = new AsyncHttpClient();
	private static AsyncSingleHttpClient mSingleClient = new AsyncSingleHttpClient();
	
	public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		mClient.get(url, params, responseHandler);
	}
	
	public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		mClient.post(url, params, responseHandler);
	}
	
	public static void getSingle(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		mSingleClient.get(url, params, responseHandler);
	}
	
	public static void postSingle(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		mSingleClient.post(url, params, responseHandler);
	}
	
}
