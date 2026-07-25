package spring.authentication.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.authentication.entities.User;

import java.util.Optional;

public interface userRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
}
