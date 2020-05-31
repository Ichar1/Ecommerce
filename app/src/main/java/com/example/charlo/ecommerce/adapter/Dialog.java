package com.example.charlo.ecommerce.adapter;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.charlo.ecommerce.R;

public class Dialog extends DialogFragment {
    View rootview;
    EditText name, message, phone, email;

    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();


        builder.setView(inflater.inflate(R.layout.message,null));
        builder.setPositiveButton("Login ", (dialog, which) -> {
           // name = find


        });

        builder.setNegativeButton("cancel", (dialog, which) -> {

        });

        return builder.create();
    }
}
