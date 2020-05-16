package ke.co.visualdiagnoser.besafe.ui.home.newHome.selfassessment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import ke.co.visualdiagnoser.besafe.R
import kotlinx.android.synthetic.main.fragment_risk.*

/**
 * A simple [Fragment] subclass.
 */
class RiskFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_risk, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        yes_button?.setOnClickListener {
            findNavController().navigate(R.id.action_riskFragment_to_over65Fragment)
        }
    }

}
