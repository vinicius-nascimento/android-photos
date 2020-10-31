package br.ufes.ceunes.photos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdaptPosts adaptPosts;
    private ArrayList<Post> posts;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        adaptPosts = new AdaptPosts(context, new ArrayList());
        recyclerView.setAdapter(adaptPosts);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        posts = new ArrayList<>();

        database = DatabaseConfig.getDatabase();
        reference = database.getReference("posts");


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewPostActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                posts.clear();
                for (DataSnapshot postsSnapshot : dataSnapshot.getChildren()) {
                    String id = postsSnapshot.getKey();
                    String image = (String) postsSnapshot.child("image").getValue();
                    String height = (String) postsSnapshot.child("height").getValue();
                    String width = (String) postsSnapshot.child("width").getValue();
                    String description = (String) postsSnapshot.child("description").getValue();
                    Long time = (Long) postsSnapshot.child("time").getValue();
                    Post post = new Post(id, image, height, width, description, time);
                    posts.add(0, post);
                }
                adaptPosts = new AdaptPosts(context, posts);
                recyclerView.setAdapter(adaptPosts);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
