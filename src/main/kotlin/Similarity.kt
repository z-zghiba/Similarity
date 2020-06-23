import Conf.DEFAULT_THRESHOLD
import Conf.JW_COEF
import Conf.THREE
import java.util.Arrays


import jdk.nashorn.internal.ir.annotations.Immutable

/**
 * Based on Jaro – Winkler  distance metric
 * https://fr.wikipedia.org/wiki/Distance_de_Jaro-Winkler
 */

@Immutable
class Similarity : NormalizedStringSimilarity, NormalizedStringDistance {

    private val threshold: Double
    private val distance = 0.87


    constructor() {
        this.threshold = DEFAULT_THRESHOLD
    }

    private constructor(_threshold: Double) {
        this.threshold = _threshold
    }


    override fun similarity(strToCompare: String, strToCompareWith: String): Double {
        if (strToCompare == strToCompareWith) {
            return 1.0        //full similarity
        }
        val mtp = matches(strToCompare, strToCompareWith)
        val m = mtp[0].toFloat()
        if (m == 0f) {
            return 0.0 // no similarity
        }

        /*
        * m is the number of corresponding characters
        * mtp[1] is the number of transpositions
        * 1/3(m/|S1| +m/|strToCompareWith|+(m-t)/m)
        * */
        val j = ((m / strToCompare.length + m / strToCompareWith.length + (m - mtp[1]) / m) / THREE).toDouble()
        var jw = j

        //calculate distance
        if (j > threshold) {
            jw = j + Math.min(JW_COEF, 1.0 / mtp[THREE]) * mtp[2].toDouble() * (1 - j)
        }
        return jw
    }


    override fun distance(strToCompare: String, strToCompareWith: String): Double {
        return 1.0 - similarity(strToCompare, strToCompareWith)
    }

    private fun matches(strToCompare: String, strToCompareWith: String): IntArray {
        val max: String
        val min: String
        if (strToCompare.length > strToCompareWith.length) {
            max = strToCompare
            min = strToCompareWith
        } else {
            max = strToCompareWith
            min = strToCompare
        }

        val range = Math.max(max.length / 2 - 1, 0)
        val match_indexes = IntArray(min.length)
        Arrays.fill(match_indexes, -1)
        val match_flags = BooleanArray(max.length)
        var matches = 0
        for (mi in 0 until min.length) {
            val c1 = min[mi]
            var xi = Math.max(mi - range, 0)
            val xn = Math.min(mi + range + 1, max.length)
            while (xi < xn) {
                if (!match_flags[xi] && c1 == max[xi]) {
                    match_indexes[mi] = xi
                    match_flags[xi] = true
                    matches++
                    break
                }
                xi++
            }
        }
        val ms1 = CharArray(matches)
        val ms2 = CharArray(matches)
        run {
            var i = 0
            var si = 0
            while (i < min.length) {
                if (match_indexes[i] != -1) {
                    ms1[si] = min[i]
                    si++
                }
                i++
            }
        }
        var i = 0
        var si = 0
        while (i < max.length) {
            if (match_flags[i]) {
                ms2[si] = max[i]
                si++
            }
            i++
        }
        var transpositions = 0
        for (mi in ms1.indices) {
            if (ms1[mi] != ms2[mi]) {
                transpositions++
            }
        }
        var prefix = 0
        for (mi in 0 until min.length) {
            if (strToCompare[mi] == strToCompareWith[mi]) {
                prefix++
            } else {
                break
            }
        }


        return intArrayOf(matches, transpositions / 2, prefix, max.length)
    }

    private fun match(first: String, second: String): Boolean {
        var size = 0
        //split the second string into words
        val wordsOfSecond = Arrays.asList(*second.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
        //split and compare each word of the first string
        for (word in first.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            if (word.length > 3 && wordsOfSecond.contains(word))
                size++
        }
        return size >= 2
    }

    fun isSimilar(_first: String, _second: String): Boolean {
        var first = _first
        var second = _second
        first = cleanString(first)
        second = cleanString(second)
        return similarity(first, second) >= distance || match(first, second)
    }


    private fun cleanString(_first: String): String {
        var first = _first
        first = first.toUpperCase().replace("[^a-zA-Z]+".toRegex(), " ")
        //TODO ADD JJC  DICTIONARY to eliminate ("MME , MR , centre com")
        return first
    }



}
