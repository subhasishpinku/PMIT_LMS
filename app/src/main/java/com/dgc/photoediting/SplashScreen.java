package com.dgc.photoediting;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.dgc.photoediting.PopUp.PopupCallBackOneButton;
import com.dgc.photoediting.PopUp.PopupClass;
import com.dgc.photoediting.utility.Utility;

import java.io.File;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;
public class SplashScreen extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1000;
    private static final int STORAGE_PERMISSION_CODE = 123;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen_activity);
        requestPermission();
        if (Utility.checkConnectivity(this)) {
            timer();
        }
        else {
            PopupClass.showPopUpWithTitleMessageOneButton(SplashScreen.this, "Ok", "", "Sorry, No Internet connection found, Please check your network connection", "", new PopupCallBackOneButton() {
                @Override
                public void onFirstButtonClick() {
                    finish();
                }
            });
        }
        File Directory = new File("/sdcard/cute_selfie_sample/");
        if (!Directory.exists()) {
            Directory.mkdirs();
        }
    }
    private void requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2296);
                Log.e("Permission","0");
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
                Log.e("Permission","1");
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(SplashScreen.this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSION_REQUEST_CODE);
            return;
        }

    }

    private void timer(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Intent i=new Intent(getApplicationContext(), Cutselfi_ScreenA.class);
                startActivity(i);
                finish();
            }
        }, 2000);
    }
}