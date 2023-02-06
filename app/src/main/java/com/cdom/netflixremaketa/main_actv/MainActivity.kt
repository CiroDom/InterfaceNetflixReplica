package com.cdom.netflixremaketa.main_actv

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.cdom.netflixremaketa.databinding.ActivityMainBinding
import com.cdom.netflixremaketa.filme_actv.FilmeActivity
import com.cdom.netflixremaketa.model.Categoria
import com.cdom.netflixremaketa.main_actv.recy_view_vert.AdapterCateg
import com.cdom.netflixremaketa.util.TaskCategoria

class MainActivity : AppCompatActivity(), TaskCategoria.Callback {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val progBar by lazy {
        binding.progBarMain
    }
    private val categorias = mutableListOf<Categoria>()
    private lateinit var adapter: AdapterCateg

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        val rvCateg = binding.rvVert
        adapter = AdapterCateg(categorias){ id ->
            val intent = Intent(this@MainActivity, FilmeActivity::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
        }
        rvCateg.layoutManager = LinearLayoutManager(this)
        rvCateg.adapter = adapter

        TaskCategoria(this).executar("https://api.tiagoaguiar.co/netflixapp/home?apiKey=7d3692ac-213c-4959-8ec2-b2e1fba49efb")
    }

    override fun naPreExecucao() {
        progBar.visibility = View.VISIBLE
    }

    override fun noResultado(categorias: List<Categoria>) {
        this.categorias.clear()
        this.categorias.addAll(categorias)
        adapter.notifyDataSetChanged()

        progBar.visibility = View.GONE
    }

    override fun naFalha(mensagem: String) {
        Toast.makeText(
            this,
            mensagem,
            Toast.LENGTH_SHORT
        ).show()
        progBar.visibility = View.GONE
    }
}