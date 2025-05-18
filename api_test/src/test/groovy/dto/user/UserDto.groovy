package dto.user

import groovy.transform.ToString

class UserDto {
}

@ToString
class ReqUpdateUserDto {
    String username
    String password
    String email
    String first_name
    String last_name
    String gender
}
