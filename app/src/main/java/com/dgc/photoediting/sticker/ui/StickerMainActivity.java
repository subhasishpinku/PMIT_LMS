package com.dgc.photoediting.sticker.ui;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dgc.photoediting.BuildConfig;
import com.dgc.photoediting.R;
import com.dgc.photoediting.sticker.ui.adapter.FontsAdapter;
import com.dgc.photoediting.sticker.utils.FontProvider;
import com.dgc.photoediting.sticker.viewmodel.Font;
import com.dgc.photoediting.sticker.viewmodel.Layer;
import com.dgc.photoediting.sticker.viewmodel.TextLayer;
import com.dgc.photoediting.sticker.widget.MotionView;
import com.dgc.photoediting.sticker.widget.entity.ImageEntity;
import com.dgc.photoediting.sticker.widget.entity.MotionEntity;
import com.dgc.photoediting.sticker.widget.entity.TextEntity;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class StickerMainActivity extends AppCompatActivity implements TextEditorDialogFragment.OnTextLayerCallback {

    public static final int SELECT_STICKER_REQUEST_CODE = 123;

    protected MotionView motionView;
    protected View textEntityEditPanel;
    private final MotionView.MotionViewCallback motionViewCallback = new MotionView.MotionViewCallback() {
        @Override
        public void onEntitySelected(@Nullable MotionEntity entity) {
            if (entity instanceof TextEntity) {
                textEntityEditPanel.setVisibility(View.VISIBLE);
            } else {
                textEntityEditPanel.setVisibility(View.GONE);
            }
        }

        @Override
        public void onEntityDoubleTap(@NonNull MotionEntity entity) {
            startTextEntityEditing();
        }
    };
    private FontProvider fontProvider;
    FloatingActionButton fab;
    ExtendedFloatingActionButton extended_fab;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sicker_activity_main);

        this.fontProvider = new FontProvider(getResources());

        motionView = (MotionView) findViewById(R.id.main_motion_view);

        textEntityEditPanel = findViewById(R.id.main_motion_text_entity_edit_panel);
        motionView.setMotionViewCallback(motionViewCallback);
        fab = (FloatingActionButton)findViewById(R.id.fab);
        extended_fab = (ExtendedFloatingActionButton)findViewById(R.id.extended_fab);
