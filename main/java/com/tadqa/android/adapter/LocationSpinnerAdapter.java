package com.tadqa.android.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tadqa.android.R;
import com.tadqa.android.pojo.LocationModel;

import java.util.List;

public class LocationSpinnerAdapter extends BaseAdapter {

    private Context mContext;
    private List<LocationModel> locationList;

    public LocationSpinnerAdapter(Context context, List<LocationModel> localityList) {
        mContext = context;
        locationList = localityList;
    }

    @Override
    public int getCount() {
        return locationList.size();
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

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_spinner_layout, null);
        TextView localityName = (TextView) view.findViewById(R.id.localityName);
        localityName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        localityName.setText(locationList.get(position).getCity());

        return view;
    }
}
