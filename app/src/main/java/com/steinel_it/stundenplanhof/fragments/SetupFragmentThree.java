package com.steinel_it.stundenplanhof.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.steinel_it.stundenplanhof.R;
import com.steinel_it.stundenplanhof.SetupActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SetupFragmentOne#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SetupFragmentThree extends Fragment {


    public SetupFragmentThree() {
    }

    public static SetupFragmentThree newInstance() {
        return new SetupFragmentThree();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setup_three, container, false);
    }

    //TODO: Switch Kalender Sync
}