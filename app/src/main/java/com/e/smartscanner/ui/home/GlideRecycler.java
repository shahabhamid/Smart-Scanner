
package com.e.smartscanner.ui.home;

        import android.content.Context;
        import android.content.Intent;
        import android.net.Uri;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;

        import com.e.smartscanner.R;
        import com.google.android.material.floatingactionbutton.FloatingActionButton;

        import java.lang.reflect.Method;
        import java.util.ArrayList;

public class GlideRecycler extends RecyclerView.Adapter<GlideRecycler.viewHolder> {


    ArrayList<String> list;
    Context context;

    public GlideRecycler(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.glide,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        String uri = list.get(position);
        holder.imageView.setImageURI(Uri.parse(uri));
        final int delpos = position;
        Toast.makeText(context,"position"+ GliderActivityClass.positionDelete ,Toast.LENGTH_SHORT).show();

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new ViewPDF().deleteImage(AddImage.imageUri.get(delpos),context);
                AddImage.imageUri.remove(delpos);
                notifyDataSetChanged();
            AddImage.viewAdapter.notifyDataSetChanged();
            }
        });
        //place = position;
        //pager(holder);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        FloatingActionButton delete;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.glideimage);
            delete = itemView.findViewById(R.id.delete_glide);
        }
    }
}





