package com.albino.restaurantapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodhunt.R
import com.example.foodhunt.model.CartItems


class CartRecyclerAdapter(val context: Context, val cartItems: ArrayList<CartItems>) :
    RecyclerView.Adapter<CartRecyclerAdapter.ViewHolderCart>() {


    class ViewHolderCart(view: View) : RecyclerView.ViewHolder(view) {
        val txtCartName: TextView = view.findViewById(R.id.txtCartName)
        val txtCartPrice: TextView = view.findViewById(R.id.txtCartPrice)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCart {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_cart_single_row, parent, false)
        return ViewHolderCart(view)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    override fun onBindViewHolder(holder: ViewHolderCart, position: Int) {
        val cartItemObject = cartItems[position]


        holder.txtCartName.text = cartItemObject.itemName
        holder.txtCartPrice.text = "Rs. " + cartItemObject.itemPrice
    }


}