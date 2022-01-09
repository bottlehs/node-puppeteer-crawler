package web.macro.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.log_item.view.*

class LogsAdapter (private val items: ArrayList<DataLogs>) : RecyclerView.Adapter<LogsAdapter.ViewHolder>() {

    private val TYPE_HEADER = 0
    private val TYPE_ITEM = 1
    private val TYPE_FOOTER = 2

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: LogsAdapter.ViewHolder, position: Int) {
        val item = items[position]
        val listener = View.OnClickListener { it ->
            Toast.makeText(it.context, "Clicked"+item.productName, Toast.LENGTH_SHORT).show()
        }
        holder.apply {
            bind(listener, item)
            itemView.tag = item
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LogsAdapter.ViewHolder {
        if ( viewType == TYPE_HEADER ) {
            val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.log_item, parent, false)
            return LogsAdapter.ViewHolder(inflatedView)
        } else {
            val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.log_item, parent, false)
            return LogsAdapter.ViewHolder(inflatedView)
        }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v
        fun bind(listener: View.OnClickListener, item: DataLogs) {
            view.date.text = item.date;
            view.executionCdoe.text = item.executionCdoe;
            view.product.text = item.productName;
            view.productId.text = item.productId
            view.purchaseId.text = item.purchaseId
            view.search.text = item.search;
            view.address.text = "";
            if ( 0 < item.address.length ) {
                val strAddress = item.address.split(",");
                if ( !strAddress.isEmpty() ) {
                    view.address.text = strAddress.get(0);
                }
            }
            view.ip.text = item.ip;
        }
    }
}