package com.tadqa.android.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tadqa.android.notification.NotificationPreviewActivity;
import com.tadqa.android.pojo.Notification;
import com.tadqa.android.util.ConnectionDetector;
import com.tadqa.android.accessibility.SolrData;
import com.tadqa.android.accessibility.TypeFaceUtil;
import com.tadqa.android.activity.About;
import com.tadqa.android.activity.Help;
import com.tadqa.android.activity.LoginActivity;
import com.tadqa.android.activity.OrderHistory;
import com.tadqa.android.activity.Rates;
import com.tadqa.android.activity.TabActivity;
import com.tadqa.android.activity.UserProfile;
import com.tadqa.android.pojo.NavDrawerItem;
import com.tadqa.android.util.NonScrollListView;
import com.tadqa.android.R;
import com.tadqa.android.util.NotificationStorageHelper;

import org.w3c.dom.Text;

import java.util.List;

public class DrawerFragment extends Fragment {

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    View view;
    TextView userName;
    ImageView imageview;
    LinearLayout signOutLayout;
    Button signInButton;
    SharedPreferences loginSharedPreferences;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
    String USER_DETAILS = "USER_DETAIL";
    String[] userListItemsIfLoggedIn = {"Home", "Profile", "Order", "Notifications"};
    String[] userListItemsIfNotLoggedIn = {"Home"};
    String[] appListItems = {"A La Carte"};
    String[] settingsListItems = {"About Us", "Rate Us", "Help", "Call Us"};
    List<NavDrawerItem> mDrawerItems;
    boolean isLoggedIn = false;
    NonScrollListView userDrawerList;
    NonScrollListView appDrawerList;
    NonScrollListView settingsDrawerList;
    Typeface custom_font;
    ConnectionDetector connectionDetector;
    SolrData solrdata;

    int[] userDrawerItemIcon = {
            R.mipmap.ic_home_black_18dp,
            R.mipmap.ic_face_black_18dp,
            R.mipmap.ic_history_black_18dp,
            R.drawable.ic_notifications_none_black_24dp,
            R.mipmap.ic_local_dining_black_18dp,
            R.mipmap.ic_restaurant_black_18dp,
            R.mipmap.ic_info_black_18dp,
            R.mipmap.ic_grade_black_18dp,
            R.mipmap.ic_help_black_18dp,
            R.mipmap.ic_call_black_18dp};

    public DrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        connectionDetector = new ConnectionDetector(getContext().getApplicationContext());
        solrdata = (SolrData) getActivity().getApplicationContext();
        sharedPreferences = getActivity().getSharedPreferences(USER_DETAILS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt("USER_LOCALITY_INT", 0);

        preferences = getActivity().getSharedPreferences("LOGIN_DETAILS", getActivity().MODE_PRIVATE);
        editor = preferences.edit();
        isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        custom_font = Typeface.createFromAsset(getResources().getAssets(), "fonts/Roboto-Regular.ttf");
    }

