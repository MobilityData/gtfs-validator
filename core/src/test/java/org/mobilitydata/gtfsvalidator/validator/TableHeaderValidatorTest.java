package org.mobilitydata.gtfsvalidator.validator;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.DuplicatedColumnNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredColumnError;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.UnknownColumnNotice;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class TableHeaderValidatorTest {
    @Test
    public void expectedColumns() {
        NoticeContainer container = new NoticeContainer();

        assertThat(new TableHeaderValidator().validate("stops.txt", new String[]{"stop_id", "stop_name"},
                ImmutableSet.of("stop_id", "stop_name", "stop_lat", "stop_lon"),
                ImmutableSet.of("stop_id"), container)).isTrue();
        assertThat(container.getNotices().isEmpty()).isTrue();
    }

    @Test
    public void unknownColumn() {
        NoticeContainer container = new NoticeContainer();

        assertThat(new TableHeaderValidator().validate("stops.txt", new String[]{"stop_id", "stop_name", "stop_extra"},
                ImmutableSet.of("stop_id", "stop_name"), ImmutableSet.of("stop_id"), container)).isTrue();
        assertThat(container.getNotices()).containsExactly(new UnknownColumnNotice("stops.txt", "stop_extra", 3));
    }

    @Test
    public void missingRequiredColumn() {
        NoticeContainer container = new NoticeContainer();

        assertThat(new TableHeaderValidator().validate("stops.txt", new String[]{"stop_name"},
                ImmutableSet.of("stop_id", "stop_name"),
                ImmutableSet.of("stop_id"), container)).isFalse();
        assertThat(container.getNotices()).containsExactly(new MissingRequiredColumnError("stops.txt", "stop_id"));
    }

    @Test
    public void duplicatedColumn() {
        NoticeContainer container = new NoticeContainer();

        assertThat(new TableHeaderValidator().validate("stops.txt", new String[]{"stop_id", "stop_name", "stop_id"},
                ImmutableSet.of("stop_id", "stop_name"),
                ImmutableSet.of("stop_id"), container)).isFalse();
        assertThat(container.getNotices()).containsExactly(new DuplicatedColumnNotice("stops.txt", "stop_id", 1, 3));
    }

    @Test
    public void emptyFile() {
        NoticeContainer container = new NoticeContainer();

        assertThat(new TableHeaderValidator().validate("stops.txt", new String[]{},
                ImmutableSet.of("stop_id", "stop_name"),
                ImmutableSet.of("stop_id"), container)).isTrue();
        assertThat(container.getNotices().isEmpty()).isTrue();
    }
}
