package com.e.smartscanner.ui.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class PDF {

    Canvas canvas;
    Paint paint;
    PdfDocument pdfDocument;
    PdfDocument.Page page;
    Context context;
    ArrayList<String> uri;
    String name;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    PDF(Context context, ArrayList<String> uri ,String name){

        this.context =  context;
        this.uri =uri;
        this.name  = name;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public  String  createPDF( ){
        File file2 = null;
        try {

            pdfDocument = new PdfDocument();
            paint = new Paint();

            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, uri.size()).create();


            for(int i = 0 ;i<uri.size();i++) {

                page = pdfDocument.startPage(pageInfo);
                canvas = page.getCanvas();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(uri.get(i)));
                bitmap = Bitmap.createScaledBitmap(bitmap,595,842,true);
                canvas.drawBitmap(bitmap,0,0,paint);
                pdfDocument.finishPage(page);
            }
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Smart Scanner22");
            if(!file.exists()){
                file.mkdir();

            }
             file2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Smart Scanner22",name+".pdf");

            pdfDocument.writeTo(new FileOutputStream(file2));


        } catch (IOException e) {
            e.printStackTrace();
        }
        return file2.getAbsolutePath();

    }

}
