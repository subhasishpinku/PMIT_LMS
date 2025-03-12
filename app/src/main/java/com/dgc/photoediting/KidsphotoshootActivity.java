package com.dgc.photoediting;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dgc.photoediting.PopUp.PopupCallBackOneButton;
import com.dgc.photoediting.PopUp.PopupClass;
import com.dgc.photoediting.setgetclass.APIClient;
import com.dgc.photoediting.setgetclass.FetchFrames;
import com.dgc.photoediting.setgetclass.MyGridAdapter;
import com.dgc.photoediting.setgetclass.PayloadFetchFrames;
import com.dgc.photoediting.utility.Utility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class KidsphotoshootActivity extends AppCompatActivity {
    Toolbar toolbar;
    private List<String> listImagesKidsphotoshoot = new ArrayList<>();
    GridView grid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kidsphotoshoot_activity);
        initToolBar();
        grid = findViewById(R.id.gridView);
        getfetchFrames();
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    default:
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("image", listImagesKidsphotoshoot.get(position));
                        intent.putExtra("title","Kids photoshoot");
                        startActivity(intent);
                        break;
                }
            }
        });
    }
    public void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Kids photoshoot");
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
    public void getfetchFrames(){
        if (Utility.checkConnectivity(getApplicationContext())) {
            Call<FetchFrames> call = APIClient.getInstance().doFetchFrameCall(
            );
            call.enqueue(new Callback<FetchFrames>() {
                @Override
                public void onResponse(Call<FetchFrames> call, retrofit2.Response<FetchFrames> response) {
                    if (response.isSuccessful()){
                        if (response.body().getStatusCode().equals("2000")){
                          //  Log.e("FetchFrame", response.body().getPayloadFetchFrames().get(0).getFramesIds()+"");
                            for (int i = 0; i <response.body().getPayloadFetchFrames().size(); i++) {
                                PayloadFetchFrames getPayloadFetchFrames=  response.body().getPayloadFetchFrames().get(i);
                                String label = getPayloadFetchFrames.getLabel();
                                Log.e("label",label);
                                if (label.equals("Kids photoshoot")) {
                                    for (int j = 0; j < getPayloadFetchFrames.getFramesIds().size(); j++) {
                                        listImagesKidsphotoshoot.add(getPayloadFetchFrames.getFramesIds().get(j));
                                    }
                                }
                            }
                            MyGridAdapter adapter = new MyGridAdapter(getApplicationContext(), listImagesKidsphotoshoot);
                            grid.setAdapter(adapter);
                        }
                        else if (response.body().getStatusCode().equals("2001")){
                            Log.e("FetchFrame", response.body().getMessage());
                        }
                        else if (response.body().getStatusCode().equals("2002")){
                            Log.e("FetchFrame", response.body().getMessage());
                        }
                    }
                    else {
                        Log.e("DATA", " "+response.message());
                    }

                }

                @Override
                public void onFailure(Call<FetchFrames> call, Throwable t) {

                }
            });
        }
        else {
            PopupClass.showPopUpWithTitleMessageOneButton(KidsphotoshootActivity.this, "Ok", "", "Sorry, No Internet connection found, Please check your network connection", "", new PopupCallBackOneButton() {
                @Override
                public void onFirstButtonClick() {
                    finish();
                }
            });
        }

    }
}
