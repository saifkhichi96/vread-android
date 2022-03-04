package sfllhkhan95.connect.core.model

import com.google.firebase.database.Exclude
import sfllhkhan95.connect.core.ui.view.holder.ViewHolder
import java.io.Serializable

/**
 *
 *
 * @author saifkhichi96
 * @version 1.0.0
 * @since 1.0.0 2019-05-29 06:35
 */
abstract class Viewable : Serializable {

    @Transient
    @Exclude
    private var view: ViewHolder<out Viewable>? = null

    fun setView(view: ViewHolder<out Viewable>) {
        this.view = view
    }

    protected fun notifyOnStateChange() {
        view?.notifyOnStateChange()
    }

    override fun equals(other: Any?): Boolean {
        return try {
            (other as Viewable).hashCode() == this.hashCode()
        } catch (ex: ClassCastException) {
            false
        }
    }

    abstract override fun hashCode(): Int

}