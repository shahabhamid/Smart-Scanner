package com.e.smartscanner.ui.home;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.e.smartscanner.MainActivity;
import com.e.smartscanner.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.scanlibrary.ScanConstants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddImage extends AppCompatActivity {

    public static  ArrayList<String> imageUri = new ArrayList<String>();
    RecyclerView recyclerView;
    public static RecyclerViewAdapter viewAdapter;
    FloatingActionButton openCamera;
    FloatingActionButton openGallery;
    FloatingActionButton share;
    String Name  = "";
    int EDIT=0;
    void init(){

        recyclerView = findViewById(R.id.AI_recyclerView);
        openCamera = findViewById(R.id.AI_camera);
        openGallery = findViewById(R.id.AI_gallery);
        share  = findViewById(R.id.AI_share);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getSupportActionBar().setDisplayShowHomeEnabled(true);
        setContentView(R.layout.activity_add_image);

        init();

        EDIT =getIntent().getIntExtra("edit",0);
        if(EDIT==1){
            imageUri = getIntent().getStringArrayListExtra("image");
            Name = getIntent().getStringExtra("name");

            Toast.makeText(this,"EDIT ",Toast.LENGTH_LONG).show();
        }else {
            String uri = getIntent().getStringExtra("image");
            imageUri.add(uri);
        }
        viewAdapter = new RecyclerViewAdapter(imageUri,this);
        recyclerView.setAdapter(viewAdapter);

        GridLayoutManager manager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(manager);

        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(EDIT==1){
                    delete();
                }
                startActivity(new Intent(AddImage.this,OpenCamera.class));
            }
        });
        openGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(EDIT==1){
                    delete();
                }
                startActivity(new Intent(AddImage.this,ChooseImage.class));

            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                setShare();
            }
        });

    }

    void setShare(){


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("SAVE PDF AND SHARE");
        View viewInflated = getLayoutInflater().inflate(R.layout.save_pdf_dialog, (ViewGroup) findViewById(R.id.content), false);
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        builder.setView(viewInflated);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                if(EDIT==1){
                    delete();
                }
                m_Text = input.getText().toString();
                PDF pdf  = new PDF(getApplicationContext(),imageUri,m_Text);
                cancel = false;
                  path= pdf.createPDF();

                add();
                imageUri.clear();
                ViewPDF viewPDF = new ViewPDF();
                viewPDF.share(getApplicationContext(),new File(path));

            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        builder.show();
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage("Are you sure you want to close this activity? All Data will be LOST")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("EXIT", true);
                        startActivity(intent);

                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.save:

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    if(EDIT==1){
                        delete();
                    }
                        save();

                }
                break;

            case android.R.id.home:

                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Closing Activity")
                        .setMessage("Are you sure you want to close this activity? All Data will be LOST")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("EXIT", true);
                                startActivity(intent);

                            }

                        })
                        .setNegativeButton("No", null)
                        .show();

                break;

        }
        return true;
    }
    private String m_Text="";
    private String path=null;

    boolean cancel = false;
    public void save(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("SAVE PDF AND SHARE");
        View viewInflated = getLayoutInflater().inflate(R.layout.save_pdf_dialog, (ViewGroup) findViewById(R.id.content), false);

        final EditText input = (EditText) viewInflated.findViewById(R.id.input);

        builder.setView(viewInflated);


        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                m_Text = input.getText().toString();

                PDF pdf  = new PDF(getApplicationContext(),imageUri,m_Text);

                cancel = false;
                path= pdf.createPDF();
                Toast.makeText(getApplicationContext(),"filepath "+ path,Toast.LENGTH_SHORT).show();

                add();
                imageUri.clear();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);

            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancel = true;
                dialog.cancel();
            }
        });

        builder.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_save,menu);
        return true;
    }
    SqliteHelper sqliteHelper;
    public void add(){
        sqliteHelper = new SqliteHelper(this,"scanner",null,1);
        sqliteHelper.queryData("CREATE TABLE IF NOT EXISTS smart (Name VARCHAR,ImagePath VARCHAR,FilePath VARCHAR,Date VARCHAR)");

        String timeStamp = new SimpleDateFormat("dd/MM/YYYY").format(new Date());

        for (int i = 0 ; i<imageUri.size();i++) {
            sqliteHelper.insertData(m_Text,imageUri.get(i),path,timeStamp);
        }

    }
    public void delete(){
        File f = new File(ScanConstants.IMAGE_PATH+Name);
        f.delete();

        SQLiteDatabase db = openOrCreateDatabase("scanner", Context.MODE_PRIVATE, null);
        Cursor c = db.rawQuery("Delete from smart where Name = ? ", null);
        db.delete("smart", "Name=?", new String[]{Name});



    }
}