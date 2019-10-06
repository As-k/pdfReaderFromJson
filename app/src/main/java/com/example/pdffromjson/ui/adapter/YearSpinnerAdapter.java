package com.example.pdffromjson.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.pdffromjson.R;

import java.util.ArrayList;

public class YearSpinnerAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> yearList, invalidYearList;

    public YearSpinnerAdapter(Context context, ArrayList<String> yearList, ArrayList<String> invalidYearList) {
        this.context = context;
        this.yearList = yearList;
        this.invalidYearList = invalidYearList;
    }

    @Override
    public int getCount() {
        return yearList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder")
        View view = LayoutInflater.from(context).inflate(R.layout.item_pdf_json, parent, false);
        TextView names = view.findViewById(R.id.year_text);

        for (int i = 0; i < invalidYearList.size(); i++) {
            if (yearList.get(position).equalsIgnoreCase(invalidYearList.get(i))) {
                names.setText(yearList.get(position));
                names.setTextColor(Color.RED);
            } else
                names.setText(yearList.get(position));
        }

        return view;
    }
}
