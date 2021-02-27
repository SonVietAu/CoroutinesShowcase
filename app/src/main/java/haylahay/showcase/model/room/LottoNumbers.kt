package haylahay.showcase.model.room

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["numbers"], unique = true)])
data class LottoNumbers(
    var numbers: String
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0

    @Ignore var isRefreshing = false
    @Ignore var isSelectedRefreshing = false
}
