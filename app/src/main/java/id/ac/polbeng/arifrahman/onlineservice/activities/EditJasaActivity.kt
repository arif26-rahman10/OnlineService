package id.ac.polbeng.arifrahman.onlineservice.activities

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import id.ac.polbeng.arifrahman.onlineservice.R
import id.ac.polbeng.arifrahman.onlineservice.databinding.ActivityEditJasaBinding
import id.ac.polbeng.arifrahman.onlineservice.helpers.Config
import id.ac.polbeng.arifrahman.onlineservice.models.DefaultResponse
import id.ac.polbeng.arifrahman.onlineservice.models.Jasa
import id.ac.polbeng.arifrahman.onlineservice.services.JasaService
import id.ac.polbeng.arifrahman.onlineservice.services.ServiceBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class EditJasaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditJasaBinding
    private var imageFile: MultipartBody.Part? = null
    private lateinit var jasa: Jasa

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditJasaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Menerima data jasa
        jasa = intent.getSerializableExtra(Config.EXTRA_JASA) as Jasa
        displayJasaData()

        // Tombol pilih gambar
        binding.btnCari.setOnClickListener {
            ImagePicker.with(this)
                .compress(512)
                .maxResultSize(540, 540)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }

        // Tombol Edit Jasa
        binding.btnEditJasa.setOnClickListener {
            editJasa()
        }

        // Tombol Hapus Jasa
        binding.btnHapusJasa.setOnClickListener {
            deleteJasa()
        }
    }

    private fun displayJasaData() {
        binding.etNamaJasa.setText(jasa.namaJasa)
        binding.etDeskripsiSingkat.setText(jasa.deskripsiSingkat)
        binding.etUraianDeskripsi.setText(jasa.uraianDeskripsi)
        binding.tvRating.text = jasa.rating.toString()
        binding.tvImage.text = jasa.gambar
        Glide.with(this)
            .load(Config.IMAGE_URL + jasa.gambar)
            .error(R.drawable.baseline_broken_image_24)
            .into(binding.imgJasa)
    }

    private fun editJasa() {
        val namaJasa = binding.etNamaJasa.text.toString()
        val deskripsiSingkat = binding.etDeskripsiSingkat.text.toString()
        val uraianDeskripsi = binding.etUraianDeskripsi.text.toString()

        // Validasi input
        if (TextUtils.isEmpty(namaJasa)) {
            binding.etNamaJasa.error = "Nama jasa tidak boleh kosong!"
            return
        }
        if (TextUtils.isEmpty(deskripsiSingkat)) {
            binding.etDeskripsiSingkat.error = "Deskripsi singkat tidak boleh kosong!"
            return
        }
        if (TextUtils.isEmpty(uraianDeskripsi)) {
            binding.etUraianDeskripsi.error = "Uraian deskripsi tidak boleh kosong!"
            return
        }

        // Validasi gambar
        if (imageFile == null) {
            Toast.makeText(this, "Silakan pilih gambar terlebih dahulu!", Toast.LENGTH_SHORT).show()
            return
        }

        val jasaService = ServiceBuilder.buildService(JasaService::class.java)
        val reqId = jasa.idJasa.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val reqNamaJasa = namaJasa.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val reqDeskripsiSingkat = deskripsiSingkat.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val reqUraianDeskripsi = uraianDeskripsi.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val call = jasaService.editJasaReplaceImage(imageFile!!, reqId, reqNamaJasa, reqDeskripsiSingkat, reqUraianDeskripsi)
        sendRequest(call, "Jasa berhasil diperbarui!")
    }

    private fun deleteJasa() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Hapus Jasa")
        builder.setMessage("Apakah Anda yakin ingin menghapus jasa ini?")
        builder.setPositiveButton("Ya") { _, _ ->
            val jasaService = ServiceBuilder.buildService(JasaService::class.java)
            val call = jasaService.deleteService(jasa.idJasa)
            sendRequest(call, "Jasa berhasil dihapus!")
        }
        builder.setNegativeButton("Tidak") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun <T> sendRequest(call: Call<T>, successMessage: String) {
        showLoading(true)
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                showLoading(false)
                if (response.isSuccessful) {
                    Toast.makeText(this@EditJasaActivity, successMessage, Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditJasaActivity, "Gagal memperbarui jasa", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                showLoading(false)
                Toast.makeText(this@EditJasaActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val imageUri: Uri = result.data!!.data!!
                val file = File(imageUri.path!!)
                imageFile = MultipartBody.Part.createFormData(
                    "file", file.name, file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                )
                Glide.with(this).load(imageUri).into(binding.imgJasa)
                binding.tvImage.text = file.name
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
