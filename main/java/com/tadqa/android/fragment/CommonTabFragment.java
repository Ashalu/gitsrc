package com.tadqa.android.fragment;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tadqa.android.ProductDescription;
import com.tadqa.android.R;
import com.tadqa.android.accessibility.ConvertInputStream;
import com.tadqa.android.accessibility.SolrData;
import com.tadqa.android.accessibility.TypeFaceUtil;
import com.tadqa.android.activity.MenuActivity;
import com.tadqa.android.pojo.FoodItem;
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
import java.util.Timer;
import java.util.TimerTask;

public class CommonTabFragment extends Fragment {

    DatabaseHelper helper;
    TextView totalAmountTextView;
    String[] imageUrl;
    String fetch_starter_url = "";
    List<FoodItem> list;
    GridView foodItemList;
    int[] quantity;
    String activity;
    private String TABLE_NAME = "CART_DATA";
    private String CART_ITEM_QUANTITY = "CART_ITEM_QUANTITY";
    Cursor cursor, itemQuantity;
    SolrData solrdata;
    ProgressBar progressBar;
    Bundle bundle;
    Intent intent;
    RelativeLayout relativeLayout;
    String MenuCategory, MealsCategory;
    int MenuPosition = 0;
    Timer timer;
    FetchStarter starter;
    String[] selectedSize;
    RelativeLayout fullLineBottom,
            halfLineBottom,
            quarterLineBottom;

    StaggeredGridAdapter adapter;
    View view;


