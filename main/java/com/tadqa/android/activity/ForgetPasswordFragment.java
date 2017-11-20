package com.tadqa.android.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.tadqa.android.util.API_LINKS;
import com.tadqa.android.util.ConnectionDetector;
import com.tadqa.android.accessibility.ConvertInputStream;
import com.tadqa.android.accessibility.SolrData;
import com.tadqa.android.accessibility.TypeFaceUtil;
import com.tadqa.android.R;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ForgetPasswordFragment extends Fragment {

    EditText number91;
    Button sendOtp;
    LinearLayout fakeStatusBar;
    boolean isContactCorrect = false;
    SolrData solrdata;
    ProgressDialog dialog;
    EditText contactNumber;


    public ForgetPasswordFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        solrdata = (SolrData) getContext().getApplicationContext();

        TypeFaceUtil.overrideFont(getActivity(), "SERIF", "fonts/Roboto-Regular.ttf");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_forgetpswd, null);

        sendOtp = (Button) view.findViewById(R.id.btnContinue);
        contactNumber = (EditText) view.findViewById(R.id.txt_number);
        number91 = (EditText) view.findViewById(R.id.editText_number_91);

        contactNumber.requestFocus();
        number91.setEnabled(false);
        sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ConnectionDetector.isConnectingToInternet(getActivity())) {

                    if (Patterns.PHONE.matcher(contactNumber.getText().toString()).matches() && contactNumber.getText().toString().length() == 10) {
                        isContactCorrect = true;
                    } else {
                        contactNumber.requestFocus();
                        contactNumber.setError("Enter a valid number");
                    }
                    if (isContactCorrect) {

                        String Url = API_LINKS.GENERATE_OTP_URL + "Contact=" + contactNumber.getText().toString();
                        GenerateOTP otp = new GenerateOTP(getActivity());
                        otp.execute(Url);
                    }

                } else {

                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setTitle("No Internet Connection");
                    alert.setMessage("Please check that you have enabled data.");
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    //setContentView(R.layout.netconnection);
                }
            }
        });

        return view;
    }

        class GenerateOTP extends AsyncTask<String, String, Boolean> {

            Context context;
            public GenerateOTP(Context context)
            {
                this.context =context;
            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                dialog = new ProgressDialog(getActivity());
                dialog.setCancelable(false);
                dialog.setMessage("Please Wait");
                dialog.show();
            }

            @Override
            protected Boolean doInBackground(String... params) {
                try {


                    URL otpURL = new URL(params[0]);
                    Log.d("doInBackground: ",otpURL.toString());

                    HttpURLConnection connection = (HttpURLConnection) otpURL.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();

                    if(connection.getResponseCode() == 200) {

                        InputStream stream = new BufferedInputStream(connection.getInputStream());
                        String response = ConvertInputStream.toString(stream);
                        Log.d("generateOTP",response);
                        return  true;

                    } else {

                        int responseCode = connection.getResponseCode();
                        Log.d("Error", String.valueOf(responseCode));
                        //if(responseCode==404)
                        //new ConnectionDetector(context).isServerNotRunning();
                        //Toast.makeText(ForgetPasswordFragment.this, "Problem in Canceling try Again !", Toast.LENGTH_SHORT).show();
                      return  false;
                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean s) {
                super.onPostExecute(s);
                dialog.dismiss();

                if ((dialog != null) && dialog.isShowing()) {
                    dialog.dismiss();
                } else if (s) {
                    ((ForgetUserPassword) getActivity()).setUserNumber(contactNumber.getText().toString());
                    ((ForgetUserPassword) getActivity()).setViewPagerGeneratePasswordPage();
                } else {
                    new ConnectionDetector(context).isServerNotRunning();
                }
            }
        }

}

