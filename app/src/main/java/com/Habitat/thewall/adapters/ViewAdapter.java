package com.Habitat.thewall.adapters;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.Habitat.thewall.BuildConfig;
import com.Habitat.thewall.R;
import com.Habitat.thewall.models.HomeView;
import com.Habitat.thewall.models.Wallpaper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.WallpaperViewHolder> {

    private Context mCtx;
    private List<HomeView> homeviewList;
    private WindowManager windowManager;
    private Activity mactivity;


    public ViewAdapter(Context mCtx, List<HomeView> homeviewList, WindowManager wm, Activity mactivity)  {
        this.mCtx = mCtx;
        this.homeviewList = homeviewList;
        this.windowManager = wm;
        this.mactivity = mactivity;

    }

    @NonNull
    @Override
    public WallpaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_home_view, parent, false);
        return new WallpaperViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull WallpaperViewHolder holder, int position) {
        HomeView w = homeviewList.get(position);

        Glide.with(mCtx)
                .load(w.url)
                .into(holder.imageView);

        if(w.isFavourites){
            holder.checkBoxFav.setChecked(true);
        }


    }


    @Override
    public int getItemCount() {
        return homeviewList.size();
    }

    class WallpaperViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

        TextView textView;
        ImageView imageView;

        CheckBox checkBoxFav;
        ImageButton buttonShare, buttonDownload, buttonsetwallpaper;




        public WallpaperViewHolder(View itemView) {
            super(itemView);


            imageView = itemView.findViewById(R.id.image_view);

            checkBoxFav  = itemView.findViewById(R.id.checkbox_favorite);
            buttonShare  = itemView.findViewById(R.id.button_share);
            buttonDownload  = itemView.findViewById(R.id.button_download);
            buttonsetwallpaper  = itemView.findViewById(R.id.button_setwallpaper);

            checkBoxFav.setOnCheckedChangeListener(this);
            buttonShare.setOnClickListener(this);
            buttonDownload.setOnClickListener(this);
            buttonsetwallpaper.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.button_share:

                    shareWallpaper(homeviewList.get(getAdapterPosition()));

                    break;

                case R.id.button_download:

                    downloadWallpaper(homeviewList.get(getAdapterPosition()));

                    break;

                case R.id.button_setwallpaper:

                    setWallpaper(homeviewList.get(getAdapterPosition()));

                    break;


            }

        }

        //set Wallpaper----------------------------------------------------------------------------------------------------------------

        private void setWallpaper(HomeView v){
            final WallpaperManager myWallpaperManager = WallpaperManager.getInstance(mCtx);

            Display display = windowManager.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            HomeView w = homeviewList.get(getAdapterPosition());
            Glide.with(mCtx)
                    .asBitmap()
                    .load(w.url)
                    .into(new CustomTarget<Bitmap>(width,height) {
                              @Override
                              public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {


                                  try {
                                      myWallpaperManager.setBitmap(resource);
                                      Toast.makeText(mactivity, "Wallpaper Set Successfully", Toast.LENGTH_LONG).show();

                                  } catch (IOException e) {
                                      e.printStackTrace();
                                  }

                              }

                              @Override
                              public void onLoadCleared(@Nullable Drawable placeholder) {

                              }
                          }
                    );


        }



        //wallpaper sharing----------------------------------------------------------------------------------------------------------------


        private void shareWallpaper(HomeView w) {


            Glide.with(mCtx)
                    .asBitmap()
                    .load(w.url)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {


                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("image/*");
                            intent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(resource));

                            mCtx.startActivity(Intent.createChooser(intent, "The Wallpapers"));

                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
        }

        private Uri getLocalBitmapUri(Bitmap bmp) {
            Uri bmpUri = null;
            try {
                File file = new File(mCtx.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "The_Wallpapers_" + System.currentTimeMillis() + ".png");
                FileOutputStream out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.close();
                bmpUri = FileProvider.getUriForFile(mCtx, BuildConfig.APPLICATION_ID + ".provider", file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmpUri;
        }

        //wallpaper Downloading--------------------------------------------------------------------------------------------------------------------


        private void downloadWallpaper(final HomeView wallpaper) {


            Glide.with(mCtx)
                    .asBitmap()
                    .load(wallpaper.url)
                    .into(new CustomTarget<Bitmap>() {
                              @Override
                              public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                  Intent intent = new Intent(Intent.ACTION_VIEW);
                                  Uri uri = saveWallpaperAndGetUri(resource, wallpaper.title);

                                  if (uri != null) {
                                      intent.setDataAndType(uri, "image/*");
                                      Toast.makeText(mCtx, "Image is saved to storage/TheWallpapers", Toast.LENGTH_SHORT).show();

                                  }
                              }

                              @Override
                              public void onLoadCleared(@Nullable Drawable placeholder) {

                              }
                          }
                    );
        }


        private Uri saveWallpaperAndGetUri(Bitmap bitmap, String title) {
            if (ContextCompat.checkSelfPermission(mCtx, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat
                        .shouldShowRequestPermissionRationale((Activity) mCtx, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

                    Uri uri = Uri.fromParts("package", mCtx.getPackageName(), null);
                    intent.setData(uri);

                    mCtx.startActivity(intent);

                } else {
                    ActivityCompat.requestPermissions((Activity) mCtx, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                }
                return null;
            }

            File folder = new File(Environment.getExternalStorageDirectory().toString() + "/" + "TheWallpapers");

            folder.mkdirs();


            File file = new File(folder, title + ".jpeg");
            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

                return FileProvider.getUriForFile(mCtx, BuildConfig.APPLICATION_ID + ".provider", file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }



        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            if(FirebaseAuth.getInstance().getCurrentUser() == null){
                Toast.makeText(mCtx, "Please Login First !!", Toast.LENGTH_LONG).show();
                compoundButton.setChecked(false);
                return;
            }





            int position = getAdapterPosition();
            HomeView w = homeviewList.get(position);

            DatabaseReference dbFavs = FirebaseDatabase.getInstance().getReference("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("favourites")
                    .child(w.homeview);

            if(b){
                dbFavs.child(w.id).setValue(w);

            }else{
                dbFavs.child(w.id).setValue(null);
            }

        }
    }
}
