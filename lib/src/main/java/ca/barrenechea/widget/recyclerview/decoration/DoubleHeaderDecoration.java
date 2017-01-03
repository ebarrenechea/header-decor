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
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * A double sticky header decoration for android's RecyclerView.
 */
public class DoubleHeaderDecoration extends RecyclerView.ItemDecoration {
    public static final long NO_SUB_HEADER_ID = -1L;
    public static final long NO_HEADER_ID = -1L;

    private DoubleHeaderAdapter mAdapter;
    private Map<Long, RecyclerView.ViewHolder> mSubHeaderCache;
    private Map<Long, RecyclerView.ViewHolder> mHeaderCache;
    private boolean mRenderSubHeaderInline;
    private boolean mRenderHeaderInline;

    /**
     * @param adapter the double header adapter to use
     */
    public DoubleHeaderDecoration(DoubleHeaderAdapter adapter) {
        this(adapter, false, false);
    }

    /**
     * @param adapter the double header adapter to use
     */
    public DoubleHeaderDecoration(DoubleHeaderAdapter adapter, boolean renderSubHeaderInline) {
        this(adapter, false, renderSubHeaderInline);
    }

    /**
     * @param adapter the double header adapter to use
     */
    public DoubleHeaderDecoration(DoubleHeaderAdapter adapter, boolean renderHeaderInline, boolean renderSubHeaderInline) {
        mAdapter = adapter;

        mSubHeaderCache = new HashMap<>();
        mHeaderCache = new HashMap<>();
        mRenderHeaderInline = renderHeaderInline;
        mRenderSubHeaderInline = renderSubHeaderInline;
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
        mSubHeaderCache.clear();
    }

    /**
     * Clears the header view cache. Headers will be recreated and
     * rebound on list scroll after this method has been called.
     */
    public void clearHeaderCache() {
        mHeaderCache.clear();
    }

    private RecyclerView.ViewHolder getSubHeader(RecyclerView parent, int position) {
        final long key = mAdapter.getSubHeaderId(position);

        if (mSubHeaderCache.containsKey(key)) {
            return mSubHeaderCache.get(key);
        } else {
            final RecyclerView.ViewHolder holder = mAdapter.onCreateSubHeaderHolder(parent);
            final View header = holder.itemView;

            //noinspection unchecked
            mAdapter.onBindSubHeaderHolder(holder, position);
            measureView(parent, header);
            mSubHeaderCache.put(key, holder);

            return holder;
        }
    }

    public View findHeaderViewUnder(float x, float y) {
        for (RecyclerView.ViewHolder holder : mHeaderCache.values()) {
            final View child = holder.itemView;
            final float translationX = ViewCompat.getTranslationX(child);
            final float translationY = ViewCompat.getTranslationY(child);

            if (x >= child.getLeft() + translationX &&
                    x <= child.getRight() + translationX &&
                    y >= child.getTop() + translationY &&
                    y <= child.getBottom() + translationY) {
                return child;
            }
        }

        return null;
    }

    public View findSubHeaderViewUnder(float x, float y) {
        for (RecyclerView.ViewHolder holder : mSubHeaderCache.values()) {
            final View child = holder.itemView;
            final float translationX = ViewCompat.getTranslationX(child);
            final float translationY = ViewCompat.getTranslationY(child);

            if (x >= child.getLeft() + translationX &&
                    x <= child.getRight() + translationX &&
                    y >= child.getTop() + translationY &&
                    y <= child.getBottom() + translationY) {
                return child;
            }
        }

        return null;
    }

    private RecyclerView.ViewHolder getHeader(RecyclerView parent, int position) {
        final long key = mAdapter.getHeaderId(position);

        if (mHeaderCache.containsKey(key)) {
            return mHeaderCache.get(key);
        } else {
            final RecyclerView.ViewHolder holder = mAdapter.onCreateHeaderHolder(parent);
            final View header = holder.itemView;

            //noinspection unchecked
            mAdapter.onBindHeaderHolder(holder, position);
            measureView(parent, header);
            mHeaderCache.put(key, holder);

            return holder;
        }
    }

