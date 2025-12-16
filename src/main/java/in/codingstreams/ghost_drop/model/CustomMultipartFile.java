package in.codingstreams.ghost_drop.model;

import in.codingstreams.ghost_drop.exception.InvalidMultipartFileException;
import io.micrometer.common.util.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public record CustomMultipartFile(MultipartFile file) implements MultipartFile {

  @Override
  @NonNull
  public String getName() {
    return requireNonBlank(file.getName(), "Filename cannot be empty/blank or null.");
  }

  @Override
  public String getOriginalFilename() {
    String original = file.getOriginalFilename();
    if (original == null || original.isBlank()) {
      throw new InvalidMultipartFileException("Original filename cannot be null or blank.");
    }
    return original;
  }

  @Override
  public String getContentType() {
    String type = file.getContentType();
    if (type == null || type.isBlank()) {
      throw new InvalidMultipartFileException("Content type cannot be null or blank.");
    }
    return type;
  }

  @Override
  public boolean isEmpty() {
    return file.isEmpty();
  }

  @Override
  public long getSize() {
    if (file.isEmpty()) {
      throw new InvalidMultipartFileException("File cannot be empty.");
    }
    return file.getSize();
  }

  @Override
  public byte @NonNull [] getBytes() throws IOException {
    byte[] bytes = file.getBytes();
    if (bytes.length == 0) {
      throw new InvalidMultipartFileException("File cannot be empty.");
    }
    return bytes;
  }

  @Override
  @NonNull
  public InputStream getInputStream() throws IOException {
    if (file.isEmpty()) {
      throw new InvalidMultipartFileException("File cannot be empty.");
    }
    return file.getInputStream();
  }

  @Override
  public void transferTo(@NonNull File destination) throws IOException, IllegalStateException {
    file.transferTo(destination);
  }

  private String requireNonBlank(String value, String message) {
    if (StringUtils.isBlank(value)) {
      throw new InvalidMultipartFileException(message);
    }
    return value;
  }
}