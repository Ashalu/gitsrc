package com.tadqa.android.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tadqa.android.R;
import com.tadqa.android.accessibility.ConvertInputStream;
import com.tadqa.android.accessibility.ExpandedListView;
import com.tadqa.android.accessibility.SolrData;
import com.tadqa.android.adapter.CustomSpinnerAdapterToolbar;
import com.tadqa.android.fragment.DrawerFragment;
import com.tadqa.android.pojo.BannerModel;
import com.tadqa.android.pojo.MenuModel;
import com.tadqa.android.util.API_LINKS;
import com.tadqa.android.util.ConnectionDetector;
import com.tadqa.android.util.DatabaseHelper;
import com.tadqa.android.viewpagerindicator.CirclePageIndicator;

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

import developer.shivam.perfecto.OnNetworkRequest;
import developer.shivam.perfecto.Perfecto;

import static com.tadqa.android.R.id.pager;

public class HomeMadeActivity extends AppCompatActivity {
    MenuClass menuclass;
    List<MenuModel> Mealslist = new ArrayList<>();
    private HttpURLConnection connection;
    InputStream stream;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    String[] imageUrl;
    Spinner spinner;
    List<BannerModel> bannersList = new ArrayList<>();
    SolrData solrData;
    String[] locationArray = new String[0];
    MenuAdapter menuadapter;
    Banner banner;
    SharedPreferences.Editor editor;
    SharedPreferences preferences;
    DatabaseHelper helper;
    String USER_DETAIL = "USER_DETAIL";
    int position;
    ExpandedListView listView;
    CoordinatorLayout coordinatorLayout;
    String mealsresponse = "", bannerresponse = "";
    CirclePageIndicator indicator;
    SlidingImage_Adapter bannerAdapter;
    private ViewPager mPager;
    DrawerLayout mDrawerLayout;
    private boolean isHomeMade;
    private boolean isTadqaTandoorExpress;
    private int datavalue;
    public static String Contact;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newlayout);

        solrData = (SolrData) getApplicationContext();

        getDataFromMenu();

        getDataFromBanner();

        listView = (ExpandedListView) findViewById(R.id.listitem);
        listView.setExpanded(true);

        locationArray = solrData.getLocationList();

        preferences = getSharedPreferences(USER_DETAIL, Context.MODE_PRIVATE);
        editor = preferences.edit();
        mPager = (ViewPager) findViewById(pager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitleTextAppearance(getApplicationContext(), R.style.appBartextsize);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.mainDrawer);
        mDrawerLayout.setDrawerShadow(R.drawable.drawable_shadow, GravityCompat.START);
        DrawerFragment drawerFragment = (DrawerFragment) getSupportFragmentManager().findFragmentById(R.id.drawerFragment);
        drawerFragment.setUp(mDrawerLayout, toolbar);


        indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.snackbarCoordinatorLayout);
        bannerAdapter = new SlidingImage_Adapter();

        mPager.setAdapter(bannerAdapter);
        indicator.setViewPager(mPager);
        listView.setAdapter(new MenuAdapter(getApplicationContext(), Mealslist));
        indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        helper = new DatabaseHelper(HomeMadeActivity.this);
        helper.open();
        helper.clearItemCart();
        helper.clearQuantityCart();
        helper.close();
        init();
        spinner = (Spinner) findViewById(R.id.chooseLocality);
        spinner.setAdapter(new CustomSpinnerAdapterToolbar(this, locationArray));
        spinner.setSelection(solrData.locationPosition);
        System.out.println(solrData.locationPosition);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(final AdapterView<?> parent, View view, final int position, long id) {
                Perfecto.with(HomeMadeActivity.this)
                        .fromUrl(API_LINKS.AREA_DETAIL_URL + locationArray[position].replace(" ", "%20"))
                        .ofTypeGet()
                        .connect(new OnNetworkRequest() {

                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onFailure(int i, String s, String s1) {

                            }

                            @Override
                            public void onSuccess(String s) {

                                JSONObject responseJson = null;
                                JSONObject UsrResponse = null;
                                try {
                                    responseJson = new JSONObject(s);
                                    Log.d("string", s);
                                    if (responseJson.has("Success")) {
                                        if (responseJson.getInt("Success") == 1) {
                                            UsrResponse = responseJson.getJSONObject("AreaDetail");
                                            if (UsrResponse.has("Area")) {
                                                String area = UsrResponse.getString("Area");
                                            }
                                            if (UsrResponse.has("isTHM")) {
                                                String IsHomeMade = UsrResponse.getString("isTHM");
                                                if (IsHomeMade.equalsIgnoreCase("true")) {
                                                    isHomeMade = true;
                                                    Contact = UsrResponse.getString("Contact");
                                                    solrData.locationPosition = parent.getSelectedItemPosition();
                                                    solrData.selectedDeliveryLocationArray = new String[100];
                                                } else {
                                                    solrData.isHomeMade = false;
                                                    solrData.isTadqaTandoorExpress = true;
                                                    solrData.selectedDeliveryLocationArray = new String[100];
                                                    solrData.locationPosition = parent.getSelectedItemPosition();
                                                    Contact = UsrResponse.getString("Contact");
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeMadeActivity.this);
                                                    builder.setTitle("Location !!");

                                                    builder.setIcon(R.drawable.ic_add_location_black_48dp);
                                                    builder.setMessage("Do you want to change your location to " + locationArray[position]);

                                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogIn, int which) {
                                                            //solrData.isHomeMade = false;
                                                            //solrData.isTadqaTandoorExpress = true;
                                                            solrData.locationPosition = parent.getSelectedItemPosition();
                                                            System.out.println(solrData.locationPosition);
                                                            Intent chooseLocality = new Intent(getApplicationContext(), TandoorExpressActivity.class);
                                                            chooseLocality.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            chooseLocality.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            startActivity(chooseLocality);
                                                            finish();
                                                            dialogIn.dismiss();
                                                        }
                                                    });
                                                    builder.setCancelable(false);
                                                    builder.show();

                                                }
                                            }
                                        } else if (responseJson.getInt("Success") == 0) {
                                            Toast.makeText(getApplication(), "Response Not Found!", Toast.LENGTH_LONG).show();
                                        } else if (responseJson.getInt("Success") == 4) {
                                            Toast.makeText(getApplication(), "Invalid Inputs", Toast.LENGTH_LONG).show();

                                        } else if (responseJson.getInt("Success") == 5) {
                                            Toast.makeText(getApplication(), "Server Internal Error", Toast.LENGTH_LONG).show();
                                        } else if (responseJson.getInt("Success") == 6) {
                                            Toast.makeText(getApplication(), "Routing Error", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    protected void getDataFromBanner() {
        try {
            if (SolrData.getBannerList().size() == 0) {
                banner = new Banner();
                banner.execute();
            } else {
                //Set meals in global/application context
                bannersList = solrData.getBannerList();
                mPager.setAdapter(new SlidingImage_Adapter());
            }
        } catch (Exception ex) {
        }
    }

    private class Banner extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            bannersList = new ArrayList<>();
            if (SolrData.getBannerList().size() == 0) {
                try {
                    URL starterUrl = new URL(API_LINKS.BANNERS_URL);
                    connection = (HttpURLConnection) starterUrl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    if (connection.getResponseCode() == 200) {
                        stream = new BufferedInputStream(connection.getInputStream());
                        bannerresponse = ConvertInputStream.toString(stream);
                        Log.d("Data", bannerresponse);
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
                bannersList = SolrData.getBannerList();
            }
            return "error";
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if (response.equals("error") || response.equals("") || response.equals(null)) {
                if (!ConnectionDetector.isConnectingToInternet(getApplicationContext())) {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getApplicationContext());
                    builder.setTitle("Error !!");

                    builder.setIcon(R.drawable.ic_portable_wifi_off_black_48dp);
                    builder.setMessage("No Internet Connection!  ");


                    builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            //menu = new FetchMenu();
                            //menu.execute();
                        }
                    });
                    builder.show();
                } else
                    new ConnectionDetector(getApplicationContext()).isServerNotRunning();
            } else {
                JSONObject responseJson = null;
                JSONObject response1 = null;
                JSONArray docs = null;

                try {
                    responseJson = new JSONObject(bannerresponse);
                    docs = responseJson.getJSONArray("Banners");

                    for (int i = 0; i < docs.length(); i++) {
                        BannerModel banner = new BannerModel();
                        banner.setName(docs.getJSONObject(i).getString("Banner"));
                        banner.setId(docs.getJSONObject(i).getString("ID"));
                        banner.setImgUrl(docs.getJSONObject(i).getString("Imagepath"));
                        bannersList.add(banner);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (bannerAdapter == null)
                    bannerAdapter = new SlidingImage_Adapter();
                mPager.setAdapter(bannerAdapter);

            }
        }


    }

    protected void getDataFromMenu() {
        try {
            if (SolrData.getMealsList().size() == 0) {
                menuclass = new MenuClass();
                menuclass.execute();
            } else {
//                Set meals in global/application context
                Mealslist = solrData.getMealsList();
                listView.setAdapter(new MenuAdapter(getApplicationContext(), Mealslist));
//                Log.d("get BAnners", Mealslist.size() + ",");
            }
        } catch (Exception ex) {
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void init() {
        final float density = getResources().getDisplayMetrics().density;
        indicator.setRadius(5 * density);
        NUM_PAGES = bannersList.size();
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 5000, 5000);
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

    }

    public class SlidingImage_Adapter extends PagerAdapter {
        private LayoutInflater inflater;

        public SlidingImage_Adapter() {
            inflater = LayoutInflater.from(getApplicationContext());
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return bannersList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false);
            assert imageLayout != null;
            final ImageView imageView = (ImageView) imageLayout
                    .findViewById(R.id.image);
            Picasso.with(getApplicationContext()).load(bannersList.get(position).getImgUrl().toString()).placeholder(R.drawable.appimg).into(imageView);
            view.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }


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
                if (!ConnectionDetector.isConnectingToInternet(getApplicationContext())) {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getApplicationContext());
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
                    new ConnectionDetector(getApplicationContext()).isServerNotRunning();
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
                        SolrData.setMealsList(Mealslist);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (menuadapter == null)
                    listView.setAdapter(new MenuAdapter(getApplicationContext(), Mealslist));
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
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
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
                Picasso.with(getApplicationContext()).load(imageurl).into(holder.itemImage);
            } else {
                Picasso.with(getApplicationContext()).load(R.drawable.appimg).into(holder.itemImage);
            }

            holder.itemImage.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Intent productDescriptionIntent = new Intent(getApplicationContext(), MenuActivity.class);
                    productDescriptionIntent.putExtra("activity", "first");
                    solrData.setCurrentMenuPosition(position);
                    startActivity(productDescriptionIntent);
                }
            });
            return convertView;

        }


    }

    static class ProductHolder {
        TextView itemName;
        ImageView itemImage;

    }
}

