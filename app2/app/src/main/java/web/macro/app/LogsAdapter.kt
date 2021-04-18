package web.macro.app

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class LogsAdapter (private val items: ArrayList<DataLogs>) : RecyclerView.Adapter<RecyclerAdapterLogs.ViewHolder>() {
    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerAdapterLogs.ViewHolder, position: Int) {
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerAdapterLogs.ViewHolder {
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    }
}