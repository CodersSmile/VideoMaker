package com.ide.photoeditor.youshoot.videoeditor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ide.photoeditor.youshoot.R;

import java.util.ArrayList;
import java.util.List;

public class VideoToolAdapter extends RecyclerView.Adapter<VideoToolAdapter.ViewHolder> {

    public OnTItemSelected onTItemSelected;
    public List<ToolModel> toolLists = new ArrayList<>();
    public int selectedSquareIndex = 0;
    Context context;

    public interface OnTItemSelected {
        void onToolSelected(int id);
    }

    public VideoToolAdapter(Context con, OnTItemSelected onTItemSelected) {
        context = con;
        this.onTItemSelected = onTItemSelected;
        this.toolLists.add(new ToolModel(R.string.cut, R.drawable.ic_cutting, 0));
        this.toolLists.add(new ToolModel(R.string.crop, R.drawable.ic_video_cropper, 1));
        this.toolLists.add(new ToolModel(R.string.compress, R.drawable.ic_video_compress, 2));
        this.toolLists.add(new ToolModel(R.string.rotate, R.drawable.ic_video_rotate, 3));
        this.toolLists.add(new ToolModel(R.string.mirror, R.drawable.ic_video_mirror, 4));
        this.toolLists.add(new ToolModel(R.string.reverse, R.drawable.ic_video_reverse, 5));
        this.toolLists.add(new ToolModel(R.string.toaudio, R.drawable.ic_video_to_mp3, 6));
        this.toolLists.add(new ToolModel(R.string.togif, R.drawable.ic_video_to_gif, 7));
        this.toolLists.add(new ToolModel(R.string.toimg, R.drawable.ic_video_to_image, 10));
        this.toolLists.add(new ToolModel(R.string.slow, R.drawable.ic_slow_motion, 8));
        this.toolLists.add(new ToolModel(R.string.fast, R.drawable.ic_fast_motion, 9));
    }

    class ToolModel {
        public int toolIcon;
        public int toolName;
        public int id;

        ToolModel(int str, int i, int id) {
            this.toolName = str;
            this.toolIcon = i;
            this.id = id;
        }
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_editing, viewGroup, false));
    }

    public void reset() {
        this.selectedSquareIndex = 0;
        notifyDataSetChanged();
    }

    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ToolModel toolModel = this.toolLists.get(i);
        viewHolder.text_view_tool_name.setText(toolModel.toolName);
        viewHolder.image_view_tool_icon.setImageDrawable(ContextCompat.getDrawable(context, toolModel.toolIcon));

    }

    public int getItemCount() {
        return this.toolLists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image_view_tool_icon;
        TextView text_view_tool_name;
        RelativeLayout relative_layout_wrapper_tool;

        ViewHolder(View view) {
            super(view);
            this.image_view_tool_icon = view.findViewById(R.id.image_view_tool_icon);
            this.text_view_tool_name = view.findViewById(R.id.text_view_tool_name);
            this.relative_layout_wrapper_tool = view.findViewById(R.id.relative_layout_wrapper_tool);
            relative_layout_wrapper_tool.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    VideoToolAdapter.this.selectedSquareIndex = ViewHolder.this.getLayoutPosition();
                    VideoToolAdapter.this.onTItemSelected.onToolSelected((VideoToolAdapter.this.toolLists.get(VideoToolAdapter.this.selectedSquareIndex)).id);
                    notifyDataSetChanged();
                }
            });

        }
    }
}
