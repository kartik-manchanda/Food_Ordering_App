package com.example.foodhunt.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodhunt.R
import com.example.foodhunt.util.ConnectionManager
import org.json.JSONObject
import java.lang.Exception

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var etForgotMobileNo: EditText
    lateinit var etForgotEmail: EditText
    lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        etForgotMobileNo = findViewById(R.id.etForgotMobileNo)
        etForgotEmail = findViewById(R.id.etForgotEmail)
        btnNext = findViewById(R.id.btnNext)

        btnNext.setOnClickListener {
            if (etForgotMobileNo.text.isNullOrEmpty() || etForgotMobileNo.text.length < 10) {
                etForgotMobileNo.error = "Invalid Mobile Number"
            } else if (etForgotEmail.text.isNullOrEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(
                    etForgotEmail.text
                ).matches()
            ) {
                etForgotEmail.error = "Invalid Email"
            } else {

                val queue = Volley.newRequestQueue(this)
                val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", etForgotMobileNo.text.toString())
                jsonParams.put("email", etForgotEmail.text.toString())

                if (ConnectionManager().checkConnectivity(this)) {
                    val jsonObjectRequest =
                        object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {

                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success) {

                                    val firstTry = data.getBoolean("first_try")
                                    if (!firstTry) {
                                        val dialog = AlertDialog.Builder(this)
                                        dialog.setTitle("Information")
                                        dialog.setMessage("Please refer to the previous email for the OTP")
                                        dialog.setPositiveButton("OK") { text, listener ->
                                            val intent = Intent(this, OTPActivity::class.java)
                                            intent.putExtra(
                                                "mobile_number",
                                                etForgotMobileNo.text.toString()
                                            )
                                            startActivity(intent)
                                        }
                                        dialog.create()
                                        dialog.show()
                                    } else {

                                        val intent = Intent(this, OTPActivity::class.java)
                                        intent.putExtra(
                                            "mobile_number",
                                            etForgotMobileNo.text.toString()
                                        )
                                        startActivity(intent)
                                    }

                                } else {
                                    Toast.makeText(
                                        this,
                                        "Mobile Number not Registered",
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

