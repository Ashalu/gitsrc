package com.tadqa.android.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tadqa.android.util.API_LINKS;
import com.tadqa.android.util.ConnectionDetector;
import com.tadqa.android.accessibility.ConvertInputStream;
import com.tadqa.android.accessibility.SMSReceiver;
import com.tadqa.android.accessibility.SolrData;
import com.tadqa.android.accessibility.TypeFaceUtil;
import com.tadqa.android.R;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GeneratePasswordFragment extends Fragment {

    EditText otpEditText;
    Button checkOtp;
    SolrData solrdata;
    String otp = "";
    SMSReceiver receiver;
    ProgressDialog dialog;

    public GeneratePasswordFragment() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        receiver = new SMSReceiver("GeneratePassword",this,null);

        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        getActivity().registerReceiver(receiver, filter);

        solrdata = (SolrData) getContext().getApplicationContext();
        TypeFaceUtil.overrideFont(getActivity(), "SERIF", "fonts/Roboto-Regular.ttf");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_resetpassword, null);

        otpEditText = (EditText) view.findViewById(R.id.txt_otp);
        checkOtp = (Button) view.findViewById(R.id.btncheckOtp);

        checkOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ConnectionDetector.isConnectingToInternet(getActivity())) {
                    if (otpEditText.getText().toString().length()==4) {
                        solrdata.setOtpCode(otpEditText.getText().toString());
                        String Url = API_LINKS.VERIFY_OTP_URL + ((ForgetUserPassword) getContext()).getUserNumber()+"&OTP="+ otpEditText.getText().toString();
                        VerifyOTP otp = new VerifyOTP(getActivity());
                        otp.execute(Url);

                    } else {
                        Toast.makeText(getActivity(), "Wrong One Time Password", Toast.LENGTH_LONG).show();
                    }
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setTitle("No Internet Connection");
                    alert.setMessage("Please check that you have enabled data.");
                    alert.setPositiveButton("OK", null);
                    alert.show();
                }
            }
        });

        return view;
    }

    public void setCode(String code){
        otpEditText.setText(code.toString());
        otp = ((ForgetUserPassword) getContext()).getUserOtp();
        if (otp.equals(code.toString())) {
            ((ForgetUserPassword) getContext()).setViewPagerChangePasswordPage();
        } else {
            Log.d("Incorrect OTP", otp);
        }
    }



    @Override
    public void onPause() {
        super.onPause();
        Log.d("Receiver State", "Unregistered");
        if(receiver!=null)
        getActivity().unregisterReceiver(receiver);
    }
    class VerifyOTP extends AsyncTask<String, String, Boolean> {

        Context context;
        String response;
        public VerifyOTP(Context context)
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
                HttpURLConnection connection = (HttpURLConnection) otpURL.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                Log.d("doInBackground: ",otpURL.toString() +" | "+ connection.getResponseCode());
                if(connection.getResponseCode() == 200) {

                    InputStream stream = new BufferedInputStream(connection.getInputStream());
                    response = ConvertInputStream.toString(stream);

                    return  true;
                } else {

                    int responseCode = connection.getResponseCode();
                    Log.d("Error", String.valueOf(responseCode));
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

            JSONObject object = null;
            try {
                object = new JSONObject(response);
                if(object.getInt("Success")==1) {
                    Toast.makeText(context, "Verify OTP... ", Toast.LENGTH_SHORT).show();
                    if ((dialog != null) && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    ((ForgetUserPassword) getContext()).setViewPagerChangePasswordPage();
                }
                else if(object.getInt("Success")==3)
                {
                    Toast.makeText(getActivity(), "Wrong One Time Password", Toast.LENGTH_LONG).show();
                }
                else if(object.getInt("Success")==4)
                {
                    Toast.makeText(getActivity(), "invald inputs", Toast.LENGTH_LONG).show();
                }
                else if(object.getInt("Success")==5)
                {
                    Toast.makeText(getActivity(), "Server internal error!", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(context, "Connection Error", Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
            if ((dialog != null) && dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

}


