package com.github.supercoding.web.controller.direction;

import com.github.supercoding.service.exceptions.CAuthenticationEntryPointException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/exceptions")
public class ExceptionController {
    @GetMapping(value = "/entrypoint")
    public void entrypointException() {
        throw new CAuthenticationEntryPointException("인증 과정에서 에러");
    }
    @GetMapping(value = "/access-denied")
    public void accessDeniedException() throws AccessDeniedException {
        throw new AccessDeniedException("");
    }
}
