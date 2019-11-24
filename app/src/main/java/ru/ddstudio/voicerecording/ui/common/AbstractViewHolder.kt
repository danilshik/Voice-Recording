package ru.ddstudio.voicerecording.ui.common

import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer

abstract class AbstractViewHolder(convertView: View) : RecyclerView.ViewHolder(convertView),
    LayoutContainer {
    override val containerView: View?
        get() = itemView

    abstract fun bind(item : Any, listener : (Any) -> Unit)
}