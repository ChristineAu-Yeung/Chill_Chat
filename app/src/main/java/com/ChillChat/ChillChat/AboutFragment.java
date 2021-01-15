package com.ChillChat.ChillChat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class AboutFragment extends Fragment {
    /**
     * Gets fired when the fragment is first created
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Set variables
        View root = inflater.inflate(R.layout.fragment_about, container, false);
        Button btnTOS = (Button) root.findViewById(R.id.btnTOS);
        Button btnPP = (Button) root.findViewById(R.id.btnPP);

        //Button listener for btnTOS
        btnTOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open Terms of Service
                Intent intent = new Intent(getActivity(), ToSActivity.class);
                startActivity(intent);
            }
        });

        //Button listener for btnPP
        btnPP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open Privacy Policy
                Intent intent = new Intent(getActivity(), PrivacyActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    private void openTOS(View view) {

    }

    private void openPP(View view) {

    }

}