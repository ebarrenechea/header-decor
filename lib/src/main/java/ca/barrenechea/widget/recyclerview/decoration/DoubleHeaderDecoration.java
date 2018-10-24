/*
 * Copyright 2014 Eduardo Barrenechea
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.barrenechea.widget.recyclerview.decoration;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * A double sticky header decoration for android's RecyclerView.
 */
public class DoubleHeaderDecoration extends RecyclerView.ItemDecoration {

    private DoubleHeaderAdapter adapter;
    private Map<Long, RecyclerView.ViewHolder> subHeaderCache;
    private Map<Long, RecyclerView.ViewHolder> headerCache;
    private boolean renderInline;
    private boolean sticky;
    private boolean subSticky;

    public DoubleHeaderDecoration(@NonNull StickyHeaderBuilder builder) {
        this.subHeaderCache = new HashMap<>();
        this.headerCache = new HashMap<>();

        adapter = (DoubleHeaderAdapter) builder.adapter;
        renderInline = builder.inline;
        sticky = builder.sticky;
        subSticky = builder.subSticky;
    }

    /**
     * Clears both the header and subheader view cache. Headers and subheaders will be recreated and
     * rebound on list scroll after this method has been called.
     */
    public void clearDoubleHeaderCache() {
        clearSubHeaderCache();
        clearHeaderCache();
    }

    /**
     * Clears the subheader view cache. Subheaders will be recreated and
     * rebound on list scroll after this method has been called.
     */
    public void clearSubHeaderCache() {
        subHeaderCache.clear();
    }

    /**
     * Clears the header view cache. Headers will be recreated and
     * rebound on list scroll after this method has been called.
     */
    public void clearHeaderCache() {
        headerCache.clear();
    }

    @NonNull
    private RecyclerView.ViewHolder getSubHeader(@NonNull RecyclerView parent, int position) {
        final long key = adapter.getSubHeaderId(position);

        if (subHeaderCache.containsKey(key)) {
            return subHeaderCache.get(key);
        } else {
            final RecyclerView.ViewHolder holder = adapter.onCreateSubHeaderViewHolder(parent);
            final View header = holder.itemView;

            //noinspection unchecked
            adapter.onBindSubHeaderViewHolder(holder, position);
            measureView(parent, header);
            subHeaderCache.put(key, holder);

            return holder;
        }
    }

    @Nullable
    public View findHeaderViewUnder(float x, float y) {
        for (RecyclerView.ViewHolder holder : headerCache.values()) {
            final View child = holder.itemView;
            final float translationX = child.getTranslationX();
            final float translationY = child.getTranslationY();

            if (x >= child.getLeft() + translationX &&
                    x <= child.getRight() + translationX &&
                    y >= child.getTop() + translationY &&
                    y <= child.getBottom() + translationY) {
                return child;
            }
        }

        return null;
    }

    @Nullable
    public View findSubHeaderViewUnder(float x, float y) {
        for (RecyclerView.ViewHolder holder : subHeaderCache.values()) {
            final View child = holder.itemView;
            final float translationX = child.getTranslationX();
            final float translationY = child.getTranslationY();

            if (x >= child.getLeft() + translationX &&
                    x <= child.getRight() + translationX &&
                    y >= child.getTop() + translationY &&
                    y <= child.getBottom() + translationY) {
                return child;
            }
        }

        return null;
    }

    @NonNull
    private RecyclerView.ViewHolder getHeader(@NonNull RecyclerView parent, int position) {
        final long key = adapter.getHeaderId(position);

        if (headerCache.containsKey(key)) {
            return headerCache.get(key);
        } else {
            final RecyclerView.ViewHolder holder = adapter.onCreateHeaderViewHolder(parent);
            final View header = holder.itemView;

            //noinspection unchecked
            adapter.onBindHeaderViewHolder(holder, position);
            measureView(parent, header);
            headerCache.put(key, holder);

            return holder;
        }
    }

    private void measureView(@NonNull RecyclerView parent, @NonNull View header) {
        int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(), View.MeasureSpec.UNSPECIFIED);

