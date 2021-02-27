package haylahay.showcase.model.room

import androidx.room.*

@Dao
interface LottoNumbersDao {
    @Query("SELECT * FROM LottoNumbers")
    fun getAll(): List<LottoNumbers>


    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAll(vararg lottoNumbers: LottoNumbers)

    @Update
    fun update(lottoNumbers: LottoNumbers)

    @Delete
    fun delete(lottoNumbers: LottoNumbers)

    @Query("select * from LottoNumbers where numbers = :numbers")
    fun find(numbers: String): LottoNumbers?
}