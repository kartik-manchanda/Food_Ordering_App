package com.example.foodhunt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.albino.restaurantapp.adapter.CartRecyclerAdapter
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodhunt.R
import com.example.foodhunt.model.CartItems
import com.example.foodhunt.model.OrderHistory
import com.example.foodhunt.util.ConnectionManager
import org.json.JSONException

class OrderHistoryAdapter(val context: Context, val orderHistoryList: ArrayList<OrderHistory>) :
    RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder>() {

    class OrderHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtHistResName: TextView = view.findViewById(R.id.txtHistResName)
        var txtHistResDate: TextView = view.findViewById(R.id.txtHistDate)
        var recyclerItemsOrdered: RecyclerView = view.findViewById(R.id.recyclerItemsOrdered)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_order_hist_single_row, parent, false)
        return OrderHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderHistoryList.size
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        val foodList = orderHistoryList[position]
        holder.txtHistResName.text = foodList.restaurantName
        var formatDate = foodList.orderPlacesAt
        formatDate = formatDate.replace("-", "/")//21-02-20 to 21/02/20
        formatDate =
            formatDate.substring(0, 6) + "20" + formatDate.substring(6, 8)//21/02/20 to 21/02/2020
        holder.txtHistResDate.text = formatDate


        var layoutManager = LinearLayoutManager(context)
        var orderedItemAdapter: CartRecyclerAdapter// the same cart adapter can be used to fill the ordered items for each restaurant

        if (ConnectionManager().checkConnectivity(context)) {

            try {

                val orderItemsPerRestaurant = ArrayList<CartItems>()

                val sharedPreferencess = context.getSharedPreferences(
                    context.getString(R.string.preferences_file_name),
                    Context.MODE_PRIVATE
                )

                val user_id = sharedPreferencess.getString("user_id", "0")

                val queue = Volley.newRequestQueue(context)

                val url = "http://13.235.250.119/v2/orders/fetch_result/" + user_id

                val jsonObjectRequest = object : JsonObjectRequest(
                    Method.GET,
                    url,
                    null,
                    Response.Listener {

                        val responseJsonObjectData = it.getJSONObject("data")

                        val success = responseJsonObjectData.getBoolean("success")

                        if (success) {

                            val data = responseJsonObjectData.getJSONArray("data")

                            val fetechedRestaurantJsonObject =
                                data.getJSONObject(position)//restaurant at index of position

                            orderItemsPerRestaurant.clear()

                            val foodOrderedJsonArray =
                                fetechedRestaurantJsonObject.getJSONArray("food_items")

                            for (j in 0 until foodOrderedJsonArray.length())//loop through all the items
                            {
                                val eachFoodItem =
                                    foodOrderedJsonArray.getJSONObject(j)//each food item
                                val itemObject = CartItems(
                                    eachFoodItem.getString("food_item_id"),
                                    eachFoodItem.getString("name"),
                                    eachFoodItem.getString("cost"),
                                    "000"//we dont save restaurant id
                                )

                                orderItemsPerRestaurant.add(itemObject)

                            }

                            orderedItemAdapter = CartRecyclerAdapter(
                                context,//pass the relativelayout which has the button to enable it later
                                orderItemsPerRestaurant
                            )//set the adapter with the data

                            holder.recyclerItemsOrdered.adapter =
                                orderedItemAdapter//bind the  recyclerView to the adapter

                            holder.recyclerItemsOrdered.layoutManager =
                                layoutManager //bind the  recyclerView to the layoutManager


                        }
                    },
                    Response.ErrorListener {

                        Toast.makeText(
                            context,
                            "Some Error occurred!!!",
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

                queue.add(jsonObjectRequest)

            } catch (e: JSONException) {
                Toast.makeText(
                    context,
                    "Some Unexpected error occured!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

    }
}