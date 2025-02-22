package in.lakshay.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import in.lakshay.entity.User;


public interface UserRepository extends JpaRepository<User, Long>{
	
	User findByUserName(String username);

}
