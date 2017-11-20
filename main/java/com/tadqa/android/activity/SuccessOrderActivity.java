package com.tadqa.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tadqa.android.R;
import com.tadqa.android.accessibility.SolrData;
import com.tadqa.android.util.ConnectionDetector;
import com.tadqa.android.util.DatabaseHelper;

public class SuccessOrderActivity extends AppCompatActivity {

    Context mContext = SuccessOrderActivity.this;

    private String TABLE_NAME = "CART_DATA";
    DatabaseHelper helper;
    Toolbar appBar;
    String number;
    TextView amount, info;
    SolrData solrdata;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ConnectionDetector.isConnectingToInternet(mContext)) {

            setContentView(R.layout.activity_success_order);
            appBar = (Toolbar) findViewById(R.id.app_bar);
            setSupportActionBar(appBar);
            amount = (TextView) findViewById(R.id.AmountPayable);
            // Calling Application class (see application tag in AndroidManifest.xml)
            solrdata = (SolrData) getApplication().getApplicationContext();
            String total = String.valueOf(solrdata.getGrandTotal());
            solrdata.setOrderID("");
            info = (TextView) findViewById(R.id.info);
            amount.setText("Amount Payable : " + getResources().getString(R.string.Rupee) + String.format("%.2f", Float.valueOf(total)));
            helper = new DatabaseHelper(SuccessOrderActivity.this);
            helper.open();
            helper.clearItemCart();
            helper.clearQuantityCart();
            helper.close();
            number = MenuActivity.Contact;
            info.setText("* Please contact " + number + " for any query.");
            button = (Button) findViewById(R.id.btnviewOrder);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent order = new Intent(SuccessOrderActivity.this, OrderHistory.class);
                    order.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(order);
                }
            });
        } else {
            setContentView(R.layout.netconnection);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.succes_order_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.home:
                Intent intent = new Intent(SuccessOrderActivity.this, ChooseLocality.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
//        Log.d("onBackPressed: SUCESS", solrData.isDelhi + " | " + solrData.isNoida);
//        if (solrData.isDelhi) {
//            SharedPreferences.Editor editor;
//            SharedPreferences sharedPreferences;
//            String USER_DETAILS = "USER_DETAIL";
//            sharedPreferences = getApplicationContext().getSharedPreferences(USER_DETAILS, Context.MODE_PRIVATE);
//            editor = sharedPreferences.edit();
//            editor.putInt("USER_LOCALITY_INT", 0);
//            editor.apply();
//            Intent intent = new Intent(SuccessOrderActivity.this, TabActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//        } else if (solrData.isNoida) {
//            SharedPreferences.Editor editor;
//            SharedPreferences sharedPreferences;
//            String USER_DETAILS = "USER_DETAIL";
//            sharedPreferences = getApplicationContext().getSharedPreferences(USER_DETAILS, Context.MODE_PRIVATE);
//            editor = sharedPreferences.edit();
//            editor.putInt("USER_LOCALITY_INT", 1);
//            editor.apply();
//            Intent intent = new Intent(SuccessOrderActivity.this, TabActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//        } else {
        Intent intent = new Intent(SuccessOrderActivity.this, ChooseLocality.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
//        }
    }
}
