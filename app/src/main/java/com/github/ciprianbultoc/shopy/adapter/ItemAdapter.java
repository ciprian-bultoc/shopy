package com.github.ciprianbultoc.shopy.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ciprianbultoc.shopy.R;
import com.github.ciprianbultoc.shopy.db.AppDatabase;
import com.github.ciprianbultoc.shopy.entity.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;

    private final Consumer<Item> clickCallback;
    private final AppDatabase db;
    private List<Item> uncheckedItems = new ArrayList<>();
    private List<Item> checkedItems = new ArrayList<>();
    private boolean checkedCollapsed = true;

    public ItemAdapter(List<Item> allItems, AppDatabase db, Consumer<Item> clickCallback) {
        this.clickCallback = clickCallback;
        this.db = db;
        setItems(allItems);
    }

    public void setItems(List<Item> allItems) {
        uncheckedItems.clear();
        checkedItems.clear();
        for (Item item : allItems) {
            if (item.checked) checkedItems.add(item);
            else uncheckedItems.add(item);
        }
        notifyDataSetChanged();
    }

    public boolean isCheckedCollapsed() {
        return checkedCollapsed;
    }

    public void toggleCheckedCollapse() {
        checkedCollapsed = !checkedCollapsed;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (!checkedItems.isEmpty() && position == uncheckedItems.size()) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        int count = uncheckedItems.size();
        if (!checkedItems.isEmpty()) {
            count += 1; // header
            if (!checkedCollapsed) count += checkedItems.size();
        }
        return count;
    }

    private Item getItemByPosition(int position) {
        if (!checkedItems.isEmpty() && position > uncheckedItems.size()) {
            return checkedItems.get(position - uncheckedItems.size() - 1);
        }
        return uncheckedItems.get(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_row, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) {
            ((HeaderViewHolder) holder).bind("Done", checkedCollapsed, v -> toggleCheckedCollapse());
        } else {
            Item item = getItemByPosition(position);
            ((ItemViewHolder) holder).bind(item, clickCallback, cb -> {
                item.checked = cb.isChecked();
                db.itemDao().update(item);
                // Defer update to avoid "RecyclerView computing layout" crash
                cb.post(() -> setItems(db.itemDao().getAllItems()));
            });
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameView;
        private final CheckBox checkBox;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.textViewItemName);
            checkBox = itemView.findViewById(R.id.checkBoxItem);
        }

        public void bind(Item item, Consumer<Item> clickCallback, Consumer<CheckBox> checkboxCallback) {
            nameView.setText(item.name);
            checkBox.setOnCheckedChangeListener(null); // reset listener
            checkBox.setChecked(item.checked);
            itemView.setOnClickListener(v -> clickCallback.accept(item));
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> checkboxCallback.accept(checkBox));
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView headerTitle;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            headerTitle = itemView.findViewById(R.id.textViewHeader);
        }

        public void bind(String title, boolean collapsed, View.OnClickListener toggleListener) {
            headerTitle.setText(title + " (" + (collapsed ? "▶" : "▼") + ")");
            itemView.setOnClickListener(toggleListener);
        }
    }
}