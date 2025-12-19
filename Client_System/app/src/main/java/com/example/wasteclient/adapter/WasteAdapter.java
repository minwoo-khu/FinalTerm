package com.example.wasteclient.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wasteclient.R;
import com.example.wasteclient.model.WasteItem;

import java.util.List;

public class WasteAdapter extends RecyclerView.Adapter<WasteAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onClick(WasteItem item);
    }

    private final List<WasteItem> items;
    private final OnItemClickListener listener;

    public WasteAdapter(List<WasteItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_waste, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WasteItem item = items.get(position);

        holder.typeText.setText(item.waste_type);

        String imgUrl = item.image;
        if (imgUrl != null) {
            imgUrl = imgUrl.replace("http://127.0.0.1:8000", "http://10.0.2.2:8000");
        }

        Glide.with(holder.itemView.getContext())
                .load(imgUrl)
                .placeholder(android.R.drawable.ic_menu_report_image)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> listener.onClick(item));
    }

    @Override
    public int getItemCount() {
        return (items == null) ? 0 : items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView typeText;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.itemImage);
            typeText = itemView.findViewById(R.id.itemType);
        }
    }
}
