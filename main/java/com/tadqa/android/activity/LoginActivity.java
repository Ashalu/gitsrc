package com.tadqa.android.activity;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tadqa.android.services.FCMTokenSender;
import com.tadqa.android.util.API_LINKS;
import com.tadqa.android.util.ConnectionDetector;
import com.tadqa.android.accessibility.ConvertInputStream;
import com.tadqa.android.accessibility.SolrData;
import com.tadqa.android.accessibility.TypeFaceUtil;
import com.tadqa.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import developer.shivam.perfecto.OnNetworkRequest;
import developer.shivam.perfecto.Perfecto;

public class LoginActivity extends AppCompatActivity {

    EditText userName, userPassword;
    SharedPreferences loginSharedPreferences;
    SharedPreferences.Editor editor;
    Button logIn;
    boolean isUsernameCorrect = false, isPasswordCorrect = false;
    ProgressDialog dialog;
    TextView forgetPassword, register;
    SolrData solrdata;
    public boolean isFromOrderHistory = false;
    public boolean isFromCheckout = false;
    String ContactNo = "";
    private Context mContext = LoginActivity.this;
    private JSONObject parentObject;

    FCMTokenSender mTokenSender;

    boolean mBound = false;

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            FCMTokenSender.ServiceBinder serviceBinder = (FCMTokenSender.ServiceBinder) service;
            mTokenSender = serviceBinder.getService();
            mBound = true;
            Log.d("Service Bounded", "Connection made");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        Intent tokenSendingIntent = new Intent(mContext, FCMTokenSender.class);
        bindService(tokenSendingIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        Intent intent = getIntent();
        if (intent.getStringExtra("which_activity").equals("CheckoutActivity")) {
            isFromCheckout = true;
        } else if (intent.getStringExtra("which_activity").equals("OrderHistory")) {
            isFromOrderHistory = true;
        } else if (intent.getStringExtra("which_activity").equals("GeneratePassword")) {

        }

        loginSharedPreferences = getSharedPreferences("LOGIN_DETAILS", Context.MODE_PRIVATE);
        editor = loginSharedPreferences.edit();

        TypeFaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Roboto-Regular.ttf");

        userName = (EditText) findViewById(R.id.editText_username);
        userPassword = (EditText) findViewById(R.id.editText_password);
        forgetPassword = (TextView) findViewById(R.id.forgetpassword);
        register = (TextView) findViewById(R.id.registerUserActivity);
        solrdata = (SolrData) getApplicationContext();

        Typeface face = Typeface.createFromAsset(getResources().getAssets(), "fonts/Roboto-Regular.ttf");
        userName.setTypeface(face);
        forgetPassword.setTypeface(face);

        forgetPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetUserPassword.class);
                startActivity(intent);
            }
        });

        logIn = (Button) findViewById(R.id.button_login);
        logIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (Patterns.PHONE.matcher(userName.getText().toString()).matches() && userName.getText().toString().length() == 10) {
                    isUsernameCorrect = true;
                } else {
                    userName.requestFocus();
                    userName.setError("Enter a valid number");
                }

                if (!userPassword.getText().toString().equals("")) {
                    isPasswordCorrect = true;
                } else {
                    userPassword.requestFocus();
                    userPassword.setError("Wrong Password");
                }
                if (isPasswordCorrect == true && isUsernameCorrect == true) {
                    LoginAction();
                }

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);

            }
        });

    }

    protected void LoginAction() {
        try {
            JSONObject objt = new JSONObject();
            try {
                ContactNo = userName.getText().toString();
                objt.put("Contact", ContactNo);
                objt.put("Password", userPassword.getText().toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            LoginTask task = new LoginTask();
            task.execute(API_LINKS.SIGN_IN_URL, objt.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public class LoginTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(LoginActivity.this);
            dialog.setCancelable(true);
            dialog.setMessage("Please Wait !");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {

                URL url = new URL(params[0]);
                Log.d("Login : ", url.toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(10000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setFixedLengthStreamingMode(params[1].getBytes().length);
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-type", "application/json");

                OutputStream os = new BufferedOutputStream(connection.getOutputStream());
                os.write(params[1].getBytes());
                os.flush();

                connection.connect();

                Log.d("SignIn Post", url.toString());
                if (connection.getResponseCode() == 200) {
                    String json = ConvertInputStream.toString(connection.getInputStream());

                    return json;
                } else {
                    Log.d("Message", connection.getErrorStream().toString());
                    return "error";
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "error";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("SignIn RESPONSE", s);
            JSONObject responseJson = null;
            JSONObject UsrResponse = null;
            try {
                responseJson = new JSONObject(s);
                if (responseJson.has("Success")) {
                    if (responseJson.getInt("Success") == 1) {
                        UsrResponse = responseJson.getJSONObject("user");
                        editor.putString("number", ContactNo);
                        editor.putBoolean("isLoggedIn", true);
                        if (UsrResponse.has("Name")) {
                            editor.putString("userName", UsrResponse.getString("Name"));
                            solrdata.setUserName(UsrResponse.getString("Name"));
                        }

                        if (UsrResponse.has("Email")) {
                            editor.putString("email", UsrResponse.getString("Email"));

                        }
                        if (UsrResponse.has("Gender")) {
                            editor.putString("gender", UsrResponse.getString("Gender"));
                        }
                        if (UsrResponse.has("Birthday")) {
                            editor.putString("dob", UsrResponse.getString("Birthday").split("T")[0]);
                        }

                        editor.apply();

                        mTokenSender.sendToken();


                        SolrData.setLoggedInStatus(true);

                        if (isFromOrderHistory) {

                            Intent resultIntent = new Intent();
                            setResult(200, resultIntent);
                            finish();

                        } else if (isFromCheckout) {

                            Intent resultIntent = new Intent();
                            setResult(200, resultIntent);
                            finish();

                        } else {
                            Intent intent = new Intent(LoginActivity.this, ChooseLocality.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }

                    } else if (responseJson.getInt("Success") == 0) {
                        Toast.makeText(getApplication(), "Invalid UserName and Password!", Toast.LENGTH_LONG).show();
                    } else if (responseJson.getInt("Success") == 4) {
                        Toast.makeText(getApplication(), "Invalid Inputs", Toast.LENGTH_LONG).show();

                    } else if (responseJson.getInt("Success") == 5) {
                        Toast.makeText(getApplication(), "Server Internal Error", Toast.LENGTH_LONG).show();
                    } else if (responseJson.getInt("Success") == 6) {
                        Toast.makeText(getApplication(), "Routing Error", Toast.LENGTH_LONG).show();
                    }
                    if ((dialog != null) && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } else {
                    if (!ConnectionDetector.isConnectingToInternet(LoginActivity.this)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setTitle("Error !!");

                        builder.setIcon(R.drawable.ic_portable_wifi_off_black_48dp);
                        builder.setMessage("No Internet Connection!  ");


                        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogIn, int which) {
                                dialogIn.dismiss();
                                LoginAction();
                            }
                        });
                        builder.show();
                    } else {
                        new ConnectionDetector(LoginActivity.this).isServerNotRunning();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            if ((dialog != null) && dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }
}

