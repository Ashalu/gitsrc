package com.tadqa.android.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tadqa.android.accessibility.ConvertInputStream;
import com.tadqa.android.accessibility.SolrData;
import com.tadqa.android.accessibility.TypeFaceUtil;
import com.tadqa.android.activity.LoginActivity;
import com.tadqa.android.OrderItemModel;
import com.tadqa.android.R;
import com.tadqa.android.adapter.OrderItemAdapter;
import com.tadqa.android.util.ParseJson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import developer.shivam.perfecto.OnNetworkRequest;
import developer.shivam.perfecto.Perfecto;

public class OrderHistoryFragment extends Fragment implements View.OnClickListener {
    String HISTORY_ORDER_URL = "http://43.252.91.43:98/admin_tadqa/getHistoryOrders/?Contact=";
    ProgressDialog dialog;
    SharedPreferences loginSharedPrefrences;
    ListView orderList;
    RelativeLayout rlNoOrderFoundLayout;
    ProgressBar pbLoading;
    boolean orderFound = false;
    List<OrderItemModel> list= new ArrayList<>();
    List<OrderItemModel> fetchList = new ArrayList<>();
    SolrData solrdata;
    View footerView;
    OrderItemAdapter adapter;
    int START_INDEX = 0;
    int FETCH_THRESHOLD = 5;
    int ORDER_FETCHED = 0;
    int TOTAL_ORDERS = 0;
    String number;

    public OrderHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        loginSharedPrefrences = getActivity().getSharedPreferences("LOGIN_DETAILS", Context.MODE_PRIVATE);
        list = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_order, container, false);
        rlNoOrderFoundLayout = (RelativeLayout) view.findViewById(R.id.rlNoItems);
        rlNoOrderFoundLayout.setVisibility(View.VISIBLE);
        pbLoading = (ProgressBar) view.findViewById(R.id.pbLoading);
        orderList = (ListView) view.findViewById(R.id.lvOrderItems);
//        listView.setOnItemClickListener(this);

        footerView = LayoutInflater.from(getActivity()).inflate(R.layout.loading_footer, null);
        Button btnLoadItems = (Button) footerView.findViewById(R.id.btnLoadItems);
        btnLoadItems.setOnClickListener(this);

        orderList.addFooterView(footerView);

//        if (isLoggedIn) {
//            number = getActivity().getSharedPreferences(Common.USER_SHARED_PREFERENCES, Context.MODE_PRIVATE).getString(User.CONTACT, "");
        number = loginSharedPrefrences.getString("number", null);

        Perfecto.with(getActivity())
                .fromUrl(getHistoryOrderUrl(number, START_INDEX, FETCH_THRESHOLD))
                .ofTypeGet()
                .connect(new OnNetworkRequest() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(final String s) {
                try {
                    JSONObject responseJson = new JSONObject(s);
                    if (responseJson.toString().contains("Order Not Found")) {
                        pbLoading.setVisibility(View.INVISIBLE);
                        orderFound = false;
                    } else {
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    TOTAL_ORDERS = Integer.parseInt(new JSONObject(s).getString("TotalCount"));
                                    if (TOTAL_ORDERS == 0) {
                                        orderList.removeFooterView(footerView);
                                        rlNoOrderFoundLayout.setVisibility(View.VISIBLE);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                rlNoOrderFoundLayout.setVisibility(View.INVISIBLE);
                                ORDER_FETCHED = ORDER_FETCHED + FETCH_THRESHOLD;
                                orderFound = true;
                                list = ParseJson.ofTypeOrder(s);
                                Log.d("List",orderList.toString());
                                adapter = new OrderItemAdapter(getActivity(), list);
                                orderList.setAdapter(adapter);
                            }
                        };
                        Handler handler = new Handler();
                        handler.postDelayed(runnable, 1000);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, String s, String s1) {

            }

        });
//        }
        return view;
    }


    public String getHistoryOrderUrl(String contact, int start, int records) {
        return HISTORY_ORDER_URL + contact + "&start=" + start + "&max=" + records;
    }

    public void onClick(View view) {
        if (view.getId() == R.id.btnLoadItems) {
            if (ORDER_FETCHED < TOTAL_ORDERS) {
                Perfecto.with(getActivity()).fromUrl(getHistoryOrderUrl(number, ORDER_FETCHED, FETCH_THRESHOLD)).ofTypeGet().connect(new OnNetworkRequest() {

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(String s) {
                        try {
                            TOTAL_ORDERS = Integer.parseInt(new JSONObject(s).getString("TotalCount"));
                            ORDER_FETCHED += FETCH_THRESHOLD;
                            fetchList = ParseJson.ofTypeOrder(s);
                            mergeList(list, fetchList);

                            if (ORDER_FETCHED >= TOTAL_ORDERS) {
                                orderList.removeFooterView(footerView);
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int i, String s, String s1) {

                    }


                });
            }
        }
    }

    public void mergeList(List<OrderItemModel> presentList, List<OrderItemModel> fetchList) {
        for (int i = 0; i < fetchList.size(); i++) {
            presentList.add(fetchList.get(i));
        }
    }



}
