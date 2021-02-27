package haylahay.showcase.model

import haylahay.showcase.model.room.LottoNumbers
import haylahay.showcase.model.room.LottoNumbersDao
import java.net.URL

class LottoNumberRepository(private val lottoNumbersDao: LottoNumbersDao) {

    private val LOTTERY_NUMBERS_URL = "https://sonvietau.info/GetLottoNumber.php"

    fun getLottoNumberSetsFromDB(): List<LottoNumbers> {
        return lottoNumbersDao.getAll()
    }

    fun getLottoNumberFromServer(lottoNumbers: LottoNumbers? = null, isToSave: Boolean = true): LottoNumbers {

        val url = URL(LOTTERY_NUMBERS_URL)
        var numbers = url.readText()
        // Ensure new number set
        while (lottoNumbersDao.find(numbers) != null)
            numbers = url.readText()

        return if (lottoNumbers == null) {
            val newLottoNumbers = LottoNumbers(numbers)
            if (isToSave)
                lottoNumbersDao.insertAll(newLottoNumbers)
            newLottoNumbers
        } else {
            lottoNumbers.numbers = numbers
            if (isToSave)
                lottoNumbersDao.update(lottoNumbers)
            lottoNumbers
        }

    }

    fun update(lottoNumbers: LottoNumbers) = lottoNumbersDao.update(lottoNumbers)

    fun deleteNumbers(lottoNumbers: LottoNumbers) = lottoNumbersDao.delete(lottoNumbers)

}