package com.cdom.netflixremaketa.filme_actv

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
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
import com.cdom.netflixremaketa.model.FilmeDetal
import com.cdom.netflixremaketa.util.TaskFilmeDetal
import com.squareup.picasso.Picasso

class FilmeActivity : AppCompatActivity(), TaskFilmeDetal.Callback {

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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == androidx.appcompat.R.id.home){
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun naPreExecucao() {
        progBar.visibility = View.VISIBLE
    }

    override fun noResultado(filmeDetal: FilmeDetal) {
        progBar.visibility = View.GONE

        titulo.text = filmeDetal.filme.titulo
        desc.text = filmeDetal.filme.desc
        elenco.text = getString(R.string.elenco, filmeDetal.filme.elenco)

        filmes.clear() //por precaução/boa prática
        filmes.addAll(filmeDetal.similares)
        adapter.notifyDataSetChanged()

        val id = intent?.getIntExtra("id", 0) ?: throw IllegalStateException("id não encontrado")
        val url = "https://api.tiagoaguiar.co/netflixapp/movie/$id?apiKey=7d3692ac-213c-4959-8ec2-b2e1fba49efb"
        TaskFilmeDetal(this).executar(url)

        val capaEmBitmap = Picasso.get().load(filmeDetal.filme.urlCapa).get()
        val capaEmDrawable = BitmapDrawable(resources, capaEmBitmap)
        val layerDrawable = ContextCompat.getDrawable(this, R.drawable.sombras) as LayerDrawable
        layerDrawable.setDrawableByLayerId(R.id.capa_drawable, capaEmDrawable)
        val imgFilme = binding.imgFilme
        imgFilme.setImageDrawable(layerDrawable)
    }

    override fun naFalha(mensagem: String) {
        progBar.visibility = View.GONE
        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show()
    }
}