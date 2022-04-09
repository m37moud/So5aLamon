import com.m37moud.mynewlang.util.InvalidTextException

class TranslateMessageIMPL {
    private val originalList = mutableListOf<String>()
    private val secondList = mutableListOf<String>()

    fun translateTxt(txt: String): String {
//        val text = "سدد جون الساقة بالون سسبوع ألبوم ستنين أمازون سو أرانب سهر شماسي" //12 word
        return extractWords(txt).joinToString(" ")
    }

//    val txt = readLine()?.split(" ")

    private fun removeThirdCharChar(txt: String): String {
        val n = txt.substring(3)

        return "ال".plus(n)
    }

    private fun extractWords(txt: String): List<String> {
        var t = txt.trim().split(" ").filter { it != "" }
//        t.toMutableList()
        val tt = mutableListOf<String>()
       if (t.size <=1){
           throw InvalidTextException("مش هينفع تترجم دى")
       }

        t.forEach { txt ->
            if ((txt.filter { it in 'أ'..'ي' }.length == txt.length)) {
                tt.add(txt)
            }

        }
        println(tt.toString())
        var s = ""

        repeat(tt.size) { i ->

            if (i % 2 == 0) {
                s = if (tt[i][0] == 'ا' && tt[i][1] == 'ل') {
                    removeThirdCharChar(tt[i])

                } else {
                    tt[i].substring(1)
                }
//                println(s)
                originalList.add(s)

            } else {
                val firstChar = tt[i][0].toString()

                secondList.add(firstChar)
            }

        }
        return getOriginalWord(originalList, secondList)
    }

    private fun getOriginalWord(wordList: List<String>, charList: List<String>): List<String> {
        val list = mutableListOf<String>()
        repeat(charList.size) { i ->
            println(wordList[i])
            val word = if (wordList[i][0] == 'ا' && wordList[i][1] == 'ل') {
                println("if true ")
                addThirdChar(wordList[i], charList[i])

            } else {
                println("if false ")
                charList[i].plus(wordList[i])
            }

            list.add(word)

        }

        return list
    }

    private fun addThirdChar(word: String, char: String): String {
        val w = char.plus(word.substring(2))

        println(w)
        return "ال".plus(w)
    }


}