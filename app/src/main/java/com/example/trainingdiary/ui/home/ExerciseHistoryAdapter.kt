
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trainingdiary.R
import com.example.trainingdiary.models.ExerciseHistoryWithExercise
import com.google.android.flexbox.FlexboxLayout

class ExerciseHistoryAdapter(
    private val exerciseDeleteListener: (Int) -> Unit,
    private val approachAddListener: (Int, Int?, Float?, Int?, Int, Int) -> Unit,
    private val context: Context
)
    : ListAdapter<ExerciseHistoryWithExercise,
        ExerciseHistoryAdapter.ExerciseHistoryViewHolder>(ExerciseHistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseHistoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.exercise_history_item, parent, false)
        return ExerciseHistoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExerciseHistoryViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)

        val firstBodyPart = currentItem?.bodyParts?.firstOrNull()
        val logo = firstBodyPart?.logo
        setDrawableIfExists(context, holder.itemView.findViewById<ImageView>(R.id.history_exercise_avatar), logo)

        holder.itemView.findViewById<ImageView>(R.id.remove_exercise_from_history).setOnClickListener {
            exerciseDeleteListener(currentItem.exerciseHistory.id)
        }

        holder.itemView.setOnClickListener { // new approach
            approachAddListener(currentItem.exerciseHistory.id, null, null, null, currentItem.approaches.size + 1, currentItem.exercise.id)
        }

        handleApproach(holder, currentItem)
    }

    private fun handleApproach(
        holder: ExerciseHistoryViewHolder,
        currentItem: ExerciseHistoryWithExercise
    ) {
        val flexboxLayout = holder.itemView.findViewById<FlexboxLayout>(R.id.approaches_flexbox)
        flexboxLayout.removeAllViewsInLayout()

        if (currentItem.approaches.isEmpty()) {
            val emptyTextView = TextView(holder.itemView.context).apply {
                text = holder.itemView.context.getString(R.string.add_approach)
                textSize = 12f
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.CENTER
            }

            flexboxLayout.addView(emptyTextView)
        } else {
            currentItem.approaches.forEachIndexed { index, approach ->
                val approachView = LayoutInflater.from(holder.itemView.context).inflate(
                    R.layout.approach_item,
                    flexboxLayout,
                    false
                )

                approachView.findViewById<TextView>(R.id.weight).text =
                    approach.weight.toString() + " " + holder.itemView.context.getString(R.string.kilogram)
                approachView.findViewById<TextView>(R.id.repeat).text =
                    approach.repeatCount.toString() + " " + holder.itemView.context.getString(R.string.repeats)

                approachView.setOnClickListener { //Edit exists approach
                    approachAddListener(
                        currentItem.exerciseHistory.id,
                        approach.id,
                        approach.weight,
                        approach.repeatCount,
                        index + 1,
                        currentItem.exercise.id
                    )
                }

                flexboxLayout.addView(approachView)
            }
        }
    }

    class ExerciseHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val exerciseIdTextView: TextView = itemView.findViewById(R.id.id)

        fun bind(exerciseHistory: ExerciseHistoryWithExercise) {
            exerciseIdTextView.text = exerciseHistory.exercise.title
        }
    }

    class ExerciseHistoryDiffCallback : DiffUtil.ItemCallback<ExerciseHistoryWithExercise>() {
        override fun areItemsTheSame(oldItem: ExerciseHistoryWithExercise, newItem: ExerciseHistoryWithExercise): Boolean {
            return oldItem.exercise.id == newItem.exercise.id
        }

        override fun areContentsTheSame(oldItem: ExerciseHistoryWithExercise, newItem: ExerciseHistoryWithExercise): Boolean {
            return oldItem == newItem
        }
    }

    fun setDrawableIfExists(context: Context, imageView: ImageView, drawableName: String?) {
        if (drawableName != null) {
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
}
