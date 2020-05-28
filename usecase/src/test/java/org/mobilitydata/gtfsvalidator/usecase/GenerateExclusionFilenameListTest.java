/*
 * Copyright (c) 2020. MobilityData IO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.usecase;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.schema.GtfsNode;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mockito.ArgumentMatchers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * This class is aimed at testing the filename list to exclude resulting from the algorithm. To do so, a simplified .
 * graph is used as proof of concept. It is represented as follows:
 *
 *                                         level0_file                         Root
 *                                        /           \
 *                                       /             \
 *                                      /               \
 *                        level1_first_child     level1_second_child          Depth: 1
 *                                  |
 *                                  |
 *                        level2_only_child                                   Depth: 2
 *
 */
class GenerateExclusionFilenameListTest {

    @Test
    void malformedFilenameListShouldLeadToValidationProcessOnAllFiles() {
        final GtfsSpecRepository mockGtfsSpecRepo = spy(GtfsSpecRepository.class);

        final ExecParamRepository mockExecParamRepo = mock(ExecParamRepository.class);
        when(mockExecParamRepo.getExecParamValue(ExecParamRepository.EXCLUSION_KEY))
                .thenReturn("[wrong_file_name.txt]");

        final Logger mockLogger = mock(Logger.class);

        final GenerateExclusionFilenameList underTest =
                new GenerateExclusionFilenameList(mockGtfsSpecRepo, mockExecParamRepo, mockLogger);
        final ArrayList<String> toCheck = underTest.execute();

        verify(mockLogger, times(1)).info("Some file requested to be excluded is not" +
                " defined by the official GTFS specification: [wrong_file_name.txt] -- will execute validation" +
                " process on all files");
        verify(mockExecParamRepo, times(1))
                .getExecParamValue(ExecParamRepository.EXCLUSION_KEY);
        verify(mockGtfsSpecRepo, times(1)).getOptionalFilenameList();
        verify(mockGtfsSpecRepo, times(1)).getRequiredFilenameList();
        verify(mockGtfsSpecRepo, times(1)).getGtfsRelationshipDescriptor();
        verifyNoMoreInteractions(mockExecParamRepo, mockGtfsSpecRepo, mockLogger);
        assertEquals(0, toCheck.size());
    }

    @Test
    void excludeRootShouldExcludeAllFile() {
        final GtfsNode level2OnlyChild = spy(new GtfsNode("level2_only_child", null));
        final GtfsNode level1SecondChild = spy(new GtfsNode("level1_second_child", null));
        final GtfsNode level1FirstChild = spy(
                new GtfsNode("level1_first_child", new ArrayList<>(List.of(level2OnlyChild))));
        final GtfsNode level0Node = spy(
                new GtfsNode("level0_file", new ArrayList<>(List.of(level1FirstChild, level1SecondChild))));

        final List<GtfsNode> childrenCollection =
                new ArrayList<>(List.of(level0Node, level1FirstChild, level1SecondChild, level2OnlyChild));
        final GtfsNode mockRoot = spy(new GtfsNode("level0_file", childrenCollection));

        final GtfsSpecRepository mockGtfsSpecRepo = spy(GtfsSpecRepository.class);
        when(mockGtfsSpecRepo.getOptionalFilenameList()).thenReturn(Collections.emptyList());
        when(mockGtfsSpecRepo.getRequiredFilenameList())
                .thenReturn(List.of("level0_file", "level1_first_child", "level1_second_child", "level2_only_child"));

        final ExecParamRepository mockExecParamRepo = mock(ExecParamRepository.class);
        when(mockExecParamRepo.getExecParamValue(ExecParamRepository.EXCLUSION_KEY))
                .thenReturn("[level0_file]");
        when(mockGtfsSpecRepo.getGtfsRelationshipDescriptor()).thenReturn(mockRoot);

        final Logger mockLogger = mock(Logger.class);

        final GenerateExclusionFilenameList underTest =
                new GenerateExclusionFilenameList(mockGtfsSpecRepo, mockExecParamRepo, mockLogger);

        final ArrayList<String> toCheck = underTest.execute();

        verify(mockExecParamRepo, times(1))
                .getExecParamValue(ExecParamRepository.EXCLUSION_KEY);
        verify(mockGtfsSpecRepo, times(1)).getOptionalFilenameList();
        verify(mockGtfsSpecRepo, times(1)).getRequiredFilenameList();
        verify(mockGtfsSpecRepo, times(1)).getGtfsRelationshipDescriptor();
        verify(level0Node, times(1)).DFS(any());

        assertEquals(4, toCheck.size());
        assertTrue(toCheck.contains("level0_file"));
        assertTrue(toCheck.contains("level1_first_child"));
        assertTrue(toCheck.contains("level1_second_child"));
        assertTrue(toCheck.contains("level2_only_child"));

        verify(mockRoot, times(1)).getChildByName(ArgumentMatchers.eq("level0_file"));
        verify(mockRoot, times(1)).getChildren();

        verifyNoMoreInteractions(mockExecParamRepo, mockGtfsSpecRepo, mockRoot);
    }

