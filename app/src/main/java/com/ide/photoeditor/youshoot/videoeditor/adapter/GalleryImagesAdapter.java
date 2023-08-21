package com.ide.photoeditor.youshoot.videoeditor.adapter;

import android.content.Context;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ide.photoeditor.youshoot.R;
import com.ide.photoeditor.youshoot.videoeditor.activity.SlideShowImageActivity;
import com.ide.photoeditor.youshoot.videoeditor.model.ImageAlbum;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ide.photoeditor.youshoot.AdsUtils.Utils.Global.mSelectedImgPath;

public class GalleryImagesAdapter extends RecyclerView.Adapter<GalleryImagesAdapter.ViewHolder> {

    private final ArrayList<ImageAlbum> list;
    private final OnClickListener handler;
    Context mContext;

    public GalleryImagesAdapter(Context context, ArrayList<ImageAlbum> list, OnClickListener handler) {
        this.list = list;
        this.handler = handler;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_images, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            ImageAlbum imageAlbum = list.get(position);
            String path = imageAlbum.getmImage();
            Glide.with(mContext).load(new File(path)).into(holder.mImgPic);

            if (mSelectedImgPath.contains(path)) {
                int pos = mSelectedImgPath.indexOf(path) + 1;
                holder.mTxtSelect.setText("" + pos);
                holder.mTxtSelect.setVisibility(View.VISIBLE);
            } else {
                holder.mTxtSelect.setVisibility(View.GONE);
            }

            holder.mImgPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imageAlbum.ismSelect()) {
//                        holder.mTxtSelect.setText("" + SelectImageActivity.getInstance().mSelectNum);
//                        holder.mTxtSelect.setVisibility(View.GONE);
                        handler.OnRemove(imageAlbum.getmImage(), position);
                    } else {
//                        holder.mTxtSelect.setText("" + SelectImageActivity.getInstance().mSelectNum);
//                        holder.mTxtSelect.setVisibility(View.VISIBLE);
                        handler.OnClick(imageAlbum.getmImage(), position);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Long mLastClickTime = Long.valueOf(0);

    void checkClick() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.mImgPic)
        ImageView mImgPic;
        @BindView(R.id.mTxtSelect)
        TextView mTxtSelect;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnClickListener {
        void OnClick(String path, int pos);

        void OnRemove(String path, int pos);
    }
}
