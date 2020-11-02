package org.mobilitydata.gtfsvalidator.domain.entity;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RawEntityTest {

    @Test
    void emptyRowShouldReturnTrue() {
        final RawEntity underTest = new RawEntity(Map.of("first key", "",
                "second key", ""), 22);
        assertTrue(underTest.isBlankLine());

    }

    @Test
    void nonEmptyRowShouldReturnFalse() {
        final RawEntity underTest = new RawEntity(Map.of("first key", "first value",
                "second key", "second value"), 22);
        assertFalse(underTest.isBlankLine());
    }
}
