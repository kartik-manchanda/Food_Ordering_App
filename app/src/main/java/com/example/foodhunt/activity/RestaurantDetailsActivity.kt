package com.example.foodhunt.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodhunt.R
import com.example.foodhunt.adapter.DetailsRecyclerAdapter
import com.example.foodhunt.model.Detail
import com.example.foodhunt.util.ConnectionManager
import org.json.JSONException

class RestaurantDetailsActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var recyclerDetail: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: DetailsRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var btnCart: Button
    lateinit var proceedToCart: RelativeLayout


    var restaurantId: String? = "100"
    var restoName: String? = "Haha"

    var detailList = ArrayList<Detail>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_details)

        recyclerDetail = findViewById(R.id.recyclerDetails)
        layoutManager = LinearLayoutManager(this@RestaurantDetailsActivity)
        progressLayout = findViewById(R.id.progressLayout2)
        progressLayout.visibility = View.VISIBLE
        progressBar = findViewById(R.id.progressBar2)
        toolbar = findViewById(R.id.detailToolbar)
        btnCart = findViewById(R.id.btnCart)
        proceedToCart = findViewById(R.id.proceedToCart)

        if (intent != null) {
            restaurantId = intent.getStringExtra("restaurant_id")
            restoName = intent.getStringExtra("restaurant_name")
        } else {
            Toast.makeText(
                this@RestaurantDetailsActivity,
                "Some Error Occurred",
                Toast.LENGTH_SHORT
            ).show()
        }

        if (restaurantId == "100" || restoName == "Haha") {
            Toast.makeText(
                this@RestaurantDetailsActivity,
                "Some Error Occurred",
                Toast.LENGTH_SHORT
            ).show()
        }

        setUpToolbar(restoName)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }


        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId"
        if (ConnectionManager().checkConnectivity(this@RestaurantDetailsActivity)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                    try {
                        progressLayout.visibility = View.GONE
                        val d = it.getJSONObject("data")
                        val success = d.getBoolean("success")

                        if (success) {

                            val data = d.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val restoJsonObject = data.getJSONObject(i)
                                val restoObject = Detail(
                                    restoJsonObject.getString("id"),
                                    restoJsonObject.getString("name"),
                                    restoJsonObject.getString("cost_for_one"),
                                    restoJsonObject.getString("restaurant_id")
                                )
                                detailList.add(restoObject)

                                recyclerAdapter = DetailsRecyclerAdapter(
                                    this@RestaurantDetailsActivity,
                                    detailList,
                                    proceedToCart,
                                    btnCart,
                                    restaurantId,
                                    restoName
                                )
                                recyclerDetail.adapter = recyclerAdapter
                                recyclerDetail.layoutManager = layoutManager
                            }

                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            this@RestaurantDetailsActivity,
                            "Error Occurred",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(
                        this@RestaurantDetailsActivity,
                        "Volley Error $it",
                        Toast.LENGTH_SHORT
                    )
                        .show()
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
                this@RestaurantDetailsActivity,
                "No Internet Connection",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    fun setUpToolbar(name: String?) {

        setSupportActionBar(toolbar)
        supportActionBar?.title = name
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onBackPressed() {

        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Alert!")
        dialog.setMessage("Going back will remove everything from cart")
        dialog.setPositiveButton("Okay") { text, listener ->
            val intent = Intent(this@RestaurantDetailsActivity, MainActivity::class.java)
            startActivity(intent)
        }
        dialog.setNegativeButton("No") { text, listener ->
        }
        dialog.create()
        dialog.show()
    }
}

