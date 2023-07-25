package com.crm.cardlinkmerchant.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.crm.cardlinkmerchant.R;

public class SpinnerAdapter extends ArrayAdapter<String> {
    Context context;
    int flags[];
    String[] countryNames;
    LayoutInflater inflter;

    public SpinnerAdapter(Context applicationContext, int[] flags, String[] countryNames) {
        super(applicationContext, R.layout.custom_spinner_image_selected, countryNames);
        this.context = applicationContext;
        this.flags = flags;
        this.countryNames = countryNames;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomDropDownView(position, convertView, parent);
    }

    public View getCustomDropDownView(int i, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_spinner_image, parent, false);

        ImageView icon = (ImageView) view.findViewById(R.id.imgSpinnerItem);
        TextView names = (TextView) view.findViewById(R.id.txtSpinnerItemLabel);
        icon.setImageResource(flags[i]);
        names.setText(countryNames[i]);
        return view;
    }
    public View getCustomView(int i, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_spinner_image_selected, parent, false);

        ImageView icon = (ImageView) view.findViewById(R.id.imgSpinnerItemSelected);
        icon.setImageResource(flags[i]);
        return view;
    }
}
