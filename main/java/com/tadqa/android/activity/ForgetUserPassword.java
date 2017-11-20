package com.tadqa.android.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tadqa.android.accessibility.NonSwipableViewPager;
import com.tadqa.android.adapter.PagerAdapter;
import com.tadqa.android.R;

import java.util.List;
import java.util.Vector;

public class ForgetUserPassword extends AppCompatActivity {

    NonSwipableViewPager viewPager;
    List<Fragment> fragments;
    LinearLayout fakeStatusBar;
    String userNumber = "";
    String userOtp = "";

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_user_password);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {

                getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            } else {

                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }

            fakeStatusBar = (LinearLayout) findViewById(R.id.fakeStatusBar);
            fakeStatusBar.setBackgroundColor(Color.parseColor("#0D47A1"));
            int height = (int) getResources().getDimension(R.dimen.default_status_height);
            fakeStatusBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
        }

        viewPager = (NonSwipableViewPager) findViewById(R.id.nonSwipableViewPager);
        fragments = new Vector<>();
        fragments.add(Fragment.instantiate(ForgetUserPassword.this, ForgetPasswordFragment.class.getName()));
        fragments.add(Fragment.instantiate(ForgetUserPassword.this, GeneratePasswordFragment.class.getName()));
        fragments.add(Fragment.instantiate(ForgetUserPassword.this, ChangePasswordFragment.class.getName()));

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        if(ReadMsgAllowed()){
            //If permission is already having then showing the toast
            Toast.makeText(ForgetUserPassword.this,"You already have the permission",Toast.LENGTH_LONG).show();
            //Existing the method with return
            return;
        }
        requestReadMsgPermission();
    }
    private void requestReadMsgPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)){

        }
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_SMS},200);
    }

    private boolean ReadMsgAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    public void setUserNumber(String number){
        userNumber = number;
    }

    public String getUserNumber(){
        return userNumber;
    }

    public void setUserOtp(String otp){
        userOtp = otp;
    }

    public String getUserOtp(){
        return userOtp;
    }

    public void setViewPagerGeneratePasswordPage(){
        viewPager.setCurrentItem(1);
    }

    public void setViewPagerChangePasswordPage(){
        viewPager.setCurrentItem(2);
    }
}
