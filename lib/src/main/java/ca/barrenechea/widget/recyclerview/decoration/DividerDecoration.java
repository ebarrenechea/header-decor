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
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

/**
 * A simple divider decoration with customizable colour, height, and left and right padding.
 */
public class DividerDecoration extends RecyclerView.ItemDecoration {
    private int height;
    private int leftPadding;
    private int rightPadding;
    private Paint paint;

    private DividerDecoration(int height, int lPadding, int rPadding, int colour) {
        this.height = height;
        this.leftPadding = lPadding;
        this.rightPadding = rPadding;
        this.paint = new Paint();
        this.paint.setColor(colour);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent,
            @NonNull RecyclerView.State state) {

        int count = parent.getChildCount();

        for (int i = 0; i < count; i++) {
            final View child = parent.getChildAt(i);
            final int top = child.getBottom();
            final int bottom = top + height;

            int left = child.getLeft() + leftPadding;
            int right = child.getRight() - rightPadding;

            canvas.save();
            canvas.drawRect(left, top, right, bottom, paint);
            canvas.restore();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
            @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.set(0, 0, 0, height);
    }

    /**
     * A basic builder for divider decorations. The default builder creates a 1px thick black
     * divider decoration.
     */
    public static class Builder {
        private Resources mResources;
        private int mHeight;
        private int mLPadding;
        private int mRPadding;
        private int mColour;

        public Builder(@NonNull Context context) {
            mResources = context.getResources();
            mHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 1f,
                    context.getResources().getDisplayMetrics());
            mLPadding = 0;
            mRPadding = 0;
            mColour = Color.BLACK;
        }

        /**
         * Set the divider height in pixels
         *
         * @param pixels height in pixels
         * @return the current instance of the Builder
         */
        @NonNull
        public Builder setHeight(float pixels) {
            mHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, pixels,
                    mResources.getDisplayMetrics());
            return this;
        }

        /**
         * Set the divider height in dp
         *
         * @param resource height resource id
         * @return the current instance of the Builder
         */
        @NonNull
        public Builder setHeight(@DimenRes int resource) {
            mHeight = mResources.getDimensionPixelSize(resource);
            return this;
        }

        /**
         * Sets both the left and right padding in pixels
         *
         * @param pixels padding in pixels
         * @return the current instance of the Builder
         */
        @NonNull
        public Builder setPadding(float pixels) {
            setLeftPadding(pixels);
            setRightPadding(pixels);
            return this;
        }

        /**
         * Sets the left and right padding in dp
         *
         * @param resource padding resource id
         * @return the current instance of the Builder
         */
        @NonNull
        public Builder setPadding(@DimenRes int resource) {
            setLeftPadding(resource);
            setRightPadding(resource);
            return this;
        }

        /**
         * Sets the left padding in pixels
         *
         * @param pixelPadding left padding in pixels
         * @return the current instance of the Builder
         */
        @NonNull
        public Builder setLeftPadding(float pixelPadding) {
            mLPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, pixelPadding,
                    mResources.getDisplayMetrics());
            return this;
        }

        /**
         * Sets the right padding in pixels
         *
         * @param pixelPadding right padding in pixels
         * @return the current instance of the Builder
         */
        @NonNull
        public Builder setRightPadding(float pixelPadding) {
            mRPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, pixelPadding,
                    mResources.getDisplayMetrics());
            return this;
        }

        /**
         * Sets the left padding in dp
         *
         * @param resource left padding resource id
         * @return the current instance of the Builder
         */
        @NonNull
        public Builder setLeftPadding(@DimenRes int resource) {
            mLPadding = mResources.getDimensionPixelSize(resource);
            return this;
        }

        /**
         * Sets the right padding in dp
         *
         * @param resource right padding resource id
         * @return the current instance of the Builder
         */
        @NonNull
        public Builder setRightPadding(@DimenRes int resource) {
            mRPadding = mResources.getDimensionPixelSize(resource);
            return this;
        }

        /**
         * Sets the divider colour
         *
         * @param resource the colour resource id
         * @return the current instance of the Builder
         */
        @NonNull
        public Builder setColorResource(@ColorRes int resource) {
            setColor(mResources.getColor(resource));
            return this;
        }

        /**
         * Sets the divider colour
         *
         * @param color the colour
         * @return the current instance of the Builder
         */
        @NonNull
        public Builder setColor(@ColorInt int color) {
            mColour = color;
            return this;
        }

        /**
         * Instantiates a DividerDecoration with the specified parameters.
         *
         * @return a properly initialized DividerDecoration instance
         */
        @NonNull
        public DividerDecoration build() {
            return new DividerDecoration(mHeight, mLPadding, mRPadding, mColour);
        }
    }
}
