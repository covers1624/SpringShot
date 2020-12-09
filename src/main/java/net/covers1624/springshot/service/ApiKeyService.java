package net.covers1624.springshot.service;

import net.covers1624.springshot.entity.ApiKey;
import net.covers1624.springshot.entity.User;
import net.covers1624.springshot.form.KeyRevocationForm;
import net.covers1624.springshot.repo.ApiKeyRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by covers1624 on 6/11/20.
 */
@Service
public class ApiKeyService {

    private static final Logger logger = LogManager.getLogger();

    private final ApiKeyRepository apiKeyRepo;

    public ApiKeyService(ApiKeyRepository apiKeyRepo) {
        this.apiKeyRepo = apiKeyRepo;
    }

    public List<ApiKey> getAllKeys(User user) {
        return apiKeyRepo.findAllByUser(user);
    }

    public ApiKey allocateKey(User user) {
        ApiKey key = new ApiKey();
        key.setUser(user);
        String secret;
        Optional<ApiKey> existing;
        do {
            secret = UUID.randomUUID().toString();
            existing = apiKeyRepo.findBySecret(secret);
        }
        while (existing.isPresent());
        key.setSecret(secret);
        apiKeyRepo.save(key);
        return key;
    }

    public void revokeKey(KeyRevocationForm key) {
        apiKeyRepo.findBySecret(key.getSecret()).ifPresent(apiKeyRepo::delete);
    }
}
