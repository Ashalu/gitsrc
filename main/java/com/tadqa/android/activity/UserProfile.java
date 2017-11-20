package com.tadqa.android.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tadqa.android.util.API_LINKS;
import com.tadqa.android.util.ConnectionDetector;
import com.tadqa.android.accessibility.ConvertInputStream;
import com.tadqa.android.accessibility.SolrData;
import com.tadqa.android.R;

import org.apache.http.client.ClientProtocolException;
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
import java.net.URL;
import java.util.Calendar;

public class UserProfile extends AppCompatActivity {

    TextView userName, dateOfBirth;
    EditText editableName, contact, email;
    SharedPreferences loginSharedPrefrences;
    SharedPreferences.Editor editor;
    Toolbar appBar;
    Button addressbook;
    FloatingActionButton saveButton;
    boolean isEditableModeOn = false;
    int DATE_PICKER_DIALOG = 1;
    int year, month, date;
    String dateOfBirthString;
    RadioGroup genderSelectionGroup;
    RadioButton genderButton, getGenderButtonMale, getGenderButtonFemale;
    ImageView imageView;
    String ContactNumber;
    SolrData solrdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ConnectionDetector.isConnectingToInternet(this)) {
            setContentView(R.layout.activity_user_profile);

            final Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            date = calendar.get(Calendar.DATE);

            //Hiding keyboard
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            loginSharedPrefrences = getSharedPreferences("LOGIN_DETAILS", Context.MODE_PRIVATE);
            editor = loginSharedPrefrences.edit();
            ContactNumber = loginSharedPrefrences.getString("number", "");

            appBar = (Toolbar) findViewById(R.id.toolbar);
            appBar.setTitle("Profile");
            setSupportActionBar(appBar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            solrdata=(SolrData)getApplicationContext();

            saveButton = (FloatingActionButton) findViewById(R.id.saveCredential);
            userName = (TextView) findViewById(R.id.userName);
            contact = (EditText) findViewById(R.id.phone);
            editableName = (EditText) findViewById(R.id.userNameEditText);
            editableName.setFilters(new InputFilter[]{filter});

            addressbook = (Button) findViewById(R.id.addressbook);
            email = (EditText) findViewById(R.id.editText_email);
            imageView = (ImageView) findViewById(R.id.userProfileIcon);
            dateOfBirth = (TextView) findViewById(R.id.editText_dateOfBirth);
            dateOfBirth.setEnabled(false);
            genderSelectionGroup = (RadioGroup) findViewById(R.id.genderSelection);
            getGenderButtonMale = (RadioButton) findViewById(R.id.male);
            getGenderButtonFemale = (RadioButton) findViewById(R.id.female);
            genderSelectionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    if (isEditableModeOn) {
                        switch (checkedId) {
                            case R.id.male:
                                imageView.setImageDrawable(getResources().getDrawable(R.drawable.user_icon_male));
                                break;

                            case R.id.female:
                                imageView.setImageDrawable(getResources().getDrawable(R.drawable.user_icon_female));
                                break;
                        }
                    }
                }
            });

            if (loginSharedPrefrences.getBoolean("isLoggedIn", true)) {


                contact.setText(ContactNumber);
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                String Uname= loginSharedPrefrences.getString("userName", "");
                if(Uname.length() >0 && !Uname.isEmpty())
                {
                    userName.setText(Uname);
                    editableName.setText(Uname);
                    email.setText(loginSharedPrefrences.getString("email", ""));
                    if (loginSharedPrefrences.getString("dob", "").equals("") || loginSharedPrefrences.getString("dob", "").equals(null)) {

                        StringBuilder stringBuilder = new StringBuilder();
                        dateOfBirthString = stringBuilder.append(month + 1).append("-")
                                .append(date).append("-")
                                .append(year).append(" ").toString();
                        dateOfBirth.setText(dateOfBirthString);
                    } else {

                        dateOfBirthString = loginSharedPrefrences.getString("dob", "");
                        dateOfBirth.setText(dateOfBirthString);
                    }

                    if (loginSharedPrefrences.getString("gender", "Male").equals("Male")) {
                        genderSelectionGroup.check(R.id.male);
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.user_icon_male));
                    } else {
                        genderSelectionGroup.check(R.id.female);
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.user_icon_female));
                    }
                }
                else {
                    GetUserProfile();
                }

            }

            dateOfBirth.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (isEditableModeOn) {
                        showDialog(DATE_PICKER_DIALOG);
                    }
                }
            });

            addressbook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent addbook=new Intent(getApplicationContext(),AddressBook.class);
                    addbook.putExtra("From", "UserProfile");
                    startActivity(addbook);
                }
            });

            //By default editable mode is off
            editableModeOff();
            saveButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (!validate()) {
                       Toast toast= Toast.makeText(getApplicationContext(), "Enter correct details", Toast.LENGTH_SHORT);//.show();
                        solrdata.setToast(toast);
                        toast.show();
                    } else {

                        editableModeOff();
                        int genderSelection = genderSelectionGroup.getCheckedRadioButtonId();
                        genderButton = (RadioButton) findViewById(genderSelection);

                        StringBuilder stringBuilder = new StringBuilder();
                        String dob = stringBuilder.append(dateOfBirthString).toString();//.append("T00:00:00Z").toString();
                        String Name=Uri.encode(editableName.getText().toString());
                        String Email=Uri.encode(email.getText().toString());
                        String Url = API_LINKS.UPDATE_PROFILE_URL+"Contact=" + ContactNumber
                                + "&Name=" + Name + "&Email=" + Email + "&Gender=" +
                                genderButton.getText().toString() + "&Birthday=" + dob;
                        UpdateUserProfile(Url,"Profile");
                        Intent intent = new Intent(UserProfile.this, ChooseLocality.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
            });
        }
            else
            {
                setContentView(R.layout.netconnection);
            }

    }


    protected void GetUserProfile() {
        try {
            if (ConnectionDetector.isConnectingToInternet(this)) {

                HttpURLConnection connection;
                String response = "";

                InputStream instream = null;
                JSONObject jObj = null;
                try {
                    String url = API_LINKS.GET_USER_PROFILE_URL + ContactNumber;
                    URL starterUrl = new URL(url);
                    connection = (HttpURLConnection) starterUrl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    if (connection.getResponseCode() == 200) {
                        instream = new BufferedInputStream(connection.getInputStream());
                        response = ConvertInputStream.toString(instream);
                    } else {
                        GetUserProfile();
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    if (e.getMessage().contains("failed to connect")) {
                        new ConnectionDetector(UserProfile.this).isServerNotRunning();
                    }
                }
                //parse string to jsonObject


                if (response != null) {
                    try {
                        jObj = new JSONObject(response);
                        if (jObj.has("Success")) {
                            if (jObj.getInt("Success") == 1) {
                                JSONObject obj = jObj.getJSONObject("user");
                                String name = obj.getString("Name");
                                String Dob = obj.getString("Birthday").split("T")[0];
                                String Email = obj.getString("Email");
                                String Gender = obj.getString("Gender");

                                editor.putString("userName", name);
                                editor.putString("email", Email);
                                editor.putString("dob", Dob);
                                editor.putString("gender", Gender);
                                editor.apply();

                                userName.setText(loginSharedPrefrences.getString("userName", ""));
                                editableName.setText(loginSharedPrefrences.getString("userName", ""));
                                email.setText(loginSharedPrefrences.getString("email", ""));
                                if (loginSharedPrefrences.getString("dob", "").equals("") || loginSharedPrefrences.getString("dob", "").equals(null)) {

                                    StringBuilder stringBuilder = new StringBuilder();
                                    dateOfBirthString = stringBuilder.append(month + 1).append("-")
                                            .append(date).append("-")
                                            .append(year).append(" ").toString();
                                    dateOfBirth.setText(dateOfBirthString);
                                } else {

                                    dateOfBirthString = loginSharedPrefrences.getString("dob", "");
                                    dateOfBirth.setText(dateOfBirthString);
                                }

                                if (loginSharedPrefrences.getString("gender", "Male").equals("Male")) {
                                    genderSelectionGroup.check(R.id.male);
                                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.user_icon_male));
                                } else {
                                    genderSelectionGroup.check(R.id.female);
                                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.user_icon_female));
                                }
                            }
                            else if (jObj.getInt("Success") == 0) {
                                Toast.makeText(getApplication(), "User Not Found !", Toast.LENGTH_LONG).show();
                            }
                            else if (jObj.getInt("Success") == 5) {
                                Toast.makeText(getApplication(), "Internal Server Error", Toast.LENGTH_LONG).show();
                            }
                            else if (jObj.getInt("Success") == 6) {
                                Toast.makeText(getApplication(), "Routing Error", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setTitle("Error !!");

                builder.setIcon(R.drawable.ic_portable_wifi_off_black_48dp);
                builder.setMessage("No Internet Connection!  ");


                builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        GetUserProfile();
                    }
                });

                builder.show();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    protected Dialog onCreateDialog(int id) {

        switch (id) {
            case 1:
                return new DatePickerDialog(UserProfile.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonthOfYear, int selectedDayOfMonth) {
                        year = selectedYear;
                        month = selectedMonthOfYear;
                        date = selectedDayOfMonth;

                        StringBuilder stringBuilder = new StringBuilder();
                        dateOfBirthString = stringBuilder.append(year).append("-")
                                .append(month + 1).append("-")
                                .append(date).toString();

                        dateOfBirth.setText(dateOfBirthString);
                    }
                }, year, month, date);

        }

        return null;
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



    private boolean validate() {
        if (userName.getText().toString().trim().equals(""))
            return false;
        else if (contact.getText().toString().trim().equals("") && !Patterns.PHONE.matcher(contact.getText()).matches())
            return false;
        else if (email.getText().toString().trim().equals("") || !Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches())
            return false;
        return true;
    }

    public void editableModeOn() {

        isEditableModeOn = true;

        userName.setVisibility(View.INVISIBLE);
        editableName.setVisibility(View.VISIBLE);
        editableName.setEnabled(true);
       // addressbook.setEnabled(true);
        saveButton.setVisibility(View.VISIBLE);
        email.setEnabled(true);
        dateOfBirth.setEnabled(true);
        getGenderButtonMale.setEnabled(true);
        getGenderButtonFemale.setEnabled(true);
        genderSelectionGroup.setEnabled(true);
        saveButton.animate().alphaBy(0).alpha(100).setInterpolator(new DecelerateInterpolator(2)).start();
    }

    public void editableModeOff() {

        isEditableModeOn = false;

        userName.setText(loginSharedPrefrences.getString("userName", ""));
        userName.setVisibility(View.VISIBLE);
        editableName.setVisibility(View.INVISIBLE);
        editableName.setEnabled(false);
       // addressbook.setEnabled(false);
        contact.setEnabled(false);
        saveButton.setVisibility(View.INVISIBLE);
        email.setEnabled(false);
        genderSelectionGroup.setEnabled(false);
        getGenderButtonMale.setEnabled(false);
        getGenderButtonFemale.setEnabled(false);

    }


    public void editUserPassword() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(UserProfile.this);

        LayoutInflater dialogLayoutInterface = getLayoutInflater();
        final View view = dialogLayoutInterface.inflate(R.layout.custom_dialog_layout, null);
        dialog.setView(view);
        dialog.setTitle("Change password");

        dialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                final EditText pswd = (EditText) view.findViewById(R.id.new_password);
                final EditText crntpswd = (EditText) view.findViewById(R.id.current_password);
                final String oldPassword=crntpswd.getText().toString();
                final String Newpassword = pswd.getText().toString();


                       JSONObject profile = new JSONObject();
                        try {

                            profile.put("Contact", ContactNumber);
                            profile.put("OldPassword", oldPassword);
                            profile.put("NewPassword", Newpassword);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    UpdateUserProfile(API_LINKS.CHANGE_PASSWORD_URL,profile.toString());


            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }


    protected void UpdateUserProfile(final String Url,final String Jsondata)
    {
        try
        {
            if(ConnectionDetector.isConnectingToInternet(this)) {
                new EditProfile().execute(Url,Jsondata);
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setTitle("Error !!");

                builder.setIcon(R.drawable.ic_portable_wifi_off_black_48dp);
                builder.setMessage("No Internet Connection!  ");


                builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        UpdateUserProfile(Url,Jsondata);
                    }
                });

                builder.show();
            }
        }
        catch (Exception ex)
        {ex.printStackTrace();}
    }

    ProgressDialog pDialog;
    private void showProgressDialog() {
        if (pDialog == null) {
            pDialog = new ProgressDialog(UserProfile.this);
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


    public class EditProfile extends AsyncTask<String, String, String> {

        InputStream stream;
        String response = "failed";
        boolean isfromProfile=false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {


            try {
                HttpURLConnection connection;
                URL url = new URL(params[0]);

                if(params[1].contains("Profile")) {

                    isfromProfile=true;
                    Log.d("User Profile ", url.toString());
                     connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                }
                else {
                    connection = (HttpURLConnection) url.openConnection();
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

                    Log.d("ChangePasssword Post", url.toString());
                    Log.d("ChangePasssword String", params[1].toString());
                }




                if (connection.getResponseCode() == 200) {
                    stream = new BufferedInputStream(connection.getInputStream());
                    response = ConvertInputStream.toString(stream);
                    Log.d("RESPONSE",response);
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
            try {
                responseJson = new JSONObject(s);
                if (responseJson.has("Success")) {
                    if (responseJson.getInt("Success") == 1) {

                        if (responseJson.has("Response"))
                            Toast.makeText(getApplication(), responseJson.getString("Response"), Toast.LENGTH_LONG).show();
                        else {
                            Toast toast = Toast.makeText(UserProfile.this, "Successful Updated ", Toast.LENGTH_SHORT);//.show();
                            solrdata.setToast(toast);
                            toast.show();
                        }
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            dismissProgressDialog();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_profile_menu, menu);
        return super.onCreateOptionsMenu(menu);
}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.edit_user_details:
                editableModeOn();
                return true;

            case R.id.edit_user_password:
                editUserPassword();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

}



