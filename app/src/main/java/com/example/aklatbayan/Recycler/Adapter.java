package com.example.aklatbayan.Recycler;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aklatbayan.R;
import com.example.aklatbayan.book_details;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    Context context;
    ArrayList<Model> titleList;

    public Adapter(Context context, ArrayList<Model> titleList) {
        this.context = context;
        this.titleList = titleList;
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.new_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {

        Model model = titleList.get(position);
        holder.title.setText(model.getTitle());

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context, book_details.class);
            intent.putExtra("txtTitle",model.getTitle());
            intent.putExtra("author",model.getAuthor());
            intent.putExtra("desc",model.getDesc());
            intent.putExtra("category",model.getCategory());
            intent.putExtra("pdfLink",model.getPdfLink());
            intent.putExtra("downloadUrl",model.getDownloadUrl());

            context.startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return titleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.txtTitle);
        }
    }
}
