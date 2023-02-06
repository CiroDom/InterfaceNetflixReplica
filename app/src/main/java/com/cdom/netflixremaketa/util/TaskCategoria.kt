package com.cdom.netflixremaketa.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.cdom.netflixremaketa.model.Categoria
import com.cdom.netflixremaketa.model.Filme
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class TaskCategoria(private val callback: Callback) {

    // Algumas coisas feitas na Tread paralela devem ser
    // enviadas para a UI Tread, o handler serve para isso
    private val handler = Handler(Looper.getMainLooper())

    interface Callback{
        fun naPreExecucao()
        fun noResultado(categorias: List<Categoria>)
        fun naFalha(mensagem: String)
    }

    fun executar(url: String){
        callback.naPreExecucao()

        val threadParalela = Executors.newSingleThreadExecutor()
        threadParalela.execute {
            var urlConnection: HttpsURLConnection? = null
            var stream: InputStream? = null

            try {
                val requestURL = URL(url) //abrir URL - requisição
                urlConnection = requestURL.openConnection() as HttpsURLConnection //abrir conexão
                urlConnection.readTimeout = 2000 //tempo de leitura
                urlConnection.connectTimeout = 2000 //tempo de conexão
                val statusCode = urlConnection.responseCode
                if (statusCode > 400){
                    throw IOException("Erro na comunicação com o servidor")
                }
                stream = urlConnection.inputStream
                val jsonEmString = stream.bufferedReader().use {it.readText()}
                val categorias = transfEmCategorias(jsonEmString)
                handler.post{
                    callback.noResultado(categorias)
                }
            }

            catch (e: IOException){
                val mensagem = e.message ?: "erro desconhecido"
                Log.e("Teste", mensagem, e)
                handler.post {
                    callback.naFalha(mensagem)
                }
            }
            finally {
                urlConnection?.disconnect()
                stream?.close()
            }
        }
    }

    private fun transfEmCategorias(jsonEmString: String): List<Categoria>{
        val categorias = mutableListOf<Categoria>()

        val jsonRaiz = JSONObject(jsonEmString) //acessar o objeto json
        val categoriasEmJson = jsonRaiz.getJSONArray("category") //catar o vetor das categorias
        for (i in 0 until categoriasEmJson.length()){
            val categoriaJson = categoriasEmJson.getJSONObject(i)

            val titulo = categoriaJson.getString("title")
            val filmesEmJson = categoriaJson.getJSONArray("movie")

            val filmes = transfEmFilmes(filmesEmJson)

            categorias.add(Categoria(titulo, filmes))
        }

        return categorias
    }

    private fun transfEmFilmes(filmesEmJson: JSONArray): List<Filme> {
        val filmes = mutableListOf<Filme>()
        for (j in 0 until filmesEmJson.length()) {
            val filmeEmJson = filmesEmJson.getJSONObject(j)

            val idFilme = filmeEmJson.getInt("id")
            val capaFilme = filmeEmJson.getString("cover_url")

            filmes.add(Filme(idFilme, capaFilme))
        }
        return filmes
    }
}