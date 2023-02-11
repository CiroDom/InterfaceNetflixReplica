package com.cdom.netflixremaketa.filme_actv.recy_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cdom.netflixremaketa.databinding.ItemSimilarBinding
import com.cdom.netflixremaketa.model.Filme
import com.squareup.picasso.Picasso

class AdapterSimil(
    private val listaFilmes: List<Filme>,
): RecyclerView.Adapter<AdapterSimil.VHolder>() {

    inner class VHolder(val binding: ItemSimilarBinding): RecyclerView.ViewHolder(binding.root){
        fun especificar(filmeSugest: Filme){
            val capaSugest = binding.imgCapa

            Picasso.get().load(filmeSugest.urlCapa).into(capaSugest)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        val view = ItemSimilarBinding.inflate(LayoutInflater.from(parent.context), parent, false)

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