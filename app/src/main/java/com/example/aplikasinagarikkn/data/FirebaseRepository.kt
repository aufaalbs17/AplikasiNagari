package com.example.aplikasinagarikkn.data

import android.util.Log
import com.example.aplikasinagarikkn.ui.screens.Laporan
import com.example.aplikasinagarikkn.ui.screens.SuratAktif
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Singleton Repository to manage Real-time Firebase Firestore database interactions
 * for Aplikasi Nagari Sako Selatan Pasia Talang.
 */
object FirebaseRepository {

    private const val TAG = "FirebaseRepository"
    private const val COLLECTION_LAPORAN = "laporan_warga"
    private const val COLLECTION_SURAT = "surat_warga"

    private var db: FirebaseFirestore? = null
    private var laporanListener: ListenerRegistration? = null
    private var suratListener: ListenerRegistration? = null

    private val _laporanListState = MutableStateFlow<List<Laporan>>(emptyList())
    val laporanListState: StateFlow<List<Laporan>> = _laporanListState.asStateFlow()

    private val _suratListState = MutableStateFlow<List<SuratAktif>>(emptyList())
    val suratListState: StateFlow<List<SuratAktif>> = _suratListState.asStateFlow()

    init {
        try {
            db = FirebaseFirestore.getInstance()
            Log.d(TAG, "FirebaseFirestore initialized successfully.")
            listenToLaporanRealtime()
            listenToSuratRealtime()
        } catch (e: Exception) {
            Log.w(TAG, "Firebase not yet configured with google-services.json. Falling back to local data.", e)
        }
    }

    /**
     * Listen to real-time changes in citizen reports (Laporan Warga)
     */
    fun listenToLaporanRealtime() {
        val firestore = db ?: return
        laporanListener?.remove()

        laporanListener = firestore.collection(COLLECTION_LAPORAN)
            .orderBy("id", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error listening to Laporan real-time changes", error)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        val id = doc.getLong("id")?.toInt() ?: 0
                        val judul = doc.getString("judul") ?: ""
                        val tanggal = doc.getString("tanggal") ?: ""
                        val status = doc.getString("status") ?: "Menunggu"
                        if (id > 0 && judul.isNotBlank()) Laporan(id, judul, tanggal, status) else null
                    }
                    _laporanListState.value = list
                    Log.d(TAG, "Real-time Laporan updated from Firebase: ${list.size} items.")
                }
            }
    }

    /**
     * Add a new citizen complaint report to Firebase Firestore
     */
    fun tambahLaporan(judul: String, kategori: String, deskripsi: String, onComplete: (Boolean) -> Unit) {
        val firestore = db
        if (firestore == null) {
            Log.w(TAG, "Firebase instance is null. Unable to push to cloud.")
            onComplete(false)
            return
        }

        val newId = (System.currentTimeMillis() % 100000).toInt()
        val data = hashMapOf(
            "id" to newId,
            "judul" to judul,
            "kategori" to kategori,
            "deskripsi" to deskripsi,
            "tanggal" to "28 Juli 2026",
            "status" to "Menunggu",
            "pelapor" to "Budi Santoso"
        )

        firestore.collection(COLLECTION_LAPORAN)
            .document(newId.toString())
            .set(data)
            .addOnSuccessListener {
                Log.d(TAG, "Laporan successfully pushed to Firebase Firestore with ID: $newId")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error pushing Laporan to Firebase Firestore", e)
                onComplete(false)
            }
    }

    /**
     * Update complaint status and official Ibu Wali Nagari response in Firebase
     */
    fun updateStatusLaporan(id: Int, statusBaru: String, tanggapan: String, onComplete: (Boolean) -> Unit) {
        val firestore = db
        if (firestore == null) {
            onComplete(false)
            return
        }

        val updates = mapOf(
            "status" to statusBaru,
            "tanggapan" to tanggapan
        )

        firestore.collection(COLLECTION_LAPORAN)
            .document(id.toString())
            .update(updates)
            .addOnSuccessListener {
                Log.d(TAG, "Laporan #$id status updated to $statusBaru in Firebase.")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error updating Laporan status in Firebase", e)
                onComplete(false)
            }
    }

    /**
     * Listen to real-time changes in digital letter applications (Surat Warga)
     */
    fun listenToSuratRealtime() {
        val firestore = db ?: return
        suratListener?.remove()

        suratListener = firestore.collection(COLLECTION_SURAT)
            .orderBy("id", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error listening to Surat real-time changes", error)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        val id = doc.getLong("id")?.toInt() ?: 0
                        val jenisSurat = doc.getString("jenisSurat") ?: ""
                        val tanggal = doc.getString("tanggal") ?: ""
                        val status = doc.getString("status") ?: "Diajukan"
                        val keterangan = doc.getString("keterangan") ?: ""
                        if (id > 0 && jenisSurat.isNotBlank()) SuratAktif(id, jenisSurat, tanggal, status, keterangan) else null
                    }
                    _suratListState.value = list
                    Log.d(TAG, "Real-time Surat updated from Firebase: ${list.size} items.")
                }
            }
    }

    /**
     * Add new digital letter application to Firebase Firestore
     */
    fun tambahSurat(jenisSurat: String, keperluan: String, onComplete: (Boolean) -> Unit) {
        val firestore = db
        if (firestore == null) {
            onComplete(false)
            return
        }

        val newId = (System.currentTimeMillis() % 100000).toInt()
        val data = hashMapOf(
            "id" to newId,
            "jenisSurat" to jenisSurat,
            "keperluan" to keperluan,
            "tanggal" to "28 Juli 2026",
            "status" to "Diajukan",
            "keterangan" to "Diterima sistem • Menunggu peninjauan Ibu Wali Nagari",
            "pemohon" to "Budi Santoso"
        )

        firestore.collection(COLLECTION_SURAT)
            .document(newId.toString())
            .set(data)
            .addOnSuccessListener {
                Log.d(TAG, "Surat successfully pushed to Firebase Firestore with ID: $newId")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error pushing Surat to Firebase Firestore", e)
                onComplete(false)
            }
    }
}
