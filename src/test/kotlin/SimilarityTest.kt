import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test


class SimilarityTest() {
    val similarity: Similarity = Similarity()


    @Test
    fun isSimilar_mixed_string() {
        val stringToCompare = "PRESSING DU CANON DOR"
        val stringToCompareWith = "DU PRESSING DU CANON D OR"
        assertTrue(similarity.isSimilar(stringToCompare, stringToCompareWith))
    }


    @Test
    fun isSimilar_string_with_more_words() {
        val stringToCompare = "ORANGE GENERALE DE TELEPHONE"
        val stringToCompareWith = "ORANGE GENERALE DE TELEPHONE TOURC"
        assertTrue(similarity.isSimilar(stringToCompare, stringToCompareWith))
    }
}


