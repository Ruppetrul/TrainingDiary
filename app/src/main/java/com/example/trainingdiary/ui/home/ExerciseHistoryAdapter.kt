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
    private val approachAddListener: (Int, Int?, Float?, Int?) -> Unit
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

        holder.itemView.findViewById<ImageView>(R.id.remove_exercise_from_history).setOnClickListener {
            exerciseDeleteListener(currentItem.exerciseHistory.id)
        }

        holder.itemView.findViewById<ImageView>(R.id.add_approach_to_history).setOnClickListener {
            approachAddListener(currentItem.exerciseHistory.id, null, null, null)
        }

        handleApproach(holder, currentItem)
    }

    private fun handleApproach(
        holder: ExerciseHistoryViewHolder,
        currentItem: ExerciseHistoryWithExercise
    ) {
        val flexboxLayout = holder.itemView.findViewById<FlexboxLayout>(R.id.approaches_flexbox)
        flexboxLayout.removeAllViewsInLayout()

        for (approach in currentItem.approaches) {
            val approachView = LayoutInflater.from(holder.itemView.context).inflate(
                R.layout.approach_item,
                flexboxLayout,
                false
            )

            approachView.findViewById<TextView>(R.id.weight).text =
                approach.weight.toString() + " Kg"
            approachView.findViewById<TextView>(R.id.repeat).text =
                approach.repeatCount.toString() + " Rep"

            approachView.setOnClickListener {
                approachAddListener(currentItem.exerciseHistory.id, approach.id, approach.weight, approach.repeatCount)
            }

            flexboxLayout.addView(approachView)
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
}
