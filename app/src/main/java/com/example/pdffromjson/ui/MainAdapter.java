package com.example.pdffromjson.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.pdffromjson.R;

public class MainAdapter extends BaseAdapter {
    private Context context;
    private int reportLists;

    public MainAdapter(Context context, int reportLists) {
        this.context = context;
        this.reportLists = reportLists;
    }

    @Override
    public int getCount() {
        return reportLists;
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
        return view;
    }
}
