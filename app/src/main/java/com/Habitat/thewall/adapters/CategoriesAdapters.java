package com.Habitat.thewall.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.Habitat.thewall.R;
import com.Habitat.thewall.activities.WallpapersActivity;
import com.Habitat.thewall.models.Category;

import java.io.ObjectInputStream;
import java.util.List;

import javax.xml.namespace.QName;

public class CategoriesAdapters extends RecyclerView.Adapter<CategoriesAdapters.CategoryViewHolder> {

    private Context mCtx;
    private List<Category> categoryList;

    public CategoriesAdapters(Context mCtx, List<Category> categoryList) {
        this.mCtx = mCtx;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_categories, parent, false);
        return new CategoryViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category c = categoryList.get(position);
        holder.textView.setText((CharSequence) c.name);
        Glide.with(mCtx)
                .load(c.thumb)
                .into(holder.imageView);


    }

    @Override
    public int getItemCount() {
        return categoryList.size();
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
            Category c = categoryList.get(p);

            Intent intent = new Intent(mCtx, WallpapersActivity.class);
            intent.putExtra("category", (CharSequence) c.name);

            mCtx.startActivity(intent);
        }
    }
}
