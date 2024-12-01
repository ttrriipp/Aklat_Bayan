package com.example.aklatbayan;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class UserFragment extends Fragment {
    Dialog logout;
    private Button lgt, lgt2, cncl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        lgt = view.findViewById(R.id.btnLogout);
        lgt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutpopup();
            }
        });
        return view;
    }

    private void logoutpopup() {
        logout= new Dialog(requireContext(), R.style.Dialog_style);
        logout.setContentView(R.layout.activity_downloads);
        logout.getWindow().setBackgroundDrawableResource(R.drawable.viewtext);

    }


}