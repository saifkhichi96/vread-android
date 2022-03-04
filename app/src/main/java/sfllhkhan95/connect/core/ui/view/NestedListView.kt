package sfllhkhan95.connect.core.ui.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ListView

class NestedListView : ListView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightSpec: Int = if (layoutParams.height == LayoutParams.WRAP_CONTENT) {
            // The great Android "hackatlon", the love, the magic.
            // The two leftmost bits in the height measure spec have
            // a special meaning, hence we can't use them to describe height.
            MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE shr 2, MeasureSpec.AT_MOST)
        } else {
            // Any other height should be respected as is.
            heightMeasureSpec
        }

        super.onMeasure(widthMeasureSpec, heightSpec)
    }
}