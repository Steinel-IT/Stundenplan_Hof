package com.steinel_it.stundenplanhof.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.steinel_it.stundenplanhof.R;

public class SetupFragmentOne extends Fragment {

    public static SetupFragmentOne newInstance() {
        SetupFragmentOne fragment = new SetupFragmentOne();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setup_one, container, false);
    }
}