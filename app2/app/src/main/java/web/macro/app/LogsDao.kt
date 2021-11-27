package web.macro.app

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.lifecycle.LiveData




@Dao
interface LogsDao {
    @Query("SELECT * FROM table_store_product_buy_logs ORDER BY date DESC")
    fun getAll(): List<Logs>

    @Query("SELECT count(*) FROM table_store_product_buy_logs WHERE productId = :productId ORDER BY date DESC")
    fun getCountProductIdAll(productId: String): Int

    @Query("SELECT * FROM table_store_product_buy_logs WHERE date BETWEEN strftime('%Y-%m-%d %H:%M:%S',:startDate) AND strftime('%Y-%m-%d %H:%M:%S',:endDate)")
    fun getDateAll(startDate: String, endDate: String): List<Logs>

    @Insert
    fun insertAll(vararg contacts: Logs)

    @Delete
    fun delete(contacts: Logs)
}