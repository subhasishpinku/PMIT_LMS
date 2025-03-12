package com.dgc.photoediting.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.dgc.photoediting.BackButtonHandlerInterface;
import com.dgc.photoediting.OnBackClickListener;
import com.dgc.photoediting.R;
import com.dgc.photoediting.crop.CropActivity;
import com.dgc.photoediting.faceplus.LoadingActivity;
import com.dgc.photoediting.image3d.augmentedimage.AugmentedImageActivity;
import com.dgc.photoediting.sticker.ui.StickerMainActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class Trending extends Fragment implements OnBackClickListener , PermissionRequest.Response{
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionRequest.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void permissionGranted() {}

    @Override
    public void permissionDenied() {
        /* TODO: The Permission was rejected by the user. The Editor was not opened,
         * Show a hint to the user and try again. */
    }
    Button openGallery,openCamera;
    public static int PESDK_RESULT = 1;
    public static int GALLERY_RESULT = 2;
    private BackButtonHandlerInterface backButtonHandler;
    boolean doubleBackToExitPressedOnce = false;
    RecyclerView recyclerView;
    ///////////////////////////////
    RecyclerView recyclerview1;
    ArrayList<String> Number;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    RecyclerViewAdapter RecyclerViewHorizontalAdapter;
    LinearLayoutManager HorizontalLayout ;
    View ChildView ;
    int RecyclerViewItemPosition ;
    ArrayList images = new ArrayList<>(Arrays.asList(R.drawable.a, R.drawable.b, R.drawable.c,
            R.drawable.d, R.drawable.e, R.drawable.f, R.drawable.g, R.drawable.h,
            R.drawable.i, R.drawable.j));
    /////////////////////////////////////////////////////////////////////
    RecyclerViewAdapter2 RecyclerViewHorizontalAdapter2;
    ArrayList<String> Number2;
    RecyclerView recyclerview2;
    FloatingActionButton fab;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        backButtonHandler = (BackButtonHandlerInterface) activity;
   //     backButtonHandler.addBackClickListener(this);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();

        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.trading_layout, container, false);
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        Adapter adapter = new Adapter(getContext(), images);
        // Setting Adapter to RecyclerView
        recyclerView.setAdapter(adapter);
        ///list_item/////
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerview1);
        RecyclerViewLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(RecyclerViewLayoutManager);
        AddItemsToRecyclerViewArrayList();
        RecyclerViewHorizontalAdapter = new RecyclerViewAdapter(Number);
        HorizontalLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(HorizontalLayout);
        recyclerView.setAdapter(RecyclerViewHorizontalAdapter);
        list_iten();
        /////////////////////////recyclerview2////////////////////////////////
        recyclerview2 = (RecyclerView)rootView.findViewById(R.id.recyclerview2);
        RecyclerViewLayoutManager = new LinearLayoutManager(getContext());
        recyclerview2.setLayoutManager(RecyclerViewLayoutManager);
        AddItemsToRecyclerViewArrayList2();
        RecyclerViewHorizontalAdapter2 = new RecyclerViewAdapter2(Number2);
        HorizontalLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerview2.setLayoutManager(HorizontalLayout);
        recyclerview2.setAdapter(RecyclerViewHorizontalAdapter2);
        fab=(FloatingActionButton)rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i=new Intent(getActivity(), PhotoEditorActivity.class);//ye fragment h
//                startActivity(i);

                createDialog();
            }
        });
        return rootView;
    }
    public AlertDialog createDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.edit_diolog, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        ((ImageView)dialogView.findViewById(R.id.openGallery)).setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
//                dialog.dismiss();
                openSystemGalleryToSelectAnImage();
            }
        });
        ((ImageView)dialogView.findViewById(R.id.openCamera)).setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
