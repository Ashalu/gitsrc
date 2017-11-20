package com.tadqa.android.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.StrictMode;
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

public class RegistrationActivity extends AppCompatActivity {

    EditText editTextUsername, editTextPassword, editTextEmail, editTextNumber;
    TextView msg;
    Button buttonDone;
    boolean isCorrectName = false, isCorrectPassword = false, isCorrectEmail = false, isCorrectNumber = false;
    String name, password, email, number;
    String registerUserString;
    HttpURLConnection connection;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    JSONObject registerUser;
    ProgressDialog dialog;
    EditText editTextNumber91;
    SolrData solrdata;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ConnectionDetector.isConnectingToInternet(this)) {
            setContentView(R.layout.activity_registration);
            TypeFaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Roboto-Regular.ttf");

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            preferences = getSharedPreferences("LOGIN_DETAILS", Context.MODE_PRIVATE);
            editor = preferences.edit();

            editTextUsername = (EditText) findViewById(R.id.editText_username);
            editTextPassword = (EditText) findViewById(R.id.editText_password);
            editTextEmail = (EditText) findViewById(R.id.editText_email);
            msg = (TextView) findViewById(R.id.txt);
            editTextNumber = (EditText) findViewById(R.id.editText_number);
            editTextNumber91 = (EditText) findViewById(R.id.editText_number_91);
            editTextNumber91.setEnabled(false);

            solrdata = (SolrData) getApplicationContext();

            buttonDone = (Button) findViewById(R.id.button_done);
            buttonDone.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    name = editTextUsername.getText().toString();
                    if (name.equals("") || name.equals(null)) {
                        editTextUsername.requestFocus();
                        editTextUsername.setError("Field Mandatory");
                    } else if (name.matches("^[\\p{L} .'-]+$")) {
                        Log.d("User Name", name);
                        isCorrectName = true;
                    } else {
                        editTextUsername.requestFocus();
                        editTextUsername.setError("Enter a valid userName");
                    }

                    password = editTextPassword.getText().toString();
                    if (password.equals("") || password.equals(null)) {
                        editTextPassword.requestFocus();
                        editTextPassword.setError("Field Mandatory");
                    } else if (password.length() > 6) {
                        Log.d("User Password", password);
                        isCorrectPassword = true;
                    } else {
                        editTextPassword.requestFocus();
                        editTextPassword.setError("Length should be greater than 6");
                    }

                    email = editTextEmail.getText().toString();
                    if (email.equals("") || email.equals(null)) {
                        editTextEmail.requestFocus();
                        editTextEmail.setError("Field Mandatory");
                    } else if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Log.d("User Email", email);
                        isCorrectEmail = true;
                    } else {
                        editTextEmail.requestFocus();
                        editTextEmail.setError("Enter a valid email");
                    }

                    number = editTextNumber.getText().toString();
                    if (number.equals("") || number.equals(null)) {
                        editTextNumber.requestFocus();
                        editTextNumber.setError("Field Mandatory");
                    } else if (Patterns.PHONE.matcher(number).matches() && number.length() == 10) {
                        Log.d("User Number", number);
                        isCorrectNumber = true;
                    } else {
                        editTextNumber.requestFocus();
                        editTextNumber.setError("Enter a valid number");
                    }


                    if ((isCorrectName && isCorrectPassword && isCorrectEmail && isCorrectNumber)) {

                        registerUser = new JSONObject();
                        try {

                            registerUser.put("Name", name);
                            registerUser.put("Contact", number);
                            registerUser.put("Email", email);
                            //registerUser.put("LeadBy", "App");
                            registerUser.put("Password", password);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        registerUserString = registerUser.toString();
                        RegisterUser user = new RegisterUser();
                        user.execute(API_LINKS.REGISTRATION_URL);

                    }
                }
            });
        } else {
            setContentView(R.layout.netconnection);
        }

    }

    public class RegisterUser extends AsyncTask<String, String, Boolean> {

        String response = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(RegistrationActivity.this);
            dialog.setCancelable(false);
            dialog.setMessage("Please Wait");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(10000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setFixedLengthStreamingMode(registerUserString.getBytes().length);
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-type", "application/json");

                OutputStream os = new BufferedOutputStream(connection.getOutputStream());
                os.write(registerUserString.getBytes());
                os.flush();


                connection.connect();

                Log.d("Register Post", url.toString());
                Log.d("Register String", registerUserString.toString());

                if (connection.getResponseCode() == 200) {

                    Log.d("Message", "Success");
                    response = ConvertInputStream.toString(connection.getInputStream());
                    return true;
                } else {
                    Log.d("Message", connection.getResponseCode() + "");
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);

            if (s == true) {
                JSONObject responseJson = null;
                Log.d("USER Response", response);
                try {
                    responseJson = new JSONObject(response);
                    if (responseJson.has("Success")) {
                        if (responseJson.getInt("Success") == 1) {
                            editor.putString("userName", name);
                            editor.putString("email", email);
                            editor.putString("number", number);
                            editor.putBoolean("isLoggedIn", true);

                            Toast toast = Toast.makeText(RegistrationActivity.this, "Thank you for filling out your information!", Toast.LENGTH_SHORT);//.show();
                            solrdata.setToast(toast);
                            toast.show();

                            buttonDone.setClickable(false);
                            dialog.dismiss();
                            editor.apply();
                            Intent intent = new Intent(RegistrationActivity.this, ChooseLocality.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else if (responseJson.getInt("Success") == 2) {
                            Toast.makeText(RegistrationActivity.this, "Already Registered User", Toast.LENGTH_LONG).show();
                            msg.setVisibility(View.VISIBLE);
                            msg.setText("Already Registered User");
                        } else if (responseJson.getInt("Success") == 4) {
                            Toast.makeText(RegistrationActivity.this, "Invalid Inputs", Toast.LENGTH_LONG).show();
                            msg.setVisibility(View.VISIBLE);
                            msg.setText("Invalid Inputs");

                        } else if (responseJson.getInt("Success") == 5) {
                            Toast.makeText(RegistrationActivity.this, "Server Internal Error", Toast.LENGTH_LONG).show();
                        } else if (responseJson.getInt("Success") == 6) {
                            Toast.makeText(RegistrationActivity.this, "Routing Error", Toast.LENGTH_LONG).show();
                        }
                        if ((dialog != null) && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {

                if (!ConnectionDetector.isConnectingToInternet(RegistrationActivity.this)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
                    builder.setTitle("Error !!");

                    builder.setIcon(R.drawable.ic_portable_wifi_off_black_48dp);
                    builder.setMessage("No Internet Connection!  ");


                    builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });

                    builder.show();

                } else {
                    new ConnectionDetector(RegistrationActivity.this).isServerNotRunning();
                }

            }
            if ((dialog != null) && dialog.isShowing()) {
                dialog.dismiss();
            }

        }
    }
}


