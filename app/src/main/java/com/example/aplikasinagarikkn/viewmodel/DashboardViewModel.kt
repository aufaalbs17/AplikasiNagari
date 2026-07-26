package com.example.aplikasinagarikkn.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikasinagarikkn.model.NewsItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

sealed class NewsState {
    object Loading : NewsState()
    data class Success(val news: List<NewsItem>) : NewsState()
    data class Error(val message: String) : NewsState()
}

class DashboardViewModel : ViewModel() {

    private val _newsState = MutableStateFlow<NewsState>(NewsState.Loading)
    val newsState: StateFlow<NewsState> = _newsState.asStateFlow()

    init {
        fetchNews()
    }

    fun fetchNews() {
        viewModelScope.launch(Dispatchers.IO) {
            _newsState.value = NewsState.Loading
            try {
                val fetchedItems = fetchNewsFromDigitalDesa()
                if (fetchedItems.isNotEmpty()) {
                    _newsState.value = NewsState.Success(fetchedItems)
                } else {
                    _newsState.value = NewsState.Success(getFallbackNews())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _newsState.value = NewsState.Success(getFallbackNews())
            }
        }
    }

    private fun fetchNewsFromDigitalDesa(): List<NewsItem> {
        val endpoint = "https://api-profil-v2.digitaldesa.id/grapher"
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkRHRFNXVCJ9.eyJyb2wiOiJzdXBlcl9hZG1pbiIsInN1YiI6ImRpZ2lkZXMuaWQiLCJmbGciOiJWRVJJRklFRCIsImV4cCI6NDcwNzY5NTM5OCwidmVyIjoicHJvZmlsQGRpZ2l0YWxkZXNhLmlkIn0.cBtPCrQY-r3tdfnjfD2WrOFqj9mwuuJVrG-7QPAU7mE"
        
        val url = URL(endpoint)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.connectTimeout = 8000
        conn.readTimeout = 8000
        conn.setRequestProperty("Content-Type", "application/json")
        conn.setRequestProperty("Authorization", "Bearer $token")
        conn.setRequestProperty("x-digides-schema", "13_11_02_2009")
        conn.setRequestProperty("User-Agent", "Mozilla/5.0")
        conn.doOutput = true

        val graphqlQuery = """
            {"query":"query { articles { articles { ID title slug short createdAt thumbnail { URL } } } }"}
        """.trimIndent()

        conn.outputStream.use { os ->
            os.write(graphqlQuery.toByteArray(Charsets.UTF_8))
        }

        val responseCode = conn.responseCode
        if (responseCode != 200) {
            return emptyList()
        }

        val responseText = conn.inputStream.bufferedReader().use { it.readText() }
        val root = JSONObject(responseText)
        val articlesArray = root.optJSONObject("data")
            ?.optJSONObject("articles")
            ?.optJSONArray("articles") ?: return emptyList()

        val newsList = mutableListOf<NewsItem>()
        val monthNames = arrayOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agt", "Sep", "Okt", "Nov", "Des")

        for (i in 0 until articlesArray.length()) {
            val item = articlesArray.getJSONObject(i)
            val title = item.optString("title")
            val slug = item.optString("slug")
            val rawShort = item.optString("short").replace("*", "").trim()
            val createdAt = item.optString("createdAt")

            var dateStr = ""
            if (createdAt.length >= 10) {
                try {
                    val parts = createdAt.substring(0, 10).split("-")
                    if (parts.size == 3) {
                        val year = parts[0]
                        val monthIdx = parts[1].toIntOrNull()?.minus(1) ?: 0
                        val day = parts[2]
                        val monthName = monthNames.getOrElse(monthIdx) { "" }
                        dateStr = "$day $monthName $year"
                    }
                } catch (e: Exception) {
                    dateStr = ""
                }
            }

            val subtitle = if (dateStr.isNotEmpty()) {
                "$dateStr • ${rawShort.take(50)}"
            } else {
                rawShort.take(60)
            }

            val thumbObj = item.optJSONObject("thumbnail")
            val thumbUrl = thumbObj?.optString("URL") ?: ""
            val fullImageUrl = if (thumbUrl.isNotEmpty()) {
                "https://cdn.digitaldesa.com/uploads/profil/13.11.02.2009/berita/$thumbUrl"
            } else {
                "https://cdn.digitaldesa.com/uploads/profil/13.11.02.2009/common/300_solselkab.png"
            }

            val articleLink = "https://sakoselatanpasiatalang.digitaldesa.id/berita/$slug"

            newsList.add(
                NewsItem(
                    title = title,
                    subtitle = subtitle,
                    imageUrl = fullImageUrl,
                    link = articleLink
                )
            )
        }

        return newsList
    }

    private fun getFallbackNews(): List<NewsItem> {
        return listOf(
            NewsItem(
                title = "PERCEPATAN PENURUNAN STUNTING, NAGARI SAKO SELATAN GELAR REMBUK STUNTING",
                subtitle = "16 Jul 2026 • Dalam upaya mendukung percepatan penurunan angka stunting...",
                imageUrl = "https://cdn.digitaldesa.com/uploads/profil/13.11.02.2009/berita/dadbd4b4ce34b424d4c267a31616201a.jpeg",
                link = "https://sakoselatanpasiatalang.digitaldesa.id/berita/percepatan-penurunan-stuntingnagari-sako-selatan-gelar-rembuk-stunting"
            ),
            NewsItem(
                title = "DALAM RANGKA GELAR KULIAH KERJA NYATA MAHASISWA UNAND LAKUKAN LOKAKARYA PROGRAM KERJA DI NAGARI SAKO SELATAN",
                subtitle = "09 Jul 2026 • Mahasiswa KKN UNAND Gelar Lokakarya Program Kerja...",
                imageUrl = "https://cdn.digitaldesa.com/uploads/profil/13.11.02.2009/berita/c445a0dee51404408ea1ad5b01224820.jpeg",
                link = "https://sakoselatanpasiatalang.digitaldesa.id/berita/dalam-rangka-gelar-kuliah-kerja-nyata-mahasiswa-unand-lakukan-lokakarya-program-kerja-di-nagari-sako-selatan"
            ),
            NewsItem(
                title = "INFORMASI LAYANAN PUBLIK DENGAN TRANSFORMASI DIGITAL",
                subtitle = "30 Jun 2026 • Mengenal SIMPEL DISIKO Portal PPID...",
                imageUrl = "https://cdn.digitaldesa.com/uploads/profil/13.11.02.2009/berita/c75baa62583367f6a046251e52f909c8.jpeg",
                link = "https://sakoselatanpasiatalang.digitaldesa.id/berita/informasi-layanan-publik-dengan-transformasi-digital"
            )
        )
    }
}
