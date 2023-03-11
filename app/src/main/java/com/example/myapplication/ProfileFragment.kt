package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.firebase.database.*

class ProfileFragment : Fragment() {
    private lateinit var email: String

    fun setEmail(emailFromHome: String) {
        email = emailFromHome
        Log.i("frag", email)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Here you can access your views and add your logic
        val deleteAccButt = view.findViewById<Button>(R.id.deleteAccButton)
        val database = FirebaseDatabase.getInstance().reference
        //If user presses delete account button on profile page
        //The database removes all data about the user
        //D from CRUD
        deleteAccButt.setOnClickListener {
            Log.i("delete", "delete account button clicked")
            val query: Query = database.child("emails").orderByChild("email").equalTo(email)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // User exists in the database
                        val userNode = dataSnapshot.children.first()
                        // Delete the user node using the DatabaseReference object
                        userNode.ref.removeValue()
                        // Show success message
                        Toast.makeText(context, "User deleted successfully", Toast.LENGTH_SHORT).show()
                        val loginIntent= Intent(requireContext(), LoginActivity::class.java)
                        startActivity(loginIntent)
                    } else {
                        // User doesn't exist in the database
                        Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                    Log.e("FIREBASE", "Error deleting user: ${error.message}")
                    Toast.makeText(context, "Error deleting user: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })

        }
    }

}