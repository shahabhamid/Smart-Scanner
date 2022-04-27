package com.e.smartscanner.ui.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.e.smartscanner.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GliderActivityClass extends AppCompatActivity {

    RecyclerView recyclerView ;
    GlideRecycler glideRecycler;
    FloatingActionButton delete;
    public static int positionDelete ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glider_class);

        recyclerView = findViewById(R.id.gliderrecycle);
         glideRecycler=new GlideRecycler(AddImage.imageUri,this);
        recyclerView.setAdapter(glideRecycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        SnapHelper snapHelper = new PagerSnapHelper();
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setLayoutManager(linearLayoutManager);
        snapHelper.attachToRecyclerView(recyclerView);




    }
}