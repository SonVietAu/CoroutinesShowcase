package haylahay.showcase.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import haylahay.showcase.R
import haylahay.showcase.ui.main.LottoNumberSetsRVAdapter
import kotlinx.android.synthetic.main.fragment_coroutine_basics.*
import kotlinx.android.synthetic.main.fragment_coroutines.*
import kotlinx.android.synthetic.main.fragment_coroutines.headerTV
import kotlinx.android.synthetic.main.fragment_coroutines.informationBtn
import kotlinx.android.synthetic.main.fragment_coroutines.informationTV
import kotlinx.android.synthetic.main.fragment_coroutines.lottoNumberSetsRV
import kotlinx.android.synthetic.main.fragment_coroutines.refreshAllBtn
import kotlinx.android.synthetic.main.fragment_coroutines.refreshCheckedBtn
import kotlinx.android.synthetic.main.fragment_coroutines.stopRefreshBtn

class CoroutineChannelFragment : CSBaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_coroutines, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        headerTV.text = "Channel"

        lottoNumberSetsRV.layoutManager = LinearLayoutManager(this.context)

        nextBtn.setOnClickListener {
            viewModel.getNextWithCoroutine()
        }

        refreshCheckedBtn.visibility = View.GONE

        refreshAllBtn.setOnClickListener {
            viewModel.refreshAllLottoNumbersWithChannel()
        }

        stopRefreshBtn.setOnClickListener {
            viewModel.stopRefreshing()
        }

        informationBtn.setOnClickListener {
            if (informationTV.visibility == View.VISIBLE) {
                informationBtn.text = "Information"
                informationTV.visibility = View.GONE
            } else {
                informationBtn.text = "Hide Info"
                informationTV.visibility = View.VISIBLE
            }
        }

   }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.numberSetsLiveData.observe(this.viewLifecycleOwner, Observer {
            if (lottoNumberSetsRV.adapter is LottoNumberSetsRVAdapter) {
                (lottoNumberSetsRV.adapter as LottoNumberSetsRVAdapter).resetDataset(it)
            } else {
                lottoNumberSetsRV.adapter = LottoNumberSetsRVAdapter(it, viewModel::refreshSets, viewModel::deleteSets)
            }

        })

    }

    override fun onResume() {
        super.onResume()
        informationTV.text = activity?.getText(R.string.channel_information)
    }
}