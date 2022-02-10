package com.GC200419191.realestateapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PropertyAdapter (val context : Context,
                       val properties : List<Property>,
                       val itemListener: PropertyItemListener)   : RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder>() {

    /**
     * This class is used to allow us to access the item_restaurant.xml objects
     * THIS IS CALLED AS VIEWHOLDER
     */
    // you can define classes within the classes
    inner class PropertyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        // Holding the information of the property
        val addressTextView = itemView.findViewById<TextView>(R.id.addressTextView)
        val cityTextView = itemView.findViewById<TextView>(R.id.cityTextView)
        val postalTextView = itemView.findViewById<TextView>(R.id.postalTextView)
        val bedTextView = itemView.findViewById<TextView>(R.id.bedTextView)
        val bathTextView = itemView.findViewById<TextView>(R.id.bathTextView)
        val roomTextView = itemView.findViewById<TextView>(R.id.roomTextView)
    }

    /**
     * This connects (aka inflates) the individual ViewHolder (which is the link to the item_restaurant.xml)
     * with the RecyclerView or activity_recycler_list.xml
     *
     * This also you can say is instance of RestaurantViewHolder class
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_property, parent, false)
        return PropertyViewHolder(view)
    }

    /**
     * This method will bind the viewHolder with a specific property object
     */
    override fun onBindViewHolder(viewHolder: PropertyViewHolder, position: Int) {

        val property = properties[position]
        viewHolder.addressTextView.text = property.address
        viewHolder.cityTextView.text = property.city
        viewHolder.postalTextView.text = property.postalCode

        viewHolder.itemView.setOnClickListener{
            itemListener.propertySelected(property)
        }
    }


    // This will return the number of properties in the database
    override fun getItemCount(): Int {
        return properties.size
    }

    interface PropertyItemListener{
        fun propertySelected(property : Property)
    }
}