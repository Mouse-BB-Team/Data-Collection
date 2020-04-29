package pl.edu.agh.data_collection.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.edu.agh.data_collection.persistence.entity.UserEntity;
import pl.edu.agh.data_collection.persistence.repository.UserRepository;

import java.util.Optional;

@Service
public class UserDetailsServiceConfig implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) {
        Optional<UserEntity> user = userRepository.findByLogin(login);

        return new UserConfig(user.orElseThrow(() -> new UsernameNotFoundException(login + "not found!")));
    }
}
