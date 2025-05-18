package dto.auth

import groovy.transform.ToString


class ReqSignInDto {
    String email
    String password

    @Override // Good practice to use @Override when overriding a method
    String toString() {
        // Masking the password in toString is a good security practice for logs
        return "ReqSignInDto(email: '${email}', password: '*** MASKED ***')"
    }
}

class ResSignInDto {
    String token
}

@ToString
class ReqSignUpDto {
    String username
    String password
    String email
    String first_name
    String last_name
    String gender
}



class ResMeDto{
    String id
    String gender
    String user_role
    String username
    String email
    String first_name
    String last_name
}



