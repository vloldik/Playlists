package ru.vladik.playlists.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import ru.vladik.playlists.R
import ru.vladik.playlists.api.playlists.User
import ru.vladik.playlists.dataclasses.musicservises.VkMusicService
import ru.vladik.playlists.extensions.UserExtensions.exists
import ru.vladik.playlists.sqlite.PlatformRegistrationSqlHelper
import ru.vladik.playlists.utils.*


class LoginActivity : AppCompatActivity() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mGoogleSignInOptions: GoogleSignInOptions
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var signInButton: SignInButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        firebaseAuth = FirebaseAuth.getInstance()
        signInButton = findViewById(R.id.login_with_google)

        if (firebaseAuth.currentUser == null) {
            signInButton.visibility = View.VISIBLE
            setupSignIn()
            setupUI()
        } else {
            prepareForStartMain(firebaseAuth.currentUser)
        }


    }

    private fun setupSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.firebase_web_cl_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }



    private fun setupUI() {
        signInButton.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }



    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account)

        } catch (e: ApiException) {
            e.printStackTrace()
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener{
            Log.d("main", "success")

            if (it.isSuccessful) {
                prepareForStartMain(firebaseAuth.currentUser)
            } else {
                Log.d("main", "failed")
            }
        }
    }

    private fun prepareForStartMain(firebaseUser: FirebaseUser?) {

        FirebaseUtil.getUser(firebaseUser?.uid.toString()).addOnCompleteListener {

            var user = it.result.getValue(User::class.java)
            if (user == null) {
                user = User(id = firebaseUser?.uid.toString(),
                    mail = firebaseUser?.email.toString(), photoUrl = firebaseUser?.photoUrl.toString(),
                    name = firebaseUser?.displayName.toString())
                FirebaseUtil.setUser(user)
            }
            if (user.exists()) {
                val vkToken = PlatformRegistrationSqlHelper(this).getServiceRegistrationInfo(VkMusicService)?.token
                if (vkToken != null) {
                    LoginUtil.vkLogIn(this, vkToken)
                    startMainAndFinish()
                } else {
                    startMainAndFinish()
                }
            }
        }
    }

    private fun startMainAndFinish() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}