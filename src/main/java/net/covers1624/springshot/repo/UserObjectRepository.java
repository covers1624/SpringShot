package net.covers1624.springshot.repo;

import net.covers1624.springshot.entity.UploadObject;
import net.covers1624.springshot.entity.User;
import net.covers1624.springshot.entity.UserObject;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Created by covers1624 on 7/11/20.
 */
public interface UserObjectRepository extends CrudRepository<UserObject, Long> {

    Optional<UserObject> findByAddress(String address);

    Optional<UserObject> findByOwnerAndObject(User user, UploadObject object);

}
