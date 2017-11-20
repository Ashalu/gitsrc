package com.tadqa.android.activity;

import android.app.ProgressDialog;
import android.content.Context;
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

import com.tadqa.android.util.API_LINKS;
import com.tadqa.android.util.ConnectionDetector;
import com.tadqa.android.accessibility.ConvertInputStream;
import com.tadqa.android.accessibility.SolrData;
import com.tadqa.android.accessibility.TypeFaceUtil;
import com.tadqa.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ChangePasswordFragment extends Fragment {

    EditText password, confirmPassword;
    Button submit;
    SolrData solrdata;
    String userNumber;

    public ChangePasswordFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        solrdata = (SolrData) getContext().getApplicationContext();
        TypeFaceUtil.overrideFont(getActivity(), "SERIF", "fonts/Roboto-Regular.ttf");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, null);

        userNumber = ((ForgetUserPassword) getContext()).getUserNumber();
        password = (EditText) view.findViewById(R.id.newpswd);
        confirmPassword = (EditText) view.findViewById(R.id.newpswd_cnf);
        submit = (Button) view.findViewById(R.id.btnGenNewPswd);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getText().toString().matches(confirmPassword.getText().toString())) {
                    // String Url = solrData.CreateNewPassword + "Contact=" + userNumber + "&newPassword=" + Uri.encode(password.getText().toString());
                    JSONObject objct = new JSONObject();
                    try {

                        objct.put("Contact", userNumber);
                        objct.put("Password", password.getText().toString());


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    CreatePassword newPassword = new CreatePassword(getActivity());
                    newPassword.execute(API_LINKS.CREATE_NEW_PASSWORD_URL, objct.toString());
                } else {
                    password.requestFocus();
                    password.setError("Password not matched");
                }
            }
        });
        return view;
    }

    class CreatePassword extends AsyncTask<String, String, Boolean> {

        ProgressDialog dialog;
        Context context;
        String response = "";

        public CreatePassword(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.setMessage("Canceling your Order");
            dialog.setMessage("Please Wait");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                URL url = new URL(params[0]);
                Log.d("doInBackground: ", url.toString());
//                HttpURLConnection connection = (HttpURLConnection) otpUrl.openConnection();
//                connection.setRequestMethod("GET");
//                connection.connect();

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

                Log.d("NewPassword Post", url.toString());
                Log.d("NewPassword String", params[1].toString());

                if (connection.getResponseCode() == 200) {

                    InputStream stream = new BufferedInputStream(connection.getInputStream());
                    response = ConvertInputStream.toString(stream);
                    Log.d("Get Password", response);
                    return true;

                } else {

                    Log.d("Error", String.valueOf(connection.getResponseCode()));
                    return false;
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

            if (!s) {
                new ConnectionDetector(context).isServerNotRunning();
            } else {
                getActivity().finish();
            }

            if ((dialog != null) && dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }
}