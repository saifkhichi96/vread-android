package dev.aspirasoft.vread.profile.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import dev.aspirasoft.vread.R
import dev.aspirasoft.vread.auth.model.User
import kotlinx.android.synthetic.main.fragment_profile_details.*

@AndroidEntryPoint
class ProfileDetailsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { args ->
            (args.getSerializable("user") as User?)?.let {
                if (it.bio == null) bio_field.visibility = View.GONE else bio.text = it.bio
                if (it.dateOfBirth == null) birthday_field.visibility = View.GONE else birthday.text = it.dateOfBirth
                location.text = it.country
                email.text = it.email
                joining_date.text = String.format(getString(R.string.ph_join_date), it.joinedOn)
                if (it.website == null) website_field.visibility = View.GONE else website.text = it.website
            }
        }
    }
}