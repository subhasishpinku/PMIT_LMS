package com.online.course.manager

class MimeTypes private constructor() {

    companion object {
        val MIME_TYPES_EXTENSION = object : HashMap<String, String>() {
            init {
                put("image/png", ".png")
                put("image/avif", ".avif")
                put("image/bmp", ".bmp")
                put("image/png", ".png")
                put("image/gif", ".gif")
                put("image/jpeg", ".jpeg")
            }
        }

        fun getFileExtensionFromMimeType(mimeType: String): String? {
            return MIME_TYPES_EXTENSION[mimeType]
        }
    }

}