package com.tadqa.android.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Rates extends AppCompatActivity {
    RatingBar ratingbar1;
    TextView button;
    SharedPreferences loginSharedPrefrences;
    SharedPreferences.Editor editor;
    EditText comments, phone;
    String phonenum;
    boolean isCorrectNumber = false;
    SolrData solrdata;
    ConnectionDetector condetect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        condetect = new ConnectionDetector(Rates.this);
        if (ConnectionDetector.isConnectingToInternet(this)) {
            setContentView(R.layout.rating);
            Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
            loginSharedPrefrences = getSharedPreferences("LOGIN_DETAILS", Context.MODE_PRIVATE);
            phone = (EditText) findViewById(R.id.contactnumber);

            comments = (EditText) findViewById(R.id.comments);
            if (toolbar != null) {

                toolbar.setTitle("Rate us");
                setSupportActionBar(toolbar);
                getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            TypeFaceUtil.overrideFont(getApplication(), "SERIF", "fonts/Roboto-Regular.ttf");

            phone.setEnabled(true);

            ratingbar1 = (RatingBar) findViewById(R.id.ratingBar1);

            ratingbar1.setRating(5);
            Drawable progress = ratingbar1.getProgressDrawable();
            //DrawableCompat.setTint(progress, Color.YELLOW);
            button = (TextView) findViewById(R.id.button1);

//        condetect.setTextAppearance(phone,getResources().getColor(R.color.Black));
//        condetect.setTextAppearance(comments,getResources().getColor(R.color.Black));
//        condetect.setTextAppearance((TextView)findViewById(R.id.txtfeed),getResources().getColor(R.color.Black));
//        condetect.setTextAppearance((TextView)findViewById(R.id.txtRate),getResources().getColor(R.color.Black));

            solrdata = (SolrData) getApplicationContext();
            boolean isbool = solrdata.getLoggedInStatus();
            phone.setEnabled(true);
            if (isbool && loginSharedPrefrences.getBoolean("isLoggedIn", true)) {
                phone.setText(loginSharedPrefrences.getString("number", ""));
                phone.setEnabled(false);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT || Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT || Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN) {
//                    Drawable tint = ratingbar1.getProgressDrawable();
//                    DrawableCompat.setTint(tint, Color.YELLOW);

                    Drawable tint = ratingbar1.getProgressDrawable();
                    DrawableCompat.setTint(tint, getResources().getColor(R.color.tintcolor));

                }
            }


            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (ConnectionDetector.isConnectingToInternet(Rates.this)) {
                        phonenum = phone.getText().toString();
                        if (phonenum.equals("") || phonenum.equals(null)) {
                            phone.setError("Field Mandatory");
                            phone.setFocusable(true);
                        } else if (Patterns.PHONE.matcher(phonenum).matches() && phonenum.length() == 10) {
                            Log.d("User Number", phonenum);
                            isCorrectNumber = true;
                            String rating = String.valueOf(ratingbar1.getRating());
                            String Comments = Uri.encode(comments.getText().toString());


                            String url = API_LINKS.SEND_FEEDBACK_URL + "Contact=" + phonenum + "&Rate=" + rating + "&Feedback=" + Comments;
                            new SendFeedBackRate().execute(url);
                            //Toast.makeText(getApplicationContext(), rating+Comments, Toast.LENGTH_SHORT).show();
                        } else {
                            phone.setError("Enter a valid number");
                            phone.setFocusable(true);

                        }
                    } else {
                        setContentView(R.layout.netconnection);
                    }
                }
            });
        } else {
            setContentView(R.layout.netconnection);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class SendFeedBackRate extends AsyncTask<String, String, String> {

        InputStream stream;
        String response = "failed";
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(Rates.this);
            dialog.setCancelable(true);
            dialog.setMessage("Please Wait !");
            dialog.show();
        }


        @Override
        protected String doInBackground(String... params) {
                try {
                    URL url = new URL(params[0]);
                    Log.d("API_LINKS", url.toString());
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();

                    if (connection.getResponseCode() == 200) {
                        stream = new BufferedInputStream(connection.getInputStream());
                        response = ConvertInputStream.toString(stream);
                        return response;
                    } else {

                        Log.d("Message", connection.getErrorStream().toString());
                        return response = "failed";
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();

                }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (response.equals("failed")) {
                new ConnectionDetector(Rates.this).isServerNotRunning();
            } else if (response.equals("") || response.equals(null)) {
                Toast.makeText(Rates.this, "Connection Error", Toast.LENGTH_SHORT).show();
            } else {
                JSONObject object = null;
                try {
                    object = new JSONObject(response);

                    if (object.getInt("Success") == 1) {
                        Toast toast = Toast.makeText(Rates.this, "Thank you for taking the time to provide us with your feedback.", Toast.LENGTH_LONG);
                        //Toast toast = Toast.makeText(Rates.this, "Thank you for taking the time to provide us with your feedback.", Toast.LENGTH_SHORT).show();
                        solrdata.setToast(toast);
                        toast.show();
                        comments.setText("");
                    } else if (object.getInt("Success") == 0) {
                        Toast.makeText(Rates.this, "User Not Found", Toast.LENGTH_LONG).show();
                    } else if (object.getInt("Success") == 4) {
                        Toast.makeText(Rates.this, "Invalid Inputs", Toast.LENGTH_LONG).show();

                    } else if (object.getInt("Success") == 5) {
                        Toast.makeText(Rates.this, "Server Internal Error", Toast.LENGTH_LONG).show();
                    } else {
                        if (object.has("Response"))
                            Toast.makeText(Rates.this, object.getString("Response"), Toast.LENGTH_LONG).show();
                        if ((dialog != null) && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if ((dialog != null) && dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }
}