package com.example.foodhunt.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.example.foodhunt.R

/**
 * A simple [Fragment] subclass.
 */
class MyProfileFragment : Fragment() {

    lateinit var txtUserName: TextView
    lateinit var txtUserMobile: TextView
    lateinit var txtUserEmail: TextView
    lateinit var txtUserAddress: TextView
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_profile, container, false)

        txtUserName = view.findViewById(R.id.txtUserName)
        txtUserMobile = view.findViewById(R.id.txtUserMobile)
        txtUserEmail = view.findViewById(R.id.txtUserEmail)
        txtUserAddress = view.findViewById(R.id.txtUserAddress)

        sharedPreferences =
            activity!!.getSharedPreferences(
                getString(R.string.preferences_file_name),
                Context.MODE_PRIVATE
            )

        txtUserName.text = sharedPreferences.getString("name", "name")
        txtUserMobile.text = sharedPreferences.getString("mobile_number", "mobile_number")
        txtUserEmail.text = sharedPreferences.getString("email", "email")
        txtUserAddress.text = sharedPreferences.getString("address", "address")



        return view
    }

}
