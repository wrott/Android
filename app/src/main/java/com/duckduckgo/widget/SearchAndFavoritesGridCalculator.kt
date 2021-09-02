/*
 * Copyright (c) 2021 DuckDuckGo
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

package com.duckduckgo.widget

import android.content.Context
import com.duckduckgo.app.browser.R
import com.duckduckgo.mobile.android.ui.view.toDp
import timber.log.Timber

class SearchAndFavoritesGridCalculator {
    fun calculateColumns(parameters: Parameters, width: Int): Int {
        var n = 2
        var totalSize = (n * parameters.item) + ((n - 1) * parameters.divider) + (parameters.margins * 2)

        Timber.i("SearchAndFavoritesWidget width n:$n $totalSize vs $width")
        while (totalSize <= width) {
            ++n
            totalSize = (n * parameters.item) + ((n - 1) * parameters.divider) + (parameters.margins * 2)
            Timber.i("SearchAndFavoritesWidget width n:$n $totalSize vs $width")
        }

        return WIDGET_COLUMNS_MIN.coerceAtLeast(n - 1)
    }

    fun calculateRows(parameters: Parameters, height: Int): Int {
        var n = 1
        var totalSize = parameters.searchBar + (n * parameters.item) + ((n - 1) * parameters.divider) + parameters.margins

        Timber.i("SearchAndFavoritesWidget height n:$n $totalSize vs $height")
        while (totalSize <= height) {
            ++n
            totalSize = parameters.searchBar + (n * parameters.item) + ((n - 1) * parameters.divider) + parameters.margins
            Timber.i("SearchAndFavoritesWidget height n:$n $totalSize vs $height")
        }

        var rows = n - 1
        rows = WIDGET_ROWS_MIN.coerceAtLeast(rows)
        rows = WIDGET_ROWS_MAX.coerceAtMost(rows)
        return rows
    }

    companion object {
        private const val WIDGET_COLUMNS_MIN = 2
        private const val WIDGET_ROWS_MAX = 4
        private const val WIDGET_ROWS_MIN = 1

        data class Parameters(val margins: Float, val item: Float, val divider: Float, val searchBar: Float = 0f)
    }
}
