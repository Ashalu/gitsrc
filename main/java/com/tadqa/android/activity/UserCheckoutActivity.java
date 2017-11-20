package com.tadqa.android.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.paytm.pgsdk.PaytmMerchant;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.tadqa.android.R;
import com.tadqa.android.accessibility.ConvertInputStream;
import com.tadqa.android.accessibility.SMSReceiver;
import com.tadqa.android.accessibility.SolrData;
import com.tadqa.android.accessibility.TypeFaceUtil;
import com.tadqa.android.adapter.CustomSpinnerAdapter;
import com.tadqa.android.pojo.LocationModel;
import com.tadqa.android.util.API_LINKS;
import com.tadqa.android.util.ConnectionDetector;
import com.tadqa.android.util.DatabaseHelper;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import developer.shivam.perfecto.OnNetworkRequest;
import developer.shivam.perfecto.Perfecto;

public class UserCheckoutActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar appBar;
    EditText userName, userNumber, otpEditText, addCity, addhouseNo, addStreet, addresslandmark;
    SharedPreferences loginSharedPreferences;
    Spinner addArea;
    Button btnProceed, otpgen;
    ProgressDialog dialog;
    SolrData solrdata;
    private String localityName = "";
    String UserAddress = "";
    boolean isOTPverified = false;
    boolean isContactCorrect = false, isNameCorrect = false, isAddressCorrect = false, isotpCorrect = false;
    TextView loginTextView, ResendOtp, ChangeAddress, tvHaveCoupon;
    SharedPreferences userSharedPreferences;
    SharedPreferences.Editor userSharedPreferencesEditor;
    String OTPResponse;
    SMSReceiver receiver;
    LinearLayout layoutAreaCity, codOtplayout;//layoutHNoStreetNo
    boolean isLoggedIn = false;
    String DeliveryAddress, selectedTime, TotalPrice, Items, OrderNumber, SelectedArea, SelectedAddress;
    JSONArray ItemsArr;

    String[] selectedDeliveryAreaList;
    String isFromActivity = "", itemID = "";
    String SignInUsrArea = "", SignInUsrCity = "";

    CoordinatorLayout coordinatorLayout;
    RadioGroup radioGroup;
    RadioButton rdcod, rdpaytm;
    boolean isfrompaytm = false;
    public String infoText;
    int discount = 0;
    private final int RESPONSE_SUCCESS = 1;

    /**
     * Below are the timer and timerTask to delay
     * the responseString got from network request.
     */
    Timer delayTimer = new Timer();
    TimerTask delayedTask;
    int DELAY_AMOUNT = 1500;
    private Context mContext = UserCheckoutActivity.this;

    @Override
    protected void onStart() {
        super.onStart();
    }

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
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_checkout);

        userSharedPreferences = getSharedPreferences("USER_DETAIL", Context.MODE_PRIVATE);
        userSharedPreferencesEditor = userSharedPreferences.edit();
        localityName = userSharedPreferences.getString("USER_LOCALITY_NAME", "");

        // Calling Application class (see application tag in AndroidManifest.xml)
        solrdata = (SolrData) getApplication().getApplicationContext();

        appBar = (Toolbar) findViewById(R.id.app_bar);

        userName = (EditText) findViewById(R.id.editText_username);
        userNumber = (EditText) findViewById(R.id.editText_number);
        //userAddress = (EditText) findViewById(R.id.editText_address);
        otpEditText = (EditText) findViewById(R.id.txt_otp);
        otpgen = (Button) findViewById(R.id.btnverify);
        btnProceed = (Button) findViewById(R.id.next);
        loginTextView = (TextView) findViewById(R.id.userLogin);
        ChangeAddress = (TextView) findViewById(R.id.changeAddress);
        ResendOtp = (TextView) findViewById(R.id.ResendOtp);
        addresslandmark = (EditText) findViewById(R.id.editText_landmark);

        tvHaveCoupon = (TextView) findViewById(R.id.tvHaveCoupon);
        tvHaveCoupon.setOnClickListener(this);

        addArea = (Spinner) findViewById(R.id.editText_area);
        addCity = (EditText) findViewById(R.id.editText_city);
        addhouseNo = (EditText) findViewById(R.id.editText_houseNo);
        addStreet = (EditText) findViewById(R.id.editText_street);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.snackbarCoordinatorLayout);
        codOtplayout = (LinearLayout) findViewById(R.id.codOtplayout);
        solrdata.discountAmount=0;
        userName.setFilters(new InputFilter[]{filter});
        //userAddress.setFilters(new InputFilter[]{filter});
        addhouseNo.setFilters(new InputFilter[]{filter});
        addStreet.setFilters(new InputFilter[]{filter});

        rdcod = (RadioButton) findViewById(R.id.cod);
        rdpaytm = (RadioButton) findViewById(R.id.paytm);

        receiver = new SMSReceiver("CheckOut", null, this);
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        getApplicationContext().registerReceiver(receiver, filter);
        layoutAreaCity = (LinearLayout) findViewById(R.id.layout_AreaCity);
        // layoutHNoStreetNo=(LinearLayout) findViewById(R.id.layout_HNoStreet);

        TypeFaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Roboto-Regular.ttf");
        selectedDeliveryAreaList = solrdata.selectedDeliveryLocationArray;
        rdcod.setSelected(true);

        addCity.setText("Noida");
        addCity.setEnabled(true);
        addArea.setAdapter(new CustomSpinnerAdapter(this, selectedDeliveryAreaList, true));
        int locationPosition = getLocationPosition(localityName);
        if (locationPosition == -1) {
            Log.d("Location", "Invalid location");
        } else if (locationPosition == 0) {
            Log.d("Location", "Not found");
        } else {
            addArea.setSelection(locationPosition);
        }

        Intent intent = getIntent();

        if (intent.getExtras().containsKey("From"))
            isFromActivity = intent.getStringExtra("From");
        if (intent.getExtras().containsKey("SelectedAddress"))
            SelectedAddress = intent.getStringExtra("SelectedAddress");

        //Get Items Quantity
        itemID = getTotalItemQuantity();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setLoginDetails();


        appBar.setTitle("User details");

        addArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//                if(solrData.isNoida) {
