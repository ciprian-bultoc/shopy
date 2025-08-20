package com.github.ciprianbultoc.shopy;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ciprianbultoc.shopy.adapter.ItemAdapter;
import com.github.ciprianbultoc.shopy.db.AppDatabase;
import com.github.ciprianbultoc.shopy.dto.DisplayedRow;
import com.github.ciprianbultoc.shopy.entity.Item;
import com.github.ciprianbultoc.shopy.entity.ItemPreset;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppDatabase db;
    private AutoCompleteTextView autoCompleteItem;
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    // Define launcher
    private final ActivityResultLauncher<Intent> editItemLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    refreshList();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);

        autoCompleteItem = findViewById(R.id.autoCompleteItem);
        Button buttonAdd = findViewById(R.id.buttonAdd);
        recyclerView = findViewById(R.id.recyclerViewItems);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ItemAdapter(db.itemDao().getAllItems(), db, this::onItemClicked);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            private final Drawable deleteIcon = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_delete);
            private final Drawable editIcon = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_edit);
            private final ColorDrawable redBackground = new ColorDrawable(Color.RED);
            private final ColorDrawable blueBackground = new ColorDrawable(Color.BLUE);

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                if (viewHolder instanceof ItemAdapter.HeaderViewHolder) {
                    // ignore headers
                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                    return;
                }

                int pos = viewHolder.getAdapterPosition();
                DisplayedRow row = adapter.getRowByPosition(pos);

                if (direction == ItemTouchHelper.LEFT) {
                    // Delete
                    adapter.removeItem(row);
                } else if (direction == ItemTouchHelper.RIGHT) {
                    // Edit
                    adapter.notifyItemChanged(pos); // reset swipe state
                    Intent intent = new Intent(MainActivity.this, EditItemActivity.class);
                    intent.putExtra(EditItemActivity.EXTRA_ITEM_ID, row.getItem().id);
                    editItemLauncher.launch(intent);
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                View itemView = viewHolder.itemView;
                int iconMargin = (itemView.getHeight() - (deleteIcon != null ? deleteIcon.getIntrinsicHeight() : 0)) / 2;

                if (dX > 0) { // swipe right → edit
                    blueBackground.setBounds(itemView.getLeft(), itemView.getTop(),
                            itemView.getLeft() + ((int) dX), itemView.getBottom());
                    blueBackground.draw(c);

                    if (editIcon != null) {
                        int iconTop = itemView.getTop() + (itemView.getHeight() - editIcon.getIntrinsicHeight()) / 2;
                        int iconLeft = itemView.getLeft() + iconMargin;
                        int iconRight = iconLeft + editIcon.getIntrinsicWidth();
                        int iconBottom = iconTop + editIcon.getIntrinsicHeight();
                        editIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                        editIcon.draw(c);
                    }

                } else if (dX < 0) { // swipe left → delete
                    redBackground.setBounds(itemView.getRight() + ((int) dX), itemView.getTop(),
                            itemView.getRight(), itemView.getBottom());
                    redBackground.draw(c);

                    if (deleteIcon != null) {
                        int iconTop = itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                        int iconRight = itemView.getRight() - iconMargin;
                        int iconLeft = iconRight - deleteIcon.getIntrinsicWidth();
                        int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();
                        deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                        deleteIcon.draw(c);
                    }
                }
            }
        };

        new ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerView);

        setupAutoComplete();

        buttonAdd.setOnClickListener(v -> addItem());

        autoCompleteItem.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addItem();
                return true;
            }
            return false;
        });

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
    }

    private void setupAutoComplete() {
        autoCompleteItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().trim();
                if (!input.isEmpty()) {
                    // Search for presets
                    List<ItemPreset> presets = db.itemPresetDao().search(input);
                    List<String> names = new ArrayList<>();
                    for (ItemPreset preset : presets) names.add(preset.name);

                    ArrayAdapter<String> adapterAuto =
                            new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, names);
                    autoCompleteItem.setAdapter(adapterAuto);
                    autoCompleteItem.showDropDown();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        // When user selects a suggestion, add it directly
        autoCompleteItem.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = (String) parent.getItemAtPosition(position);

            // Find preset in DB
            List<ItemPreset> presets = db.itemPresetDao().search(selectedName);
            Integer categoryId = null;
            Integer marketId = null;
            if (!presets.isEmpty()) {
                categoryId = presets.get(0).categoryId;
                marketId = presets.get(0).marketId;
            }

            // Insert item into DB
            Item newItem = new Item(selectedName, categoryId, marketId, false);
            db.itemDao().insert(newItem);

            // Refresh RecyclerView
            adapter.setDisplayedRows(db.itemDao().getAllItems());

            // Clear input
            autoCompleteItem.setText("");
        });
    }


    private void addItem() {
        String name = autoCompleteItem.getText().toString().trim();
        if (name.isEmpty()) return;

        // Check if there is a preset
        List<ItemPreset> presets = db.itemPresetDao().search(name);
        Integer categoryId = null;
        Integer marketId = null;
        if (!presets.isEmpty()) {
            categoryId = presets.get(0).categoryId;
            marketId = presets.get(0).marketId;
        }

        Item newItem = new Item(name, categoryId, marketId, false);
        db.itemDao().insert(newItem);

        adapter.setDisplayedRows(db.itemDao().getAllItems());
        autoCompleteItem.setText("");
    }

    // Callback when clicking on an item in RecyclerView
    private void onItemClicked(Item item) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_manage_categories) {
            // Navigate to ManageCategoriesActivity
            Intent intent = new Intent(this, ManageCategoriesActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshList() {
        List<Item> items = db.itemDao().getAllItems();
        adapter.setDisplayedRows(items);
    }
}
