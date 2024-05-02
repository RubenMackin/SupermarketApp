package com.rubenmackin.supermarketapp.data.network

import android.net.Uri
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.storageMetadata
import com.rubenmackin.supermarketapp.data.response.ProductResponse
import com.rubenmackin.supermarketapp.data.response.TopProductsResponse
import com.rubenmackin.supermarketapp.domain.model.Product
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import kotlin.coroutines.resume

class FirebaseDataBaseService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {

    companion object {
        const val PRODUCTS_PATH = "products"
        const val MANAGEMENTS_PATH = "management"
        const val PRODUCTS_IMAGE_PATH = "products"
        const val TOP_PRODUCTS_DOCUMENT = "top_products"
    }

    suspend fun getAllProducts(): List<Product> {
        return firestore.collection(PRODUCTS_PATH).get().await().map { product ->
            product.toObject(ProductResponse::class.java).toDomain()
        }
    }

    suspend fun getLastProduct(): Product? {
        return firestore.collection(PRODUCTS_PATH).orderBy("id", Query.Direction.DESCENDING)
            .limit(1).get().await().firstOrNull()?.toObject(ProductResponse::class.java)?.toDomain()
    }

    suspend fun getTopProducts(): List<String> {
        return firestore.collection(MANAGEMENTS_PATH).document(TOP_PRODUCTS_DOCUMENT).get().await()
            .toObject(TopProductsResponse::class.java)?.ids ?: emptyList()
    }

    suspend fun uploadAndDownloadImage(uri: Uri): String {
        return suspendCancellableCoroutine<String> { suspendCancellable ->
            val reference = storage.reference.child("${PRODUCTS_IMAGE_PATH}/${uri.lastPathSegment}")
            reference.putFile(uri, createMetaData()).addOnSuccessListener {
                downloadImage(it, suspendCancellable)
            }.addOnFailureListener {
                suspendCancellable.resume("")
            }
        }
    }

    private fun downloadImage(
        uploadTask: UploadTask.TaskSnapshot,
        suspendCancellable: CancellableContinuation<String>
    ) {
        uploadTask.storage.downloadUrl.addOnSuccessListener {
            suspendCancellable.resume(it.toString())
        }.addOnFailureListener {
            suspendCancellable.resume("")
        }
    }

    private fun createMetaData(): StorageMetadata {
        val metadata = storageMetadata {
            contentType = "image/jpeg"
            setCustomMetadata("date", Date().time.toString())
        }
        return metadata
    }

    private fun generateProductId(): String {
        return Date().time.toString()
    }

    suspend fun uploadNewProduct(
        imageUrl: String,
        name: String,
        description: String,
        price: String
    ): Boolean {
        val id = generateProductId()

        val product = hashMapOf(
            "id" to id,
            "name" to name,
            "description" to description,
            "price" to price,
            "image" to imageUrl
        )

        return suspendCancellableCoroutine {cancellableCorrutine ->
            firestore.collection(PRODUCTS_PATH).document(id).set(product).addOnSuccessListener {
                cancellableCorrutine.resume(true)
            }.addOnFailureListener {
                cancellableCorrutine.resume(false)
            }
        }

    }
}