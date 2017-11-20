package com.tadqa.android.activity;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import com.tadqa.android.util.API_LINKS;
import com.tadqa.android.util.ConnectionDetector;
import com.tadqa.android.accessibility.ConvertInputStream;
import com.tadqa.android.accessibility.SolrData;
import com.tadqa.android.accessibility.TypeFaceUtil;
import com.tadqa.android.R;

public class About extends AppCompatActivity {

    Toolbar toolbar;
    SolrData solrdata;
    ArrayList<String> data = new ArrayList<>();
    TextView txtAbout;
    Spanned spanedtxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        if (toolbar != null) {
            toolbar.setTitle("About tadqa");

            setSupportActionBar(toolbar);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        solrdata = (SolrData) getApplicationContext();
        TypeFaceUtil.overrideFont(getApplication(), "SERIF", "fonts/Roboto-Regular.ttf");


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        txtAbout = (TextView) findViewById(R.id.txtabt);
//        txtAbout.setText(Html.fromHtml("<p>Quality food is our resolve, committed service is our culture and satisfied customer is our objective!</p>" +
//                "<p>tadqa Services Pvt. Ltd. is fast emerging as a prominent player in corporate and retail food service, contract catering and allied industries.\n" +
//                " With our cloud kitchen in East Delhi and Takeaway outlet in Noida, tadqa is best placed to serve customers with wide variety of offerings.\n" +
//                "Our commitment to customer satisfaction, quality food and value product makes us stand out from the crowd, helping us carve new milestones in the food service industry.</p>" +
//                "<p>Backed by Share India group, a leading name in financial service industry - our diversified business interests include in-house-developed brands, " +
//                "integrated contract catering and Support Services. To better cater to our customers," +
//                " we have hired chefs with over 15 years of experience and expertise in entire spectrum of Indian cuisine.</p>" +
//                "<p>Our Values:<br/> Food service is not just business but our passion.</p>"));
//        ConnectionDetector connectionDetector=new ConnectionDetector(getApplicationContext());
//        connectionDetector.setTypeFont(txtAbout);

        if (solrdata.aboutData.size() == 0) {
            Abouttadqa task = new Abouttadqa();
            task.execute(API_LINKS.ABOUT_US_URL);
        } else {
            String abtData = "";
            Log.d("Aboutus", solrdata.aboutData.size() + "");
            for (int t = 0; t < solrdata.aboutData.size(); t++) {
                abtData += ("<p>" + solrdata.aboutData.get(t) + "</p>");
            }

            spanedtxt = Html.fromHtml(abtData.replace("\n", "<br>"));
            txtAbout.setText(spanedtxt);

            ConnectionDetector connectionDetector = new ConnectionDetector(getApplicationContext());
            connectionDetector.setTypeFont(txtAbout);
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

    public class Abouttadqa extends AsyncTask<String, String, Boolean> {

        String response = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                InputStream instream = null;
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                if (connection.getResponseCode() == 200) {
                    instream = new BufferedInputStream(connection.getInputStream());
                    response = ConvertInputStream.toString(instream);
                    return true;

                } else {

                    return false;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }


        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);

            if (s == true) {

                JSONObject jObj = null;
                try {
                    jObj = new JSONObject(response);
                    JSONArray array = (JSONArray) jObj.getJSONArray("AboutUs");
                    for (int i = 0; i < array.length(); i++) {

                        JSONObject obj = array.getJSONObject(i);
                        data.add(obj.getString("AboutUs"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String abtData = "";
                for (int t = 0; t < data.size(); t++) {
                    abtData += (("<p>" + data.get(t) + "</p>")).toString();
                }
                spanedtxt = Html.fromHtml(abtData.replace("\n", "<br>"));
                txtAbout.setText(spanedtxt);
                solrdata.aboutData = data;
                ConnectionDetector connectionDetector = new ConnectionDetector(getApplicationContext());
                connectionDetector.setTypeFont(txtAbout);

            } else {


                if (!ConnectionDetector.isConnectingToInternet(About.this)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(About.this);
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
                    new ConnectionDetector(About.this).isServerNotRunning();
                }

            }


        }
    }
}

