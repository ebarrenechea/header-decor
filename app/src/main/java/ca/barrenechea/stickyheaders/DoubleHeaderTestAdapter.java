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

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DoubleHeaderTestAdapter extends RecyclerView.Adapter<DoubleHeaderTestAdapter.ViewHolder> implements
        DoubleHeaderAdapter<DoubleHeaderTestAdapter.TimelineHolder, DoubleHeaderTestAdapter.DateHeaderHolder> {

    private LayoutInflater mInflater;

    public DoubleHeaderTestAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View view = mInflater.inflate(R.layout.item_test, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.item.setText("Item " + i);
    }


    @Override
    public int getItemCount() {
        return 50;
    }

    @Override
    public long getHeaderId(int position) {
        return position / 14;
    }

    @Override
    public long getSubHeaderId(int position) {
        return position / 7;
    }

    @Override
    public TimelineHolder onCreateHeaderHolder(ViewGroup parent) {
        final View view = mInflater.inflate(R.layout.super_header_test, parent, false);
        return new TimelineHolder(view);
    }

    @Override
    public DateHeaderHolder onCreateSubHeaderHolder(ViewGroup parent) {
        final View view = mInflater.inflate(R.layout.header_test, parent, false);
        return new DateHeaderHolder(view);
    }

    @Override
    public void onBindHeaderHolder(TimelineHolder viewholder, int position) {
        viewholder.timeline.setText("Timeline " + getHeaderId(position));
    }

    @Override
    public void onBindSubHeaderHolder(DateHeaderHolder viewholder, int position) {
        viewholder.date.setText("Date " + getSubHeaderId(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView item;

        public ViewHolder(View itemView) {
            super(itemView);

            item = (TextView) itemView;
        }
    }

    static class TimelineHolder extends RecyclerView.ViewHolder {
        public TextView timeline;

        public TimelineHolder(View itemView) {
            super(itemView);

            timeline = (TextView) itemView;
        }
    }

    static class DateHeaderHolder extends RecyclerView.ViewHolder {
        public TextView date;

        public DateHeaderHolder(View itemView) {
            super(itemView);

            date = (TextView) itemView;
        }
    }
}
