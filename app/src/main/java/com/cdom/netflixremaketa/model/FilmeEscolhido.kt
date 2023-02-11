package com.cdom.netflixremaketa.model

data class FilmeEscolhido(
    val filme: Filme,
    val similares: List<Filme>
)
