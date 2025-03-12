package com.dgc.photoediting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.dgc.photoediting.PopUp.PopupCallBackOneButton;
import com.dgc.photoediting.PopUp.PopupClass;
import com.dgc.photoediting.argear.ui.CameraActivity;
import com.dgc.photoediting.crop.CropActivity;
import com.dgc.photoediting.emotionrecognition.MainActivityemotion;
import com.dgc.photoediting.faceplus.LoadingActivity;
import com.dgc.photoediting.hairsegment.MainActivityhair;
import com.dgc.photoediting.image3d.augmentedimage.AugmentedImageActivity;
import com.dgc.photoediting.masksegment.MainActivitySegment;
import com.dgc.photoediting.mycollection.ImagesActivity;
import com.dgc.photoediting.setgetclass.APIClient;
import com.dgc.photoediting.setgetclass.FetchFrames;
import com.dgc.photoediting.setgetclass.MyGridAdapter;
import com.dgc.photoediting.setgetclass.PayloadFetchFrames;
import com.dgc.photoediting.sticker.ui.StickerMainActivity;
import com.dgc.photoediting.stylesegment.MainActivityStyle;
import com.dgc.photoediting.utility.Utility;
import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ly.img.android.pesdk.PhotoEditorSettingsList;
import ly.img.android.pesdk.assets.filter.basic.FilterPackBasic;
import ly.img.android.pesdk.assets.font.basic.FontPackBasic;
import ly.img.android.pesdk.assets.frame.basic.FramePackBasic;
import ly.img.android.pesdk.assets.overlay.basic.OverlayPackBasic;
import ly.img.android.pesdk.assets.sticker.emoticons.StickerPackEmoticons;
import ly.img.android.pesdk.assets.sticker.shapes.StickerPackShapes;
import ly.img.android.pesdk.backend.model.EditorSDKResult;
import ly.img.android.pesdk.backend.model.state.LoadSettings;
import ly.img.android.pesdk.backend.model.state.PhotoEditorSaveSettings;
import ly.img.android.pesdk.backend.model.state.manager.SettingsList;
import ly.img.android.pesdk.ui.activity.CameraPreviewBuilder;
import ly.img.android.pesdk.ui.activity.EditorBuilder;
import ly.img.android.pesdk.ui.model.state.UiConfigFilter;
import ly.img.android.pesdk.ui.model.state.UiConfigFrame;
import ly.img.android.pesdk.ui.model.state.UiConfigOverlay;
import ly.img.android.pesdk.ui.model.state.UiConfigSticker;
import ly.img.android.pesdk.ui.model.state.UiConfigText;
import ly.img.android.pesdk.ui.utils.PermissionRequest;
import ly.img.android.serializer._3.IMGLYFileWriter;
import retrofit2.Call;
import retrofit2.Callback;

public class TopActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    TabLayout tabLayout;
    TabHost tabHost;
    ViewPager viewPager;
    //    private int[] tabicons ={
//            R.mipmap.pending,
//            R.mipmap.accept
//    };
    public static final String OUTPUT_PHOTO_DIRECTORY = "cute_selfie_sample";
    Button openGallery, openCamera;
    public static int CAMERA_RESULT = 1;
    public static int GALLERY_RESULT = 2;
    public static int DS_CAMERA_RESULT = 3;
    public static int DS_GALLERY_RESULT = 4;
    private BackButtonHandlerInterface backButtonHandler;
    boolean doubleBackToExitPressedOnce = false;
    RecyclerView recyclerView;
    GridView grid;
    RecyclerView recyclerview1;
    RecyclerView recyclerview2;
    ArrayList<String> Number;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    RecyclerViewAdapter RecyclerViewHorizontalAdapter;
    LinearLayoutManager HorizontalLayout;
    View ChildView;
    int RecyclerViewItemPosition;
    ArrayList images = new ArrayList<>(Arrays.asList(R.drawable.a, R.drawable.b, R.drawable.c,
            R.drawable.d, R.drawable.e, R.drawable.f, R.drawable.g, R.drawable.h,
            R.drawable.i, R.drawable.j));
    InterstitialAd mInterstitialAdSAve;
    RecyclerViewAdapter2 RecyclerViewHorizontalAdapter2;
    ArrayList<String> Number2;
    FloatingActionButton fab;
    Integer[] Frame_id, Frame_id1;
    Toolbar toolbar1;
    private List<String> listImagesBirthday = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top_layout_activity);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
