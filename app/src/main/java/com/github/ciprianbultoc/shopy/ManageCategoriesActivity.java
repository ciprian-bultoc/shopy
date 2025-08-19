package com.github.ciprianbultoc.shopy;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ciprianbultoc.shopy.adapter.CategoryAdapter;
import com.github.ciprianbultoc.shopy.db.AppDatabase;
import com.github.ciprianbultoc.shopy.entity.Category;

public class ManageCategoriesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_categories);

        db = AppDatabase.getInstance(this);

        recyclerView = findViewById(R.id.recyclerViewCategories);
        EditText editTextCategory = findViewById(R.id.editTextCategory);
        Button buttonAddCategory = findViewById(R.id.buttonAddCategory);


        adapter = new CategoryAdapter(
                db.categoryDao().getAllCategories(),
                this::editCategory,   // edit callback
                this::deleteCategory  // delete callback
        );

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CategoryAdapter(db.categoryDao().getAllCategories(), this::editCategory, this::deleteCategory);
        recyclerView.setAdapter(adapter);

        editTextCategory.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                buttonAddCategory.performClick(); // trigger the add button
                return true;
            }
            return false;
        });

        buttonAddCategory.setOnClickListener(v -> {
            String name = editTextCategory.getText().toString().trim();
            if (!name.isEmpty()) {
                Category c = new Category();
                c.name = name;
                db.categoryDao().insert(c);

                adapter.setCategories(db.categoryDao().getAllCategories());
                editTextCategory.setText("");
            }
        });
    }

    private void editCategory(Category category) {
        EditText input = new EditText(this);
        input.setText(category.name);

        new AlertDialog.Builder(this)
                .setTitle("Edit Category")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    category.name = input.getText().toString().trim();
                    db.categoryDao().update(category);
                    adapter.setCategories(db.categoryDao().getAllCategories());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteCategory(Category category) {
        new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    db.categoryDao().delete(category);
                    // Update adapter after deletion
                    if (adapter != null) {
                        adapter.setCategories(db.categoryDao().getAllCategories());
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}

