package ke.co.visualdiagnoser.besafe.ui.home

import androidx.lifecycle.LifecycleObserver
import ke.co.visualdiagnoser.besafe.ui.home.newHome.NewHomeFragment

class HomePresenter(fragment: NewHomeFragment) : LifecycleObserver {

    init {
        fragment.lifecycle.addObserver(this)
    }
}