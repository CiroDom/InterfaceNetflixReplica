package com.cdom.netflixremaketa.filme_actv

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.cdom.netflixremaketa.R
import com.cdom.netflixremaketa.databinding.ActivityFilmeBinding
import com.cdom.netflixremaketa.filme_actv.recy_view.AdapterSimil
import com.cdom.netflixremaketa.model.Filme
import com.cdom.netflixremaketa.model.FilmeEscolhido
import com.cdom.netflixremaketa.util.TaskFilmeEscolhido
import com.squareup.picasso.Picasso
import java.util.concurrent.Executors

class FilmeActivity : AppCompatActivity(), TaskFilmeEscolhido.Callback {

    private val binding by lazy {
        ActivityFilmeBinding.inflate(layoutInflater)
    }
    private val titulo by lazy {
        binding.txtTituloFilme
    }
    private val desc by lazy {
        binding.txtDescFilme
    }
    private val elenco by lazy {
        binding.txtElencoFilme
    }
    private val rvFilmesSimil by lazy {
        binding.recyViewFilmesSimil
    }
    private val filmes by lazy {
        mutableListOf<Filme>()
    }
    private val progBar by lazy {
        binding.progBarFilmeDetal
    }
    private lateinit var adapter: AdapterSimil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null

        adapter = AdapterSimil(filmes)
        rvFilmesSimil.layoutManager = GridLayoutManager(this, 3)
        rvFilmesSimil.adapter = adapter

        val id = intent?.getIntExtra("id", 0) ?: throw IllegalStateException("id não encontrado")
        val url = "https://api.tiagoaguiar.co/netflixapp/movie/$id?apiKey=7d3692ac-213c-4959-8ec2-b2e1fba49efb"
        TaskFilmeEscolhido(this).executar(url)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun naPreExecucao() {
        progBar.visibility = View.VISIBLE
    }

    override fun noResultado(filmeEscolhido: FilmeEscolhido) {
        progBar.visibility = View.GONE

        titulo.text = filmeEscolhido.filme.titulo
        desc.text = filmeEscolhido.filme.desc
        elenco.text = getString(R.string.elenco, filmeEscolhido.filme.elenco)

        filmes.clear() //por precaução/boa prática
        filmes.addAll(filmeEscolhido.similares)
        adapter.notifyDataSetChanged()

        val handler = Handler(Looper.getMainLooper())
        val threadParalela = Executors.newSingleThreadExecutor()
        threadParalela.execute {
            val capaEmBitmap = Picasso.get().load(filmeEscolhido.filme.urlCapa).get()
            val capaEmDrawable = BitmapDrawable(resources, capaEmBitmap)
            val layerDrawable = ContextCompat.getDrawable(this, R.drawable.sombras) as LayerDrawable
            layerDrawable.setDrawableByLayerId(R.id.capa_drawable, capaEmDrawable)
            val imgFilme = binding.imgFilme
            handler.post {
                imgFilme.setImageDrawable(layerDrawable)
            }
        }
    }

    override fun naFalha(mensagem: String) {
        progBar.visibility = View.GONE

        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show()
    }
}