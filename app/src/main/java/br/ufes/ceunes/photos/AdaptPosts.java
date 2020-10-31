package br.ufes.ceunes.photos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdaptPosts extends RecyclerView.Adapter<HolderPosts> {

    private final ArrayList<Post> posts;
    private Context context;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    public AdaptPosts(Context context, ArrayList posts) {
        this.context = context;
        this.posts = posts;
    }

    @Override
    public HolderPosts onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HolderPosts(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_posts, parent, false));
    }

    @Override
    public void onBindViewHolder(final HolderPosts holder, int position) {
        final Post post = posts.get(position);

        database = DatabaseConfig.getDatabase();
        reference = database.getReference("posts");

        Activity activity = (Activity) context;
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        double larguraDaTela = size.x;

        holder.image.requestLayout();
        double proporcao = Double.parseDouble(post.getHeight()) / Double.parseDouble(post.getWidth());
        holder.image.getLayoutParams().height = (int) Math.round(proporcao * larguraDaTela);

        Glide.with(context).load(post.getImage()).into(holder.image);
        holder.description.setText(post.getDescription());
        holder.time.setText(makeHorarioOferta(post.getTime()));

        if (holder.description.getText().toString().isEmpty())
            holder.description.setVisibility(View.GONE);
    }


    @Override
    public int getItemCount() {
        return posts != null ? posts.size() : 0;
    }

    private String makeHorarioOferta(Long horario) {
        String resp;
        Long horarioAtual = System.currentTimeMillis();
        long diferenca = horarioAtual - horario;
        long aux;

        if (diferenca < 1000) {
            resp = "agora";
        } else {
            if (diferenca < (1000 * 60)) {
                aux = diferenca / 1000;
                resp = aux + "s";
            } else {
                if (diferenca < (1000 * 60 * 60)) {
                    aux = diferenca / (1000 * 60);
                    resp = aux + " min";
                } else {
                    if (diferenca < (1000 * 60 * 60 * 24)) {
                        aux = diferenca / (1000 * 60 * 60);
                        resp = aux + "h";
                    } else {
                        if (diferenca < (1000 * 60 * 60 * 24 * 7)) {
                            aux = diferenca / (1000 * 60 * 60 * 24);
                            resp = aux + "d";
                        } else {
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
                            resp = sdf.format(new Date(horario));
                        }
                    }
                }
            }
        }
        return resp;
    }
}


