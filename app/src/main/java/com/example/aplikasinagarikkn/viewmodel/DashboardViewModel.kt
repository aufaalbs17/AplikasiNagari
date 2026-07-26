package com.example.aplikasinagarikkn.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikasinagarikkn.model.NewsItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jsoup.Jsoup

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
                // Mensimulasikan pengambilan data dari API digitaldesa yang terkunci.
                // Data ini disesuaikan persis dengan website Nagari Sako Selatan Pasia Talang.
                kotlinx.coroutines.delay(1000) // Animasi loading 1 detik

                val newsItems = listOf(
                    NewsItem(
                        title = "PERCEPATAN PENURUNAN STUNTING, NAGARI SAKO SELATAN...",
                        subtitle = "16 Jul 2026 • Percepatan Penurunan Stunting...",
                        imageUrl = "https://cdn.digitaldesa.com/uploads/profil/13.11.02.2009/common/300_solselkab.png", 
                        link = "https://sakoselatanpasiatalang.digitaldesa.id/berita/percepatan-penurunan-stunting"
                    ),
                    NewsItem(
                        title = "DALAM RANGKA GELAR KULIAH KERJA NYATA MAHASISWA UNAND...",
                        subtitle = "09 Jul 2026 • Mahasiswa KKN UNAND Gelar Lokakarya...",
                        imageUrl = "https://cdn.digitaldesa.com/uploads/profil/13.11.02.2009/common/300_solselkab.png",
                        link = "https://sakoselatanpasiatalang.digitaldesa.id/berita/dalam-rangka-gelar-kuliah-kerja-nyata"
                    ),
                    NewsItem(
                        title = "INFORMASI LAYANAN PUBLIK DENGAN TRANSFORMASI DIGITAL",
                        subtitle = "30 Jun 2026 • Mengenal SIMPEL DISIKO...",
                        imageUrl = "https://cdn.digitaldesa.com/uploads/profil/13.11.02.2009/common/300_solselkab.png",
                        link = "https://sakoselatanpasiatalang.digitaldesa.id/berita/informasi-layanan-publik-dengan-transformasi-digital"
                    )
                )

                _newsState.value = NewsState.Success(newsItems)
            } catch (e: Exception) {
                _newsState.value = NewsState.Error(e.message ?: "Gagal memuat berita")
            }
        }
    }
}
