package com.tadqa.android.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tadqa.android.OrderItemModel;
import com.tadqa.android.R;
import com.tadqa.android.accessibility.ConvertInputStream;
import com.tadqa.android.fragment.RecentOrderFragment;
import com.tadqa.android.util.API_LINKS;
import com.tadqa.android.view.TrackView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class OrderDetailActivity extends AppCompatActivity implements View.OnClickListener {

    OrderItemModel orderItemModel;
    Context mContext = OrderDetailActivity.this;
    TrackView tvTrackOrderView;
    Button btnCancelOrder;
    private boolean isCancelled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        orderItemModel = (OrderItemModel) getIntent().getSerializableExtra("ORDER_OBJECT");
        setSharedElementDetails(orderItemModel);

        ((ListView) findViewById(R.id.lvOrderItems))
                .setAdapter(new ArrayAdapter<>(mContext,
                        android.R.layout.simple_list_item_1,
                        parseItemsToArray(orderItemModel.getItemName())));

        btnCancelOrder = (Button) findViewById(R.id.btnCancelOrder);
        btnCancelOrder.setOnClickListener(this);

        tvTrackOrderView = (TrackView) findViewById(R.id.tvTrackOrder);
        if (orderItemModel.getOrdStatus().equalsIgnoreCase("pending")) {
            tvTrackOrderView.setStatus(TrackView.Status.PENDING);
        } else if (orderItemModel.getOrdStatus().equalsIgnoreCase("received")) {
            tvTrackOrderView.setStatus(TrackView.Status.RECEIVED);
        } else if (orderItemModel.getOrdStatus().equalsIgnoreCase("dispatched")) {
            tvTrackOrderView.setStatus(TrackView.Status.DISPATCHED);
        } else if (orderItemModel.getOrdStatus().equalsIgnoreCase("delivered")) {
            tvTrackOrderView.setStatus(TrackView.Status.DELIVERED);
        } else if (orderItemModel.getOrdStatus().toLowerCase().contains("cancel")) {
            tvTrackOrderView.setStatus(TrackView.Status.CANCELED);
        }
    }

    public void setSharedElementDetails(OrderItemModel orderItemModel) {
        TextView tvOrderId = (TextView) findViewById(R.id.tvOrderId);
        TextView tvOrderAmount = (TextView) findViewById(R.id.tvOrderAmount);
        TextView tvOrderDate = (TextView) findViewById(R.id.tvOrderDate);
        TextView tvOrderItemCount = (TextView) findViewById(R.id.tvOrderItemCount);
        TextView tvOrderStatus = (TextView) findViewById(R.id.tvOrderStatus);
        TextView tvOrderDeliveryTime = (TextView) findViewById(R.id.tvOrderDeliveryTime);

        tvOrderId.setText(orderItemModel.getId());
        tvOrderAmount.setText(mContext.getResources().getString(R.string.Rupee) + orderItemModel.getPrice());
        tvOrderDate.setText(getDateString(orderItemModel.getOrderDateTime()));

        if (orderItemModel.getItemCounts() < 2) {
            tvOrderItemCount.setText(orderItemModel.getItemCounts() + " item");
        } else {
            tvOrderItemCount.setText(orderItemModel.getItemCounts() + " items");
        }

        tvOrderStatus.setText(orderItemModel.getOrdStatus());
        tvOrderDeliveryTime.setText(orderItemModel.getDeliveryDateTime());
    }

    public static String getDateString(String dateString) {
        String[] dateTimeArray = dateString.split("T");

        Date newDate = new Date();

        String date = dateTimeArray[0];
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            newDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat newFormat = new SimpleDateFormat("MMMM d, yyyy");

        return newFormat.format(newDate);
    }

    public String[] parseItemsToArray(String items) {
        String[] itemsArray = new String[0];
        try {
            JSONArray jsonArray = new JSONArray(items);
            itemsArray = new String[jsonArray.length()];

            for (int i = 0; i < itemsArray.length; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String item = jsonObject.getString("Qty") + " x " + jsonObject.getString("ItemName") + "(" + jsonObject.getString("Type") + ")";
                itemsArray[i] = item;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return itemsArray;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCancelOrder:
                String[] dateTime = orderItemModel.getOrderDateTime().split("T");

                String time = dateTime[1].toString().substring(0, dateTime[1].length() - 1);
                if (getDifferenceInMinutes(time) > 5) {
                    Toast.makeText(mContext, "Cannot cancel this order", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogCustom);
                    builder.setTitle("Cancel Order");
                    builder.setMessage("Do You want to cancel this order !");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    CancelOrder order = new CancelOrder();
                                    String Order = API_LINKS.CANCEL_ORDER_URL + "OrderConfirmationDateTime=" + orderItemModel.getOrderDateTime() + "&OrderId=" + orderItemModel.getId();
                                    order.execute(Order);
                                    dialog.dismiss();
                                }
                            });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // mDrawerLayout.closeDrawer(mDrawerList);
                            dialog.cancel();
                        }
                    });
                    builder.show();

                }
        }

    }

    class CancelOrder extends AsyncTask<String, String, Boolean> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(mContext);
            dialog.setCancelable(false);
            dialog.setMessage("Canceling your Order");
            dialog.setMessage("Please Wait");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                if (connection.getResponseCode() == 200) {
                    InputStream stream = new BufferedInputStream(connection.getInputStream());
                    String response = ConvertInputStream.toString(stream);

                    Log.d("Order Cancelled", "Order Cancelled" + "," + response);
                    isCancelled = true;
                    return true;

                } else {
                    isCancelled = false;
                    Log.d("Error", String.valueOf(connection.getResponseCode()));
                    Toast.makeText(mContext, "Problem in Canceling try Again !", Toast.LENGTH_SHORT).show();
                    return false;
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Boolean response) {
            super.onPostExecute(response);
            dialog.dismiss();
            if (response) {
                Intent order = new Intent(mContext, OrderHistory.class);
                startActivity(order);
            } else {
                Toast.makeText(mContext, "Sorry", Toast.LENGTH_SHORT).show();
            }
            if ((dialog != null) && dialog.isShowing()) {
                dialog.dismiss();
            }


        }
    }

    public long getDifferenceInMinutes(String time) {

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String currentDateString = format.format(calendar.getTime());
        Date orderDate = new Date();
        Date currentDate = new Date();
        try {
            currentDate = format.parse(currentDateString);
            orderDate = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long difference = currentDate.getTime() - orderDate.getTime();
        Log.d("Current Time", currentDate.getTime() + "");
        Log.d("Order Time", orderDate.getTime() + "");
        Log.d("Difference", (currentDate.getTime() - orderDate.getTime()) / 1000 / 60 + " Minutes");
        return difference / 1000 / 60;
    }
}
