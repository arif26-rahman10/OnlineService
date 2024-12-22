package id.ac.polbeng.arifrahman.onlineservice.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.ac.polbeng.arifrahman.onlineservice.databinding.ActivityAddJasaBinding

class AddJasaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddJasaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddJasaBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}