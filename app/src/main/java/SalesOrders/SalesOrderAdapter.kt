package SalesOrders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kharchabook.R

class SalesOrderAdapter(private val salesOrders:List<SalesOrder>): RecyclerView.Adapter<SalesOrderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sales_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val salesOrder = salesOrders[position]
        holder.SalesId.text=salesOrder.SalesId
        holder.CustAccount.text=salesOrder.CustAccount
        holder.CustomerName.text=salesOrder.CustomerName
        holder.SalesStatus.text= getSalesStatus(salesOrder.SalesStatus)
        holder.SalesType.text= getSalesType(salesOrder.SalesType)
    }

    override fun getItemCount(): Int {
        return salesOrders.size
    }
     class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val SalesId:TextView=itemView.findViewById(R.id.SalesId)
        val CustAccount:TextView=itemView.findViewById(R.id.CustAccount)
        val CustomerName:TextView=itemView.findViewById(R.id.CustName)
        val SalesStatus:TextView=itemView.findViewById(R.id.SalesStatus)
        val SalesType:TextView=itemView.findViewById(R.id.OrderType)
    }
    fun getSalesStatus(statusCode: Int): String {
        when (statusCode) {
            0 -> return "None"
            1 -> return "Open Order"
            2 -> return "Received"
            3 -> return "Delivered"
            4 -> return "Cancelled"
            else -> return "Unknown"
        }
    }
  fun getSalesType(statusCode: Int): String {
        when (statusCode) {
            1 -> return "Sales Order"
            2 -> return "Return Order"
            else -> return "Unknown"
        }
    }




}