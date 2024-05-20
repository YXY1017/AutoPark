
import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.State

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.autopark.database.AppDatabase
import com.example.autopark.model.BankCard
import com.example.autopark.model.Car

import com.example.autopark.model.User
import com.example.autopark.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class UserViewModel (application: Application) : AndroidViewModel(application) {
    private val userRepository: UserRepository
    val bankCards = mutableStateListOf<BankCard>()
    private val _user = mutableStateOf(User("", "", "", "", "", "", ""))
    val user: State<User> = _user

    private var phoneNumber:String = ""

    //private val sessionManager = SessionManager(application)
    //todo might declear the firebase instance as a global varaible

    init {

        //for Room
        val userDao = AppDatabase.getDatabase(application).userDao()
        val bankCardDao = AppDatabase.getDatabase(application).bankCardDao()
        userRepository = UserRepository(userDao, bankCardDao)
        loadCurrentUser()


    }

    /*
    *   Input:User object
    *   Output: Boolean if registration process successful
    *   This function takes an user object and insert the user object into the firebase database*/

    //这个就是返回用户的creditcards
    fun fetchUserAndCards() {
        val email = getUserEmail()
        if (email.isNotEmpty()) {
            getCards(email)
        } else {
            Log.e("UserViewModel", "User email is not available.")
        }
    }

    //新建的method，和下面的不太一样，这个就是更新bankcards而已，没有onclick-什么乱七八糟的，可以直接用这个，真香
    fun getCards(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bankCardsList = userRepository.getAllCardsForUser(email)
                withContext(Dispatchers.Main) {
                    bankCards.clear()
                    bankCards.addAll(bankCardsList)
                }
            } catch (e: Exception) {
                Log.e("GetCards", "Failed to get cards: ${e.message}")
                withContext(Dispatchers.Main) {
                    bankCards.clear()
                }
            }
        }
    }


    fun getPhoneNumber():String{
        return phoneNumber
    }

    fun setPhoneNumber(phone: String) {
        phoneNumber = phone
    }

    fun registerUser(user: User,onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                //get firebase database instance
                val auth = FirebaseAuth.getInstance()
                //print firebase log
                Log.d("FirebaseInit", "Firebase Auth instance obtained: ${FirebaseAuth.getInstance()}")

                //todo register user with more attributes not just email and password

                // Create user with email and password, obtain result object returned by firebase
                val result = auth.createUserWithEmailAndPassword(user.email, user.password).await()

                val currentUser = result.user

                //if current user is not null, execute code block inside let{...}
                currentUser?.let {

                    //set id of user object to the unique ID from firebase authtication result
                    user.id = it.uid

                    // Prepare to write to Firebase Database
                    val database = Firebase.database.reference

                    //Creates a database reference pointing to path users/userId
                    val userRef = database.child("users").child(user.id)

                    // Await the completion of the database write operation
                    userRef.setValue(user).await()

                    //insert User object data into Room
                    userRepository.insertUser(user)

                    loadCurrentUser()

                    withContext(Dispatchers.Main) {

                    onComplete(true)// Indicate success
                    }
                } ?: run {
                    //if Current user is null than register not successful
                    onComplete(false)
                }
            } catch (e: Exception) {

                Log.e("UserRegistration", "Failed to register user: ${e.message}")

                withContext(Dispatchers.Main) {
                    onComplete(false)
                }

            }
        }
    }

    /*
    *   Input: String email , String password
    *   Output: Boolean if login successfully
    *
    * This Method takes email/password and return true if logged.
    *  */
    fun loginUser(email: String, password: String, onComplete: (Boolean) -> Unit) {

        viewModelScope.launch(Dispatchers.IO) {
            try {

                //get firebase instance
                val auth = FirebaseAuth.getInstance()

                //print the firebase log
                Log.d("FirebaseLogin", "Firebase Auth instance obtained for login")

                // Attempt to log in with email and password
                val result = auth.signInWithEmailAndPassword(email, password).await()

                // get user object
                val currentUser = result.user

                if (currentUser != null) {
                    //print user uuid
                    Log.d("FirebaseLogin", "User logged in successfully: ${currentUser.uid}")

                    //val userDetails = User(currentUser.uid, currentUser.email)
                    //sessionManager.createLoginSession(userDetails)

                    // Check if user exists in Room database
                    addUserToRoomDatabase(currentUser) { isSuccess ->
                        if (isSuccess) {
                            Log.d("FirebaseLogin", "User successfully added to Room database")
                            loadCurrentUser()
                            // Continue with your flow, e.g., navigate to another screen
                        } else {
                            Log.e("FirebaseLogin", "Failed to add user to Room database")
                            // Handle the error, show a message, etc.
                        }
                    }
                    withContext(Dispatchers.Main) {
                        onComplete(true) // Indicate success on UI thread
                    }
                } else {
                    //no user received from result
                    Log.d("FirebaseLogin", "Login failed: User is null")
                    withContext(Dispatchers.Main) {
                        onComplete(false) // Indicate failure on UI thread
                    }
                }

            } catch (e: Exception) {
                Log.e("FirebaseLogin", "Failed to login user: ${e.message}")
                withContext(Dispatchers.Main) {
                    onComplete(false) // Indicate failure on UI thread
                }
            }
        }
    }

    fun isEmailRegistered(email: String, callback: (Boolean) -> Unit) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("users")

        // Query the database for the email
        databaseReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // If the snapshot exists, then the email is found
                    callback(true)
                } else {
                    // If the snapshot does not exist, then the email is not found
                    callback(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
                Log.e("FirebaseDB", "Error checking for email: ${error.message}")
                callback(false)
            }
        })
    }

    fun carPlateAlreadyAdded(carPlate:String,callback: (Boolean) -> Unit){
        val databaseReference = FirebaseDatabase.getInstance().getReference("cars")
        // Query the database for the email
        databaseReference.orderByChild("plateId").equalTo(carPlate).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // If the snapshot exists, then the car is found
                    callback(true)
                } else {
                    // If the snapshot does not exist, then the car is not found
                    callback(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
                Log.e("FirebaseDB", "Error checking for email: ${error.message}")
                callback(false)
            }
        })

    }



    fun updateUserToRoomDatabase(userId: String, firstName: String, lastName: String, email: String, phone: String, dateOfBirth: String, password: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userDao = AppDatabase.getDatabase(getApplication<Application>()).userDao()

                val updatedUser = User(id = userId, firstName = firstName, lastName = lastName, email = email, phone = phone, dateOfBirth = dateOfBirth, password = password)

                // Update Firebase
                val firebaseDatabase = FirebaseDatabase.getInstance().reference
                val userUpdates = mapOf(
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "phone" to phone,
                    "dateOfBirth" to dateOfBirth
                )
                firebaseDatabase.child("users").child(userId).updateChildren(userUpdates)
                    .addOnSuccessListener {
                        Log.d(TAG, "User data updated successfully in Firebase.")
                    }
                    .addOnFailureListener {
                        Log.e(TAG, "Failed to update user data in Firebase.", it)
                    }
                userDao.updateUser(updatedUser)

                withContext(Dispatchers.Main) {
                    onComplete(true) // Indicate failure
                }
            } catch (e: Exception) {
                Log.e("UserUpdate", "Error update user to Room database: ${e.message}")
                withContext(Dispatchers.Main) {
                    onComplete(false) // Indicate failure
                }
            }
        }
    }

    fun addUserToRoomDatabase(user: FirebaseUser, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userDao = AppDatabase.getDatabase(getApplication<Application>()).userDao()
                val userEmail = user.email ?: ""  // Handle case where email might be null

                val dbUser = userDao.getUserByEmail(userEmail)

                if (dbUser == null) {
                    Log.d("FirebaseLogin", "User not found in database, adding user")
                    fetchUserDetailsFromFirebase(user.uid) { userDetails ->
                        viewModelScope.launch(Dispatchers.IO) {
                            if (userDetails != null) {
                                userDao.insertUser(userDetails)
                                Log.d("FirebaseLogin", "User added to database")
                                withContext(Dispatchers.Main) {
                                    onComplete(true) // User was added successfully
                                }
                            } else {
                                Log.e("FirebaseLogin", "Failed to fetch user details from Firebase")
                                withContext(Dispatchers.Main) {
                                    onComplete(false) // Failed to add user
                                }
                            }
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onComplete(true) // User already exists, consider this a success
                    }
                }
            } catch (e: Exception) {
                Log.e("FirebaseLogin", "Error adding user to Room database: ${e.message}")
                withContext(Dispatchers.Main) {
                    onComplete(false) // Indicate failure
                }
            }
        }
    }


    //Loads the current user into the user object. To ensure all data about the user in firebase
    // is loaded locally.
    fun loadCurrentUser() {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val userID = currentUser?.uid ?: return // Exit if currentUser is null
        val database = Firebase.database
        val myRef = database.getReference("users").child(userID)

        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val id = dataSnapshot.child("id").value.toString()
                val email = dataSnapshot.child("email").value.toString()
                val password = dataSnapshot.child("password").value.toString()
                val firstName = dataSnapshot.child("firstName").value.toString()
                val lastName = dataSnapshot.child("lastName").value.toString()
                val dateOfBirth = dataSnapshot.child("dateOfBirth").value.toString()
                val phone = dataSnapshot.child("phone").value.toString()

                _user.value = User(id, email, password, firstName, lastName, dateOfBirth, phone)
                Log.d(TAG, "User data updated: ${_user.value}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        }
        myRef.addValueEventListener(listener)
    }


    fun fetchUserDetailsFromFirebase(uid: String, onComplete: (User?) -> Unit) {
        val databaseReference = Firebase.database.getReference("users/$uid")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val id = dataSnapshot.child("id").value.toString()
                val email = dataSnapshot.child("email").value.toString()
                val password = dataSnapshot.child("password").value.toString()
                val firstName = dataSnapshot.child("firstName").value.toString()
                val lastName = dataSnapshot.child("lastName").value.toString()
                //new fetch
                val dateOfBirth = dataSnapshot.child("dateOfBirth").value.toString()

                val phone = dataSnapshot.child("phone").value.toString()

                val user = User(id, email, password, firstName, lastName,dateOfBirth,phone)
                onComplete(user)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseDB", "Failed to read user data: ${error.message}")
                onComplete(null)
            }
        })
    }


    fun addCard(bankCard: BankCard, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val insertResult = userRepository.insertCard(bankCard)
                withContext(Dispatchers.Main) {
                    onComplete(insertResult != -1L)
                }
            } catch (e: Exception) {
                Log.e("AddCard", "Failed to add card: ${e.message}")
                withContext(Dispatchers.Main) {
                    onComplete(false)
                }
            }
        }
    }

    fun getCardList(email: String, onCardsReceived: (List<BankCard>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bankCardsList = userRepository.getAllCardsForUser(email)
                withContext(Dispatchers.Main) {
                    bankCards.clear()
                    bankCards.addAll(bankCardsList)
                    onCardsReceived(bankCards)
                }
            } catch (e: Exception) {
                Log.e("GetCards", "Failed to get cards: ${e.message}")
                withContext(Dispatchers.Main) {
                    bankCards.clear()
                    onCardsReceived(emptyList())
                }
            }
        }
    }

    fun removeCard(card: BankCard, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = userRepository.deleteCard(card)
                withContext(Dispatchers.Main) {
                    if (result > 0) {
                        bankCards.remove(card)
                        onComplete(true)
                    } else {
                        onComplete(false)
                    }
                }
            } catch (e: Exception) {
                Log.e("RemoveCard", "Failed to remove card: ${e.message}")
                withContext(Dispatchers.Main) {
                    onComplete(false)
                }
            }
        }
    }

    fun deleteCar(plateId: String, onComplete: (Boolean) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            onComplete(false) // Handle case where no user is logged in
            return
        }

        // Firebase database reference to "cars"
        val databaseReference = Firebase.database.getReference("cars")
        // Attempt to retrieve the car by plateId
        databaseReference.child(plateId).get().addOnSuccessListener { dataSnapshot ->
            // Parse the snapshot to a Car object
            val car = dataSnapshot.getValue(Car::class.java)
            // Check if car exists and belongs to the current user
            if (car != null && car.ownerEmail == currentUser.email) {
                // Attempt to remove the car
                dataSnapshot.ref.removeValue()
                    .addOnSuccessListener {
                        onComplete(true) // Success callback
                    }
                    .addOnFailureListener { exception ->
                        Log.e("DeleteCar", "Failed to delete car: ${exception.message}")
                        onComplete(false) // Failure callback
                    }
            } else {
                onComplete(false) // Not authorized or car does not exist
            }
        }.addOnFailureListener { exception ->
            Log.e("DeleteCar", "Failed to find car: ${exception.message}")
            onComplete(false) // Error in retrieving car
        }
    }


    fun getUserEmail(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser?.email ?: ""
    }


    fun addCar(
        plateId: String,
        brand: String,
        modelName: String,
        year: Int,
        state: String,
        onComplete: (Boolean) -> Unit
    ) {

        val currentUser = FirebaseAuth.getInstance().currentUser
        val ownerEmail = currentUser?.email ?: return onComplete(false) // No logged-in user

        val car = Car(plateId, brand, state, modelName, year, ownerEmail)

        val databaseReference = Firebase.database.getReference("cars")
        databaseReference.child(plateId).setValue(car)
            .addOnSuccessListener {
                onComplete(true) // Car added successfully
            }
            .addOnFailureListener { e ->
                Log.e("AddCar", "Failed to add car: ${e.message}")
                onComplete(false) // Failed to add car
            }
    }



    // Call this from the UI when appropriate


    //create a empty list
    private val _cars = MutableStateFlow<List<Car>>(emptyList())

    //later fetch locations to this empty list and return
    val cars: StateFlow<List<Car>> = _cars.asStateFlow()

    fun getCarsByUser() {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email
        if (userEmail == null) {
            Log.e("GetCars", "No authenticated user found")
            _cars.value = emptyList()
            return
        }

        // Ensure the database reference is to the root 'cars' node
        val dbRef = FirebaseDatabase.getInstance().getReference("cars")
        dbRef.orderByChild("ownerEmail").equalTo(userEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val cars = snapshot.children.mapNotNull { child ->
                        child.getValue<Car>()
                    }
                    _cars.value = cars
                    Log.d("FirebaseDB", "Fetched cars: ${cars.size}")  // Log the size of fetched cars
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseDB", "Failed to read cars data: ${error.message}")
                    _cars.value = emptyList()
                }
            })
    }

    fun getCarByPlateId(plateId: String, onResult: (Car?) -> Unit) {
        val dbRef = Firebase.database.getReference("cars")
        dbRef.child(plateId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val car = snapshot.getValue(Car::class.java)
                onResult(car)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseDB", "Failed to read car data: ${error.message}")
                onResult(null)
            }
        })
    }

    fun updateCar(car: Car, onComplete: (Boolean) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null || currentUser.email != car.ownerEmail) {
            Log.d("UpdateCar", "Authentication failed or not the owner.")
            onComplete(false) // Not logged in or not the owner of the car
            return
        }

        // Optional: Validate car details before update
        if (car.plateId.isBlank() || car.brand.isBlank() || car.modelName.isBlank() || car.yearMake <= 0) {
            Log.d("UpdateCar", "Invalid car details.")
            onComplete(false)
            return
        }

        val databaseReference = Firebase.database.getReference("cars")
        databaseReference.child(car.plateId).setValue(car)
            .addOnSuccessListener {
                Log.d("UpdateCar", "Car updated successfully for plateId: ${car.plateId}")
                onComplete(true)
            }
            .addOnFailureListener { exception ->
                Log.e("UpdateCar", "Failed to update car: ${exception.message}")
                onComplete(false)
            }
    }


    //Checks if the user is logged in or not, if logged in return ture.
    fun isLoggedIn(): Boolean{
        //load the user object from firebase.
        val currentUser = FirebaseAuth.getInstance().currentUser
        //If user is not logged in, return false, if logged in, return ture.
        return currentUser != null
    }

}