//                dialog.dismiss();
                opencam();
            }
        });
        ((Button)dialogView.findViewById(R.id.cancelId)).setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                dialog.dismiss();
            }
        });
        builder.setView(dialogView);
        dialog.show();
        return dialog;
    }
    private void openSystemGalleryToSelectAnImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, GALLERY_RESULT);
        } else {
            Toast.makeText(
                    getContext(),
                    "No Gallery APP installed",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void openEditor(Uri inputImage) {
        SettingsList settingsList = createPesdkSettingsList();

        // Set input image
        settingsList.getSettingsModel(LoadSettings.class).setSource(inputImage);

        settingsList.getSettingsModel(PhotoEditorSaveSettings.class).setOutputToGallery(Environment.DIRECTORY_DCIM);

        new EditorBuilder(getActivity())
                .setSettingsList(settingsList)
                .startActivityForResult(this, GALLERY_RESULT);
    }
    private void opencam() {
        PhotoEditorSettingsList settingsList = createPesdkSettingsList();
        new CameraPreviewBuilder(getActivity())
                .setSettingsList(settingsList)
                .startActivityForResult(this, PESDK_RESULT,  PermissionRequest.NEEDED_PREVIEW_PERMISSIONS_AND_FINE_LOCATION);

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
        if (resultCode == RESULT_OK && requestCode == PESDK_RESULT) {
            // Editor has saved an Image.
            EditorSDKResult data = new EditorSDKResult(intent);

            data.notifyGallery(EditorSDKResult.UPDATE_RESULT & EditorSDKResult.UPDATE_SOURCE);

            Log.i("PESDK", "Source image is located here " + data.getSourceUri());
            Log.i("PESDK", "Result image is located here " + data.getResultUri());

            // TODO: Do something with the result image

            // OPTIONAL: read the latest state to save it as a serialisation
            SettingsList lastState = data.getSettingsList();
            try {
                new IMGLYFileWriter(lastState).writeJson(new File(
                        Environment.getExternalStorageDirectory(),
                        "serialisationReadyToReadWithPESDKFileReader.json"
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }if (resultCode == RESULT_OK && requestCode == GALLERY_RESULT) {
            // Open Editor with some uri in this case with an image selected from the system gallery.
            Uri selectedImage = intent.getData();
            Log.e("PESDK", "Result image is located here " + selectedImage);
            openEditor(selectedImage);

        } else if (resultCode == RESULT_OK && requestCode == GALLERY_RESULT) {
            // Editor has saved an Image.
            EditorSDKResult data = new EditorSDKResult(intent);

            // This adds the result and source image to Android's gallery
            data.notifyGallery(EditorSDKResult.UPDATE_RESULT & EditorSDKResult.UPDATE_SOURCE);

            Log.i("PESDK", "Source image is located here " + data.getSourceUri());
            Log.i("PESDK", "Result image is located here " + data.getResultUri());

            // TODO: Do something with the result image

            // OPTIONAL: read the latest state to save it as a serialisation
            SettingsList lastState = data.getSettingsList();
            try {
                new IMGLYFileWriter(lastState).writeJson(new File(
                        Environment.getExternalStorageDirectory(),
                        "serialisationReadyToReadWithPESDKFileReader.json"
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (resultCode == RESULT_CANCELED && requestCode == PESDK_RESULT) {
            // Editor was canceled
            EditorSDKResult data = new EditorSDKResult(intent);

            Uri sourceURI = data.getSourceUri();
            // TODO: Do something...
        }
    }
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //    @Override
//    public void onDetach() {
//        super.onDetach();
//
//    }
    @Override
    public void onDetach() {
        super.onDetach();
//        backButtonHandler.removeBackClickListener(this);
//        backButtonHandler = null;
    }
    @Override
    public boolean onBackClick() {
        //Toast.makeText(getContext(), "Do not exit", Toast.LENGTH_SHORT).show();
        return true;
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
            // Inflating the Layout(Instantiates list_item.xml layout file into View object)
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

            // Passing view to ViewHolder
            Adapter.ViewHolder viewHolder = new Adapter.ViewHolder(view);
            return viewHolder;
        }

        // Binding data to the into specified position
        @Override
        public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
            // TypeCast Object to int type
            int res = (int) images.get(position);
            holder.images.setImageResource(res);
        }

        @Override
        public int getItemCount() {
            // Returns number of items currently available in Adapter
            return images.size();
        }

        // Initializing the Views
        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView images;

            public ViewHolder(View view) {
                super(view);
                images = (ImageView) view.findViewById(R.id.imageView);
            }
        }
    }
    public void list_iten(){
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override public boolean onSingleTapUp(MotionEvent motionEvent) {
                    return true;
                }
            });
            @Override
            public boolean onInterceptTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {
                ChildView = Recyclerview.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                if(ChildView != null && gestureDetector.onTouchEvent(motionEvent)) {
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
        Number.add("birthday");
        Number.add("anniversary");
        Number.add("kids photoshoot");
        Number.add("engagement");
        //   Number.add("make up");

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
        public RecyclerViewAdapter.MyView onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_item, parent, false);
            return new RecyclerViewAdapter.MyView(itemView);
        }
        @Override
        public void onBindViewHolder(final RecyclerViewAdapter.MyView holder, final int position) {
            holder.textView.setText(list.get(position));
        }
        @Override
        public int getItemCount() {
            return list.size();
        }
    }


    public void AddItemsToRecyclerViewArrayList2() {
        Number2 = new ArrayList<>();
        Number2.add("Crop");
        Number2.add("Sticker");
        Number2.add("Face++");
        Number2.add("Deepar");
        Number2.add("Ratio");
        Number2.add("Filter");
        Number2.add("3d image");
        Number2.add("Augmented");
        Number2.add("DS Photo Editor");
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
        public RecyclerViewAdapter2.MyView onCreateViewHolder(ViewGroup parent, int viewType) {
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
            return new RecyclerViewAdapter2.MyView(itemView);
        }

        @Override
        public void onBindViewHolder(final RecyclerViewAdapter2.MyView holder, final int position) {
            holder.textView.setText(list.get(position));

            if(holder.textView.getText().equals("Crop")){
                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), CropActivity.class);
                        startActivity(intent);

                    }
                });

            }

            if (holder.textView.getText().equals("3d image")) {
                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ///intent/
                        Intent intent = new Intent(getContext(), AugmentedImageActivity.class);
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
                        Intent intent = new Intent(getContext(), StickerMainActivity.class);
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
                        Intent intent = new Intent(getContext(), LoadingActivity.class);
                        startActivity(intent);
                    }
                });
                //  Intent intent = new Intent(getContext(), FUBeautyActivity.class);
                // startActivity(intent);
            }

        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

}