package com.example.aplikasinagarikkn.data

import android.util.Log
import com.example.aplikasinagarikkn.model.DefaultAccounts
import com.example.aplikasinagarikkn.model.LaporanModel
import com.example.aplikasinagarikkn.model.SuratModel
import com.example.aplikasinagarikkn.model.UserAccount
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Central Real-time Repository to manage User Accounts, Admin Database,
 * Citizen Complaints (Pengaduan Warga), and Digital Letter Requests (Pengajuan Surat Digital)
 * for Nagari Sako Selatan Pasia Talang.
 */
object FirebaseRepository {

    private const val TAG = "FirebaseRepository"
    private const val COLLECTION_USERS = "users"
    private const val COLLECTION_LAPORAN = "laporan_warga"
    private const val COLLECTION_SURAT = "surat_warga"

    private var db: FirebaseFirestore? = null
    private var laporanListener: ListenerRegistration? = null
    private var suratListener: ListenerRegistration? = null

    // State for currently logged in user/admin
    private val _currentUserState = MutableStateFlow<UserAccount>(DefaultAccounts.defaultWarga)
    val currentUserState: StateFlow<UserAccount> = _currentUserState.asStateFlow()

    // State for all citizen complaints (Pengaduan)
    private val _laporanListState = MutableStateFlow<List<LaporanModel>>(getInitialLaporanMock())
    val laporanListState: StateFlow<List<LaporanModel>> = _laporanListState.asStateFlow()

    // State for all letter requests (Surat Digital)
    private val _suratListState = MutableStateFlow<List<SuratModel>>(getInitialSuratMock())
    val suratListState: StateFlow<List<SuratModel>> = _suratListState.asStateFlow()

    init {
        try {
            db = FirebaseFirestore.getInstance()
            Log.d(TAG, "FirebaseFirestore initialized successfully.")
            listenToLaporanRealtime()
            listenToSuratRealtime()
        } catch (e: Exception) {
            Log.w(TAG, "Firebase not yet configured with google-services.json. Utilizing local real-time repository fallback.", e)
        }
    }

    // --- ACCOUNT MANAGEMENT ---

    fun loginAccount(roleIndex: Int, email: String) {
        val account = if (roleIndex == 1 || email.contains("admin", ignoreCase = true)) {
            DefaultAccounts.defaultAdmin
        } else {
            DefaultAccounts.defaultWarga
        }
        _currentUserState.value = account
        Log.d(TAG, "Logged in as: ${account.nama} (${account.role})")
    }

    fun updateProfile(nama: String, noHp: String, alamat: String, onComplete: (Boolean) -> Unit = {}) {
        val current = _currentUserState.value
        val updated = current.copy(
            nama = nama.ifBlank { current.nama },
            noHp = noHp.ifBlank { current.noHp },
            alamatJorong = alamat.ifBlank { current.alamatJorong }
        )
        _currentUserState.value = updated

        val firestore = db
        if (firestore != null && current.id.isNotBlank()) {
            firestore.collection(COLLECTION_USERS)
                .document(current.id)
                .set(updated)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        } else {
            onComplete(true)
        }
    }

    // --- LAPORAN PENGADUAN MANAGEMENT ---

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
                        val userId = doc.getString("userId") ?: ""
                        val pelaporNama = doc.getString("pelaporNama") ?: doc.getString("pelapor") ?: ""
                        val pelaporNik = doc.getString("pelaporNik") ?: ""
                        val judul = doc.getString("judul") ?: ""
                        val kategori = doc.getString("kategori") ?: "Fasilitas Umum"
                        val deskripsi = doc.getString("deskripsi") ?: ""
                        val tanggal = doc.getString("tanggal") ?: ""
                        val status = doc.getString("status") ?: "Menunggu"
                        val tanggapanAdmin = doc.getString("tanggapanAdmin") ?: doc.getString("tanggapan") ?: ""
                        val fotoUri = doc.getString("fotoUri")

