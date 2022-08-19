package com.example.happyplaceapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaceapp.databinding.ActivityMainBinding
import com.example.happyplaceapp.swipeButton.SwipeToEditCallback
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private var binding:ActivityMainBinding? = null
    private val items: ArrayList<registerEntity> = arrayListOf()

    lateinit var adapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        adapter = MainAdapter(items, this){
            goToAnotherActivity(it)
        }

        binding?.fabAddHappyPlace?.setOnClickListener{
            val intent = Intent(this, AddHappyPlaceActivity::class.java)
            startActivity(intent)
        }

        val dao = (application as RegisterApp).db.registerDao()
        getAllCompletedData(dao)
    }

    private fun getAllCompletedData(registerDao: registerDao){

        lifecycleScope.launch {
            registerDao.fetchAllData().collect { allCompletedData->
                val list = ArrayList(allCompletedData)
                setUpListOfDataIntoRecyclerView(list, registerDao)
            }
        }
    }

    private fun setUpListOfDataIntoRecyclerView(registerList:ArrayList<registerEntity>,
                                                registerDao: registerDao) {

        if(registerList.isNotEmpty()){
            val itemAdapter = MainAdapter(registerList, this)

            itemAdapter.onItemClick = {
                goToAnotherActivity(it)
            }

            binding?.rvHappyPlaceList?.layoutManager = LinearLayoutManager(this)
            binding?.rvHappyPlaceList?.adapter = itemAdapter
            binding?.rvHappyPlaceList?.visibility = View.VISIBLE
            binding?.noRecordText?.visibility = View.GONE

            val editSwipeHandler = object : SwipeToEditCallback(this) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val adapter = binding?.rvHappyPlaceList!!.adapter as MainAdapter
                    adapter.notifyEditItem(this@MainActivity, viewHolder.adapterPosition, 1)
                }
            }
            val itemTouchHelper = ItemTouchHelper(editSwipeHandler)
            itemTouchHelper.attachToRecyclerView(binding?.rvHappyPlaceList)

        }else{
            binding?.rvHappyPlaceList?.visibility = View.GONE
            binding?.noRecordText?.visibility = View.VISIBLE
        }
    }

    private fun goToAnotherActivity(registerEntity: registerEntity) {

        val intent = Intent(this, HappyPlaceDetailsActivity::class.java)
        intent.putExtra("data", registerEntity)
        startActivity(intent)

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}

