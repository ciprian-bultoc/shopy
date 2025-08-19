package com.github.ciprianbultoc.shopy;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.github.ciprianbultoc.shopy.db.AppDatabase;
import com.github.ciprianbultoc.shopy.entity.Category;
import com.github.ciprianbultoc.shopy.entity.Item;
import com.github.ciprianbultoc.shopy.entity.ItemPreset;

import java.util.ArrayList;
import java.util.List;

public class EditItemActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "item_id";

    private AppDatabase db;
    private Item item;
    private AutoCompleteTextView autoCompleteCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        db = AppDatabase.getInstance(this);

        autoCompleteCategory = findViewById(R.id.autoCompleteCategory);
        Button buttonSave = findViewById(R.id.buttonSave);

        int itemId = getIntent().getIntExtra(EXTRA_ITEM_ID, -1);
        if (itemId == -1) finish(); // nothing to edit

        item = db.itemDao().getById(itemId);
        if (item == null) finish();

        setupCategoryAutocomplete();

        buttonSave.setOnClickListener(v -> {
            // find categoryId by name
            String categoryName = autoCompleteCategory.getText().toString();
            Category category = db.categoryDao().getByName(categoryName);
            if (category != null) {
                item.categoryId = category.id;
                db.itemDao().update(item);

                ItemPreset preset = new ItemPreset(item.name, category.id, item.marketId);
                db.itemPresetDao().insert(preset);
            }
            finish();
        });
    }

    private void setupCategoryAutocomplete() {
        List<Category> categories = db.categoryDao().getAllCategories();
        List<String> categoryNames = new ArrayList<>();
        for (Category c : categories) categoryNames.add(c.name);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                categoryNames
        );
        autoCompleteCategory.setAdapter(adapter);

        autoCompleteCategory.setOnClickListener(v -> autoCompleteCategory.showDropDown());

        // preselect current category
        if (item.categoryId != null) {
            Category current = db.categoryDao().getById(item.categoryId);
            if (current != null) autoCompleteCategory.setText(current.name, false);
        }
    }
}