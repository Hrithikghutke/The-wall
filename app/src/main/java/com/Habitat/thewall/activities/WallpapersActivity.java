package com.Habitat.thewall.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.Habitat.thewall.R;
import com.Habitat.thewall.adapters.WallpapersAdapter;
import com.Habitat.thewall.models.Wallpaper;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WallpapersActivity extends AppCompatActivity {

    private ReviewManager manager;
    private ReviewInfo reviewInfo;

    List<Wallpaper> wallpaperList;
    List<Wallpaper> favList;
    RecyclerView recyclerView;
    WallpapersAdapter adapter;

    DatabaseReference dbWallpapers, dbFavs;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpapers);

        Intent intent = getIntent();

        final String category = intent.getStringExtra("category");

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbarlayout);

        favList = new ArrayList<>();
        wallpaperList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        
        adapter = new WallpapersAdapter(this, wallpaperList, getWindowManager(), this);

        recyclerView.setAdapter(adapter);

        progressBar = findViewById(R.id.spin_kit);
        init();





        dbWallpapers = FirebaseDatabase.getInstance().getReference("images")
                .child(category);



        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            dbFavs = FirebaseDatabase.getInstance().getReference("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("favourites")
                    .child(category);
            fetchFavWallpapers(category);
        }else{
            fetchWallpapers(category);
        }


    }

    private void init() {
        manager = ReviewManagerFactory.create(this);

        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(new OnCompleteListener<ReviewInfo>() {
            @Override
            public void onComplete(Task<ReviewInfo> task) {
                if (task.isSuccessful()) {
                    // We can get the ReviewInfo object
                    reviewInfo = task.getResult();
                } else {
                    Toast.makeText(WallpapersActivity.this, "There Was Some Error", Toast.LENGTH_SHORT).show();
                    // There was some problem, continue regardless of the result.
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        if(reviewInfo != null){

            Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
            flow.addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.

                   // if(task.isSuccessful()){
                      //  Toast.makeText(WallpapersActivity.this, "Review Successful", Toast.LENGTH_SHORT).show();

                    //}else{
                      //  Toast.makeText(WallpapersActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    //}
                }
            });
        }
        super.onBackPressed();
    }

    private void fetchFavWallpapers(final String category){

        progressBar.setVisibility(View.GONE);
        dbFavs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    for(DataSnapshot wallpaperSnapshot: dataSnapshot.getChildren()){

                        String id = wallpaperSnapshot.getKey();
                        String title = wallpaperSnapshot.child("title").getValue(String.class);
                        String desc = wallpaperSnapshot.child("desc").getValue(String.class);
                        String url = wallpaperSnapshot.child("url").getValue(String.class);

                        Wallpaper w = new Wallpaper(id, title, desc, url, category);
                        favList.add(w);
                    }
                }
                fetchWallpapers(category);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseerror) {

            }
        });



    }

    private void fetchWallpapers(final String category){

        progressBar.setVisibility(View.GONE);
        dbWallpapers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    for(DataSnapshot wallpaperSnapshot: dataSnapshot.getChildren()){

                        String id = wallpaperSnapshot.getKey();
                        String title = wallpaperSnapshot.child("title").getValue(String.class);
                        String desc = wallpaperSnapshot.child("desc").getValue(String.class);
                        String url = wallpaperSnapshot.child("url").getValue(String.class);

                        Wallpaper w = new Wallpaper(id, title, desc, url, category);

                        if(isFavourites(w)){
                            w.isFavourites = true;
                        }
                        wallpaperList.add(w);
                    }
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseerror) {

            }
        });

    }

    private boolean isFavourites(Wallpaper w){
        for(Wallpaper f: favList){
            if(f.id.equals(w.id)){
                return true;
            }
        }
        return false;

    }
}