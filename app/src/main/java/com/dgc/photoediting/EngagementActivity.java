package com.dgc.photoediting;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.Nullable;
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

public class EngagementActivity extends AppCompatActivity {
    Toolbar toolbar;
    private List<String> listImagesEngagement = new ArrayList<>();
    GridView grid;
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.engagement_activity);
        initToolBar();
        grid = findViewById(R.id.gridView);
        getfetchFrames();
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    default:
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("image", listImagesEngagement.get(position));
                        intent.putExtra("title","Engagement");
                        startActivity(intent);
                        break;
                }
            }
        });
    }
    public void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Engagement");
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
                           // Log.e("FetchFrame", response.body().getPayloadFetchFrames().get(0).getFramesIds()+"");
                            for (int i = 0; i <response.body().getPayloadFetchFrames().size(); i++) {
                                PayloadFetchFrames getPayloadFetchFrames=  response.body().getPayloadFetchFrames().get(i);
                                String label = getPayloadFetchFrames.getLabel();
                                Log.e("label",label);
                                if (label.equals("Engagement")) {
                                    for (int j = 0; j < getPayloadFetchFrames.getFramesIds().size(); j++) {
                                        listImagesEngagement.add(getPayloadFetchFrames.getFramesIds().get(j));
                                    }
                                }
                            }
                            MyGridAdapter adapter = new MyGridAdapter(getApplicationContext(), listImagesEngagement);
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
            PopupClass.showPopUpWithTitleMessageOneButton(EngagementActivity.this, "Ok", "", "Sorry, No Internet connection found, Please check your network connection", "", new PopupCallBackOneButton() {
                @Override
                public void onFirstButtonClick() {
                    finish();
                }
            });
        }

    }
}
