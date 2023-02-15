package com.cdom.netflixremaketa.main_actv.recy_view_hori

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cdom.netflixremaketa.R
import com.cdom.netflixremaketa.databinding.ItemFilmeBinding
import com.cdom.netflixremaketa.model.Filme
import com.squareup.picasso.Picasso

class AdapterFilmes(
    private val listaFilmes: List<Filme>,
    private val cliqueImagem: ((Int) -> Unit)? = null
): RecyclerView.Adapter<AdapterFilmes.VHolder>() {

    inner class VHolder(private val binding: ItemFilmeBinding): RecyclerView.ViewHolder(binding.root){
        fun especificar(filmeEspecifico: Filme){
            val imgCapa = binding.imgCapa
            imgCapa.setOnClickListener {
                cliqueImagem?.invoke(filmeEspecifico.id)
            }

            Picasso
                .get()
                .load(filmeEspecifico.urlCapa)
                .placeholder(R.drawable.placeholder_bg) // <=== AQUI VAI CARREGAR NOVAMENTE O placeholder
                .into(imgCapa)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        val view = ItemFilmeBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return VHolder(view)
    }

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        val filmeEspecifico = listaFilmes[position]
        holder.especificar(filmeEspecifico)
    }

    override fun getItemCount(): Int {
        return listaFilmes.size
    }
}