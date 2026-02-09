package com.example.medilink.Doctorside;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

import com.example.medilink.R;
import com.google.android.material.button.MaterialButton;

public class Report_fragment extends Fragment {

    private MaterialButton open_report_form;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_fragment, container, false);

        open_report_form = view.findViewById(R.id.Open_report_form);

        open_report_form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Correctly start the ReportForm activity
                Intent intent = new Intent(requireContext(), ReportForm.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
