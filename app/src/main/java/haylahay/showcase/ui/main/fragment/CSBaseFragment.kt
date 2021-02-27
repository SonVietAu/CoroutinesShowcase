package haylahay.showcase.ui.main.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import haylahay.showcase.CSApplication
import haylahay.showcase.ui.main.LottoNumberSetsRVAdapter
import haylahay.showcase.ui.main.MainViewModel
import kotlinx.android.synthetic.main.main_fragment.*

abstract class CSBaseFragment : Fragment() {

    lateinit var viewModel: MainViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity!!.application as CSApplication)
        viewModel = ViewModelProvider(this.activity!!).get(MainViewModel::class.java)
        viewModel.setLottoNumberRepository((activity!!.application as CSApplication).lottoNumberRepository)

    }

}