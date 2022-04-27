package com.e.smartscanner.ui.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.e.smartscanner.R;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.viewHolder> {


    ArrayList<String> list;
    Context context;

    public RecyclerViewAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.sample_add_image,parent,false);
        return new viewHolder(view);
    }
    int place;
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.viewHolder holder, int position) {

        String uri = AddImage.imageUri.get(position);
        holder.imageView.setImageURI(Uri.parse(uri));
        place = position;
        pager(holder);

    }
        public  void pager(viewHolder holder) {
        holder.imageView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, GliderActivityClass.class);
            intent.putExtra("image", list);
            intent.putExtra("place",place);
            context.startActivity(intent);
        }});
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.SAI_imageView);
        }
    }
}





