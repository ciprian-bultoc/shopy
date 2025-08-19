package com.github.ciprianbultoc.shopy.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ciprianbultoc.shopy.entity.Category;

import java.util.List;
import java.util.function.Consumer;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private final Consumer<Category> editCallback;
    private final Consumer<Category> deleteCallback;
    private List<Category> categories;

    public CategoryAdapter(List<Category> categories, Consumer<Category> editCallback, Consumer<Category> deleteCallback) {
        this.categories = categories;
        this.editCallback = editCallback;
        this.deleteCallback = deleteCallback;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.text1.setText(category.name);

        holder.itemView.setOnClickListener(v -> editCallback.accept(category));
        holder.itemView.setOnLongClickListener(v -> {
            deleteCallback.accept(category);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text1;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
        }
    }
}
