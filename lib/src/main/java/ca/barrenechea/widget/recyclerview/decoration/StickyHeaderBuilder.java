package ca.barrenechea.widget.recyclerview.decoration;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

public class StickyHeaderBuilder {

    StickyHeaderAdapter adapter;
    boolean sticky = true;
    boolean subSticky = true;
    boolean inline = false;

    public StickyHeaderBuilder(@NonNull StickyHeaderAdapter adapter) {
        this.adapter = adapter;
    }

    public StickyHeaderBuilder isSticky(boolean sticky) {
        this.sticky = sticky;
        return this;
    }

    public StickyHeaderBuilder isSubSticky(boolean subSticky) {
        this.subSticky = subSticky;
        return this;
    }

    public StickyHeaderBuilder renderInline(boolean inline) {
        this.inline = inline;
        return this;
    }

    public RecyclerView.ItemDecoration build() {
        if (adapter instanceof DoubleHeaderAdapter) {
            return new DoubleHeaderDecoration(this);
        } else {
            return new StickyHeaderDecoration(this);
        }
    }
}
