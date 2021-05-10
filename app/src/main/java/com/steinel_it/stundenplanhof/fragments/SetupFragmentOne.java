package com.steinel_it.stundenplanhof.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.steinel_it.stundenplanhof.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SetupFragmentOne#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SetupFragmentOne extends Fragment {


    public SetupFragmentOne() {
    }

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