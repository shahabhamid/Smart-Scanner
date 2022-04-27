package com.e.smartscanner.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

public class ChooseImage extends AppCompatActivity {

    int REQUEST_CODE = 100;
    public static  int PICK_CODE = 1000;
    public static  int PERMISSION_CODE = 1001;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_CODE){
            if((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                openGallery();
            }else{
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show();
            }
        }
    }


    public void ImagePermission(){
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.M){

            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_DENIED){

                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissions,PERMISSION_CODE);
            }
            else{
                openGallery();
            }

        }
        else {
            openGallery();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImagePermission();
    }

    public  void openGallery(){

        int preference = ScanConstants.OPEN_MEDIA;
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
        startActivityForResult(intent, REQUEST_CODE);

      }
}
