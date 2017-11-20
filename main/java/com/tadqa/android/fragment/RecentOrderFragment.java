package com.tadqa.android.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tadqa.android.OrderItemModel;
import com.tadqa.android.R;
import com.tadqa.android.accessibility.ConvertInputStream;
import com.tadqa.android.accessibility.SolrData;
import com.tadqa.android.accessibility.TypeFaceUtil;
import com.tadqa.android.activity.LoginActivity;
import com.tadqa.android.activity.OrderDetailActivity;
import com.tadqa.android.activity.OrderHistory;
import com.tadqa.android.util.API_LINKS;
import com.tadqa.android.util.NonScrollListView;

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
import java.util.List;

import developer.shivam.perfecto.OnNetworkRequest;
import developer.shivam.perfecto.Perfecto;

public class RecentOrderFragment extends Fragment implements AdapterView.OnItemClickListener {

    private String phoneNumber;
    ProgressDialog dialog;
    SharedPreferences loginSharedPrefrences;
    NonScrollListView orderList;
    List<OrderItemModel> list = new ArrayList<>();
    SolrData solrdata;
    TextView tvFooter;
    private Context mContext;
    private String TAG = "RECENT_ORDER_FRAGMENT";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        loginSharedPrefrences = getActivity().getSharedPreferences("LOGIN_DETAILS", Context.MODE_PRIVATE);
    }

    public RecentOrderFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        TypeFaceUtil.overrideFont(getContext().getApplicationContext(), "SERIF", "fonts/Roboto-Regular.ttf");

        View view = inflater.inflate(R.layout.fragment_recent_order, container, false);

        orderList = (NonScrollListView) view.findViewById(R.id.listView_order_list);
        orderList.setOnItemClickListener(this);

        tvFooter = (TextView) view.findViewById(R.id.footer);
        solrdata = (SolrData) getContext().getApplicationContext();
        boolean isLoggedIn = solrdata.getLoggedInStatus();

        if (isLoggedIn) {
            phoneNumber = loginSharedPrefrences.getString("number", null);
            getRecentOrders(phoneNumber);
        } else {
            view = inflater.inflate(R.layout.activity_signin_first, container, false);
            ImageView img = (ImageView) view.findViewById(R.id.imgorder);
            img.setImageResource(R.drawable.order);
            Button btnSignin = (Button) view.findViewById(R.id.btnSignin);
            btnSignin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.putExtra("which_activity", "OrderHistory");
                    getActivity().startActivityForResult(intent, 200);
                }
            });
        }

        return view;
    }

    public void getRecentOrders(String userNumber) {
        Perfecto.with(mContext)
                .fromUrl(API_LINKS.CURRENT_ORDER_URL + userNumber)
                .ofTypeGet()
                .connect(new OnNetworkRequest() {

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(String response) {
                        Log.d(TAG, response);
                        processResponse(response);
                    }

                    @Override
                    public void onFailure(int responseCode, String responseMessage, String errorStream) {

                    }
                });
    }

    public void processResponse(String response) {
        try {
            JSONObject object = new JSONObject(response);
            if (object.getInt("Success") == 1) {

                JSONArray docs = object.getJSONArray("Orders");

                for (int i = 0; i < docs.length(); i++) {
                    OrderItemModel model = new OrderItemModel();

                    model.setId(docs.getJSONObject(i).getString("OrderId"));
                    model.setOrderDateTime(docs.getJSONObject(i).getString("OrderConfirmationDateTime"));
                    JSONArray items = docs.getJSONObject(i).getJSONArray("Items");

                    model.setItemCounts(items.length());

                    model.setItemName(items.toString());

                    model.setPrice(docs.getJSONObject(i).getString("GrandTotal"));
                    model.setOrdStatus(docs.getJSONObject(i).getString("OrdStatus"));
                    model.setDeliveryDateTime(docs.getJSONObject(i).getString("DeliveryDateTime"));

                    list.add(model);
                }
            } else if (object.getInt("Success") == 0) {
                Toast.makeText(getActivity(), "Order not found", Toast.LENGTH_LONG).show();
            } else if (object.getInt("Success") == 4) {
                Toast.makeText(getActivity(), "Invalid Inputs", Toast.LENGTH_LONG).show();

            } else if (object.getInt("Success") == 5) {
                Toast.makeText(getActivity(), "Server Internal Error", Toast.LENGTH_LONG).show();
            } else {
                if (object.has("Response"))
                    Toast.makeText(getActivity(), object.getString("Response"), Toast.LENGTH_LONG).show();
                if ((dialog != null) && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (list.size() == 0) {
            tvFooter.setVisibility(View.INVISIBLE);
        } else {
            tvFooter.setVisibility(View.VISIBLE);
            orderList.setAdapter(new RecentOrderItemAdapter());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(mContext, OrderDetailActivity.class);
        intent.putExtra("ORDER_OBJECT", list.get(position));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat optionCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                            view,
                            "SHARED_ELEMENT");
            startActivity(intent, optionCompat.toBundle());
        } else {
            startActivity(intent);
        }
    }

    private class RecentOrderItemAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public OrderItemModel getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_row_order, null);

            TextView tvOrderId = (TextView) convertView.findViewById(R.id.tvOrderId);
            TextView tvOrderAmount = (TextView) convertView.findViewById(R.id.tvOrderAmount);
            TextView tvOrderDate = (TextView) convertView.findViewById(R.id.tvOrderDate);
            TextView tvOrderItemCount = (TextView) convertView.findViewById(R.id.tvOrderItemCount);
            TextView tvOrderStatus = (TextView) convertView.findViewById(R.id.tvOrderStatus);
            TextView tvOrderDeliveryTime = (TextView) convertView.findViewById(R.id.tvOrderDeliveryTime);

            tvOrderId.setText(getItem(position).getId());
            tvOrderAmount.setText(mContext.getResources().getString(R.string.Rupee) + getItem(position).getPrice());
            tvOrderDate.setText(getDateString(getItem(position).getOrderDateTime()));

            if (getItem(position).getItemCounts() < 2) {
                tvOrderItemCount.setText(getItem(position).getItemCounts() + " item");
            } else {
                tvOrderItemCount.setText(getItem(position).getItemCounts() + " items");
            }

            tvOrderStatus.setText(getItem(position).getOrdStatus());
            tvOrderDeliveryTime.setText(getItem(position).getDeliveryDateTime());

            return convertView;
        }
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


}
