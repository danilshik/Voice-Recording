package ru.ddstudio.voicerecording.ui.recording_list

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import ru.ddstudio.voicerecording.R
import ru.ddstudio.voicerecording.data.database.entities.RecordEntity
import kotlinx.android.synthetic.main.item_record_list.view.*
import ru.ddstudio.voicerecording.extensions.toStringTime
import java.time.format.DateTimeFormatter

class RecordListAdapter (
    private val listener : (RecordEntity) -> Unit) : RecyclerView.Adapter<RecordListAdapter.RecordListItemViewHolder>(){

    var items : List<RecordEntity> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordListItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val convertView = inflater.inflate(R.layout.item_record_list, parent, false)
        return RecordListItemViewHolder(convertView)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecordListItemViewHolder, position: Int) {
        holder.bind(items[position], listener)
    }

    fun updateData(data : List<RecordEntity>){
        val diffCallback = object : DiffUtil.Callback(){
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                items[oldItemPosition].id == data[newItemPosition].id


            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                items[oldItemPosition].hashCode() == data[newItemPosition].hashCode()

            override fun getOldListSize(): Int = items.size

            override fun getNewListSize(): Int = data.size
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = data
        diffResult.dispatchUpdatesTo(this)
    }

    inner class RecordListItemViewHolder(convertView: View) : RecyclerView.ViewHolder(convertView),
        LayoutContainer {
        override val containerView: View?
            get() = itemView

        fun bind(record : RecordEntity, listener: (RecordEntity) -> Unit){
            itemView.tv_name.text = record.name
            itemView.tv_duration.text = record.duration.millis.toStringTime()
            itemView.tv_date_created.text = record.createdDateTime.toString("dd.MM.yyyy HH:mm:ss")
        }
    }

}
