package net.covers1624.springshot.service;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import net.covers1624.springshot.SpringShotProperties;
import net.covers1624.springshot.entity.UploadObject;
import net.covers1624.springshot.entity.User;
import net.covers1624.springshot.entity.UserObject;
import net.covers1624.springshot.repo.UploadObjectRepository;
import net.covers1624.springshot.repo.UserObjectRepository;
import net.covers1624.springshot.util.Utils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Created by covers1624 on 26/11/20.
 */
@Service
@SuppressWarnings ("UnstableApiUsage")
public class ObjectService {

    private static final HashFunction SHA256 = Hashing.sha256();

    private final SpringShotProperties properties;
    private final UploadObjectRepository objectRepo;
    private final UserObjectRepository userObjectRepo;

    public ObjectService(SpringShotProperties properties, UploadObjectRepository objectRepo, UserObjectRepository userObjectRepo) {
        this.properties = properties;
        this.objectRepo = objectRepo;
        this.userObjectRepo = userObjectRepo;
    }

    public UserObject ingestUserUpload(MultipartFile file, User user) throws IOException {
        UploadObject obj = ingestObject(file);

        Optional<UserObject> userObjectOpt = userObjectRepo.findByOwnerAndObject(user, obj);
        if (userObjectOpt.isPresent()) {
            return userObjectOpt.get();
        }

        Hasher hasher = SHA256.newHasher();
        hasher.putString(obj.getHash(), StandardCharsets.UTF_8);
        hasher.putString(user.getUsername(), StandardCharsets.UTF_8);
        String addr = uniqueHash(hasher.hash().toString(), properties.getMinHashLen());

        UserObject userObject = new UserObject();
        userObject.setAddress(addr);
        userObject.setOwner(user);
        userObject.setName(file.getOriginalFilename());
        userObject.setObject(obj);
        userObjectRepo.save(userObject);
        obj.addOwner(userObject);
        objectRepo.save(obj);
        return userObject;
    }

    public UploadObject ingestObject(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        Hasher hasher = SHA256.newHasher();
        hasher.putBytes(bytes);
        String hash = hasher.hash().toString();
        Optional<UploadObject> objectOpt = objectRepo.findByHash(hash);
        if (objectOpt.isPresent()) {
            return objectOpt.get();
        }

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        UploadObject obj = new UploadObject(hash, bytes.length, extension);
        Path path = getPath(obj);
        file.transferTo(Utils.ensureExists(path));
        obj.setContentType(Files.probeContentType(path));
        objectRepo.save(obj);
        return obj;
    }

    public Path getPath(UploadObject object) {
        String suffix = StringUtils.isEmpty(object.getExtension()) ? "" : "." + object.getExtension();
        return properties.getObjectsDir().resolve(object.getHash() + suffix);
    }

    public String getAddress(UserObject object) {
        String linkPrefix = object.getOwner() != null ? object.getOwner().getLinkPrefix() : "https://ss.ln-k.net/";
        return StringUtils.appendIfMissing(linkPrefix, "/") + object.getAddress();
    }

    private String uniqueHash(String input, int minLen) {
        for (int i = minLen; i < input.length(); i++) {
            String substr = input.substring(0, i);
            if (userObjectRepo.findByAddress(substr).isEmpty()) {
                return substr;
            }
        }

        throw new IllegalStateException("Hash already exists, should never get here! " + input);
    }

}
