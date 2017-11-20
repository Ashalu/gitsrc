package com.tadqa.android.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tadqa.android.R;
import com.tadqa.android.accessibility.ConvertInputStream;
import com.tadqa.android.accessibility.SolrData;
import com.tadqa.android.accessibility.TypeFaceUtil;
import com.tadqa.android.adapter.CustomSpinnerAdapter;
import com.tadqa.android.pojo.LocationModel;
import com.tadqa.android.util.API_LINKS;
import com.tadqa.android.util.ConnectionDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddAddress extends Activity {

    Button btnSave;
    ProgressDialog pDialog;
    Spinner spArea;
    String[] deliveryAreaList;
    EditText etHouseNumber, etStreetNumber, etLandmark, etPostal, etState, etCity;
    String selectedArea, selectedCity, ContactNumber;
    SharedPreferences loginSharedPreferences;
    SolrData solrdata;
    String FromActivity = "";
    String TAG="AddAddress";
    CoordinatorLayout coordinatorLayout;

    private String blockCharacterSet = "~#^$%*!@/()-+&'\":;,?{}=!$^';,?×÷<>{}€£¥₩%~`¤♡♥_|《》¡¿°•○●□■◇◆♧♣▲▼▶◀↑↓←→☆★▪:-);-):-D:-(:'(:O";

    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addaddress);
        btnSave = (Button) findViewById(R.id.save);
        spArea = (Spinner) findViewById(R.id.editText_area);
        etHouseNumber = (EditText) findViewById(R.id.editText_houseNo);
        etStreetNumber = (EditText) findViewById(R.id.editText_street);
        etPostal = (EditText) findViewById(R.id.editText_postal);
        etState = (EditText) findViewById(R.id.edtxt_state);
        etLandmark = (EditText) findViewById(R.id.editText_landmark);
        etHouseNumber.setFilters(new InputFilter[]{filter});
        etStreetNumber.setFilters(new InputFilter[]{filter});
        etCity = (EditText) findViewById(R.id.edtxt_city);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.snackbarCoordinatorLayout);
        loginSharedPreferences = getSharedPreferences("LOGIN_DETAILS", Context.MODE_PRIVATE);

        ContactNumber = loginSharedPreferences.getString("number", "");

        TypeFaceUtil.overrideFont(getApplication(), "SERIF", "fonts/Roboto-Regular.ttf");
        solrdata = (SolrData) getApplication().getApplicationContext();

        if (solrdata.selectedDeliveryLocationArray[0] == null) {
            GetDeliveryAreaByOutLet obj = new GetDeliveryAreaByOutLet();
            if (solrdata.isHomeMade)
                obj.execute(API_LINKS.GET_DELIVERY_AREA_BY_OUTLET_URL + "thm");
            else if (solrdata.isTadqaTandoorExpress)
                obj.execute(API_LINKS.GET_DELIVERY_AREA_BY_OUTLET_URL + "tte");
            else
                obj.execute(API_LINKS.GET_DELIVERY_AREA_BY_OUTLET_URL + "all");

        } else {
            setAreaAddress();
        }


        spArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedArea = deliveryAreaList[position];

                if (solrdata.userSelectedDeliveryAreaByOutLetList != null && solrdata.userSelectedDeliveryAreaByOutLetList.size() != 0) {
                    LocationModel mdl = solrdata.userSelectedDeliveryAreaByOutLetList.get(position);
                    if (mdl != null) {
                        selectedCity = mdl.getCity();
                        etCity.setText(selectedCity);
                        selectedArea = mdl.getArea();
                        etState.setText(mdl.getState());
                        etPostal.setText(mdl.getPostal());

                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }

        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean iscorrectHouseno = false, iscorrectStreetAdd=false;
                if (etHouseNumber != null && etHouseNumber.getText().toString().length() > 0) {
                    iscorrectHouseno = true;
                } else {
                    etHouseNumber.setError("Field Mandatory");
                    etHouseNumber.requestFocus();
                }
                if (etStreetNumber != null && etStreetNumber.getText().toString().length() > 0) {
                    iscorrectStreetAdd = true;
                } else {
                    etStreetNumber.setError("Field Mandatory");
                    etStreetNumber.requestFocus();
                }
                if (iscorrectHouseno && iscorrectStreetAdd) {

                    Log.i(TAG, etHouseNumber.getText().toString() + "," + etStreetNumber.getText().toString() + "," + selectedArea + "," + etLandmark.getText() + "," + etPostal.getText() + "," + selectedCity + "," + etState.getText());

                    if (ContactNumber.length() == 10) {
                        if (etPostal.getText().toString().length() == 0)
                            etPostal.setText("");

                        JSONObject ob1 = new JSONObject();
                        JSONArray address = new JSONArray();

                        try {
                            ob1.put("address", etHouseNumber.getText().toString() + "," + etStreetNumber.getText().toString());
                            ob1.put("area", selectedArea);
                            ob1.put("landmark", etLandmark.getText().toString());
                            ob1.put("pincode", etPostal.getText().toString());
                            ob1.put("city", selectedCity);
                            ob1.put("state", etState.getText().toString());
                            address.put(0, ob1);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        JSONObject value = new JSONObject();
                        try {
                            value.put("Contact", ContactNumber);
                            value.put("Address", address);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        onSaveAddress(API_LINKS.ADD_EDIT_ADDRESS_URL, value.toString());

                    }
                }
            }
        });
    }

    void setAreaAddress() {
        try {
            if (deliveryAreaList == null)
                deliveryAreaList = solrdata.selectedDeliveryLocationArray;

            spArea.setAdapter(new CustomSpinnerAdapter(this, deliveryAreaList, true));
            spArea.setSelection(0);

            Intent intent = getIntent();
            if (intent.getExtras().containsKey("From"))
                FromActivity = intent.getStringExtra("From");

            if (FromActivity.contains("$")) {

                String[] arr = FromActivity.split("\\$")[1].split("\\|");
                String[] addressArr = arr[1].split(",");
                if (addressArr.length > 0)
                    etHouseNumber.setText(addressArr[0]);
                else
                    etHouseNumber.setText("");
                if (addressArr.length > 1)
                    etStreetNumber.setText(addressArr[1]);
                else
                    etStreetNumber.setText("");

                if (arr.length > 2) {
                    int pos = Arrays.asList(deliveryAreaList).indexOf(arr[2]);
                    spArea.setAdapter(new CustomSpinnerAdapter(AddAddress.this, deliveryAreaList, true));
                    if (pos != -1) {
                        spArea.setSelection(pos);
                        selectedArea = deliveryAreaList[pos];
                    }

                    if (arr.length > 3)
                        if (arr[3].length() > 0)
                            etLandmark.setText(arr[3]);
                        else
                            etLandmark.setText("");

                    if (arr.length > 4)
                        etPostal.setText(arr[4]);


                    if (arr.length > 5) {
                        etCity.setText(arr[5]);
                    }
                    if (arr.length > 6)
                        etState.setText(arr[6]);

                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    protected void onSaveAddress(final String Path, final String Url) {
        try {
            if (ConnectionDetector.isConnectingToInternet(this)) {

                if (coordinatorLayout != null)
                    coordinatorLayout.setVisibility(View.GONE);
                   new AddEditAddress().execute(Path, Url);
            } else {

                coordinatorLayout.setVisibility(View.VISIBLE);

                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                onSaveAddress(Path, Url);

                            }
                        });

                // Changing message text color
                snackbar.setActionTextColor(Color.RED);
                // Changing action button text color
                View sbView = snackbar.getView();

                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                snackbar.show();
            }
        } catch (Exception ex) {
        }
    }

    private void showProgressDialog() {
        if (pDialog == null) {
            pDialog = new ProgressDialog(AddAddress.this);
            pDialog.setMessage("Loading. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
        }
        pDialog.show();
    }

    private void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

     class AddEditAddress extends AsyncTask<String, String, String> {


        InputStream stream;
        String response = "failed";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }


        @Override
        protected String doInBackground(String... params) {


            try {

                URL url = new URL(params[0]);
                Log.d("Address URL ", url.toString());
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
                Log.d("Post Address", params[1]);

                if (connection.getResponseCode() == 200) {
                    stream = new BufferedInputStream(connection.getInputStream());
                    response = ConvertInputStream.toString(stream);
                } else {
                    Log.d("Message", connection.getErrorStream().toString());
                    return response = "failed to load the data";
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

            JSONObject responseJson = null;
            if (response.equals("failed") || response.contains("failed")) {
                Toast.makeText(AddAddress.this, response.toUpperCase(), Toast.LENGTH_LONG).show();

            } else {

                try {
                    responseJson = new JSONObject(response);
                  if (responseJson.has("Success")) {
                      if (responseJson.getInt("Success") == 1) {
                        Intent resultIntent = new Intent();
                        setResult(200, resultIntent);
                        finish();

                        Toast.makeText(AddAddress.this, "Successfully update Address", Toast.LENGTH_SHORT).show();
                        if (FromActivity.contains("CheckoutActivity")) {
                            Intent intent = new Intent(AddAddress.this, AddressBook.class);
                            intent.putExtra("From", "CheckoutActivity");
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Intent myintent = new Intent(getApplicationContext(), AddressBook.class);
                            myintent.putExtra("From", "AddAddress");
                            startActivity(myintent);
                        }
                    }
                      else if (responseJson.getInt("Success") == 0) {
                          Log.d(TAG,"Response=0 Record Not Found");
                      } else if (responseJson.getInt("Success") == 4) {
                          Log.d(TAG,"Response=4 Invalid Inputs");
                      } else if (responseJson.getInt("Success") == 5) {
                          Log.d(TAG,"Response=5 Server Internal Error");
                      } else if (responseJson.getInt("Success") == 6) {
                          Log.d(TAG,"Response=6 Routing Error");
                      }

                }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            dismissProgressDialog();
        }
    }

    class GetDeliveryAreaByOutLet extends AsyncTask<String, String, String> {
        String response = "";
        List<LocationModel> UserSelectedDeliveryArea = new ArrayList<>();

        @Override
        protected String doInBackground(String... params) {

            try {

                if (ConnectionDetector.isConnectingToInternet(AddAddress.this)) {
                    /**
                     * Below code is for A La Carte items.
                     */
                    URL url = new URL(params[0]);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);

                    conn.connect();
                    if (conn.getResponseCode() == 200) {
                        response = ConvertInputStream.toString(conn.getInputStream());
                    } else {
                        response = "failed";
                    }
                }

            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                response = "failed";
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONObject responseJson = null;
            JSONArray docsArray = null;

            try {
                responseJson = new JSONObject(response);
                if (responseJson.has("Success")) {
                    if (responseJson.getInt("Success") == 1) {
                        docsArray = responseJson.getJSONArray("outlet");
                        deliveryAreaList = new String[docsArray.length()];
                        for (int i = 0; i < docsArray.length(); i++) {
                            deliveryAreaList[i] = docsArray.getJSONObject(i).getString("Area");
                            LocationModel model = new LocationModel();
                            model.setArea(docsArray.getJSONObject(i).getString("Area"));
                            model.setCity(docsArray.getJSONObject(i).getString("City"));
                            model.setState(docsArray.getJSONObject(i).getString("State"));
                            model.setPostal(docsArray.getJSONObject(i).getString("Pincode"));
                            UserSelectedDeliveryArea.add(model);
                        }
                        solrdata.selectedDeliveryLocationArray = deliveryAreaList;
                        solrdata.userSelectedDeliveryAreaByOutLetList = UserSelectedDeliveryArea;

                        setAreaAddress();

                    }  else if (responseJson.getInt("Success") == 0) {
                        Toast.makeText(getApplication(), "Record Not Found!", Toast.LENGTH_LONG).show();
                        Log.d(TAG,"Response=0 Record Not Found");
                    } else if (responseJson.getInt("Success") == 4) {
                        Toast.makeText(getApplication(), "Invalid Inputs", Toast.LENGTH_LONG).show();
                        Log.d(TAG,"Response=4 Invalid Inputs");
                    } else if (responseJson.getInt("Success") == 5) {
                        Log.d(TAG,"Response=5 Server Internal Error");
                    } else if (responseJson.getInt("Success") == 6) {
                        Log.d(TAG,"Response=6 Routing Error");
                    }
                }
                else {
                    Toast.makeText(getApplication(), "Record Not Found!", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }
}

