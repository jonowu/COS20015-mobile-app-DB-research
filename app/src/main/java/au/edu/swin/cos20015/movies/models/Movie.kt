package au.edu.swin.cos20015.movies.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import com.google.firebase.firestore.DocumentId

@Parcelize
data class Movie(
    @DocumentId var documentId: String = "",
    var title: String = "", var rating: Int = 0,
    var actors: List<String> = listOf(), var genre: String = ""
): Parcelable {
}

