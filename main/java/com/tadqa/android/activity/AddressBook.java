package com.tadqa.android.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tadqa.android.R;
import com.tadqa.android.accessibility.ConvertInputStream;
import com.tadqa.android.accessibility.SolrData;
import com.tadqa.android.accessibility.TypeFaceUtil;
import com.tadqa.android.pojo.LocationModel;
import com.tadqa.android.util.API_LINKS;
import com.tadqa.android.util.ConnectionDetector;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AddressBook extends AppCompatActivity {

    Toolbar appBar;
    ProgressDialog dialog;
    SolrData solrdata;
    TextView tvaddAddress;
    String FromActivity="";
    ListView listview;
    ArrayList<LocationModel>list=new ArrayList<>();
    SharedPreferences loginSharedPrefrences;
    String ContactNumber="";
    String TAG="AddressBook";

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addressbook);

        // Calling Application class (see application tag in AndroidManifest.xml)
        solrdata = (SolrData) getApplication().getApplicationContext();
        appBar = (Toolbar) findViewById(R.id.app_bar);
        listview = (ListView) findViewById(R.id.list_viewaddress);
        tvaddAddress = (TextView) findViewById(R.id.addbtnAddress);
        TypeFaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Roboto-Regular.ttf");
        appBar.setTitle("Address Book");
        TextView title = (TextView) findViewById(R.id.txtadbook);

        TypeFaceUtil.overrideFont(getApplication(), "SERIF", "fonts/Roboto-Regular.ttf");
        Intent intent = getIntent();
        if (intent.getExtras().containsKey("From"))
            FromActivity = intent.getStringExtra("From");
        if (FromActivity.contains("CheckoutActivity")) {
            title.setText("Choose Your Address");
        } else
            title.setText("Your Address Book");

        tvaddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ins = new Intent(getApplicationContext(), AddAddress.class);
                ins.putExtra("From", FromActivity);
                startActivity(ins);
            }
        });

        loginSharedPrefrences = getSharedPreferences("LOGIN_DETAILS", Context.MODE_PRIVATE);
        ContactNumber = loginSharedPrefrences.getString("number", "");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        GetAddress();
    }

    public void GetAddress()
    {
        try {
            if (ConnectionDetector.isConnectingToInternet(this)) {

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
                        GetAddress();
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    if (e.getMessage().contains("failed to connect")) {
                        new ConnectionDetector(AddressBook.this).isServerNotRunning();
                    }
                }
                //parse string to jsonObject
                try {

                    if (response != null) {
                        jObj = new JSONObject(response);
                        if (jObj.has("Success")) {
                            if (jObj.getInt("Success") == 1) {
                                if (jObj.has("Address")) {
                                    JSONArray obj = jObj.getJSONArray("Address");
                                    LocationModel newaddress;
                                    StringBuilder stringBuilder = new StringBuilder();
                                    String UsrAddress[] = new String[obj.length()];
                                    for (int k = 0; k < obj.length(); k++) {
                                        newaddress = new LocationModel();
                                        JSONObject obj1 = obj.getJSONObject(k);
                                        if (obj1.has("address"))
                                            newaddress.setAddress(obj1.getString("address"));
                                        if (obj1.has("area"))
                                            newaddress.setArea(obj1.getString("area"));
                                        if (obj1.has("landmark"))
                                            newaddress.setLandmark(obj1.getString("landmark"));
                                        if (obj1.has("pincode"))
                                            newaddress.setPostal(obj1.getString("pincode"));
                                        if (obj1.has("city"))
                                            newaddress.setCity(obj1.getString("city"));
                                        if (obj1.has("state"))
                                            newaddress.setState(obj1.getString("state"));

                                        list.add(newaddress);

                                        UsrAddress[k] = (newaddress.getAddress()+"|"+newaddress.getArea() + "|" + newaddress.getLandmark() + "|" + newaddress.getPostal() + "|" + newaddress.getCity() + "|" + newaddress.getState());
                                    }
                                    listview.setAdapter(new AddressAdapter(getApplicationContext(), list));
                                    solrdata.setUserAddress(UsrAddress[0]);
//
                                }
                            }
                            else if(jObj.getInt("Success") == 0) {
                                Toast.makeText(getApplication(), "Record Not Found!", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Response=0 Record Not Found");
                            }
                            else if(jObj.getInt("Success") == 4) {
                                Toast.makeText(getApplication(), "Invalid User!", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Response=4 Invalid User");
                            }
                            else if(jObj.getInt("Success") == 5) {
                                Log.d(TAG, "Response=5 Internal Server Error!");
                            }
                            else if(jObj.getInt("Success") == 6) {
                                Log.d(TAG, "Response=6 Routing Error!");
                            }
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
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

                        GetAddress();
                    }
                });

                builder.show();
            }
        }
        catch (Exception ex)
        {}

    }
    public class AddressAdapter extends BaseAdapter
    {
        Context context;
        List<LocationModel> itemList;

        public AddressAdapter(Context con, List<LocationModel> list) {
            context = con;
            itemList = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ItemHolder holder;

            if (convertView == null) {

                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.layout_address, null);
                holder = new ItemHolder();
                holder.rdgroup=(RadioGroup) convertView.findViewById(R.id.addSelection);
                holder.selectRadio=(RadioButton) convertView.findViewById(R.id.selectAddress);
                holder.selectRadio1=(RadioButton) convertView.findViewById(R.id.selectAddress1);
                holder.HnoStreetno = (TextView) convertView.findViewById(R.id.idhnoStreet);
                holder.area = (TextView) convertView.findViewById(R.id.idarea);
                holder.City = (TextView) convertView.findViewById(R.id.idcity);
                holder.State = (TextView) convertView.findViewById(R.id.idstate);
                holder.imgEdit=(ImageView) convertView.findViewById(R.id.idimgEdit);

                convertView.setTag(holder);
            } else {
                holder = (ItemHolder) convertView.getTag();
            }

            if(FromActivity.contains("CheckoutActivity")) {
                holder.rdgroup.setVisibility(View.VISIBLE);
                holder.selectRadio.setVisibility(View.VISIBLE);
                if (position == 0) {
                    holder.selectRadio.isChecked();
                    holder.selectRadio.setTag(position);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.selectRadio.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.tadka_green)));
                }
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {

                    holder.selectRadio1.setVisibility(View.VISIBLE);
                    holder.selectRadio.setVisibility(View.GONE);
                    // Checked change Listener for RadioGroup 1
                    holder.rdgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {

                            switch (checkedId) {
                                case R.id.selectAddress:
                                    Toast.makeText(getApplicationContext(), "Android", Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.selectAddress1:
                                    notifyDataSetChanged();
                                    if (list.size() > 0) {
                                        LocationModel selectedaddress = list.get(position);
                                        String newAdd = selectedaddress.getAddress()+"|"+selectedaddress.getArea()+"|"+selectedaddress.getLandmark()+"|"+selectedaddress.getPostal()+"|"+selectedaddress.getCity()+"|"+selectedaddress.getState();
                                        Log.d("Selected Radio Address", newAdd + "," + position);

                                        Intent intent = new Intent(AddressBook.this, UserCheckoutActivity.class);
                                        intent.putExtra("From", "UserProfile");
                                        intent.putExtra("SelectedAddress", newAdd);

                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                    break;
                                default:
                                    break;

                            }
                        }
                    });
                }
                else
                {
                    holder.selectRadio.setVisibility(View.VISIBLE);
                    holder.selectRadio1.setVisibility(View.GONE);

                    holder.selectRadio.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            v.setSelected(true);
                            notifyDataSetChanged();
                            if(list.size() >0) {
                                LocationModel selectedaddress = list.get(position);
                                String newAdd = selectedaddress.getAddress()+"|"+selectedaddress.getArea()+"|"+selectedaddress.getLandmark()+"|"+selectedaddress.getPostal()+"|"+selectedaddress.getCity()+"|"+selectedaddress.getState();
                                Log.d("Selected Radio Address", newAdd + "," + position);

                                Intent intent = new Intent(AddressBook.this, UserCheckoutActivity.class);
                                intent.putExtra("From", "UserProfile");
                                intent.putExtra("SelectedAddress", newAdd);

                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }


                        }
                    });
                }

            }
            else {

                convertView.setPadding(20,0,0,0);
                holder.rdgroup.setVisibility(View.GONE);
                holder.selectRadio.setVisibility(View.GONE);
            }
            final LocationModel CompleteAddress=list.get(position);

            if(CompleteAddress.getAddress().toString().length()>0)
                holder.HnoStreetno.setText("House No. / Flat No : "+ CompleteAddress.getAddress());
            else
                holder.HnoStreetno.setText("");

            if(CompleteAddress.getLandmark().toString().length()>1)
                holder.area.setText(CompleteAddress.getLandmark() +", "+ CompleteAddress.getArea());
            else
                holder.area.setText(CompleteAddress.getArea());

            if(CompleteAddress.getCity().toString().length() >0) {
                holder.City.setText(CompleteAddress.getCity());
                if (CompleteAddress.getPostal().length() > 3)
                    holder.City.setText(CompleteAddress.getCity() + ", " + CompleteAddress.getPostal());
            }
            else
                holder.City.setText("");
            if(CompleteAddress.getState().toString().length()>0) {
                holder.State.setText(CompleteAddress.getState());
            }
            else
                holder.State.setText("");



            holder.imgEdit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    String str=FromActivity+"$"+position+"|"+(CompleteAddress.getAddress() +"|"+CompleteAddress.getArea()+"|"+CompleteAddress.getLandmark()+"|"+CompleteAddress.getCity()+"|"+CompleteAddress.getState());
                    Intent editaddressIntent = new Intent(getApplicationContext(), AddAddress.class);
                    editaddressIntent.putExtra("From", str);
                    startActivity(editaddressIntent);

                }
            });

            return convertView;
        }
    }
    static class ItemHolder {
        TextView HnoStreetno,area,City,State;
        ImageView imgEdit;
        RadioButton selectRadio;
        RadioButton selectRadio1;
        RadioGroup rdgroup;
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!FromActivity.contains("CheckoutActivity"))
        {
            Intent intent = new Intent(AddressBook.this, UserProfile.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

}


