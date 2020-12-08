package net.covers1624.springshot.controller;

import net.covers1624.springshot.entity.User;
import net.covers1624.springshot.entity.UserObject;
import net.covers1624.springshot.service.ObjectService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Created by covers1624 on 7/11/20.
 */
@Controller
@RequestMapping ("/api/v1/")
@SuppressWarnings ("UnstableApiUsage")
public class ApiV1Controller {

    private static final Logger logger = LogManager.getLogger();

    private final ObjectService objectService;

    public ApiV1Controller(ObjectService objectService) {
        this.objectService = objectService;
    }

    @PostMapping (value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<String> upload(@RequestParam ("file") MultipartFile file, Authentication auth) throws IOException {
        UserObject object = objectService.ingestUserUpload(file, (User) auth.getPrincipal());
        return ResponseEntity.ok(objectService.getAddress(object));
    }

//    @GetMapping ("exists")
//    public ResponseEntity<String> objectExists(@RequestParam ("hash") String hash, Authentication auth) {
//        User user = (User) auth.getPrincipal();
//
//        Optional<UploadObject> objectOpt = objectRepo.findByHash(hash);
//        if (objectOpt.isPresent()) {
//            return ResponseEntity.ok(StringUtils.appendIfMissing(user.getLinkPrefix(), "/") + objectOpt.get().getAddress());
//        }
//
//        return ResponseEntity.notFound().build();
//    }
}
