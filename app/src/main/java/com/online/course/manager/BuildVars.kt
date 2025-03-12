package com.online.course.manager

import com.online.course.R


object BuildVars {
    @JvmField
    var LOGS_ENABLED = true
    const val API_KEY = "1234"
    const val BASE_URL = "https://app.rocket-soft.org/api/development/"

    class DefaultLang(val value: String) {
        companion object {
            const val CODE = "en"
            const val NAME = "English"
        }
    }

    val LNG_FLAG = object : HashMap<String, Int>() {
        init {
            put("ENGLISH", R.drawable.flag_united_kingdom)
            put("PERSIAN", R.drawable.flag_iran)
            put("ARABIC", R.drawable.flag_iraq)
            put("FRENCH", R.drawable.flag_france)
            put("GERMAN", R.drawable.flag_germany)
            put("SPANISH", R.drawable.flag_spain)
            put("RUSSIAN", R.drawable.flag_russian_federation)
            put("TURKISH", R.drawable.flag_turkey)
            put("CHINESE", R.drawable.flag_china)
            put("HINDI", R.drawable.flag_india)
            put("BENGALI", R.drawable.flag_bangladesh)
            put("PORTUGUESE", R.drawable.flag_portugal)
            put("INDONESIAN", R.drawable.flag_indonesia)
            put("JAPANESE", R.drawable.flag_japan)
            put("KOREAN", R.drawable.flag_south_korea)
            put("URDU", R.drawable.flag_pakistan)
            put("ITALIAN", R.drawable.flag_italy)
            put("POLISH", R.drawable.flag_poland)
            put("YORUBA", R.drawable.flag_nigeria)
            put("DUTCH", R.drawable.flag_netherlands)
            put("MALAYSIAN", R.drawable.flag_malaysia)
            put("GREEK", R.drawable.flag_greece)
            put("CZECH", R.drawable.flag_czech_republic)
            put("HEBREW", R.drawable.flag_israel)
        }
    }

}