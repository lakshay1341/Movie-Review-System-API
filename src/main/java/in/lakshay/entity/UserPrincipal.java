package in.lakshay.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

import lombok.extern.slf4j.Slf4j;

// wrapper for our User entity to make it work with Spring Security
// implements UserDetails interface which Spring Security uses for auth
@Slf4j // for logging stuff
public class UserPrincipal implements UserDetails {
	private User user; // the actual user entity

	public UserPrincipal(User user) {
		this.user = user; // just wrap the user entity
	}

	// get user's authorities/roles for Spring Security
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// we only have one role per user for now, might change later
		return Collections.singleton(new SimpleGrantedAuthority(user.getRole().getName()));
	}

	// get password for auth
	@Override
	public String getPassword() {
		String password = user.getPassword();
		// debug log to check if password has bcrypt prefix - helps with auth debugging
		log.debug("UserPrincipal.getPassword() for user {}: password format is {}",
			user.getUserName(),
			password.startsWith("{bcrypt}") ? "Has {bcrypt} prefix" : "Missing {bcrypt} prefix");
		return password; // return the encoded password
	}

	// get username for auth
	@Override
	public String getUsername() {
		return user.getUserName(); // use our userName field
	}

	// account expiration - we don't use this feature yet
	@Override
	public boolean isAccountNonExpired() {
		// todo: maybe implement account expiration later?
		return true; // all accounts are valid for now
	}

	// account locking - we don't use this feature yet
	@Override
	public boolean isAccountNonLocked() {
		// todo: implement account locking for security
		return true; // no accounts are locked for now
	}

	// credential expiration - we don't use this feature yet
	@Override
	public boolean isCredentialsNonExpired() {
		// todo: maybe implement password expiration policy?
		return true; // credentials never expire for now
	}

	// account enabling - we don't use this feature yet
	@Override
	public boolean isEnabled() {
		// todo: add enabled/disabled flag to User entity
		return true; // all accounts are enabled for now
	}

	// todo: add method to get user id directly?
}