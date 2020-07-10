package com.example.foodhunt.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.foodhunt.R
import com.example.foodhunt.activity.RestaurantDetailsActivity
import com.example.foodhunt.database.RestaurantEntity
import com.example.foodhunt.fragment.HomeFragment
import com.example.foodhunt.model.Restaurants
import com.squareup.picasso.Picasso

class HomeRecyclerAdapter(
    val context: Context, var
    itemList: ArrayList<Restaurants>
) : RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {

    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtCostForOne: TextView = view.findViewById(R.id.txtCostForOne)
        val txtRestaurantRating: TextView = view.findViewById(R.id.txtRestaurantRating)
        val imgRestaurantImage: ImageView = view.findViewById(R.id.imgRestaurantImage)
        val imgFavRestaurant: ImageView = view.findViewById(R.id.imgFavRestaurant)
        val llContent: LinearLayout = view.findViewById(R.id.llContent)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_home_single_row, parent, false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val restaurant = itemList[position]
        holder.txtRestaurantName.text = restaurant.restaurantName
        holder.txtCostForOne.text = "₹" + restaurant.costForOne + "/person"
        holder.txtRestaurantRating.text = restaurant.restaurantRating
        Picasso.get().load(restaurant.restaurantImage).error(R.drawable.ic_default_image_restaurant)
            .into(holder.imgRestaurantImage)
        holder.llContent.setOnClickListener {
            val intent = Intent(context, RestaurantDetailsActivity::class.java)
            intent.putExtra("restaurant_id", restaurant.restaurantId)
            intent.putExtra("restaurant_name", restaurant.restaurantName)
            context.startActivity(intent)
        }
        val restaurantEntity = RestaurantEntity(
            restaurant.restaurantId.toInt(),
            restaurant.restaurantName,
            restaurant.restaurantRating,
            holder.txtCostForOne.text.toString(),
            restaurant.restaurantImage
        )

        val checkFav = HomeFragment.DBAsyncTask(context, restaurantEntity, 1).execute()
        val isFav = checkFav.get()

        if (isFav) {
            holder.imgFavRestaurant.setImageResource(R.drawable.ic_fav_after)
        } else {
            holder.imgFavRestaurant.setImageResource(R.drawable.ic_fav_before)
        }

        holder.imgFavRestaurant.setOnClickListener {
            if (!HomeFragment.DBAsyncTask(context, restaurantEntity, 1).execute().get()) {

                val async = HomeFragment.DBAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(context, "Restaurant added to Favourites", Toast.LENGTH_SHORT)
                        .show()
                    holder.imgFavRestaurant.setImageResource(R.drawable.ic_fav_after)
                } else {
                    Toast.makeText(context, "Some error Occurred", Toast.LENGTH_SHORT).show()
                }
            } else {
                val async = HomeFragment.DBAsyncTask(context, restaurantEntity, 3).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        context,
                        "Restaurant removed from Favourites",
                        Toast.LENGTH_SHORT
                    ).show()
                    holder.imgFavRestaurant.setImageResource(R.drawable.ic_fav_before)
                } else {
                    Toast.makeText(context, "Some error Occurred", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun filterList(filteredList: ArrayList<Restaurants>) {//to update the recycler view depending on the search
        itemList = filteredList
        notifyDataSetChanged()
    }


}