package com.tadqa.android.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tadqa.android.accessibility.TypeFaceUtil;
import com.tadqa.android.R;

public class StartupActivity extends AppCompatActivity {

    Button loginActivity, registrationActivity;
    LinearLayout skipFooter;
    private int PERMISSION_CODE = 100;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("SMS READ PERMISSION", "Permission granted");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        TypeFaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Roboto-Regular.ttf");

        loginActivity = (Button) findViewById(R.id.button_login);
        registrationActivity = (Button) findViewById(R.id.button_sign_up);
        skipFooter = (LinearLayout) findViewById(R.id.skipFooter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, PERMISSION_CODE);
            }
        }


        skipFooter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                StartupActivity.this.finish();
            }
        });

        loginActivity.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent loginActivity = new Intent(StartupActivity.this, LoginActivity.class);
                loginActivity.putExtra("which_activity", "StartupActivity");
                startActivity(loginActivity);
                overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
            }
        });

        registrationActivity.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent registrationIntent = new Intent(StartupActivity.this, RegistrationActivity.class);
                startActivity(registrationIntent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
            }
        });
    }

}
