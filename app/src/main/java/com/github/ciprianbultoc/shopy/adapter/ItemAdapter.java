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
import com.github.ciprianbultoc.shopy.db.AppDatabase;
import com.github.ciprianbultoc.shopy.entity.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_CATEGORY_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_CHECKED_HEADER = 2;

    private final AppDatabase db;
    private final List<Object> displayList = new ArrayList<>();
    private final OnItemClick listener;

    private boolean checkedCollapsed = true;

    private List<Item> uncheckedItems = new ArrayList<>();
    private List<Item> checkedItems = new ArrayList<>();

    public ItemAdapter(List<Item> items, AppDatabase db, OnItemClick listener) {
        this.db = db;
        this.listener = listener;
        setItems(items);
    }

    public void setItems(List<Item> items) {
        displayList.clear();

        // Split into checked and unchecked
        uncheckedItems = new ArrayList<>();
        checkedItems = new ArrayList<>();
        for (Item item : items) {
            if (item.checked) checkedItems.add(item);
            else uncheckedItems.add(item);
        }

        // Group unchecked items by category
        Map<Integer, List<Item>> grouped = new HashMap<>();
        for (Item item : uncheckedItems) {
            grouped.computeIfAbsent(item.categoryId, k -> new ArrayList<>()).add(item);
        }

        // Add category headers + items
        for (Map.Entry<Integer, List<Item>> entry : grouped.entrySet()) {
            String categoryName = getCategoryName(entry.getKey());
            displayList.add(categoryName); // header
            displayList.addAll(entry.getValue());
        }

        // Add checked section if any
        if (!checkedItems.isEmpty()) {
            displayList.add(TYPE_CHECKED_HEADER); // marker
            if (!checkedCollapsed) {
                displayList.addAll(checkedItems);
            }
        }

        notifyDataSetChanged();
    }

    private String getCategoryName(Integer categoryId) {
        if (categoryId == null) return "None";
        String name = db.categoryDao().getById(categoryId).name;
        return name != null ? name : "None";
    }

    @Override
    public int getItemViewType(int position) {
        Object obj = displayList.get(position);
        if (obj instanceof String) return TYPE_CATEGORY_HEADER;
        else if (obj instanceof Integer && obj.equals(TYPE_CHECKED_HEADER))
            return TYPE_CHECKED_HEADER;
        else return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_CATEGORY_HEADER || viewType == TYPE_CHECKED_HEADER) {
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
        if (viewType == TYPE_CATEGORY_HEADER) {
            ((HeaderViewHolder) holder).bind((String) displayList.get(position));
        } else if (viewType == TYPE_CHECKED_HEADER) {
            ((HeaderViewHolder) holder).bind("Checked", checkedCollapsed, v -> {
                checkedCollapsed = !checkedCollapsed;
                setItems(db.itemDao().getAllItems());
            });
        } else {
            ((ItemViewHolder) holder).bind((Item) displayList.get(position));
        }
    }

    public Item getItemByPosition(int position) {
        if (position < uncheckedItems.size()) {
            return uncheckedItems.get(position);
        } else if (!checkedItems.isEmpty()) {
            int checkedIndex = position - uncheckedItems.size() - 1; // -1 if header is counted
            if (checkedIndex >= 0 && checkedIndex < checkedItems.size()) {
                return checkedItems.get(checkedIndex);
            }
        }
        return null;
    }

    public void removeItem(Item item) {
        // Remove from uncheckedItems or checkedItems depending on checked state
        if (item.checked) {
            checkedItems.remove(item);
        } else {
            uncheckedItems.remove(item);
        }

        // Delete from DB
        db.itemDao().delete(item);

        notifyDataSetChanged();
    }

    public interface OnItemClick {
        void onItemClick(Item item);
    }

    // ViewHolders
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
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
                    Item item = (Item) displayList.get(pos);
                    item.checked = !item.checked;
                    db.itemDao().update(item);
                    setItems(db.itemDao().getAllItems());

                    // Call MainActivity callback
                    if (listener != null) listener.onItemClick(item);
                }
            });
        }

        void bind(Item item) {
            nameView.setText(item.name);
            if (item.checked) {
                nameView.setTypeface(null, Typeface.ITALIC);
                nameView.setTextColor(Color.GRAY);
            } else {
                nameView.setTypeface(null, Typeface.NORMAL);
                nameView.setTextColor(Color.WHITE);
            }
        }
    }
}
