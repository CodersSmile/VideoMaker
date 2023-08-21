package com.ide.photoeditor.youshoot.videoeditor.adapter;

import android.content.Context;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ide.photoeditor.youshoot.R;
import com.ide.photoeditor.youshoot.videoeditor.model.GalleryAlbum;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GalleryAlbumAdapter extends RecyclerView.Adapter<GalleryAlbumAdapter.ViewHolder> {

    private final ArrayList<GalleryAlbum> list;
    private final OnAlbumClickListener handler;
    Context mContext;

    public GalleryAlbumAdapter(Context context, ArrayList<GalleryAlbum> list, OnAlbumClickListener handler) {
        this.list = list;
        this.handler = handler;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_albums, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            Glide.with(mContext).load(new File(list.get(position).mImageList.get(0).getmImage())).into(holder.mImgTop);
            holder.mTxtTotal.setText("" + list.get(position).mImageList.size());
            holder.mTxtAlbumName.setText("" + list.get(position).mAlbumName);
            holder.mLLMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handler.Onclick(list.get(position),position);
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
        @BindView(R.id.mImgTop)
        ImageView mImgTop;
        @BindView(R.id.mTxtAlbumName)
        TextView mTxtAlbumName;
        @BindView(R.id.mTxtTotal)
        TextView mTxtTotal;
        @BindView(R.id.mLLMain)
        LinearLayout mLLMain;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnAlbumClickListener {
        void Onclick(GalleryAlbum album,int pos);
    }
}