//        Intent iin= getIntent();
//        Bundle b = iin.getExtras();
//        if(b!=null)
//        {
//            String image =(String) b.get("image");
//            System.out.println("imageView"+" "+image);
////            Glide.with(StickerMainActivity.this)
////                    .load(image)
////                    .into();
//
//        }
//        else {
//
//        }
        initToolBar();
        addSticker(R.drawable.zubat);
        initTextEntitiesListeners();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StickerSelectActivity.class);
                startActivityForResult(intent, SELECT_STICKER_REQUEST_CODE);

            }
        });
        extended_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTextSticker();
            }
        });
    }
    public void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Sticker");
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
    private void addSticker(final int stickerResId) {
        motionView.post(new Runnable() {
            @Override
            public void run() {
                Layer layer = new Layer();
                Bitmap pica = BitmapFactory.decodeResource(getResources(), stickerResId);
                Log.e("ADDSTRICKER",""+stickerResId+""+"1");
                ImageEntity entity = new ImageEntity(layer, pica, motionView.getWidth(), motionView.getHeight());
                motionView.addEntityAndPosition(entity);
            }
        });
    }

    private void initTextEntitiesListeners() {
        findViewById(R.id.text_entity_font_size_increase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increaseTextEntitySize();
            }
        });
        findViewById(R.id.text_entity_font_size_decrease).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decreaseTextEntitySize();
            }
        });
        findViewById(R.id.text_entity_color_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTextEntityColor();
            }
        });
        findViewById(R.id.text_entity_font_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTextEntityFont();
            }
        });
        findViewById(R.id.text_entity_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTextEntityEditing();
            }
        });
    }

    private void increaseTextEntitySize() {
        TextEntity textEntity = currentTextEntity();
        if (textEntity != null) {
            textEntity.getLayer().getFont().increaseSize(TextLayer.Limits.FONT_SIZE_STEP);
            textEntity.updateEntity();
            motionView.invalidate();
        }
    }

    private void decreaseTextEntitySize() {
        TextEntity textEntity = currentTextEntity();
        if (textEntity != null) {
            textEntity.getLayer().getFont().decreaseSize(TextLayer.Limits.FONT_SIZE_STEP);
            textEntity.updateEntity();
            motionView.invalidate();
        }
    }

    private void changeTextEntityColor() {
        TextEntity textEntity = currentTextEntity();
        if (textEntity == null) {
            return;
        }

        int initialColor = textEntity.getLayer().getFont().getColor();

        ColorPickerDialogBuilder
                .with(StickerMainActivity.this)
                .setTitle(R.string.select_color)
                .initialColor(initialColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(8) // magic number
                .setPositiveButton(R.string.ok, new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        TextEntity textEntity = currentTextEntity();
                        if (textEntity != null) {
                            textEntity.getLayer().getFont().setColor(selectedColor);
                            textEntity.updateEntity();
                            motionView.invalidate();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    private void changeTextEntityFont() {
        final List<String> fonts = fontProvider.getFontNames();
        FontsAdapter fontsAdapter = new FontsAdapter(this, fonts, fontProvider);
        new AlertDialog.Builder(this)
                .setTitle(R.string.select_font)
                .setAdapter(fontsAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        TextEntity textEntity = currentTextEntity();
                        if (textEntity != null) {
                            textEntity.getLayer().getFont().setTypeface(fonts.get(which));
                            textEntity.updateEntity();
                            motionView.invalidate();
                        }
                    }
                })
                .show();
    }

    private void startTextEntityEditing() {
        TextEntity textEntity = currentTextEntity();
        if (textEntity != null) {
            TextEditorDialogFragment fragment = TextEditorDialogFragment.getInstance(textEntity.getLayer().getText());
            fragment.show(getFragmentManager(), TextEditorDialogFragment.class.getName());
        }
    }

    @Nullable
    private TextEntity currentTextEntity() {
        if (motionView != null && motionView.getSelectedEntity() instanceof TextEntity) {
            return ((TextEntity) motionView.getSelectedEntity());
        } else {
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.stiker_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.main_add_sticker) {
            Intent intent = new Intent(this, StickerSelectActivity.class);
            startActivityForResult(intent, SELECT_STICKER_REQUEST_CODE);
            return true;
        } else if (item.getItemId() == R.id.main_add_text) {
            addTextSticker();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void addTextSticker() {
        TextLayer textLayer = createTextLayer();
        TextEntity textEntity = new TextEntity(textLayer, motionView.getWidth(),
                motionView.getHeight(), fontProvider);
        motionView.addEntityAndPosition(textEntity);

        // move text sticker up so that its not hidden under keyboard
        PointF center = textEntity.absoluteCenter();
        center.y = center.y * 0.5F;
        textEntity.moveCenterTo(center);

        // redraw
        motionView.invalidate();

        startTextEntityEditing();
    }

    private TextLayer createTextLayer() {
        TextLayer textLayer = new TextLayer();
        Font font = new Font();
        font.setColor(Color.WHITE);
        font.setSize(TextLayer.Limits.INITIAL_FONT_SIZE);
        font.setTypeface(fontProvider.getDefaultFontName());
        textLayer.setFont(font);

        if (BuildConfig.DEBUG) {
            textLayer.setText("");
        }
        return textLayer;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_STICKER_REQUEST_CODE) {
                if (data != null) {
                    int stickerId = data.getIntExtra(StickerSelectActivity.EXTRA_STICKER_ID, 0);
                    if (stickerId != 0) {
                        addSticker(stickerId);
                        Log.e("ADDSTRICKER",""+stickerId+""+"0");
                    }


                }

            }
        }
    }

    @Override
    public void textChanged(@NonNull String text) {
        TextEntity textEntity = currentTextEntity();
        if (textEntity != null) {
            TextLayer textLayer = textEntity.getLayer();
            if (!text.equals(textLayer.getText())) {
                textLayer.setText(text);
                textEntity.updateEntity();
                motionView.invalidate();
            }
        }
    }
}
