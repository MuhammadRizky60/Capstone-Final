package com.example.agronect.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agronect.R
import com.example.agronect.data.response.Animal
import com.example.agronect.ui.adapter.ListCoffeeAdapter
import com.example.agronect.ui.detail.DetailHistoryActivity

class DashboardFragment : Fragment() {

    private lateinit var rvAnimal: RecyclerView
    private val list = ArrayList<Animal>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        rvAnimal = view.findViewById(R.id.rv_animal)
        rvAnimal.setHasFixedSize(true)

        list.addAll(getListAnimal())
        showRecyclerList()

        return view
    }

    private fun getListAnimal(): ArrayList<Animal> {
        val dataName = resources.getStringArray(R.array.data_name)
        val dataDescription = resources.getStringArray(R.array.data_description)
        val dataPhoto = resources.obtainTypedArray(R.array.data_photo)
        val listAnimal = ArrayList<Animal>()
        for (i in dataName.indices) {
            val animal = Animal(dataName[i], dataDescription[i], dataPhoto.getResourceId(i, -1))
            listAnimal.add(animal)
        }
        return listAnimal
    }

    private fun showRecyclerList() {
        rvAnimal.layoutManager = LinearLayoutManager(context)
        val listCoffeeAdapter = ListCoffeeAdapter(list)
        rvAnimal.adapter = listCoffeeAdapter

        listCoffeeAdapter.setOnItemClickCallBack(object : ListCoffeeAdapter.OnItemCallback {
            override fun onItemClicked(data: Animal) {
                val intentToDetail = Intent(context, DetailHistoryActivity::class.java)
                intentToDetail.putExtra("Data", data)
                startActivity(intentToDetail)
            }
        })
    }
}
