package net.covers1624.springshot.repo;

import net.covers1624.springshot.entity.UploadObject;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Created by covers1624 on 7/11/20.
 */
public interface UploadObjectRepository extends CrudRepository<UploadObject, Long> {

    Optional<UploadObject> findByHash(String hash);

}
