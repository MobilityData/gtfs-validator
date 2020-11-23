package org.mobilitydata.gtfsvalidator.table;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.input.GtfsFeedName;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static com.google.common.truth.Truth.assertThat;

/**
 * Runs GtfsLevelTableContainer on test CSV data.
 */
@RunWith(JUnit4.class)
public class GtfsLevelTableLoaderTest {
    private static final GtfsFeedName FEED_NAME = GtfsFeedName.parseString("au-sydney-buses");

    @Test
    public void validFile() throws IOException {
        ValidatorLoader validatorLoader = new ValidatorLoader();
        Reader reader = new StringReader("level_id,level_name,level_index\n"
                + "level1,Ground,1\n");
        GtfsLevelTableLoader loader = new GtfsLevelTableLoader();
        NoticeContainer noticeContainer = new NoticeContainer();
        GtfsLevelTableContainer tableContainer = (GtfsLevelTableContainer) loader.load(reader, FEED_NAME, validatorLoader, noticeContainer);

        assertThat(noticeContainer.getNotices()).isEmpty();
        assertThat(tableContainer.entityCount()).isEqualTo(1);
        GtfsLevel level = tableContainer.byLevelId("level1");
        assertThat(level).isNotNull();
        assertThat(level.levelId()).isEqualTo("level1");
        assertThat(level.levelName()).isEqualTo("Ground");
        assertThat(level.levelIndex()).isEqualTo(1);

        reader.close();
    }

    @Test
    public void missingRequiredField() throws IOException {
        ValidatorLoader validatorLoader = new ValidatorLoader();
        Reader reader = new StringReader("level_id,level_name,level_index\n"
                + ",Ground,1\n");
        GtfsLevelTableLoader loader = new GtfsLevelTableLoader();
        NoticeContainer noticeContainer = new NoticeContainer();
        GtfsLevelTableContainer tableContainer = (GtfsLevelTableContainer) loader.load(reader, FEED_NAME, validatorLoader, noticeContainer);

        assertThat(noticeContainer.getNotices()).isNotEmpty();
        assertThat(tableContainer.entityCount()).isEqualTo(0);
        reader.close();
    }

    @Test
    public void emptyFile() throws IOException {
        ValidatorLoader validatorLoader = new ValidatorLoader();
        Reader reader = new StringReader("");
        GtfsLevelTableLoader loader = new GtfsLevelTableLoader();
        NoticeContainer noticeContainer = new NoticeContainer();
        GtfsLevelTableContainer tableContainer = (GtfsLevelTableContainer) loader.load(reader, FEED_NAME, validatorLoader, noticeContainer);

        assertThat(noticeContainer.getNotices()).isNotEmpty();
        assertThat(noticeContainer.getNotices().get(0).getClass().getSimpleName()).isEqualTo("EmptyFileNotice");
        reader.close();
    }

}

