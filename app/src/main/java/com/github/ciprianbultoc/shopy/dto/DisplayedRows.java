package com.github.ciprianbultoc.shopy.dto;

import java.util.ArrayList;
import java.util.List;

public class DisplayedRows {

    private List<DisplayedRow> allRows;
    private List<DisplayedRow> checked;
    private List<DisplayedRow> unchecked;

    private boolean collapsedChecked;

    public DisplayedRows() {
        this.allRows = new ArrayList<>();
        this.checked = new ArrayList<>();
        this.unchecked = new ArrayList<>();
        this.collapsedChecked = false;
    }

    public List<DisplayedRow> getAllRows() {
        return allRows;
    }

    public void setAllRows(List<DisplayedRow> allRows) {
        this.allRows = allRows;
    }

    public List<DisplayedRow> getChecked() {
        return checked;
    }

    public void setChecked(List<DisplayedRow> checked) {
        this.checked = checked;
    }

    public List<DisplayedRow> getUnchecked() {
        return unchecked;
    }

    public void setUnchecked(List<DisplayedRow> unchecked) {
        this.unchecked = unchecked;
    }

    public boolean isCollapsedChecked() {
        return collapsedChecked;
    }

    public void setCollapsedChecked(boolean collapsedChecked) {
        this.collapsedChecked = collapsedChecked;
    }
}
