package com.newage.aquapets.services.filewebservices

import android.net.Uri
import com.google.firebase.storage.StorageReference

class FirebaseFileWebService(
    private val _firebaseStorageReference: StorageReference,
    private val _fileUri: Uri
) : IFileWebService {
    override fun fileUpload(onSuccess: () -> Unit, onFailure: () -> Unit, onError: () -> Unit) {
        try {
            _firebaseStorageReference.putFile(_fileUri)
                .addOnSuccessListener {
                    //tempJSON.delete()
                    // progressDialog!!.dismiss()
                    // msgAllUsers()
                    // adapter.notifyItemInserted((sellerItemsDescriptionArrayList.size()-1));
                    //adapter.notifyDataSetChanged();
                    //Log.i("InsertPosition",Integer.toString(sellerItemsDescriptionArrayList.size()-1));
                    onSuccess()
                }
                .addOnFailureListener {
                    //Toast.makeText(this@SellerActivity, "UPLOAD ERROR!!", Toast.LENGTH_SHORT).show()
                    //Log.i("JSON upload error",e.getMessage());
//                    tempJSON.delete()
//                    progressDialog!!.dismiss()
//                    deleteUploadedItems(tempImageID)
//                    sellerItemsDescriptionArrayList!!.removeAt(sellerItemsDescriptionArrayList!!.size - 1)
//                    adapter!!.notifyItemRemoved(sellerItemsDescriptionArrayList!!.size - 1)
                    onFailure()
                }
        } catch (e: Exception) {
            //  Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            //Log.i("TEMP FILE ERROR",e.getMessage());
            onError();
        }
    }

    override fun fileDownload(onSuccess: () -> Unit, onFailure: () -> Unit, onError: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun fileDelete(onSuccess: () -> Unit, onFailure: () -> Unit, onError: () -> Unit) {
        TODO("Not yet implemented")
    }
}