    private void measureView(RecyclerView parent, View header) {
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
        if (mAdapter.getSubHeaderId(position) == NO_SUB_HEADER_ID) {
            return false;
        }

        if (position == 0) {
            return true;
        }

        int previous = position - 1;
        return mAdapter.getSubHeaderId(position) != mAdapter.getSubHeaderId(previous);
    }

    private boolean hasHeader(int position) {
        if (mAdapter.getHeaderId(position) == NO_HEADER_ID) {
            return false;
        }
        if (position == 0) {
            return true;
        }

        int previous = position - 1;
        return mAdapter.getHeaderId(position) != mAdapter.getHeaderId(previous);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);

        int headerHeight = 0;

        if (position != RecyclerView.NO_POSITION) {
            if (hasHeader(position)) {
                View header = getHeader(parent, position).itemView;
                headerHeight += mRenderHeaderInline ? 0 : getHeaderHeightForLayout(header);
            }
            if (hasSubHeader(position)) {
                View header = getSubHeader(parent, position).itemView;
                headerHeight += mRenderSubHeaderInline ? 0 : getSubHeaderHeightForLayout(header);
            }
        }

        outRect.set(0, headerHeight, 0, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final int count = parent.getChildCount();

        boolean headerDrawn = false;
        for (int layoutPos = 0; layoutPos < count; layoutPos++) {
            final View child = parent.getChildAt(layoutPos);
            boolean visible = getAnimatedTop(child) > -child.getHeight()/* && child.getTop() < parent.getHeight()*/;
            final int adapterPos = parent.getChildAdapterPosition(child);
            if (visible && adapterPos != RecyclerView.NO_POSITION) {
                int left, top;
                View header = null;

                if (mAdapter.getHeaderId(adapterPos) != NO_HEADER_ID) {
                    header = getHeader(parent, adapterPos).itemView;
                }

                View subHeader = null;
                if (mAdapter.getSubHeaderId(adapterPos) != NO_SUB_HEADER_ID) {
                    subHeader = getSubHeader(parent, adapterPos).itemView;
                }


                if (!headerDrawn || hasSubHeader(adapterPos)) {
                    if (subHeader != null) {
                        c.save();
                        left = child.getLeft();
                        top = getSubHeaderTop(parent, child, header, subHeader, adapterPos, layoutPos);
                        c.translate(left, top);
                        subHeader.setTranslationX(left);
                        subHeader.setTranslationY(top);
                        subHeader.draw(c);
                        c.restore();
                    }
                }

                if (!headerDrawn || hasHeader(adapterPos)) {
                    if (header != null) {
                        c.save();
                        left = child.getLeft();
                        top = getHeaderTop(parent, child, header, subHeader, adapterPos, layoutPos);
                        c.translate(left, top);
                        header.setTranslationX(left);
                        header.setTranslationY(top);
                        header.draw(c);
                        c.restore();
                    }
                }

                headerDrawn = true;
            }
        }
    }

    private int getSubHeaderTop(RecyclerView parent, View child, View header, View subHeader, int adapterPos, int layoutPos) {
        int top = getAnimatedTop(child) - getSubHeaderHeightForLayout(subHeader);
        int headerHeight = header != null ? header.getMeasuredHeight() : 0;
        if (mRenderHeaderInline) {
            top = getTopInline(parent, adapterPos, layoutPos, top, headerHeight);
        }
        if (isFirstValidChild(layoutPos, parent)) {
            // find next view with sub-header and compute the offscreen push if needed
            final int count = parent.getChildCount();
            final long currentHeaderId = mAdapter.getHeaderId(adapterPos);
            final long currentSubHeaderId = mAdapter.getSubHeaderId(adapterPos);
            for (int i = layoutPos + 1; i < count; i++) {
                final View next = parent.getChildAt(i);
                int adapterPosHere = parent.getChildAdapterPosition(next);
                if (adapterPosHere != RecyclerView.NO_POSITION) {
                    final long nextHeaderId = mAdapter.getHeaderId(adapterPosHere);
                    final long nextSubHeaderId = mAdapter.getSubHeaderId(adapterPosHere);

                    if (nextSubHeaderId != currentSubHeaderId) {
                        int nextSubHeaderHeight = 0;
                        if (nextSubHeaderId != NO_SUB_HEADER_ID) {
                            nextSubHeaderHeight = getSubHeader(parent, adapterPosHere).itemView.getHeight();
                        }
                        int headersHeight = getSubHeaderHeightForLayout(subHeader) + nextSubHeaderHeight;
                        if (nextHeaderId != currentHeaderId && nextHeaderId != NO_HEADER_ID) {
                            headersHeight += getHeader(parent, adapterPosHere).itemView.getHeight();
                        }

                        int offset = getAnimatedTop(next) - headersHeight;
                        if (nextSubHeaderId != NO_SUB_HEADER_ID && nextHeaderId != NO_HEADER_ID && currentHeaderId != nextHeaderId && mRenderHeaderInline) {
                            offset += headerHeight;
                        }
                        if (offset < headerHeight) {
                            return offset;
                        } else {
                            break;
                        }
                    }
                }
            }
        }

        return Math.max(headerHeight, top);
    }

