package com.GC200419191.realestateapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.lang.Exception

class PropertyListViewModel : ViewModel() {

    //this will hold a mutable list of Restaurant objects
    private val properties = MutableLiveData<List<Property>>()

    // This is initializer method which will always run once you instantiate object
    init {
        loadProperties()
    }

    private fun loadProperties() {

        // 1) connecting to db and using addSnapshotListener
        // Connecting to collection/table and ordering it by the ascending.
        val db = FirebaseFirestore.getInstance().collection("properties")
            .orderBy("address", Query.Direction.ASCENDING)
        // This will not only return the document whereas it will also look for the changes it have.
        // This is lambda expression which have two inputs table and exception
        db.addSnapshotListener { documents, exception ->


            //2) This step will make sure that their is no exception
            //if there is an exception - let's log it
            // ? means that it could be null
            exception?.let {
                Log.i("DB_Response", "Listen failed : " + exception)
                return@addSnapshotListener
            }
            // No exception if this is executed
            // ? here also means if null don't run it
            Log.i("DB_Response", "# of elements returned: ${documents?.size()}")

            //3) This step we are going to make sure that objects are passed to view model
            //create an array list of Restaurant objects that will be used to
            //populate the MutableLiveData variable called restaurants
            // TIP always put the entire collection into the list not single by single that is wrong
            val propertyList = ArrayList<Property>()

            //loop over the documents from the DB and create Properties objects
            documents?.let {
                for (document in documents) {
                    try {
                        // creating the object of the class to make sure that every datafield enter is right.
                        val property = document.toObject(Property::class.java)
                        propertyList.add(property)
                    } catch (e: Exception) {
                        Log.i("DB_Response", document.toString())
                    }

                }
            }
            properties.value = propertyList
        }
    }

    // This method will provide access to restaurants variable with all of the data
    fun getRestaurants() : LiveData<List<Property>>
    {
        return properties
    }
}