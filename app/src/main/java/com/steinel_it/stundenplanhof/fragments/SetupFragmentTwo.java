package com.steinel_it.stundenplanhof.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.steinel_it.stundenplanhof.R;
import com.steinel_it.stundenplanhof.interfaces.SetupValueInterface;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SetupFragmentTwo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SetupFragmentTwo extends Fragment {

    public static final int SELECT_CODE_COURSES = 0;
    public static final int SELECT_CODE_SEMESTER = 1;
    public static final int SELECT_CODE_YEAR = 2;

    private SetupValueInterface setupValueInterface;
    private View view;

    public static SetupFragmentTwo newInstance() {
        return new SetupFragmentTwo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_setup_two, container, false);
        setSpinnerSelect();
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            setupValueInterface = (SetupValueInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement SetupValueInterface");
        }
    }

    private void setSpinnerSelect() {
        Spinner spinnerCourse = view.findViewById(R.id.spinnerSetupCourse);
        Spinner spinnerSemester = view.findViewById(R.id.spinnerSetupSemester);
        Spinner spinnerYear = view.findViewById(R.id.spinnerSetupYear);

        //Set SemesterSpinner presetValue
        ArrayList<String> semesterPreset = new ArrayList<>();
        semesterPreset.add(getString(R.string.loadSemester));
        ArrayAdapter<String> spinnerSemesterPresetAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, semesterPreset);
        spinnerSemesterPresetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSemester.setAdapter(spinnerSemesterPresetAdapter);

        //Set YearSpinner presetValue
        ArrayList<String> yearPreset = new ArrayList<>();
        yearPreset.add(getString(R.string.loadYear));
        ArrayAdapter<String> spinnerYearPresetAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, yearPreset);
        spinnerYearPresetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(spinnerYearPresetAdapter);
        //Set On Item Choose
        spinnerCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                setupValueInterface.onSelectValue(SELECT_CODE_COURSES, pos - 1, (String) adapterView.getAdapter().getItem(pos));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        spinnerSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                setupValueInterface.onSelectValue(SELECT_CODE_SEMESTER, pos - 1, (String) adapterView.getAdapter().getItem(pos));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                setupValueInterface.onSelectValue(SELECT_CODE_YEAR, pos - 1, (String) adapterView.getAdapter().getItem(pos));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    public void setCourses(ArrayList<String> courses) {
        Spinner spinner = view.findViewById(R.id.spinnerSetupCourse);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, courses);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
    }

    public void setSemester(ArrayList<String> semester) {
        Spinner spinner = view.findViewById(R.id.spinnerSetupSemester);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, semester);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
    }

    public void setYears(ArrayList<String> years) {
        Spinner spinner = view.findViewById(R.id.spinnerSetupYear);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, years);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
    }
}