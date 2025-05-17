package helper.user

import dto.auth.ReqSignUpDto
import helper.RandomUtility

enum InvalidCase {
    INVALID_USERNAME,
    INVALID_EMAIL,
    INVALID_FIRST_NAME,
    INVALID_LAST_NAME,
    INVALID_PASSWORD,
    INVALID_GENDER,
    ALL_EMPTY_FIELD,
    EXIST_USERNAME,
    EXIST_EMAIL
}

@SuppressWarnings("unused")
class GenerateUserUtility {
    static ReqSignUpDto generateAlwaysNewValidUser() {
        def username = "test_user" + RandomUtility.generateRandom4AlphabetString() + RandomUtility.generateRandomAlphabeticString(3)
        def email = "test" + RandomUtility.generateRandom4AlphabetString() + RandomUtility.generateRandomAlphabeticString(3) + "@gmail.com"
        def password = "test" + RandomUtility.generateRandom7DigitNumber() + RandomUtility.generateRandom4AlphabetString()
        def firstName = "test_first_name" + RandomUtility.generateRandomAlphabeticString(5)
        def lastname = "test_last_name" + RandomUtility.generateRandomAlphabeticString(5)
        def gender = "male"


        def result = new ReqSignUpDto()
        result.username = username
        result.email = email
        result.first_name = firstName
        result.last_name = lastname
        result.gender = gender
        result.password = password

        return result
    }

    static ReqSignUpDto generateInvalidUserBaseOnCase(InvalidCase invalidCase) {
        def username = "test_user" + RandomUtility.generateRandom4AlphabetString() + RandomUtility.generateRandomAlphabeticString(3)
        def email = "test" + RandomUtility.generateRandom4AlphabetString() + RandomUtility.generateRandomAlphabeticString(3) + "@gmail.com"
        def password = "test" + RandomUtility.generateRandom7DigitNumber() + RandomUtility.generateRandom4AlphabetString()
        def firstName = "test_first_name" + RandomUtility.generateRandomAlphabeticString(5)
        def lastname = "test_last_name" + RandomUtility.generateRandomAlphabeticString(5)
        def gender = "male"

        def result = new ReqSignUpDto()
        result.username = username
        result.email = email
        result.first_name = firstName
        result.last_name = lastname
        result.gender = gender
        result.password = password

        switch (invalidCase) {
            case invalidCase.INVALID_USERNAME:
                result.username = ""
                break
            case invalidCase.INVALID_EMAIL:
                result.email = ""
                break
            case invalidCase.INVALID_FIRST_NAME:
                result.first_name = ""
                break
            case invalidCase.INVALID_LAST_NAME:
                result.last_name = ""
                break
            case invalidCase.INVALID_PASSWORD:
                result.password = ""
                break
            case invalidCase.INVALID_GENDER:
                result.gender = ""
                break
            case invalidCase.ALL_EMPTY_FIELD:
                result.username = ""
                result.email = ""
                result.first_name = ""
                result.last_name = ""
                result.gender = ""
                break
            case invalidCase.EXIST_USERNAME:
                result.username = "test_user"
                break
            case invalidCase.EXIST_EMAIL:
                result.email = "test@gmail.com"
        }
        return result
    }


}
