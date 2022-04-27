package com.scanlibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jhansi on 05/04/15.
 */
public class Utils {

    private Utils() {

    }

    public static Uri getUri(Context context, Bitmap bitmap) {
     //   ByteArrayOutputStream bytes = new ByteArrayOutputStream();
       // bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
       //String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

     //   File file = new File(ScanConstants.IMAGE_PATH+"/Temp", "IMG_Edited" + timeStamp + ".jpg");
       // System.out.println("ajasdjfjkashdf path of image"+path+" jkasdfk "+file.getAbsolutePath());



        File file = null;
        // Assume block needs to be inside a Try/Catch block.
    try {
        OutputStream fOut = null;
        File file3 = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Smart Scanner22");
        if(!file3.exists()){
            file3.mkdir();

        }
         file = new File(ScanConstants.IMAGE_PATH, "IMG_Edited" + timeStamp + ".jpg");

        fOut = new FileOutputStream(file);
        Bitmap pictureBitmap =bitmap; // obtaining the Bitmap
        pictureBitmap.compress(Bitmap.CompressFormat.JPEG,50 , fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate

        fOut.close(); // do not forget to close the stream

       //     MediaStore.Images.Media.insertImage(context.getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
        e.printStackTrace();
    }


        return Uri.fromFile(file);
    }

    public static Bitmap getBitmap(Context context, Uri uri) throws IOException {
        File file3 = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Smart Scanner22");
        if(!file3.exists()){
            file3.mkdir();

        }
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        return bitmap;
    }
}