//        Frame_id = new Integer[]{
//                R.drawable.fframe1,
//                R.drawable.fframe2,
//                R.drawable.fframe3,
//                R.drawable.fframe4,
//                R.drawable.fframe5,
//                R.drawable.fframe6,
//                R.drawable.fframe7,
//                R.drawable.fframe8,
//                R.drawable.fframe9,
//                R.drawable.fframe10,
//                R.drawable.fframe11,
//                R.drawable.fframe12,
//                R.drawable.fframe13,
//                R.drawable.fframe14,
//                R.drawable.fframe15,
//                R.drawable.fframe16,
//                R.drawable.fframe17,
//                R.drawable.fframe18,
//                R.drawable.frame19,
//                R.drawable.frame20,
//                R.drawable.frame21,
//        };
//
//        Frame_id1 = new Integer[]{
//                R.drawable.frame1,
//                R.drawable.frame2,
//                R.drawable.frame3,
//                R.drawable.frame4,
//                R.drawable.frame5,
//                R.drawable.frame6,
//                R.drawable.frame7,
//                R.drawable.frame8,
//                R.drawable.frame9,
//                R.drawable.frame10,
//                R.drawable.frame11,
//                R.drawable.frame12,
//                R.drawable.frame13,
//                R.drawable.frame14,
//                R.drawable.frame15,
//                R.drawable.frame16,
//                R.drawable.frame17,
//                R.drawable.frame18,
//                R.drawable.frame19,
//                R.drawable.frame20,
//                R.drawable.frame21,
//
//        };

        grid = findViewById(R.id.gridView);
      //  Adapter_grid adapter = new Adapter_grid(getApplicationContext(), Frame_id);
      //  grid.setAdapter(adapter);
        getfetchFrames();
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    default:
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("image", listImagesBirthday.get(position));
                        intent.putExtra("title","Birthday");
                        startActivity(intent);
                        break;
                }
            }
        });
//        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//                MainActivity.imgid = Frame_id1[arg2];
//
//
//                if (arg2 == 18) {
//
//                    if (isInternetAvailable()) {
//                        //setupInterstialAdForSave();
//                    }
//
//                }
//
//                if (arg2 == 15) {
//
//                    if (isInternetAvailable()) {
//
//                       // setupInterstialAdForSave();
//                    }
//
//                }
//
//                if (arg2 == 10) {
//
//                    if (isInternetAvailable()) {
//
//                       // setupInterstialAdForSave();
//                    }
//
//                }
//
//
//                if (arg2 == 10) {
//
//                    if (isInternetAvailable()) {
//
//                       // setupInterstialAdForSave();
//                    }
//
//                }
//
//                if (arg2 == 6) {
//
//                    if (isInternetAvailable()) {
//
//                        //setupInterstialAdForSave();
//                    }
//                }
//
//                if (arg2 == 3) {
//
//                    if (isInternetAvailable()) {
//
//                       // setupInterstialAdForSave();
//                    }
//                }
//
//                if (arg2 == 2) {
//
//                    if (isInternetAvailable()) {
//
//                        //setupInterstialAdForSave();
//                    }
//                }
//                Intent i = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(i);
//            }
//        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview1);
        RecyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(RecyclerViewLayoutManager);
        AddItemsToRecyclerViewArrayList();
        RecyclerViewHorizontalAdapter = new RecyclerViewAdapter(Number);
        HorizontalLayout = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(HorizontalLayout);
        recyclerView.setAdapter(RecyclerViewHorizontalAdapter);
        list_iten();

        recyclerview2 = (RecyclerView) findViewById(R.id.recyclerview2);
        RecyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerview2.setLayoutManager(RecyclerViewLayoutManager);
        AddItemsToRecyclerViewArrayList2();
        RecyclerViewHorizontalAdapter2 = new RecyclerViewAdapter2(Number2);
        HorizontalLayout = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerview2.setLayoutManager(HorizontalLayout);
        recyclerview2.setAdapter(RecyclerViewHorizontalAdapter2);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog(CAMERA_RESULT, GALLERY_RESULT);

            }
        });
        initToolBar();
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.navigation_categories:

                break;
            case R.id.recommand:
                Intent i = new Intent(getApplicationContext(), ImagesActivity.class);
                startActivity(i);
                break;
            case R.id.feed:
               // fragment = new Features();
                Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
                startActivity(intent);
                break;
