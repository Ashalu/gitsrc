package com.tadqa.android.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tadqa.android.R;
import com.tadqa.android.accessibility.SolrData;
import com.tadqa.android.accessibility.TypeFaceUtil;
import com.tadqa.android.adapter.PagerAdapter;
import com.tadqa.android.fragment.DrawerFragment;
import com.tadqa.android.fragment.FragmentOne;
import com.tadqa.android.fragment.FragmentThree;
import com.tadqa.android.fragment.FragmentTwo;
import com.tadqa.android.pojo.FoodItem;
import com.tadqa.android.pojo.LocationModel;
import com.tadqa.android.util.API_LINKS;
import com.tadqa.android.util.ConnectionDetector;
import com.tadqa.android.util.ParseJson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Vector;

import developer.shivam.perfecto.OnNetworkRequest;
import developer.shivam.perfecto.Perfecto;
import developer.shivam.perfecto.Printer;

public class ChooseLocality extends AppCompatActivity {

    ProgressDialog dialog;
    ProgressBar pbLocationLoading;
    Toolbar toolbar;
    List<Fragment> fragments;
    String USER_DETAILS = "USER_DETAIL";
    SharedPreferences sharedPreferences, loginSharedPreferences;
    SharedPreferences.Editor editor, loginEditor;
    ViewPager viewPager;
    Button[] dots;
    boolean isLoggedIn = false;
    DrawerLayout mDrawerLayout;
    SolrData solrData;
    String[] locationArray = new String[0];
    int position = -1;
    private Context mContext = ChooseLocality.this;
    private Spinner spinner;
    public static String number;
    public boolean time;
    Button goButton;
    private final int PERMISSION_CODE = 100;

    @Override
    protected void onStart() {
        super.onStart();
        SolrData.setIsChooseLocalityOnTop(true);
        clearGuestUser();
    }

