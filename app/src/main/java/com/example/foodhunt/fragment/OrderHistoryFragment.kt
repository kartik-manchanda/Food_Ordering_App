package com.example.foodhunt.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.example.foodhunt.R
import com.example.foodhunt.adapter.OrderHistoryAdapter
import com.example.foodhunt.model.OrderHistory
import com.example.foodhunt.util.ConnectionManager
import java.lang.Exception


class OrderHistoryFragment : Fragment() {

    lateinit var progressLayout: RelativeLayout
    lateinit var recyclerHistory: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: OrderHistoryAdapter
    lateinit var sharedPreferences: SharedPreferences
    lateinit var noOrderPlaced: RelativeLayout
    var orderHistoryList = ArrayList<OrderHistory>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)
        progressLayout = view.findViewById(R.id.progressLayout4)
        progressLayout.visibility = View.VISIBLE
        recyclerHistory = view.findViewById(R.id.recyclerHistory)
        layoutManager = LinearLayoutManager(activity as Context)
        noOrderPlaced = view.findViewById(R.id.no_order_placed)

        sharedPreferences =
            activity!!.getSharedPreferences(
                getString(R.string.preferences_file_name),
                Context.MODE_PRIVATE
            )

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/orders/fetch_result/" + sharedPreferences.getString(
            "user_id",
            "0"
        )
        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                    try {
                        val response = it.getJSONObject("data")
                        val success = response.getBoolean("success")
                        if (success) {
                            progressLayout.visibility = View.GONE
                            val data = response.getJSONArray("data")
                            if (data.length() == 0) {//no items present display toast

                                Toast.makeText(
                                    activity as Context,
                                    "No Orders Placed yet!!!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                noOrderPlaced.visibility = View.VISIBLE

                            } else {
                                noOrderPlaced.visibility = View.INVISIBLE
                                for (i in 0 until data.length()) {
                                    val foodJsonObject = data.getJSONObject(i)
                                    val foodObject = OrderHistory(
                                        foodJsonObject.getString("order_id"),
                                        foodJsonObject.getString("restaurant_name"),
                                        foodJsonObject.getString("total_cost"),
                                        foodJsonObject.getString("order_placed_at").substring(0, 9)
                                    )
                                    orderHistoryList.add(foodObject)
                                    recyclerAdapter =
                                        OrderHistoryAdapter(activity as Context, orderHistoryList)
                                    recyclerHistory.adapter = recyclerAdapter
                                    recyclerHistory.layoutManager = layoutManager
                                }
                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Error Occurred",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                    } catch (e: Exception) {
                        Toast.makeText(
                            activity as Context,
                            "Error Occurred $e",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(
                        activity as Context,
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
            Toast.makeText(activity as Context, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }
        return view
    }

}