    @Test
    void excludeAllFileWithDepth1ShouldExclude3File() {
        final GtfsNode level2OnlyChild = spy(new GtfsNode("level2_only_child", null));
        final GtfsNode level1SecondChild = spy(new GtfsNode("level1_second_child", null));
        final GtfsNode level1FirstChild = spy(
                new GtfsNode("level1_first_child", new ArrayList<>(List.of(level2OnlyChild))));
        final GtfsNode level0Node = spy(
                new GtfsNode("level0_file", new ArrayList<>(List.of(level1FirstChild, level1SecondChild))));

        final List<GtfsNode> childrenCollection =
                new ArrayList<>(List.of(level0Node, level1FirstChild, level1SecondChild, level2OnlyChild));
        final GtfsNode mockRoot = spy(new GtfsNode("level0_file", childrenCollection));

        final GtfsSpecRepository mockGtfsSpecRepo = spy(GtfsSpecRepository.class);
        when(mockGtfsSpecRepo.getOptionalFilenameList()).thenReturn(Collections.emptyList());
        when(mockGtfsSpecRepo.getRequiredFilenameList())
                .thenReturn(new ArrayList<>(List.of("level0_file", "level1_first_child",
                        "level1_second_child", "level2_only_child")));

        final ExecParamRepository mockExecParamRepo = mock(ExecParamRepository.class);
        when(mockExecParamRepo.getExecParamValue(ExecParamRepository.EXCLUSION_KEY))
                .thenReturn("[level1_second_child,level1_first_child]");
        when(mockGtfsSpecRepo.getGtfsRelationshipDescriptor()).thenReturn(mockRoot);

        final Logger mockLogger = mock(Logger.class);

        final GenerateExclusionFilenameList underTest =
                new GenerateExclusionFilenameList(mockGtfsSpecRepo, mockExecParamRepo, mockLogger);

        final ArrayList<String> toCheck = underTest.execute();

        verify(mockExecParamRepo, times(1))
                .getExecParamValue(ExecParamRepository.EXCLUSION_KEY);
        verify(mockGtfsSpecRepo, times(1)).getOptionalFilenameList();
        verify(mockGtfsSpecRepo, times(1)).getRequiredFilenameList();
        verify(mockGtfsSpecRepo, times(1)).getGtfsRelationshipDescriptor();
        verify(level1SecondChild, times(1)).DFS(any());
        verify(mockRoot, times(1))
                .getChildByName(ArgumentMatchers.eq("level1_second_child"));
        verify(mockRoot, times(2)).getChildren();
        verify(mockRoot, times(1))
                .getChildByName(ArgumentMatchers.eq("level1_first_child"));

        assertEquals(3, toCheck.size());
        assertTrue(toCheck.contains("level1_first_child"));
        assertTrue(toCheck.contains("level1_second_child"));
        assertTrue(toCheck.contains("level2_only_child"));
        verifyNoMoreInteractions(mockExecParamRepo, mockGtfsSpecRepo, mockRoot);
    }

    @Test
    void excludeAllFileWithDepth2ShouldExclude1File() {
        final GtfsNode level2OnlyChild = spy(new GtfsNode("level2_only_child", null));
        final GtfsNode level1SecondChild = spy(new GtfsNode("level1_second_child", null));
        final GtfsNode level1FirstChild = spy(
                new GtfsNode("level1_first_child", new ArrayList<>(List.of(level2OnlyChild))));
        final GtfsNode level0Node = spy(
                new GtfsNode("level0_file", new ArrayList<>(List.of(level1FirstChild, level1SecondChild))));

        final List<GtfsNode> childrenCollection =
                new ArrayList<>(List.of(level0Node, level1FirstChild, level1SecondChild, level2OnlyChild));
        final GtfsNode mockRoot = spy(new GtfsNode("level0_file", childrenCollection));

        final GtfsSpecRepository mockGtfsSpecRepo = spy(GtfsSpecRepository.class);
        when(mockGtfsSpecRepo.getOptionalFilenameList()).thenReturn(Collections.emptyList());
        when(mockGtfsSpecRepo.getRequiredFilenameList())
                .thenReturn(new ArrayList<>(List.of("level0_file", "level1_first_child",
                        "level1_second_child", "level2_only_child")));

        final ExecParamRepository mockExecParamRepo = mock(ExecParamRepository.class);
        when(mockExecParamRepo.getExecParamValue(ExecParamRepository.EXCLUSION_KEY))
                .thenReturn("[level2_only_child]");
        when(mockGtfsSpecRepo.getGtfsRelationshipDescriptor()).thenReturn(mockRoot);

        final Logger mockLogger = mock(Logger.class);

        final GenerateExclusionFilenameList underTest =
                new GenerateExclusionFilenameList(mockGtfsSpecRepo, mockExecParamRepo, mockLogger);

        final ArrayList<String> toCheck = underTest.execute();

        verify(mockExecParamRepo, times(1)).getExecParamValue(ExecParamRepository.EXCLUSION_KEY);
        verify(mockGtfsSpecRepo, times(1)).getOptionalFilenameList();
        verify(mockGtfsSpecRepo, times(1)).getRequiredFilenameList();
        verify(mockGtfsSpecRepo, times(1)).getGtfsRelationshipDescriptor();
        verify(level2OnlyChild, times(1)).DFS(any());
        verify(mockRoot, times(1))
                .getChildByName(ArgumentMatchers.eq("level2_only_child"));
        verify(mockRoot, times(1)).getChildren();
        verify(mockRoot, times(1))
                .getChildByName(ArgumentMatchers.eq("level2_only_child"));

        assertEquals(1, toCheck.size());
        assertTrue(toCheck.contains("level2_only_child"));
        verifyNoMoreInteractions(mockExecParamRepo, mockGtfsSpecRepo, mockRoot);
    }
}