package com.example.kharchabook

import SalesOrders.SalesOrder
import SalesOrders.SalesOrderAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SalesTableListPage : AppCompatActivity() {
    lateinit var recyclerView:RecyclerView
    lateinit var adapter: SalesOrderAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales_table_list_page)
        recyclerView=findViewById(R.id.recyclerView)
        val SalesOrders = listOf(
            SalesOrder("S.O.908797778","00001","Borjan Pvt Ltd.",1,1),
            SalesOrder("S.O.908793438","00002","Borjan Pvt Ltd.",1,2),
            SalesOrder("S.O.903465448","00003","Borjan Pvt Ltd.",2,1),
            SalesOrder("S.O.908745338","00004","Borjan Pvt Ltd.",3,2),
            SalesOrder("S.O.908797568","00005","Borjan Pvt Ltd.",2,1),
            SalesOrder("S.O.908798948","00006","Borjan Pvt Ltd.",2,2),
            // Add more items as needed
        )
        adapter= SalesOrderAdapter(SalesOrders)
        recyclerView.adapter=adapter
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
}