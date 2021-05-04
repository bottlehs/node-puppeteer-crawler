package web.macro.app

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LogsDao {
    @Query("SELECT * FROM tb_logs")
    fun getAll(): List<Logs>

    @Insert
    fun insertAll(vararg contacts: Logs)

    @Delete
    fun delete(contacts: Logs)
}