package sfllhkhan95.connect.core.ui.view.holder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import sfllhkhan95.connect.core.model.Viewable

/**
 *
 *
 * @author saifkhichi96
 * @version 1.0.0
 * @since 1.0.0 2019-05-29 04:54
 */
abstract class ViewHolder<T : Viewable>(protected val context: Context, @LayoutRes resId: Int?) {

    protected lateinit var itemView: View

    var data: T? = null
        set(value) {
            field = value
            data?.setView(this)
            onStateChanged()
        }

    val view: View
        get() = itemView

    init {
        if (resId != null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            itemView = inflater.inflate(resId, null, false)
        }
    }

    private fun onStateChanged() {
        if (data != null) {
            updateWith(data!!)
            itemView.visibility = View.VISIBLE
        } else {
            itemView.visibility = View.GONE
        }
    }

    protected abstract fun updateWith(data: T)

    protected fun setLayoutRes(@LayoutRes resId: Int) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        itemView = inflater.inflate(resId, null, false)
    }

    fun notifyOnStateChange() {
        onStateChanged()
    }

    fun addClickListener(clickListener: View.OnClickListener) {
        itemView.setOnClickListener(clickListener)
    }

}