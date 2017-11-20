package com.tadqa.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tadqa.android.R;


public class FragmentOne extends Fragment {

    String[] localityNames = {"North Delhi", "South Delhi", "East Delhi", "West Delhi"};
    String location_name;

    public FragmentOne() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_one, null);

        ImageView one = (ImageView) view.findViewById(R.id.one);
        return view;
    }


}
