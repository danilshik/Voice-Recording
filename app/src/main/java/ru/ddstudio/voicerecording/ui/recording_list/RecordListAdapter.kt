package ru.ddstudio.voicerecording.ui.recording_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.ddstudio.voicerecording.R
import ru.ddstudio.voicerecording.data.database.entities.RecordEntity

class RecordListAdapter (
    private val listener : (RecordEntity) -> Unit) : RecyclerView.Adapter<RecordListItemViewHolder>(){

    var items : List<RecordEntity> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordListItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return RecordListItemViewHolder(inflater.inflate(R.layout.item_record_list, parent, false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecordListItemViewHolder, position: Int) {
        holder.bind(items[position], listener)
    }

    fun updateData(data : List<RecordEntity>){
        val diffCallback = object : DiffUtil.Callback(){
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                items[oldItemPosition].id == items[newItemPosition].id


            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                items[oldItemPosition].hashCode() == items[newItemPosition].hashCode()

            override fun getOldListSize(): Int = items.size

            override fun getNewListSize(): Int = data.size
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items = data
        diffResult.dispatchUpdatesTo(this)
    }

}
