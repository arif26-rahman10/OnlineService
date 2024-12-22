package id.ac.polbeng.arifrahman.onlineservice.fragments


import android.content.Intent
import id.ac.polbeng.arifrahman.onlineservice.activities.AddJasaActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import id.ac.polbeng.arifrahman.onlineservice.adapters.JasaAdapter
import id.ac.polbeng.arifrahman.onlineservice.databinding.FragmentServiceBinding
import id.ac.polbeng.arifrahman.onlineservice.helpers.SessionHandler
import id.ac.polbeng.arifrahman.onlineservice.models.Jasa
import id.ac.polbeng.arifrahman.onlineservice.models.JasaResponse
import id.ac.polbeng.arifrahman.onlineservice.services.JasaService
import id.ac.polbeng.arifrahman.onlineservice.services.ServiceBuilder
import retrofit2.Call
import retrofit2.Response

class ServiceFragment : Fragment() {
    private var _binding: FragmentServiceBinding? = null
    private val binding get() = _binding!!
    private lateinit var session : SessionHandler
    private lateinit var jasaAdapter: JasaAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        session = SessionHandler(requireContext())
        jasaAdapter = JasaAdapter()
        _binding = FragmentServiceBinding.inflate(inflater, container,
            false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.rvData?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = jasaAdapter
        }
        _binding?.fabAddJasa?.setOnClickListener {
            val intent = Intent(context, AddJasaActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        loadService()
    }

    private fun loadService() {
        val jasaService: JasaService =
            ServiceBuilder.buildService(JasaService::class.java)
        val requestCall: Call<JasaResponse> =
            jasaService.getJasaUser(session.getUserId())

        showLoading(true)

        requestCall.enqueue(object : retrofit2.Callback<JasaResponse>{
            override fun onFailure(call: Call<JasaResponse>, t: Throwable)
            {
                showLoading(false)
                Toast.makeText(context, "Error terjadi ketika sedang mengambil data jasa: " + t.toString(), Toast.LENGTH_LONG).show()
            }
            override fun onResponse(
                call: Call<JasaResponse>,
                response: Response<JasaResponse>
            ) {
                showLoading(false)
                if(!response.body()?.error!!) {
                    val servicesResponse: JasaResponse? = response.body()
                    servicesResponse?.let {
                        val daftarJasa: ArrayList<Jasa> =
                            servicesResponse.data
                        jasaAdapter.setData(daftarJasa)
                        jasaAdapter.setOnItemClickCallback(object :
                            JasaAdapter.OnItemClickCallback {
                            override fun onItemClicked(data: Jasa) {
                                Toast.makeText(context, "Service clicked${data.namaJasa}", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }else{
                    Toast.makeText(context, "Gagal menampilkan data jasa:" + response.body()?.message, Toast.LENGTH_LONG).show()
                }
            }
        });
    }

    private fun showLoading(isLoading: Boolean) {
        _binding?.progressBar?.visibility = if (isLoading) View.VISIBLE
        else View.GONE
    }
}