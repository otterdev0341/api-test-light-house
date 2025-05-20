package helper

import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Step

enum AUTH {
    SignIn,
    SignUp,
    Me
}

enum BASECRUD {
    Create,
    GetById,
    GetAll,
    Update,
    Delete
}

@Epic("URL Management")
@Feature("Help to prepare for test")
@SuppressWarnings("unused")
class UrlManagement {
    @Step("Get Base Url of the project")
    static String getBaseUrl() {
        return "http://127.0.0.1:8000"
    } // end getBaseUrl

    @Step("Get Auth Url for test base on Enum")
    static String getAuthUrl(AUTH auth) {
        switch (auth) {
            case auth.SignIn:
                return getBaseUrl() + "/v1/sign-in"
            case auth.SignUp:
                return getBaseUrl() + "/v1/sign-up"
            case auth.Me:
                return getBaseUrl() + "/v1/me"
        }
    } // end getAuthUrl

    static String getUpdateUserUrl() {
        return getBaseUrl() + "/v1/user"
    }

    static String getBaseAssetTypeUrl() {
        return getBaseUrl() + "/v1/asset-type"
    }

    static String getBaseAssetUrl() {
        return  getBaseUrl() + "/v1/asset"
    }

    static String getBaseContactType() {
        return getBaseUrl() + "/v1/contact-type"

    }

    static String getBaseExpenseType() {
        return getBaseUrl() + "/v1/expense-type"
    }

    static String getBaseContact() {
        return getBaseUrl() + "/v1/contact"
    }

    static String getBaseExpense() {
        return getBaseUrl() + "/v1/expense"

    }

    static String getIncomeRecord() {
        return getBaseUrl() + "/v1/income"
    }

    static String getPaymentRecord() {
        return getBaseUrl() + "/v1/payment"
    }

    static String getTransferRecord() {
        return getBaseUrl() + "/v1/transfer"

    }

    static String getTransactionType() {
        return getBaseUrl() + "/v1/transaction-type"
    }

    static String getCurrentSheet() {
        return getBaseUrl() + "/v1/current-sheet"
    }

    @Step("Get Asset Type Url for test base on Enum")
    static String getAssetTypeUrl(BASECRUD crud, String asset_type_id) {
        switch (crud){
            case crud.Create:
                return getBaseUrl() + "/asset-type"
            case crud.GetById:
                return getBaseUrl() + "/asset-type/${asset_type_id}"
            case crud.GetAll:
                return getBaseUrl() + "/asset-type"
            case crud.Update:
                return getBaseUrl() + "/asset-type/${asset_type_id}"
            case crud.Delete:
                return getBaseUrl() + "/asset-type/${asset_type_id}"
        }
    } // end getAssetTypeUrl


}// end class
