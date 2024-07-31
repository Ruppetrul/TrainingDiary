import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trainingdiary.R
import com.example.trainingdiary.models.ExerciseHistoryWithExercise

class ExerciseHistoryAdapter : ListAdapter<ExerciseHistoryWithExercise, ExerciseHistoryAdapter.ExerciseHistoryViewHolder>(ExerciseHistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseHistoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.exercise_history_item, parent, false)
        return ExerciseHistoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExerciseHistoryViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
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
