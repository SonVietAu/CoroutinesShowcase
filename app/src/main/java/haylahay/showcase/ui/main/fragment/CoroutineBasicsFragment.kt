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
import kotlinx.android.synthetic.main.fragment_coroutine_basics.informationBtn
import kotlinx.android.synthetic.main.fragment_coroutine_basics.informationTV
import kotlinx.android.synthetic.main.fragment_coroutine_basics.lottoNumberSetsRV
import kotlinx.android.synthetic.main.fragment_coroutine_basics.refreshAllBtn
import kotlinx.android.synthetic.main.fragment_coroutine_basics.refreshCheckedBtn
import kotlinx.android.synthetic.main.fragment_coroutine_basics.stopRefreshBtn
import kotlinx.android.synthetic.main.fragment_coroutines.*

class CoroutineBasicsFragment : CSBaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_coroutine_basics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lottoNumberSetsRV.layoutManager = LinearLayoutManager(this.context)

        coroutineNextBtn.setOnClickListener {
            viewModel.getNextWithCoroutine()
        }

        threadNextBtn.setOnClickListener {
            viewModel.getNextWithoutCoroutine()
        }

        refreshCheckedBtn.setOnClickListener {
            viewModel.refreshSelectedLottoNumbers()
        }

        refreshAllBtn.setOnClickListener {
            viewModel.refreshAllLottoNumbers()
        }

        deleteCheckedBtn.setOnClickListener {
            viewModel.deleteSelectedLottoNumbers()
        }

        deleteAllBtn.setOnClickListener {
            viewModel.deleteAllLottoNumbers()
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
                lottoNumberSetsRV.adapter = LottoNumberSetsRVAdapter(it, viewModel::refreshSets, viewModel::deleteSets, viewModel::refreshWithoutPosting)
            }

        })

    }

}