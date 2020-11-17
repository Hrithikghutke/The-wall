package com.Habitat.thewall.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.Habitat.thewall.R;
import com.Habitat.thewall.activities.ViewActivity;
import com.Habitat.thewall.activities.WallpapersActivity;
import com.Habitat.thewall.models.Category;
import com.Habitat.thewall.models.Home;

import java.util.List;

public class HomeAdapters extends RecyclerView.Adapter<HomeAdapters.CategoryViewHolder> {

    private Context mCtx;
    private List<Home> homeList;

    public HomeAdapters(Context mCtx, List<Home> homeList) {
        this.mCtx = mCtx;
        this.homeList = homeList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_home, parent, false);
        return new CategoryViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Home c = homeList.get(position);
        
        Glide.with(mCtx)
                .load(c.thumb)
                .into(holder.imageView);


    }

    @Override
    public int getItemCount() {
        return homeList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textView;
        ImageView imageView;

        public CategoryViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.text_view_cat_name);
            imageView = itemView.findViewById(R.id.image_view);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int p = getAdapterPosition();
            Home c = homeList.get(p);

            Intent intent = new Intent(mCtx, ViewActivity.class);
            intent.putExtra("Home", (CharSequence) c.name);

            mCtx.startActivity(intent);
        }
    }
}
