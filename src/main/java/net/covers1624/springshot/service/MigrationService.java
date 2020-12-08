package net.covers1624.springshot.service;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.opencsv.CSVReader;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.covers1624.springshot.entity.UploadObject;
import net.covers1624.springshot.entity.User;
import net.covers1624.springshot.entity.UserObject;
import net.covers1624.springshot.repo.UploadObjectRepository;
import net.covers1624.springshot.repo.UserObjectRepository;
import net.covers1624.springshot.repo.UserRepository;
import net.covers1624.springshot.util.ThrowingConsumer;
import net.covers1624.springshot.util.Utils;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * Created by covers1624 on 25/11/20.
 */
@Service
@SuppressWarnings ("UnstableApiUsage")
public class MigrationService {

    private static final HashFunction SHA256 = Hashing.sha256();

    private static final Logger logger = LogManager.getLogger();

    private final ObjectService objectService;
    private final UserRepository userRepo;
    private final UploadObjectRepository objectRepo;
    private final UserObjectRepository userObjectRepo;

    public MigrationService(ObjectService objectService, UserRepository userRepo, UploadObjectRepository objectRepo, UserObjectRepository userObjectRepo) {
        this.objectService = objectService;
        this.userRepo = userRepo;
        this.objectRepo = objectRepo;
        this.userObjectRepo = userObjectRepo;
    }

    @PostConstruct
    public void doMigration() throws Exception {
        Path migration = Paths.get("migration");
        if (Files.exists(migration) && Files.isDirectory(migration)) {
            Path objects = migration.resolve("objects");
            logger.info("Found migration folder, beginning migration..");
            Map<String, User> userMap = new HashMap<>();
            migrate(migration.resolve("web_users.csv"), "User", colLookup -> {
                String id = colLookup.get("uuid");
                String name = colLookup.get("name");
                Optional<User> userOpt = userRepo.findByUsername(name);
                User user = userOpt.orElseGet(() -> {
                    User u = new User();
                    u.setUsername(name);
                    u.setPlaceholder(true);
                    userRepo.save(u);
                    return u;
                });
                userMap.put(id, user);
            });
            migrate(migration.resolve("web_screen_shots.csv"), "Screen Shot", colLookup -> {
                String address = colLookup.get("address");
                String sha1 = colLookup.get("sha1");
                String name = colLookup.get("name");
                User user = userMap.get(colLookup.get("user"));
                String ext = FilenameUtils.getExtension(name);
                Path oPath = objects.resolve(sha1 + "." + ext);
                String hash = sha256(oPath);

                Optional<UploadObject> objectOpt = objectRepo.findByHash(hash);
                UploadObject object = objectOpt.orElseGet(() -> {
                    try {
                        UploadObject o = new UploadObject(hash, (int) Files.size(oPath), ext);
                        o.setContentType(Files.probeContentType(oPath));
                        Files.copy(oPath, Utils.ensureExists(objectService.getPath(o)), StandardCopyOption.REPLACE_EXISTING);
                        objectRepo.save(o);
                        return o;
                    } catch (IOException e) {
                        Utils.throwUnchecked(e);
                        return null;//Unpossible
                    }
                });
                if (userObjectRepo.findByOwnerAndObject(user, object).isEmpty()) {
                    UserObject obj = new UserObject();
                    obj.setAddress(address);
                    obj.setOwner(user);
                    obj.setName(name);
                    obj.setObject(object);
                    userObjectRepo.save(obj);
                    object.addOwner(obj);
                    objectRepo.save(object);
                }
                logger.info("Migrated {}'s screenshot {}:{}",  user == null ? "unknown" : user.getUsername(), address, name);
            });
        }
    }

    public void migrate(Path file, String name, ThrowingConsumer<ColLookup, Throwable> handler) throws Exception {
        if (Files.notExists(file)) {
            logger.warn("Missing file, skipping migration of: {}", file);
            return;
        }
        logger.info("Migrating from: {}", file);
        InputStream is = Files.newInputStream(file);
        if (file.getFileName().toString().endsWith(".bz2")) {
            logger.info(" Detected bzip2 compression.");
            is = new BZip2CompressorInputStream(is);
        } else if (file.getFileName().toString().endsWith(".gz")) {
            logger.info(" Detected gzip compression.");
            is = new GzipCompressorInputStream(is);
        }
        try (CSVReader reader = new CSVReader(new InputStreamReader(is))) {
            String[] header = reader.readNext();
            if (header == null) {
                logger.error(" No header for: {}", file);
                return;
            }
            Object2IntMap<String> headerLookup = new Object2IntOpenHashMap<>(header.length);
            headerLookup.defaultReturnValue(-1);
            for (int i = 0; i < header.length; i++) {
                headerLookup.put(header[i], i);
            }

            int numParsed = 0;
            for (String[] line : reader) {
                numParsed++;
                try {
                    handler.accept(e -> {
                        int idx = headerLookup.getInt(e);
                        if (idx == -1) {
                            throw new IllegalArgumentException("Unknown column '" + e + "' Expected: " + Arrays.toString(header));
                        }
                        return line[idx];
                    });
                } catch (Throwable t) {
                    logger.warn("Failed to load csv line, ignoring.. " + Arrays.toString(line), t);
                }
                if ((numParsed % 100) == 0) {
                    logger.info(" Parsed {} {}s", numParsed, name);
                }
            }
            logger.info("Finished migrating {}, found {} {}s", file, numParsed, name);
        }
    }

    private String sha256(Path path) throws IOException {
        Hasher hasher = SHA256.newHasher();
        try (InputStream is = Files.newInputStream(path)) {
            hasher.putBytes(Utils.toBytes(is));
        }
        return hasher.hash().toString();
    }

    private interface ColLookup {

        default boolean getBoolean(String col) {
            return Boolean.parseBoolean(get(col));
        }

        default UUID getUUID(String col) {
            return UUID.fromString(get(col));
        }

        String get(String col);
    }
}
