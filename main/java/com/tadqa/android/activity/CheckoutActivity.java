package com.tadqa.android.activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tadqa.android.R;
import com.tadqa.android.accessibility.ConvertInputStream;
import com.tadqa.android.accessibility.SolrData;
import com.tadqa.android.accessibility.TypeFaceUtil;
import com.tadqa.android.pojo.CheckoutModel;
import com.tadqa.android.pojo.LocationModel;
import com.tadqa.android.util.API_LINKS;
import com.tadqa.android.util.ConnectionDetector;
import com.tadqa.android.util.DatabaseHelper;
import com.tadqa.android.util.NonScrollListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import developer.shivam.perfecto.OnNetworkRequest;
import developer.shivam.perfecto.Perfecto;

public class CheckoutActivity extends AppCompatActivity {

    private final String TAG = "CheckoutActivity";
    Cursor cursor, cursorTwo;
    private String TABLE_NAME = "CART_DATA";
    private String CART_ITEM_QUANTITY = "CART_ITEM_QUANTITY";
    DatabaseHelper helper;
    CustomAdapter adapter;
    Toolbar appBar;
    NonScrollListView checkoutList;
    List<CheckoutModel> list;
    List<Integer> quantity;
    LinearLayout emptyCartLayout;
    TextView totalAmount;
    TextView tvVatAmount, tvServiceTaxAmount, tvGrandTotal, Discount;
    Button btnOrder;
    ProgressDialog dialog;
    Button applyCode;
    SharedPreferences loginSharedPrefrences;
    SolrData solrdata;
    EditText additionalInfo, coupon;
    String selectedTime;
    Calendar calendar;
    SimpleDateFormat dateFormat;
    String time;
    Date NextDate;

    /**
     * Order Details
     */
    int discountAmount = 0;
    double serviceTaxAmount = 0;
    double vatAmount = 0;

    String number = "";
    CoordinatorLayout coordinatorLayout;
    public static String addinfo;
    private Context mContext = CheckoutActivity.this;
    private FetchOrderId fetchOrderIdRequest;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        TypeFaceUtil.overrideFont(getApplication(), "SERIF", "fonts/Roboto-Regular.ttf");

        solrdata = (SolrData) getApplication().getApplicationContext();

