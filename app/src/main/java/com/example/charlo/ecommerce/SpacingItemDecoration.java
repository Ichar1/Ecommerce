package com.example.charlo.ecommerce;

import android.support.v7.widget.RecyclerView;

public class SpacingItemDecoration extends RecyclerView.ItemDecoration {
    private int spanCount;
    private int spacingPx;
    private boolean includeEdge;

    public SpacingItemDecoration(int spanCount, int spacingPx, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacingPx = spacingPx;
        this.includeEdge = includeEdge;
    }
}
