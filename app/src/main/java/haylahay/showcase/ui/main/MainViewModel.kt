package haylahay.showcase.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import haylahay.showcase.model.LottoNumberRepository
import haylahay.showcase.model.room.LottoNumbers
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.util.*
import kotlin.concurrent.thread

class MainViewModel: ViewModel() {

    private lateinit var lottoNumberRepository: LottoNumberRepository
    fun setLottoNumberRepository(lottoNumberRepository: LottoNumberRepository) {
        this.lottoNumberRepository = lottoNumberRepository
    }

    private val _lottoNumberSets = Vector<LottoNumbers>()

    private val _numberSetsLiveData = MutableLiveData<List<LottoNumbers>>()
    val numberSetsLiveData: LiveData<List<LottoNumbers>>
        get() = _numberSetsLiveData

    var multiRefreshJob: Job? = null

    fun loadFromLocalDB() {
        viewModelScope.launch(Dispatchers.IO) {
            val lottoNumberSets = lottoNumberRepository.getLottoNumberSetsFromDB()
            _lottoNumberSets.removeAllElements()
            _lottoNumberSets.addAll(lottoNumberSets)
            _numberSetsLiveData.postValue(_lottoNumberSets)
        }
    }

    fun getNextWithCoroutine() {
        viewModelScope.launch(Dispatchers.IO) {
            val lottoNumbers = lottoNumberRepository.getLottoNumberFromServer()
            _lottoNumberSets.addElement(lottoNumbers)
            _numberSetsLiveData.postValue(_lottoNumberSets)
        }
    }

    fun getNextWithoutCoroutine() {
        thread {
            val lottoNumbers = lottoNumberRepository.getLottoNumberFromServer()
            _lottoNumberSets.addElement(lottoNumbers)
            _numberSetsLiveData.postValue(_lottoNumberSets)
        }
    }

    fun refreshSets(vararg lottoNumbersVarargs: LottoNumbers): Job =
        viewModelScope.launch(Dispatchers.IO) {

            val refreshSize = lottoNumbersVarargs.size
            lottoNumbersVarargs.forEach {
                    lottoNumbers ->
                launch {
                    lottoNumbers.isRefreshing = true
                    _numberSetsLiveData.postValue(_lottoNumberSets)
                    lottoNumberRepository.getLottoNumberFromServer(lottoNumbers)

                    lottoNumbers.isRefreshing = false
                    _numberSetsLiveData.postValue(_lottoNumberSets)
                }

                delayToAvoidDenialOfService(refreshSize)

            }
        }

    // This function refresh without posting the result, it is just there
    fun refreshWithoutPosting(lottoNumbers: LottoNumbers) {
        lottoNumberRepository.getLottoNumberFromServer(lottoNumbers)
    }

    fun deleteSets(vararg lottoNumbersVarargs: LottoNumbers): Job =
        viewModelScope.launch(Dispatchers.IO) {

            val defers = lottoNumbersVarargs.map {
                    lottoNumbers ->
                async {
                    lottoNumberRepository.deleteNumbers(lottoNumbers)
                    _lottoNumberSets.remove(lottoNumbers)
                    _numberSetsLiveData.postValue(_lottoNumberSets)
                }
            }

            defers.awaitAll()

            _numberSetsLiveData.postValue(_lottoNumberSets)
        }

    fun refreshSelectedLottoNumbers() {

        val selected = _lottoNumberSets.filter {
            it.isSelectedRefreshing
        }

        multiRefreshJob = refreshSets(*selected.map { it }.toTypedArray())
    }

    fun refreshAllLottoNumbers() {
        multiRefreshJob = refreshSets(*_lottoNumberSets.map { it }.toTypedArray())
    }

    fun deleteSelectedLottoNumbers() {

        val selected = _lottoNumberSets.filter {
            it.isSelectedRefreshing
        }

        multiRefreshJob = deleteSets(*selected.map { it }.toTypedArray())
    }

    fun deleteAllLottoNumbers() {
        multiRefreshJob = deleteSets(*_lottoNumberSets.map { it }.toTypedArray())
    }

