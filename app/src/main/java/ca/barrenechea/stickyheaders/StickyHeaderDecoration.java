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

public class StickyHeaderDecoration extends RecyclerView.ItemDecoration {

    private Map<Long, RecyclerView.ViewHolder> mHeaderCache;

    private Adapter mAdapter;

    public StickyHeaderDecoration(StickyHeaderDecoration.Adapter adapter) {
        mAdapter = adapter;
        mHeaderCache = new HashMap<>();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);

        int headerHeight = 0;
        if (hasHeader(position)) {
            View header = getHeader(parent, position).itemView;
            headerHeight = header.getHeight();
        }

        outRect.set(0, headerHeight, 0, 0);
    }

    private boolean hasHeader(int position) {
        if (position == 0) {
            return true;
        }

        int previous = position - 1;
        return mAdapter.getHeaderId(position) != mAdapter.getHeaderId(previous);
    }

    private RecyclerView.ViewHolder getHeader(RecyclerView parent, int position) {
        final long key = mAdapter.getHeaderId(position);

        if (mHeaderCache.containsKey(key)) {
            return mHeaderCache.get(key);
        } else {
            final RecyclerView.ViewHolder holder = mAdapter.onCreateHeaderViewHolder(parent);
            final View header = holder.itemView;

            //noinspection unchecked
            mAdapter.onBindHeaderViewHolder(holder, position);

            int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(), View.MeasureSpec.UNSPECIFIED);

            int childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
                    parent.getPaddingLeft() + parent.getPaddingRight(), header.getLayoutParams().width);
            int childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
                    parent.getPaddingTop() + parent.getPaddingBottom(), header.getLayoutParams().height);

            header.measure(childWidth, childHeight);
            header.layout(0, 0, header.getMeasuredWidth(), header.getMeasuredHeight());

            mHeaderCache.put(key, holder);

            return holder;
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final int count = parent.getChildCount();

        for (int i = 0; i < count; i++) {
            final View child = parent.getChildAt(i);

            final int adapterPosition = parent.getChildAdapterPosition(child);

            if (i == 0 || hasHeader(adapterPosition)) {
                View header = getHeader(parent, adapterPosition).itemView;
                c.save();
                final int left = child.getLeft();
                final int top = getHeaderTop(child, header, adapterPosition);
                c.translate(left, top);
                header.draw(c);
                c.restore();
            }
        }
    }

    private int getHeaderTop(View child, View header, int position) {
        int top = child.getTop() - header.getHeight();
        if (position != mAdapter.getItemCount() - 1) {
            if (!hasHeader(position + 1)) {
                top = Math.max(0, top);
            } else {
                top = child.getTop() + (child.getHeight() - header.getHeight());
                top = Math.min(0, top);
            }
        }

        return top;
    }

    public interface Adapter<T extends RecyclerView.ViewHolder> {
        public long getHeaderId(int position);

        public T onCreateHeaderViewHolder(ViewGroup parent);

        public void onBindHeaderViewHolder(T viewholder, int position);

        public int getItemCount();
    }
}
