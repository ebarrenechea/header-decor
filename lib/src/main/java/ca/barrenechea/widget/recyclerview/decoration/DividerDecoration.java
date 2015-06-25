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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

/**
 * A simple and borderline useless divider decoration. Used mostly for testing and debugging purposes.
 * It will always draw a 1px thick black divider.
 */
public class DividerDecoration extends RecyclerView.ItemDecoration {

    private int mHeight;
    private Paint mPaint;

    public DividerDecoration(Context context) {
        mHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 1f, context.getResources().getDisplayMetrics());
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int count = parent.getChildCount();

        for (int i = 0; i < count; i++) {
            final View child = parent.getChildAt(i);
            final int top = child.getTop();
            final int bottom = top + mHeight;
            final int left = child.getLeft();
            final int right = child.getRight();

            c.save();
            c.drawRect(left, top, right, bottom, mPaint);
            c.restore();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 0, mHeight);
    }
    
    /**
     * Set Color Divider
     */
    public void setColorDivider(int color) {
        mPaint.setColor(color);
    }
}
