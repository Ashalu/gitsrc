package com.tadqa.android.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tadqa.android.R;
import com.tadqa.android.accessibility.ConvertInputStream;
import com.tadqa.android.accessibility.SolrData;
import com.tadqa.android.activity.HomeMadeActivity;
import com.tadqa.android.pojo.MenuModel;
import com.tadqa.android.util.API_LINKS;
import com.tadqa.android.util.ConnectionDetector;
import com.tadqa.android.util.DatabaseHelper;

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


public class MealsFragment extends Fragment {

    List<MenuModel> list;
    ListView foodItemList;
    ProgressDialog dialog;
    List<MenuModel> Mealslist = new ArrayList<>();
    LinearLayout footerCheckout;
    TextView totalAmountTextView;
    DatabaseHelper helper;
    String[] imageUrl;
    HttpURLConnection connection;
    //MenuList fetchMenuData;
    SolrData solrdata;
    MenuAdapter menuadapter;
    MenuClass menuclass;
    String mealsresponse = "";
    ConnectionDetector connectiondetector;
    //    FetchingList fetchMealsData;
    CoordinatorLayout coordinatorLayout;
    List<MenuModel> MealsCategorylist = new ArrayList<>();
    InputStream stream;

    public MealsFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        helper = new DatabaseHelper(getActivity());
        helper.open();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_meals, null);
        footerCheckout = (LinearLayout) view.findViewById(R.id.checkoutFooter);
        totalAmountTextView = (TextView) view.findViewById(R.id.totalAmount);
        foodItemList = (ListView) view.findViewById(R.id.foodItemsList);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.snackbarCoordinatorLayout);
        getDataFromMenu();
        foodItemList.setAdapter(new MenuAdapter(getActivity(),Mealslist));


        return view;
    }

    protected void getDataFromMenu() {
        try {
            if (SolrData.getMenuList().size() == 0) {
                MealsFragment.this.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        menuclass = new MenuClass();
                        menuclass.execute();
                    }
                });
            } else {
//                Set meals in global/application context
//                Mealslist = SolrData.getMealsList();
//                foodItemList.setAdapter(new MenuAdapter(getActivity(),Mealslist));
//                Log.d("get BAnners", Mealslist.size() + ",");
            }
        } catch (Exception ex) {
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        connectiondetector = new ConnectionDetector(getContext());
        // Calling Application class (see application tag in AndroidManifest.xml)
        solrdata = (SolrData) getContext().getApplicationContext();
//        getDataFromSolr();


//        checkout.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Intent checkout_activity = new Intent(getActivity(), CheckoutActivity.class);
//                startActivity(checkout_activity);
//
////                Intent checkout_activity = new Intent(getActivity(), UserCheckoutActivity.class);
////                startActivity(checkout_activity);
//
//
//            }
//        });
    }

//    protected void getDataFromSolr() {
//        try {
//            if (SolrData.getMenuList().size() == 0) {
//
//                MealsFragment.this.getActivity().runOnUiThread(new Runnable() {
//                    public void run() {
//                        fetchMenuData = new MenuList();
//                        fetchMenuData.execute();
//                    }
//                });
//            } else {
//                //Set meals in global/application context
//                list = SolrData.getMenuList();
//                foodItemList.setAdapter(new MenuItemAdpater(getActivity(), list));
//                Log.d("get SOLR DATA Meals", list.size() + ",");
//            }
//        } catch (Exception ex) {
//        }
//    }

    @Override
    public void onStart() {
        super.onStart();
    }


    static class ProductHolder {
        TextView itemName;
        ImageView itemImage;

    }

    private class MenuClass extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Mealslist = new ArrayList<>();
            if (SolrData.getMealsList().size() == 0) {
                try {
                    URL starterUrl = new URL(API_LINKS.ALL_CATEGORY_URL);
                    connection = (HttpURLConnection) starterUrl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    if (connection.getResponseCode() == 200) {
                        stream = new BufferedInputStream(connection.getInputStream());
                        mealsresponse = ConvertInputStream.toString(stream);
                        Log.d("Data", mealsresponse);
//                        isLoading = true;
                        return "success";
                    } else {
                        return "error";
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return "error";
                } catch (IOException e) {
                    e.printStackTrace();
                    return "error";
                }
            } else {
                Mealslist = SolrData.getMealsList();
            }
            return "error";
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if (response.equals("error") || response.equals("") || response.equals(null)) {
                if (!ConnectionDetector.isConnectingToInternet(getActivity())) {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
                    builder.setTitle("Error !!");

                    builder.setIcon(R.drawable.ic_portable_wifi_off_black_48dp);
                    builder.setMessage("No Internet Connection!  ");


                    builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                } else
                    new ConnectionDetector(getContext()).isServerNotRunning();
            } else {
                JSONObject responseJson = null;
                JSONObject response1 = null;
                JSONArray docs = null;

                try {
                    responseJson = new JSONObject(mealsresponse);
                    docs = responseJson.getJSONArray("Category");

                    for (int i = 0; i < docs.length(); i++) {
                        MenuModel model = new MenuModel();
                        model.setVisibility(docs.getJSONObject(i).getString("isVisible"));
                        model.setName(docs.getJSONObject(i).getString("Category"));
                        model.setUrl(docs.getJSONObject(i).getString("Imagepath"));
                        if (docs.getJSONObject(i).getString("isVisible").equalsIgnoreCase("false"))
                            Mealslist.add(model);
                        SolrData.setMenuList(Mealslist);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (menuadapter == null)
                    foodItemList.setAdapter(new MenuAdapter(getActivity(), Mealslist));


            }
        }
    }
    private class MenuAdapter extends BaseAdapter {
        Context context;
        List<MenuModel> itemList;

        @Override
        public int getCount() {
            return Mealslist.size();
        }

        public MenuAdapter(Context con, List<MenuModel> list) {
            context = con;
            itemList = list;
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
            ProductHolder holder;

            if (convertView == null) {
                holder = new ProductHolder();
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                convertView = inflater.inflate(R.layout.menuitem_layout, null);
                holder.itemName = (TextView) convertView.findViewById(R.id.item_name);
                holder.itemImage = (ImageView) convertView.findViewById(R.id.productImage);
                convertView.setTag(holder);


            } else {
                holder = (ProductHolder) convertView.getTag();
            }
            imageUrl = new String[Mealslist.size()];
            String titleCaseValue = Mealslist.get(position).getName().toString();
            String imageurl = Mealslist.get(position).getUrl().toString();
            holder.itemName.setText(titleCaseValue);
            if (imageUrl.length != 0) {
                Picasso.with(getActivity()).load(imageurl).into(holder.itemImage);
            } else {
                Picasso.with(getActivity()).load(R.drawable.appimg).into(holder.itemImage);
            }

            holder.itemImage.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Intent productDescriptionIntent = new Intent(getActivity(), HomeMadeActivity.class);
                    solrdata.setCurrentMenuPosition(position);
                    startActivity(productDescriptionIntent);
                }
            });
            return convertView;

        }


    }
}