        appBar = (Toolbar) findViewById(R.id.app_bar);
        emptyCartLayout = (LinearLayout) findViewById(R.id.emptyCartLayout);
        totalAmount = (TextView) findViewById(R.id.totalAmount);
        loginSharedPrefrences = getSharedPreferences("LOGIN_DETAILS", Context.MODE_PRIVATE);
        number = loginSharedPrefrences.getString("number", "");
        tvVatAmount = (TextView) findViewById(R.id.VatTax);
        tvServiceTaxAmount = (TextView) findViewById(R.id.ServiceTax);
        tvGrandTotal = (TextView) findViewById(R.id.totalAmountPayable);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.snackbarCoordinatorLayout);
        btnOrder = (Button) findViewById(R.id.Next);
        additionalInfo = (EditText) findViewById(R.id.comments);
        Discount = (TextView) findViewById(R.id.Discount);

        /**
         * Helper is used to perform function on the database
         * like opening, closing, inserting, and fetching the
         * data.
         */
        try {

            helper = new DatabaseHelper(CheckoutActivity.this);
            helper.open();

            cursor = helper.getData(TABLE_NAME);
            cursor.moveToFirst();

            fetchCursorData();

            cursorTwo = helper.getData(CART_ITEM_QUANTITY);
        } catch (Exception e) {
            System.out.print(e);
        }

        setToolbarTitle();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        checkoutList = (NonScrollListView) findViewById(R.id.cartList);

        adapter = new CustomAdapter();
        checkoutList.setAdapter(adapter);

        btnOrder.setEnabled(false);
        btnOrder.setAlpha(0.5f);
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComposeOrder();

            }
        });

        time = calcFormattedTime();
        selectedTime = time;


        fetchOrderIdRequest = new FetchOrderId();
        fetchOrderIdRequest.execute();
    }

    protected String calcFormattedTime() {

        dateFormat = new SimpleDateFormat("hh:mm a");
        calendar = Calendar.getInstance();

        String time = dateFormat.format(calendar.getTime());
        Log.d("Current time ", time);

        try {
            String AMPM = "";
            if (time.contains("am"))
                AMPM = "am";
            else
                AMPM = "pm";
            Date date = calendar.getTime();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR, 1);
            time = dateFormat.format(calendar.getTime());
            NextDate = calendar.getTime();
            String[] tt = time.split("\\:");
            int minutes = calendar.getTime().getMinutes();
            String newMin = String.valueOf(calendar.getTime().getMinutes());

            int mod = minutes % 10;
            if (mod != 0) {
                String tm = String.valueOf(minutes);
                if (minutes < 15)
                    newMin = "00";
                else if (minutes > 45)
                    newMin = "00";
                else
                    newMin = "30";

            } else {
                if (minutes < 15)
                    newMin = "00";
                else if (minutes > 45)
                    newMin = "00";
                else
                    newMin = "30";

            }
            if (!date.after(NextDate)) {
                int hh = Integer.valueOf(tt[0]);
                if (time.toLowerCase().contains("am")) {
                    if (minutes > 45) {
                        if (time.contains("12")) {
                            hh = 00;
                            time = (hh + 1) + ":" + newMin + " pm";
                        } else if (time.contains("11")) {
                            time = (hh + 1) + ":" + newMin + " pm";
                        } else
                            time = (hh + 1) + ":" + newMin + " am";
                    } else
                        time = tt[0] + ":" + newMin + " am";
                } else {
                    if (minutes > 45) {
                        if (time.contains("12") && time.toLowerCase().contains("pm")) {
                            hh = 00;
                            time = (hh + 1) + ":" + newMin + " pm";
                        } else if (time.contains("11")) {
                            time = (hh + 1) + ":" + newMin + " am";
                        } else
                            time = (hh + 1) + ":" + newMin + " pm";
                    } else
                        time = tt[0] + ":" + newMin + " pm";
                }
            } else {
                int hh = 0;
                Log.d("NextDate: ", NextDate + " " + NextDate.getTime());

            }

            String[] timet = time.split("\\:");
            if (timet[0].length() == 1) {
                time = time.replace(timet[0], "0" + timet[0]);
            }
            Log.d("calcFormattedTime: ", time + " " + AMPM);

            return time;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return time;
    }

    void ComposeOrder() {

        if (ConnectionDetector.isConnectingToInternet(this)) {
            Log.d("Contact Number ", number);
            addinfo = additionalInfo.getText().toString();

            if (coordinatorLayout != null)
                coordinatorLayout.setVisibility(View.GONE);
            //solrData.setGrandTotal(String.valueOf(calculateTotalPrice()));
            solrdata.setcheckoutItemsArr(itemArrString());
            solrdata.setcheckoutSelectedTime(selectedTime.toString());

            System.out.println(solrdata.isHomeMade);
            System.out.println(solrdata.isTadqaTandoorExpress);

            if (solrdata.selectedDeliveryLocationArray[0] == null) {
                GetDeliveryAreaByOutLet obj = new GetDeliveryAreaByOutLet();
                if (solrdata.isHomeMade)
                    obj.execute(API_LINKS.GET_DELIVERY_AREA_BY_OUTLET_URL + "thm");
                if (solrdata.isTadqaTandoorExpress)
                    obj.execute(API_LINKS.GET_DELIVERY_AREA_BY_OUTLET_URL + "tte");

            } else {
                if (solrdata.isTadqaTandoorExpress) {
                    checkIsStoreOpen("tte");
                } else {
                    checkIsStoreOpen("thm");
                }
            }

        } else {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            );
            params.setMargins(0, 0, 0, 90);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);

            final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
            int pixels = (int) (245 * scale + 0.5f);
            int heightpixel = (int) (38 * scale + 0.5f);
            params.height = heightpixel;
            params.width = pixels;

            btnOrder.setLayoutParams(params);
            coordinatorLayout.setVisibility(View.VISIBLE);
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ComposeOrder();
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


    public void fetchCursorData() {
        /**
         * This code snippet will convert the cursor data into
         * two listOne "listOne" and "quantity".First listOne will contain
         * all about the item and second listOne only contains the
         * quantity.
         */
        list = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++) {
            CheckoutModel model = new CheckoutModel();
            cursor.moveToPosition(i);
            model.setItemId(cursor.getString(cursor.getColumnIndex("ITEM_ID")));
            model.setItemName(cursor.getString(cursor.getColumnIndex("ITEM_NAME")));
            model.setItemPrice(cursor.getInt(cursor.getColumnIndex("ITEM_PRICE")));
            model.setItemQuantity(cursor.getInt(cursor.getColumnIndex("ITEM_QUANTITY")));
            model.setItemImageUrl(cursor.getString(cursor.getColumnIndex("ITEM_IMAGE_URL")));
            model.setItemTotalPrice(cursor.getInt(cursor.getColumnIndex("ITEM_TOTAL_PRICE")));
            String Qtytype = cursor.getString(cursor.getColumnIndex("ITEM_TYPE"));
            // Log.d("fetchCursorData: ",Qtytype);
            model.setItemType(Qtytype);
            list.add(model);
        }

        quantity = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            quantity.add(cursor.getInt(cursor.getColumnIndex("ITEM_QUANTITY")));
        }

        totalAmount.setText(getResources().getString(R.string.Rupee) + String.valueOf(calculateTotalPrice()));

    }

    public class Holder {
        TextView name;
        TextView price;
        TextView quantity;
        TextView total_price;
        Button decrement, increment;
    }

    public class CustomAdapter extends BaseAdapter {

        Holder holder;

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

            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(CheckoutActivity.this).inflate(R.layout.checkout_item, null);

                holder.name = (TextView) convertView.findViewById(R.id.item_name);
                holder.price = (TextView) convertView.findViewById(R.id.price);
                holder.total_price = (TextView) convertView.findViewById(R.id.totalPrice);
                holder.quantity = (TextView) convertView.findViewById(R.id.item_quantity);
                holder.decrement = (Button) convertView.findViewById(R.id.decrement);
                holder.increment = (Button) convertView.findViewById(R.id.increment);

                convertView.setTag(holder);
            } else {

                holder = (Holder) convertView.getTag();
            }

            holder.name.setText(list.get(position).getItemName() + "  (" + list.get(position).getItemType() + ")");
            holder.price.setText(getResources().getString(R.string.Rupee) + list.get(position).getItemPrice());
            holder.total_price.setText(getResources().getString(R.string.Rupee) + String.valueOf(quantity.get(position) * list.get(position).getItemPrice()));
            holder.quantity.setText(String.valueOf(quantity.get(position)));

            holder.decrement.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int q = quantity.get(position);
                    if (q > 1) {
                        q -= 1;
                        quantity.set(position, q);
                        holder.quantity.setText(String.valueOf(quantity.get(position)));
                        holder.total_price.setText(String.valueOf(quantity.get(position) * list.get(position).getItemPrice()));
                        addItem(list.get(position).getItemId(),
                                list.get(position).getItemName(),
                                list.get(position).getItemPrice(),
                                q,
                                list.get(position).getItemImageUrl(),
                                list.get(position).getItemType());
                        addItemQuantity(list.get(position).getItemId(),
                                q,
                                list.get(position).getItemType());
                        totalAmount.setText(getResources().getString(R.string.Rupee) + String.valueOf(calculateTotalPrice()));
                        adapter.notifyDataSetChanged();
                    } else {
                        q = 0;
                        helper.removeWithType(TABLE_NAME, list.get(position).getItemId(), list.get(position).getItemType());
                        helper.removeWithType(CART_ITEM_QUANTITY, list.get(position).getItemId(), list.get(position).getItemType());
                        list.remove(position);
                        quantity.remove(position);
                        totalAmount.setText(getResources().getString(R.string.Rupee) + String.valueOf(calculateTotalPrice()));
                        adapter.notifyDataSetChanged();
                        setToolbarTitle();
                        isListEmpty();
                    }
                }
            });

            holder.increment.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int q = quantity.get(position);
                    q += 1;
                    quantity.set(position, q);
                    holder.total_price.setText(String.valueOf(quantity.get(position) * list.get(position).getItemPrice()));
                    holder.quantity.setText(String.valueOf(quantity.get(position)));
                    addItem(list.get(position).getItemId(),
                            list.get(position).getItemName(),
                            list.get(position).getItemPrice(),
                            q,
                            list.get(position).getItemImageUrl(),
                            list.get(position).getItemType());
                    addItemQuantity(list.get(position).getItemId(),
                            q,
                            list.get(position).getItemType());
                    totalAmount.setText(getResources().getString(R.string.Rupee) + String.valueOf(calculateTotalPrice()));
                    adapter.notifyDataSetChanged();
                }
            });

            return convertView;
        }

    }

    public int calculateTotalPrice() {

        /**
         * Calculating the total amount by
         *  adding all items price
         */
        int totalAmount = 0;
        for (int i = 0; i < list.size(); i++) {
            totalAmount += quantity.get(i) * list.get(i).getItemPrice();
        }

        solrdata.subTotal = totalAmount;

        /**
         * Calculating grand total amount by adding
         *  taxes
         */
        double vat = totalAmount * vatAmount / 100;
        double service = totalAmount * serviceTaxAmount / 100;

        tvVatAmount.setText(getResources().getString(R.string.Rupee) + String.format("%.2f", vat));
        tvServiceTaxAmount.setText(getResources().getString(R.string.Rupee) + String.format("%.2f", service));

        tvGrandTotal.setText(getResources().getString(R.string.Rupee) + String.format("%.2f", solrdata.getGrandTotalBeforeAddingDiscound()));
        return totalAmount;
    }


    public void addItem(String id, String name, int price, int quantity, String image_url, String type) {
        /**
         * It will addressOne items to the database with all
         * its data.
         */
        ContentValues values = new ContentValues();
        values.put("ITEM_ID", id);
        values.put("ITEM_NAME", name);
        values.put("ITEM_PRICE", price);
        values.put("ITEM_QUANTITY", quantity);
        values.put("ITEM_IMAGE_URL", image_url);
        values.put("ITEM_TOTAL_PRICE", quantity * price);
        values.put("ITEM_TYPE", type);
        helper.insertData(TABLE_NAME, values, id, type);
    }

    public void addItemQuantity(String id, int quantity, String type) {
        /**
         * It will addressOne items to database with only
         * item id and its corresponding quantity.
         */
        ContentValues values = new ContentValues();
        values.put("ITEM_ID", id);
        values.put("ITEM_QUANTITY", quantity);
        values.put("ITEM_TYPE", type);

        helper.insertCartItemQuantityData(CART_ITEM_QUANTITY, values, id, type);
    }

    public void isListEmpty() {

        /**
         * this method will check the size of the listOne,
         * is listOne is found to be empty it will set the visibility
         * of fragment to View.Visible and the fragment would be visible
         * indication your cart is empty.
         */
        if (list.size() == 0) {
            setToolbarTitle();
            emptyCartLayout.setVisibility(View.VISIBLE);
        }
    }

    public void setToolbarTitle() {
        /**
         * This method will set the toolbar title
         * according to the size of the listOne.
         */
        if (list.size() < 2) {
            appBar.setTitle(list.size() + " item in your cart.");
            setSupportActionBar(appBar);
        } else {
            appBar.setTitle(list.size() + " items in your cart.");
            setSupportActionBar(appBar);
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
        if (helper != null)
            helper.close();
    }

    public String getItemName(String item) {
        String name = "";
        for (char c : item.toCharArray()) {
            if (c == ' ') {
                c = '+';
                name += c;
            } else {
                name += c;
            }
        }
        return name;
    }

    public String itemString() {
        String item = "";

        for (int i = 0; i < list.size(); i++) {
            String singleItem = getItemName(list.get(i).getItemName()) + "," + quantity.get(i) + "," + list.get(i).getItemPrice() + "," + list.get(i).getItemPrice() + "," + "Self" + "|";
            item += singleItem;
        }
        return item;
    }

    public JSONArray itemArrString() {
        // String[] itemArr = new String[list.size()];
        JSONArray itemArr = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            JSONObject object = new JSONObject();
            try {
                object.put("ItemName", list.get(i).getItemName());
                object.put("Qty", quantity.get(i));
                object.put("Price", list.get(i).getItemPrice());
                object.put("Type", list.get(i).getItemType());
                itemArr.put(i, object);

                //String singleItem = (list.get(i).getItemName()) + "," + quantity.get(i) + "," + list.get(i).getItemPrice();
                //itemArr[i] = singleItem;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return itemArr;
    }

    class FetchOrderId extends AsyncTask<String, String, Boolean> {

        String responseString;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(mContext);
            dialog.setCancelable(false);
            dialog.setMessage("Please Wait");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                URL otpURL = new URL(API_LINKS.GET_ORDER_ID_URL);
                HttpURLConnection connection = (HttpURLConnection) otpURL.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                Log.d("doInBackground: ", otpURL.toString() + " | " + connection.getResponseCode());
                if (connection.getResponseCode() == 200) {
                    InputStream stream = new BufferedInputStream(connection.getInputStream());
                    responseString = ConvertInputStream.toString(stream);
                    return true;
                } else {
                    int responseCode = connection.getResponseCode();
                    Log.d("Error", String.valueOf(responseCode));
                    return false;
                }


            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean response) {
            super.onPostExecute(response);

            if (response)
            try {
                JSONObject parentObject = new JSONObject(this.responseString);
                System.out.println(parentObject);
                if (parentObject.getInt("Success") == 1) {
                    if (parentObject.has("OrderID")) {
                        solrdata.orderId = parentObject.getString("OrderID");
                    }
                    if (parentObject.has("VAT")) {
                        String vat = parentObject.getString("VAT").split("\\%")[0];
                        vatAmount = Double.valueOf(vat);
                        solrdata.vat = vatAmount;
                    }
                    if (parentObject.has("ST")) {
                        String serviceTax = parentObject.getString("ST").split("\\%")[0];
                        serviceTaxAmount = Double.valueOf(serviceTax);
                        solrdata.serviceTax = serviceTaxAmount;
                    }

                    Log.d(TAG, "Order Id : " + solrdata.orderId);
                    Log.d(TAG, "VAT Amount : " + solrdata.vat);
                    Log.d(TAG, "Service Tax : " + solrdata.serviceTax);

                    calculateTotalPrice();

                    btnOrder.setAlpha(1.0f);
                    btnOrder.setEnabled(true);
                } else {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
                    dialogBuilder.setMessage("Unable to process your order");
                    dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
                    dialogBuilder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            fetchOrderIdRequest.execute();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if ((dialog != null) && dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    class GetDeliveryAreaByOutLet extends AsyncTask<String, String, String> {

        String response= "failed";
        List<LocationModel> userSelectedDeliveryAreaList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                if (ConnectionDetector.isConnectingToInternet(CheckoutActivity.this)) {
                    URL url = new URL(params[0]);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);

                    conn.connect();
                    if (conn.getResponseCode() == 200) {
                        response = ConvertInputStream.toString(conn.getInputStream());
                    } else {
                        response = "failed";
                    }
                }
            } catch (IOException e) {
                Log.d("I0 Exception", e.getMessage());
                e.printStackTrace();
                return response;
            }

            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (!s.equals("") || s != null) {
                try {
                    JSONObject responseJson = new JSONObject(response);
                    if (responseJson.has("Success")) {
                        dialog.dismiss();
                        if (responseJson.getInt("Success") == 1) {
                            JSONArray docsArray = responseJson.getJSONArray("outlet");
                            String[] deliveryAreasArray = new String[docsArray.length()];
                            for (int i = 0; i < docsArray.length(); i++) {
                                deliveryAreasArray[i] = docsArray.getJSONObject(i).getString("Area");
                                LocationModel model = new LocationModel();
                                model.setArea(docsArray.getJSONObject(i).getString("Area"));
                                model.setCity(docsArray.getJSONObject(i).getString("City"));
                                model.setState(docsArray.getJSONObject(i).getString("State"));
                                model.setPostal(docsArray.getJSONObject(i).getString("Pincode"));
                                userSelectedDeliveryAreaList.add(model);
                            }
                            solrdata.selectedDeliveryLocationArray = deliveryAreasArray;
                            solrdata.userSelectedDeliveryAreaByOutLetList = userSelectedDeliveryAreaList;

                            if (solrdata.isTadqaTandoorExpress) {
                                checkIsStoreOpen("tte");
                            } else {
                                checkIsStoreOpen("thm");
                            }

                        } else {
                            Toast.makeText(getApplication(), "Record Not Found!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplication(), "Record Not Found!", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {

            }
        }
    }

    public void checkIsStoreOpen(String storeTag) {
        dialog.show();
        Perfecto.with(CheckoutActivity.this)
                .fromUrl(API_LINKS.GET_OPENING_TIME_URL + storeTag)
                .ofTypeGet()
                .connect(new OnNetworkRequest() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(String response) {
                        Log.d("Response", response);
                        dialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("isClosed").equalsIgnoreCase("TRUE")) {
                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
                                dialogBuilder.setMessage("We are Closed Now! We look forward to seeing you again when we reopen");
                                dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });
                                dialogBuilder.show();
                            } else {
                                Intent checkout_activity = new Intent(CheckoutActivity.this, UserCheckoutActivity.class);
                                checkout_activity.putExtra("From", "Checkout");
                                checkout_activity.putExtra("SelectedAddress", "");
                                startActivity(checkout_activity);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int i, String responseMessage, String errorStream) {
                        dialog.dismiss();
                        Log.d("Error", responseMessage);
                    }
                });
    }
}
