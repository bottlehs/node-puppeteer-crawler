package web.macro.app

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LogsDao {
    @Query("SELECT * FROM table_buy_logs ORDER BY strDate DESC")
    fun getAll(): List<Logs>

    @Query("SELECT * FROM table_buy_logs WHERE strDate BETWEEN strftime('%Y-%m-%d %H:%M:%S',:startStrDate) AND strftime('%Y-%m-%d %H:%M:%S',:endStrDate)")
    fun getDateAll(startStrDate: String, endStrDate: String): List<Logs>

    @Insert
    fun insertAll(vararg contacts: Logs)

    @Delete
    fun delete(contacts: Logs)
}