package helper

//import java.util.Random // Keep for type declaration, though SecureRandom is a subclass
import java.security.SecureRandom

@SuppressWarnings("unused")
class RandomUtility {

    //noinspection Typo
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    // Using SecureRandom for less predictable sequences.
    private static final Random RANDOM = new SecureRandom()
    // private static final Random RANDOM = new Random() // This line is now commented out


    /**
     * Generates a random 7-digit integer.
     * The number will be between 1,000,000 and 9,999,999, inclusive.
     * @return A random 7-digit integer.
     */
    static int generateRandom7DigitNumber() {
        // NextInt(n) generates a number between 0 (inclusive) and n (exclusive).
        // We want a number between 1,000,000 and 9,999,999.
        // Range size: 9,999,999 - 1,000,000 + 1 = 9,000,000
        // So, RANDOM.nextInt(9000000) will give a number from 0 to 8,999,999.
        // Add the lower bound (1,000,000) to shift the range.
        return RANDOM.nextInt(9000000) + 1000000
    }

    /**
     * Generates a random string of 7 digits.
     * This version ensures the output is a string and can be useful
     * if you specifically need a string representation.
     * @return A random 7-digit string.
     */
    static String generateRandom7DigitString() {
        return String.valueOf(generateRandom7DigitNumber())
    }

    /**
     * Generates a random string of a specified length using alphabetic characters (a-z, A-Z).
     * @param length The desired length of the random string.
     * @return A random alphabetic string of the specified length.
     */
    static String generateRandomAlphabeticString(int length) {
        if (length <= 0) {
            return ""
        }
        StringBuilder sb = new StringBuilder(length)
        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(ALPHABET.length())
            sb.append(ALPHABET.charAt(randomIndex))
        }
        return sb.toString()
    }

    /**
     * Generates a random 4-character string using alphabetic characters (a-z, A-Z).
     * @return A random 4-character alphabetic string.
     */
    static String generateRandom4AlphabetString() {
        return generateRandomAlphabeticString(4)
    }
}

// Example of how to use it (you can run this part in a Groovy script or test):
