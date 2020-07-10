package com.example.foodhunt.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.foodhunt.R
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.albino.restaurantapp.adapter.CartRecyclerAdapter
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodhunt.model.CartItems
import com.example.foodhunt.util.ConnectionManager
import kotlinx.android.synthetic.main.activity_cart.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception

class CartActivity : AppCompatActivity() {

    lateinit var cartToolbar: Toolbar
    lateinit var txtCartText: TextView
    lateinit var recyclerCart: RecyclerView
    lateinit var btnPlaceOrder: Button
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: CartRecyclerAdapter
    var orderList = arrayListOf<String>()
    var restaurantName: String? = "rohit"
    var restaurantId: String? = "11111"
    var totalAmount: Int = 0
    var cartListItems = ArrayList<CartItems>()


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        cartToolbar = findViewById(R.id.cartToolbar)
        txtCartText = findViewById(R.id.txtCartText)
        recyclerCart = findViewById(R.id.recyclerCart)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        progressLayout = findViewById(R.id.progressLayout3)
        progressLayout.visibility = View.VISIBLE
        progressBar = findViewById(R.id.progressBar3)
        layoutManager = LinearLayoutManager(this)

        if (intent != null) {
            restaurantName = intent.getStringExtra("restaurantName")
            restaurantId = intent.getStringExtra("restaurantId")
            orderList = intent.getStringArrayListExtra("selectedItemsId")
        } else {
            Toast.makeText(this, "Some error occurred", Toast.LENGTH_SHORT).show()
        }

        if (restaurantId == "11111" || restaurantName == "rohit" || orderList == null) {
            Toast.makeText(this, "Some error occurred", Toast.LENGTH_SHORT).show()
        }
        setUpToolbar()

        txtCartText.text = "Ordering From: $restaurantName"

        fetchData()

        btnPlaceOrder.setOnClickListener {
            val sharedPreferences = getSharedPreferences(
                getString(R.string.preferences_file_name),
                Context.MODE_PRIVATE
            )



            if (ConnectionManager().checkConnectivity(this@CartActivity)) {
                progressBar3.visibility = View.GONE


                val foodJsonArray = JSONArray()

                for (foodItem in orderList) {
                    val singleItemObject = JSONObject()
                    singleItemObject.put("food_item_id", foodItem)
                    foodJsonArray.put(singleItemObject)

                }
                val queue = Volley.newRequestQueue(this)
                val url = "http://13.235.250.119/v2/place_order/fetch_result/"
                val jsonParams = JSONObject()

                jsonParams.put("user_id", sharedPreferences.getString("user_id", "0"))
                jsonParams.put("restaurant_id", restaurantId.toString())
                jsonParams.put("total_cost", totalAmount.toString())
                jsonParams.put("food", foodJsonArray)


                val jsonObject =
                    object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {

                        try {

                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")

                            if (success) {

                                Toast.makeText(this, "Order Placed", Toast.LENGTH_SHORT).show()

                                val intent = Intent(this, OrderPlacedActivity::class.java)
                                startActivity(intent)
                                finishAffinity()


                            } else {
                                Toast.makeText(
                                    this,
                                    "Unexpected error occurred",
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

                queue.add(jsonObject)


            } else {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Error")
                dialog.setMessage("No Internet Connection Found. \nPlease connect to the internet and \nre-open the app")
                dialog.setNegativeButton("OK") { text, listener ->
                    ActivityCompat.finishAffinity(this)
                }
                dialog.create()
                dialog.show()

            }


        }


    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun fetchData() {

        if (ConnectionManager().checkConnectivity(this)) {

            progressLayout3.visibility = View.VISIBLE

            try {

                val queue = Volley.newRequestQueue(this)

                val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId"

                val jsonObjectRequest = object : JsonObjectRequest(
                    Method.GET,
                    url,
                    null,
                    Response.Listener {

                        val responseJsonObjectData = it.getJSONObject("data")

                        val success = responseJsonObjectData.getBoolean("success")

                        if (success) {

                            val data = responseJsonObjectData.getJSONArray("data")

                            //old listener of jsonObjectRequest are still listening therefore clear is used
                            cartListItems.clear()//clear all items to get updated values

                            totalAmount = 0

                            for (i in 0 until data.length()) {
                                val cartItemJsonObject = data.getJSONObject(i)

                                if (orderList.contains(cartItemJsonObject.getString("id")))//if the fetched id is present in the selected id save
                                {

                                    val menuObject = CartItems(
                                        cartItemJsonObject.getString("id"),
                                        cartItemJsonObject.getString("name"),
                                        cartItemJsonObject.getString("cost_for_one"),
                                        cartItemJsonObject.getString("restaurant_id")
                                    )

                                    totalAmount += cartItemJsonObject.getString("cost_for_one")
                                        .toString().toInt()


                                    cartListItems.add(menuObject)

                                }

                                recyclerAdapter = CartRecyclerAdapter(
                                    this,
                                    cartListItems
                                )

                                recyclerCart.adapter =
                                    recyclerAdapter

                                recyclerCart.layoutManager =
                                    layoutManager
                            }

                            //set the total on the button
                            btnPlaceOrder.text = "Place Order(Total:Rs. " + totalAmount + ")"

                        }
                        progressLayout3.visibility = View.INVISIBLE
                    },
                    Response.ErrorListener {

                        Toast.makeText(
                            this,
                            "Some Error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()

                        progressLayout3.visibility = View.INVISIBLE

                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()

                        headers["Content-type"] = "application/json"
                        headers["token"] = "ba8f4327609460"

                        return headers
                    }
                }

                queue.add(jsonObjectRequest)

            } catch (e: JSONException) {
                Toast.makeText(
                    this,
                    "Some Unexpected error occurred!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {

            val alterDialog = AlertDialog.Builder(this)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Internet Connection can't be establish!")
            alterDialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)//open wifi settings
                startActivity(settingsIntent)
            }

            alterDialog.setNegativeButton("Exit") { text, listener ->
                finishAffinity()//closes all the instances of the app and the app closes completely
            }

            alterDialog.create()
            alterDialog.show()

        }

    }

    fun setUpToolbar() {

        setSupportActionBar(cartToolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

}