//            case R.id.user:
//                fragment = new Features();
//                break;
            default:
        }
        return loadFragment(fragment);
    }
    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            this.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
    public void initToolBar() {
        toolbar1 = (Toolbar) findViewById(R.id.toolbar);
        toolbar1.setTitle("catagories");
        setSupportActionBar(toolbar1);
        toolbar1.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar1.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );
    }
    public AlertDialog createDialog(int camCode, int glryCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater)getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.edit_diolog, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        ((ImageView) dialogView.findViewById(R.id.openGallery)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openSystemGalleryToSelectAnImage(glryCode);
            }
        });
        ((ImageView) dialogView.findViewById(R.id.openCamera)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                opencam(camCode);
            }
        });
        ((Button) dialogView.findViewById(R.id.cancelId)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        builder.setView(dialogView);
        dialog.show();
        return dialog;
    }

    private void openSystemGalleryToSelectAnImage(int glryCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            startActivityForResult(intent, glryCode);
        } else {
            Toast.makeText(getApplicationContext(), "No Gallery APP installed", Toast.LENGTH_LONG).show();
        }
    }

    private void openEditor(Uri inputImage) {
        SettingsList settingsList = createPesdkSettingsList();

        // Set input image
        settingsList.getSettingsModel(LoadSettings.class).setSource(inputImage);

        settingsList.getSettingsModel(PhotoEditorSaveSettings.class).setOutputToGallery(Environment.DIRECTORY_DCIM);

        new EditorBuilder(this)
                .setSettingsList(settingsList)
                .startActivityForResult(this, GALLERY_RESULT);
    }

    private void opencam(int camCode) {
        PhotoEditorSettingsList settingsList = createPesdkSettingsList();
        new CameraPreviewBuilder(this)
                .setSettingsList(settingsList)
                .startActivityForResult(this, camCode, PermissionRequest.NEEDED_PREVIEW_PERMISSIONS_AND_FINE_LOCATION);

    }

    private PhotoEditorSettingsList createPesdkSettingsList() {

        // Create a empty new SettingsList and apply the changes on this referance.
        PhotoEditorSettingsList settingsList = new PhotoEditorSettingsList();

        // If you include our asset Packs and you use our UI you also need to add them to the UI,
        // otherwise they are only available for the backend
        // See the specific feature sections of our guides if you want to know how to add our own Assets.

        settingsList.getSettingsModel(UiConfigFilter.class).setFilterList(
                FilterPackBasic.getFilterPack()
        );

        settingsList.getSettingsModel(UiConfigText.class).setFontList(
                FontPackBasic.getFontPack()
        );

        settingsList.getSettingsModel(UiConfigFrame.class).setFrameList(
                FramePackBasic.getFramePack()
        );

        settingsList.getSettingsModel(UiConfigOverlay.class).setOverlayList(
                OverlayPackBasic.getOverlayPack()
        );

        settingsList.getSettingsModel(UiConfigSticker.class).setStickerLists(
                StickerPackEmoticons.getStickerCategory(),
                StickerPackShapes.getStickerCategory()
        );

        return settingsList;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK && requestCode == CAMERA_RESULT) {
            EditorSDKResult data = new EditorSDKResult(intent);
            data.notifyGallery(EditorSDKResult.UPDATE_RESULT & EditorSDKResult.UPDATE_SOURCE);
            SettingsList lastState = data.getSettingsList();
            try {
                new IMGLYFileWriter(lastState).writeJson(new File(Environment.getExternalStorageDirectory(), "serialisationReadyToReadWithPESDKFileReader.json"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (resultCode == RESULT_OK && requestCode == GALLERY_RESULT) {
            Uri selectedImage = intent.getData();
            openEditor(selectedImage);
        } else if (resultCode == RESULT_OK && requestCode == DS_GALLERY_RESULT) {
            Uri selectedImage = intent.getData();
            Intent dsPhotoEditorIntent = new Intent(getApplicationContext(), DsPhotoEditorActivity.class);
            dsPhotoEditorIntent.setData(selectedImage);
            dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, OUTPUT_PHOTO_DIRECTORY);
            startActivityForResult(dsPhotoEditorIntent, 105);
        } else if (resultCode == RESULT_OK && requestCode == DS_CAMERA_RESULT) {
            Uri selectedImage = intent.getData();
            Intent dsPhotoEditorIntent = new Intent(getApplicationContext(), DsPhotoEditorActivity.class);
            dsPhotoEditorIntent.setData(selectedImage);
            dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, OUTPUT_PHOTO_DIRECTORY);
            startActivityForResult(dsPhotoEditorIntent, 105);
        } else if (requestCode == 105) {
            Toast.makeText(getApplicationContext(), "Photo saved in " + OUTPUT_PHOTO_DIRECTORY + " folder.", Toast.LENGTH_LONG).show();
        }
    }

    public void list_iten() {
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {
                    return true;
                }
            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {
                ChildView = Recyclerview.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                if (ChildView != null && gestureDetector.onTouchEvent(motionEvent)) {
                    //Getting clicked value.
                    RecyclerViewItemPosition = Recyclerview.getChildAdapterPosition(ChildView);
                    // Showing clicked item value on screen using toast message.
                    // Toast.makeText(getContext(), Number.get(RecyclerViewItemPosition), Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });
    }

    public void AddItemsToRecyclerViewArrayList() {
        Number = new ArrayList<>();
        Number.add("Birthday");
        Number.add("Anniversary");
        Number.add("Kids photoshoot");
        Number.add("Engagement");
    }

    public void AddItemsToRecyclerViewArrayList2() {
        Number2 = new ArrayList<>();
//        Number2.add("Crop");
//        Number2.add("Sticker");
        Number2.add("Face++");
        Number2.add("Style Segment");
        Number2.add("Style Emotion");
        Number2.add("Style Hair");
        Number2.add("argear");
        Number2.add("Filter");
        Number2.add("3d image");
        Number2.add("Mask Segment");
        Number2.add("DS Photo Editor");
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyView> {
        private List<String> list;

        public class MyView extends RecyclerView.ViewHolder {
            public TextView textView;

            public MyView(View view) {
                super(view);
                textView = (TextView) view.findViewById(R.id.textview1);
            }
        }

        public RecyclerViewAdapter(List<String> horizontalList) {
            this.list = horizontalList;
        }

        @Override
        public MyView onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_item, parent, false);
            CardView cardView = itemView.findViewById(R.id.cardview);
/*
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), FUBeautyActivity.class);
                    startActivity(intent);
                }
            });
*/
            return new MyView(itemView);
        }

        @Override
        public void onBindViewHolder(final MyView holder, final int position) {
            holder.textView.setText(list.get(position));
            if (holder.textView.getText().equals("Birthday")) {
                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), BirthdayActivity.class);
                        startActivity(intent);

                    }
                });

            }
            if (holder.textView.getText().equals("Anniversary")) {
                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), AnniversaryActivity.class);
                        startActivity(intent);

                    }
                });

            }

            if (holder.textView.getText().equals("Kids photoshoot")) {
                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), KidsphotoshootActivity.class);
                        startActivity(intent);

                    }
                });

            }

            if (holder.textView.getText().equals("Engagement")) {
                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), EngagementActivity.class);
                        startActivity(intent);

                    }
                });

            }

        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    public class RecyclerViewAdapter2 extends RecyclerView.Adapter<RecyclerViewAdapter2.MyView> {
        private List<String> list;

        public class MyView extends RecyclerView.ViewHolder {
            public TextView textView;

            public MyView(View view) {
                super(view);
                textView = (TextView) view.findViewById(R.id.textview1);
            }
        }

        public RecyclerViewAdapter2(List<String> horizontalList) {
            this.list = horizontalList;
        }

        @Override
        public MyView onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_item1, parent, false);
            CardView cardView = itemView.findViewById(R.id.cardview);
