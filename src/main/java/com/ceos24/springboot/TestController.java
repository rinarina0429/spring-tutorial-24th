package com.ceos24.springboot;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/tests")
public class TestController {

    private final TestService testService;

    @GetMapping
    public List<Test> findAllTests() {
        return testService.findAllTests();
    }
}
