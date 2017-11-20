package com.tadqa.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tadqa.android.R;


public class FragmentThree extends Fragment {


    public FragmentThree() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_three, null);

        ImageView three = (ImageView) view.findViewById(R.id.three);
        three.setBackgroundDrawable(getResources().getDrawable(R.drawable.ban2));
        return inflater.inflate(R.layout.fragment_three, container, false);
    }


}
