package com.github.ciprianbultoc.shopy.constants;

public class Constants {

    public enum DisplayedRowType {
        NONE(-1),
        HEADER_ITEM_CATEGORY(0),
        SHOPPING_ITEM(1),
        HEADER_CHECKED_ITEM(2);

        private final int itemViewType;

        DisplayedRowType(int itemViewType) {
            this.itemViewType = itemViewType;
        }

        public int getItemViewType() {
            return itemViewType;
        }
    }

}
