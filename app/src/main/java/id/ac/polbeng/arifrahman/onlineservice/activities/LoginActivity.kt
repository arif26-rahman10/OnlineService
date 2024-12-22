package id.ac.polbeng.arifrahman.onlineservice.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import id.ac.polbeng.arifrahman.onlineservice.databinding.ActivityLoginBinding
import id.ac.polbeng.arifrahman.onlineservice.helpers.SessionHandler
import id.ac.polbeng.arifrahman.onlineservice.models.LoginResponse
import id.ac.polbeng.arifrahman.onlineservice.models.User
import id.ac.polbeng.arifrahman.onlineservice.services.ServiceBuilder
import id.ac.polbeng.arifrahman.onlineservice.services.UserService
import retrofit2.Call
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var session: SessionHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        session = SessionHandler(applicationContext)

        // Jika pengguna sudah login, langsung ke MainActivity
        if (session.isLoggedIn()) {
            loadMainActivity()
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Navigasi ke RegisterActivity
        binding.tvDaftar.setOnClickListener {
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Logika login
        binding.btnLogin.setOnClickListener { handleLogin() }
    }

    private fun handleLogin() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        if (TextUtils.isEmpty(email)) {
            binding.etEmail.error = "Email tidak boleh kosong!"
            binding.etEmail.requestFocus()
            return
        }

        if (TextUtils.isEmpty(password)) {
            binding.etPassword.error = "Password tidak boleh kosong!"
            binding.etPassword.requestFocus()
            return
        }

        val filter = HashMap<String, String>()
        filter["email"] = email
        filter["password"] = password

        val userService: UserService = ServiceBuilder.buildService(UserService::class.java)
        val requestCall: Call<LoginResponse> = userService.loginUser(filter)

        showLoading(true)

        requestCall.enqueue(object : retrofit2.Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(
                    this@LoginActivity,
                    "Error terjadi ketika sedang login: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                showLoading(false)
                if (response.body()?.error == false) {
                    val loginResponse: LoginResponse? = response.body()
                    loginResponse?.let {
                        val user: User = loginResponse.data
                        session.saveUser(user)
                        Toast.makeText(
                            this@LoginActivity,
                            "Pengguna ${user.nama} dengan email ${user.email} berhasil login!",
                            Toast.LENGTH_LONG
                        ).show()
                        loadMainActivity()
                    }
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Gagal login: ${response.body()?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    private fun loadMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