/*
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), FUBeautyActivity.class);
                    startActivity(intent);
                }
            });
*/

            return new MyView(itemView);
        }

        @Override
        public void onBindViewHolder(final MyView holder, final int position) {
            holder.textView.setText(list.get(position));

            if (holder.textView.getText().equals("Crop")) {
                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), CropActivity.class);
                        startActivity(intent);

                    }
                });

            }

            if (holder.textView.getText().equals("3d image")) {
                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ///intent/
                        Intent intent = new Intent(getApplicationContext(), AugmentedImageActivity.class);
                        startActivity(intent);
                    }
                });
                //  Intent intent = new Intent(getContext(), FUBeautyActivity.class);
                // startActivity(intent);

            }


            if (holder.textView.getText().equals("Sticker")) {
                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), StickerMainActivity.class);
                        startActivity(intent);
//                        Intent intent = new Intent(getContext(), EntryActivity.class);
//                        startActivity(intent);
                    }
                });
                //  Intent intent = new Intent(getContext(), FUBeautyActivity.class);
                // startActivity(intent);

            }

            if (holder.textView.getText().equals("Face++")) {
                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Intent intent = new Intent(getContext(), StickerMainActivity.class);
//                        startActivity(intent);
                        Intent intent = new Intent(getApplicationContext(), LoadingActivity.class);
                        startActivity(intent);
                    }
                });
                //  Intent intent = new Intent(getContext(), FUBeautyActivity.class);
                // startActivity(intent);
            }


            if (holder.textView.getText().equals("DS Photo Editor")) {
                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        createDialog(DS_CAMERA_RESULT, DS_GALLERY_RESULT);

                    }
                });
            }
            if (holder.textView.getText().equals("argear")) {
                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                        startActivity(intent);
                    }
                });
            }
            if (holder.textView.getText().equals("Mask Segment")) {
                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), MainActivitySegment.class);
                        startActivity(intent);
                    }
                });
            }

            if (holder.textView.getText().equals("Style Segment")) {
                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), MainActivityStyle.class);
                        startActivity(intent);
                    }
                });
            }

            if (holder.textView.getText().equals("Style Emotion")) {
                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), MainActivityemotion.class);
                        startActivity(intent);
                    }
                });
            }
            if (holder.textView.getText().equals("Style Hair")) {
                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), MainActivityhair.class);
                        startActivity(intent);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    public boolean isInternetAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private void setupInterstialAdForSave() {
        mInterstitialAdSAve = new InterstitialAd(getApplicationContext());
        mInterstitialAdSAve.setAdUnitId(getResources().getString(R.string.full_screen_ad_unit_id_save));

        AdRequest adRequestFull = new AdRequest.Builder().build();

        mInterstitialAdSAve.loadAd(adRequestFull);
        mInterstitialAdSAve.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                // Full screen advertise will show only after loading complete
                mInterstitialAdSAve.show();
            }
        });
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
                       //     Log.e("FetchFrame", response.body().getPayloadFetchFrames().get(0).getFramesIds()+"");
                            for (int i = 0; i <response.body().getPayloadFetchFrames().size(); i++) {
                                PayloadFetchFrames getPayloadFetchFrames=  response.body().getPayloadFetchFrames().get(i);
                                String label = getPayloadFetchFrames.getLabel();
                                Log.e("label",label);
                                if (label.equals("Birthday")) {
                                    for (int j = 0; j < getPayloadFetchFrames.getFramesIds().size(); j++) {
                                        listImagesBirthday.add(getPayloadFetchFrames.getFramesIds().get(j));
                                    }
                                }
                            }
                            MyGridAdapter adapter = new MyGridAdapter(getApplicationContext(), listImagesBirthday);
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
        PopupClass.showPopUpWithTitleMessageOneButton(TopActivity.this, "Ok", "", "Sorry, No Internet connection found, Please check your network connection", "", new PopupCallBackOneButton() {
            @Override
            public void onFirstButtonClick() {
                finish();
            }
        });
    }

    }

}
