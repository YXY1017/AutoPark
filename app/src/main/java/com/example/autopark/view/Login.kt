package com.example.autopark.view

import UserViewModel
import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

import androidx.navigation.NavController
import com.example.autopark.R
import com.example.autopark.Routes

import com.example.autopark.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


//The main screen of the app.
@Composable
fun LoginScreen(navController: NavController, userViewModel: UserViewModel) {
    // Context handling in Compose
    val context = LocalContext.current
    val activity = context as? Activity ?: return // Safely cast to Activity or return if not possible

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var isSigningUp by remember { mutableStateOf(false) }
    val loading by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf("") }

    var loginErrorMessage by remember { mutableStateOf("") }
    var examplePhoneMessage by remember { mutableStateOf("") }


    Column(modifier = Modifier.fillMaxSize()) {
        // Auto-Park Text Icon
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 50.dp)
        ) {
            Image(
                modifier = Modifier
                    .size(120.dp)
                    .padding(10.dp),
                painter = painterResource(id = R.drawable.icon_no_bg),
                contentDescription = "App Icon",
                contentScale = ContentScale.Fit
            )
        }

        Column(
            modifier = Modifier
                .weight(2f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (!isSigningUp) { // Login
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        userViewModel.loginUser(email, password) { success ->
                            if (success) {
                                navController.navigate(Routes.Home.value)
                            } else {
                                loginErrorMessage = "Login Failed: Incorrect password or email"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !loading
                ) {
                    Text("Log In")
                }

                if (loginErrorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(loginErrorMessage, color = MaterialTheme.colorScheme.error)
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                // Google sign in
                val signInLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                        task.addOnSuccessListener { googleAccount ->
                            val credential = GoogleAuthProvider.getCredential(googleAccount.idToken, null)
                            FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { authTask ->
                                if (authTask.isSuccessful) {
                                    // Handle successful login
                                    val firebaseUser = authTask.result?.user
                                    firebaseUser?.let {
                                        checkAndAddUserToDatabase(userViewModel, it)  // Check and add user to database
                                        userViewModel.loadCurrentUser()
                                    }
                                    navController.navigate(Routes.Home.value)
                                } else {
                                    // Handle failed login
                                    loginError = "Firebase Authentication failed with error: ${authTask.exception?.message}"
                                    Log.e("LoginScreen", "Firebase auth failed", authTask.exception)
                                }
                            }
                        }.addOnFailureListener {
                            // Handle sign in failure
                            loginError = "Google Sign-In failed with error: ${it.message}"
                            Log.e("LoginScreen", "Google sign-in failed", it)
                        }
                    }
                        else {
                            loginError = "Google Sign-In failed with resultCode: ${result.resultCode}"
                            Log.e("LoginScreen", "Google sign-in failed with resultCode: ${result.resultCode}")
                        }
                }
                Button(onClick = {
                    val signInClient = setupGoogleSignIn(activity)
                    val signInIntent = signInClient.signInIntent
                    signInLauncher.launch(signInIntent)
                }) {
                    Text("Sign In with Google")
                }

                if (loginError.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(loginError, color = MaterialTheme.colorScheme.error)
                }
            } else{ // Sign up
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {
                        phoneNumber = it
                    },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {//create a user with phone numebr?
                        if (phoneNumber.isNotEmpty() && phoneNumber.length == 10) { //check phone number length/ pure number
                            userViewModel.setPhoneNumber(phoneNumber)
                            navController.navigate(Routes.Register.value)
                        } else {
                            loginErrorMessage = "Please enter a valid phone number."
                            examplePhoneMessage =" eg.0404352260 (10 Digits) "
                        } },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text("Sign Up")
                }

                if (loginErrorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(loginErrorMessage, color = MaterialTheme.colorScheme.error)
                    Text(examplePhoneMessage, color = MaterialTheme.colorScheme.error)
                }

            }

            Spacer(modifier = Modifier.height(24.dp))
            TextButton(
                onClick = { isSigningUp = !isSigningUp }
            ) {
                Text(if (isSigningUp) "Already have an account? Log In" else "Don't have an account? Sign Up")
            }
        }
    }
}

fun setupGoogleSignIn(activity: Activity): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("961081358966-s8ae6a52r6rgotc1adfl6ioeaugjm8lo.apps.googleusercontent.com")  // Replace with your actual web client ID
        .requestEmail()
        .build()

    return GoogleSignIn.getClient(activity, gso)
}

// For add google login user into firebase realtime database
fun checkAndAddUserToDatabase(userViewModel: UserViewModel, user: FirebaseUser) {
    val userRef = Firebase.database.getReference("users/${user.uid}")

    userRef.get().addOnSuccessListener { dataSnapshot ->
        if (!dataSnapshot.exists()) {
            // User does not exist, add them
            val newUser = User(
                id = user.uid,
                firstName = user.displayName.toString(),
                lastName = "",
                email = user.email.toString(),
                password = "",
                phone = "",
                dateOfBirth = ""
            )
            userRef.setValue(newUser).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // User added successfully
                    userViewModel.addUserToRoomDatabase(user) { isSuccess ->
                        if (isSuccess) {
                            Log.d("GoogleLogin", "User successfully added to Room database")
                            // Continue with your flow, e.g., navigate to another screen
                        } else {
                            Log.e("GoogleLogin", "Failed to add user to Room database")
                            // Handle the error, show a message, etc.
                        }
                    }

                } else {
                    // Handle failure
                }
            }
        } else {
            // User already exists in the database
            // Check if user in room database
            userViewModel.addUserToRoomDatabase(user) { isSuccess ->
                if (isSuccess) {
                    Log.d("FirebaseLogin", "User successfully added to Room database")
                    // Continue with your flow, e.g., navigate to another screen
                } else {
                    Log.e("FirebaseLogin", "Failed to add user to Room database")
                    // Handle the error, show a message, etc.
                }
            }
        }
    }.addOnFailureListener {
        // Handle failure to check user
    }
}
