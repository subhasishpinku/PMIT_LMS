package com.dgc.photoediting;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

public class EditActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerview;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    ArrayList images = new ArrayList<>(Arrays.asList(R.drawable.a, R.drawable.b, R.drawable.c,
            R.drawable.d, R.drawable.e, R.drawable.f, R.drawable.g, R.drawable.h,
            R.drawable.i, R.drawable.j));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity);
        recyclerview = (RecyclerView)findViewById(R.id.recyclerview);
        RecyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerview.setLayoutManager(RecyclerViewLayoutManager);
        Adapter adapter = new Adapter(getApplicationContext(), images);
        recyclerview.setAdapter(adapter);
        initToolBar();
    }
    public void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Edit");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );
    }
    public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        ArrayList images;
        Context context;
        public Adapter(Context context, ArrayList images) {
            this.context = context;
            this.images = images;
        }
        @NonNull
        @Override
        public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            Adapter.ViewHolder viewHolder = new Adapter.ViewHolder(view);
            return viewHolder;
        }
        @Override
        public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
            int res = (int) images.get(position);
            holder.images.setImageResource(res);
        }
        @Override
        public int getItemCount() {
            return images.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView images;

            public ViewHolder(View view) {
                super(view);
                images = (ImageView) view.findViewById(R.id.imageView);
            }
        }
    }
}
