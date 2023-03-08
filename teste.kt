package com.corning.inspection.utils

fun main(args: Array<String>) {
    // TESTANDO

    val encoder = mapOf("a" to 0, "e" to 1, "i" to 2, "o" to 3, "u" to 4, "aa" to -1) //O encoder deve ser carregado de algum canto. Aqui estou criando manualmente
    val decoder = encoder.entries.associateBy({ it.value }, { it.key }) //O decoder é gerado a partir do encoder
    val bpeRanks = mapOf(kotlin.Pair("a", "a") to 10) //Não entendi bem esse. Mas, aparentemente, se existir uma sequência "aa", não considerará os a's individualmente. Esse também deverá ser carregado de outro canto

    val tokenizer = GPT2Tokenizer(encoder, decoder, bpeRanks)
    println(tokenizer.encode("i")) //[2]
    println(tokenizer.encode("aiioiuaa")) // [0, 2, 2, 3, 2, 4, -1]
    println(tokenizer.decode(listOf(1, 2, 0))) // eia

}


class GPT2Tokenizer(
    private val encoder: Map<String, Int>,
    private val decoder: Map<Int, String>,
    private val bpeRanks: Map<kotlin.Pair<String, String>, Int>) {
    private val encodeRegex = Regex("""'s|'t|'re|'ve|'m|'ll|'d| ?\p{L}+| ?\p{N}+| ?[^\s\p{L}\p{N}]+|\s+(?!\S)|\s+""")

    fun decode(tokens: List<Int>): String {
        val text = tokens.joinToString("") { decoder.getOrDefault(it, "") }
        val utfCodepoints = text.map { byteDecoder[it.toString()]!! }
        return String(utfCodepoints.toIntArray(), 0, utfCodepoints.size)
    }

    fun encode(text: String): MutableList<Int> {
        val tokens = encodeRegex.findAll(text).map { result ->
            result.value.codePoints()
                .boxed()
                .map { byteEncoder[it]!! }
                .toArray()
                .joinToString("")
        }

        return tokens
            .map { bpe(it) }
            .flatten()
            .map { encoder[it]!! }
            .toMutableList()
    }

    private fun bpe(token: String): List<String> {
        if (token.length <= 1) return listOf(token)

        var word = token.map { it.toString() }
        var pairs = getPairs(word)

        while (true) {
            if (!pairs.any { bpeRanks.containsKey(it) }) break
            val (first, second) = pairs.minBy { bpeRanks.getOrDefault(it, Int.MAX_VALUE) } ?: break

            var i = 0
            val newWord = mutableListOf<String>()
            while (i < word.size) {
                val j = word.withIndex().indexOfFirst { it.index >= i && it.value == first }
                if (j != -1) {
                    newWord.addAll(word.subList(i, j))
                    i = j
                } else {
                    newWord.addAll(word.subList(i, word.size))
                    break
                }

                if (word[i] == first && i < word.size-1 && word[i+1] == second) {
                    newWord.add(first+second)
                    i += 2
                } else {
                    newWord.add(word[i])
                    i += 1
                }
            }

            word = newWord
            if (word.size == 1) {
                break
            } else {
                pairs = getPairs(word)
            }
        }

        return word
    }

    private fun getPairs(word: List<String>): Set<kotlin.Pair<String, String>> {
        return mutableSetOf<kotlin.Pair<String, String>>().apply {
            for (i in 0 until word.size-1) {
                add(word[i] to word[i+1])
            }
        }
    }

}










