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

package ca.barrenechea.stickyheaders.ui;

import android.support.v7.widget.RecyclerView;

import ca.barrenechea.stickyheaders.widget.DoubleHeaderTestAdapter;
import ca.barrenechea.widget.recyclerview.decoration.DoubleHeaderDecoration;

public class DoubleHeaderFragment extends BaseDecorationFragment {

    @Override
    protected void setAdapterAndDecor(RecyclerView list) {
        final DoubleHeaderTestAdapter adapter = new DoubleHeaderTestAdapter(this.getActivity());

        list.setAdapter(adapter);
        list.addItemDecoration(new DoubleHeaderDecoration(adapter), 1);
    }
}
