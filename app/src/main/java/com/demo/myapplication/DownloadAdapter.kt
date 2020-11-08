import android.app.DownloadManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.demo.myapplication.R
import kotlinx.android.synthetic.main.item_download.view.*
import java.io.File

class DownloadAdapter(var list: ArrayList<ItemModel>, val listener: (ItemModel) -> Unit) :
    RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_download, parent, false)
        )

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]
        with(holder.itemView) {

            tv_name.text = model.title
            tv_status.text = statusMessage(model.status)
            progressBar.isVisible = model.isDownloading
            progressBar.progress = model.progress;
            setOnClickListener {
                listener(model)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
        if (payloads.firstOrNull() != null) {
            with(holder.itemView) {
                (payloads.first() as Bundle).getInt("progress").also {
                    progressBar.progress = it
                    progressBar.isVisible = it < 100
                }
            }
        }
    }

    fun setDownloading(model: ItemModel, isDownloading: Boolean) {
        getDummy(model)?.isDownloading = isDownloading
        notifyItemChanged(list.indexOf(model))
    }
    fun setTitle(model: ItemModel, id: Int) {
        getDummy(model)?.status = id
        notifyDataSetChanged()
        notifyItemChanged(list.indexOf(model))
    }

    fun setProgress(model: ItemModel, progress: Int) {
        getDummy(model)?.progress = progress
        notifyItemChanged(list.indexOf(model), Bundle().apply { putInt("progress", progress) })
    }

    fun AddItems(model: ItemModel):Boolean {
        if (!list.contains(model)) {
            list.add(model)
            notifyDataSetChanged();
            return true;
        }else return false;

    }

    private fun statusMessage(status: Int): String? {
        var msg = ""
        msg = when (status) {
            DownloadManager.STATUS_FAILED -> "Failed"
            DownloadManager.STATUS_PAUSED -> "Paused"
            DownloadManager.STATUS_PENDING -> "Pending"
            DownloadManager.STATUS_RUNNING -> "Downloading..."
            DownloadManager.STATUS_SUCCESSFUL -> "Downloaded"
            else -> "Queuing"
        }
        return msg
    }

    private fun getDummy(dummy: ItemModel) = list.find { dummy.id == it.id }


}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
