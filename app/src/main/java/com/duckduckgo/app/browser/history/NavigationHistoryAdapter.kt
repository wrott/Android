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

package com.duckduckgo.app.browser.history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.duckduckgo.app.browser.NavigationHistory
import com.duckduckgo.app.browser.NavigationHistoryEntry
import com.duckduckgo.app.browser.R
import kotlinx.android.synthetic.main.navigation_history_row.view.*

class NavigationHistoryAdapter : RecyclerView.Adapter<NavigationViewHolder>() {

    private var navigationHistory: NavigationHistory = NavigationHistory(emptyList())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavigationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.navigation_history_row, parent, false)
        return NavigationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NavigationViewHolder, position: Int) {
        val entry = navigationHistory.entries[position]
        holder.bindItem(entry)
    }

    override fun getItemCount(): Int {
        return navigationHistory.entries.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateNavigationHistory(navigationHistory: NavigationHistory) {
        this.navigationHistory = navigationHistory
        notifyDataSetChanged()
    }
}

class NavigationViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {

    val titleView: TextView = itemView.findViewById(R.id.title)

    fun bindItem(entry: NavigationHistoryEntry) {
        titleView.text = entry.title
    }
}
