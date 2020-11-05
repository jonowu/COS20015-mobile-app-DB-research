package au.edu.swin.cos20015.movies

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import au.edu.swin.cos20015.movies.models.Movie
import au.edu.swin.cos20015.movies.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import android.renderscript.ScriptGroup

import kotlinx.android.synthetic.main.activity_create.*


private var signedInUser: User? = null
private const val TAG = "CreateActivity"
// these are character types that emoji unicodes can be made out of
private val VALID_CHAR_TYPES = listOf(
    Character.SURROGATE,
    Character.NON_SPACING_MARK,
    Character.OTHER_SYMBOL
).map { it.toInt() }.toSet()
class CreateActivity : AppCompatActivity() {
    private lateinit var firestoreDb: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        // Get the extras (if there are any)
        val extras = intent.extras
        Log.i(TAG, "hello yeet")
        if (extras != null) {
            if (extras.containsKey(EXTRA_TRIVIA)) {
                val movie = intent.getParcelableExtra<Movie>(EXTRA_TRIVIA)!!
                ettitle.setText(movie.title)
                etGenre.setText(movie.genre)
                rbRating.rating = movie.rating.toFloat()
                etActors.setText(movie.actors.toString()
                    .replace("[", "")  //remove the right bracket
                    .replace("]", ""))  //remove the left bracket)
                btnSubmit.text = "Update"
                btnDelete.visibility = View.VISIBLE // make the delete button visible
            }
        }
        firestoreDb = FirebaseFirestore.getInstance() // points to the root of the db
        btnSubmit.setOnClickListener {
            handleSubmitButtonClick()
        }
        btnDelete.setOnClickListener() {
            handleDeleteButtonClick()
        }


    }


    private fun handleDeleteButtonClick() {
        AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Delete Trivia").setMessage("Are you sure you want to delete this trivia?")
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                val extras = intent.extras
                if (extras != null && extras.containsKey(EXTRA_TRIVIA)) { // If trivia is being updated
                    val movie = intent.getParcelableExtra<Movie>(EXTRA_TRIVIA)!!
                    Toast.makeText(this, "Updating...", Toast.LENGTH_SHORT).show()
                    firestoreDb.collection("movies").document(movie.documentId)
                        .delete()
                        .addOnSuccessListener {

                            finish()
                        }
                        .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
                }
            }).setNegativeButton("No", null).show()
        return
    }

    private fun handleSubmitButtonClick() {
        Log.i(TAG, "submit button clicked")

        // Data validation
        if (ettitle.text.isBlank()) {
            Toast.makeText(this, "title cannot be empty", Toast.LENGTH_SHORT).show()
            //return
        }
        if (rbRating.rating < 1) {
            Toast.makeText(this, "Rating must be selected", Toast.LENGTH_SHORT).show()
            //return
        }
        if (etActors.text.isBlank()) {
            Toast.makeText(this, "Actors cannot be empty", Toast.LENGTH_SHORT).show()
            //return
        }

        btnSubmit.isEnabled = false

        val actors = etActors.text.split(",").map { it.trim() }
        // Create movie object with the provided details
        val newTrivia =  Movie(
            "",
            ettitle.text.toString(),
            rbRating.rating.toInt(),
            actors,
            etGenre.text.toString()
        )
        val extras = intent.extras
        if (extras != null && extras.containsKey(EXTRA_TRIVIA)) { // If trivia is being updated
            Log.i(TAG, " attempting to update trivia")

            val movie = intent.getParcelableExtra<Movie>(EXTRA_TRIVIA)!!
            Toast.makeText(this, "Updating...", Toast.LENGTH_SHORT).show()

            firestoreDb.collection("movies").document(movie.documentId)
                .set(newTrivia, SetOptions.merge())
                .addOnSuccessListener { val profileIntent = Intent(
                    this,
                    ProfileActivity::class.java
                )
                    profileIntent.putExtra(EXTRA_USERNAME, signedInUser?.username)
                    startActivity(profileIntent)
                    finish()
                }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }


        } else { // add the newTrivia to Firebase
            Log.i(TAG, "adding new trivia")

            firestoreDb.collection("movies")
                .add(newTrivia)
                .addOnSuccessListener {
                    // navigate back to profile screen
                    Log.i(TAG, " trivia added")
                    finish()
                }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }

    }
}