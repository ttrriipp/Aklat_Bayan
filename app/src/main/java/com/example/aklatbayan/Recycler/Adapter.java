package com.example.aklatbayan.Recycler;

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
import com.example.aklatbayan.R;
import com.example.aklatbayan.BookDetails;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    Context context;
    ArrayList<Model> titleList;

    public Adapter(Context context, ArrayList<Model> titleList) {
        this.context = context;
        this.titleList = titleList;
    }

    public void setFilteredList(List<Model> filteredList) {

        this.titleList = (ArrayList<Model>) filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {

        Model model = titleList.get(position);
        holder.title.setText(model.getTitle());
        holder.desc.setText(model.getDesc());
        holder.category.setText(model.getCategory());

        Glide.with(context)
                .load(model.getThumbnailUrl())
                .placeholder(R.drawable.no_cover_available)
                .error(R.drawable.no_cover_available)
                .into(holder.homeThumbnail);

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context, BookDetails.class);
            intent.putExtra("txtTitle",model.getTitle());
            intent.putExtra("author",model.getAuthor());
            intent.putExtra("desc",model.getDesc());
            intent.putExtra("category",model.getCategory());
            intent.putExtra("pdfLink",model.getPdfLink());
            intent.putExtra("downloadUrl",model.getDownloadUrl());
            intent.putExtra("thumbnailUrl", model.getThumbnailUrl());

            context.startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return titleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, desc, category;
        ImageView homeThumbnail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.txtTitle);
            desc = itemView.findViewById(R.id.txtDesc);
            category = itemView.findViewById(R.id.txtCategory);
            homeThumbnail = itemView.findViewById(R.id.homeThumbnail);
        }
    }
}
