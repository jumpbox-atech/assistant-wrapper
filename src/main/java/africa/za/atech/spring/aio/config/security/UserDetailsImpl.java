package africa.za.atech.spring.aio.config.security;

import africa.za.atech.spring.aio.functions.users.model.Users;
import africa.za.atech.spring.aio.functions.users.repo.UsersRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetailsService {

    private final UsersRepo repoUsers;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> userRecord = repoUsers.findByUsernameIgnoreCase(username);
        if (userRecord.isEmpty()) {
            throw new UsernameNotFoundException("Unable to retrieve user with username: [" + username + "]");
        }
        return User.builder()
                .username(userRecord.get().getUsername())
                .password(userRecord.get().getPassword())
                .roles(userRecord.get().getRole())
                .disabled(userRecord.get().isDisabled())
                .accountLocked(false)
                .credentialsExpired(false).build();
    }

}
