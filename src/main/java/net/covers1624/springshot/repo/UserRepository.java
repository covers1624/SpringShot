package net.covers1624.springshot.repo;

import net.covers1624.springshot.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Created by covers1624 on 5/8/20.
 */
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

}
