package web.macro.app

import androidx.room.*

@Entity(tableName = "table_buy_logs")
data class Logs(
    @PrimaryKey(autoGenerate = true) val id:Long,
    var strDate: String,
    var strProduct: String,
    var strPurchase: String,
    var strIp: String,
    var strAddress: String,
    var strSearch: String
)