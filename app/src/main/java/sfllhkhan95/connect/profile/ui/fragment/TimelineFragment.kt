package sfllhkhan95.connect.profile.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import sfllhkhan95.connect.R

@AndroidEntryPoint
class TimelineFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments
        if (args != null) {
            val isOwnProfile = args.getBoolean("isOwnProfile")
            if (!isOwnProfile) {
                view.findViewById<View>(R.id.compose_view).visibility = View.GONE
            }
        }

        // TODO: Disable click listeners on avatars
    }

}