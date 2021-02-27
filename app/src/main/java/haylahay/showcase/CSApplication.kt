package haylahay.showcase

import android.app.Application
import androidx.room.Room
import haylahay.showcase.model.LottoNumberRepository
import haylahay.showcase.model.room.CSDatabase

class CSApplication : Application() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            CSDatabase::class.java, "database-name"
        ).build()
    }

    val lottoNumberRepository by lazy {
        LottoNumberRepository(db.lottoNumbersDao())
    }

}