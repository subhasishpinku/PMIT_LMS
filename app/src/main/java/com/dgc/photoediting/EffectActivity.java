package com.dgc.photoediting;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.uvstudio.him.photofilterlibrary.PhotoFilter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

public class EffectActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView imageView;
    Bitmap iBitmap,oBitmap;
    PhotoFilter photoFilter;
    int count=0;
    private static final int PERMISSION_REQUEST_CODE = 1000;
    private static final int STORAGE_PERMISSION_CODE = 123;
    Button btn_camera,btn_gallery;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PICK_IMAGE_REQUEST = 2;
    private File actualImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.effect_activity);
        imageView=(ImageView)findViewById(com.uvstudio.him.photofilterlibrary.R.id.imageView);
        btn_camera = (Button)findViewById(com.uvstudio.him.photofilterlibrary.R.id.btn_camera);
        btn_gallery = (Button)findViewById(com.uvstudio.him.photofilterlibrary.R.id.btn_gallery);
        iBitmap= BitmapFactory.decodeResource(getResources(), R.drawable.cute_selfi_spashscreen);
        photoFilter=new PhotoFilter();
        imageView.setOnClickListener(this);
        requestPermission();
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(EffectActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 110);
                } else {
                    takePhotoFromCamera();
                }
            }
        });
        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

    }
    private void saveImage(Bitmap finalBitmap, String image_name) {
        //String root = Environment.getExternalStorageDirectory().toString();
        File root = Environment.getExternalStorageDirectory();
        File myDir = new File(String.valueOf(root));
        myDir.mkdirs();
        String fname = "Image-" + image_name+ ".jpg";
        //   File file = new File(myDir, fname);
//        File file = new File(root.getAbsolutePath()+"/DCIM/Screenshots/img.jpg");
        File file = new File("/sdcard/cute_selfie_sample/"+fname);
        if (file.exists()) file.delete();
        Log.i("LOAD", root + fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Create a File for saving an image or video */
    private  File getOutputMediaFile(){
//        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
//                + "/Android/data/"
//                + getApplicationContext().getPackageName()
//                + "/Files");
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }
    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d("TAG", "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("TAG", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("TAG", " "+"Error accessing file: " + e.getMessage());
        }
    }
    private void requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(EffectActivity.this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
    private void showFileChooser() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select an Image"), PICK_IMAGE_REQUEST);

        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }
    private void takePhotoFromCamera() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        } else {

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
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2296) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                actualImage = FileUtil.from(getApplicationContext(),data.getData());
                iBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.fromFile(actualImage));
                imageView.setImageBitmap(iBitmap);
                String fileImage = String.valueOf(actualImage);
                String file_extn = fileImage.substring(fileImage.lastIndexOf("/") + 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

        }
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                iBitmap = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(iBitmap);
                Uri tempUri = getImageUri(getApplicationContext(), iBitmap);
                File finalFile = new File(getRealPathFromURII(tempUri));
                String url = String.valueOf(finalFile);
                actualImage = finalFile;
                Log.e("path", String.valueOf(actualImage));
                String fileImage = String.valueOf(actualImage);
                String file_extn = fileImage.substring(fileImage.lastIndexOf("/") + 1);
            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    public String getRealPathFromURII(Uri uri) {
        String path = "";
        if (getApplicationContext().getContentResolver() != null) {
            Cursor cursor = getApplicationContext().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onClick(View view) {
        count++;
        switch (count) {
            case 1:
                imageView.setImageBitmap(photoFilter.one(this, iBitmap));
                Toast.makeText(this, "Filter 1"+iBitmap, Toast.LENGTH_SHORT).show();
                storeImage(photoFilter.one(this, iBitmap));
                saveImage(photoFilter.one(this, iBitmap),"001");
                break;
            case 2:
                imageView.setImageBitmap(photoFilter.two(this, iBitmap));
                Toast.makeText(this, "Filter 2", Toast.LENGTH_SHORT).show();
                storeImage(photoFilter.two(this, iBitmap));
                saveImage(photoFilter.two(this, iBitmap),"002");
                break;
            case 3:
                imageView.setImageBitmap(photoFilter.three(this, iBitmap));
                Toast.makeText(this, "Filter 3", Toast.LENGTH_SHORT).show();
                storeImage(photoFilter.three(this, iBitmap));
                saveImage(photoFilter.three(this, iBitmap),"003");
                break;
            case 4:
                imageView.setImageBitmap(photoFilter.four(this, iBitmap));
                Toast.makeText(this, "Filter 4", Toast.LENGTH_SHORT).show();
                storeImage(photoFilter.four(this, iBitmap));
                saveImage(photoFilter.four(this, iBitmap),"004");
                break;
            case 5:
                imageView.setImageBitmap(photoFilter.five(this, iBitmap));
                Toast.makeText(this, "Filter 5", Toast.LENGTH_SHORT).show();
                storeImage(photoFilter.five(this, iBitmap));
                saveImage(photoFilter.five(this, iBitmap),"005");
                break;
            case 6:
                imageView.setImageBitmap(photoFilter.six(this, iBitmap));
                Toast.makeText(this, "Filter 6", Toast.LENGTH_SHORT).show();
                storeImage(photoFilter.six(this, iBitmap));
                saveImage(photoFilter.six(this, iBitmap),"006");
                break;
            case 7:
                imageView.setImageBitmap(photoFilter.seven(this, iBitmap));
                Toast.makeText(this, "Filter 7", Toast.LENGTH_SHORT).show();
                storeImage(photoFilter.seven(this, iBitmap));
                saveImage(photoFilter.seven(this, iBitmap),"007");
                break;
            case 8:
                imageView.setImageBitmap(photoFilter.eight(this, iBitmap));
                Toast.makeText(this, "Filter 8", Toast.LENGTH_SHORT).show();
                storeImage(photoFilter.eight(this, iBitmap));
                saveImage(photoFilter.eight(this, iBitmap),"008");
                break;
            case 9:
                imageView.setImageBitmap(photoFilter.nine(this, iBitmap));
                Toast.makeText(this, "Filter 9", Toast.LENGTH_SHORT).show();
                storeImage(photoFilter.nine(this, iBitmap));
                saveImage(photoFilter.nine(this, iBitmap),"009");
                break;
            case 10:
                imageView.setImageBitmap(photoFilter.ten(this, iBitmap));
                Toast.makeText(this, "Filter 10", Toast.LENGTH_SHORT).show();
                storeImage(photoFilter.ten(this, iBitmap));
                saveImage(photoFilter.ten(this, iBitmap),"010");
                break;
            case 11:
                imageView.setImageBitmap(photoFilter.eleven(this, iBitmap));
                Toast.makeText(this, "Filter 11", Toast.LENGTH_SHORT).show();
                storeImage(photoFilter.eleven(this, iBitmap));
                saveImage(photoFilter.eleven(this, iBitmap),"011");
                break;
            case 12:
                imageView.setImageBitmap(photoFilter.twelve(this, iBitmap));
                Toast.makeText(this, "Filter 12", Toast.LENGTH_SHORT).show();
                storeImage(photoFilter.twelve(this, iBitmap));
                saveImage(photoFilter.twelve(this, iBitmap),"012");
                break;
            case 13:
                imageView.setImageBitmap(photoFilter.thirteen(this, iBitmap));
                Toast.makeText(this, "Filter 13", Toast.LENGTH_SHORT).show();
                storeImage(photoFilter.thirteen(this, iBitmap));
                saveImage(photoFilter.thirteen(this, iBitmap),"013");
                break;
            case 14:
                imageView.setImageBitmap(photoFilter.fourteen(this, iBitmap));
                Toast.makeText(this, "Filter 14", Toast.LENGTH_SHORT).show();
                storeImage(photoFilter.fourteen(this, iBitmap));
                saveImage(photoFilter.fourteen(this, iBitmap),"014");
                break;
            case 15:
                imageView.setImageBitmap(photoFilter.fifteen(this, iBitmap));
                Toast.makeText(this, "Filter 15", Toast.LENGTH_SHORT).show();
                storeImage(photoFilter.fifteen(this, iBitmap));
                saveImage(photoFilter.fifteen(this, iBitmap),"015");
                break;
            case 16:
                imageView.setImageBitmap(photoFilter.sixteen(this, iBitmap));
                Toast.makeText(this, "Filter 16", Toast.LENGTH_SHORT).show();
                storeImage(photoFilter.sixteen(this, iBitmap));
                saveImage(photoFilter.sixteen(this, iBitmap),"016");
                break;
        }
        if(count==16)
            count=0;
    }

}

