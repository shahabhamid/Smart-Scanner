package com.e.smartscanner.ui.home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.e.smartscanner.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ViewPDFsAdapter extends  RecyclerView.Adapter<ViewPDFsAdapter.viewholder> {

    private final Context context;
    private ArrayList<ArrayList<ViewPDF >> list;

    ViewPDFsAdapter(Context context, ArrayList<ArrayList<ViewPDF >> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_view_pdfs,parent,false);
        return new viewholder(view);
    }
    public void filterList(ArrayList<ArrayList<ViewPDF >> filterdNames) {
        this.list = filterdNames;
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {

        final ViewPDF viewPDF = list.get(position).get(0);
        holder.name.setText(viewPDF.getFileName());
        holder.date.setText(viewPDF.getDateCreated());

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(list.get(position).get(0).getImagePath()));
            bitmap = Bitmap.createScaledBitmap(bitmap,256,256,false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder.imageView.setImageBitmap(bitmap);
        edit(holder,position);
        delete(holder,viewPDF,position);
        share(holder,viewPDF);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPDF.open(viewPDF.getFilePath(),context);
            }
        });

    }
 public  void share(viewholder viewholder, final ViewPDF viewPDF){
        viewholder.share.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                viewPDF.share(context,new File(viewPDF.getFilePath()));
            }
        });
 }
    public void delete (viewholder holder, final ViewPDF viewPDF, final int position){
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to delete this File? ")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                viewPDF.deletePDF(list.get(position),context);
                                list.remove(position);
                                notifyDataSetChanged();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();

            }
        });
    }
    public  void edit(viewholder viewholder, final int position){

        final ArrayList<String> imageUri = new ArrayList<>();

        for(int i = 0;i<list.get(position).size();i++){
            imageUri.add(i,list.get(position).get(i).getImagePath());
        }

        viewholder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,AddImage.class);
                intent.putExtra("image",imageUri);
                intent.putExtra("edit",1);
                intent.putExtra("name",list.get(position).get(0).getFileName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewholder extends RecyclerView.ViewHolder{


        ImageView imageView;
        CardView cardView;
        ImageButton edit,share,delete;
        TextView name,date;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.SVP_imageView);
            edit = itemView.findViewById(R.id.SVP_edit);
            share = itemView.findViewById(R.id.SVP_share);
            delete = itemView.findViewById(R.id.SVP_delete);
            name = itemView.findViewById(R.id.SVP_title);
            date = itemView.findViewById(R.id.SVP_date);
            cardView = itemView.findViewById(R.id.SVP_cardview);
            cardView.setClickable(true);

        }
    }
}
