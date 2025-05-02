package in.lakshay.service;

import in.lakshay.entity.User;
import in.lakshay.entity.UserPrincipal;
import in.lakshay.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

// loads user details for spring security authentication

@Service
@Slf4j // logging
public class MyUserDetailsService implements UserDetailsService {
	@Autowired // db access
	private UserRepository userRepository;

	// spring security calls this to load user during login
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.debug("Loading user by username: {}", username);
		User user = userRepository.findByUserName(username)
			.orElseThrow(() -> {
				log.warn("User not found: {}", username); // failed login attempt
				return new UsernameNotFoundException("User not found");
			});

		// check if password has bcrypt prefix - helps debug auth issues
		log.debug("User found: {}, Role: {}, Password format: {}",
			user.getUserName(),
			user.getRole().getName(),
			user.getPassword().startsWith("{bcrypt}") ? "Has {bcrypt} prefix" : "Missing {bcrypt} prefix");

		return new UserPrincipal(user); // wrap in spring security obj
	}
}