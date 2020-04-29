package pl.edu.agh.data_collection.config;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import pl.edu.agh.data_collection.persistence.entity.UserEntity;

import java.util.Collections;

class UserConfig extends User {
    UserConfig(UserEntity user) {
        super(user.getLogin(), user.getPassword(), Collections.singletonList(new SimpleGrantedAuthority(user.getAuthority())));
    }
}
