package com.e.smartscanner.ui.home;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.e.smartscanner.R;
import com.scanlibrary.ScanConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class HomeFragment extends Fragment {

    ArrayList<String > list = new ArrayList<>();
    ArrayList<ViewPDF> Imagelist = new ArrayList<>();
    ArrayList< ArrayList<ViewPDF>> data = new ArrayList<>();
    RecyclerView recyclerView;
    ImageButton captureImage;
    ImageButton searchBTN;
    ImageButton sortBTN;
    EditText searchedit;
    SqliteHelper sqliteHelper;
    ViewPDF viewPDF;
    String searchvalue="";
    boolean searchvisible = true;
    int truefalse=0;


    void getData() {
    Imagelist.clear();
    data.clear();
    String name = "";
    int ID=0;
        try {
            SQLiteDatabase db = getActivity().openOrCreateDatabase("scanner", Context.MODE_PRIVATE, null);
            Cursor c = db.rawQuery("select * from smart", null);
            if (c.moveToFirst()) {

                String value;
                    do {


                         value =c.getString(c.getColumnIndex("Name"));


                        if(value.equals(searchvalue)||
                               value.startsWith(searchvalue)||
                                value.contains(searchvalue)||
                        searchvalue.equals(""))
                       {
                           System.out.println("value  "+ value +" search value "+searchvalue+"INSIDE LIST");
                            viewPDF = new ViewPDF();

                            viewPDF.setFileName(c.getString(c.getColumnIndex("Name")));
                            viewPDF.setFilePath(c.getString(c.getColumnIndex("FilePath")));
                            viewPDF.setImagePath(c.getString(c.getColumnIndex("ImagePath")));
                            viewPDF.setDateCreated(c.getString(c.getColumnIndex("Date")));

                            String id = c.getString(c.getColumnIndex("Name"));

                            if (name.equals(id)) {
                                Imagelist.add(viewPDF);
                                if (c.isLast()) {
                                    data.add(Imagelist);
                                }
                            } else if (name.equals("")) {
                                Imagelist.add(viewPDF);
                                name = id;
                                if (c.isLast()) {
                                    data.add(ID, Imagelist);
                                    ID++;
                                }
                            } else {

                                name = id;
                                data.add(ID, Imagelist);
                                Imagelist = new ArrayList<>();
                                Imagelist.add(viewPDF);
                                ID++;
                                if (c.isLast()) {
                                    data.add(Imagelist);
                                }
                            }
                      }
                    } while (c.moveToNext());

                }

        } catch (Exception ex) {

        }

    }

 void sort(){

     Collections.sort(data, new Comparator<ArrayList<ViewPDF>>() {
         @Override
         public int compare(ArrayList<ViewPDF> o1, ArrayList<ViewPDF> o2) {
             return o1.get(0).getFileName().toLowerCase().compareTo(o2.get(0).getFileName().toLowerCase());
         }
     });

    }
    void setOrderDes() {
        sort();
        int i = 0;
        ArrayList<ArrayList<ViewPDF>> descend = new ArrayList<>();
        data.trimToSize();
        for (i = data.size() - 1; i >= 0; i--) {
            descend.add(data.get(i));
        }
        viewPDFsAdapter =new ViewPDFsAdapter(getContext(), descend);
        recyclerView.setAdapter(viewPDFsAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
    }


    void init(View view){
        captureImage = view.findViewById(R.id.Hcapture);
        recyclerView = view.findViewById(R.id.H_recyclarView);
        searchBTN = view.findViewById(R.id.H_search);
        searchedit  =view.findViewById(R.id.searchpdf);
        sortBTN  = view.findViewById(R.id.sortButton);

    }
    ViewPDFsAdapter viewPDFsAdapter;
    void setAdapterNlayout(){
        getData();
       viewPDFsAdapter =new ViewPDFsAdapter(getContext(), data);
        recyclerView.setAdapter(viewPDFsAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
    }
    public void filter(String text) {
        //new array list that will hold the filtered data
        ArrayList< ArrayList<ViewPDF>> datafilter = new ArrayList<>();

        //looping through existing elements

        for (ArrayList<ViewPDF> s : data) {
            //if the existing elements contains the search input
            if (s.get(0).getFileName().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                datafilter.add(s);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        viewPDFsAdapter.filterList(datafilter);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        init(root);

        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), OpenCamera.class));
            }
        });
        searchedit.setVisibility(View.INVISIBLE);
        searchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(searchvisible==false) {
                    searchedit.setVisibility(View.INVISIBLE);
                    searchedit.setText("");
                    searchvisible = true;
                }else if(searchvisible ==true){

                    searchedit.setVisibility(View.VISIBLE);
                    searchvisible = false;
                }


            }
        });

            searchedit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    searchvalue = searchedit.getText().toString();
                   filter(searchvalue);
                   //getData();
                   //viewPDFsAdapter.notifyDataSetChanged();
                }
            });

            setAdapterNlayout();

            sortBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(truefalse==0) {
                        sort();
                        truefalse++;
                        viewPDFsAdapter =new ViewPDFsAdapter(getContext(), data);
                        recyclerView.setAdapter(viewPDFsAdapter);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                        recyclerView.setLayoutManager(linearLayoutManager);

                    }

                        else if(truefalse ==1){
                            truefalse--;

                        setOrderDes();


                    }

                    //setAdapterNlayout();
                }
            });

        return root;
    }

}