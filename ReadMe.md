# Coroutines Showcase

## Introduction

Coroutines simplify async coding. This showcase demonstrates my knowledge and usage of Coroutines.
    
## The Application

The application simply get multiple sets (one set at a time) of lottery number from a server, save them in a Room database and display them in a RecyclerView. Any set can also be individually deleted or refreshed from the server. The sets can also be deleted and refreshed concurrently. 

## Implementation

With four main coroutine features to demonstrate, four fragments were created. The fragments demonstrate coroutine basics, Deferred class, Channel communication and coroutine cancellation. Each fragment is simply a view with buttons, it is the MainViewModel that holds all but one the coroutine executables. 

### Room Data Load

The first coroutine this application execute is the loading of the initial data (aka lotto number) sets from a Room database via the LottoNumberRepository. The Room database and repository implementations follow the standard recommendation. The loading is simply get the data and then post the data, only the process is completed within a coroutine scope. 

The implementation can be seen in the MainViewModel 'loadFromLocalDB()' function.

### Simple Network Request

The second coroutine is a request to the server for the next set of lotto number, namely 'getNextWithCoroutine()'. The process involves getting the next set, add it to the current sets and re-post for displaying, all within the coroutine scope 'viewModelScope'.  The launch is given an IO Dispatcher, taking the execution block off the main thread to perform the network request. 

The second implementation has a thread version, namely 'getNextWithoutCoroutine()'. This version execute in a thread rather than a coroutine scope. The coding is the same for this simple network request, hence there not much different in benefit of either implementations should there only be single request at any given time.

Another implementation to refresh data from the server allows views to control coroutines. This implementation is coded in the LottoNumberSetsRVAdapter where the 'holder' refresh button get 'setOnClickListener'. Though the viewModelScope is already given, executing coroutine from the MainViewModel requires a posting of the whole list of data and a re-population of the whole recyclerView. The advantage of allowing the view to control the coroutine process is that only one row of the recyclerView need to be repopulated as only one row is being refreshed. 

### Concurrency

The next coroutine handles concurrent requests to the server, namely the function 'refreshSets(...)'. The function one parameter is a vararg of LottoNumbers enabling the function to refresh one or a concurrent of an extreme number of data. Concurrency is achieved by launching a new coroutine within the top level coroutine for the size of the vararg. This is best viewed from the 'Basics' screen once the 'Refresh All' is clicked. 

Concurrency allows for an extremely large number of tasks to be executed at the same time while servers can only service a limited number of requests. The execution of coroutine concurrency ran into such limitation resulting in a ConnectException. The 'delayToAvoidDenialOfService(refreshSize)' function was implemented to limit the number of concurrent execution to avoid such exception.

Concurrency is also used for deleting data. However, the usual 'launch' within a coroutine for concurrency resulted in an IndexOutOfBoundsException when the data set for the recycler view changed before the view can display the list. To ensure that the view will only display the list once the list has settle, the Deferred class is use to 'awaitAll' the current deletions. The implementation can be found in the method 'deleteSets(...)'.

### Deferred Usage

Deferred object is simply the result of coroutine 'async' execution. To get the object within a Deferred, 'await' would have to be called. How 'Deferred' can be used practically is yet to be found. 

Though 'async' seem to have some concurrency elements, the 'awaitAll' make 'Deferred' less useful. 

Two implementations using Deferred are 'getNextWithDeferred()' and 'refreshAllLottoNumbersWithDeferredAndCollection()'. 

### Channel Usage

Channel is a way for coroutines to pass data to other coroutines. This is demonstrated with the CoroutineChannelFragment and the MainViewModel 'refreshAllLottoNumbersWithChannel()' function. In the function, child coroutines 'send' its refreshed LottoNumbers object to the root-level coroutine via their shared 'channel'. The effect of Channel can be seen with results returning in random order from refreshment.

### Coroutine Cancellation

Though coroutine 'cancellation' are being used in the refresh all process of the section basics, Deferred and Channel, they are more of a 'stop' rather than a cancellation. The idea of a cancellation (as stated in a Android Kotlin tutorial) is that the whole process get cancelled and no changes will be made. This is implemented in the CoroutineCancellationFragment and the MainViewModel 'refreshAllLottoNumbersButCancellable()', 'refreshNumbersButNotSave(...)' and 'cancelRefreshingCompletely()' functions. The process holds and not save new data till all the child coroutines complete. Users can then cancel the whole 'Refresh All' should the refreshments have not completed.

## Acknowledgement

This showcase is derived from 'Use Kotlin Coroutines in your Android App' by Googler in [Codelab](https://developer.android.com/codelabs/kotlin-coroutines?continue=https%3A%2F%2Fdeveloper.android.com%2Fcourses%2Fpathways%2Fandroid-coroutines%23codelab-https%3A%2F%2Fdeveloper.android.com%2Fcodelabs%2Fkotlin-coroutines#0)