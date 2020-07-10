package com.example.foodhunt.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodhunt.R
import com.example.foodhunt.util.ConnectionManager
import org.json.JSONObject
import java.lang.Exception

class OTPActivity : AppCompatActivity() {
    lateinit var etOtp: EditText
    lateinit var etOtpNewPass: EditText
    lateinit var etOtpConfirmPass: EditText
    lateinit var btnSubmit: Button
    lateinit var sharedPreferences: SharedPreferences
    var mobileNumber: String? = "haha"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        etOtp = findViewById(R.id.etOtp)
        etOtpNewPass = findViewById(R.id.etOtpNewPass)
        etOtpConfirmPass = findViewById(R.id.etOtpConfirmPass)
        btnSubmit = findViewById(R.id.btnSubmit)
        sharedPreferences =
            getSharedPreferences(getString(R.string.preferences_file_name), Context.MODE_PRIVATE)

        if (intent != null) {
            mobileNumber = intent.getStringExtra("mobile_number")
        }

        btnSubmit.setOnClickListener {

            if (etOtp.text.isNullOrEmpty() || etOtp.text.length < 4) {
                etOtp.error = "Invalid OTP"
            } else if (etOtpNewPass.text.isNullOrEmpty()) {
                etOtpNewPass.error = "Invalid Password"
            } else if (etOtpNewPass.text.length < 7) {
                etOtpNewPass.error = "Password should be greater than or equal to 7 characters"
            } else if (etOtpConfirmPass.text.isNullOrEmpty() || etOtpNewPass.text.toString() != etOtpConfirmPass.text.toString()) {
                etOtpConfirmPass.error = "Password not matched"
            } else {
                val queue = Volley.newRequestQueue(this)
                val url = "http://13.235.250.119/v2/reset_password/fetch_result"
                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", mobileNumber.toString())
                jsonParams.put("password", etOtpConfirmPass.text.toString())
                jsonParams.put("otp", etOtp.text.toString())

                if (ConnectionManager().checkConnectivity(this)) {
                    val jsonObjectRequest =
                        object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {

                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success) {

                                    sharedPreferences.edit().clear().apply()
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)

                                } else {
                                    Toast.makeText(
                                        this,
                                        "Invalid OTP",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(this, "Error $e", Toast.LENGTH_SHORT).show()
                            }
                        }, Response.ErrorListener {
                            Toast.makeText(this, "Volley Error", Toast.LENGTH_SHORT).show()
                        }) {
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String, String>()
                                headers["Content-type"] = "application/json"
                                headers["token"] = "ba8f4327609460"
                                return headers
                            }
                        }
                    queue.add(jsonObjectRequest)
                } else {
                    Toast.makeText(
                        this,
                        "No Internet Connection",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
    }
}
