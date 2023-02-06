package com.cdom.netflixremaketa.main_actv.recy_view_vert

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cdom.netflixremaketa.databinding.ItemCategoriaBinding
import com.cdom.netflixremaketa.model.Categoria
import com.cdom.netflixremaketa.main_actv.recy_view_hori.AdapterFilmes

class AdapterCateg(
    private val categorias: List<Categoria>,
    private val cliqueImagem: (Int) -> Unit
): RecyclerView.Adapter<AdapterCateg.VHolder>() {

    inner class VHolder(private val binding: ItemCategoriaBinding): RecyclerView.ViewHolder(binding.root){
        fun especificar(categoria: Categoria){
            val titulo = binding.txtTituloCateg
            val rvCategoria = binding.rvHori

            titulo.text = categoria.nome
            rvCategoria.layoutManager = LinearLayoutManager(binding.root.context, RecyclerView.HORIZONTAL, false)
            rvCategoria.adapter = AdapterFilmes(categoria.filmes, cliqueImagem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        val view = ItemCategoriaBinding.inflate(LayoutInflater.from(parent.context))
        return VHolder(view)
    }

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        val categEspecifica = categorias[position]
        holder.especificar(categEspecifica)
    }

    override fun getItemCount(): Int {
        return categorias.size
    }

}