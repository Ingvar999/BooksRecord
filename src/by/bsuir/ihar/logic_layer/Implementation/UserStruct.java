package by.bsuir.ihar.logic_layer.Implementation;

import java.io.Serializable;

class UserStruct implements Serializable {
    String email;
    byte[] passwordhash;

    UserStruct(String email, byte[] passwordhash){
        this.email = email;
        this.passwordhash = passwordhash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!UserStruct.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final UserStruct other = (UserStruct) obj;
        if ((this.email == null) ? (other.email != null) : !this.email.equals(other.email)) {
            return false;
        }
        if (this.passwordhash.equals(other.passwordhash)) {
            return false;
        }
        return true;
    }
}
