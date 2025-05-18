package helper.user

import dto.auth.ReqSignInDto

enum INVALID_LOGIN {
    EMPTY_EMAIL,
    INVALID_EMAIL,
    EMPTY_PASSWORD,
    INVALID_PASSWORD,
    INVALID_BOTH_EMAIL_PASSWORD,
    EMPTY_BOTH_EMAIL_PASSWORD
}


@SuppressWarnings("unused")
class GenerateSignInUtility {
    static ReqSignInDto generateValidUserToSignIn() {
        def email = "test@gmail.com"
        def password = "password"
        return new ReqSignInDto(email: email, password: password)
    }

    static ReqSignInDto generateInvalidBaseOnCase(INVALID_LOGIN invalidCase) {
        def email = "test@gmail.com"
        def password = "password"
        def result = new ReqSignInDto(email: email, password: password)

        switch (invalidCase) {
            case invalidCase.EMPTY_EMAIL:
                result.setEmail("")
                break
            case invalidCase.INVALID_EMAIL:
                result.setEmail("the_meow#gmail.com")
                break
            case invalidCase.EMPTY_PASSWORD:
                result.setPassword("")
                break
            case invalidCase.INVALID_PASSWORD:
                result.setPassword("passwordx")
                break
            case invalidCase.INVALID_BOTH_EMAIL_PASSWORD:
                result.setEmail("the_meow@gmail.com")
                result.setPassword("passwordx")
                break
            case invalidCase.EMPTY_BOTH_EMAIL_PASSWORD:
                result.setEmail("")
                result.setPassword("")
                break

        }
        return result
    }
}
