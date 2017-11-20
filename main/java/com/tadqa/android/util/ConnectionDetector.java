package com.tadqa.android.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.tadqa.android.activity.ChooseLocality;
import com.tadqa.android.activity.TabActivity;
import com.tadqa.android.R;

public class ConnectionDetector {

    private static Context mContext;

    public ConnectionDetector(Context context) {
        this.mContext = context;
    }

    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void isServerNotRunning() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogCustom);
        builder.setTitle("Alert!");
        builder.setIcon(R.drawable.ic_cloud_off_black_24dp);
        builder.setMessage("Oops Something went wrong ! Sorry for inconvenience.Please try again later.");


        builder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent intent = new Intent(mContext, ChooseLocality.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mContext.startActivity(intent);
                        dialog.dismiss();

                    }
                });

        builder.show();

    }

    public void setTypeFont(TextView text) {

//        text.setTextAppearance(mContext, android.R.style.TextAppearance_Small);
        text.setTextColor(mContext.getResources().getColor(R.color.Black));
        text.setTypeface(Typeface.createFromAsset(mContext.getResources().getAssets(), "fonts/Roboto-Light.ttf"));
    }

    public static void isRetryConnection() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogCustom);
        builder.setTitle("Alert!");
        builder.setIcon(R.drawable.ic_portable_wifi_off_black_48dp);
        builder.setMessage("No Connection  - Retry Again");


        builder.setCancelable(false)
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

//                        Intent intent = new Intent(mContext, ChooseLocality.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        mContext.startActivity(intent);
                        dialog.dismiss();

                    }
                });

        builder.show();

    }

    public static void RetryConnectionSnackbar(CoordinatorLayout coordinatorLayout) {
        try {

            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });


            // Changing message text color
            snackbar.setActionTextColor(Color.RED);

            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        } catch (Exception ex) {
        }
    }

    public static void isChangeLocation(final String From, final String To) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogCustom);
        builder.setTitle("Alert!");
        builder.setIcon(R.drawable.dish);
        // TextView text=new TextView(mContext);
        String t = "Your cart contain dishes from " + From + ". Do you want to change your location to " + To + " and Discard the selection? ";
        //text.setText(t);
        // text.setTextSize(7);
        builder.setMessage(t);//(text.getText().toString());


        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (From.contains("EastDelhi")) {
                    SharedPreferences.Editor editor;
                    SharedPreferences sharedPreferences;
                    String USER_DETAILS = "USER_DETAIL";
                    sharedPreferences = mContext.getSharedPreferences(USER_DETAILS, Context.MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    editor.putInt("USER_LOCALITY_INT", 1);
                    editor.apply();

                    Intent intent = new Intent(mContext, TabActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    dialog.dismiss();
                } else {
                    SharedPreferences.Editor editor;
                    SharedPreferences sharedPreferences;
                    String USER_DETAILS = "USER_DETAIL";
                    sharedPreferences = mContext.getSharedPreferences(USER_DETAILS, Context.MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    editor.putInt("USER_LOCALITY_INT", 0);
                    editor.apply();

                    Intent intent = new Intent(mContext, TabActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    dialog.dismiss();
                }

                ClearCart();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog

                if (From.contains("Noida")) {
                    SharedPreferences.Editor editor;
                    SharedPreferences sharedPreferences;
                    String USER_DETAILS = "USER_DETAIL";
                    sharedPreferences = mContext.getSharedPreferences(USER_DETAILS, Context.MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    editor.putInt("USER_LOCALITY_INT", 1);
                    editor.apply();

                    Intent intent = new Intent(mContext, TabActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    dialog.dismiss();
                } else {
                    SharedPreferences.Editor editor;
                    SharedPreferences sharedPreferences;
                    String USER_DETAILS = "USER_DETAIL";
                    sharedPreferences = mContext.getSharedPreferences(USER_DETAILS, Context.MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    editor.putInt("USER_LOCALITY_INT", 0);
                    editor.apply();

                    Intent intent = new Intent(mContext, TabActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    dialog.dismiss();
                }
                dialog.dismiss();
            }
        });

        builder.show();


    }

    static void ClearCart() {
        try {

            DatabaseHelper helper;
            helper = new DatabaseHelper(mContext);
            helper.open();
            helper.clearItemCart();
            helper.clearQuantityCart();
            helper.close();
        } catch (Exception ex) {
        }
    }


//    public void setMediumFont(TextView txtview)
//    {
////        if (Build.VERSION.SDK_INT <= 23) {
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////                txtview.setTextAppearance(android.R.style.TextAppearance_Medium);
////            }
////
////        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            txtview.setTextAppearance(android.R.style.TextAppearance_Small);
//        }
//        txtview.setTextColor(mContext.getResources().getColor(R.color.primary));
//        txtview.setTypeface(Typeface.createFromAsset(mContext.getResources().getAssets(), "fonts/Roboto-Regular.ttf"));
//    }
//
//    public void setTextAppearance(TextView txtview) {
//
////        if (Build.VERSION.SDK_INT < 22) {
////
////            txtview.setTextAppearance(mContext, android.R.style.TextAppearance_Small);
////        }
////        else {
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////                txtview.setTextAppearance(android.R.style.TextAppearance_Medium);
////            }
////        }
//        txtview.setTextAppearance(mContext, android.R.style.TextAppearance_Small);
//        txtview.setTextColor(mContext.getResources().getColor(R.color.primary));
//
//        txtview.setTypeface( Typeface.createFromAsset(mContext.getResources().getAssets(), "fonts/Roboto-Regular.ttf"));
//    }
//    public void setTextAppearance(TextView txtview,int color) {
//
////        if (Build.VERSION.SDK_INT < 22) {
////
////            txtview.setTextAppearance(mContext, android.R.style.TextAppearance_Small);
////        }
////        else {
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////                txtview.setTextAppearance(android.R.style.TextAppearance_Medium);
////            }
////        }
//        txtview.setTextAppearance(mContext, android.R.style.TextAppearance_Small);
//        txtview.setTextColor(color);//(mContext.getResources().getColor(R.color.primary));
//        if(txtview.getText().toString().contains("Quality food"))
//           txtview.setTypeface(Typeface.createFromAsset(mContext.getResources().getAssets(), "fonts/Roboto-Light.ttf"));
//        else
//            txtview.setTypeface( Typeface.createFromAsset(mContext.getResources().getAssets(), "fonts/Roboto-Regular.ttf"));
//    }
//
//    public void setTextAppearance(Button btn) {
//
////        if (Build.VERSION.SDK_INT < 22) {
////
////            btn.setTextAppearance(mContext, android.R.style.TextAppearance_Small);
////        }
////        else {
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////                btn.setTextAppearance(android.R.style.TextAppearance_Medium);
////            }
////        }
//        btn.setTextAppearance(mContext, android.R.style.TextAppearance_Small);
//        btn.setTextColor(mContext.getResources().getColor(R.color.primary));
//        btn.setTypeface( Typeface.createFromAsset(mContext.getResources().getAssets(), "fonts/Roboto-Regular.ttf"));
//    }


}
