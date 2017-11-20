package com.tadqa.android.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.tadqa.android.util.API_LINKS;

import org.json.JSONException;
import org.json.JSONObject;

import developer.shivam.perfecto.OnNetworkRequest;
import developer.shivam.perfecto.Perfecto;

public class FCMTokenSender extends Service {

    ServiceBinder mServiceBinder = new ServiceBinder();
    private Context mContext = FCMTokenSender.this;
    private JSONObject parentObject;

    boolean isTokenSend = false;

    Handler mFCMTokenSendingHandler;

    public class TokenSendingRunnable implements Runnable {

        @Override
        public void run() {
            if (!isTokenSend) {
                sendRegistrationTokenToServer();
                mFCMTokenSendingHandler.postDelayed(this, 10000);
            } else {
                stopSelf();
            }
        }
    }

    public class ServiceBinder extends Binder {
        public FCMTokenSender getService() {
            return FCMTokenSender.this;
        }
    }

    /**
     * @param intent
     * @return mServiceBinder object so that client can bind to it.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mServiceBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void sendToken() {
        mFCMTokenSendingHandler = new Handler();
        mFCMTokenSendingHandler.post(new TokenSendingRunnable());
    }

    private void sendRegistrationTokenToServer() {

        if (getSharedPreferences("LOGIN_DETAILS", Context.MODE_PRIVATE).getBoolean("isLoggedIn", false)) {

            String deviceId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
            String number = getSharedPreferences("LOGIN_DETAILS", Context.MODE_PRIVATE).getString("number", "");
            String token = getSharedPreferences("LOGIN_DETAILS", Context.MODE_PRIVATE).getString("token", "");

            if (!number.equals("") && !token.equals("")) {
                try {
                    parentObject = new JSONObject();
                    parentObject.put("deviceid", deviceId);
                    parentObject.put("contact", number);
                    parentObject.put("token", token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Perfecto.with(mContext)
                        .fromUrl(API_LINKS.SEND_FCM_DETAILS)
                        .ofTypePost(parentObject)
                        .connect(new OnNetworkRequest() {

                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onSuccess(String response) {
                                Log.d("FCM Notification", response);
                                isTokenSend = true;
                            }

                            @Override
                            public void onFailure(int responseCode, String responseMessage, String errorStream) {
                                Log.d("FCM Notification", "Response Code : " + responseCode + " " + "Response Message : " + responseMessage);
                            }
                        });
            } else {
                Log.d("FCM Notification", "User is logged in but number or token is empty");
            }
        } else {
            Log.d("FCM Notification", "Token not sent. No user is logged in");
        }
    }
}
