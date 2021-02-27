package haylahay.showcase.model.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(LottoNumbers::class), version = 1)
abstract class CSDatabase : RoomDatabase() {
    abstract fun lottoNumbersDao(): LottoNumbersDao
}