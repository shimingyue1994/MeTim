package com.yue.libtim.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yue.libtim.R;
import com.yue.libtim.utils.soft.SoftKeyBoardUtil;


public class InputFaceFragment extends Fragment {

    public InputFaceFragment() {
        // Required empty public constructor
    }


    public static InputFaceFragment newInstance() {
        InputFaceFragment fragment = new InputFaceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
        View view = inflater.inflate(R.layout.fragment_input_face, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_face);
        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = SoftKeyBoardUtil.getSoftKeyBoardHeight();
        recyclerView.setLayoutParams(params);
        return view;
    }
}