    public CommonTabFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //adapter = new StaggeredGridAdapter();
        helper = new DatabaseHelper(getActivity());
        helper.open();
    }

    @Override
    public void onStart() {
        super.onStart();


        quantity = new int[list.size()];
        imageUrl = new String[list.size()];
        selectedSize = new String[list.size()];
        adapter = new StaggeredGridAdapter();
        foodItemList.setAdapter(adapter);


        if (helper != null) {
            ((MenuActivity) getActivity()).checkCart();
            defaultValue();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (view != null)
            unbindDrawables(view);
        System.gc();
    }

    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_tab_common, container, false);
        TypeFaceUtil.overrideFont(getContext().getApplicationContext(), "SERIF", "fonts/Roboto-Regular.ttf");

        list = new ArrayList<>();
        solrdata = (SolrData) getContext().getApplicationContext();
        intent = getActivity().getIntent();
        activity = intent.getStringExtra("activity");
        bundle = getArguments();
        fetch_starter_url = bundle.getString("API_LINK");
        MenuCategory = bundle.getString("Menu");
        progressBar = (ProgressBar) view.findViewById(R.id.progressWheel);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.mailLayout);
        relativeLayout.setVisibility(View.INVISIBLE);
        totalAmountTextView = (TextView) view.findViewById(R.id.totalAmount);
        foodItemList = (GridView) view.findViewById(R.id.foodItemsList);
        getDataFromSolr();

        return view;
    }

    protected void getDataFromSolr() {
        try {
            timer = new Timer();
            TimerTask task = new TimerTask() {

                @Override
                public void run() {
                    starter = new FetchStarter();
                    starter.execute();
                }
            };
            timer.schedule(task, 1000);
        } catch (Exception ex) {
        }
    }


    public class FetchStarter extends AsyncTask<String, Void, Void> {

        HttpURLConnection connection;
        InputStream stream;
        String response = "";

        @Override
        protected Void doInBackground(String... params) {

            switch (MenuCategory) {

                case "1":
                    MenuPosition = 1;
                    if (SolrData.getVegstarterList().size() == 0) {
                        fetch();
                    } else {
                        list = SolrData.getVegstarterList();
                        response = "Success";
                    }
                    break;
                case "2":
                    MenuPosition = 2;
                    if (SolrData.getNonVegstarterList().size() == 0) {
                        fetch();
                    } else {
                        list = SolrData.getNonVegstarterList();
                        response = "Success";
                    }
                    break;

                case "3":
                    MenuPosition = 3;
                    if (SolrData.getBreadsList().size() == 0) {
                        fetch();
                    } else {
                        list = SolrData.getBreadsList();
                        response = "Success";
                    }
                    break;

                case "4":
                    MenuPosition = 4;
                    if (SolrData.getRollsList().size() == 0) {
                        fetch();
                    } else {
                        list = SolrData.getRollsList();
                        response = "Success";
                    }
                    break;

                case "5":
                    MenuPosition = 5;
                    if (SolrData.getRiceList().size() == 0) {
                        fetch();
                    } else {
                        list = SolrData.getRiceList();
                        response = "Success";
                    }
                    break;

                case "6":
                    MenuPosition = 6;
                    if (SolrData.getPlatterList().size() == 0) {
                        fetch();
                    } else {
                        list = SolrData.getPlatterList();
                        response = "Success";
                    }

                    break;

                case "7":
                    MenuPosition = 7;
                    if (SolrData.getVegMainCourseList().size() == 0) {
                        fetch();
                    } else {
                        list = SolrData.getVegMainCourseList();
                        response = "Success";
                    }
                    break;

                case "8":
                    MenuPosition = 8;
                    if (SolrData.getNonVegMainCourseList().size() == 0) {
                        fetch();
                    } else {
                        list = SolrData.getNonVegMainCourseList();
                        response = "Success";
                    }
                    break;

                case "9":
                    MenuPosition = 9;
                    if (SolrData.getExtrasList().size() == 0) {
                        fetch();
                    } else {
                        list = SolrData.getExtrasList();
                        response = "Success";
                    }
                    break;
//                case "10":
//                    MenuPosition = 10;
//                    if (SolrData.getMealList().size() == 0) {
//                        fetch();
//                    } else {
//                        list = SolrData.getMealList();
//                        response = "Success";
//                    }
//                    break;
//                case "11":
//                    MenuPosition = 11;
//                    if (SolrData.getMealList().size() == 0) {
//                        fetch();
//                    } else {
//                        list = SolrData.getMealList();
//                        response = "Success";
//                    }
//                    break;
//                case "12":
//                    MenuPosition = 10;
//                    if (SolrData.getMealList().size() == 0) {
//                        fetch();
//                    } else {
//                        list = SolrData.getMealList();
//                        response = "Success";
//                    }
//                    break;
//                case "13":
//                    MenuPosition = 10;
//                    if (SolrData.getMealList().size() == 0) {
//                        fetch();
//                    } else {
//                        list = SolrData.getMealList();
//                        response = "Success";
//                    }
//                    break;
//                case "14":
//                    MenuPosition = 10;
//                    if (SolrData.getMealList().size() == 0) {
//                        fetch();
//                    } else {
//                        list = SolrData.getMealList();
//                        response = "Success";
//                    }
//                    break;
//                case "15":
//                    MenuPosition = 10;
//                    if (SolrData.getMealList().size() == 0) {
//                        fetch();
//                    } else {
//                        list = SolrData.getMealList();
//                        response = "Success";
//                    }

                default:
                    fetch();
                    break;
            }
            return null;
        }

        public String fetch() {
            String error = "error";
            try {

                URL starterUrl = new URL(fetch_starter_url);
                connection = (HttpURLConnection) starterUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                if (connection.getResponseCode() == 200) {
                    stream = new BufferedInputStream(connection.getInputStream());
                    response = ConvertInputStream.toString(stream);
                    return response;

                } else {
                    return response = error;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response = error;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (response.equals("failed")) {
                Log.d("Error", response.toUpperCase());
            } else if (response.equals("") || response.equals(null)) {
                Log.d("Error", "Connection Error");
            } else if (response.equals("Success")) {

                quantity = new int[list.size()];
                imageUrl = new String[list.size()];
                selectedSize = new String[list.size()];
                defaultValue();

                if (adapter == null)
                    adapter = new StaggeredGridAdapter();
                foodItemList.setAdapter(adapter);

                progressBar.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);

            } else if (response.equals("error")) {

                if (!ConnectionDetector.isConnectingToInternet(getActivity())) {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
                    builder.setTitle("Error !!");

                    builder.setIcon(R.drawable.ic_portable_wifi_off_black_48dp);
                    builder.setMessage("No Internet Connection!  ");


                    builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            getDataFromSolr();
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
                    responseJson = new JSONObject(response);
                    docs = responseJson.getJSONArray("Menu");//.getJSONObject("response");
                    //  docs = response1.getJSONArray("Menu");

                    for (int i = 0; i < docs.length(); i++) {
                        FoodItem model = new FoodItem();
                        model.setId(docs.getJSONObject(i).getString("ItemId"));
                        model.setName(docs.getJSONObject(i).getString("ItemName"));
                        model.setChefSpecial(docs.getJSONObject(i).getString("ChefSpecial"));

                        if (docs.getJSONObject(i).has("ItemHalfPrice")) {
                            String price = docs.getJSONObject(i).getString("ItemHalfPrice");
                            double val = Double.valueOf(price);
                            if (val != 0)
                                model.setHalfPrice(docs.getJSONObject(i).getString("ItemHalfPrice"));
                        }
                        if (docs.getJSONObject(i).has("ItemFullPrice")) {
                            model.setFullPrice(docs.getJSONObject(i).getString("ItemFullPrice"));
                        }
                        if (docs.getJSONObject(i).has("ItemQtrPrice")) {
                            String price = docs.getJSONObject(i).getString("ItemQtrPrice");
                            double val = Double.valueOf(price);
                            if (val != 0)
                                model.setQtrPrice(docs.getJSONObject(i).getString("ItemQtrPrice"));
                        }
                        model.setImageUrl(docs.getJSONObject(i).getString("ImageUrl"));
                        if (docs.getJSONObject(i).getString("isAvail").equals("TRUE")) {
                            list.add(model);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (MenuPosition == 1) {
                    solrdata.setVegstarterList(list);
                } else if (MenuPosition == 2) {
                    solrdata.setNonVegStarterList(list);
                } else if (MenuPosition == 3) {
                    solrdata.setBreadsList(list);
                } else if (MenuPosition == 4) {
                    solrdata.setRollsList(list);
                } else if (MenuPosition == 5) {
                    solrdata.setRiceList(list);
                } else if (MenuPosition == 6) {
                    solrdata.setPlatterList(list);
                } else if (MenuPosition == 7) {
                    solrdata.setVegMainCourseList(list);
                } else if (MenuPosition == 8) {
                    solrdata.setNonVegMainCourseList(list);
                } else if (MenuPosition == 9) {
                    solrdata.setExtrasList(list);
//                }  else if(MenuPosition==10)
//                    {
//                        solrdata.setMealList(list);
//                    }
                }

                quantity = new int[list.size()];
                imageUrl = new String[list.size()];
                selectedSize = new String[list.size()];
                defaultValue();

                if (adapter == null)
                    adapter = new StaggeredGridAdapter();
                foodItemList.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    final static class Holder {
        TextView name;
        ImageView image;
        ImageView chefSpl;
        TextView itemPrice;
        TextView itemQuantity;
        Button increment;
        Button decrement;
        Button add;
        LinearLayout quantityChangeLayout;
        RelativeLayout imgLayout;

    }

    public class StaggeredGridAdapter extends BaseAdapter {

        Dialog productDialog;
        ColorDrawable whiteDrawable,
                greenDrawable;
        String selected_price;
        TextView fullPriceTextView,
                halfPriceTextView,
                quarterPriceTextView,
                productItemQuantity;
        LinearLayout fullPriceLinearLayout,
                halfPriceLinearLayout,
                quarterPriceLinearLayout,
                mainContainerLayout;
        Button cancel,
                add;
        int itemQuantity = 0;

        Button increment, decrement;
        int currentItemQuantity = 1;
        int currentItemPosition = 0;
        // View dialogview;


        public StaggeredGridAdapter() {

//            .setView(R.layout.layoutS)
//                    .setTitle("Add A New Contact")
//                    .setPositiveButton("Add +", null)
//                    .setNegativeButton("Pick",null);

            productDialog = new Dialog(getActivity());
            productDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            productDialog.setContentView(R.layout.activity_translucent);

//            LayoutInflater inflater = getActivity().getLayoutInflater();
//            dialogview = inflater.inflate(R.layout.activity_translucent, null);

            whiteDrawable = new ColorDrawable();
            whiteDrawable.setColor(getResources().getColor(android.R.color.white));
            greenDrawable = new ColorDrawable();
            greenDrawable.setColor(getResources().getColor(R.color.tadka_green));

            fullPriceTextView = (TextView) productDialog.findViewById(R.id.textView_full_price);
            halfPriceTextView = (TextView) productDialog.findViewById(R.id.textView_half_price);
            quarterPriceTextView = (TextView) productDialog.findViewById(R.id.textView_quarter_price);

            fullPriceLinearLayout = (LinearLayout) productDialog.findViewById(R.id.linearLayout_full_price);
            halfPriceLinearLayout = (LinearLayout) productDialog.findViewById(R.id.linearLayout_half_price);
            quarterPriceLinearLayout = (LinearLayout) productDialog.findViewById(R.id.linearLayout_quarter_price);
            mainContainerLayout = (LinearLayout) productDialog.findViewById(R.id.containerLayout);

            productItemQuantity = (TextView) productDialog.findViewById(R.id.item_quantity);
            productItemQuantity.setText("1");

            increment = (Button) productDialog.findViewById(R.id.increment);
            decrement = (Button) productDialog.findViewById(R.id.decrement);
            cancel = (Button) productDialog.findViewById(R.id.button_cancel);
            add = (Button) productDialog.findViewById(R.id.button_add);

            fullLineBottom = (RelativeLayout) productDialog.findViewById(R.id.line_full);
            halfLineBottom = (RelativeLayout) productDialog.findViewById(R.id.line_half);
            quarterLineBottom = (RelativeLayout) productDialog.findViewById(R.id.line_quarter);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public String getDeviceName() {
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            if (model.startsWith(manufacturer)) {
                return model;
            } else {
                return model;
            }
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final Holder holder;

            if (convertView == null) {

                holder = new Holder();

                LayoutInflater inflater = LayoutInflater.from(getActivity());
                //convertView = inflater.inflate(R.layout.food_grid_component, null);
                convertView = inflater.inflate(R.layout.activity_card_layout, null);
                holder.name = (TextView) convertView.findViewById(R.id.item_name);
                holder.chefSpl = (ImageView) convertView.findViewById(R.id.chefSplImg);
                holder.image = (ImageView) convertView.findViewById(R.id.productImage);
                holder.itemPrice = (TextView) convertView.findViewById(R.id.item_price);
                holder.itemQuantity = (TextView) convertView.findViewById(R.id.item_quantity);
                holder.increment = (Button) convertView.findViewById(R.id.increment);
                holder.decrement = (Button) convertView.findViewById(R.id.decrement);
                holder.add = (Button) convertView.findViewById(R.id.add);
                holder.quantityChangeLayout = (LinearLayout) convertView.findViewById(R.id.quantity_change_buttons_layout);
                holder.imgLayout = (RelativeLayout) convertView.findViewById(R.id.line2);
                convertView.setTag(holder);


            } else {

                holder = (Holder) convertView.getTag();
            }

            final View vw = convertView;

            String titleCaseValue = list.get(position).getName().toString();

            String chef = list.get(position).getChefSpecial().toString();
            if (chef.equalsIgnoreCase("False")) {
                holder.chefSpl.setVisibility(View.INVISIBLE);
            } else {
                holder.chefSpl.setVisibility(View.VISIBLE);
            }


            if (quantity.length != 0) {
                itemQuantity = quantity[position];//getItemQuantity(list.get(position).getId());
            } else {
                Log.d("Position", position + "");
                itemQuantity = 0;
            }
            holder.itemQuantity.setText(String.valueOf(itemQuantity));


            if (!list.get(position).getQtrPrice().toString().isEmpty())
                holder.itemPrice.setText(getResources().getString(R.string.Rupee) + list.get(position).getQtrPrice());
            else if (!list.get(position).getHalfPrice().toString().isEmpty())
                holder.itemPrice.setText(getResources().getString(R.string.Rupee) + list.get(position).getHalfPrice());
            else
                holder.itemPrice.setText(getResources().getString(R.string.Rupee) + list.get(position).getFullPrice());

            holder.name.setText(titleCaseValue);

            //if(getDeviceName().toUpperCase().matches("GT-I9060I"))
            {
                holder.itemPrice.setTextSize(15);
                holder.name.setTextSize(15);
                holder.add.setTextSize(15);
                //  holder.imgLayout.setMinimumHeight(130);
            }

            holder.increment.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (list.get(position).getHalfPrice() == "" && list.get(position).getQtrPrice() == "") {
                        int quan = quantity[position];//getItemQuantity(list.get(position).getId());
                        quan = quan + 1;
                        quantity[position] = quan;
                        holder.itemQuantity.setText(String.valueOf(quan));
                        addItem(list.get(position).getId(),
                                list.get(position).getName(),
                                Integer.parseInt(list.get(position).getFullPrice()),
                                quan,
                                list.get(position).getImageUrl(),
                                "Full");
                        addItemQuantity(list.get(position).getId(),
                                quan,
                                "Full");
                        ((MenuActivity) getActivity()).checkCart();
                        Log.d("holder Increment: ", list.get(position).getId() + " " + quan);

                    } else {

                        if (list.get(position).getQtrPrice().toString().isEmpty())
                            mainContainerLayout.setWeightSum(2f);
                        else if (list.get(position).getHalfPrice().toString().isEmpty())
                            mainContainerLayout.setWeightSum(2f);


                        selected_price = "Full";
                        fullPriceLinearLayout.isSelected();

                        int qtyfull = getItemQuantityOfType(list.get(position).getId(), "Full");
                        if (qtyfull > 0)
                            productItemQuantity.setText(String.valueOf(qtyfull));
                        else
                            productItemQuantity.setText("1");

                        Log.d("holder Increment full: ", list.get(position).getId() + " " + qtyfull);

                        productDialog.show();
                        currentItemQuantity = 1;
                        currentItemPosition = position;

                        if (!(list.get(position).getFullPrice().equals(null) || list.get(position).getFullPrice().equals(""))) {
                            fullPriceTextView.setText(list.get(position).getFullPrice());

                        } else {
                            fullPriceLinearLayout.setVisibility(View.GONE);
                        }
                        if (!(list.get(position).getHalfPrice().equals(null) || list.get(position).getHalfPrice().equals(""))) {
                            halfPriceTextView.setText(list.get(position).getHalfPrice());
                        } else {
                            halfPriceLinearLayout.setVisibility(View.GONE);
                        }
                        if (!(list.get(position).getQtrPrice().equals(null) || list.get(position).getQtrPrice().equals(""))) {
                            quarterPriceTextView.setText(list.get(position).getQtrPrice());
                        } else {
                            quarterPriceLinearLayout.setVisibility(View.GONE);
                        }


                    }

                }
            });

            holder.decrement.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (isDifferentTypeSelected(position)) {
                        Toast.makeText(getActivity(), "This item is added in different sizes, change in cart for respective size.", Toast.LENGTH_SHORT).show();
                    } else {
                        int quan = quantity[position];//getItemQuantity(list.get(position).getId());
                        if (quan > 1) {
                            quan -= 1;
                            quantity[position] = quan;
                            holder.itemQuantity.setText(String.valueOf(quan));
                            addItem(list.get(position).getId(),
                                    list.get(position).getName(),
                                    Integer.parseInt(list.get(position).getFullPrice()),
                                    quan,
                                    list.get(position).getImageUrl(),
                                    "Full");
                            addItemQuantity(list.get(position).getId(),
                                    quan,
                                    "Full");
                            ((MenuActivity) getActivity()).checkCart();
                        } else {
                            helper.remove(TABLE_NAME, list.get(position).getId());
                            helper.remove(CART_ITEM_QUANTITY, list.get(position).getId());
                            holder.itemQuantity.setText("0");
                            quantity[position] = 0;
                            ((MenuActivity) getActivity()).checkCart();
                            //   holder.add.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });

            // Log.d("getView: ",list.get(position).getId() +" "+position +" "+currentItemPosition);

            if (itemQuantity > 0) {
                holder.add.setVisibility(View.INVISIBLE);
            } else {
                holder.add.setVisibility(View.VISIBLE);
            }

/**
 *                         Product Dialog coding start here.
 */

            increment.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    currentItemQuantity += 1;
                    productItemQuantity.setText(String.valueOf(currentItemQuantity));

                    holder.add.setVisibility(View.INVISIBLE);
                }
            });

            decrement.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (currentItemQuantity > 1) {
                        currentItemQuantity -= 1;
                        productItemQuantity.setText(String.valueOf(currentItemQuantity));
                    } else {
                        Log.d("Minimum Quantity", "1");
                    }
                }
            });


            cancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    productDialog.dismiss();
                    fullPriceLinearLayout.isSelected();
                    fullLineBottom.setBackgroundDrawable(greenDrawable);
                    quarterLineBottom.setBackgroundDrawable(whiteDrawable);
                    halfLineBottom.setBackgroundDrawable(whiteDrawable);
                }
            });

            final String dialogaddQty;

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d("Quantity", String.valueOf(quantity[currentItemQuantity]) + " " + selected_price);
                    Log.d("CurrentItemQuantity", String.valueOf(currentItemQuantity));

                    addItem(list.get(currentItemPosition).getId(),
                            list.get(currentItemPosition).getName(),
                            Integer.parseInt(whichPrice(currentItemPosition, selected_price)),
                            currentItemQuantity,
                            list.get(currentItemPosition).getImageUrl(),
                            selected_price);
                    addItemQuantity(list.get(currentItemPosition).getId(),
                            currentItemQuantity,
                            selected_price);

                    ((MenuActivity) getActivity()).checkCart();
                    onUserSelectValue(currentItemPosition);
                    // notifyDataSetChanged();
                    // notifyDataSetInvalidated();
                    productDialog.dismiss();

                }

            });

            /**
             * By default selected price will be of Full.
             */
            fullLineBottom.setBackgroundDrawable(greenDrawable);
            quarterLineBottom.setBackgroundDrawable(whiteDrawable);
            halfLineBottom.setBackgroundDrawable(whiteDrawable);

            quarterPriceLinearLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    selected_price = "Quarter";
                    fullLineBottom.setBackgroundDrawable(whiteDrawable);
                    quarterLineBottom.setBackgroundDrawable(greenDrawable);
                    halfLineBottom.setBackgroundDrawable(whiteDrawable);

                    int qty = getItemQuantityOfType(list.get(currentItemPosition).getId(), "Quarter");
                    if (qty > 0)
                        productItemQuantity.setText(String.valueOf(qty));
                    else
                        productItemQuantity.setText("1");
                }
            });

            halfPriceLinearLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    selected_price = "Half";
                    fullLineBottom.setBackgroundDrawable(whiteDrawable);
                    quarterLineBottom.setBackgroundDrawable(whiteDrawable);
                    halfLineBottom.setBackgroundDrawable(greenDrawable);

                    int qty = getItemQuantityOfType(list.get(currentItemPosition).getId(), "Half");
                    if (qty > 0)
                        productItemQuantity.setText(String.valueOf(qty));
                    else
                        productItemQuantity.setText("1");

                }
            });

            fullPriceLinearLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    selected_price = "Full";
                    fullLineBottom.setBackgroundDrawable(greenDrawable);
                    quarterLineBottom.setBackgroundDrawable(whiteDrawable);
                    halfLineBottom.setBackgroundDrawable(whiteDrawable);

                    int qty = getItemQuantityOfType(list.get(currentItemPosition).getId(), "Full");
                    if (qty > 0)
                        productItemQuantity.setText(String.valueOf(qty));
                    else
                        productItemQuantity.setText("1");
                }
            });

            holder.add.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    holder.add.setVisibility(View.INVISIBLE);

                    if (list.get(position).getHalfPrice().equals("") && list.get(position).getQtrPrice().equals("")) {

                        int quan = getItemQuantity(list.get(position).getId());
                        quan = quan + 1;
                        quantity[position] = quan;
                        holder.itemQuantity.setText(String.valueOf(quantity[position]));
                        addItem(list.get(position).getId(),
                                list.get(position).getName(),
                                Integer.parseInt(list.get(position).getFullPrice()),
                                quan,
                                list.get(position).getImageUrl(),
                                "Full");
                        addItemQuantity(list.get(position).getId(),
                                quan,
                                "Full");
                        ((MenuActivity) getActivity()).checkCart();
                        v.setVisibility(View.INVISIBLE);
                        Log.d("onClick holder ADD: ", list.get(position).getId() + " " + quan);

                    } else {

                        if (list.get(position).getQtrPrice().toString().isEmpty())
                            mainContainerLayout.setWeightSum(2f);
                        else if (list.get(position).getHalfPrice().toString().isEmpty())
                            mainContainerLayout.setWeightSum(2f);


                        selected_price = "Full";
                        productItemQuantity.setText("1");
                        productDialog.show();
                        currentItemQuantity = 1;
                        currentItemPosition = position;

                        if (!(list.get(position).getFullPrice().equals(null) || list.get(position).getFullPrice().equals(""))) {
                            fullPriceTextView.setText(list.get(position).getFullPrice());

                        } else {
                            fullPriceLinearLayout.setVisibility(View.GONE);
                        }
                        if (!(list.get(position).getHalfPrice().equals(null) || list.get(position).getHalfPrice().equals(""))) {
                            halfPriceTextView.setText(list.get(position).getHalfPrice());
                        } else {
                            halfPriceLinearLayout.setVisibility(View.GONE);
                        }
                        if (!(list.get(position).getQtrPrice().equals(null) || list.get(position).getQtrPrice().equals(""))) {
                            quarterPriceTextView.setText(list.get(position).getQtrPrice());
                        } else {
                            quarterPriceLinearLayout.setVisibility(View.GONE);
                        }
                        v.setVisibility(View.INVISIBLE);
                    }

                }

            });

            /**
             *                         Product Dialog coding ends here.
             */

            //final Transformation transformation = new RoundedCornersTransform();
            if (imageUrl.length != 0) {
                //  Picasso.with(getActivity()).load(imageUrl[position]).placeholder(R.drawable.appimg).resize(250, 250).into(holder.image);
                //Picasso.with(getActivity()).load(imageUrl[position]).transform(new RoundedCornersTransform()).placeholder(R.drawable.appimg).resize(250,250).into(holder.image);
                Picasso.with(getActivity()).load(imageUrl[position]).placeholder(R.drawable.appimg).into(holder.image);
            } else {
                //Picasso.with(getActivity()).load(R.drawable.appimg).resize(250, 250).into(holder.image);
                Picasso.with(getActivity()).load(R.drawable.appimg).into(holder.image);

            }


            holder.image.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Intent productDescriptionIntent = new Intent(getActivity(), ProductDescription.class);
                    productDescriptionIntent.putExtra("product_name", list.get(position).getName());
                    productDescriptionIntent.putExtra("chef", list.get(position).getChefSpecial());
                    productDescriptionIntent.putExtra("product_url", list.get(position).getImageUrl());
                    productDescriptionIntent.putExtra("product_id", String.valueOf(list.get(position).getId()));
                    productDescriptionIntent.putExtra("product_price", String.valueOf(list.get(position).getFullPrice()));
                    productDescriptionIntent.putExtra("product_Halfprice", String.valueOf(list.get(position).getHalfPrice()));
                    productDescriptionIntent.putExtra("product_Qtrprice", String.valueOf(list.get(position).getQtrPrice()));
                    startActivity(productDescriptionIntent);
                }
            });

            return convertView;
        }
    }

    void onUserSelectValue(int id) {
        try {
            quantity = new int[list.size()];
            imageUrl = new String[list.size()];
            selectedSize = new String[list.size()];
            if (adapter == null) {
                adapter = new StaggeredGridAdapter();
            }
            foodItemList.setAdapter(adapter);

            if (helper != null) {

                ((MenuActivity) getActivity()).checkCart();
                defaultValue();
            }
            foodItemList.setSelection(id);

        } catch (Exception ex) {

        }
    }

    public String whichPrice(int position, String type) {

        String price = "";

        if (type == "Full") {
            price = list.get(position).getFullPrice();
        }
        if (type == "Half") {
            price = list.get(position).getHalfPrice();
        }
        if (type == "Quarter") {
            price = list.get(position).getQtrPrice();
        }

        return price;
    }

    public void addItem(String id, String name, int price, int quantity, String image_url, String type) {

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

        ContentValues values = new ContentValues();
        values.put("ITEM_ID", id);
        values.put("ITEM_QUANTITY", quantity);
        values.put("ITEM_TYPE", type);

        helper.insertCartItemQuantityData(CART_ITEM_QUANTITY, values, id, type);
    }

    public void defaultValue() {

        cursor = helper.getData(TABLE_NAME);
        try {
            if (cursor.getCount() == 0) {
                quantity = new int[list.size()];
                for (int i = 0; i < quantity.length; i++) {
                    quantity[i] = 0;
                }
            } else {
                quantity = new int[list.size()];
                for (int i = 0; i < quantity.length; i++) {
                    quantity[i] = getItemQuantity(list.get(i).getId());
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        cursor.close();

        selectedSize = new String[list.size()];
        for (int i = 0; i < selectedSize.length; i++) {
            selectedSize[i] = "Full";
        }

        imageUrl = new String[list.size()];
        for (int i = 0; i < imageUrl.length; i++) {
            imageUrl[i] = list.get(i).getImageUrl();
        }
    }

    public int getItemQuantity(String id) {
        int quantity = 0;

        itemQuantity = helper.getItemQuantityData(CART_ITEM_QUANTITY, id);
        if (itemQuantity != null) {
            try {
                if (itemQuantity.moveToFirst()) {
                    for (int i = 0; i < itemQuantity.getCount(); i++) {
                        itemQuantity.moveToPosition(i);
                        int value = itemQuantity.getInt(itemQuantity.getColumnIndex("ITEM_QUANTITY"));
                        quantity += value;
                    }
                    solrdata.SetItemIDQuantity(id, quantity);
                } else {
                    quantity = 0;
                }
                itemQuantity.close();
            } catch (Exception e) {
                System.out.print(e);
            }
        } else {

            solrdata.getItemIDQuantity(id);
        }

        return quantity;
    }

    public int getItemQuantityOfType(String id, String type) {
        int quantity = 0;

        try {
            Cursor cursor = helper.getItemQuantityDataOfType(TABLE_NAME, id, type);
            if (cursor.moveToFirst()) {
                quantity = cursor.getInt(cursor.getColumnIndex("ITEM_QUANTITY"));
                return quantity;
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return quantity;
    }

    public boolean isDifferentTypeSelected(int position) {
        boolean status = false;

        if (!list.get(position).getHalfPrice().equals("")) {
            if (getItemQuantityOfType(list.get(position).getId(), "Half") > 0) {
                status = true;
            }
        }

        if (!list.get(position).getQtrPrice().equals("")) {
            if (getItemQuantityOfType(list.get(position).getId(), "Quarter") > 0) {
                status = true;
            }
        }

        return status;
    }

    @Override
    public void onDetach() {

        if (timer != null) {
            timer.cancel();
            timer.purge();
        }

        if (starter != null && starter.getStatus() == AsyncTask.Status.RUNNING) {
            starter.cancel(true);
        }

        if (helper != null)
            helper.close();
        if (cursor != null)
            cursor.close();

        System.gc();

        super.onDetach();
    }


//    public class RoundedCornersTransform implements Transformation {
//        @Override
//        public Bitmap transform(Bitmap source) {
//            int size = Math.min(source.getWidth(), source.getHeight());
//
//            int x = (source.getWidth() - size) / 2;
//            int y = (source.getHeight() - size) / 2;
//
//            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
//            if (squaredBitmap != source) {
//                source.recycle();
//            }
//
//            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());
//
//            Canvas canvas = new Canvas(bitmap);
//            Paint paint = new Paint();
//            BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
//            paint.setShader(shader);
//            paint.setAntiAlias(true);
//
//            float r = size / 18f;//8f;
//            canvas.drawRoundRect(new RectF(0, 0, source.getWidth(), source.getHeight()), r, r, paint);
//            squaredBitmap.recycle();
//            return bitmap;
//        }
//
//        @Override
//        public String key() {
//            return "rounded_corners";
//        }
//    }


}

