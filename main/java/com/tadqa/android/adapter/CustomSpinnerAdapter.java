package com.tadqa.android.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tadqa.android.R;

public class CustomSpinnerAdapter extends BaseAdapter {

    Context context;
    String[] arrayOfNames;
    boolean isFromCheckout=false;



    public CustomSpinnerAdapter(Context context, String[] array,boolean isfrom) {
        this.context = context;
        arrayOfNames = array;
        isFromCheckout=isfrom;

    }

    @Override
    public int getCount() {

        int len = 0;
        if (arrayOfNames!=null && arrayOfNames.length > 0)
            len = arrayOfNames.length;
        return len;
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

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_spinner_layout, null);
        TextView localityName = (TextView) view.findViewById(R.id.localityName);
        localityName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        localityName.setText(arrayOfNames[position]);

        if(isFromCheckout)
        {
            localityName.setText(arrayOfNames[position]);
            localityName.setGravity(Gravity.LEFT);
            localityName.setPadding(15,5,5,10);
            localityName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        }
        return view;
    }
}
