package com.tadqa.android.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.tadqa.android.util.ConnectionDetector;
import com.tadqa.android.accessibility.TypeFaceUtil;
import com.tadqa.android.fragment.OrderHistoryFragment;
import com.tadqa.android.fragment.RecentOrderFragment;
import com.tadqa.android.R;

public class OrderHistory extends AppCompatActivity {

    Toolbar actionBar;
    TabLayout tabLayout;
    ViewPager viewPager;
    String[] tabItems = { "Current Order", "History Orders" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ConnectionDetector.isConnectingToInternet(this)) {
            setContentView(R.layout.activity_order_history);
            TypeFaceUtil.overrideFont(getApplication(), "SERIF", "fonts/Roboto-Regular.ttf");

            init();
        }else
        {
            setContentView(R.layout.netconnection);
        }
    }

    private void init() {

        actionBar = (Toolbar) findViewById(R.id.app_bar);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        actionBar.setTitle("Your Orders");
        setSupportActionBar(actionBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        viewPager.setAdapter(new CustomFragmentPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200) {

            viewPager.setAdapter(new CustomFragmentPagerAdapter(getSupportFragmentManager()));
            tabLayout.setupWithViewPager(viewPager);
        }
    }

    public class CustomFragmentPagerAdapter extends FragmentPagerAdapter {

        public CustomFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {

                case 0 :
                    return new RecentOrderFragment();
                case 1 :
                    return new OrderHistoryFragment();
                  //return  new PaymentFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return tabItems.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabItems[position];
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_checkout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                Intent intent = new Intent(OrderHistory.this, ChooseLocality.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                onBackPressed();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Intent intent = new Intent(OrderHistory.this, ChooseLocality.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}
