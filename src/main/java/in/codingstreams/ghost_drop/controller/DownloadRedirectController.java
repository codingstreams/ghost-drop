package in.codingstreams.ghost_drop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class DownloadRedirectController {
  @GetMapping("/download/{accessCode}")
  public String forwardToDownloadApi(@PathVariable String accessCode) {
    return "forward:/api/files/download/" + accessCode;
  }
}

