package com.github.ciprianbultoc.shopy.dto;

import com.github.ciprianbultoc.shopy.constants.Constants.DisplayedRowType;
import com.github.ciprianbultoc.shopy.entity.Item;

public class DisplayedRow {

    private DisplayedRowType type;

    private Item item;

    private String text;


    public DisplayedRow() {
        this.type = DisplayedRowType.NONE;
        this.item = null;
        this.text = "";
    }

    public DisplayedRow(Item item) {
        this.item = item;
        this.type = DisplayedRowType.SHOPPING_ITEM;
        this.text = item.name;
    }

    public DisplayedRow(String text, DisplayedRowType type) {
        this.type = type;
        this.text = text;
        this.item = null;
    }

    public DisplayedRowType getType() {
        return type;
    }

    public void setType(DisplayedRowType type) {
        this.type = type;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
