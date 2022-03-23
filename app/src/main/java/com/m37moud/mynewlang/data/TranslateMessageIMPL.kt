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
        val t = txt.split(" ")

        var s = ""
        repeat(t.size) { i ->

            if (i % 2 == 0) {

                s = if (t[i][0] == 'ا' && t[i][1] == 'ل') {
                    removeThirdCharChar(t[i])

                } else {
                    t[i].substring(1)
                }
//                println(s)
                originalList.add(s)

            } else {
                val firstChar = t[i][0].toString()

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
