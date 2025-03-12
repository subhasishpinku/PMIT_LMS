package com.dgc.photoediting.utility;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.view.View;
import android.view.ViewGroup;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class Utility {
    private static final String ALLOWED_CHARACTERS = "0123456789QWERTYUIOPASDFGHJKLZXCVBNM";
    public  static final Set<String> someStringSetCODE=new HashSet<>();


    public static boolean checkConnectivity(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }


    public static String setCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(c);
        return date;
    }

    public static String changeDateFormat(String inputFormat, String outputFormat, String inputDate) {
        DateFormat iFormat, oFormat = null;
        Date date = null;
        try {
            iFormat = new SimpleDateFormat(inputFormat);
            oFormat = new SimpleDateFormat(outputFormat);
            date = iFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return oFormat.format(date);
    }

    public static String getRandomString(int sizeOfRandomString) {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }



    public static Bitmap getViewBitmap(View v, int maxWidth, int maxHeight) {
        ViewGroup.LayoutParams vParams = v.getLayoutParams();

        //If the View hasn't been attached to a layout, or had LayoutParams set
        //return null, or handle this case however you want
        if (vParams == null) {
            return null;
        }

        int wSpec = measureSpecFromDimension(vParams.width, maxWidth);
        int hSpec = measureSpecFromDimension(vParams.height, maxHeight);

        v.measure(wSpec, hSpec);

        final int width = v.getMeasuredWidth();
        final int height = v.getMeasuredHeight();

        //Cannot make a zero-width or zero-height bitmap
        if (width == 0 || height == 0) {
            return null;
        }

        v.layout(0, 0, width, height);

        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        v.draw(canvas);

        return result;
    }

    private static int measureSpecFromDimension(int dimension, int maxDimension) {
        switch (dimension) {
            case ViewGroup.LayoutParams.MATCH_PARENT:
                return View.MeasureSpec.makeMeasureSpec(maxDimension, View.MeasureSpec.EXACTLY);
            case ViewGroup.LayoutParams.WRAP_CONTENT:
                return View.MeasureSpec.makeMeasureSpec(maxDimension, View.MeasureSpec.AT_MOST);
            default:
                return View.MeasureSpec.makeMeasureSpec(dimension, View.MeasureSpec.EXACTLY);
        }
    }

    public static Image bitmapToImage(Bitmap bitmap, Document document) {
        Image image = null;
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            image = Image.getInstance(stream.toByteArray());
            float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                    - document.rightMargin() - 0) / image.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
            image.scalePercent(scaler);
            image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public static String changeDateFormat(Calendar cal, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        return (sdf.format(cal.getTime()));
    }

    public static int getBatteryPercentage(Context context) {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, intentFilter);
        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;
        float batteryperc = level / (float) scale;

        return (int) (batteryperc * 100);
    }

    public static Bitmap getRoundedCornerBitmap(Context context, Bitmap input, int pixels , int w , int h , boolean squareTL, boolean squareTR, boolean squareBL, boolean squareBR  ) {

        Bitmap output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);

//make sure that our rounded corner is scaled appropriately
        final float roundPx = 50F;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);


//draw rectangles over the corners we want to be square
        if (squareTL ){
            canvas.drawRect(0, 0, w/2, h/2, paint);
        }
        if (squareTR ){
            canvas.drawRect(w/2, 0, w, h/2, paint);
        }
        if (squareBL ){
            canvas.drawRect(0, h/2, w/2, h, paint);
        }
        if (squareBR ){
            canvas.drawRect(w/2, h/2, w, h, paint);
        }

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(input, 0,0, paint);

        return output;
    }

}

