package com.dgc.photoediting.setgetclass;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dgc.photoediting.R;

import java.util.ArrayList;
import java.util.List;

public class MyGridAdapter extends BaseAdapter {

    private Context gContext;
    private List<String> listImages = new ArrayList<>();
    private List<String> listTexts = new ArrayList<>();
    private LayoutInflater inflater;


    public MyGridAdapter(Context gContext, List<String> listImages) {
        this.gContext = gContext;
        this.listImages = listImages;
        this.listTexts = listTexts;
    }

    @Override
    public int getCount() {
        return listImages.size();
    }

    @Override
    public Object getItem(int position) {
        return listImages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (convertView == null) {
            inflater = (LayoutInflater) gContext.getSystemService(gContext.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_grid, null);
        }

        Log.e("item_image",listImages.get(position));
        ImageView item_image = view.findViewById(R.id.imageView1);
        //item_image.setImageResource(listImages.get(position));
        Glide.with(gContext)
                .load(listImages.get(position))
                .into(item_image);
        return view;
    }
}
