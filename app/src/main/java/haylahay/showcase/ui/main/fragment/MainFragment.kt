package haylahay.showcase.ui.main.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import haylahay.showcase.R
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        introductionWV.loadUrl("file:///android_asset/Instruction.html");

        toBasicsBtn.setOnClickListener {
            replaceFragment(CoroutineBasicsFragment())
        }

        toDeferredBtn.setOnClickListener {
            replaceFragment(CoroutineDeferredFragment())
        }

        toChannelBtn.setOnClickListener {
            replaceFragment(CoroutineChannelFragment())
        }

        toCancellationBtn.setOnClickListener {
            replaceFragment(CoroutineCancellationFragment())
        }

    }

    private fun replaceFragment(newFragment: Fragment) {
        val supportFragmentManager = activity!!.supportFragmentManager
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, newFragment)
            addToBackStack(null)
            commit()
        }
    }

}