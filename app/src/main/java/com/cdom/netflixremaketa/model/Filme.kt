package com.cdom.netflixremaketa.model

data class Filme(
    val id: Int,
    val urlCapa: String,
    val titulo: String = "",
    val desc: String = "",
    val elenco: String = "",
)