// É necessário definir byteEncoder
val byteEncoder: Map<Int, String> by lazy {
    hashMapOf<Int, String>().apply {
        put(33, "!")
        put(34, "\"")
        put(35, "#")
        put(36, "$")
        put(37, "%")
        put(38, "&")
        put(39, "'")
        put(40, "(")
        put(41, ")")
        put(42, "*")
        put(43, "+")
        put(44, ",")
        put(45, "-")
        put(46, ".")
        put(47, "/")
        put(48, "0")
        put(49, "1")
        put(50, "2")
        put(51, "3")
        put(52, "4")
        put(53, "5")
        put(54, "6")
        put(55, "7")
        put(56, "8")
        put(57, "9")
        put(58, ":")
        put(59, ";")
        put(60, "<")
        put(61, "=")
        put(62, ">")
        put(63, "?")
        put(64, "@")
        put(65, "A")
        put(66, "B")
        put(67, "C")
        put(68, "D")
        put(69, "E")
        put(70, "F")
        put(71, "G")
        put(72, "H")
        put(73, "I")
        put(74, "J")
        put(75, "K")
        put(76, "L")
        put(77, "M")
        put(78, "N")
        put(79, "O")
        put(80, "P")
        put(81, "Q")
        put(82, "R")
        put(83, "S")
        put(84, "T")
        put(85, "U")
        put(86, "V")
        put(87, "W")
        put(88, "X")
        put(89, "Y")
        put(90, "Z")
        put(91, "[")
        put(92, "\\")
        put(93, "]")
        put(94, "^")
        put(95, "_")
        put(96, "`")
        put(97, "a")
        put(98, "b")
        put(99, "c")
        put(100, "d")
        put(101, "e")
        put(102, "f")
        put(103, "g")
        put(104, "h")
        put(105, "i")
        put(106, "j")
        put(107, "k")
        put(108, "l")
        put(109, "m")
        put(110, "n")
        put(111, "o")
        put(112, "p")
        put(113, "q")
        put(114, "r")
        put(115, "s")
        put(116, "t")
        put(117, "u")
        put(118, "v")
        put(119, "w")
        put(120, "x")
        put(121, "y")
        put(122, "z")
        put(123, "{")
        put(124, "|")
        put(125, "}")
        put(126, "~")
        put(161, "\u00a1")
        put(162, "\u00a2")
        put(163, "\u00a3")
        put(164, "\u00a4")
        put(165, "\u00a5")
        put(166, "\u00a6")
        put(167, "\u00a7")
        put(168, "\u00a8")
        put(169, "\u00a9")
        put(170, "\u00aa")
        put(171, "\u00ab")
        put(172, "\u00ac")
        put(174, "\u00ae")
        put(175, "\u00af")
        put(176, "\u00b0")
        put(177, "\u00b1")
        put(178, "\u00b2")
        put(179, "\u00b3")
        put(180, "\u00b4")
        put(181, "\u00b5")
        put(182, "\u00b6")
        put(183, "\u00b7")
        put(184, "\u00b8")
        put(185, "\u00b9")
        put(186, "\u00ba")
        put(187, "\u00bb")
        put(188, "\u00bc")
        put(189, "\u00bd")
        put(190, "\u00be")
        put(191, "\u00bf")
        put(192, "\u00c0")
        put(193, "\u00c1")
        put(194, "\u00c2")
        put(195, "\u00c3")
        put(196, "\u00c4")
        put(197, "\u00c5")
        put(198, "\u00c6")
        put(199, "\u00c7")
        put(200, "\u00c8")
        put(201, "\u00c9")
        put(202, "\u00ca")
        put(203, "\u00cb")
        put(204, "\u00cc")
        put(205, "\u00cd")
        put(206, "\u00ce")
        put(207, "\u00cf")
        put(208, "\u00d0")
        put(209, "\u00d1")
        put(210, "\u00d2")
        put(211, "\u00d3")
        put(212, "\u00d4")
        put(213, "\u00d5")
        put(214, "\u00d6")
        put(215, "\u00d7")
        put(216, "\u00d8")
        put(217, "\u00d9")
        put(218, "\u00da")
        put(219, "\u00db")
        put(220, "\u00dc")
        put(221, "\u00dd")
        put(222, "\u00de")
        put(223, "\u00df")
        put(224, "\u00e0")
        put(225, "\u00e1")
        put(226, "\u00e2")
        put(227, "\u00e3")
        put(228, "\u00e4")
        put(229, "\u00e5")
        put(230, "\u00e6")
        put(231, "\u00e7")
        put(232, "\u00e8")
        put(233, "\u00e9")
        put(234, "\u00ea")
        put(235, "\u00eb")
        put(236, "\u00ec")
        put(237, "\u00ed")
        put(238, "\u00ee")
        put(239, "\u00ef")
        put(240, "\u00f0")
        put(241, "\u00f1")
        put(242, "\u00f2")
        put(243, "\u00f3")
        put(244, "\u00f4")
        put(245, "\u00f5")
        put(246, "\u00f6")
        put(247, "\u00f7")
        put(248, "\u00f8")
        put(249, "\u00f9")
        put(250, "\u00fa")
        put(251, "\u00fb")
        put(252, "\u00fc")
        put(253, "\u00fd")
        put(254, "\u00fe")
        put(255, "\u00ff")
        put(0, "\u0100")
        put(1, "\u0101")
        put(2, "\u0102")
        put(3, "\u0103")
        put(4, "\u0104")
        put(5, "\u0105")
        put(6, "\u0106")
        put(7, "\u0107")
        put(8, "\u0108")
        put(9, "\u0109")
        put(10, "\u010a")
        put(11, "\u010b")
        put(12, "\u010c")
        put(13, "\u010d")
        put(14, "\u010e")
        put(15, "\u010f")
        put(16, "\u0110")
        put(17, "\u0111")
        put(18, "\u0112")
        put(19, "\u0113")
        put(20, "\u0114")
        put(21, "\u0115")
        put(22, "\u0116")
        put(23, "\u0117")
        put(24, "\u0118")
        put(25, "\u0119")
        put(26, "\u011a")
        put(27, "\u011b")
        put(28, "\u011c")
        put(29, "\u011d")
        put(30, "\u011e")
        put(31, "\u011f")
        put(32, "\u0120")
        put(127, "\u0121")
        put(128, "\u0122")
        put(129, "\u0123")
        put(130, "\u0124")
        put(131, "\u0125")
        put(132, "\u0126")
        put(133, "\u0127")
        put(134, "\u0128")
        put(135, "\u0129")
        put(136, "\u012a")
        put(137, "\u012b")
        put(138, "\u012c")
        put(139, "\u012d")
        put(140, "\u012e")
        put(141, "\u012f")
        put(142, "\u0130")
        put(143, "\u0131")
        put(144, "\u0132")
        put(145, "\u0133")
        put(146, "\u0134")
        put(147, "\u0135")
        put(148, "\u0136")
        put(149, "\u0137")
        put(150, "\u0138")
        put(151, "\u0139")
        put(152, "\u013a")
        put(153, "\u013b")
        put(154, "\u013c")
        put(155, "\u013d")
        put(156, "\u013e")
        put(157, "\u013f")
        put(158, "\u0140")
        put(159, "\u0141")
        put(160, "\u0142")
        put(173, "\u0143")
    }
}

internal val byteDecoder by lazy {
    byteEncoder.entries.associateBy({ it.value }) { it.key }
}
