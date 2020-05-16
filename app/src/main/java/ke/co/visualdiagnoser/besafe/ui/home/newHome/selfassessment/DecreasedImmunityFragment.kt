package ke.co.visualdiagnoser.besafe.ui.home.newHome.selfassessment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import ke.co.visualdiagnoser.besafe.R

import kotlinx.android.synthetic.main.fragment_decreased_immunity.*

/**
 * A simple [Fragment] subclass.
 */
class DecreasedImmunityFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_decreased_immunity, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        yes_button?.setOnClickListener {
            findNavController().navigate(R.id.action_decreasedImmunityFragment_to_pregnancyFragment)
        }
    }

}
