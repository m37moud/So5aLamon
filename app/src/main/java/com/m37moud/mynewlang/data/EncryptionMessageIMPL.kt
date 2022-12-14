package com.m37moud.mynewlang.data

import com.m37moud.mynewlang.util.intOrString

class EncryptionMessageIMPL {
    private val char = "س"
    private val char2 = "م"
    private val wordList = mutableListOf<String>()
    private val charList = mutableListOf<String>()
    private val suggestWordList = mutableMapOf<String, List<String>>()

    //        val text = "سدد جون الساقة بالون سسبوع ألبوم ستنين أمازون سو أرانب سهر شماسي" //12 word
    private val aList = listOf<String>("البوم", "امازون", "ارانب")
    private val aaList = listOf<String>("البوم", "أمازون", "أرانب")
    private val aaaList = listOf<String>("البوم", "امازون", "ارانب")
    private val bList = listOf<String>("بالون", "بليلة", "بطاطا")
    private val tList = listOf<String>("تمر")
    private val thList = listOf<String>("ثمبوكسة")
    private val gList = listOf<String>("جون", "جمبرى")
    private val hList = listOf<String>("حمص", "حمار")
    private val kaList = listOf<String>("خرم", "خرا")
    private val dList = listOf<String>("درة")
    private val zList = listOf<String>("ذكية")
    private val rList = listOf<String>("رمان")
    private val zalList = listOf<String>("زمارة", "زرافة")
    private val sList = listOf<String>("سمك", "سمنة")
    private val shList = listOf<String>("شماسي", "شجرة")
    private val sadList = listOf<String>("صفارة", "صمبورة")
    private val dadList = listOf<String>("ضفدع")
    private val taList = listOf<String>("طيارة", "طماطم")
    private val zaList = listOf<String>("ظاظا")
    private val ainList = listOf<String>("عمود", "عربية")
    private val gainList = listOf<String>("غبى")
    private val fList = listOf<String>("فلفل")
    private val kafList = listOf<String>("قطة")
    private val kList = listOf<String>("كمون", "كفته", "كباب", "كورة")
    private val lList = listOf<String>("لمون")
    private val mList = listOf<String>("ماما", "مروان")
    private val nList = listOf<String>("نرمين", "نونا")
    private val hhList = listOf<String>("هرم")
    private val oList = listOf<String>("وزة")
    private val iList = listOf<String>("يمامة")
    private val numList = listOf<String>("طيارة", "بالونة", "كورة")
    private val numList2 = listOf<String>("طيارات", "بالونات", "كورات")


    init {
        suggestWordList["ا"] = aList
        suggestWordList["أ"] = aaList
        suggestWordList["إ"] = aaaList
        suggestWordList["ب"] = bList
        suggestWordList["ت"] = tList
        suggestWordList["ث"] = thList
        suggestWordList["ج"] = gList
        suggestWordList["ح"] = hList
        suggestWordList["خ"] = kaList
        suggestWordList["د"] = dList
        suggestWordList["ذ"] = zList
        suggestWordList["ر"] = rList
        suggestWordList["ز"] = zalList
        suggestWordList["س"] = sList
        suggestWordList["ش"] = shList
        suggestWordList["ص"] = sadList
        suggestWordList["ض"] = dadList
        suggestWordList["ط"] = taList
        suggestWordList["ظ"] = zaList
        suggestWordList["ع"] = ainList
        suggestWordList["غ"] = gainList
        suggestWordList["ف"] = fList
        suggestWordList["ق"] = kafList
        suggestWordList["ك"] = kList
        suggestWordList["ل"] = lList
        suggestWordList["م"] = mList
        suggestWordList["ن"] = nList
        suggestWordList["ه"] = hhList
        suggestWordList["و"] = oList
        suggestWordList["ي"] = iList
        suggestWordList["ى"] = iList
        suggestWordList["-"] = numList
        suggestWordList["*"] = numList2
    }


    fun encryptTxt(txt: String): String {
//        val text = "جدد الباقة أسبوع أتنين أو شهر" //6 word
        return extractWords(txt).joinToString(" ")
    }



    private fun extractWords(txt: String): List<String> {
//        val t = txt.split(" ")
        val t = txt.trim().split(" ").filter {
            it != ""

        }
        println(t.toString())

        loop@ for (i in t) {
//
//            if(!i.intOrString()){
//                continue@loop
//            }

            if ((i.filter { it in 'أ'..'ي' }.length != i.length) && !i.intOrString() ) {
                continue@loop
            }
            println(i)
            if (i[0] == 'ا' && i[1] == 'ل') {
//                wordList.add(char.plus(t[i].substring(1)))
                wordList.add(removeThirdCharChar(i))

                charList.add(i[2].toString())
            } else if (i.intOrString()) {
                wordList.add(i)
                if (i.length > 1)
                    charList.add("-")
                else charList.add("*")

            } else {

                if (i.startsWith(char)) {

                    wordList.add(char2.plus(i.substring(1)))
                    charList.add(i[0].toString())
                } else {
                    wordList.add(char.plus(i.substring(1)))
                    charList.add(i[0].toString())
                }

            }

        }

        return completeEncryptTxt(wordList, charList)
//        return charList


    }

    private fun removeThirdCharChar(txt: String): String {
        val n = txt.substring(2)
        return if (n.startsWith(char)) {
            "ال".plus(char2.plus(n.substring(1)))
        } else {
            "ال".plus(char.plus(n.substring(1)))
        }


    }

    private fun completeEncryptTxt(word: List<String>, char: List<String>): List<String> {
        val temp = mutableListOf<String>()
        repeat(word.size) { i ->
            temp.add(word[i])
            temp.add(
                chosePropriateWord(char[i])
            )

        }

        return temp
    }

    private fun chosePropriateWord(char: String): String {
        var tempWord = ""
        if (suggestWordList.containsKey(char)) {
            tempWord = suggestWordList[char]!!.random()
        }
        return tempWord
    }

    companion object {
        const val ar = "ا آ أ ب پ ت ث ج چ ح خ د ذ ر ز ژ س ش ص ض ط ظ ع غ ف ق ک گ ل م ن و ه ی ي"
    }

}