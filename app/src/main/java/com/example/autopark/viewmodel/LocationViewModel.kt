import android.util.Log

import androidx.lifecycle.ViewModel

import com.example.autopark.model.Location

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.flow.*


class LocationViewModel : ViewModel() {

    //get db, route is locations
    private val dbRef = FirebaseDatabase.getInstance().getReference("locations")

    //create a empty list
    private val _locations = MutableStateFlow<List<Location>>(emptyList())

    //later fetch locations to this empty list and return
    val locations: StateFlow<List<Location>> = _locations.asStateFlow()

    init {
        fetchLocations() // locationViewModel.locations.collectAsState()
    }

    /*
    * Input:None
    * Return:None
    *
    * This method fetch locations from firebase to _locations.values and pass it to the home.kt
    *
    * */
    fun fetchLocations() {
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fetchedLocations = snapshot.children.mapNotNull { child ->
                    val location = child.getValue<Location>()
                    location?.copy(locationId = child.key?.toIntOrNull() ?: 0) // Ensuring the location ID is set correctly
                }
                _locations.value = fetchedLocations //set locations
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseDB", "Failed to read location data: ${error.message}")
            }
        })
    }

    //A utility function to get the location name based on location ID.
    fun getLocationName(id: Int): String{
        val locationList = locations.value
        for (location in locationList) {
            if (location.locationId == id) {
                return location.name
            }
        }
        return "Unknown Location"
    }

}
