package helper.user

import dto.user.ReqUpdateUserDto
import helper.RandomUtility

@SuppressWarnings("unused")
class GenerateUpdateUserUtility {

    static ReqUpdateUserDto GenerateValidUpdateUser() {
        def result = new ReqUpdateUserDto()
        result.setFirst_name("test${RandomUtility.generateRandomAlphabeticString(3)}")
        result.setLast_name("test${RandomUtility.generateRandomAlphabeticString(3)}")
        return result
    }

}
