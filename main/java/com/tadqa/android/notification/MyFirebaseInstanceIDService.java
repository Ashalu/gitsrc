package com.tadqa.android.notification;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.tadqa.android.util.API_LINKS;

import developer.shivam.perfecto.OnNetworkRequest;
import developer.shivam.perfecto.Perfecto;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    private Context mContext = MyFirebaseInstanceIDService.this;

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {

        SharedPreferences loginSharedPreferences = getSharedPreferences("LOGIN_DETAILS", Context.MODE_PRIVATE);
        SharedPreferences.Editor loginSharedPreferencesEditor = loginSharedPreferences.edit();
        loginSharedPreferencesEditor.putString("token", token);
        loginSharedPreferencesEditor.apply();

        if (getSharedPreferences("LOGIN_DETAILS", Context.MODE_PRIVATE).getBoolean("isLoggedIn", false)) {

            String deviceId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
            String number = getSharedPreferences("LOGIN_DETAILS", Context.MODE_PRIVATE).getString("number", "");

            if (!number.equals("")) {
                Perfecto.with(mContext)
                        .fromUrl(API_LINKS.SEND_FCM_DETAILS + deviceId + "&contact=" + number + "&token=" + token)
                        .ofTypeGet()
                        .connect(new OnNetworkRequest() {

                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onSuccess(String response) {
                                Log.d("FCM Notification", response);
                            }

                            @Override
                            public void onFailure(int responseCode, String responseMessage, String errorStream) {
                                Log.d("FCM Notification", "Response Code : " + responseCode + " " + "Response Message : " + responseMessage);
                            }
                        });
            } else {
                Log.d("FCM Notification", "User is logged in but number is empty");
            }
        } else {
            Log.d("FCM Notification", "Token not sent. No user is logged in");
        }
    }
}
