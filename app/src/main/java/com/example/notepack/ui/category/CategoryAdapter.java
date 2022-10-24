package com.example.notepack.ui.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.notepack.R;

import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<Category> {

    public CategoryAdapter(Context context, ArrayList<Category> categoryList){
        super(context,0,categoryList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    public View initView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.category, parent, false);
        }

        TextView textName = convertView.findViewById(R.id.name);
        ImageView image = convertView.findViewById(R.id.image);

        Category category = getItem(position);

        if(category != null) {
            textName.setText(category.getName());
            image.setImageResource(category.getImage());
        }

        return convertView;
    }

}
