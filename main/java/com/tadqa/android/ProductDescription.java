package com.tadqa.android;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.flaviofaria.kenburnsview.RandomTransitionGenerator;
import com.squareup.picasso.Picasso;
import com.tadqa.android.accessibility.ConvertInputStream;
import com.tadqa.android.accessibility.ExpandedGridView;
import com.tadqa.android.accessibility.PieView;
import com.tadqa.android.accessibility.ScrollViewX;
import com.tadqa.android.accessibility.SolrData;
import com.tadqa.android.accessibility.TypeFaceUtil;
import com.tadqa.android.activity.CheckoutActivity;
import com.tadqa.android.util.API_LINKS;
import com.tadqa.android.util.ConnectionDetector;
import com.tadqa.android.util.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ProductDescription extends AppCompatActivity {

    ScrollViewX scrollViewX;
    Toolbar actionBar;
    String itemId,
            productName,
            productPrice,
            productUrl,
            productPriceHalf = "",
            productPriceQtr = "";
    ColorDrawable drawable, blackDrawable;
    KenBurnsView productImageView;
    TextView productNameTextView,
    //productCategoryTextView,
    productDescriptionTextView,
            productItemQuantityTextView,
            productPriceTextView;
    Button decrement,
            increment, decrementQty, incrementQty;
    int currentItemQuantity = 1;
    private String TABLE_NAME = "CART_DATA";
    private String CART_ITEM_QUANTITY = "CART_ITEM_QUANTITY";
    Cursor cursor, itemQuantity;
    LinearLayout footerCheckout, textchef;
    TextView totalAmountTextView;
    DatabaseHelper helper;
    int quantity = 0;
    TextView checkout;
    List<String> ingreList;
    ExpandedGridView gridView;
    // LinearLayout nutritionTable;
    SolrData solrdata;
    ConnectionDetector connectionDetect;
    PieView view;
    float[] values;
    ProgressBar loadingView;
    RelativeLayout descriptionView;
    TextView first, second, third, fourth;


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
            maincontainerlayout,
            fakeStatusBar;
    Button cancel,
            add;

    RelativeLayout fullLineBottom,
            halfLineBottom,
            quarterLineBottom,
            mainlayout;

    LoadProductInfo executor;
    String chefSpl = "";
    ImageView productChef;

    @Override
    protected void onResume() {
        super.onResume();

        if (ConnectionDetector.isConnectingToInternet(this)) {

            helper = new DatabaseHelper(ProductDescription.this);
            helper.open();
            cursor = helper.getData(TABLE_NAME);

            checkCart();
            updateTotalAmount(cursor);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        solrdata = (SolrData) getApplicationContext();
        connectionDetect = new ConnectionDetector(ProductDescription.this);
        TypeFaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Roboto-Regular.ttf");


        if (ConnectionDetector.isConnectingToInternet(this)) {
            setContentView(R.layout.activity_product_description);

            mainlayout = (RelativeLayout) findViewById(R.id.mainRlayout);

            descriptionView = (RelativeLayout) findViewById(R.id.productDescription);
            descriptionView.setVisibility(View.INVISIBLE);


            values = new float[]{20, 80, 100, 160};
            view = (PieView) findViewById(R.id.pieView);

            Intent intent = getIntent();
            productName = intent.getStringExtra("product_name");
            productUrl = intent.getStringExtra("product_url");
            productPrice = intent.getStringExtra("product_price");
            itemId = intent.getStringExtra("product_id");

            if (intent.getExtras().containsKey("chef"))
                chefSpl = intent.getStringExtra("chef");


            if (intent.getExtras().containsKey("product_Halfprice"))
                productPriceHalf = intent.getStringExtra("product_Halfprice");
            if (intent.getExtras().containsKey("product_Qtrprice"))
                productPriceQtr = intent.getStringExtra("product_Qtrprice");

            typeCastMapping();
            init();

            ingreList = new LinkedList<>();

            executor = new LoadProductInfo();
            executor.execute();
        } else {
            setContentView(R.layout.netconnection);
        }
    }

    @Override
    public void onStart() {
        try {
            super.onStart();

            if (helper != null) {

                Log.d("onStart: _ProductDes ", helper.toString());
                checkCart();
                int qty = getItemQuantity(itemId);
                Log.d("onStart: Quantity ", String.valueOf(qty));
                productItemQuantityTextView.setText(String.valueOf(qty));
                quantity = getItemQuantity(itemId);
                fullPriceLinearLayout.isSelected();
                fullLineBottom.setBackgroundDrawable(greenDrawable);
                quarterLineBottom.setBackgroundDrawable(whiteDrawable);
                halfLineBottom.setBackgroundDrawable(whiteDrawable);
            }
            Log.d("onStart: _ProductDes ", "product start");
        } catch (Exception ex) {

        }
    }

    @Override
    public void onDestroy() {

        if (helper != null) {
            helper.close();
            if (executor != null && executor.getStatus() == AsyncTask.Status.RUNNING) {
                executor.cancel(true);

            }
        }
        super.onDestroy();

    }


    public void createPieAnimation(float[] values) {

        float amount = 0;
        float[] v = values;

        for (int i = 0; i < v.length; i++) {
            amount += v[i];
        }

        PropertyValuesHolder firstAngle = PropertyValuesHolder.ofFloat("firstAngle", 0, (values[0] / amount) * 360);
        PropertyValuesHolder secondAngle = PropertyValuesHolder.ofFloat("secondAngle", 0, (values[1] / amount) * 360);
        PropertyValuesHolder thirdAngle = PropertyValuesHolder.ofFloat("thirdAngle", 0, (values[2] / amount) * 360);
        PropertyValuesHolder fourthAngle = PropertyValuesHolder.ofFloat("fourthAngle", 0, (values[3] / amount) * 360);

        ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(firstAngle,
                secondAngle,
                thirdAngle,
                fourthAngle);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float first, second, third, fourth;

                first = (float) animation.getAnimatedValue("firstAngle");
                second = (float) animation.getAnimatedValue("secondAngle");
                third = (float) animation.getAnimatedValue("thirdAngle");
                fourth = (float) animation.getAnimatedValue("fourthAngle");

                view.setValues(new float[]{first, second, third, fourth});
                view.postInvalidate();
            }
        });

        animator.setDuration(5000);
        animator.start();
    }

    public String getUrl() {
        return API_LINKS.ITEM_DESCRIPTION_URL + "ItemId=" + itemId;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void typeCastMapping() {
        scrollViewX = (ScrollViewX) findViewById(R.id.mainLayout);
        actionBar = (Toolbar) findViewById(R.id.app_bar);
        actionBar.setTitle("");
        productChef = (ImageView) findViewById(R.id.product_chef);
        productImageView = (KenBurnsView) findViewById(R.id.imageView_product_image);
        productNameTextView = (TextView) findViewById(R.id.textView_product_name);
        //productCategoryTextView = (TextView) findViewById(R.id.textView_product_category);
        productDescriptionTextView = (TextView) findViewById(R.id.textView_product_description);
        increment = (Button) findViewById(R.id.incrementItm);
        decrement = (Button) findViewById(R.id.decrementItm);
        increment.setClickable(false);
        decrement.setClickable(false);
        productPriceTextView = (TextView) findViewById(R.id.textView_product_price);
        totalAmountTextView = (TextView) findViewById(R.id.totalAmount);
        fakeStatusBar = (LinearLayout) findViewById(R.id.fakeStatusBar);
        footerCheckout = (LinearLayout) findViewById(R.id.checkoutFooter);
        productItemQuantityTextView = (TextView) findViewById(R.id.item_quantity);
        textchef = (LinearLayout) findViewById(R.id.textchef);
        checkout = (TextView) findViewById(R.id.checkoutActivity);
        gridView = (ExpandedGridView) findViewById(R.id.gridViewIngredients);
        gridView.setExpanded(true);
        // nutritionTable = (LinearLayout) findViewById(R.id.nutritionTable);
        descriptionView = (RelativeLayout) findViewById(R.id.productDescription);
        loadingView = (ProgressBar) findViewById(R.id.loadingProgressWheel);
        first = (TextView) findViewById(R.id.first);
        second = (TextView) findViewById(R.id.second);
        third = (TextView) findViewById(R.id.third);
        fourth = (TextView) findViewById(R.id.fourth);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {

                getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            } else {

                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }

            //fakeStatusBar.setBackgroundColor(getResources().getColor(R.color.Black));
            int height = (int) getResources().getDimension(R.dimen.default_status_height);
            fakeStatusBar.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height));
        }

        initdialog();
    }

    private void initdialog() {
        productDialog = new Dialog(ProductDescription.this);
        productDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        productDialog.setContentView(R.layout.activity_translucent);

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
        maincontainerlayout = (LinearLayout) productDialog.findViewById(R.id.containerLayout);

        productItemQuantity = (TextView) productDialog.findViewById(R.id.item_quantity);
        productItemQuantity.setText("1");

        incrementQty = (Button) productDialog.findViewById(R.id.increment);
        decrementQty = (Button) productDialog.findViewById(R.id.decrement);

        fullLineBottom = (RelativeLayout) productDialog.findViewById(R.id.line_full);
        halfLineBottom = (RelativeLayout) productDialog.findViewById(R.id.line_half);
        quarterLineBottom = (RelativeLayout) productDialog.findViewById(R.id.line_quarter);

        RandomTransitionGenerator generator = new RandomTransitionGenerator(10000, new AccelerateDecelerateInterpolator());
        productImageView.setTransitionGenerator(generator);
    }

    private void init() {

        drawable = new ColorDrawable();
        drawable.setColor(getResources().getColor(android.R.color.white));

        blackDrawable = new ColorDrawable();
        blackDrawable.setColor(getResources().getColor(R.color.Black));

        setSupportActionBar(actionBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        scrollViewX.setOnScrollViewListener(new ScrollViewX.OnScrollViewListener() {

            @Override
            public void onScrollChanged(ScrollViewX view, int l, int t, int oldl, int oldt) {
                drawable.setAlpha(getAlphaForActionBar(view.getScrollY()));
                blackDrawable.setAlpha(getAlphaForActionBar(view.getScrollY()));
            }

            private int getAlphaForActionBar(int scrollY) {
                int minDist = 0, maxDist = 300;
                if (scrollY > maxDist) {
                    actionBar.setTitle(productName);
                    return 255;
                } else if (scrollY < minDist) {
                    return 0;
                } else {
                    int alpha = 0;
                    actionBar.setTitle("");
                    alpha = (int) ((255.0 / maxDist) * scrollY);
                    return alpha;
                }
            }
        });

        drawable.setAlpha(0);
        blackDrawable.setAlpha(0);
        actionBar.setBackgroundDrawable(drawable);
        fakeStatusBar.setBackgroundDrawable(blackDrawable);

        Picasso.with(ProductDescription.this).load(productUrl).placeholder(R.drawable.appimg).into(productImageView);
        productNameTextView.setText(productName);

        if (chefSpl.equalsIgnoreCase("False")) {
            productChef.setVisibility(View.INVISIBLE);
            textchef.setVisibility(View.INVISIBLE);
        } else {
            productChef.setVisibility(View.VISIBLE);
            textchef.setVisibility(View.VISIBLE);
        }


        //productPriceTextView.setText(productPrice);
        productPriceTextView.setText("₹ " + productPrice);
        /*Toast.makeText(ProductDescription.this, productName, Toast.LENGTH_SHORT).show();*/

        increment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!(productPriceHalf.equals(null)) && !(productPriceQtr.equals(null)) && productPriceHalf.isEmpty() && productPriceQtr.isEmpty()) {
                    quantity = quantity + 1;
                    productItemQuantityTextView.setText(String.valueOf(quantity));
                    addItem(itemId,
                            productName,
                            Integer.parseInt(String.valueOf(productPrice)),
                            quantity,
                            productUrl, "Full");
                    addItemQuantity(itemId,
                            quantity, "Full");
                    checkCart();
                    getItemQuantity(itemId);
                } else {

                    if (productPriceQtr.isEmpty())
                        maincontainerlayout.setWeightSum(2f);
                    else if (productPriceHalf.isEmpty())
                        maincontainerlayout.setWeightSum(2f);

                    currentItemQuantity = 1;
                    int qty = getItemQuantity(itemId);
                    productItemQuantityTextView.setText(String.valueOf(qty));

                    selected_price = "Full";
                    int qtyfull = getItemQuantityOfType(itemId, "Full");
                    if (qtyfull > 0)
                        productItemQuantity.setText(String.valueOf(qtyfull));
                    else
                        productItemQuantity.setText("1");

                    productDialog.show();

                    if (!(productPrice.equals(null) || productPrice.equals(""))) {
                        fullPriceTextView.setText(productPrice);

                    } else {
                        fullPriceLinearLayout.setVisibility(View.GONE);
                    }
                    if (!(productPriceHalf.equals(null) || productPriceHalf.equals(""))) {
                        halfPriceTextView.setText(productPriceHalf);
                    } else {
                        halfPriceLinearLayout.setVisibility(View.GONE);
                    }
                    if (!(productPriceQtr.equals(null) || productPriceQtr.equals(""))) {
                        quarterPriceTextView.setText(productPriceQtr);
                    } else {
                        quarterPriceLinearLayout.setVisibility(View.GONE);
                    }


                }
            }
        });

        decrement.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (isDifferentTypeSelected(itemId)) {
                    Toast.makeText(ProductDescription.this, "Quantity removed from checkout as different types are selected", Toast.LENGTH_SHORT).show();
                } else {

                    if (quantity > 1) {
                        quantity = quantity - 1;
                        productItemQuantityTextView.setText(String.valueOf(quantity));
                        addItem(itemId,
                                productName,
                                Integer.parseInt(productPrice),
                                quantity,
                                productUrl, "Full");
                        addItemQuantity(itemId,
                                quantity, "Full");
                        checkCart();
                        getItemQuantity(itemId);
                    } else {
                        quantity = 0;
                        helper.remove(TABLE_NAME, itemId);
                        helper.remove(CART_ITEM_QUANTITY, itemId);
                        productItemQuantityTextView.setText("0");
                        checkCart();
                    }
                }
            }
        });

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent checkout_activity = new Intent(ProductDescription.this, CheckoutActivity.class);
                startActivity(checkout_activity);

