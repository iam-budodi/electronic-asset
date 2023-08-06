package com.japhet_sebastian.organization.repository;

import com.japhet_sebastian.organization.control.CollegeRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CollegeRepositoryTest {

    @Inject
    CollegeRepository repository;

    @Test
    public void testSearchCollege() {

    }

}