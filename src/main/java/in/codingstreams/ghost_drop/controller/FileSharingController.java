package in.codingstreams.ghost_drop.controller;

import in.codingstreams.ghost_drop.dto.FileUploadResponse;
import in.codingstreams.ghost_drop.service.filesharing.FileSharingService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class FileSharingController {
  private final FileSharingService fileSharingService;

  @PostMapping("/upload")
  public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
    var response = fileSharingService.uploadFile(file);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/download/{accessCode}")
  public ResponseEntity<Resource> downloadFile(@PathVariable String accessCode) {
    var wrapper = fileSharingService.getFile(accessCode);

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + wrapper.originalFileName() + "\"")
        .contentType(MediaType.parseMediaType(wrapper.contentType()))
        .body(wrapper.resource());
  }

  @GetMapping("/{accessCode}/info")
  public ResponseEntity<FileUploadResponse> getFileInfo(@PathVariable String accessCode) {
    return ResponseEntity.ok(fileSharingService.getFileInfo(accessCode));
  }
}