package ke.co.visualdiagnoser.besafe.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.Navigator
import androidx.navigation.fragment.NavHostFragment
import ke.co.visualdiagnoser.besafe.HasBlockingState

open class BaseFragment : Fragment() {

    override fun onResume() {
        super.onResume()
        val activity = this.activity
        if (activity is HasBlockingState) {
            activity.isUiBlocked = false
        }
    }

    protected fun navigateTo(actionId: Int, bundle: Bundle? = null, navigatorExtras: Navigator.Extras? = null) {
        val activity = this.activity
        if (activity is HasBlockingState) {
            activity.isUiBlocked = true
        }
        NavHostFragment.findNavController(this).navigate(actionId, bundle, null, navigatorExtras)
    }

    protected fun popBackStack() {
        val activity = this.activity
        if (activity is HasBlockingState) {
            activity.isUiBlocked = true
        }
        NavHostFragment.findNavController(this).popBackStack()
    }
}