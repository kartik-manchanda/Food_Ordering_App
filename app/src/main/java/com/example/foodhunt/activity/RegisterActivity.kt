package com.example.foodhunt.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodhunt.R
import org.json.JSONObject
import java.lang.Exception

class RegisterActivity : AppCompatActivity() {

    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etRegMobileNo: EditText
    lateinit var etAddress: EditText
    lateinit var etRegPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btnRegister: Button
    lateinit var sharedPreferences: SharedPreferences
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etRegMobileNo = findViewById(R.id.etRegMobileNo)
        etAddress = findViewById(R.id.etAddress)
        etRegPassword = findViewById(R.id.etRegPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        toolbar = findViewById(R.id.regToolbar)

        setUpToolbar()

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        sharedPreferences =
            getSharedPreferences(getString(R.string.preferences_file_name), Context.MODE_PRIVATE)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnRegister.setOnClickListener {
            if (etName.text.isNullOrEmpty() || (etName.text.length < 3)) {
                etName.error = "Invalid Name"
                return@setOnClickListener
            } else if (etEmail.text.isNullOrEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(
                    etEmail.text
                ).matches()
            ) {
                etEmail.error = "Invalid Email"
                return@setOnClickListener
            } else if (etRegMobileNo.text.isNullOrEmpty() || (etRegMobileNo.text.length < 10)) {
                etRegMobileNo.error = "Invalid Mobile Number"
                return@setOnClickListener
            } else if (etAddress.text.isNullOrEmpty()) {
                etAddress.error = "Invalid Address"
                return@setOnClickListener
            } else if (etRegPassword.text.isNullOrEmpty()) {
                etRegPassword.error = "Invalid Password"
                return@setOnClickListener
            } else if (etRegPassword.text.length < 4) {
                etRegPassword.error = "Password should be more than or equal to 4 digits"
                return@setOnClickListener
            } else if ((etConfirmPassword.text.toString() != etRegPassword.text.toString()) || etConfirmPassword.text.isNullOrEmpty()) {
                etConfirmPassword.error = "Password don't match"
                return@setOnClickListener
            } else {


                val queue = Volley.newRequestQueue(this@RegisterActivity)
                val url = "http://13.235.250.119/v2/register/fetch_result"

                val jsonParams = JSONObject()
                jsonParams.put("name", etName.text.toString())
                jsonParams.put("mobile_number", etRegMobileNo.text.toString())
                jsonParams.put("password", etRegPassword.text.toString())
                jsonParams.put("address", etAddress.text.toString())
                jsonParams.put("email", etEmail.text.toString())


                val jsonRequest =
                    object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {

                        try {
                            val d = it.getJSONObject("data")
                            val success = d.getBoolean("success")
                            Toast.makeText(this@RegisterActivity, "$success", Toast.LENGTH_SHORT)
                                .show()
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

                                sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()

                                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Some Error Occurred",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }

                        } catch (e: Exception) {
                            Toast.makeText(this@RegisterActivity, "Error $e", Toast.LENGTH_SHORT)
                                .show()
                        }


                    }, Response.ErrorListener {
                        Toast.makeText(
                            this@RegisterActivity,
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

    fun setUpToolbar() {

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onBackPressed() {
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(intent)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}
