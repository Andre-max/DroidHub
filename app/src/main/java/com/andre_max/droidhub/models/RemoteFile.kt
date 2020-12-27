package com.andre_max.droidhub.models

data class RemoteFile(
    var displayName: String,
    var storagePath: String,
    var firestoreId: String,
    var sizeInBytes: Long,
    var timeStamp: Long,
    var contentType: String,
    var extension: String
) {
    constructor() : this("", "", "", -1, -1, "", "")
}