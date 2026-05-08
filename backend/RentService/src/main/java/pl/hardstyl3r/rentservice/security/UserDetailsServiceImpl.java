package pl.hardstyl3r.rentservice.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.hardstyl3r.rentservice.domain.Client;
import pl.hardstyl3r.rentservice.ports.driven.ClientPort;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final ClientPort clientPort;

    public UserDetailsServiceImpl(ClientPort clientPort) {
        this.clientPort = clientPort;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Client client = clientPort.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Client not found with username: " + username));

        String role = client.getRole() != null ? client.getRole().name() : "CLIENT";

        return new org.springframework.security.core.userdetails.User(
                client.getUsername(),
                "",
                client.isActive(),
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }
}
