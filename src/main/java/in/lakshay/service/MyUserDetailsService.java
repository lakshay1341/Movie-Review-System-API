package in.lakshay.service;

import in.lakshay.entity.User;
import in.lakshay.entity.UserPrincipal;
import in.lakshay.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUserName(username)
			.orElseThrow(() -> new UsernameNotFoundException("User not found"));
		return new UserPrincipal(user);
	}
}