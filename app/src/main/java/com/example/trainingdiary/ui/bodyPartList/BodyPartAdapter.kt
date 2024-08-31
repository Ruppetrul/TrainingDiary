package com.example.trainingdiary.ui.bodyPartList

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trainingdiary.R
import com.example.trainingdiary.models.BodyPart

class BodyPartAdapter(
    private val context: Context,
    private val itemClickListener: (Int) -> Unit
) : RecyclerView.Adapter<BodyPartAdapter.RecordViewHolder>() {

    private var records = emptyList<BodyPart>()

    inner class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recordTextView: TextView = itemView.findViewById(R.id.title)

        fun bind(record: BodyPart) {
            recordTextView.text = record.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.body_part_item, parent, false)
        return RecordViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val current = records[position]
        holder.bind(current)
        holder.itemView.setOnClickListener {
            itemClickListener(current.id)
        }

        setDrawableIfExists(context, holder.itemView.findViewById(R.id.history_exercise_avatar), current.logo)
    }

    override fun getItemCount() = records.size

    @SuppressLint("NotifyDataSetChanged")
    fun setRecords(newRecords: List<BodyPart>) {
        this.records = newRecords
        notifyDataSetChanged()
    }

    fun setDrawableIfExists(context: Context, imageView: ImageView, drawableName: String?) {

        val resourceId = context.resources.getIdentifier(drawableName, "drawable", context.packageName)

        Log.d("RESOURCE", "drawableName: $drawableName: $resourceId")
        if (resourceId != 0) {
            imageView.setImageResource(resourceId)
            imageView.setBackgroundColor(Color.WHITE);
        } else {
            //imageView.setImageResource(R.drawable.default_image)
        }
    }
}
