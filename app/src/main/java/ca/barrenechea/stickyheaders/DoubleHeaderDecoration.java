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

package ca.barrenechea.stickyheaders;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

public class DoubleHeaderDecoration extends RecyclerView.ItemDecoration {

    private DoubleHeaderAdapter mAdapter;
    private Map<Long, RecyclerView.ViewHolder> mSubHeaderCache;
    private Map<Long, RecyclerView.ViewHolder> mHeaderCache;

    public DoubleHeaderDecoration(DoubleHeaderAdapter adapter) {
        mAdapter = adapter;

        mSubHeaderCache = new HashMap<>();
        mHeaderCache = new HashMap<>();
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
        if (position == 0) {
            return true;
        }

        int previous = position - 1;
        return mAdapter.getSubHeaderId(position) != mAdapter.getSubHeaderId(previous);
    }

    private boolean hasHeader(int position) {
        if (position == 0) {
            return true;
        }

        int previous = position - 1;
        return mAdapter.getHeaderId(position) != mAdapter.getHeaderId(previous);
    }

    private boolean isLastForHeader(int position) {
        return position == mAdapter.getItemCount() - 1 || mAdapter.getHeaderId(position + 1) != mAdapter.getHeaderId(position);
    }

    private boolean isLastForSubHeader(int position) {
        return position == mAdapter.getItemCount() - 1 || mAdapter.getSubHeaderId(position + 1) != mAdapter.getSubHeaderId(position);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);

        int headerHeight = 0;

        if (hasSubHeader(position)) {
            if (hasHeader(position)) {
                View header = getHeader(parent, position).itemView;
                headerHeight += header.getHeight();
            }

            View header = getSubHeader(parent, position).itemView;
            headerHeight += header.getHeight();
        }

        outRect.set(0, headerHeight, 0, 0);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final int count = parent.getChildCount();

        for (int layoutPos = 0; layoutPos < count; layoutPos++) {
            final View child = parent.getChildAt(layoutPos);

            final int adapterPos = parent.getChildAdapterPosition(child);

            if (layoutPos == 0 || hasSubHeader(adapterPos)) {
                int left, top;
                final View header = getHeader(parent, adapterPos).itemView;
                final View subHeader = getSubHeader(parent, adapterPos).itemView;

                c.save();
                left = child.getLeft();
                top = getSubHeaderTop(parent, child, header, subHeader, adapterPos, layoutPos);
                c.translate(left, top);
                subHeader.draw(c);
                c.restore();

                if (layoutPos == 0 || hasHeader(adapterPos)) {
                    c.save();
                    left = child.getLeft();
                    top = getHeaderTop(parent, child, subHeader, header, adapterPos, layoutPos);
                    c.translate(left, top);
                    header.draw(c);
                    c.restore();
                }
            }
        }
    }

    private int getSubHeaderTop(RecyclerView parent, View child, View header, View subHeader, int adapterPos, int layoutPos) {
        int top = child.getTop() - subHeader.getHeight();
        if (layoutPos == 0) {
            final int count = parent.getChildCount();
            final long currentHeaderId = mAdapter.getHeaderId(adapterPos);
            final long currentSubHeaderId = mAdapter.getSubHeaderId(adapterPos);

            // find next view with sub-header and compute the offscreen push if needed
            for (int i = 1; i < count; i++) {
                final View next = parent.getChildAt(i);
                final long nextHeaderId = mAdapter.getHeaderId(adapterPos + i);
                final long nextSubHeaderId = mAdapter.getSubHeaderId(adapterPos + i);

                if ((nextSubHeaderId != currentSubHeaderId)) {
                    int headersHeight = subHeader.getHeight() + getSubHeader(parent, i).itemView.getHeight();
                    if (nextHeaderId != currentHeaderId) {
                        headersHeight += getHeader(parent, i).itemView.getHeight();
                    }

                    final int offset = next.getTop() - headersHeight;
                    if (offset < header.getHeight()) {
                        return offset;
                    } else {
                        break;
                    }
                }
            }
        }

        return Math.max(header.getHeight(), top);
    }

    private int getHeaderTop(RecyclerView parent, View child, View header, View subHeader, int adapterPos, int layoutPos) {
        int top = child.getTop() - header.getHeight() - subHeader.getHeight();
        if (layoutPos == 0) {
            final int count = parent.getChildCount();
            final long currentId = mAdapter.getHeaderId(adapterPos);

            // find next view with header and compute the offscreen push if needed
            for (int i = 1; i < count; i++) {
                long nextId = mAdapter.getHeaderId(adapterPos + i);

                if (nextId != currentId) {
                    final View next = parent.getChildAt(i);
                    final int headersHeight = header.getHeight() + getHeader(parent, i).itemView.getHeight();
                    final int offset = next.getTop() - headersHeight - subHeader.getHeight();

                    if (offset < 0) {
                        return offset;
                    } else {
                        break;
                    }
                }
            }

            top = Math.max(0, top);
        }

        return top;
    }
}
