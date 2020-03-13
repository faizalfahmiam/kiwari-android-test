package com.faizal.simplechat

import android.app.Activity
import android.content.Intent
import android.text.format.DateFormat
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.database.FirebaseListAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private var adapter: FirebaseListAdapter<ChatMessage>? = null
    private var SIGN_IN_REQUEST_CODE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar?.inflateMenu(R.menu.main_menu)

        var name =  findViewById<TextView>(R.id.name)
        var img =  findViewById<ImageView>(R.id.img)
        if (FirebaseAuth.getInstance().currentUser == null) {
            // Start sign in/sign up activity
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .build(),
                SIGN_IN_REQUEST_CODE
            )
        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast
            Toast.makeText(
                this,
                "Welcome " + FirebaseAuth.getInstance()
                    .currentUser!!
                    .displayName!!,
                Toast.LENGTH_LONG
            )
                .show()
            if( FirebaseAuth.getInstance().currentUser?.email == "jarjit@mail.com"){
                name.text = FirebaseAuth.getInstance().currentUser?.displayName
                Glide.with(this).load("https://api.adorable.io/avatars/160/jarjit@mail.com.png").into(img)
            }
            if( FirebaseAuth.getInstance().currentUser?.email == "ismail@mail.com"){
                name.text = FirebaseAuth.getInstance().currentUser?.displayName
                Glide.with(this).load("https://api.adorable.io/avatars/160/ismail@mail.com.png").into(img)
            }

            // Load chat room contents
            displayChatMessages()
        }
        val fab = findViewById<View>(R.id.fab) as Button
        fab.setOnClickListener {
            val input = findViewById<View>(R.id.input) as EditText

            // Read the input field and push a new instance
            // of ChatMessage to the Firebase database
            FirebaseDatabase.getInstance()
                .reference
                .push()
                .setValue(
                    FirebaseAuth.getInstance()
                        .currentUser!!
                        .displayName?.let { it1 ->
                        ChatMessage(
                            input.text.toString(),
                            it1
                        )
                    }
                )

            // Clear the input
            input.setText("")
        }

        toolbar.setOnMenuItemClickListener(object : Toolbar.OnMenuItemClickListener {

            override fun onMenuItemClick(item: MenuItem): Boolean {
                if (item.itemId == R.id.menu_sign_out) {
                    AuthUI.getInstance().signOut(this@MainActivity)
                        .addOnCompleteListener {
                            Toast.makeText(
                                this@MainActivity,
                                "You have been signed out.",
                                Toast.LENGTH_LONG
                            )
                                .show()

                            // Close activity
                            finish()
                        }
                }

                return false
            }
        })
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(
                    this,
                    "Successfully signed in. Welcome!",
                    Toast.LENGTH_LONG
                )
                    .show()
                displayChatMessages()
            } else {
                Toast.makeText(
                    this,
                    "We couldn't sign you in. Please try again later.",
                    Toast.LENGTH_LONG
                )
                    .show()

                // Close the app
                finish()
            }
        }

    }

    private fun displayChatMessages() {
        val listOfMessages = findViewById<View>(R.id.list_of_messages) as ListView

        adapter = object : FirebaseListAdapter<ChatMessage>(
            this, ChatMessage::class.java,
            R.layout.message, FirebaseDatabase.getInstance().reference
        ) {
            override fun populateView(v: View, model: ChatMessage, position: Int) {
                // Get references to the views of message.xml
                val messageText = v.findViewById<View>(R.id.message_text) as TextView
                val messageUser = v.findViewById<View>(R.id.message_user) as TextView
                val messageTime = v.findViewById<View>(R.id.message_time) as TextView

                // Set their text
                messageText.text = model.messageText
                messageUser.text = model.messageUser

                // Format the date before showing it
                messageTime.text = DateFormat.format(
                    "dd-MM-yyyy (HH:mm:ss)",
                    model.messageTime
                )
            }
        }

        listOfMessages.adapter = adapter
    }
}