    private int getTopInline(RecyclerView parent, int adapterPos, int layoutPos, int top, int headerHeight) {
        boolean haveSubheaderBefore = false;
        final long currentHeaderId = mAdapter.getHeaderId(adapterPos);
        final long currentSubHeaderId = mAdapter.getSubHeaderId(adapterPos);
        for (int i = layoutPos - 1; i >= 0; i--) {
            final View next = parent.getChildAt(i);
            int adapterPosHere = parent.getChildAdapterPosition(next);
            if (adapterPosHere != RecyclerView.NO_POSITION) {
                final long previousSubHeaderId = mAdapter.getSubHeaderId(adapterPosHere);
                final long previousHeaderId = mAdapter.getHeaderId(adapterPosHere);
                if (previousHeaderId == currentHeaderId && previousSubHeaderId != currentSubHeaderId) {
                    haveSubheaderBefore = true;
                }
            }
        }
        if (!haveSubheaderBefore) {
            top += headerHeight;
        }
        return top;
    }

    private int getHeaderTop(RecyclerView parent, View child, View header, View subHeader, int adapterPos, int layoutPos) {
        int top = getAnimatedTop(child) - getHeaderHeightForLayout(header, mRenderHeaderInline) - getSubHeaderHeightForLayout(subHeader);
        if (isFirstValidChild(layoutPos, parent)) {
            final int count = parent.getChildCount();
            final long currentId = mAdapter.getHeaderId(adapterPos);

            // find next view with header and compute the offscreen push if needed
            for (int i = layoutPos + 1; i <= count; i++) {
                View next = parent.getChildAt(i);
                int adapterPosHere = parent.getChildAdapterPosition(next);
                if (adapterPosHere != RecyclerView.NO_POSITION) {
                    long nextId = mAdapter.getHeaderId(adapterPosHere);
                    if (nextId != currentId) {
                        int nextHeaderHeight = 0;
                        if (nextId != NO_HEADER_ID) {
                            nextHeaderHeight = getHeader(parent, adapterPosHere).itemView.getHeight();
                        }
                        final int headersHeight = getHeaderHeightForLayout(header, mRenderHeaderInline) + nextHeaderHeight;
                        View nextSubHeader = null;
                        if (mAdapter.getSubHeaderId(adapterPosHere) != NO_SUB_HEADER_ID) {
                            nextSubHeader = getSubHeader(parent, adapterPosHere).itemView;
                        }
                        final int offset = getAnimatedTop(next) - (headersHeight + getSubHeaderHeightForLayout(nextSubHeader));
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

    private boolean isFirstValidChild(int layoutPos, RecyclerView parent) {
        boolean isFirstValidChild = true;
        for (int otherLayoutPos = layoutPos - 1; otherLayoutPos >= 0; --otherLayoutPos) {
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

    private int getAnimatedTop(View child) {
        return child.getTop() + (int) child.getTranslationY();
    }

    private int getHeaderHeightForLayout(View header) {
        if (header != null) {
            return header.getMeasuredHeight();
        }
        return 0;
    }

    private int getHeaderHeightForLayout(View header, boolean inline) {
        if (header != null && !inline) {
            return header.getMeasuredHeight();
        }
        return 0;
    }

    private int getSubHeaderHeightForLayout(View header) {
        if (header != null) {
            return mRenderSubHeaderInline ? 0 : header.getMeasuredHeight();
        }
        return 0;
    }
}