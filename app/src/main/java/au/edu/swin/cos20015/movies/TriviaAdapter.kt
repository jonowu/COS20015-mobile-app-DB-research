package au.edu.swin.cos20015.movies

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import au.edu.swin.cos20015.movies.models.Movie
import kotlinx.android.synthetic.main.item_trivia.view.*

// create adapter that takes two parameters: the context and list of trivia
// inherit it from the base adapter of RecyclerView.
class TriviaAdapter (val context: Context, val trivia: List<Movie>,
                     val listener: (Movie) -> Unit) :
    RecyclerView.Adapter<TriviaAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_trivia, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = trivia.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(trivia[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(trivia: Movie) {
            itemView.setOnClickListener { listener(trivia) } // show the toast

            itemView.tvGenre.text = trivia.genre
            // show the first answer on the card
            itemView.tvAnswer.text = trivia.actors[0]
            itemView.tvRating.text = trivia.rating.toString()
            itemView.tvtitle.text = trivia.title
        }
    }


}