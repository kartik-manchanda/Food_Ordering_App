package com.example.foodhunt.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodhunt.R
import org.json.JSONObject
import java.lang.Exception

class LoginActivity : AppCompatActivity() {

    lateinit var etMobileNo: EditText
    lateinit var etPassword: EditText
    lateinit var btnLogin: Button
    lateinit var txtForgotPassword: TextView
    lateinit var txtRegister: TextView
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etMobileNo = findViewById(R.id.etMobileNo)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        txtRegister = findViewById(R.id.txtRegister)

        txtRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        txtForgotPassword.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }



        sharedPreferences =
            getSharedPreferences(getString(R.string.preferences_file_name), Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnLogin.setOnClickListener {
            if (etMobileNo.text.isNullOrEmpty() || etPassword.text.isNullOrEmpty() || etMobileNo.text.length < 10 || etPassword.text.length < 4) {
                Toast.makeText(
                    this@LoginActivity,
                    "Invalid Mobile Number or Password",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                val queue = Volley.newRequestQueue(this@LoginActivity)
                val url = "http://13.235.250.119/v2/login/fetch_result"

                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", etMobileNo.text.toString())
                jsonParams.put("password", etPassword.text.toString())

                val jsonRequest =
                    object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {

                        try {
                            val d = it.getJSONObject("data")
                            val success = d.getBoolean("success")
                            if (success) {
                                sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()

                                val data = d.getJSONObject("data")
                                sharedPreferences.edit()
                                    .putString("user_id", data.getString("user_id")).apply()
                                sharedPreferences.edit().putString("name", data.getString("name"))
                                    .apply()
                                sharedPreferences.edit().putString("email", data.getString("email"))
                                    .apply()
                                sharedPreferences.edit()
                                    .putString("mobile_number", data.getString("mobile_number"))
                                    .apply()
                                sharedPreferences.edit()
                                    .putString("address", data.getString("address")).apply()

                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Mobile Number not Registered",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }

                        } catch (e: Exception) {
                            Toast.makeText(this@LoginActivity, "Error $e", Toast.LENGTH_SHORT)
                                .show()
                        }


                    }, Response.ErrorListener {
                        Toast.makeText(
                            this@LoginActivity,
                            "Volley Error $it",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "ba8f4327609460"
                            return headers
                        }
                    }

                queue.add(jsonRequest)

            }
        }

    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onBackPressed() {
        finishAffinity()
    }
}
