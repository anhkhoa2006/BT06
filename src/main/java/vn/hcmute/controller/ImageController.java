package vn.hcmute.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import vn.hcmute.utils.Constants;

import java.io.File;
import java.nio.file.Files;

@Controller
public class ImageController {

    @GetMapping("/image")
    @ResponseBody
    public ResponseEntity<Resource> getImage(@RequestParam(name = "fname", required = false) String fileName) {
        if (fileName == null || fileName.trim().isEmpty() || fileName.contains("..")) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        File file = new File(Constants.DIR, fileName);
        if (!file.exists() || !file.isFile()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Resource resource = new FileSystemResource(file);
        HttpHeaders headers = new HttpHeaders();
        try {
            String mimeType = Files.probeContentType(file.toPath());
            if (mimeType != null) {
                headers.setContentType(MediaType.parseMediaType(mimeType));
            } else {
                headers.setContentType(MediaType.IMAGE_JPEG);
            }
        } catch (Exception e) {
            headers.setContentType(MediaType.IMAGE_JPEG);
        }

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
}
