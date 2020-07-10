package com.example.foodhunt.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.foodhunt.R
import com.example.foodhunt.activity.RestaurantDetailsActivity
import com.example.foodhunt.database.RestaurantEntity
import com.example.foodhunt.fragment.HomeFragment
import com.squareup.picasso.Picasso

class FavouriteRecyclerAdapter(
    val context: Context,
    val restaurantList: List<RestaurantEntity>
) :
    RecyclerView.Adapter<FavouriteRecyclerAdapter.FavouriteViewHolder>() {

    class FavouriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtFavRestaurantName: TextView = view.findViewById(R.id.txtFavRestaurantName)
        val txtFavCostForOne: TextView = view.findViewById(R.id.txtFavCostForOne)
        val txtFavRestaurantRating: TextView = view.findViewById(R.id.txtFavRestaurantRating)
        val imgFavRestaurantImage: ImageView = view.findViewById(R.id.imgFavRestaurantImage)
        val imgFav2Restaurant: ImageView = view.findViewById(R.id.imgFav2Restaurant)
        val llFavContent: LinearLayout = view.findViewById(R.id.llFavContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_favourite_single_row, parent, false)
        return FavouriteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val restaurant = restaurantList[position]
        holder.txtFavRestaurantName.text = restaurant.restaurantName
        holder.txtFavCostForOne.text = restaurant.restaurantPrice
        holder.txtFavRestaurantRating.text = restaurant.restaurantRating
        Picasso.get().load(restaurant.restaurantImage).error(R.drawable.ic_default_image_restaurant)
            .into(holder.imgFavRestaurantImage)
        holder.llFavContent.setOnClickListener {
            val intent = Intent(context, RestaurantDetailsActivity::class.java)
            intent.putExtra("restaurant_id", restaurant.restaurant_id)
            intent.putExtra("restaurant_name", restaurant.restaurantName)
            context.startActivity(intent)
        }
        val restaurantEntity = RestaurantEntity(
            restaurant.restaurant_id,
            restaurant.restaurantName,
            restaurant.restaurantRating,
            holder.txtFavCostForOne.text.toString(),
            restaurant.restaurantImage
        )
        holder.imgFav2Restaurant.setOnClickListener {
            if (!HomeFragment.DBAsyncTask(context, restaurantEntity, 1).execute().get()) {

                val async = HomeFragment.DBAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(context, "Restaurant added to Favourites", Toast.LENGTH_SHORT)
                        .show()
                    holder.imgFav2Restaurant.setImageResource(R.drawable.ic_fav_after)
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
                    holder.imgFav2Restaurant.setImageResource(R.drawable.ic_fav_before)
                } else {
                    Toast.makeText(context, "Some error Occurred", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
