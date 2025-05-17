package helper.user

import io.qameta.allure.Epic

@SuppressWarnings("unused")
@Epic("Help to offer gender id to persist database")

enum GENDER {
    Male,
    Female

}

class GenderUtility {
    static String get_gender_id(GENDER gender) {
        switch (gender) {
            case gender.Male:
                return "8b0a1c6e63f74b55b7d918c38c77c87f"
            case gender.Female:
                return "d9e5f01441b34d95bd6f9a6b4b35dc99"
        }
    }
}
