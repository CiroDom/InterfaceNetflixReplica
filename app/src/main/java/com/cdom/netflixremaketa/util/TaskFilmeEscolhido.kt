package com.cdom.netflixremaketa.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.cdom.netflixremaketa.model.Filme
import com.cdom.netflixremaketa.model.FilmeEscolhido
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class TaskFilmeEscolhido(private val callback: Callback) {

    // Algumas coisas feitas na Tread paralela devem ser
    // enviadas para a UI Tread, o handler serve para isso
    private val handler = Handler(Looper.getMainLooper())

    interface Callback{
        fun naPreExecucao()
        fun noResultado(filmeEscolhido: FilmeEscolhido)
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

                if (statusCode == 400){
                    stream = urlConnection.errorStream
                    val jsonEmString = stream.bufferedReader().use { it.readText() }
                    val jsonRaiz = JSONObject(jsonEmString)
                    val mensagemErro = jsonRaiz.getString("message")
                    throw IOException(mensagemErro)
                }
                else if (statusCode > 400){
                    throw IOException("Erro na comunicação com o servidor")
                }

                stream = urlConnection.inputStream
                val jsonEmString = stream.bufferedReader().use {it.readText()}
                val filmeEscolhido = transfEmFilmeEscolhido(jsonEmString)
                handler.post{
                    callback.noResultado(filmeEscolhido)
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

    private fun transfEmFilmeEscolhido(jsonEmString: String): FilmeEscolhido {
        val jsonRaiz = JSONObject(jsonEmString) //acessar o objeto json

        val id = jsonRaiz.getInt("id")
        val capaUrl = jsonRaiz.getString("cover_url")
        val titulo = jsonRaiz.getString("title")
        val desc = jsonRaiz.getString("desc")
        val elenco = jsonRaiz.getString("cast")
        val filmesEmJson = jsonRaiz.getJSONArray("movie")

        val similares = mutableListOf<Filme>()
        for (i in 0 until filmesEmJson.length()){
            val filmeSimilEmJson = filmesEmJson.getJSONObject(i)

            val similarId = filmeSimilEmJson.getInt("id")
            val similarCapaUrl = filmeSimilEmJson.getString("cover_url")

            similares.add(Filme(similarId, similarCapaUrl))
        }
        val filmeEscolhido = Filme(id, capaUrl, titulo, desc, elenco)

        return FilmeEscolhido(filmeEscolhido, similares)
    }
}