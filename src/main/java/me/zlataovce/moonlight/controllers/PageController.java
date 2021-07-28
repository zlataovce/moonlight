package me.zlataovce.moonlight.controllers;

import lombok.RequiredArgsConstructor;
import me.zlataovce.moonlight.misc.JavaStackTraceParser;
import me.zlataovce.moonlight.misc.ParsingUtils;
import me.zlataovce.moonlight.storage.Paste;
import me.zlataovce.moonlight.storage.PasteRepository;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class PageController {
    private final PasteRepository pasteRepository;

    @GetMapping(path = "/")
    public String index() {
        return "index";
    }

    @GetMapping(path = "/error")
    public String error(@RequestParam String error, @RequestParam String content, Model model) {
        model.addAttribute("error", error);
        model.addAttribute("content", content);

        return "error";
    }

    @GetMapping(path = "/view/{id}")
    public String view(@PathVariable(name="id") String id, Model model) {
        final Optional<Paste> search = this.pasteRepository.findByIdentifier(id);
        if (search.isPresent()) {
            final String[] data = new String(Base64.getDecoder().decode(search.get().getContent()), Charset.defaultCharset()).split("\n");
            final List<String> dataList = Arrays.stream(data).collect(Collectors.toList());
            model.addAttribute("content", dataList);
            // normalizing line prefix
            model.addAttribute("maxlineprefixlength", ((dataList.size() - 1) + ":").length());
            model.addAttribute("exceptions", JavaStackTraceParser.parseExceptions(JavaStackTraceParser.isolateExceptions(Arrays.stream(data).filter(x -> !x.equals("")).collect(Collectors.toList()))));
            return "view";
        } else {
            return "redirect:error?error=404&content=Log%20not%20found.";
        }
    }

    @PostMapping(path = "/put", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public String saveAndRetrieve(@RequestParam MultiValueMap<String, String> formData) {
        final String toSave = formData.getFirst("input");
        if (toSave == null) {
            return "redirect:error?error=400&content=Invalid%20request%20body.";
        }

        final String id = RandomStringUtils.randomAlphanumeric(15);
        this.pasteRepository.save(new Paste().setContent(Base64.getEncoder().encodeToString(ParsingUtils.filterIpAddr(toSave).getBytes(Charset.defaultCharset()))).setIdentifier(id).setUrl(null));
        return "redirect:view/" + id;
    }

    @PostMapping(path = "/retrieve", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public String search(@RequestParam MultiValueMap<String, String> formData) {
        final String searchFor = formData.getFirst("input");
        if (searchFor == null) {
            return "redirect:error?error=400&content=Invalid%20request%20body.";
        }
        final Optional<Paste> searchUrl = this.pasteRepository.findByUrl(searchFor);
        if (searchUrl.isPresent()) {
            return "redirect:view/" + searchUrl.get().getIdentifier();
        }
        final Optional<Paste> searchIdentifier = this.pasteRepository.findByIdentifier(searchFor);
        if (searchIdentifier.isPresent()) {
            return "redirect:view/" + searchIdentifier.get().getIdentifier();
        }
        try {
            final List<String> content = IOUtils.readLines(new InputStreamReader(new URL(searchFor).openConnection().getInputStream()));
            final String id = RandomStringUtils.randomAlphanumeric(15);
            this.pasteRepository.save(new Paste().setContent(Base64.getEncoder().encodeToString(ParsingUtils.filterIpAddr(String.join("\n", content)).getBytes(Charset.defaultCharset()))).setIdentifier(id).setUrl(searchFor));
            return "redirect:view/" + id;
        } catch (IOException e) {
            return "redirect:error?error=500&content=Could%20not%20acquire%20log.";
        }
    }
}
