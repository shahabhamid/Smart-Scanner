package com.e.smartscanner.ui.home;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.e.smartscanner.BuildConfig;
import com.e.smartscanner.MainActivity;
import com.scanlibrary.ScanConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class ViewPDF  {

    private String imagePath;
    private String FilePath;
    private String DateCreated;
    private String FileName;

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public String getDateCreated() {
        return DateCreated;
    }

    public void setDateCreated(String dateCreated) {
        DateCreated = dateCreated;
    }

    public void open(String path, Context context){
        try {
            File file2 = new File(path);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri apkURI = FileProvider.getUriForFile(context, context.getPackageName()+ ".provider", file2);
                intent.setDataAndType(apkURI, "application/pdf");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                intent.setDataAndType(Uri.fromFile(file2),"application/pdf");
            }
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No Application available to view pdf", Toast.LENGTH_LONG).show();
        }catch (Exception ex){
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    public void deletePDF(ArrayList<ViewPDF> list, Context context){
        File f = new File(list.get(0).getFilePath());
        f.delete();

        for (int i = 0 ;i<list.size();i++){
            File f1 = new File(Uri.parse(list.get(i).getImagePath()).getPath());
            f1.delete();
        }

        SQLiteDatabase db = context.openOrCreateDatabase("scanner", Context.MODE_PRIVATE, null);
        Cursor c = db.rawQuery("Delete from smart where Name = ? ", null);
        db.delete("smart", "Name=?", new String[]{list.get(0).getFileName()});
        Toast.makeText(context," Deleted!",Toast.LENGTH_LONG).show();

    }
    public void deleteImage(String imagePath, Context context){

        File file = new File(Uri.parse(imagePath).getPath());
        file.delete();
        SQLiteDatabase db = context.openOrCreateDatabase("scanner", Context.MODE_PRIVATE, null);
        Cursor c = db.rawQuery("Delete from smart where ImagePath = ? ", null);
        db.delete("smart", "ImagePath=?", new String[]{imagePath});
        Toast.makeText(context," Deleted!",Toast.LENGTH_LONG).show();

    }
    public void edit(ViewPDF viewPDF,String newName){
        File file = new File(ScanConstants.IMAGE_PATH,viewPDF.getFileName()+".pdf");
        file.renameTo(new File(ScanConstants.IMAGE_PATH,newName+".pdf"));
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void share(Context context, File outputFile){


        Uri uri=FileProvider.getUriForFile(Objects.requireNonNull(context.getApplicationContext()), BuildConfig.APPLICATION_ID + ".provider", outputFile);
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.setType("application/pdf");
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(share, "Share it"));



    }
}
