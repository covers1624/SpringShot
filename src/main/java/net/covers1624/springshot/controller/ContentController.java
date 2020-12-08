package net.covers1624.springshot.controller;

import net.covers1624.springshot.SpringShotProperties;
import net.covers1624.springshot.repo.UserObjectRepository;
import net.covers1624.springshot.service.ObjectService;
import net.covers1624.springshot.util.Utils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

/**
 * Created by covers1624 on 9/11/20.
 */
@Controller
public class ContentController {

    private final ObjectService objectService;
    private final UserObjectRepository userObjectRepo;

    public ContentController(ObjectService objectService, UserObjectRepository userObjectRepo) {
        this.objectService = objectService;
        this.userObjectRepo = userObjectRepo;
    }

    @ResponseBody
    @GetMapping ("/{address}")
    public ResponseEntity<byte[]> getContent(@PathVariable ("address") String address) throws IOException {
        var userObject = userObjectRepo.findByAddress(address);
        if (userObject.isPresent()) {
            var object = userObject.get();
            object.setLastAccess(new Date());
            userObjectRepo.save(object);
            byte[] bytes;
            Path objectPath = objectService.getPath(object.getObject());
            try (InputStream is = Files.newInputStream(objectPath)) {
                bytes = Utils.toBytes(is);
            }
            var builder = ResponseEntity.ok();
            var headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(Files.probeContentType(objectPath)));
            headers.setContentDisposition(ContentDisposition.builder("inline")
                    .filename(object.getName())
                    .build()
            );
            builder.headers(headers);
            return builder.body(bytes);
        }

        return ResponseEntity.notFound().build();
    }

}
