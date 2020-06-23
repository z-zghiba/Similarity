interface NormalizedStringSimilarity : StringSimilarity

interface NormalizedStringDistance : StringDistance

interface StringSimilarity {
    /**
     * Compute and return a measure of similarity between 2 strings.
     * @param strToCompare
     * @param strToCompareWith
     * @return similarity (0 means both strings are completely different)
     */
    fun similarity(strToCompare: String, strToCompareWith: String): Double
}


interface StringDistance {
    /**
     * Compute and return a measure of distance.
     * Must be != 0.
     * @param strToCompare
     * @param strToCompareWith
     * @return
     */
    fun distance(strToCompare: String, strToCompareWith: String): Double

}
