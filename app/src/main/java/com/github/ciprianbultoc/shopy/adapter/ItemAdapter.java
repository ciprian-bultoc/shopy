package com.github.ciprianbultoc.shopy.adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ciprianbultoc.shopy.R;
import com.github.ciprianbultoc.shopy.constants.Constants;
import com.github.ciprianbultoc.shopy.db.AppDatabase;
import com.github.ciprianbultoc.shopy.dto.DisplayedRow;
import com.github.ciprianbultoc.shopy.dto.DisplayedRows;
import com.github.ciprianbultoc.shopy.entity.Category;
import com.github.ciprianbultoc.shopy.entity.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final AppDatabase db;
    private final OnItemClick listener;

    private DisplayedRows rows;

    public ItemAdapter(List<Item> items, AppDatabase db, OnItemClick listener) {
        this.db = db;
        this.listener = listener;
        this.rows = new DisplayedRows();
        setDisplayedRows(items);
    }

    public void setDisplayedRows(List<Item> items) {
        rows.getAllRows().clear();
        rows.getUnchecked().clear();
        rows.getChecked().clear();

        // Split items
        items.forEach(item -> {
            DisplayedRow row = new DisplayedRow(item);
            if (item.checked) {
                rows.getChecked().add(row);
            } else {
                rows.getUnchecked().add(row);
            }
        });

        // Group unchecked items by category
        Map<Integer, List<DisplayedRow>> grouped = new HashMap<>();
        for (DisplayedRow row : rows.getUnchecked()) {
            grouped.computeIfAbsent(row.getItem().categoryId, k -> new ArrayList<>()).add(row);
        }

        List<Category> categories = db.categoryDao().getAllCategories();

        // Add category headers + items
        for (Map.Entry<Integer, List<DisplayedRow>> entry : grouped.entrySet()) {
            String categoryName = getCategoryName(entry.getKey(), categories);
            rows.getAllRows().add(new DisplayedRow(categoryName, Constants.DisplayedRowType.HEADER_ITEM_CATEGORY));
            rows.getAllRows().addAll(entry.getValue());
        }

        // Add checked section if any
        if (!rows.getChecked().isEmpty()) {
            rows.getAllRows().add(new DisplayedRow("Checked", Constants.DisplayedRowType.HEADER_CHECKED_ITEM));
            if (!rows.isCollapsedChecked()) {
                rows.getAllRows().addAll(rows.getChecked());
            }
        }

        notifyDataSetChanged();
    }

    private String getCategoryName(Integer categoryId, List<Category> categories) {
        if (categoryId == null) {
            return "None";
        }
        Category category = categories.stream().filter(cat -> cat.id == categoryId).findFirst().orElse(null);
        return category != null ? category.name : "None";
    }

    @Override
    public int getItemViewType(int position) {
        return this.rows.getAllRows().get(position).getType().getItemViewType();
    }

    @Override
    public int getItemCount() {
        return rows.getAllRows().size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Constants.DisplayedRowType.HEADER_ITEM_CATEGORY.getItemViewType() || viewType == Constants.DisplayedRowType.HEADER_CHECKED_ITEM.getItemViewType()) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.checked_item_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_row, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == Constants.DisplayedRowType.HEADER_ITEM_CATEGORY.getItemViewType()) {
            ((HeaderViewHolder) holder).bind(rows.getAllRows().get(position).getText());
        } else if (viewType == Constants.DisplayedRowType.HEADER_CHECKED_ITEM.getItemViewType()) {
            ((HeaderViewHolder) holder).bind("Checked", rows.isCollapsedChecked(), v -> {
                rows.setCollapsedChecked(!rows.isCollapsedChecked());
                setDisplayedRows(db.itemDao().getAllItems());
            });
        } else {
            ((ItemViewHolder) holder).bind(rows.getAllRows().get(position));
        }
    }

    public DisplayedRow getRowByPosition(int position) {
        return rows.getAllRows().get(position);
    }

    public void removeItem(DisplayedRow row) {
        if (Boolean.TRUE.equals(row.getItem().checked)) {
            rows.getChecked().remove(row);
        } else {
            rows.getUnchecked().remove(row);
        }

        db.itemDao().delete(row.getItem());

        notifyDataSetChanged();
    }

    public interface OnItemClick {
        void onItemClick(Item item);
    }

    // ViewHolders
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerText;

        HeaderViewHolder(View itemView) {
            super(itemView);
            headerText = itemView.findViewById(R.id.textViewHeader);
        }

        void bind(String title) {
            headerText.setText(title);
        }

        void bind(String title, boolean checkedCollapsed, View.OnClickListener toggleListener) {
            if (title.equals("Checked")) {
                // Show ▶ when collapsed, ▼ when expanded
                headerText.setText(title + (checkedCollapsed ? " ▶" : " ▼"));
            } else {
                headerText.setText(title);
            }
            itemView.setOnClickListener(toggleListener);
        }

    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView nameView;

        ItemViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.textViewItemName);

            // Click row: toggle checked AND trigger listener
            itemView.setOnClickListener(v -> {

                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    Item item = rows.getAllRows().get(pos).getItem();
                    item.checked = !item.checked;
                    db.itemDao().update(item);
                    setDisplayedRows(db.itemDao().getAllItems());

                    // Call MainActivity callback
                    if (listener != null) listener.onItemClick(item);
                }
            });
        }

        void bind(DisplayedRow row) {
            nameView.setText(row.getText());
            if (row.getItem().checked) {
                nameView.setTypeface(null, Typeface.ITALIC);
                nameView.setTextColor(Color.GRAY);
            } else {
                nameView.setTypeface(null, Typeface.NORMAL);
                nameView.setTextColor(Color.WHITE);
            }
        }
    }
}