    void clearGuestUser() {
        /**
         * If solrData is null this condition will
         *  avoid force closing of app on launch.
         */
        if (solrData != null) {
            solrData.setareaPincode("");
            solrData.setUserAddress("");
            solrData.setUserNumber("");
            solrData.setUserName("");
            solrData.isAlreadyExists = false;
            solrData.setOtpCode("");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        SolrData.setIsChooseLocalityOnTop(false);
    }

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
        setContentView(R.layout.activity_choose_locality);
        solrData = (SolrData) getApplicationContext();

        /**
         * Creating search list and saving it globally.
         */
        createSearchList();

        TypeFaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Roboto-Regular.ttf");
        TextView tvTagLine = (TextView) findViewById(R.id.tagline);

        tvTagLine.setTextColor(getResources().getColor(R.color.Black));
        tvTagLine.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "fonts/Roboto-Regular.ttf"));

        /**
         * Getting sharedPreferences in use to store a boolean for launching the login
         * activity or not. For 'true' it means that login activity is to be launched
         * and false means user id logged in and not need to call Login Activity.
         */

        loginSharedPreferences = getSharedPreferences("LOGIN_DETAILS", Context.MODE_PRIVATE);
        loginEditor = loginSharedPreferences.edit();

        isLoggedIn = loginSharedPreferences.getBoolean("isLoggedIn", false);

        solrData.setLoggedInStatus(isLoggedIn);

        sharedPreferences = getSharedPreferences(USER_DETAILS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean("DO_START_STARTUP_ACTIVITY", true);

        if (!isLoggedIn) {
            if (sharedPreferences.getBoolean("DO_START_STARTUP_ACTIVITY", true)) {
                Intent intentStartupActivity = new Intent(ChooseLocality.this, StartupActivity.class);
                startActivity(intentStartupActivity);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED &&
                            checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, PERMISSION_CODE);
                    }
                }
            }
        }

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.inflateMenu(R.menu.menu_main);
        ColorDrawable drawable = new ColorDrawable();
        drawable.setAlpha(0);
        toolbar.setBackgroundDrawable(drawable);
        toolbar.setTitle("");//("tadqa!");

        setSupportActionBar(toolbar);

        //setBackgroundWallpapers();

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {

                getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            } else {

                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }

            fakeStatusBar = (LinearLayout) findViewById(R.id.fakeStatusBar);
            fakeStatusBar.setBackgroundColor(getResources().getColor(R.color.Black));
            int height = (int) getResources().getDimension(R.dimen.default_status_height);
            fakeStatusBar.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height));
        }*/

        mDrawerLayout = (DrawerLayout) findViewById(R.id.mainDrawer);
        mDrawerLayout.setDrawerShadow(R.drawable.drawable_shadow, GravityCompat.START);
        DrawerFragment mDrawerFragment = (DrawerFragment) getSupportFragmentManager().findFragmentById(R.id.drawerFragment);
        mDrawerFragment.setUp(mDrawerLayout, null);

        spinner = (Spinner) findViewById(R.id.chooseOutlets);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                solrData.locationPosition = parent.getSelectedItemPosition();
                solrData.locationName = locationArray[parent.getSelectedItemPosition()];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                solrData.locationPosition = 0;
                solrData.locationName = locationArray[0];
            }
        });

        pbLocationLoading = (ProgressBar) findViewById(R.id.pbLoading);
        /**
         * Fetch location list and set location names to
         *  spinner.
         */
        fetchLocationList();

        goButton = (Button) findViewById(R.id.buttonGo);
        goButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (solrData.isTHMClose && solrData.isTTEClose) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChooseLocality.this);
                    builder.setTitle("Closed !!");

                    builder.setIcon(R.drawable.ic_portable_wifi_off_black_48dp);
                    builder.setMessage("Tadqa is Closed !  We'll wait until you get back tomorrow.");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogIn, int which) {
                            dialogIn.dismiss();
                        }
                    });
                    builder.show();
                } else {
                    checkArea();
                }
            }
        });
        goButton.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "fonts/Roboto-Regular.ttf"));

    }


    public void checkArea() {
        if (ConnectionDetector.isConnectingToInternet(mContext)) {
            if (locationArray.length != 0) {
                dialog = new ProgressDialog(mContext);
                dialog.setMessage("Please wait");
                dialog.show();
                Perfecto.with(ChooseLocality.this)
                        .fromUrl(API_LINKS.AREA_DETAIL_URL + locationArray[spinner.getSelectedItemPosition()].replace(" ", "%20"))
                        .ofTypeGet()
                        .connect(new OnNetworkRequest() {

                            @Override
                            public void onSuccess(String response) {
                                JSONObject userResponseJSONObject = null;
                                try {
                                    JSONObject responseJson = new JSONObject(response);
                                    if (responseJson.has("Success")) {
                                        if (responseJson.getInt("Success") == 1) {
                                            userResponseJSONObject = responseJson.getJSONObject("AreaDetail");

                                            if (userResponseJSONObject.has("isTHM")) {
                                                String IsHomeMade = userResponseJSONObject.getString("isTHM");
                                                if (IsHomeMade.equals("TRUE")) {
                                                    solrData.isHomeMade = true;
                                                } else
                                                    solrData.isHomeMade = false;

                                            }
                                            if (userResponseJSONObject.has("isTTE")) {
                                                String isTadqaTandoorExpress = userResponseJSONObject.getString("isTTE");
                                                if (isTadqaTandoorExpress.equals("TRUE")) {
                                                    solrData.isTadqaTandoorExpress = true;
                                                } else
                                                    solrData.isTadqaTandoorExpress = false;
                                            }
                                            if (userResponseJSONObject.has("Contact"))
                                                number = userResponseJSONObject.getString("Contact");

                                            if (solrData.isHomeMade) {
                                                solrData.selectedDeliveryLocationArray = new String[100];
                                                Intent intent = new Intent(ChooseLocality.this, HomeMadeActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                            } else if (solrData.isTadqaTandoorExpress) {
                                                solrData.selectedDeliveryLocationArray = new String[100];
                                                Intent intent = new Intent(ChooseLocality.this, TandoorExpressActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                            } else {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(ChooseLocality.this);
                                                builder.setTitle("Closed !!");

                                                builder.setIcon(R.drawable.ic_portable_wifi_off_black_48dp);
                                                builder.setMessage("Tadqa is Closed !  We 'll wait until you get back tomorrow.");
                                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogIn, int which) {
                                                        dialogIn.dismiss();
                                                    }
                                                });
                                                builder.show();
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
                                        if ((dialog != null) && dialog.isShowing()) {
                                            dialog.dismiss();
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onFailure(int i, String s, String s1) {

                            }
                        });
            } else {
                Toast.makeText(mContext, "Please select your area", Toast.LENGTH_SHORT).show();
            }
        } else {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
            dialogBuilder.setMessage("Please check your internet connection");
            dialogBuilder.setTitle("No internet connection");
            dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            dialogBuilder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    checkArea();
                }
            });
            dialogBuilder.show();
        }
    }

    public void setDot(int position) {
        for (int i = 0; i < 3; i++) {
            dots[i].setBackgroundResource(R.drawable.circular_button_white);
        }
        dots[position].setBackgroundResource(R.drawable.circular_button);
    }

    public void setBackgroundWallpapers() {
        fragments = new Vector<>();
        fragments.add(Fragment.instantiate(this, FragmentOne.class.getName()));
        fragments.add(Fragment.instantiate(this, FragmentTwo.class.getName()));
        fragments.add(Fragment.instantiate(this, FragmentThree.class.getName()));

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(0);

        dots = new Button[3];
        dots[0] = (Button) findViewById(R.id.one);
        dots[1] = (Button) findViewById(R.id.two);
        dots[2] = (Button) findViewById(R.id.three);

        dots[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0, true);
            }
        });

        dots[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1, true);
            }
        });

        dots[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(2, true);
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        dots[0].setBackgroundResource(R.drawable.circular_button);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (this.mDrawerLayout != null) {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                this.mDrawerLayout.closeDrawer(GravityCompat.START);
            } else
                super.onBackPressed();
        }
    }

    public void createSearchList() {
        /**
         * This code snippet is for fetching the searchable items
         *  from database and store it globally to avoid repeated network query.
         */
        if (ConnectionDetector.isConnectingToInternet(mContext)) {
            Perfecto.with(ChooseLocality.this)
                    .fromUrl(API_LINKS.ALL_ITEMS_URL)
                    .ofTypeGet()
                    .connect(new OnNetworkRequest() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onSuccess(String response) {
                            if (!response.equals("") || !response.equals(null)) {
                                List<FoodItem> foodItemList = ParseJson.ofFoodItemsSearchList(response);
                                SolrData.setSearchList(foodItemList);
                            }
                        }

                        @Override
                        public void onFailure(int i, String s, String s1) {

                        }
                    });
        } else {
            Log.d(getResources().getString(R.string.network_offline), "Unable to fetch search list due to no internet connection");
        }
    }

    public void fetchLocationList() {
        if (ConnectionDetector.isConnectingToInternet(mContext)) {
            Perfecto.with(ChooseLocality.this)
                    .fromUrl(API_LINKS.AREA_LOCATION_URL)
                    .ofTypeGet()
                    .connect(new OnNetworkRequest() {

                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onSuccess(String response) {
                            locationArray = ParseJson.ofTypeLocation(response);
                            SolrData.setLocationList(locationArray);

                            spinner.setVisibility(View.VISIBLE);
                            pbLocationLoading.setVisibility(View.INVISIBLE);
                            spinner.setAdapter(new ArrayAdapter<>(mContext, R.layout.custom_spinner_layout, R.id.localityName, locationArray));
                        }

                        @Override
                        public void onFailure(int i, String s, String s1) {
                            Printer.writeError("Error loading network list");
                            Printer.writeError(s);
                        }

                    });
        } else {
            Log.d(getResources().getString(R.string.network_offline), "Cannot fetch location list");
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
            dialogBuilder.setMessage("Please check your internet connection");
            dialogBuilder.setTitle("No internet connection");
            dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            dialogBuilder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    fetchLocationList();
                }
            });
            dialogBuilder.show();
        }
    }

}