    @Override
    public void onResume() {
        super.onResume();
        preferences = getActivity().getSharedPreferences("LOGIN_DETAILS", getActivity().MODE_PRIVATE);
        isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        setCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        TypeFaceUtil.overrideFont(getContext(), "SERIF", "fonts/Roboto-Regular.ttf");

        if (connectionDetector == null)
            connectionDetector = new ConnectionDetector(getContext().getApplicationContext());

        view = inflater.inflate(R.layout.fragment_drawer, null);
        userName = (TextView) view.findViewById(R.id.textView_user_name);
        imageview = (ImageView) view.findViewById(R.id.imageView_user_icon);
        signOutLayout = (LinearLayout) view.findViewById(R.id.signOutLayout);
        signInButton = (Button) view.findViewById(R.id.signInButton);
        userDrawerList = (NonScrollListView) view.findViewById(R.id.listView_user_drawer_items);
        appDrawerList = (NonScrollListView) view.findViewById(R.id.listView_app_items);
        settingsDrawerList = (NonScrollListView) view.findViewById(R.id.listView_settings_items);
        setCurrentUser();

        signOutLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isLoggedIn) {
                    mDrawerLayout.closeDrawers();
                    SolrData.setLoggedInStatus(false);
                    editor.putBoolean("isLoggedIn", false);
                    editor.apply();
                    isLoggedIn = false;
                    clearUserInfo();
                    setCurrentUser();


                }
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!isLoggedIn) {
                    mDrawerLayout.closeDrawers();
                    Handler handler = new Handler();
                    Runnable delayRunnable = new Runnable() {

                        @Override
                        public void run() {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.putExtra("which_activity", "DrawerFragment");
                            startActivity(intent);
                        }
                    };
                    handler.postDelayed(delayRunnable, 500);
                }
            }
        });
        return view;
    }

    void clearUserInfo() {
        try {
            //set isLoggedIn in application variable
            solrdata.setLoggedInStatus(isLoggedIn);
            solrdata.setUserName("");
            solrdata.setOrderID("");
            solrdata.setareaPincode("");
            solrdata.setcheckoutItemsArr(null);
            solrdata.setcheckoutSelectedTime("");
            solrdata.setUserAddress("");
            solrdata.setUserDOB("");
            solrdata.setUserEmail("");
            solrdata.setUserGender("");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setUp(DrawerLayout drawerLayout, Toolbar toolbar) {
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),
                mDrawerLayout,
                toolbar,
                R.string.open,
                R.string.close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }


    public void setCurrentUser() {

        /**
         * Getting login status from sharedPreferences
         * and updating the navigation drawer accordingly.
         */
        loginSharedPreferences = getActivity().getSharedPreferences("LOGIN_DETAILS", Context.MODE_PRIVATE);
        if (!isLoggedIn) {
            userName.setText("Guest User");
            signOutLayout.setVisibility(View.INVISIBLE);
            signInButton.setVisibility(View.VISIBLE);

            userDrawerList.setAdapter(new UserListAdapter(userListItemsIfNotLoggedIn, "user"));
            userDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 0:
                            if (SolrData.isChooseLocalityOnTop()) {
                                mDrawerLayout.closeDrawers();
                            } else {
                                mDrawerLayout.closeDrawers();
                                Handler handler = new Handler();
                                Runnable delayRunnable = new Runnable() {

                                    @Override
                                    public void run() {
                                        getActivity().finish();
                                    }
                                };
                                handler.postDelayed(delayRunnable, 500);
                            }
                            break;
                    }
                }
            });

        } else {

            String name = loginSharedPreferences.getString("userName", "Guest User");
            userName.setText("Hi ! " + name);
            signInButton.setVisibility(View.INVISIBLE);
            signOutLayout.setVisibility(View.VISIBLE);

            if (loginSharedPreferences.getString("gender", "Male").equals("Male")) {

                imageview.setImageDrawable(getResources().getDrawable(R.drawable.user_icon_male));
            } else {

                imageview.setImageDrawable(getResources().getDrawable(R.drawable.user_icon_female));
            }

            userDrawerList.setAdapter(new UserListAdapter(userListItemsIfLoggedIn, "user"));
            userDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 0:
                            if (SolrData.isChooseLocalityOnTop()) {
                                mDrawerLayout.closeDrawers();
                            } else {
                                mDrawerLayout.closeDrawers();
                                Handler handler = new Handler();
                                Runnable delayRunnable = new Runnable() {

                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        /*Intent intent = new Intent(getActivity(), ChooseLocality.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);*/
                                        getActivity().finish();
                                    }
                                };
                                handler.postDelayed(delayRunnable, 500);
                            }
                            break;

                        case 1:
                            mDrawerLayout.closeDrawers();
                            Handler handler = new Handler();
                            Runnable delayRunnable = new Runnable() {

                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    Intent userProfile = new Intent(getActivity(), UserProfile.class);
                                    startActivity(userProfile);
                                }
                            };
                            handler.postDelayed(delayRunnable, 500);

                            break;

                        case 2:

                            if (isLoggedIn) {
                                mDrawerLayout.closeDrawers();
                                handler = new Handler();
                                delayRunnable = new Runnable() {

                                    @Override
                                    public void run() {
                                        Intent order = new Intent(getActivity(), OrderHistory.class);
                                        startActivity(order);
                                    }
                                };
                                handler.postDelayed(delayRunnable, 500);
                            }

                            break;

                        case 3:
                            if (isLoggedIn) {
                                mDrawerLayout.closeDrawers();
                                handler = new Handler();
                                delayRunnable = new Runnable() {

                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(getContext(), NotificationPreviewActivity.class);
                                        startActivity(intent);
                                    }
                                };
                                handler.postDelayed(delayRunnable, 500);
                            }
                            break;
                    }
                }
            });
        }

        appDrawerList.setAdapter(new UserListAdapter(appListItems, "app"));
        appDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        editor = sharedPreferences.edit();
                        editor.putInt("USER_LOCALITY_INT", 0);
                        editor.apply();

                        Intent intentMeals = new Intent(getActivity(), TabActivity.class);
                        startActivity(intentMeals);

                        break;

                    case 1:
                        Intent alaCarte = new Intent(getActivity(), TabActivity.class);
                        startActivity(alaCarte);

                        editor = sharedPreferences.edit();
                        editor.putInt("USER_LOCALITY_INT", 1);
                        editor.apply();

                        break;
                }
            }
        });

        settingsDrawerList.setAdapter(new UserListAdapter(settingsListItems, "settings"));
        settingsDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {

                    case 0:
                        Intent aboutUsActivity = new Intent(getActivity(), About.class);
                        startActivity(aboutUsActivity);
                        break;
                    case 1:
                        Intent feedback = new Intent(getActivity(), Rates.class);
                        startActivity(feedback);


                        break;
                    case 2:
                        Intent help = new Intent(getActivity(), Help.class);
                        startActivity(help);
                        break;
                    case 3:
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom);

                        builder.setTitle("Do You want to call the tadqa helpline?");
                        builder.setMessage("Click yes to Make Call!");
                        builder.setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        String number = "8010402402";
                                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                        callIntent.setData(Uri.parse("tel:" + number));
                                        startActivity(callIntent);
                                    }
                                });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // mDrawerLayout.closeDrawer(mDrawerList);
                                dialog.cancel();
                            }
                        });
                        builder.show();
                        break;

                    default:
                        break;
                }
            }
        });


    }


    public class UserListAdapter extends BaseAdapter {

        String[] list;
        int spacing = 0;
        int layout = R.layout.user_drawer_item_list;
        ;

        public UserListAdapter(String[] list, String whichList) {
            this.list = list;
            if (whichList.equals("user")) {
                spacing = 0;
                layout = R.layout.user_drawer_item_list;
            } else if (whichList.equals("app")) {
                spacing = 4;
                layout = R.layout.user_drawer_item_list;
            } else if (whichList.equals("settings")) {
                spacing = 6;
                //layout = R.layout.simple_drawer_item_list;
                layout = R.layout.user_drawer_item_list;
            }
        }

        @Override
        public int getCount() {
            return list.length;
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

            View view = LayoutInflater.from(getActivity()).inflate(layout, null);

            TextView textView = (TextView) view.findViewById(R.id.text);
            textView.setTypeface(custom_font);

            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            imageView.setImageResource(userDrawerItemIcon[spacing + position]);
            imageView.setColorFilter(getResources().getColor(R.color.md_grey_700));

            textView.setText(list[position]);

            TextView tvNotificationCount = (TextView) view.findViewById(R.id.tvNotificationCount);
            if (list[position].equals("Notifications")) {
                tvNotificationCount.setVisibility(View.VISIBLE);
                NotificationStorageHelper notificationStorageHelper = new NotificationStorageHelper(getContext());
                notificationStorageHelper.open();
                int size = notificationStorageHelper.getNotifications().getCount();
                tvNotificationCount.setText(String.valueOf(size));
            } else {
                tvNotificationCount.setVisibility(View.INVISIBLE);
            }

            return view;
        }
    }


}
