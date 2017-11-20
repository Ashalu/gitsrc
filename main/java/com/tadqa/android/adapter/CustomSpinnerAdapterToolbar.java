package com.tadqa.android.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tadqa.android.pojo.LocationModel;
import com.tadqa.android.R;

import java.util.List;

public class CustomSpinnerAdapterToolbar extends BaseAdapter {

    Context context;
    String[] arrayOfNames;

    public CustomSpinnerAdapterToolbar(Context context, String[] array) {
        this.context = context;
        arrayOfNames = array;
    }

    @Override
    public int getCount() {
        return arrayOfNames.length;
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
        localityName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        if (arrayOfNames.length - 1 >= position)
            localityName.setText(arrayOfNames[position]);

        return view;
    }
}