//                    SelectedArea =Noida[position];
//                }
//                else if(solrData.isDelhi)
//                {
//                    SelectedArea =Delhilist[position];
//                }
                SelectedArea = selectedDeliveryAreaList[position];
                LocationModel mdl = solrdata.userSelectedDeliveryAreaByOutLetList.get(position);

                if (mdl != null)
                    addCity.setText(mdl.getCity());

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }

        });


        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userName.setError(null);
                userNumber.setError(null);
                addresslandmark.setError(null);
                addhouseNo.setError(null);
                addStreet.setError(null);
                addCity.setError(null);

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.putExtra("which_activity", "CheckoutActivity");
                startActivityForResult(intent, 200);
            }
        });

        ChangeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent addbook = new Intent(getApplicationContext(), AddressBook.class);
                addbook.putExtra("From", "CheckoutActivity");
                // startActivityForResult(addbook, 200);
                startActivityForResult(addbook, 200);
            }
        });


        ResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValidateUserDetails();
                if (isNameCorrect == true && isContactCorrect == true && isAddressCorrect == true) {
                    SendOtp();
                }

            }
        });

        otpgen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValidateUserDetails();
                {
                    if (isNameCorrect == true && isContactCorrect == true && isAddressCorrect == true) {
                        SendOtp();
                    }
                }
            }
        });


        btnProceed.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ValidateUserDetails();
                if (!isfrompaytm) {

                    solrdata.setOtpCode(OTPResponse);
                    if (isLoggedIn) {
                        DeliveryAddress = UserAddress;
                    } else {
                        DeliveryAddress = (addhouseNo.getText().toString() + "," + addStreet.getText().toString()) + "|" + SelectedArea + "|" + addresslandmark.getText().toString() + " |201301|" + addCity.getText().toString() + "|" + "UP";
                    }
                    Log.d("userInfo ", userName.getText() + " | " + userNumber.getText() + " | " + DeliveryAddress + " | otp | " + otpEditText.getText());
                    VerifyingOtp();

                } else if (isfrompaytm) {
                    if (userNumber.getText().toString().length() != 0 && isAddressCorrect && isNameCorrect && isContactCorrect && userName.getText().toString().length() != 0) {

                        paytmMerchant paytm = new paytmMerchant(OrderNumber, userNumber.getText().toString(), "10", solrdata.getUserEmail());

                        if (isLoggedIn) {
                            DeliveryAddress = UserAddress;
                        } else {
                            DeliveryAddress = (addhouseNo.getText().toString() + "," + addStreet.getText().toString()) + "|" + SelectedArea + "|" + addresslandmark.getText().toString() + "|201301|" + addCity.getText().toString() + "|" + "UP";
                        }
                        Log.d("userInfo ", userName.getText() + " | " + userNumber.getText() + " | " + DeliveryAddress + " | otp | " + otpEditText.getText());


                    } else {
                        Toast.makeText(UserCheckoutActivity.this, "Check Your Credentials..", Toast.LENGTH_SHORT);
                    }

                }

            }

        });


        userNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!userNumber.getText().toString().matches(solrdata.getUserNumber())) {
                    if (userNumber.getText().toString().length() < 10) {
                        otpEditText.setText("");
                        btnProceed.setEnabled(false);
                        btnProceed.setBackgroundResource(R.drawable.button_grey_roundcorner);
                    } else {
                        otpEditText.setText("");
                        btnProceed.setEnabled(true);
                        btnProceed.setBackgroundResource(R.drawable.button_round_corner);
                    }
                }
            }
        });

        rdpaytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                codOtplayout.setVisibility(View.GONE);
                btnProceed.setEnabled(true);
                isfrompaytm = true;
                btnProceed.setBackgroundResource(R.drawable.button_round_corner);
            }

        });
        rdcod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codOtplayout.setVisibility(View.VISIBLE);
                btnProceed.setEnabled(false);
                btnProceed.setBackgroundResource(R.drawable.button_grey_roundcorner);
                isfrompaytm = false;
            }
        });

        if (rdpaytm.isSelected()) {

            codOtplayout.setVisibility(View.GONE);
            btnProceed.setEnabled(true);
            isfrompaytm = true;
            btnProceed.setBackgroundResource(R.drawable.button_round_corner);
        }


        if (rdcod.isSelected()) {
            codOtplayout.setVisibility(View.VISIBLE);
            btnProceed.setEnabled(false);
            btnProceed.setBackgroundResource(R.drawable.button_grey_roundcorner);
            isfrompaytm = false;
        }
    }

    Cursor cursor;
    String TABLE_NAME = "CART_DATA";
    DatabaseHelper helper;
    int idvalue = -1;

    public String getTotalItemQuantity() {

        helper = new DatabaseHelper(getApplicationContext());
        helper.open();

        cursor = helper.getData(TABLE_NAME);
        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    return cursor.getString(cursor.getColumnIndex("ITEM_ID"));
                } else {
                    return "";
                }
            } else return "";
        }
        return "";

    }


    protected void CheckUserAddress() {
        try {
            boolean isRightaddressSelection = false;

            if (!itemID.isEmpty() && itemID.length() == 0) {
                itemID = getTotalItemQuantity();
            }
            if (!itemID.isEmpty()) {
                Log.d("getItemID Alacarte", itemID);
                String ss = itemID.replace("Item", "");
                idvalue = Integer.valueOf(ss);
            }
            {
                String arr[] = new String[0];
                if (SignInUsrArea.length() > 0) {
                    arr = SignInUsrArea.split("\\,");
                }
                int pos = -1;
                //pos = Arrays.asList(Noida).indexOf(arr[arr.length-1]);
                pos = Arrays.asList(selectedDeliveryAreaList).indexOf(arr[arr.length - 1]);
                if (pos != -1) {
                    isRightaddressSelection = true;
                    addArea.setSelection(pos);
                } else
                    addArea.setSelection(0);
            }

            if (!isRightaddressSelection) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(UserCheckoutActivity.this);
                builder.setTitle("Alert !");

                builder.setIcon(R.drawable.ic_add_location_black_48dp);
                if (solrdata.isHomeMade)
                    builder.setMessage("Tadqa Home Made will not able to deliver in this area.Change your address with respect to selected meals location.");
                else
                    builder.setMessage("Tadqa Tandoor Express will not able to deliver in this area.Change your address with respect to selected meals location.");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent addbook = new Intent(getApplicationContext(), AddressBook.class);
                        addbook.putExtra("From", "CheckoutActivity");
                        startActivityForResult(addbook, 200);

                    }
                });

                builder.show();
                // Toast.makeText(this, "User Address not valid for delivery.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void SendOtp() {
        try {

            if (ConnectionDetector.isConnectingToInternet(UserCheckoutActivity.this)) {

                if (userNumber.getText().toString().length() != 0 && isAddressCorrect && isNameCorrect && isContactCorrect && userName.getText().toString().length() != 0) {

                    solrdata.setUserName(userName.getText().toString());
                    solrdata.setUserNumber(userNumber.getText().toString());
                    String SetAddress = (addhouseNo.getText().toString() + "," + addStreet.getText().toString()) + "|" + SelectedArea + "|" + addresslandmark.getText().toString() + "|201301|" + addCity.getText().toString() + "|UP";
                    solrdata.setUserAddress(SetAddress);
                    Log.d("Set ADDRESS: ", SetAddress);
                    String Url = API_LINKS.ORDER_OTP_URL + "Contact=" + userNumber.getText().toString();
                    GenerateOTP otp = new GenerateOTP(getApplicationContext());
                    otp.execute(Url);

                    btnProceed.setEnabled(true);
                    btnProceed.setBackgroundResource(R.drawable.button_round_corner);
                } else {
                    Toast.makeText(UserCheckoutActivity.this, "Check Your Credentials..", Toast.LENGTH_SHORT);
                }
            } else {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                );
                params.setMargins(0, 0, 0, 80);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
                int pixels = (int) (245 * scale + 0.5f);
                int heightpixel = (int) (38 * scale + 0.5f);
                params.height = heightpixel;
                params.width = pixels;

                btnProceed.setLayoutParams(params);
                coordinatorLayout.setVisibility(View.VISIBLE);
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SendOtp();
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
            ex.printStackTrace();
        }
    }

    protected void VerifyingOtp() {
        try {

            if (ConnectionDetector.isConnectingToInternet(UserCheckoutActivity.this)) {

                if (otpEditText.getText().toString().length() == 4) {

                    solrdata.setOtpCode(otpEditText.getText().toString());

                    String Url = API_LINKS.VERIFY_OTP_URL + userNumber.getText() + "&OTP=" + otpEditText.getText().toString();
                    VerifyOTP otp = new VerifyOTP(getApplicationContext());
                    otp.execute(Url);

                    btnProceed.setEnabled(true);
                    btnProceed.setBackgroundResource(R.drawable.button_round_corner);


                } else {
                    Toast.makeText(UserCheckoutActivity.this, "Check OTP..", Toast.LENGTH_SHORT);
                }
            } else {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                );
                params.setMargins(0, 0, 0, 80);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
//                params.height=75;
//                params.width=450;
                final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
                int pixels = (int) (245 * scale + 0.5f);
                int heightpixel = (int) (38 * scale + 0.5f);
                params.height = heightpixel;
                params.width = pixels;

                btnProceed.setLayoutParams(params);
                coordinatorLayout.setVisibility(View.VISIBLE);
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                VerifyingOtp();
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
            ex.printStackTrace();
        }
    }

    protected void ValidateUserDetails() {
        try {

            String number = userNumber.getText().toString().trim();
            String address = UserAddress;//userAddress.getText().toString().trim();
            if (number.equals("") || number.equals(null)) {
                userNumber.requestFocus();
                userNumber.setError("Field Mandatory");
            } else if (Patterns.PHONE.matcher(number).matches() && number.length() == 10) {
                Log.d("User Number", number);
                isContactCorrect = true;
            } else {
                userNumber.requestFocus();
                userNumber.setError("Enter a valid number");

            }

            if (addhouseNo.getText().toString().length() == 0) {
                addhouseNo.requestFocus();
                addhouseNo.setError("Enter valid Address");
            } else if (Patterns.PHONE.matcher(address).matches() && addhouseNo.getText().toString().length() > 1) {
                Log.d("Address", address);
                addhouseNo.setError("Enter the Correct Address ");
                isAddressCorrect = false;
            } else {
                isAddressCorrect = true;
            }
            if (addStreet.getText().toString().length() == 0) {
                addStreet.requestFocus();
                addStreet.setError("Enter valid Address");
            } else if (Patterns.PHONE.matcher(address).matches() && addStreet.getText().toString().length() > 1) {
                Log.d("Address", address);
                addStreet.setError("Enter the Correct Address ");
                isAddressCorrect = false;
            } else {
                isAddressCorrect = true;
            }
            if (SelectedArea.length() == 0) {
                addArea.requestFocus();

            } else {
                isAddressCorrect = true;
            }
            if (addCity.getText().toString().length() == 0) {
                addCity.requestFocus();
                addCity.setError("Enter valid Address");
                isAddressCorrect = false;
            } else if (Patterns.PHONE.matcher(address).matches() && addCity.getText().toString().length() > 1) {
                Log.d("Address", address);
                addCity.setError("Enter the valid City ");
                isAddressCorrect = false;
            } else {
                isAddressCorrect = true;
            }


            if (userName.getText().toString().length() == 0) {
                userName.requestFocus();
                userName.setError("Enter valid Name");
            }
            Pattern ps = Pattern.compile("^[a-zA-Z ]+$");
            Matcher ms = ps.matcher(userName.getText().toString());
            boolean bs = ms.matches();

            if (bs == false) {
                userName.setError("Enter the Correct name");
            } else {
                isNameCorrect = true;
            }

            if (isNameCorrect == true && isContactCorrect == true && isAddressCorrect == true) {
                rdcod.setChecked(true);
                btnProceed.setEnabled(true);
                btnProceed.setBackgroundResource(R.drawable.button_round_corner);
            } else {
                rdpaytm.setChecked(false);
                btnProceed.setEnabled(false);
            }
        } catch (Exception ex) {
        }
    }

    public void setCode(String code) {
        otpEditText.setText(code.toString());
        otpEditText.setText(code);
        if (OTPResponse.equals(code.toString())) {
            isotpCorrect = true;
        } else {
            Log.d("Incorrect OTP", code);
        }
    }


    public void setLoginDetails() {
        try {
            /**
             * If user is logged in then the details will be pre-filled
             */
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            loginSharedPreferences = getSharedPreferences("LOGIN_DETAILS", Context.MODE_PRIVATE);
            isLoggedIn = loginSharedPreferences.getBoolean("isLoggedIn", false);
            Log.d("isLoggedIn :  ", isLoggedIn + "");

            selectedTime = solrdata.getcheckoutSelectedTime();
            ItemsArr = solrdata.getcheckoutItemsArr();
            TotalPrice = String.valueOf(solrdata.getGrandTotal());
            btnProceed.setText("Proceed to pay " + getResources().getString(R.string.Rupee) + String.format("%.2f", solrdata.getGrandTotal()));
            OrderNumber = solrdata.getOrderID();//randomize();

            if (isLoggedIn) {

                userNumber.setText(loginSharedPreferences.getString("number", null));
                userNumber.setEnabled(false);

                String ADDress = solrdata.getUserAddress();//loginSharedPreferences.getString("addressOne", null);
                if (ADDress == null || ADDress.toString().length() == 0) {
                    GetAddress(userNumber.getText().toString());
                    ADDress = solrdata.getUserAddress();//loginSharedPreferences.getString("addressOne", null);
                    Log.d("GET ADDRESS: ", ADDress);
                    solrdata.setUserNumber(userNumber.getText().toString());
                }
                solrdata.setUserName(loginSharedPreferences.getString("userName", null));
                UserAddress = ADDress.toString();

                if (isFromActivity.contains("UserProfile")) {
                    if (!SelectedAddress.isEmpty()) {
                        UserAddress = SelectedAddress;
                    }
                }
                userName.setText(solrdata.getUserName());
                userName.setEnabled(false);
                Log.d("ADdress ", UserAddress);

                if (UserAddress.length() > 0) {
                    String[] newarr = UserAddress.split(Pattern.quote("|"));
                    String[] addressArr = newarr[0].split(",");
                    if (addressArr.length > 0)
                        addhouseNo.setText(addressArr[0]);
                    else
                        addhouseNo.setText("");

                    if (addressArr.length > 1) {
                        StringBuilder stringBuilder = new StringBuilder();
                        String streetadd = "";
                        if (addressArr.length > 1)
                            streetadd = stringBuilder.append(addressArr[1]).append(" ").toString();

                        addStreet.setText(streetadd);
                    } else
                        addStreet.setText("");


                    if (newarr.length > 1)
                        SignInUsrArea = newarr[1];
                    else
                        SignInUsrArea = "";

                    if (newarr.length > 2)
                        addresslandmark.setText(newarr[2]);
                    else
                        addresslandmark.setText("");


//                    if (addressArr.length > 2)
//                        SignInUsrArea = addressArr[2];
//                    else
//                        SignInUsrArea = newarr[0];

                    if (newarr.length > 5)
                        SignInUsrCity = newarr[4];

                    addCity.setText(SignInUsrCity);

                } else {
                    ChangeAddress.setText("Add Address");
                }
                addhouseNo.setEnabled(false);
                addCity.setEnabled(false);
                addStreet.setEnabled(false);
                addArea.setEnabled(false);
                addresslandmark.setEnabled(false);
                CheckUserAddress();

                loginTextView.setVisibility(View.GONE);
                layoutAreaCity.setVisibility(View.VISIBLE);
                ChangeAddress.setVisibility(View.VISIBLE);
                if (solrdata != null) {
                    OTPResponse = solrdata.getOtpCode();
//                    otpEditText.setText(OTPResponse);
                    Log.d("setLoginDetails: OTP ", OTPResponse);
                }

            } else {

                ChangeAddress.setVisibility(View.GONE);
                loginTextView.setVisibility(View.VISIBLE);


            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private int getLocationPosition(String localityName) {
        if (localityName.equals("")) {
            return -1; //invalid locality name
        } else {
            for (int i = 0; i < selectedDeliveryAreaList.length; i++) {
                if (localityName.equals(selectedDeliveryAreaList[i])) {
                    return i;
                }
            }
        }
        return 0;
    }

    public void GetAddress(final String ContactNumber) {
        SharedPreferences.Editor editor;
        loginSharedPreferences = getSharedPreferences("LOGIN_DETAILS", Context.MODE_PRIVATE);
        editor = loginSharedPreferences.edit();
        try {
            if (ConnectionDetector.isConnectingToInternet(getApplicationContext())) {

                HttpURLConnection connection;
                String response = "";

                InputStream instream = null;
                JSONObject jObj = null;
                try {
                    String url = API_LINKS.GET_ADDRESS_URL + ContactNumber;
                    URL starterUrl = new URL(url);
                    connection = (HttpURLConnection) starterUrl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    if (connection.getResponseCode() == 200) {
                        instream = new BufferedInputStream(connection.getInputStream());
                        response = ConvertInputStream.toString(instream);
                    } else {
                        GetAddress(ContactNumber);
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    if (e.getMessage().contains("failed to connect")) {
                        new ConnectionDetector(UserCheckoutActivity.this).isServerNotRunning();
                    }
                }
                //parse string to jsonObject
                try {

                    if (response != null) {
                        jObj = new JSONObject(response);
                        JSONArray obj = jObj.getJSONArray("Address");

                        StringBuilder stringBuilder = new StringBuilder();
                        String UsrAddress[] = new String[obj.length()];
                        for (int k = 0; k < obj.length(); k++) {
                            String address = "", area = "", landmark = "", pincode = "", city = "", state = "";
                            JSONObject obj1 = obj.getJSONObject(k);
                            if (obj1.has("address"))
                                address = obj1.getString("address");
                            if (obj1.has("area"))
                                area = obj1.getString("area");
                            if (obj1.has("landmark"))
                                landmark = obj1.getString("landmark");
                            if (obj1.has("pincode"))
                                pincode = obj1.getString("pincode");
                            if (obj1.has("city"))
                                city = obj1.getString("city");
                            if (obj1.has("state"))
                                state = obj1.getString("state");

                            UsrAddress[k] = address + "|" + area + "|" + landmark + "|" + pincode + "|" + city + "|" + state;
                            ;
                        }
                        solrdata.setUserAddress(UsrAddress[0]);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getApplicationContext());
                builder.setTitle("Error !!");

                builder.setIcon(R.drawable.ic_portable_wifi_off_black_48dp);
                builder.setMessage("No Internet Connection!  ");


                builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        GetAddress(ContactNumber);
                    }
                });

                builder.show();
            }
        } catch (Exception ex) {
            GetAddress(ContactNumber);
        }

    }

    void sendOrder() {
        if (ConnectionDetector.isConnectingToInternet(UserCheckoutActivity.this)) {
            Log.d("Order", "Contact Number" + userNumber.getText().toString());
            FinalOrder order = new FinalOrder();
            //btnOrder.execute(makeUrl(Address,selectedTime,TotalPrice,Items));
            order.execute(API_LINKS.PLACE_ORDER_URL, (postOrder(DeliveryAddress, selectedTime, TotalPrice, ItemsArr, infoText, discount)).toString());


        } else {

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            );
            params.setMargins(0, 0, 0, 80);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);

            params.height = 75;
            params.width = 350;//280

            btnProceed.setLayoutParams(params);
            coordinatorLayout.setVisibility(View.VISIBLE);
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sendOrder();
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200) {
            setLoginDetails();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_checkout, menu);
        return true;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("Receiver State", "Unregistered");
        if (receiver != null) {
            try {
                getApplicationContext().unregisterReceiver(receiver);
            } catch (Exception ex) {
            }
        }
    }

    EditText etCouponCode;
    Button btnApply;
    ProgressBar pbLoading;

    TextView tvTotalAmount;
    TextView tvDiscountAmount;
    TextView tvServiceTaxAmount;
    TextView tvVatAmount;
    TextView tvGrandTotalAmount;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvHaveCoupon:
                final Dialog promoCodeDialog = new Dialog(this);
                promoCodeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                View promoCodeDialogView = LayoutInflater.from(this).inflate(R.layout.layout_coupon_dialog, null);

                etCouponCode = (EditText) promoCodeDialogView.findViewById(R.id.etPromoCode);
                etCouponCode.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
                pbLoading = (ProgressBar) promoCodeDialogView.findViewById(R.id.pbLoading);

                tvTotalAmount = (TextView) promoCodeDialogView.findViewById(R.id.tvSubTotalAmount);
                tvTotalAmount.setText(getResources().getString(R.string.Rupee) + (solrdata.subTotal - solrdata.discountAmount));

                tvDiscountAmount = (TextView) promoCodeDialogView.findViewById(R.id.tvDiscountAmount);
                tvDiscountAmount.setText(getResources().getString(R.string.Rupee) + solrdata.discountAmount);

                tvServiceTaxAmount = (TextView) promoCodeDialogView.findViewById(R.id.tvServiceTaxAmount);
                tvServiceTaxAmount.setText(getResources().getString(R.string.Rupee) + String.format("%.2f", solrdata.serviceTax * solrdata.subTotal / 100));

                tvVatAmount = (TextView) promoCodeDialogView.findViewById(R.id.tvVatAmount);
                tvVatAmount.setText(getResources().getString(R.string.Rupee) + String.format("%.2f", solrdata.vat * solrdata.subTotal / 100));

                tvGrandTotalAmount = (TextView) promoCodeDialogView.findViewById(R.id.tvGrandTotalAmount);
                tvGrandTotalAmount.setText(getResources().getString(R.string.Rupee) + String.format("%.2f", solrdata.getGrandTotal()));

                btnApply = (Button) promoCodeDialogView.findViewById(R.id.btnApply);
                btnApply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (etCouponCode.getText().toString().length() != 0) {
                            if (userNumber.getText().toString().length() != 0 && userNumber.getText().toString().length() == 10) {
                                btnApply.setClickable(false);
                                pbLoading.setVisibility(View.VISIBLE);

                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("coupon", etCouponCode.getText().toString());
                                    jsonObject.put("contact", userNumber.getText().toString());
                                    jsonObject.put("total", solrdata.subTotal);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Perfecto.with(UserCheckoutActivity.this)
                                        .fromUrl(API_LINKS.APPLY_COUPON_POST_URL)
                                        .ofTypePost(jsonObject)
                                        .connect(new OnNetworkRequest() {

                                            @Override
                                            public void onStart() {

                                            }

                                            @Override
                                            public void onSuccess(String response) {
                                                if (!response.equals("")) {
                                                    try {
                                                        final JSONObject parentObject = new JSONObject(response);
                                                        if (parentObject.getInt("Success") == RESPONSE_SUCCESS) {
                                                            delayedTask = new TimerTask() {
                                                                @Override
                                                                public void run() {
                                                                    promoCodeDialog.dismiss();
                                                                    try {
                                                                        printToastOnUIThread(mContext, "You got " + getResources().getString(R.string.Rupee) + parentObject.getInt("Discount") + " discountAmount");
                                                                        discount = parentObject.getInt("Discount");
                                                                        solrdata.discountAmount = discount;
                                                                        Log.d("Discount", String.valueOf(discount));
                                                                        runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                tvTotalAmount.setText(getResources().getString(R.string.Rupee) + solrdata.subTotal);
                                                                                tvGrandTotalAmount.setText(getResources().getString(R.string.Rupee) + String.format("%.2f", solrdata.getGrandTotal()));
                                                                                btnProceed.setText("Proceed to pay " + getResources().getString(R.string.Rupee) + String.format("%.2f", solrdata.getGrandTotal()));
                                                                            }
                                                                        });
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            };
                                                            delayTimer.schedule(delayedTask, DELAY_AMOUNT);
                                                        } else {
                                                            delayedTask = new TimerTask() {
                                                                @Override
                                                                public void run() {
                                                                    runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            pbLoading.setVisibility(View.INVISIBLE);
                                                                            btnApply.setClickable(true);
                                                                        }
                                                                    });
                                                                    printToastOnUIThread(mContext, "Invalid Coupon");
                                                                }
                                                            };
                                                            delayTimer.schedule(delayedTask, DELAY_AMOUNT);
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                } else {
                                                    delayedTask = new TimerTask() {
                                                        @Override
                                                        public void run() {
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    pbLoading.setVisibility(View.INVISIBLE);
                                                                    btnApply.setClickable(true);
                                                                }
                                                            });
                                                            printToastOnUIThread(mContext, "Cannot process your request at this time");
                                                        }
                                                    };
                                                    delayTimer.schedule(delayedTask, DELAY_AMOUNT);
                                                }
                                            }

                                            @Override
                                            public void onFailure(int i, String s, String s1) {
                                                Log.d("Failure", s);
                                                pbLoading.setVisibility(View.INVISIBLE);
                                                btnApply.setClickable(true);
                                            }
                                        });
                            } else {
                                Toast.makeText(UserCheckoutActivity.this, "Please be register on Tadqa", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(UserCheckoutActivity.this, "Enter a valid coupon", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                promoCodeDialog.setContentView(promoCodeDialogView);
                promoCodeDialog.show();
                break;
        }
    }

    public void printToastOnUIThread(final Context context, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    class GenerateOTP extends AsyncTask<String, String, Boolean> {

        Context context;
        String response;

        public GenerateOTP(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(UserCheckoutActivity.this);
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

                Log.d("doInBackground: ", otpURL.toString() + " | " + connection.getResponseCode());
                if (connection.getResponseCode() == 200) {

                    InputStream stream = new BufferedInputStream(connection.getInputStream());
                    response = ConvertInputStream.toString(stream);
//                    OTPResponse = responseString.split("\\:")[1].replaceAll("[^0-9.]", "");
//                    Log.d("OTP received : ",OTPResponse.toString());
                    return true;
                } else {

                    int responseCode = connection.getResponseCode();
                    Log.d("Error", String.valueOf(responseCode));
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

            JSONObject object = null;
            try {
                object = new JSONObject(response);
                if (object.getInt("Success") == 1) {
                    Toast.makeText(context, "Sending OTP... ", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Connection Error", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if ((dialog != null) && dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }


    class VerifyOTP extends AsyncTask<String, String, Boolean> {

        Context context;
        String response;

        public VerifyOTP(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(UserCheckoutActivity.this);
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

                Log.d("doInBackground: ", otpURL.toString() + " | " + connection.getResponseCode());
                if (connection.getResponseCode() == 200) {

                    InputStream stream = new BufferedInputStream(connection.getInputStream());
                    response = ConvertInputStream.toString(stream);

                    return true;
                } else {

                    int responseCode = connection.getResponseCode();
                    Log.d("Error", String.valueOf(responseCode));
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

            JSONObject object = null;
            try {
                object = new JSONObject(response);
                if (object.getInt("Success") == 1) {
                    isOTPverified = true;
                    Toast.makeText(context, "Verify OTP... ", Toast.LENGTH_SHORT).show();
                    if ((dialog != null) && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    sendOrder();
                } else if (object.getInt("Success") == 3) {
                    Toast.makeText(UserCheckoutActivity.this, "Wrong One Time Password", Toast.LENGTH_LONG).show();
                } else if (object.getInt("Success") == 4) {
                    Toast.makeText(UserCheckoutActivity.this, "invald inputs", Toast.LENGTH_LONG).show();
                } else if (object.getInt("Success") == 5) {
                    Toast.makeText(UserCheckoutActivity.this, "Server internal error!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Connection Error", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if ((dialog != null) && dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    public JSONObject postOrder(String Address, String selectedTime, String TotalPrice, JSONArray Items, String infotext, int discount) {
        Log.d("Items Arr: ", Items.toString());
        JSONObject object = new JSONObject();
        try {

            object.put("Contact", userNumber.getText().toString());
            object.put("Name", userName.getText().toString());
            object.put("OrderId", OrderNumber);
            object.put("DeliveryDateTime", selectedTime);

            String[] addarr = Address.split("\\|");
            Log.d("Post Address", Address);
            JSONObject ob1 = new JSONObject();
            try {

                ob1.put("address", addarr[0]);
                ob1.put("area", addarr[1]);
                ob1.put("landmark", addarr[2]);
                ob1.put("pincode", addarr[3]);
                ob1.put("city", addarr[4]);
                ob1.put("state", addarr[5]);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            object.put("DeliveryAddress", ob1);
            object.put("Items", Items);
            object.put("PaymentMode", "COD");
            object.put("Total", String.format(Locale.getDefault(), "%.2f", Float.valueOf(TotalPrice)));
            object.put("Discount", discount);
            object.put("AddInfo", CheckoutActivity.addinfo);
            Log.d("Sending data", object.toString());
            if (solrdata.isHomeMade)
                object.put("Outlet", "Tadqa Home Made");
            if (solrdata.isTadqaTandoorExpress)
                object.put("Outlet", "Tadqa Tandoor Express");

        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return object;

    }

    public class FinalOrder extends AsyncTask<String, String, Boolean> {

        int connectionTimeout = 20000;
        boolean isSocketTimeoutException = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(UserCheckoutActivity.this);
            dialog.setCancelable(false);
            dialog.setMessage("Cooking your Meal");
            dialog.setTitle("Please Wait");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);
                Log.d("Order Details", params[0]);
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

                Log.d("Register Post", url.toString());
                Log.d("Register String", params[1].toString());

                int Responsecode = connection.getResponseCode();
                Log.d("Order Response == ", Responsecode + "");
                if (connection.getResponseCode() == 200) {
                    Log.d("Order Punched", "get ready for your meal");
                    return true;
                } else {
                    Log.d("Error", String.valueOf(Responsecode));
                    return false;
                }


            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                isSocketTimeoutException = true;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean condition) {
            super.onPostExecute(condition);

            if (condition == true) {
                dialog.dismiss();
                Intent intent = new Intent(UserCheckoutActivity.this, SuccessOrderActivity.class);
                intent.putExtra("OrderId", OrderNumber);
                UserCheckoutActivity.this.finish();
                startActivity(intent);
            } else {
                if (!ConnectionDetector.isConnectingToInternet(UserCheckoutActivity.this)) {

                    // Toast.makeText(CheckoutActivity.this, "Sorry unable to process the btnOrder ", Toast.LENGTH_SHORT);

                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(UserCheckoutActivity.this);
                    builder.setTitle("Error !");

                    builder.setIcon(R.drawable.ic_portable_wifi_off_black_48dp);
                    builder.setMessage("unable to process the btnOrder \n No Internet Connection!  ");


                    builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.show();
                } else {
                    if (isSocketTimeoutException) {
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(UserCheckoutActivity.this);
                        builder.setTitle("Connection Timed Out !");

                        builder.setIcon(R.drawable.ic_portable_wifi_off_black_48dp);
                        builder.setMessage("To process the btnOrder \n Please try again!  ");


                        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                sendOrder();
                            }
                        });

                        builder.show();
                    }
                }

            }
            if ((dialog != null) && dialog.isShowing()) {
                dialog.dismiss();
            }

        }

    }

    class paytmMerchant {


        public paytmMerchant(String OrderId, String ContactNo, String Amount, String Email) {

            onStartTransaction(OrderId, ContactNo, Amount, Email);

        }

        public void onStartTransaction(String OrderId, String contact_number, String Amount, String Email) {

            PaytmPGService Service = PaytmPGService.getProductionService();//.getStagingService();
            Map<String, String> paramMap = new HashMap<String, String>();

            // these are mandatory parameters


            paramMap.put("ORDER_ID", OrderId);
            paramMap.put("MID", getResources().getString(R.string.ProductionMID));//.test_staging_merchantID));
            paramMap.put("CUST_ID", contact_number);
            paramMap.put("CHANNEL_ID", getResources().getString(R.string.ProductionChannel_ID));//test_channel_id));
            paramMap.put("INDUSTRY_TYPE_ID", getResources().getString(R.string.ProductionIndustry_ID));//.test_industrytype_id));
            paramMap.put("WEBSITE", getResources().getString(R.string.ProductionWebsiteWAP));//test_website));
            paramMap.put("TXN_AMOUNT", Amount);
            paramMap.put("THEME", getResources().getString(R.string.test_theme));
            paramMap.put("EMAIL", Email);
            paramMap.put("MOBILE_NO", contact_number);
            PaytmOrder Order = new PaytmOrder(paramMap);


            PaytmMerchant Merchant = new PaytmMerchant(API_LINKS.GENERATE_CHECKSUM_URL,
                    API_LINKS.VERIFY_CHECKSUM_URL
            );
            Service.initialize(Order, Merchant, null);

            Service.startPaymentTransaction(UserCheckoutActivity.this, false, true, new PaytmPaymentTransactionCallback() {
                @Override
                public void someUIErrorOccurred(String inErrorMessage) {
                    // Some UI Error Occurred in Payment Gateway Activity.
                    // // This may be due to initialization of views in
                    // Payment Gateway Activity or may be due to //
                    // initialization of webview. // Error Message details
                    // the error occurred.
                    Log.d("Error", inErrorMessage);
                }


                @Override
                public void onTransactionSuccess(Bundle inResponse) {
                    // After successful transaction this method gets called.
                    // // Response bundle contains the merchant responseString
                    // parameters.

                    Log.d("Response", "Payment Transaction is successful " + inResponse);
                    Toast.makeText(getApplicationContext(), "Payment Transaction is successful ", Toast.LENGTH_LONG).show();
                    sendOrder();
                }

                @Override
                public void onTransactionFailure(String inErrorMessage,
                                                 Bundle inResponse) {
                    // This method gets called if transaction failed. //
                    // Here in this case transaction is completed, but with
                    // a failure. // Error Message describes the reason for
                    // failure. // Response bundle contains the merchant
                    // responseString parameters.
                    Log.d("Resonse", "Payment Transaction Failed " + inErrorMessage);
                    Toast.makeText(getApplicationContext(), "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
                }

                @Override
                public void networkNotAvailable() { // If network is not
                    // available, then this
                    // method gets called.
                }

                @Override
                public void clientAuthenticationFailed(String inErrorMessage) {
                    // This method gets called if client authentication
                    // failed. // Failure may be due to following reasons //
                    // 1. Server error or downtime. // 2. Server unable to
                    // generate checksum or checksum responseString is not in
                    // proper format. // 3. Server failed to authenticate
                    // that client. That is value of payt_STATUS is 2. //
                    // Error Message describes the reason for failure.
                }

                @Override
                public void onErrorLoadingWebPage(int iniErrorCode,
                                                  String inErrorMessage, String inFailingUrl) {


                    Log.d("on wEBlOADING ....", "Payment" + inFailingUrl + "," + iniErrorCode);

                }

                // had to be added: NOTE
                @Override
                public void onBackPressedCancelTransaction() {
                    // TODO Auto-generated method stub

                    Log.d("onBack Press....", "Payment onBackPressedCancelTransaction ");
                }


            });

        }
    }


}

