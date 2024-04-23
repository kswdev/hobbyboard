package com.hobbyboard.application.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hobbyboard.annotation.CurrentUser;
import com.hobbyboard.application.usacase.StudyAccountUsacase;
import com.hobbyboard.domain.account.dto.account.AccountDto;
import com.hobbyboard.domain.study.dto.StudyDescriptionForm;
import com.hobbyboard.domain.study.dto.StudyDto;
import com.hobbyboard.domain.study.dto.StudyForm;
import com.hobbyboard.domain.study.entity.Study;
import com.hobbyboard.domain.study.entity.StudyTag;
import com.hobbyboard.domain.study.entity.StudyZone;
import com.hobbyboard.domain.study.service.StudyReadService;
import com.hobbyboard.domain.study.service.StudyWriteService;
import com.hobbyboard.domain.tag.dto.TagForm;
import com.hobbyboard.domain.tag.entity.Tag;
import com.hobbyboard.domain.tag.service.TagService;
import com.hobbyboard.domain.zone.dto.ZoneDto;
import com.hobbyboard.domain.zone.dto.request.ZoneForm;
import com.hobbyboard.domain.zone.entity.Zone;
import com.hobbyboard.domain.zone.service.ZoneService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.Banner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequestMapping("/study/{path}/settings")
@RequiredArgsConstructor
@Controller
public class StudySettingController {

    private final StudyWriteService studyWriteService;
    private final StudyReadService studyReadService;
    private final StudyAccountUsacase studyAccountUsacase;
    private final TagService tagService;
    private final ZoneService zoneService;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;

    @GetMapping("/study")
    public String study(
            @PathVariable String path,
            @CurrentUser AccountDto accountDto,
            Model model
    ) {
        StudyDto studyDto = StudyDto.from(studyReadService.findByPath(path));

        model.addAttribute("study", studyDto);
        model.addAttribute("account", accountDto);
        return "study/settings/study";
    }

    @GetMapping("/zones")
    public String updateZones(
            @CurrentUser AccountDto accountDto,
            @PathVariable String path,
            Model model
    ) throws JsonProcessingException {

        List<String> zoneList = zoneService.findAll().stream()
                .map(Zone::toString)
                .toList();

        StudyDto studyDto = StudyDto.from(studyReadService.findByPath(path));
        List<String> zones = studyDto.getZones().stream()
                .map(ZoneDto::toString)
                .toList();

        model.addAttribute("study", studyDto);
        model.addAttribute("zones", zones);
        model.addAttribute("whitelist", objectMapper.writeValueAsString(zoneList));
        return "study/settings/zones";
    }

    @PostMapping("/zones/add")
    @ResponseBody
    public ResponseEntity<Void> addZones(
            @PathVariable String path,
            @RequestBody ZoneForm zoneForm
    ) {
        studyAccountUsacase.addZones(path, zoneForm);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/zones/remove")
    @ResponseBody
    public ResponseEntity<Void> removeZones(
            @PathVariable String path,
            @RequestBody ZoneForm zoneForm
    ) {
        studyAccountUsacase.removeZones(path, zoneForm);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tags/add")
    @ResponseBody
    public ResponseEntity<Void> addTags(
            @RequestBody TagForm tagForm,
            @PathVariable String path
    ) {
        studyAccountUsacase.addTag(tagForm, path);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tags/remove")
    @ResponseBody
    public ResponseEntity<Void> removeTags(
            @RequestBody TagForm tagForm,
            @PathVariable String path
    ) {
        studyAccountUsacase.removeTag(tagForm, path);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tags")
    public String viewStudyTag(
            @CurrentUser AccountDto accountDto,
            @PathVariable String path,
            Model model
    ) throws JsonProcessingException {
        StudyDto studyDto = StudyDto.from(studyReadService.findByPath(path));
        Set<String> tags = studyDto.getTags();
        Set<String> allTags = tagService.findAll().stream()
                .map(Tag::getTitle)
                .collect(Collectors.toUnmodifiableSet());

        model.addAttribute("study", studyDto);
        model.addAttribute("account", accountDto);
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTags));
        model.addAttribute("tags", tags);
        return "study/settings/tags";
    }

    @GetMapping("/banner")
    public String viewStudyBanner(
            @CurrentUser AccountDto accountDto,
            @PathVariable String path,
            Model model
    ) {
        StudyDto studyDto   = StudyDto.from(studyAccountUsacase.getStudyToUpdate(accountDto, path));

        model.addAttribute("account", accountDto);
        model.addAttribute("study", studyDto);

        return "study/settings/banner";
    }

    @PostMapping("/banner/enable")
    public String bannerEnable(
            @PathVariable String path,
            RedirectAttributes attributes
    ) {
        studyWriteService.enableStudyBanner(path);
        attributes.addFlashAttribute("message", "배너가 활성화되었습니다.");

        return "redirect:/study/" + getEncode(path) + "/settings/banner";
    }

    @PostMapping("/banner/disable")
    public String bannerDisable(
            @PathVariable String path,
            RedirectAttributes attributes
    ) {
        studyWriteService.disableStudyBanner(path);
        attributes.addFlashAttribute("message", "배너가 비활성화되었습니다.");

        return "redirect:/study/" + getEncode(path) + "/settings/banner";
    }

    @PostMapping("/banner")
    public String updateBannerImage(
            @CurrentUser AccountDto accountDto,
            @PathVariable String path,
            String image,
            RedirectAttributes attributes
    ) {
        studyAccountUsacase.updateStudyImage(accountDto, path, image);
        attributes.addFlashAttribute("message", "스터디 이미지를 수정했습니다.");

        return "redirect:/study/" + getEncode(path) + "/settings/banner";
    }

    @GetMapping("/description")
    public String viewStudySetting(
            @CurrentUser AccountDto accountDto,
            @PathVariable String path,
            Model model
    ) {
        StudyDto studyDto   = StudyDto.from(studyAccountUsacase.getStudyToUpdate(accountDto, path));
        StudyForm studyForm = modelMapper.map(studyDto, StudyForm.class);

        model.addAttribute("account", accountDto);
        model.addAttribute("study", studyDto);
        model.addAttribute("studyDescriptionForm", studyForm);

        return "study/settings/description";
    }

    @PostMapping("/description")
    public String updateStudyInfo(
            @Valid StudyDescriptionForm studyForm,
            @CurrentUser AccountDto accountDto,
            @PathVariable String path,
            RedirectAttributes attributes,
            BindingResult errors,
            Model model
    ) {
        if (errors.hasErrors()) {
            model.addAttribute("account", accountDto);
            model.addAttribute("studyDescriptionForm", studyForm);
            return "study/settings/description";
        }

        Study updatedStudy = studyWriteService.updateStudyDescription(studyForm);
        attributes.addFlashAttribute("message", "스터디 소개를 수정했습니다.");
        return "redirect:/study/" + getEncode(updatedStudy.getPath()) + "/settings/description";
    }

    private static String getEncode(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }
}
