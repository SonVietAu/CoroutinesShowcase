package haylahay.showcase

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import haylahay.showcase.ui.main.MainViewModel
import haylahay.showcase.ui.main.fragment.MainFragment

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        ViewModelProvider(this).get(MainViewModel::class.java).apply {
            setLottoNumberRepository((application as CSApplication).lottoNumberRepository)
            loadFromLocalDB()
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
        }
    }
}