        int childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
                parent.getPaddingLeft() + parent.getPaddingRight(), header.getLayoutParams().width);
        int childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
                parent.getPaddingTop() + parent.getPaddingBottom(), header.getLayoutParams().height);

        header.measure(childWidth, childHeight);
        header.layout(0, 0, header.getMeasuredWidth(), header.getMeasuredHeight());
    }

    private boolean hasSubHeader(int position) {
        if (position == 0) {
            return true;
        }

        int previous = position - 1;
        return adapter.getSubHeaderId(position) != adapter.getSubHeaderId(previous);
    }

    private boolean hasHeader(int position) {
        if (position == 0) {
            return true;
        }

        int previous = position - 1;
        return adapter.getHeaderId(position) != adapter.getHeaderId(previous);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);

        int headerHeight = 0;

        if (position != RecyclerView.NO_POSITION && hasSubHeader(position)) {
            if (hasHeader(position)) {
                View header = getHeader(parent, position).itemView;
                headerHeight += header.getHeight();
            }

            View header = getSubHeader(parent, position).itemView;
            headerHeight += getSubHeaderHeightForLayout(header);
        }

        outRect.set(0, headerHeight, 0, 0);
    }

    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        draw(canvas, parent, false);
    }

    @Override
    public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        draw(canvas, parent, true);
    }

    private void draw(@NonNull Canvas canvas, @NonNull RecyclerView parent, boolean over) {
        final int count = parent.getChildCount();
        long previousHeaderId = -1;
        long previousSubHeaderId = -1;

        for (int layoutPos = 0; layoutPos < count; layoutPos++) {
            final View child = parent.getChildAt(layoutPos);
            final int adapterPos = parent.getChildAdapterPosition(child);

            if (adapterPos != RecyclerView.NO_POSITION) {
                int left, top = 0;
                final long headerId = adapter.getHeaderId(adapterPos);
                final long subHeaderId = adapter.getSubHeaderId(adapterPos);

                final View header;
                final View subHeader;
                if (subHeaderId != previousSubHeaderId && (subSticky || hasSubHeader(adapterPos)) ||
                        headerId != previousHeaderId && (sticky || hasHeader(adapterPos))) {
                    header = getHeader(parent, adapterPos).itemView;
                    subHeader = getSubHeader(parent, adapterPos).itemView;
                } else {
                    continue;
                }

                if (subHeaderId != previousSubHeaderId && (subSticky || hasSubHeader(adapterPos))) {
                    previousSubHeaderId = subHeaderId;

                    if (subSticky && over || !subSticky && !over) {
                        canvas.save();
                        left = child.getLeft();
                        top = getSubHeaderTop(parent, child, header, subHeader, adapterPos, layoutPos);
                        canvas.translate(left, top);

                        subHeader.setTranslationX(left);
                        subHeader.setTranslationY(top);
                        subHeader.draw(canvas);
                        canvas.restore();
                    }
                }

                if ((headerId != previousHeaderId || subSticky && top < header.getHeight()) && (sticky || hasHeader(adapterPos))) {
                    previousHeaderId = headerId;

                    if (sticky && over || !sticky && !over) {
                        canvas.save();
                        left = child.getLeft();
                        top = getHeaderTop(parent, child, header, subHeader, adapterPos, layoutPos);
                        canvas.translate(left, top);

                        header.setTranslationX(left);
                        header.setTranslationY(top);
                        header.draw(canvas);
                        canvas.restore();
                    }
                }
            }
        }
    }

    private int getSubHeaderTop(@NonNull RecyclerView parent, @NonNull View child,
                                @NonNull View header, @NonNull View subHeader, int adapterPos, int layoutPos) {
        int top = getAnimatedTop(child) - getSubHeaderHeightForLayout(subHeader);
        if (subSticky) {
            if (isFirstValidChild(layoutPos, parent)) {
                final int count = parent.getChildCount();
                final long currentHeaderId = adapter.getHeaderId(adapterPos);
                final long currentSubHeaderId = adapter.getSubHeaderId(adapterPos);

                // find next view with sub-header and compute the offscreen push if needed
                for (int i = layoutPos + 1; i < count; i++) {
                    final View next = parent.getChildAt(i);
                    int adapterPosHere = parent.getChildAdapterPosition(next);
                    if (adapterPosHere != RecyclerView.NO_POSITION) {
                        final long nextHeaderId = adapter.getHeaderId(adapterPosHere);
                        final long nextSubHeaderId = adapter.getSubHeaderId(adapterPosHere);

                        if ((nextSubHeaderId != currentSubHeaderId)) {
                            int headersHeight = getSubHeaderHeightForLayout(subHeader) +
                                    getSubHeader(parent, adapterPosHere).itemView.getHeight();
                            if (nextHeaderId != currentHeaderId) {
                                headersHeight += getHeader(parent, adapterPosHere).itemView.getHeight();
                            }

                            final int offset = getAnimatedTop(next) - headersHeight;
                            if (offset < (sticky ? header.getHeight() : 0)) {
                                return offset;
                            } else {
                                break;
                            }
                        }
                    }
                }
            }

            top = Math.max(sticky ? header.getHeight() : 0, top);
        }

        return top;
    }

    private int getHeaderTop(@NonNull RecyclerView parent, @NonNull View child,
                             @NonNull View header, @NonNull View subHeader, int adapterPos, int layoutPos) {

        int top = getAnimatedTop(child) - header.getHeight() - getSubHeaderHeightForLayout(subHeader);
        if (isFirstValidChild(layoutPos, parent) && sticky) {
            final int count = parent.getChildCount();
            final long currentId = adapter.getHeaderId(adapterPos);

            // find next view with header and compute the offscreen push if needed
            for (int i = layoutPos + 1; i < count; i++) {
                View next = parent.getChildAt(i);
                int adapterPosHere = parent.getChildAdapterPosition(next);
                if (adapterPosHere != RecyclerView.NO_POSITION) {
                    long nextId = adapter.getHeaderId(adapterPosHere);
                    if (nextId != currentId) {
                        final int headersHeight = header.getHeight() +
                                getHeader(parent, adapterPosHere).itemView.getHeight();
                        final int offset = getAnimatedTop(next) - headersHeight -
                                getSubHeaderHeightForLayout(subHeader);

                        if (offset < 0) {
                            return offset;
                        } else {
                            break;
                        }
                    }
                }
            }

            top = Math.max(0, top);
        }

        return top;
    }

    private boolean isFirstValidChild(int layoutPos, @NonNull RecyclerView parent) {
        boolean isFirstValidChild = true;
        for (int otherLayoutPos = layoutPos - 2; otherLayoutPos >= 0; --otherLayoutPos) {
            final View otherChild = parent.getChildAt(otherLayoutPos);
            if (parent.getChildAdapterPosition(otherChild) != RecyclerView.NO_POSITION) {
                boolean visible = getAnimatedTop(otherChild) > -otherChild.getHeight();
                if (visible) {
                    isFirstValidChild = false;
                    break;
                }
            }
        }
        return isFirstValidChild;
    }

    private int getAnimatedTop(@NonNull View child) {
        return child.getTop() + (int) child.getTranslationY();
    }

    private int getSubHeaderHeightForLayout(@NonNull View header) {
        return renderInline ? 0 : header.getHeight();
    }
}