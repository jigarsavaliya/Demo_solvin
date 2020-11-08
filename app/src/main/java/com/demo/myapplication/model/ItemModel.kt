import android.net.Uri
import java.io.Serializable

/**
 * Model for holding dowmloading items
 */
class ItemModel(val id: Int, val url: String) :Serializable{
    var Path:String?=null;
    var Size:String?=null;
    var isDownloading: Boolean = false
    var progress = 0
   var status: Int=0;
    val title:String
    get() = url.substring(url.lastIndexOf("/") + 1)
}