//                Intent checkout_activity = new Intent(ProductDescription.this, UserCheckoutActivity.class);
//                startActivity(checkout_activity);
            }
        });


        //  Product Dialog coding start here.

        incrementQty.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                currentItemQuantity += 1;
                productItemQuantity.setText(String.valueOf(currentItemQuantity));

            }
        });

        decrementQty.setOnClickListener(new View.OnClickListener() {

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

        cancel = (Button) productDialog.findViewById(R.id.button_cancel);
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

        add = (Button) productDialog.findViewById(R.id.button_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addItem(itemId,
                        productName,
                        Integer.parseInt(whichPrice(selected_price)),
                        currentItemQuantity,
                        productUrl,
                        selected_price);
                addItemQuantity(itemId, currentItemQuantity, selected_price);

                int qty = getItemQuantity(itemId);
                checkCart();

                //Log.d("onADD: ", qty + "");
                productItemQuantityTextView.setText(qty + "");

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

                int qty = getItemQuantityOfType(itemId, "Quarter");
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

                int qty = getItemQuantityOfType(itemId, "Half");
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

                int qty = getItemQuantityOfType(itemId, "Full");
                if (qty > 0)
                    productItemQuantity.setText(String.valueOf(qty));
                else
                    productItemQuantity.setText("1");
            }
        });


    }

    public String whichPrice(String type) {

        String price = "";

        if (type == "Full") {
            price = productPrice;
        }
        if (type == "Half") {
            price = productPriceHalf;
        }
        if (type == "Quarter") {
            price = productPriceQtr;
        }

        return price;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_product_description, menu);
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

    public class LoadProductInfo extends AsyncTask<String, String, String> {

        String response;

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            try {
                URL url = new URL(getUrl());
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(3000);
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();

                Log.d("Product DES", url.toString());
                if (connection.getResponseCode() == 200) {
                    return response = ConvertInputStream.toString(connection.getInputStream());
                } else {
                    Log.d("Message", connection.getResponseCode() + connection.getInputStream().toString());
                    return "error";
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "error";
            } finally {
                connection.disconnect();
             }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (connectionDetect == null)
                connectionDetect = new ConnectionDetector(ProductDescription.this);

            if (s.equals(null) || s.equals("") || s.equals("error")) {
                connectionDetect.isServerNotRunning();
            } else {
                JSONObject object = null;
                JSONObject response = null;

                try {
                    increment.setClickable(true);
                    decrement.setClickable(true);
                    quantity = getItemQuantity(itemId);
                    productItemQuantityTextView.setText(getItemQuantity(itemId) + "");

                    object = new JSONObject(s);
                    if (object.getInt("Success") == 1) {
                        response = object.getJSONObject("data");
                        if (response.has("Description")) {
                            String description = response.getString("Description");
                            productDescriptionTextView.setText(description.replaceAll("\\|", ",").replaceAll("�", " "));
                        } else {
                            productDescriptionTextView.setText("A flat, leavened bread of northwest India, made of wheat flour stuffed with gobhi and baked in a tandoor and served with butter");
                        }

                        JSONObject object2, object3;
                        String[] ingredientName;
                        String[] ingredientImage;
                        if (response.has("Ingre")) {

                            JSONArray ingreArray = response.getJSONArray("Ingre");
                            ingredientImage = new String[ingreArray.length()];
                            ingredientName = new String[ingreArray.length()];
                            for (int y = 0; y < ingreArray.length(); y++) {
                                object2 = ingreArray.getJSONObject(y);
                                for (int t = 0; t < object2.names().length(); t++) {
                                    Log.v("INGRE", "key = " + object2.names().getString(t) + " value = " + object2.get(object2.names().getString(t)));
                                    ingredientName[y] = object2.names().getString(t);
                                    ingredientImage[y] = object2.get(object2.names().getString(t)).toString();
                                }
                            }
                            GridViewAdapter adapter = new GridViewAdapter(ingredientName, ingredientImage);
                            gridView.setAdapter(adapter);

                        }
                        HashMap<String, String> nutrition = new HashMap<>();
                        if (response.has("NutritionInfo")) {

                            JSONArray nutriArray = response.getJSONArray("NutritionInfo");
                            for (int y = 0; y < nutriArray.length(); y++) {
                                object3 = nutriArray.getJSONObject(y);
                                for (int k = 0; k < object3.names().length(); k++) {
                                    Log.v("NutritionInfo", "key = " + object3.names().getString(k) + " value = " + object3.get(object3.names().getString(k)));
                                    nutrition.put(object3.names().getString(k), object3.get(object3.names().getString(k)).toString());
                                }
                            }
                        }

                        float[] formattedAmount = new float[0];

                        for (HashMap.Entry<String, String> entry : nutrition.entrySet()) {

                            Object[] unFormattedAmount;
                            formattedAmount = new float[4];

                            unFormattedAmount = nutrition.values().toArray();

                            Object[] formattedName;
                            formattedName = nutrition.keySet().toArray();

                            for (int i1 = 0; i1 < formattedName.length; i1++) {
                                if (formattedName[i1].toString().toLowerCase().contains("fat")) {
                                    first.setText(String.valueOf(formattedName[i1].toString() + " - " + unFormattedAmount[i1].toString()));
                                } else if (formattedName[i1].toString().toLowerCase().contains("protein")) {
                                    second.setText(String.valueOf(formattedName[i1].toString() + " - " + unFormattedAmount[i1].toString()));
                                } else if (formattedName[i1].toString().toLowerCase().contains("carbo")) {
                                    third.setText(String.valueOf(formattedName[i1].toString() + " - " + unFormattedAmount[i1].toString()));
                                } else if (formattedName[i1].toString().toLowerCase().contains("calor")) {
                                    fourth.setText(String.valueOf(formattedName[i1].toString() + " - " + unFormattedAmount[i1].toString()));
                                }
                            }

                            for (int j = 0; j < formattedName.length; j++) {
                                if (formattedName[j].toString().toLowerCase().contains("fat")) {
                                    formattedAmount[0] = Float.parseFloat(String.valueOf(unFormattedAmount[j]).replaceAll("[^0-9.]", ""));
                                } else if (formattedName[j].toString().toLowerCase().contains("protein")) {
                                    formattedAmount[1] = Float.parseFloat(String.valueOf(unFormattedAmount[j]).replaceAll("[^0-9.]", ""));
                                } else if (formattedName[j].toString().toLowerCase().contains("carbo")) {
                                    formattedAmount[2] = Float.parseFloat(String.valueOf(unFormattedAmount[j]).replaceAll("[^0-9.]", ""));
                                }

                                formattedAmount[3] = 0;
                                    /*if (formattedName[j].toString().contains("Cal")) {
                                        //do nothing
                                    } else {
                                        formattedAmount[j] = Float.parseFloat(String.valueOf(unFormattedAmount[j]).replaceAll("[^0-9.]", ""));
                                    }*/
                            }

                            descriptionView.setVisibility(View.VISIBLE);
                            loadingView.setVisibility(View.GONE);
                            createPieAnimation(formattedAmount);
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    object = null;
                    response = null;
                    // array =null;
                }
            }
        }
    }

    public void addItem(String id, String name, int price, int quantity, String image_url, String type) {

        ContentValues values = new ContentValues();
        values.put("ITEM_ID", id);
        values.put("ITEM_NAME", name);
        values.put("ITEM_PRICE", price);
        values.put("ITEM_QUANTITY", quantity);
        values.put("ITEM_IMAGE_URL", image_url);
        values.put("ITEM_TOTAL_PRICE", quantity * price);
        if (type.isEmpty())
            type = "Full";

        values.put("ITEM_TYPE", type);

        helper.insertData(TABLE_NAME, values, id, type);
    }

    public void addItemQuantity(String id, int quantity, String type) {

        ContentValues values = new ContentValues();
        values.put("ITEM_ID", id);
        values.put("ITEM_QUANTITY", quantity);
        if (type.isEmpty())
            type = "Full";
        values.put("ITEM_TYPE", type);

        helper.insertCartItemQuantityData(CART_ITEM_QUANTITY, values, id, type);
    }


    public void checkCart() {

        cursor = helper.getData(TABLE_NAME);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    footerCheckout.setVisibility(View.GONE);
                    if (mainlayout != null)
                        mainlayout.setPadding(0, 0, 0, 0);
//                    if(textchef!=null)
//                        textchef.setPadding(0,0,0,-25);

                }
            });
        } else {
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    footerCheckout.setVisibility(View.VISIBLE);
                    if (mainlayout != null)
                        mainlayout.setPadding(0, 0, 0, 55);

//                    if(textchef!=null)
//                        textchef.setPadding(0,0,0,-35);
                }
            });
        }

        updateTotalAmount(cursor);
    }

    public void updateTotalAmount(Cursor cursor) {
        cursor.moveToFirst();
        int totalAmount = 0;

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            totalAmount += cursor.getInt(cursor.getColumnIndex("ITEM_TOTAL_PRICE"));
        }

        totalAmountTextView.setText("₹ " + totalAmount);


    }

    public int getItemQuantity(String id) {
        int quantity = 0;

        itemQuantity = helper.getItemQuantityData(CART_ITEM_QUANTITY, id);

        try {
            if (itemQuantity.moveToFirst()) {
                for (int i = 0; i < itemQuantity.getCount(); i++) {
                    itemQuantity.moveToPosition(i);
                    int value = itemQuantity.getInt(itemQuantity.getColumnIndex("ITEM_QUANTITY"));
                    quantity += value;
                }
            } else {
                quantity = 0;
            }
        } catch (Exception e) {
            System.out.print(e);
        }
        Log.d("getItemQty: ProductDes", String.valueOf(quantity) + " " + itemQuantity.toString());

        return quantity;
    }

    public int getItemQuantityOfType(String id, String type) {
        int quantity = 0;

        try {

            Cursor cursor = helper.getItemQuantityDataOfType(TABLE_NAME, id, type);
            if (cursor.moveToFirst()) {
                cursor.moveToPosition(0);
                quantity = cursor.getInt(cursor.getColumnIndex("ITEM_QUANTITY"));
                return quantity;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return quantity;
    }

    public boolean isDifferentTypeSelected(String id) {
        boolean status = false;

//        if(!productPrice.equals("") && productPrice.equals(null) && productPrice.isEmpty())
//        {
//            if(getItemQuantityOfType(id, "Full") > 0) {
//                status = false;
//            }
//        }

        if (!productPriceHalf.equals("") && !productPriceHalf.equals(null) && !productPriceHalf.isEmpty()) {
            if (getItemQuantityOfType(id, "Half") > 0) {
                status = true;
            }
        }

        if (!productPriceQtr.equals("") && !productPriceQtr.equals(null) && !productPriceQtr.isEmpty()) {

            if (getItemQuantityOfType(id, "Quarter") > 0) {
                status = true;
            }
        }

        return status;
    }


    public class GridViewAdapter extends BaseAdapter {

        String[] ingredients;
        String[] images;

        public GridViewAdapter(String[] ingredients, String[] images) {
            this.ingredients = ingredients;
            this.images = images;
        }

        @Override
        public int getCount() {
            return ingredients.length;
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
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(ProductDescription.this).inflate(R.layout.ingredients_grid_item, null);

            ImageView image = (ImageView) view.findViewById(R.id.ingredientPhoto);

            if (images[position] != null && !images[position].toString().equals("no")) {
                Picasso.with(ProductDescription.this).load(images[position]).placeholder(R.drawable.appimg).into(image);
            } else {
                Picasso.with(ProductDescription.this).load(R.drawable.appimg).into(image);
            }
            TextView name = (TextView) view.findViewById(R.id.ingredientName);
            name.setText(ingredients[position]);

            return view;
        }
    }
}
