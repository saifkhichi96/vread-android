package dev.aspirasoft.vread.profile.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import dev.aspirasoft.vread.R
import dev.aspirasoft.vread.auth.model.User
import kotlinx.android.synthetic.main.fragment_profile_summary.*

@AndroidEntryPoint
class ProfileSummaryFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { args ->
            (args.getSerializable("user") as User?)?.let {
                name.text = "${it.firstName} ${it.lastName}"
                username.text = String.format(getString(R.string.ph_username), it.username)

                avatar.openProfileOnClick = false
                avatar.showUser(it.uid)
            }
        }
    }

}