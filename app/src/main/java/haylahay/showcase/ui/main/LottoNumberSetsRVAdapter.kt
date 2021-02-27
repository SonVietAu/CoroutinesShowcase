package haylahay.showcase.ui.main

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import haylahay.showcase.R
import haylahay.showcase.model.room.LottoNumbers
import kotlinx.coroutines.*

class LottoNumberSetsRVAdapter(
    var lottoNumberSets: List<LottoNumbers>,
    val refreshNumbers: (LottoNumbers) -> Unit,
    val deleteNumbers: (LottoNumbers) -> Unit,
    // 'refreshWithoutPosting' will allow for coroutine initiation and control from the view. The advantage of this process is that only one row of the recyclerView has to be updated which perhaps it should be since only one row is being refreshed
    val refreshWithoutPosting: ((LottoNumbers) -> Unit)? = null) : RecyclerView.Adapter<LottoNumbersVH>() {
    /**
     * Called when RecyclerView needs a new [ViewHolder] of the given type to represent
     * an item.
     *
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     *
     *
     * The new ViewHolder will be used to display items of the adapter using
     * [.onBindViewHolder]. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary [View.findViewById] calls.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new ViewHolder that holds a View of the given view type.
     * @see .getItemViewType
     * @see .onBindViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LottoNumbersVH {
        return LottoNumbersVH(LayoutInflater.from(parent.context)
            .inflate(R.layout.numbers_holder, parent, false))
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the [ViewHolder.itemView] to reflect the item at the given
     * position.
     *
     *
     * Note that unlike [android.widget.ListView], RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the `position` parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use [ViewHolder.getAdapterPosition] which will
     * have the updated adapter position.
     *
     * Override [.onBindViewHolder] instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: LottoNumbersVH, position: Int) {
        val lottoNumbers = lottoNumberSets[position]

        holder.numbersChb.isChecked = lottoNumbers.isSelectedRefreshing

        if (lottoNumbers.isRefreshing) {
            holder.numbersChb.text =
                holder.itemView.context.getText(R.string.number_refreshing_message)
            holder.refreshBtn.isEnabled = false
        } else {
            holder.numbersChb.text = lottoNumbers.numbers
            holder.refreshBtn.isEnabled = true
        }

        holder.numbersChb.setOnCheckedChangeListener { _, isChecked ->
            lottoNumbers.isSelectedRefreshing = isChecked
        }

        holder.refreshBtn.setOnClickListener {
            holder.refreshBtn.isEnabled = false
            holder.deleteBtn.isEnabled = false

            if (refreshWithoutPosting != null) {

                /*
                val job = Job()
                val uiScope = CoroutineScope(Dispatchers.Main + job)
                uiScope.launch {
                // or
                */
                GlobalScope.launch(Dispatchers.Main) {
                    withContext(Dispatchers.IO) {
                        refreshWithoutPosting.invoke(lottoNumbers)
                    }
                    holder.numbersChb.text = lottoNumbers.numbers
                    holder.refreshBtn.isEnabled = true
                    holder.deleteBtn.isEnabled = true
                }
            } else {
                refreshNumbers(lottoNumbers)
            }
            holder.numbersChb.text = holder.itemView.context.getText(R.string.number_refreshing_message)
        }

        holder.deleteBtn.setOnClickListener {
            holder.refreshBtn.isEnabled = false
            holder.deleteBtn.isEnabled = false
            deleteNumbers(lottoNumbers)
            holder.numbersChb.text = holder.itemView.context.getText(R.string.number_refreshing_message)
        }
    }

    override fun onViewRecycled(holder: LottoNumbersVH) {
        super.onViewRecycled(holder)
        holder.numbersChb.setOnCheckedChangeListener(null)
    }

    fun resetDataset(lottoNumberSets: List<LottoNumbers>) {
        this.lottoNumberSets = lottoNumberSets
        this.notifyDataSetChanged()
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int =
        lottoNumberSets.size

}

class LottoNumbersVH(val lottoNumbersV: View) : RecyclerView.ViewHolder(lottoNumbersV) {

    val numbersChb: CheckBox by lazy { lottoNumbersV.findViewById(R.id.numbersChb) }
    val refreshBtn: ImageButton by lazy { lottoNumbersV.findViewById(R.id.refreshBtn) }
    val deleteBtn: ImageButton by lazy { lottoNumbersV.findViewById(R.id.deleteBtn) }

}