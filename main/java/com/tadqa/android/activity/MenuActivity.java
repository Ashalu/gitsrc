package com.tadqa.android.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tadqa.android.R;
import com.tadqa.android.accessibility.SolrData;
import com.tadqa.android.accessibility.TypeFaceUtil;
import com.tadqa.android.fragment.CommonTabFragment;
import com.tadqa.android.util.DatabaseHelper;

public class MenuActivity extends AppCompatActivity {

    Toolbar actionBar;
    DatabaseHelper helper;
    LinearLayout footerCheckout;
    String TABLE_NAME = "CART_DATA";
    TextView totalAmountTextView;
    TabLayout tabLayout;
    TextView checkout;
    public ViewPager viewPager;
    Cursor cursor;
    public static String Contact;
    SolrData solrData;
    String[] tadqaTandoorExpressItems = {"Veg Starters", "Non-Veg Starter", "Veg Main Course", "Non-Veg Main Course", "Platter", "Rolls", "Rice", "Breads", "Extras"};
    String[] tadqaHomemadeItems = {"Meals", "Paratha", "Paneer", "Rolls", "Veg Section", "Chicken Section"};
    String itemType = "";
    String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        try {
            itemType = getIntent().getStringExtra("activity");
            if (itemType.equals("first")) {
                title = "Tadqa Homemade";
            } else if (itemType.equals("second")) {
                title = "Tadqa Tandoor Express";
            } else {
                Log.d("No Menu", itemType);
            }
        } catch (Exception e) {
            Log.d("Invalid intent extra", e.getMessage());
        }

        solrData = (SolrData) getApplicationContext();

        TypeFaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Roboto-Regular.ttf");

