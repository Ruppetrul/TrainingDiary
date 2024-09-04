package com.example.trainingdiary.ui.exerciseList

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trainingdiary.R
import com.example.trainingdiary.models.ExerciseWithBodyParts

class ExerciseAdapter(
    private val itemClickListener: (Int) -> Unit,
    private val context: Context
) : RecyclerView.Adapter<ExerciseAdapter.RecordViewHolder>() {

    private var records = emptyList<ExerciseWithBodyParts>()

    inner class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recordTextView: TextView = itemView.findViewById(R.id.title)

        fun bind(record: ExerciseWithBodyParts) {
            recordTextView.text = record.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.exercise_item, parent, false)
        return RecordViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val current = records[position]
        holder.bind(current)
        holder.itemView.setOnClickListener {
            itemClickListener(current.id!!)
        }

        setDrawableIfExists(context, holder.itemView.findViewById<ImageView>(R.id.exercise_avatar),
            current.bodyParts.first().logo
        )
    }

    override fun getItemCount() = records.size

    @SuppressLint("NotifyDataSetChanged")
    fun setRecords(newRecords: List<ExerciseWithBodyParts>) {
        this.records = newRecords
        notifyDataSetChanged()
    }

    fun setDrawableIfExists(context: Context, imageView: ImageView, drawableName: String?) {
        val resourceId = context.resources.getIdentifier(drawableName, "drawable", context.packageName)

        if (resourceId != 0) {
            imageView.setImageResource(resourceId)
            imageView.setBackgroundColor(Color.WHITE);
        } else {
            //imageView.setImageResource(R.drawable.default_image)
        }
    }
}
