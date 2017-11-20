package com.tadqa.android.fragment;


import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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

import com.squareup.picasso.Picasso;
import com.tadqa.android.util.API_LINKS;
import com.tadqa.android.util.ConnectionDetector;
import com.tadqa.android.accessibility.ConvertInputStream;
import com.tadqa.android.accessibility.ExpandedListView;
import com.tadqa.android.accessibility.SolrData;
import com.tadqa.android.activity.MenuActivity;
import com.tadqa.android.pojo.BannerModel;
import com.tadqa.android.pojo.MenuModel;
import com.tadqa.android.R;
import com.tadqa.android.viewpagerindicator.CirclePageIndicator;

public class AlaCarteFragment extends Fragment {

    SolrData data;
    View view;
    private static ViewPager mPager;
    private static CirclePageIndicator indicator;
    private static int currentPage = 0;
    List<BannerModel> bannersList = new ArrayList<>();
    List<MenuModel> Menulist = new ArrayList<>();
    private static int NUM_PAGES = 0;
    //    private static final Integer[] IMAGES = {R.drawable.baner1, R.drawable.baner2, R.drawable.baner3, R.drawable.baner5, R.drawable.baner7};
    ExpandedListView listView;
    HttpURLConnection connection;
    String[] imageUrl;
    ProgressDialog dialog;
    InputStream stream;
    Timer timer;
    boolean isLoading = false;
    SlidingImage_Adapter bannerAdapter;
    MenuAdapter menudaapter;
    Banner banner;
    MenuClass menuclass;
    String menuresponse="";
    String bannerresponse = "";
    TextView Menu, starter;

    //    private ArrayList<Integer> ImagesArray = new ArrayList<Integer>();
    public AlaCarteFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
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
//            ((ViewGroup) view).removeAllViews();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("On Start", "Enter");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        view = inflater.inflate(R.layout.newlayout, null);
        getDataFromBanner();
        getDataFromMenu();
        mPager = (ViewPager) view.findViewById(R.id.pager);
        data = (SolrData) getContext().getApplicationContext();
        listView = (ExpandedListView) view.findViewById(R.id.listitem);
        listView.setExpanded(true);
        starter = (TextView) view.findViewById(R.id.item_name);
        Menu = (TextView) view.findViewById(R.id.textMenu);
        Menu.setTextSize(17);
        indicator = (CirclePageIndicator)
                view.findViewById(R.id.indicator);

        init();

        bannerAdapter = new SlidingImage_Adapter();
        mPager.setAdapter(bannerAdapter);
        indicator.setViewPager(mPager);
        listView.setAdapter(new MenuAdapter(getActivity(),Menulist));



        return view;
    }
    protected void getDataFromBanner() {
        try {
            if (SolrData.getBannerList().size() == 0) {
                AlaCarteFragment.this.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        banner = new Banner();
                        banner.execute();
                    }
                });
            } else {
                //Set meals in global/application context
                bannersList = data.getBannerList();
                mPager.setAdapter(new SlidingImage_Adapter());
                Log.d("get BAnners", bannersList.size() + ",");
            }
        } catch (Exception ex) {
        }
    }

    protected void getDataFromMenu() {
        try {
            if (SolrData.getMenuList().size() == 0) {
                AlaCarteFragment.this.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        menuclass = new MenuClass();
                        menuclass.execute();
                    }
                });
            } else {

//                Menulist = SolrData.getMenuList();
//                listView.setAdapter(new MenuAdapter(getActivity(),Menulist));
//                Log.d("get BAnners", Menulist.size() + ",");
            }
        } catch (Exception ex) {
        }
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
            inflater = LayoutInflater.from(getActivity());
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
            Picasso.with(getActivity()).load(bannersList.get(position).getImgUrl().toString()).placeholder(R.drawable.appimg).into(imageView);
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
                        isLoading = true;
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
                if (!ConnectionDetector.isConnectingToInternet(getActivity())) {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
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
                    new ConnectionDetector(getContext()).isServerNotRunning();
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

    private class MenuClass extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Menulist = new ArrayList<>();
            if (SolrData.getMenuList().size() == 0) {
                try {
                    URL starterUrl = new URL(API_LINKS.ALL_CATEGORY_URL);
                    connection = (HttpURLConnection) starterUrl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    if (connection.getResponseCode() == 200) {
                        stream = new BufferedInputStream(connection.getInputStream());
                        menuresponse = ConvertInputStream.toString(stream);
                        Log.d("Data", menuresponse);
                        isLoading = true;
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
                Menulist = SolrData.getMenuList();
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
                            //menu = new FetchMenu();
                            //menu.execute();
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
                    responseJson = new JSONObject(menuresponse);
                    docs = responseJson.getJSONArray("Category");

                    for (int i = 0; i < docs.length(); i++) {
                        MenuModel model = new MenuModel();
                        model.setVisibility(docs.getJSONObject(i).getString("isVisible"));
                        model.setName(docs.getJSONObject(i).getString("Category"));
                        model.setUrl(docs.getJSONObject(i).getString("Imagepath"));
                        if(docs.getJSONObject(i).getString("isVisible").equalsIgnoreCase("true"))
                        Menulist.add(model);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (menudaapter == null)
                    listView.setAdapter(new MenuAdapter(getActivity(),Menulist));

            }
        }



    }

    private class MenuAdapter extends BaseAdapter {
        Context context;
        List<MenuModel> itemList;
        @Override
        public int getCount() {
            return Menulist.size();
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
        public View getView(int position, View convertView, ViewGroup parent) {
            ProductHolder holder;

            if (convertView == null) {
                holder = new ProductHolder();
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                //convertView = inflater.inflate(R.layout.food_grid_component, null);
                convertView = inflater.inflate(R.layout.menuitem_layout, null);
                holder.itemName = (TextView) convertView.findViewById(R.id.item_name);
                holder.itemImage = (ImageView) convertView.findViewById(R.id.productImage);
                convertView.setTag(holder);


            } else {

                holder = (ProductHolder) convertView.getTag();
            }

            final View vw = convertView;
            imageUrl = new String[Menulist.size()];
//            if (ITEM_FETCHED < TOTAL_ITEMS) {
//                fetch();
//
//            }

            String titleCaseValue = Menulist.get(position).getName().toString();
            String imageurl = Menulist.get(position).getUrl().toString();
            holder.itemName.setText(titleCaseValue);
            if (imageUrl.length != 0) {
                Picasso.with(getActivity()).load(imageurl).into(holder.itemImage);
            } else {
                Picasso.with(getActivity()).load(R.drawable.appimg).into(holder.itemImage);
            }

            holder.itemImage.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Intent productDescriptionIntent = new Intent(getActivity(), MenuActivity.class);
//                    data.setCurrentMenuPosition(position);
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

