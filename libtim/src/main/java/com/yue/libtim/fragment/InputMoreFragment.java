package com.yue.libtim.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yue.libtim.R;
import com.yue.libtim.utils.soft.SoftKeyBoardUtil;


public class InputMoreFragment extends Fragment {

    public InputMoreFragment() {
        // Required empty public constructor
    }


    public static InputMoreFragment newInstance() {
        InputMoreFragment fragment = new InputMoreFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input_more, container, false);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = SoftKeyBoardUtil.getSoftKeyBoardHeight();
        Log.i("shimySoft",params.height+"");
        view.setLayoutParams(params);
        return view;
    }
}
