package com.example.autopark.utils
import com.example.autopark.model.Location
import com.google.firebase.database.FirebaseDatabase
object DatabaseInitializer {

    fun generateMockLocations(): List<Location> {
        return listOf(
            //Location(1, "3070", "Monash-01", "308 Woodside road", 145.133858, -37.912473, 4.5, 3, "08:00", "18:00"),
            //Location(2, "3071", "Monash-02", "310 Woodside road", 145.134858, -37.912573, 5.0, 2, "09:00", "17:00")
            // Add more locations as needed
            //Location(3, "3072", "Monash-03", "312 Woodside road", 145.135858, -37.913573, 4.0, 4, "07:00", "19:00"),
            //Location(4, "3073", "Monash-04", "314 Woodside road", 145.136858, -37.914573, 3.5, 2, "10:00", "16:00"),
            //Location(5, "3074", "Monash-05", "316 Woodside road", 145.137858, -37.915573, 6.0, 3, "09:00", "17:00"),
            //Location(6, "3075", "Monash-06", "318 Woodside road", 145.138858, -37.916573, 7.0, 2, "08:00", "18:00"),
            //Location(7, "3076", "Monash-07", "320 Woodside road", 145.139858, -37.917573, 5.5, 4, "06:00", "20:00"),
            //Location(8, "3077", "Monash-08", "322 Woodside road", 145.140858, -37.918573, 4.5, 3, "11:00", "15:00"),
            //Location(9, "3078", "Monash-09", "324 Woodside road", 145.141858, -37.919573, 5.0, 5, "12:00", "14:00"),
            //Location(10, "3079", "Monash-10", "326 Woodside road", 145.142858, -37.920573, 3.0, 2, "08:00", "18:00"),
            //Location(11, "3080", "Monash-11", "328 Woodside road", 145.143858, -37.921573, 4.5, 2, "09:00", "17:00"),
            //Location(12, "3081", "Monash-12", "330 Woodside road", 145.144858, -37.922573, 6.5, 3, "07:00", "19:00")
        )
    }

    fun insertMockLocationsToFirebase(locations: List<Location>) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("locations")

        locations.forEach { location ->
            databaseReference.child(location.locationId.toString()).setValue(location)
        }
    }



}