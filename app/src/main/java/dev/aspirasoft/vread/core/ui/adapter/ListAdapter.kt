package dev.aspirasoft.vread.core.ui.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import dev.aspirasoft.vread.core.model.Viewable
import dev.aspirasoft.vread.core.ui.view.holder.ViewHolder
import dev.aspirasoft.vread.core.util.OnItemClickListener
import java.lang.reflect.Constructor

/**
 * @author saifkhichi96
 * @version 1.0.0
 * @since 1.0.0 2019-05-29 05:16
 */
open class ListAdapter<T : Viewable>
constructor(private val context: Context, viewHolder: Class<out ViewHolder<T>>) : BaseAdapter() {
    private val constructor: Constructor<out ViewHolder<T>>? = try {
        viewHolder.getConstructor(Context::class.java)
    } catch (ex: NoSuchMethodException) {
        null
    }

    protected val items = ArrayList<T>()
    var itemClickListener: OnItemClickListener<T>? = null

    open fun add(data: T): Boolean {
        for (item in items) {
            if (item == data) {
                return false
            }
        }

        items.add(data)
        this.notifyDataSetChanged()
        return true
    }

    fun clear() {
        items.clear()
        this.notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): T {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var view: View? = null
        try {
            val viewHolder: ViewHolder<T>?
            if (convertView == null) {
                viewHolder = constructor?.newInstance(context)
                view = viewHolder?.view
                view?.tag = viewHolder
            } else {
                view = convertView
                viewHolder = view.tag as ViewHolder<T>
            }

            viewHolder?.data = items[position]
            viewHolder?.addClickListener(View.OnClickListener {
                itemClickListener?.onItemClick(items[position])
            })

        } catch (ignored: Exception) {

        }

        return view
    }

}