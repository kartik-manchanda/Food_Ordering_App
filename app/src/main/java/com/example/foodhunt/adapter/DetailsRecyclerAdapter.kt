package com.example.foodhunt.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.foodhunt.R
import com.example.foodhunt.activity.CartActivity
import com.example.foodhunt.model.Detail

class DetailsRecyclerAdapter(
    val context: Context,
    val itemList: ArrayList<Detail>,
    val proceedToCart: RelativeLayout,
    val btnCart: Button,
    val restaurantId: String?,
    val restaurantName: String?
) :
    RecyclerView.Adapter<DetailsRecyclerAdapter.DetailViewHolder>() {

    var itemSelectedCount: Int = 0
    var orderList = arrayListOf<String>()


    class DetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtSerialNumber: TextView = view.findViewById(R.id.txtSerialNumber)
        val txtMenuItems: TextView = view.findViewById(R.id.txtMenuItems)
        val txtPrice: TextView = view.findViewById(R.id.txtPrice)
        val btnAdd: Button = view.findViewById(R.id.btnAdd)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_rest_detail_single_row, parent, false)
        return DetailViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        val detail = itemList[position]
        holder.btnAdd.tag = detail.foodId + ""
        holder.txtSerialNumber.text = (position + 1).toString()
        holder.txtMenuItems.text = detail.foodName
        holder.txtPrice.text = "Rs. " + detail.foodPrice



        btnCart.setOnClickListener {

            val intent = Intent(context, CartActivity::class.java)

            intent.putExtra(
                "restaurantId",
                restaurantId.toString()
            )// pass the restaurant id to the next acticity

            intent.putExtra("restaurantName", restaurantName)

            intent.putExtra("selectedItemsId", orderList)//pass all the items selected by the user

            context.startActivity(intent)
        }


        holder.btnAdd.setOnClickListener {

            if (holder.btnAdd.text.toString() == "Remove") {
                itemSelectedCount--//unselected

                orderList.remove(holder.btnAdd.tag.toString())

                holder.btnAdd.text = "Add"

                holder.btnAdd.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorPrimary
                    )
                )

            } else {
                itemSelectedCount++//selected

                orderList.add(holder.btnAdd.tag.toString())


                holder.btnAdd.text = "Remove"

                holder.btnAdd.setBackgroundColor(ContextCompat.getColor(context, R.color.addToCart))

            }

            if (itemSelectedCount > 0) {
                proceedToCart.visibility = View.VISIBLE
            } else {
                proceedToCart.visibility = View.INVISIBLE
            }

        }
    }
}


