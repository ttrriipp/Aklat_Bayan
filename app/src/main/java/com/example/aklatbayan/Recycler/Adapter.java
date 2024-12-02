package com.example.aklatbayan.Recycler;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aklatbayan.R;
import com.example.aklatbayan.BookDetails;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<Model> titleList;
    private boolean isDownloadList = false;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public Adapter(Context context, ArrayList<Model> titleList, boolean isDownloadList) {
        this.context = context;
        this.titleList = titleList;
        this.isDownloadList = isDownloadList;
    }

    public void setFilteredList(List<Model> filteredList) {

        this.titleList = (ArrayList<Model>) filteredList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (titleList.get(position).getId().startsWith("header_")) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_history_header, parent, false);
            return new HeaderViewHolder(view);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Model model = titleList.get(position);
        
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).headerTitle.setText(model.getTitle());
        } else if (holder instanceof ViewHolder) {
            ViewHolder itemHolder = (ViewHolder) holder;
            if (model.getTitle() != null) {
                itemHolder.title.setText(model.getTitle());
            }
            if (model.getDesc() != null) {
                itemHolder.desc.setText(model.getDesc());
            }
            if (model.getCategory() != null) {
                itemHolder.category.setText(model.getCategory());
            }

            if (model.getThumbnailUrl() != null) {
                Glide.with(context)
                        .load(model.getThumbnailUrl())
                        .placeholder(R.drawable.no_cover_available)
                        .error(R.drawable.no_cover_available)
                        .into(itemHolder.homeThumbnail);
            }

            if (model.getId() != null) {
                FirebaseFirestore.getInstance()
                        .collection("reading_progress")
                        .document(model.getId())
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                float progress = documentSnapshot.getDouble("progress").floatValue();
                                itemHolder.readingProgress.setProgress(Math.round(progress));
                                itemHolder.readingProgress.setVisibility(View.VISIBLE);
                            } else {
                                itemHolder.readingProgress.setVisibility(View.GONE);
                            }
                        })
                        .addOnFailureListener(e -> itemHolder.readingProgress.setVisibility(View.GONE));

                itemHolder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(context, BookDetails.class);
                    intent.putExtra("txtTitle", model.getTitle());
                    intent.putExtra("author", model.getAuthor());
                    intent.putExtra("desc", model.getDesc());
                    intent.putExtra("category", model.getCategory());
                    intent.putExtra("pdfLink", model.getPdfLink());
                    intent.putExtra("downloadUrl", model.getDownloadUrl());
                    intent.putExtra("thumbnailUrl", model.getThumbnailUrl());
                    intent.putExtra("id", model.getId());
                    context.startActivity(intent);
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return titleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, desc, category;
        ImageView homeThumbnail;
        ProgressBar readingProgress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.txtTitle);
            desc = itemView.findViewById(R.id.txtDesc);
            category = itemView.findViewById(R.id.txtCategory);
            homeThumbnail = itemView.findViewById(R.id.homeThumbnail);
            readingProgress = itemView.findViewById(R.id.readingProgress);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerTitle;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setVisibility(View.VISIBLE);
            headerTitle = itemView.findViewById(R.id.headerTitle);
        }
    }
}
