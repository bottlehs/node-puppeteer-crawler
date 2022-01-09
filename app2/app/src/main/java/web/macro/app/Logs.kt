package web.macro.app

import androidx.room.*

@Entity(tableName = "table_store_product_buy_logs")
data class Logs(
    @PrimaryKey(autoGenerate = true) val id:Long,
    var date: String,
    var executionCdoe: String,
    var productId: String,
    var purchaseId: String,
    var productName: String,
    var purchase: String,
    var ip: String,
    var address: String,
    var search: String
)