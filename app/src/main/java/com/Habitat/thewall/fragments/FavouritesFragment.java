package com.Habitat.thewall.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Habitat.thewall.R;
import com.Habitat.thewall.adapters.WallpapersAdapter;
import com.Habitat.thewall.models.Wallpaper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavouritesFragment extends Fragment {

    List<Wallpaper> favWalls;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    WallpapersAdapter adapter;

    DatabaseReference dbFavs;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favourites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        favWalls = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.spin_kit);



        adapter = new WallpapersAdapter(getActivity(), favWalls, getActivity().getWindowManager(), getActivity());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setAdapter(adapter);


        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_area, new SettingsFragment())
                    .commit();
            return;
        }

        dbFavs = FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("favourites");

        progressBar.setVisibility(View.VISIBLE);

        dbFavs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                progressBar.setVisibility(View.GONE);

                for (DataSnapshot category : dataSnapshot.getChildren()) {

                    for (DataSnapshot wallpaperSnapshot : category.getChildren()) {


                        String id = wallpaperSnapshot.getKey();
                        String title = wallpaperSnapshot.child("title").getValue(String.class);
                        String desc = wallpaperSnapshot.child("desc").getValue(String.class);
                        String url = wallpaperSnapshot.child("url").getValue(String.class);

                        Wallpaper w = new Wallpaper(id, title, desc, url, category.getKey());
                        w.isFavourites = true;

                        favWalls.add(w);

                    }

                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
