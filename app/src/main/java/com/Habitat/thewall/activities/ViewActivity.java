package com.Habitat.thewall.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Habitat.thewall.R;
import com.Habitat.thewall.adapters.ViewAdapter;
import com.Habitat.thewall.models.HomeView;
import com.Habitat.thewall.models.Wallpaper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewActivity extends AppCompatActivity {

    List<HomeView> viewList;
    List<HomeView> favList;
    RecyclerView recyclerView;
    ViewAdapter adapter;

    DatabaseReference dbWallpapers, dbFavs;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        Intent intent = getIntent();

        final String Home = intent.getStringExtra("Home");

        favList = new ArrayList<>();
        viewList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        
        adapter = new ViewAdapter(this, viewList, getWindowManager(), this);

        recyclerView.setAdapter(adapter);

        progressBar = findViewById(R.id.progressbar);


        assert Home != null;
        dbWallpapers = FirebaseDatabase.getInstance().getReference("wallpapers")
                .child(Home);

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            dbFavs = FirebaseDatabase.getInstance().getReference("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("favourites")
                    .child(Home);
            fetchFavWallpapers(Home);
        }else{
            fetchWallpapers(Home);
        }




    }

    private void fetchFavWallpapers(final String Home){
        progressBar.setVisibility(View.GONE);
        dbFavs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    for(DataSnapshot wallpaperSnapshot: dataSnapshot.getChildren()){

                        String id = wallpaperSnapshot.getKey();
                        String title = wallpaperSnapshot.child("title").getValue(String.class);

                        String url = wallpaperSnapshot.child("url").getValue(String.class);

                        HomeView w = new HomeView(id, title, url, Home);
                        favList.add(w);
                    }
                }
                fetchWallpapers(Home);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseerror) {

            }
        });

    }

    private void fetchWallpapers(final String Home){

        progressBar.setVisibility(View.GONE);
        dbWallpapers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    for(DataSnapshot wallpaperSnapshot: dataSnapshot.getChildren()){

                        String id = wallpaperSnapshot.getKey();
                        String title = wallpaperSnapshot.child("title").getValue(String.class);

                        String url = wallpaperSnapshot.child("url").getValue(String.class);

                        HomeView w = new HomeView(id, title, url, Home);

                        if(isFavourites(w)){
                            w.isFavourites = true;
                        }
                        viewList.add(w);
                    }
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseerror) {

            }
        });

    }
    private boolean isFavourites(HomeView w){
        for(HomeView f: favList){
            if(f.id.equals(w.id)){
                return true;
            }
        }
        return false;

    }
}