    fun getNextWithDeferred() {
        viewModelScope.launch(Dispatchers.IO) {
            val deferred =
                async {
                    lottoNumberRepository.getLottoNumberFromServer()
                }
            val lottoNumbers = deferred.await()
            _lottoNumberSets.addElement(lottoNumbers)
            _numberSetsLiveData.postValue(_lottoNumberSets)
        }
    }

    fun refreshAllLottoNumbersWithDeferredAndCollection() {

        val refreshSize = _lottoNumberSets.size

        multiRefreshJob = viewModelScope.launch {

            val defers = _lottoNumberSets.map {

                delayToAvoidDenialOfService(refreshSize)

                async(Dispatchers.IO) {
                    it.isRefreshing = true
                    _numberSetsLiveData.postValue(_lottoNumberSets)
                    lottoNumberRepository.getLottoNumberFromServer(it)
                }
            }

            defers.awaitAll().forEach {
                it.isRefreshing = false
                // No different if posting values here as the 'awaitAll' means that even if one is finished, this forEach block will not execute till they all finished. So, the values are posted outside this loop.
                // _numberSetsLiveData.postValue(_lottoNumberSets)
            }

            _numberSetsLiveData.postValue(_lottoNumberSets)

        }
    }

    fun refreshAllLottoNumbersWithChannel() {

        val refreshSize = _lottoNumberSets.size

        viewModelScope.launch(Dispatchers.IO) {
            val channel = Channel<LottoNumbers>()

            val expectedCount = _lottoNumberSets.size
            launch {
                _lottoNumberSets.forEach {
                    launch {
                        it.isRefreshing = true
                        _numberSetsLiveData.postValue(_lottoNumberSets)
                        channel.send(lottoNumberRepository.getLottoNumberFromServer(it))
                    }

                    delayToAvoidDenialOfService(refreshSize)

                }
            }

            repeat(expectedCount) {
                val lottoNumbers = channel.receive()
                lottoNumbers.isRefreshing = false
                _numberSetsLiveData.postValue(_lottoNumberSets)
            }
        }

    }

    fun stopRefreshing() {
        multiRefreshJob?.cancel()
        _lottoNumberSets.forEach {
            it.isRefreshing = false
        }
        _numberSetsLiveData.postValue(_lottoNumberSets)
    }

    fun refreshAllLottoNumbersButCancellable() {
        multiRefreshJob = refreshNumbersButNotSave(*_lottoNumberSets.map { it }.toTypedArray())
    }

    private fun refreshNumbersButNotSave(vararg lottoNumbersVarargs: LottoNumbers): Job =
        viewModelScope.launch(Dispatchers.IO) {

            val refreshSize = _lottoNumberSets.size

            _lottoNumberSets.forEach {
                it.isRefreshing = true
            }
            _numberSetsLiveData.postValue(_lottoNumberSets)

            val newLottoNumbersJobs = lottoNumbersVarargs.map { lottoNumbers ->
                val newLottoNumbersJob = async {
                    lottoNumberRepository.getLottoNumberFromServer(lottoNumbers, false)
                    lottoNumbers.isRefreshing = false
                }

                delayToAvoidDenialOfService(refreshSize)

                newLottoNumbersJob

            }

            newLottoNumbersJobs.awaitAll()

            _lottoNumberSets.forEach {
                lottoNumberRepository.update(it)
            }

            _numberSetsLiveData.postValue(_lottoNumberSets)
        }

    fun cancelRefreshingCompletely() {
        multiRefreshJob?.cancel()
        loadFromLocalDB()
    }

    suspend fun delayToAvoidDenialOfService(refreshSize: Int) {
        // Tried and fail to limit the number of coroutines so try 'delay' and delay works
        // Delay to ensure no denial of service from too much https connection. Limiting should perhaps be done in the repository but this is where the looping occur. This delaying has a nice side effect for the GUI
        when {
            refreshSize > 10 && refreshSize < 20 -> delay(50)
            refreshSize >= 20 -> delay(200)
        }
    }

}