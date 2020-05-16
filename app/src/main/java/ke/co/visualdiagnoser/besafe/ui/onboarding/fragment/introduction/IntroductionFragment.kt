package ke.co.visualdiagnoser.besafe.ui.onboarding.fragment.introduction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ke.co.visualdiagnoser.besafe.R
import ke.co.visualdiagnoser.besafe.ui.PagerChildFragment
import ke.co.visualdiagnoser.besafe.ui.UploadButtonLayout
import kotlinx.android.synthetic.main.fragment_intro.*

class IntroductionFragment : PagerChildFragment() {

    override val navigationIcon: Int? = null
    override var stepProgress: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            : View? = inflater.inflate(R.layout.fragment_intro, container, false)

    override fun getUploadButtonLayout() = UploadButtonLayout.ContinueLayout(R.string.intro_button) {
        navigateTo(R.id.action_introFragment_to_howItWorksFragment)
    }

    override fun updateButtonState() {
        enableContinueButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        root.removeAllViews()
    }
}