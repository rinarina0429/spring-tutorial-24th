package com.ceos24.springboot;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;

    @Transactional(readOnly = true)
    public List<Test> findAllTests() {
        return testRepository.findAll();
    }
}
