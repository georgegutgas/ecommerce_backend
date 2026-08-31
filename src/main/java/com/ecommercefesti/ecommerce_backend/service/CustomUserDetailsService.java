package com.ecommercefesti.ecommerce_backend.service;

import com.ecommercefesti.ecommerce_backend.entity.User;
import com.ecommercefesti.ecommerce_backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Usa tu entidad JPA
        com.ecommercefesti.ecommerce_backend.entity.User dbUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el email: " + email));

        // Construye el UserDetails usando la ruta absoluta de Spring Security
        return org.springframework.security.core.userdetails.User
                .withUsername(dbUser.getEmail())
                .password(dbUser.getPassword())
                .authorities(dbUser.getRole())
                .build();
    }
}