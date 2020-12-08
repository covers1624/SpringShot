package net.covers1624.springshot.repo;

import net.covers1624.springshot.entity.ApiKey;
import net.covers1624.springshot.entity.User;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by covers1624 on 5/11/20.
 */
public interface ApiKeyRepository extends CrudRepository<ApiKey, Long> {

    Optional<ApiKey> findBySecret(String secret);

    List<ApiKey> findAllByUser(User user);

}
