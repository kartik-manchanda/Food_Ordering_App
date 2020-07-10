package com.example.foodhunt.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.example.foodhunt.R
import com.example.foodhunt.adapter.HomeRecyclerAdapter
import com.example.foodhunt.database.RestaurantDatabase
import com.example.foodhunt.database.RestaurantEntity
import com.example.foodhunt.model.Restaurants
import com.example.foodhunt.util.ConnectionManager
import kotlinx.android.synthetic.main.sort_radio_button.view.*
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class HomeFragment : Fragment() {

    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var radioButtonView: View
    lateinit var etSearch: EditText


    var restaurantList = ArrayList<Restaurants>()

    var ratingComparator = Comparator<Restaurants> { rest1, rest2 ->

        if (rest1.restaurantRating.compareTo(rest2.restaurantRating, true) == 0) {
            rest1.restaurantName.compareTo(rest2.restaurantName, true)
        } else {
            rest1.restaurantRating.compareTo(rest2.restaurantRating, true)
        }

    }

    var costComparator = Comparator<Restaurants> { rest1, rest2 ->

        rest1.costForOne.compareTo(rest2.costForOne, true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        setHasOptionsMenu(true)

        recyclerHome = view.findViewById(R.id.recyclerHome)
        layoutManager = LinearLayoutManager(activity)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE
        progressBar = view.findViewById(R.id.progressBar)
        etSearch = view.findViewById(R.id.etSearch)

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener {


                    try {
                        val d = it.getJSONObject("data")
                        val success = d.getBoolean("success")

                        if (success) {

                            progressLayout.visibility = View.GONE
                            val data = d.getJSONArray("data")
                            for (i in 0 until data.length()) {

                                val restaurantJsonObject = data.getJSONObject(i)

                                val restaurantObject = Restaurants(
                                    restaurantJsonObject.getString("id"),
                                    restaurantJsonObject.getString("name"),
                                    restaurantJsonObject.getString("cost_for_one"),
                                    restaurantJsonObject.getString("rating"),
                                    restaurantJsonObject.getString("image_url")
                                )
                                restaurantList.add(restaurantObject)
                                recyclerAdapter =
                                    HomeRecyclerAdapter(activity as Context, restaurantList)

                                recyclerHome.adapter = recyclerAdapter
                                recyclerHome.layoutManager = layoutManager

                            }

                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some Error Occurred",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "$e Error Occurred ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }


                }, Response.ErrorListener {
                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Volley Error Occurred $it",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Context-type"] = "application/json"
                        headers["token"] = "ba8f4327609460"
                        return headers
                    }
                }
            queue.add(jsonObjectRequest)
        } else {
            //internet not available

            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("No Internet Connection Found. \nPlease connect to the internet and \nre-open the app")
            dialog.setNegativeButton("OK") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }

        fun filterFun(strTyped: String) {//to filter the recycler view depending on what is typed
            val filteredList = arrayListOf<Restaurants>()

            for (item in restaurantList) {
                if (item.restaurantName.toLowerCase()
                        .contains(strTyped.toLowerCase())
                ) {//to ignore case and if contained add to new list

                    filteredList.add(item)

                }
            }

            recyclerAdapter.filterList(filteredList)

        }

        etSearch.addTextChangedListener(object : TextWatcher {
            //as the user types the search filter is applied
            override fun afterTextChanged(strTyped: Editable?) {
                filterFun(strTyped.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        }
        )


        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        when (id) {

            R.id.sort -> {
                radioButtonView = View.inflate(
                    activity as Context,
                    R.layout.sort_radio_button,
                    null
                )//radiobutton view for sorting display
                androidx.appcompat.app.AlertDialog.Builder(activity as Context)
                    .setTitle("Sort By?")
                    .setView(radioButtonView)
                    .setPositiveButton("OK") { text, listener ->
                        if (radioButtonView.radio_high_to_low.isChecked) {
                            Collections.sort(restaurantList, costComparator)
                            restaurantList.reverse()
                            recyclerAdapter.notifyDataSetChanged()//updates the adapter
                        }
                        if (radioButtonView.radio_low_to_high.isChecked) {
                            Collections.sort(restaurantList, costComparator)
                            recyclerAdapter.notifyDataSetChanged()//updates the adapter
                        }
                        if (radioButtonView.radio_rating.isChecked) {
                            Collections.sort(restaurantList, ratingComparator)
                            restaurantList.reverse()
                            recyclerAdapter.notifyDataSetChanged()//updates the adapter
                        }
                    }
                    .setNegativeButton("CANCEL") { text, listener ->

                    }
                    .create()
                    .show()
            }
        }

        return super.onOptionsItemSelected(item)
    }


    class DBAsyncTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {

            when (mode) {
                1 -> {
                    //check if the restaurant is favourite or not
                    val restaurant: RestaurantEntity? = db.restaurantDao()
                        .getRestaurantById(restaurantEntity.restaurant_id.toString())
                    db.close()
                    return restaurant != null
                }

                2 -> {
                    //Save the restaurant into DB as favourite
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }

                3 -> {
                    //Remove the favourite restaurant
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }


}