        actionBar = (Toolbar) findViewById(R.id.app_bar);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        totalAmountTextView = (TextView) findViewById(R.id.totalAmount);
        footerCheckout = (LinearLayout) findViewById(R.id.checkoutFooter);
        checkout = (TextView) findViewById(R.id.checkoutActivity);
        checkout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, CheckoutActivity.class);
                startActivity(intent);
            }
        });
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(2);
        actionBar.setTitle(title);
        actionBar.setTitleTextAppearance(getApplicationContext(), R.style.ToolbarTitle);
        setSupportActionBar(actionBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        solrData = (SolrData) getApplicationContext();
        helper = new DatabaseHelper(MenuActivity.this);
        helper.open();

        checkCart();
        viewPager.setAdapter(new CustomFragmentPagerAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(solrData.getCurrentMenuPosition());

        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            RelativeLayout relativeTabLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_item, tabLayout, false);
            TextView tabTextView = (TextView) relativeTabLayout.findViewById(R.id.tab_title);
            tabTextView.setText(tab.getText().toString().toUpperCase());
            tab.setCustomView(relativeTabLayout);
        }
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                tab.setText(tab.getText() + "1");
                //checkCart();
                // updateTotalAmount(cursor);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //checkCart();
                // updateTotalAmount(cursor);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        if(solrData.isHomeMade)
            Contact=HomeMadeActivity.Contact;
        else
            Contact=TandoorExpressActivity.Contact;
    }

    @Override
    public void onDestroy() {
        try {
            super.onDestroy();
            if (helper != null) {
                helper.close();
                helper = null;
            }
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
            solrData = null;
            footerCheckout = null;
            totalAmountTextView = null;
            tabLayout = null;
            checkout = null;
            viewPager = null;
            tadqaTandoorExpressItems = null;
            tadqaHomemadeItems = null;
        } catch (Exception ex) {
            super.onDestroy();
        }

    }

    @Override
    public void onStart() {
        try {
            super.onStart();
            checkCart();
        } catch (Exception ex) {
        }
    }

    public class CustomFragmentPagerAdapter extends FragmentPagerAdapter {

        public CustomFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

          if(itemType.equals("first")) {
              switch (position) {

                  case 0:
                      Bundle bundleOne = new Bundle();
                      bundleOne.putString("API_LINK", solrData.getApiLinks(10));
                      bundleOne.putString("Menu", "10");

                      CommonTabFragment fragmentOne = new CommonTabFragment();
                      fragmentOne.setArguments(bundleOne);
                      return fragmentOne;


                  case 1:
                      Bundle bundleTwo = new Bundle();
                      bundleTwo.putString("API_LINK", solrData.getApiLinks(11));
                      bundleTwo.putString("Menu", "11");
                      CommonTabFragment fragmentTwo = new CommonTabFragment();

                      fragmentTwo.setArguments(bundleTwo);
                      return fragmentTwo;

                  case 2:
                      CommonTabFragment fragmentThree = new CommonTabFragment();
                      Bundle bundleThree = new Bundle();
                      bundleThree.putString("API_LINK", solrData.getApiLinks(12));
                      bundleThree.putString("Menu","12");
                      fragmentThree.setArguments(bundleThree);
                      return fragmentThree;

                  case 3:
                      CommonTabFragment fragmentFour = new CommonTabFragment();
                      Bundle bundleFour = new Bundle();
                      bundleFour.putString("API_LINK", solrData.getApiLinks(13));
                      bundleFour.putString("Menu","13");
                      fragmentFour.setArguments(bundleFour);
                      return fragmentFour;


                  case 4:
                      CommonTabFragment fragmentFive = new CommonTabFragment();
                      Bundle bundleFive = new Bundle();
                      bundleFive.putString("API_LINK", solrData.getApiLinks(14));
                      bundleFive.putString("Menu","14");
                      fragmentFive.setArguments(bundleFive);
                      return fragmentFive;

                  case 5:
                      CommonTabFragment fragmentSix = new CommonTabFragment();
                      Bundle bundleSix = new Bundle();
                      bundleSix.putString("API_LINK", solrData.getApiLinks(15));
                      bundleSix.putString("Menu","15");
                      fragmentSix.setArguments(bundleSix);
                      return fragmentSix;
              }
          }
          else if(itemType.equals("second")) {
              switch (position) {

                  case 0:
                      Bundle bundleOne = new Bundle();
                      bundleOne.putString("API_LINK", solrData.getApiLinks(1));
                      bundleOne.putString("Menu", "1");

                      CommonTabFragment fragmentOne = new CommonTabFragment();
                      fragmentOne.setArguments(bundleOne);
                      return fragmentOne;


                  case 1:
                      Bundle bundleTwo = new Bundle();
                      bundleTwo.putString("API_LINK", solrData.getApiLinks(2));
                      bundleTwo.putString("Menu", "2");
                      CommonTabFragment fragmentTwo = new CommonTabFragment();

                      fragmentTwo.setArguments(bundleTwo);
                      return fragmentTwo;

                  case 2:
                      CommonTabFragment fragmentThree = new CommonTabFragment();
                      Bundle bundleThree = new Bundle();
                      bundleThree.putString("API_LINK", solrData.getApiLinks(7));

                      bundleThree.putString("Menu", "7");
                      fragmentThree.setArguments(bundleThree);
                      return fragmentThree;

                  case 3:
                      CommonTabFragment fragmentFour = new CommonTabFragment();
                      Bundle bundleFour = new Bundle();
                      bundleFour.putString("API_LINK", solrData.getApiLinks(8));
                      bundleFour.putString("Menu", "8");
                      fragmentFour.setArguments(bundleFour);
                      return fragmentFour;


                  case 4:
                      CommonTabFragment fragmentFive = new CommonTabFragment();
                      Bundle bundleFive = new Bundle();
                      bundleFive.putString("API_LINK", solrData.getApiLinks(6));
                      bundleFive.putString("Menu", "6");
                      fragmentFive.setArguments(bundleFive);
                      return fragmentFive;

                  case 5:
                      CommonTabFragment fragmentSix = new CommonTabFragment();
                      Bundle bundleSix = new Bundle();
                      bundleSix.putString("API_LINK", solrData.getApiLinks(4));
                      bundleSix.putString("Menu", "4");
                      fragmentSix.setArguments(bundleSix);
                      return fragmentSix;

                  case 6:
                      CommonTabFragment fragmentSeven = new CommonTabFragment();
                      Bundle bundleSeven = new Bundle();
                      bundleSeven.putString("API_LINK", solrData.getApiLinks(5));
                      bundleSeven.putString("Menu", "5");

                      fragmentSeven.setArguments(bundleSeven);
                      return fragmentSeven;

                  case 7:
                      CommonTabFragment fragmentEight = new CommonTabFragment();
                      Bundle bundleEight = new Bundle();
                      bundleEight.putString("API_LINK", solrData.getApiLinks(3));
                      bundleEight.putString("Menu", "3");
                      fragmentEight.setArguments(bundleEight);
                      return fragmentEight;

                  case 8:
                      CommonTabFragment fragmentNine = new CommonTabFragment();
                      Bundle bundleNine = new Bundle();
                      bundleNine.putString("API_LINK", solrData.getApiLinks(9));
                      bundleNine.putString("Menu", "9");
                      fragmentNine.setArguments(bundleNine);
                      return fragmentNine;
//                case 9:
//                    CommonTabFragment fragmentTen = new CommonTabFragment();
//                    Bundle bundleTen = new Bundle();
//                    bundleTen.putString("API_LINK", solrData.getApiLinks(10));
//                    bundleTen.putString("Menu", "10");
//                    fragmentTen.setArguments(bundleTen);
//         }           return fragmentTen;
              }
          }
            return null;
        }

        @Override
        public int getCount() {
            if (itemType.equals("first")) {
                return tadqaHomemadeItems.length;
            } else {
                return tadqaTandoorExpressItems.length;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (itemType.equals("first")) {
                return tadqaHomemadeItems[position];
            } else {
                return tadqaTandoorExpressItems[position];
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tab_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.searchButton:
                Intent intent = new Intent(MenuActivity.this, SearchActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void checkCart() {

        cursor = helper.getData(TABLE_NAME);
        //cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    footerCheckout.setVisibility(View.GONE);
                }
            });
        } else {
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    footerCheckout.setVisibility(View.VISIBLE);
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

}