                        if (id > 0 && judul.isNotBlank()) {
                            LaporanModel(
                                id = id,
                                userId = userId,
                                pelaporNama = pelaporNama,
                                pelaporNik = pelaporNik,
                                judul = judul,
                                kategori = kategori,
                                deskripsi = deskripsi,
                                tanggal = tanggal,
                                status = status,
                                tanggapanAdmin = tanggapanAdmin,
                                fotoUri = fotoUri
                            )
                        } else null
                    }
                    if (list.isNotEmpty()) {
                        _laporanListState.value = list
                        Log.d(TAG, "Real-time Laporan updated from Firebase: ${list.size} items.")
                    }
                }
            }
    }

    fun tambahLaporan(
        judul: String,
        kategori: String,
        deskripsi: String,
        fotoUri: String? = null,
        onComplete: (Boolean) -> Unit = {}
    ) {
        val user = _currentUserState.value
        val newId = (System.currentTimeMillis() % 100000).toInt()
        val formattedDate = SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("id-ID")).format(Date())

        val newLaporan = LaporanModel(
            id = newId,
            userId = user.id,
            pelaporNama = user.nama,
            pelaporNik = user.nik,
            judul = judul,
            kategori = kategori,
            deskripsi = deskripsi,
            tanggal = formattedDate,
            status = "Menunggu",
            tanggapanAdmin = "",
            fotoUri = fotoUri
        )

        // Update local state immediately
        val currentList = _laporanListState.value.toMutableList()
        currentList.add(0, newLaporan)
        _laporanListState.value = currentList

        val firestore = db
        if (firestore != null) {
            val data = hashMapOf(
                "id" to newId,
                "userId" to user.id,
                "pelaporNama" to user.nama,
                "pelaporNik" to user.nik,
                "judul" to judul,
                "kategori" to kategori,
                "deskripsi" to deskripsi,
                "tanggal" to formattedDate,
                "status" to "Menunggu",
                "tanggapanAdmin" to "",
                "fotoUri" to fotoUri
            )

            firestore.collection(COLLECTION_LAPORAN)
                .document(newId.toString())
                .set(data)
                .addOnSuccessListener {
                    Log.d(TAG, "Laporan pushed to Firebase Firestore ID: $newId")
                    onComplete(true)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error pushing Laporan to Firebase", e)
                    onComplete(true)
                }
        } else {
            onComplete(true)
        }
    }

    fun updateStatusLaporan(
        id: Int,
        statusBaru: String,
        tanggapanAdmin: String,
        onComplete: (Boolean) -> Unit = {}
    ) {
        // Update local state immediately
        _laporanListState.value = _laporanListState.value.map { item ->
            if (item.id == id) {
                item.copy(status = statusBaru, tanggapanAdmin = tanggapanAdmin)
            } else item
        }

        val firestore = db
        if (firestore != null) {
            val updates = mapOf(
                "status" to statusBaru,
                "tanggapanAdmin" to tanggapanAdmin
            )
            firestore.collection(COLLECTION_LAPORAN)
                .document(id.toString())
                .update(updates)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(true) }
        } else {
            onComplete(true)
        }
    }

    // --- SURAT DIGITAL MANAGEMENT ---

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
                        val userId = doc.getString("userId") ?: ""
                        val pemohonNama = doc.getString("pemohonNama") ?: doc.getString("pemohon") ?: ""
                        val pemohonNik = doc.getString("pemohonNik") ?: ""
                        val jenisSurat = doc.getString("jenisSurat") ?: ""
                        val keperluan = doc.getString("keperluan") ?: ""
                        val tanggal = doc.getString("tanggal") ?: ""
                        val status = doc.getString("status") ?: "Diajukan"
                        val keterangan = doc.getString("keterangan") ?: ""
                        val lampiranUri = doc.getString("lampiranUri")

                        if (id > 0 && jenisSurat.isNotBlank()) {
                            SuratModel(
                                id = id,
                                userId = userId,
                                pemohonNama = pemohonNama,
                                pemohonNik = pemohonNik,
                                jenisSurat = jenisSurat,
                                keperluan = keperluan,
                                tanggal = tanggal,
                                status = status,
                                keterangan = keterangan,
                                lampiranUri = lampiranUri
                            )
                        } else null
                    }
                    if (list.isNotEmpty()) {
                        _suratListState.value = list
                        Log.d(TAG, "Real-time Surat updated from Firebase: ${list.size} items.")
                    }
                }
            }
    }

    fun tambahSurat(
        jenisSurat: String,
        keperluan: String,
        lampiranUri: String? = null,
        onComplete: (Boolean) -> Unit = {}
    ) {
        val user = _currentUserState.value
        val newId = (System.currentTimeMillis() % 100000).toInt()
        val formattedDate = SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("id-ID")).format(Date())
        val initialKeterangan = "Diterima sistem • Menunggu peninjauan Ibu Wali Nagari Sako Selatan"

        val newSurat = SuratModel(
            id = newId,
            userId = user.id,
            pemohonNama = user.nama,
            pemohonNik = user.nik,
            jenisSurat = jenisSurat,
            keperluan = keperluan,
            tanggal = formattedDate,
            status = "Diajukan",
            keterangan = initialKeterangan,
            lampiranUri = lampiranUri
        )

        // Update local state immediately
        val currentList = _suratListState.value.toMutableList()
        currentList.add(0, newSurat)
        _suratListState.value = currentList

        val firestore = db
        if (firestore != null) {
            val data = hashMapOf(
                "id" to newId,
                "userId" to user.id,
                "pemohonNama" to user.nama,
                "pemohonNik" to user.nik,
                "jenisSurat" to jenisSurat,
                "keperluan" to keperluan,
                "tanggal" to formattedDate,
                "status" to "Diajukan",
                "keterangan" to initialKeterangan,
                "lampiranUri" to lampiranUri
            )

            firestore.collection(COLLECTION_SURAT)
                .document(newId.toString())
                .set(data)
                .addOnSuccessListener {
                    Log.d(TAG, "Surat pushed to Firebase Firestore ID: $newId")
                    onComplete(true)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error pushing Surat to Firebase", e)
                    onComplete(true)
                }
        } else {
            onComplete(true)
        }
    }

    fun updateStatusSurat(
        id: Int,
        statusBaru: String,
        keterangan: String,
        onComplete: (Boolean) -> Unit = {}
    ) {
        // Update local state immediately
        _suratListState.value = _suratListState.value.map { item ->
            if (item.id == id) {
                item.copy(status = statusBaru, keterangan = keterangan)
            } else item
        }

        val firestore = db
        if (firestore != null) {
            val updates = mapOf(
                "status" to statusBaru,
                "keterangan" to keterangan
            )
            firestore.collection(COLLECTION_SURAT)
                .document(id.toString())
                .update(updates)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(true) }
        } else {
            onComplete(true)
        }
    }

    // --- MOCK DATA INITIALIZATION ---

    private fun getInitialLaporanMock(): List<LaporanModel> {
        return listOf(
            LaporanModel(
                id = 101,
                userId = "warga_101",
                pelaporNama = "Budi Santoso",
                pelaporNik = "1303012807980001",
                judul = "Lampu jalan mati di Jorong Pasia",
                kategori = "Fasilitas Umum",
                deskripsi = "Lampu jalan utama dekat simpang Jorong Pasia tidak menyala sejak 2 hari yang lalu.",
                tanggal = "28 Juli 2026",
                status = "Menunggu",
                tanggapanAdmin = "Laporan diterima. Akan dicek oleh dinas terkait."
            ),
            LaporanModel(
                id = 102,
                userId = "warga_102",
                pelaporNama = "Rahmat Hidayat",
                pelaporNik = "1303011504950002",
                judul = "Saluran air tersumbat sampah",
                kategori = "Kebersihan",
                deskripsi = "Drainase di RT 03 tersumbat tumpukan sampah sehingga meluap saat hujan deras.",
                tanggal = "27 Juli 2026",
                status = "Diproses",
                tanggapanAdmin = "Petugas kebersihan Nagari sedang di lokasi untuk pembersihan."
            ),
            LaporanModel(
                id = 103,
                userId = "warga_101",
                pelaporNama = "Budi Santoso",
                pelaporNik = "1303012807980001",
                judul = "Jalan berlubang di RT 02 Jorong Pasia",
                kategori = "Fasilitas Umum",
                deskripsi = "Lubang menganga cukup berbahaya untuk pengendara roda dua.",
                tanggal = "25 Juli 2026",
                status = "Selesai",
                tanggapanAdmin = "Perbaikan jalan berlubang telah selesai ditambal oleh tim kerja Nagari."
            )
        )
    }

    private fun getInitialSuratMock(): List<SuratModel> {
        return listOf(
            SuratModel(
                id = 201,
                userId = "warga_101",
                pemohonNama = "Budi Santoso",
                pemohonNik = "1303012807980001",
                jenisSurat = "Surat Keterangan Usaha (SKU)",
                keperluan = "Untuk kelengkapan permohonan kredit KUR BRI",
                tanggal = "27 Juli 2026",
                status = "Ditinjau Wali",
                keterangan = "Sedang verifikasi kelengkapan berkas oleh Ibu Wali Nagari"
            ),
            SuratModel(
                id = 202,
                userId = "warga_102",
                pemohonNama = "Rahmat Hidayat",
                pemohonNik = "1303011504950002",
                jenisSurat = "Surat Keterangan Domisili",
                keperluan = "Persyaratan pendaftaran sekolah anak",
                tanggal = "26 Juli 2026",
                status = "Selesai",
                keterangan = "Surat telah ditandatangani Ibu Wali Nagari & siap diambil di Kantor Nagari"
            )
        )
    }
}
