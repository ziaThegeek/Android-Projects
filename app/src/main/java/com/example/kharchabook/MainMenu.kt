package com.example.kharchabook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView

class MainMenu : AppCompatActivity() {
    lateinit var salesOrderCardView: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        salesOrderCardView=findViewById(R.id.SalesOrders)

        salesOrderCardView.setOnClickListener{
            val intent= Intent(this@MainMenu,SalesTableListPage::class.java)
            startActivity(intent)
        }
    }
}