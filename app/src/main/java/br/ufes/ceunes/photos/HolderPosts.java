package br.ufes.ceunes.photos;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.github.chrisbanes.photoview.PhotoView;

public class HolderPosts extends RecyclerView.ViewHolder {

    public PhotoView image;
    public TextView time, description;

    public HolderPosts(View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.img);
        time = itemView.findViewById(R.id.txt_time);
        description = itemView.findViewById(R.id.txt_desc);
    }
}