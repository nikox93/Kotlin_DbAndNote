package com.example.db_test

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.db_test.databinding.ActivityMainBinding
import com.example.db_test.db.MyAdapter
import com.example.db_test.db.MyDbManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var bindingClass: ActivityMainBinding
    val myDbManager = MyDbManager(this)
    val myAdapter= MyAdapter(ArrayList(),this)
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(bindingClass.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        init()
        initSearchView()
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDb()
        fillAdapter("")
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }

    fun onClickNew(view: View) {
        val intent = Intent(this, EditActivity::class.java)
        startActivity(intent)
    }

    // рисуем внутри элемента rcView в MyActivity поэлементно rc_item
    fun init(){
        var rcView = bindingClass.rcView
        rcView.layoutManager = LinearLayoutManager(this)
        val swapHelper = getSwapMg()
        swapHelper.attachToRecyclerView(rcView)
        rcView.adapter = myAdapter
    }

    //функция поиска
    private fun initSearchView(){
        var searchView = bindingClass.searchView

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            // поиск по буквам/тексту
            override fun onQueryTextChange(newText: String?): Boolean {
                fillAdapter(newText!!)
                //Log.d("MyLog","New text : $newText")
                return true
            }
        } )
    }

    private fun fillAdapter(text: String){

        job?.cancel()

        // запускаем функцию Coroutine для оптимизации потоков - урок 10 Блокнот
        job = CoroutineScope(Dispatchers.Main).launch {

            val tvNoElement = bindingClass.tvNoElement
            val list = myDbManager.readDbData(text)
            myAdapter.updateAdapter(list)
            if(list.size > 0){
                tvNoElement.visibility = View.GONE
            } else {
                tvNoElement.visibility = View.VISIBLE
            }

        }
    }

    private fun getSwapMg(): ItemTouchHelper {
        return ItemTouchHelper(object:ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                myAdapter.removeItem(viewHolder.adapterPosition,myDbManager)
            }

        })

    }
}

/*
считывание id элементов через binding view
(YT - канал Neco - урок 9 по kotlin и AS для начинающих
*/