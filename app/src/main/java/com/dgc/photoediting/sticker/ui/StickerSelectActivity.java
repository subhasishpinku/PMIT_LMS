package com.dgc.photoediting.sticker.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dgc.photoediting.PopUp.PopupCallBackOneButton;
import com.dgc.photoediting.PopUp.PopupClass;
import com.dgc.photoediting.R;
import com.dgc.photoediting.setgetclass.APIClient;
import com.dgc.photoediting.setgetclass.FetchStickers;
import com.dgc.photoediting.utility.Utility;

import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;


public class StickerSelectActivity extends AppCompatActivity {

    public static final String EXTRA_STICKER_ID = "extra_sticker_id";
    private List<String> listSticker = new ArrayList<String>();
    RecyclerView recyclerView;
//    private final int[] stickerIds = {
//            R.drawable.abra,
//            R.drawable.abra,
//            R.drawable.bracelet,
//            R.drawable.bullbasaur,
//            R.drawable.camera,
//            R.drawable.candy,
//            R.drawable.caterpie,
//            R.drawable.charmander,
//            R.drawable.mankey,
//            R.drawable.map,
//            R.drawable.mega_ball,
//            R.drawable.meowth,
//            R.drawable.pawprints,
//            R.drawable.pidgey,
//            R.drawable.pikachu,
//            R.drawable.pikachu_1,
//            R.drawable.pikachu_2,
//            R.drawable.player,
//            R.drawable.pointer,
//            R.drawable.pokebag,
//            R.drawable.pokeball,
//            R.drawable.pokeballs,
//            R.drawable.pokecoin,
//            R.drawable.pokedex,
//            R.drawable.potion,
//            R.drawable.psyduck,
//            R.drawable.rattata,
//            R.drawable.revive,
//            R.drawable.squirtle,
//            R.drawable.star,
//            R.drawable.star_1,
//            R.drawable.superball,
//            R.drawable.tornado,
//            R.drawable.venonat,
//            R.drawable.weedle,
//            R.drawable.zubat
//    };
    Toolbar  toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_sticker_activity);

        //noinspection ConstantConditions
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.stickers_recycler_view);
        GridLayoutManager glm = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(glm);
        sticker();
        initToolBar();
//        List<Integer> stickers = new ArrayList<>(stickerIds.length);
//        for (Integer id : stickerIds) {
//            stickers.add(id);
//        }
//
//        recyclerView.setAdapter(new StickersAdapter(stickers, this));
    }
    public void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Sticker View");
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onStickerSelected(int stickerId) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_STICKER_ID, stickerId);
        setResult(RESULT_OK, intent);
        finish();
    }

    class StickersAdapter extends RecyclerView.Adapter<StickersAdapter.StickerViewHolder> {

        private final List<Integer> stickerIds;
        private final Context context;
        private final LayoutInflater layoutInflater;

        StickersAdapter(@NonNull List<Integer> stickerIds, @NonNull Context context) {
            this.stickerIds = stickerIds;
            this.context = context;
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public StickerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new StickerViewHolder(layoutInflater.inflate(R.layout.sticker_item, parent, false));
        }

        @Override
        public void onBindViewHolder(StickerViewHolder holder, int position) {
            holder.image.setImageDrawable(ContextCompat.getDrawable(context, getItem(position)));
        }

        @Override
        public int getItemCount() {
            return stickerIds.size();
        }

        private int getItem(int position) {
            return stickerIds.get(position);
        }

        class StickerViewHolder extends RecyclerView.ViewHolder {

            ImageView image;

            StickerViewHolder(View itemView) {
                super(itemView);
                image = (ImageView) itemView.findViewById(R.id.sticker_image);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = getAdapterPosition();
                        if (pos >= 0) { // might be NO_POSITION
                            onStickerSelected(getItem(pos));
                        }
                    }
                });
            }
        }
    }
    public void sticker(){
        if (Utility.checkConnectivity(getApplicationContext())) {
            Call<FetchStickers> call = APIClient.getInstance().doFetchStickersCall(
            );
            call.enqueue(new Callback<FetchStickers>() {
                @Override
                public void onResponse(Call<FetchStickers> call, retrofit2.Response<FetchStickers> response) {
                    if (response.isSuccessful()){
                        if (response.body().getStatusCode().equals("2000")){
                         //   Log.e("StickerFrame", response.body().getStickerIds().get(0));
                            for (int i = 0; i <response.body().getStickerIds().size(); i++) {
                                listSticker.add(response.body().getStickerIds().get(i));
                                Log.e("StickerFrame", response.body().getMessage());
                            }
                            StickerViewAdapter adapter = new StickerViewAdapter(getApplicationContext(),listSticker);
                            recyclerView.setAdapter(adapter);
                        }
                        else if (response.body().getStatusCode().equals("2001")){
                            Log.e("StickerFrame", response.body().getMessage());
                        }
                        else if (response.body().getStatusCode().equals("2002")){
                            Log.e("StickerFrame", response.body().getMessage());
                        }
                    }
                    else {
                        Log.e("DATA", " "+response.message());
                    }

                }

                @Override
                public void onFailure(Call<FetchStickers> call, Throwable t) {

                }
            });
        }
        else {
            PopupClass.showPopUpWithTitleMessageOneButton(StickerSelectActivity.this, "Ok", "", "Sorry, No Internet connection found, Please check your network connection", "", new PopupCallBackOneButton() {
                @Override
                public void onFirstButtonClick() {
                    finish();
                }
            });
        }

    }

    public class StickerViewAdapter extends RecyclerView.Adapter<StickerViewAdapter.ViewHolder> {
        private final List<String> listSticker;
        private Context mCtx;
        int listview;
        public StickerViewAdapter(Context mCtx, List<String> listSticker) {
            this.mCtx = mCtx;
            this.listSticker = listSticker;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sticker_item, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            listview = position;
            Log.e("position",listSticker.get(position));
            Glide.with(mCtx)
                    .load(listSticker.get(position))
                    .into(holder.images);
            holder.images.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(getApplicationContext(), StickerMainActivity.class);
//                    intent.putExtra("image", listSticker.get(position));
////                    intent.putExtra("title","Birthday");
//                    startActivity(intent);
////                    finish();

                    Intent shareIntent =   new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Sticker Link");
                    String app_url = listSticker.get(position);
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,app_url);
                    startActivity(Intent.createChooser(shareIntent, "Share via"));
                }
            });

        }
        @Override
        public int getItemCount() {
            return listSticker.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView images;
            public ViewHolder(View view) {
                super(view);
                images = (ImageView) view.findViewById(R.id.sticker_image);
            }
        }
    }
    Bitmap drawable_from_url(String url) throws java.net.MalformedURLException, java.io.IOException {

        HttpURLConnection connection = (HttpURLConnection)new URL(url) .openConnection();
        connection.setRequestProperty("User-agent","Mozilla/4.0");

        connection.connect();
        InputStream input = connection.getInputStream();

        return BitmapFactory.decodeStream(input);
    }
}
