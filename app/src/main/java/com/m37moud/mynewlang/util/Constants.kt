package com.m37moud.mynewlang.util

class Constants {
    companion object {
        //        var encyprate = false
        const val ACTION_TRANSLATE = "translate"
        const val ACTION_ENCRYPT = "encrypt"
        const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
        const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
        const val ENCRYPT_ACTION = "encrypt_action"


        const val FLOATING_DIALOG_ACTION_START = "FLOATING_DIALOG_ACTION_START"
        const val FLOATING_DIALOG_ACTION_END = "FLOATING_DIALOG_ACTION_END"

        const val ENCRYPRAT_TXT = "encryptTxt"
        const val ORIGINAL_TXT = "originalTxt"


        const val AD_REWARDEDED_ID = "ca-app-pub-3940256099942544/5224354917"  
        const val AD_InterstitialAd_ID = "ca-app-pub-3940256099942544/1033173712"
        const val AD_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"



        fun textContainsArabic(text: String): Boolean {
            for (charac in text.toCharArray()) {
                if (Character.UnicodeBlock.of(charac) === Character.UnicodeBlock.ARABIC) {
                    return true
                }
            }
            return false

        }

    }
}
