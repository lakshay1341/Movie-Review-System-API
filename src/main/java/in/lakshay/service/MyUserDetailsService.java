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

@Service
@Slf4j
public class MyUserDetailsService implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.debug("Loading user by username: {}", username);
		User user = userRepository.findByUserName(username)
			.orElseThrow(() -> {
				log.warn("User not found: {}", username);
				return new UsernameNotFoundException("User not found");
			});

		log.debug("User found: {}, Role: {}, Password format: {}",
			user.getUserName(),
			user.getRole().getName(),
			user.getPassword().startsWith("{bcrypt}") ? "Has {bcrypt} prefix" : "Missing {bcrypt} prefix");

		return new UserPrincipal(user);
	}
}