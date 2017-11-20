package com.tadqa.android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;

import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.tadqa.android.util.ConnectionDetector;
import com.tadqa.android.accessibility.SolrData;
import com.tadqa.android.accessibility.TypeFaceUtil;
import com.tadqa.android.util.DatabaseHelper;
import com.tadqa.android.fragment.AlaCarteFragment;
import com.tadqa.android.fragment.DrawerFragment;
import com.tadqa.android.fragment.MealsFragment;
import com.tadqa.android.R;

public class TabActivity extends AppCompatActivity {

    public ViewPager pager;
    TabLayout layout;
    String USER_DETAIL = "USER_DETAIL";
    String[] localityList = { "Noida"};
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    int position;
    Spinner spinner;
    LinearLayout fakeStatusBar;
    DrawerLayout mDrawerLayout;
    SolrData solrdata;
    

    Cursor cursor;
    String TABLE_NAME = "CART_DATA";
    DatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        solrdata=(SolrData)getApplicationContext();

        if (ConnectionDetector.isConnectingToInternet(this)) {
            setContentView(R.layout.activity_tab);

            TypeFaceUtil.overrideFont(getApplication(), "SERIF", "fonts/Roboto-Regular.ttf");

            preferences = getSharedPreferences(USER_DETAIL, Context.MODE_PRIVATE);
            editor = preferences.edit();
            position = preferences.getInt("USER_LOCALITY_INT", 0);
            Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
            TextView txtLocation =(TextView)findViewById(R.id.location);

            Intent intent = getIntent();
            String location = intent.getStringExtra("locality_name");
            toolbar.setTitle("Delivery to : " + location);

            txtLocation.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            // Spinner element

            toolbar.setTitleTextAppearance(getApplicationContext(), R.style.appBartextsize);
            setSupportActionBar(toolbar);


            /**
             * Setting drawer to top.
             */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {

                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    fakeStatusBar = (LinearLayout) findViewById(R.id.fakeStatusBar);
                    fakeStatusBar.setBackgroundColor(getResources().getColor(R.color.Black));
                    int height = (int) getResources().getDimension(R.dimen.default_status_height);
                    fakeStatusBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
                }
            }

            mDrawerLayout = (DrawerLayout) findViewById(R.id.mainDrawer);
            mDrawerLayout.setDrawerShadow(R.drawable.drawable_shadow, GravityCompat.START);
            DrawerFragment drawerFragment = (DrawerFragment) getSupportFragmentManager().findFragmentById(R.id.drawerFragmentSecond);
            drawerFragment.setUp(mDrawerLayout, toolbar);

            pager = (ViewPager) findViewById(R.id.viewPager);
            layout = (TabLayout) findViewById(R.id.tabLayout);


     pager.setAdapter(new CustomFragmentAdapter(getSupportFragmentManager()));
            pager.setOffscreenPageLimit(2);

            /*changes 01042016*/
            pager.setCurrentItem(position);

           layout.setupWithViewPager(pager);
//            spinner = (Spinner) findViewById(R.id.chooseLocality);
//            spinner.setAdapter(new CustomSpinnerAdapterToolbar(this, localityList));
//            spinner.setSelection(position);
//
//           spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    //After selecting the locality String will be updated
//                    editor.putInt("USER_LOCALITY_INT", position);
//                    //to set pager on required TAB i.e Alacarte for NOIDA
//                    pager.setCurrentItem(position);
//
//                    if(position==0) {
//                        solrData.isDelhi = true;
//                        solrData.isNoida = false;
//                    }
//                    else
//                    {
//                        solrData.isDelhi = false;
//                        solrData.isNoida = true;
//                    }
//                    checkCart(position);
//                    Log.d("TABActvity : ",position+" | "+solrData.isDelhi +" | "+ solrData.isNoida );
//
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//
//                }
//            });
//
//

            layout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    pager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
//                    pager.setCurrentItem(spinner.getSelectedItemPosition());
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    layout.getTabAt(position).select();
//                    spinner.setSelection(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        } else {
            setContentView(R.layout.netconnection);
        }
    }

    @Override
    public void onBackPressed() {
        if(this.mDrawerLayout!=null)
        {
            if(mDrawerLayout.isDrawerOpen(GravityCompat.START))
            {
                this.mDrawerLayout.closeDrawer(GravityCompat.START);
            }
            else
                super.onBackPressed();
        }
        else
            super.onBackPressed();

    }

    public class CustomFragmentAdapter extends FragmentPagerAdapter {

        String[] fragments = {"Homemade","Tandoor Express"};

        public CustomFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position)
            {
                case 0:
                    return new MealsFragment();


                case 1:

                    return new AlaCarteFragment();



            }
            return null;
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return fragments[position];
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tab_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.searchButton :
                Intent intent = new Intent(TabActivity.this, SearchActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String getTotalItemQuantity() {

        Cursor Quantity = helper.getData(TABLE_NAME);
        if(Quantity!=null) {
            if (Quantity.moveToFirst()) {
                return Quantity.getString(Quantity.getColumnIndex("ITEM_ID"));
            } else {
                return "";
            }
        }
        else return "";

    }
    public  void checkCart(int position)
    {
        // changes by varsha 05072016

        Log.d("On Start", "Enter");

        helper = new DatabaseHelper(getApplicationContext());
        helper.open();

        cursor = helper.getData(TABLE_NAME);
        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            String myId=getTotalItemQuantity();
            if(!myId.isEmpty()) {
                Log.d("getItemID Alacarte", myId);
                String ss=myId.replace("Item","");
                int idvalue=Integer.valueOf(ss);
                if(idvalue <= 8 && position==1)
                    new ConnectionDetector(TabActivity.this).isChangeLocation("Noida", "East Delhi");
                else if(idvalue >8 && position==0)
                    new ConnectionDetector(TabActivity.this).isChangeLocation("East Delhi","Noida");
            }
        }
    }
}
