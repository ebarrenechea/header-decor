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

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * The adapter to assist the {@link DoubleHeaderAdapter} in creating and binding the headers and
 * sub-header views.
 *
 * @param <H> the header view holder
 * @param <S> the sub-header view holder
 */
public interface DoubleHeaderAdapter<H extends RecyclerView.ViewHolder, S extends RecyclerView.ViewHolder> extends StickyHeaderAdapter<H> {

    /**
     * Returns the sub-header id for the item at the given position.
     *
     * @param position the item position
     * @return the sub-header id
     */
    long getSubHeaderId(int position);

    /**
     * Creates a new sub-header ViewHolder.
     *
     * @param parent the sub-header's view parent
     * @return a view holder for the created sub-header view
     */
    @NonNull
    S onCreateSubHeaderViewHolder(@NonNull ViewGroup parent);

    /**
     * Updates the sub-header view to reflect the header data for the given position
     *
     * @param holder the sub-header view holder
     * @param position the sub-header's item position
     */
    void onBindSubHeaderViewHolder(@NonNull S holder, int position);
}
