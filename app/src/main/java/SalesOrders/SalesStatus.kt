package SalesOrders

enum class SalesStatus(val status:Int,val statusLabel:String) {
    NONE(1, "None"),
    BACKORDER(1, "Open order"),
    DELIVERED(2, "Delivered"),
    INVOICED(3, "Invoiced"),
    CANCELLED(4, "Cancelled")
}