package com.dgc.photoediting.PopUp;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.dgc.photoediting.R;

public class PopupClass {

    public PopupClass() {
    }

    public static void showPopUpWithTitleMessageOneButton(final Activity mContext, final String Btn1, final String title, final String message, final String result, final PopupCallBackOneButton popupCallBackOneButton) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final Dialog customDialog = new Dialog(mContext);
                customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                customDialog.setCancelable(false);

                LayoutInflater layoutInflater = (LayoutInflater) mContext.getApplicationContext().getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
                View view = layoutInflater.inflate(R.layout.popup_title_message_button, null);
                customDialog.setContentView(view);

                TextView tv_popup_title = view.findViewById(R.id.tv_popup_title);
                TextView tv_popup_message = view.findViewById(R.id.tv_popup_message);
                TextView tv_popup_result = view.findViewById(R.id.tv_popup_result);
                Button btn_1 = view.findViewById(R.id.btn_1);

                Typeface regularFont = ResourcesCompat.getFont(mContext, R.font.calibri_normal);
                Typeface boldFont = ResourcesCompat.getFont(mContext, R.font.calibri_bold);

                tv_popup_title.setTypeface(boldFont);
                tv_popup_message.setTypeface(regularFont);
                tv_popup_result.setTypeface(boldFont);
                btn_1.setTypeface(regularFont);

                tv_popup_title.setText(title);
                tv_popup_message.setText(message);
                tv_popup_result.setText(result);
                btn_1.setText(Btn1);

                if (title.isEmpty()) {
                    tv_popup_title.setVisibility(View.GONE);
                }

                btn_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupCallBackOneButton.onFirstButtonClick();
                        customDialog.dismiss();
                    }
                });

                customDialog.show();

            }
        });


    }


}
