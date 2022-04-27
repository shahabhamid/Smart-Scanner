package com.e.smartscanner.ui.home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

public class OpenCamera extends AppCompatActivity {

    public  static  final int REQUEST_CAMERA= 1;
    int REQUEST_CODE = 99;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        askCameraPermission();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CAMERA){

            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openCamera();
            }else{
                Toast.makeText(this,"Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void askCameraPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,new String [] {Manifest.permission.CAMERA},REQUEST_CAMERA);

        }
        else{
            openCamera();
        }
    }

    public void openCamera(){

      int preference = ScanConstants.OPEN_CAMERA;
      Intent intent = new Intent(this, ScanActivity.class);
      intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
      startActivityForResult(intent, REQUEST_CODE);

    }
}
