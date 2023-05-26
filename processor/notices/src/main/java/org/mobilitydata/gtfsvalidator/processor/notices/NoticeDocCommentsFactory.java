package org.mobilitydata.gtfsvalidator.processor.notices;

import java.util.List;
import java.util.Optional;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import org.mobilitydata.gtfsvalidator.notice.NoticeDocComments;
import org.mobilitydata.gtfsvalidator.processor.notices.CommentCleaner.SplitComment;

/**
 * Provides methods for constructing {@link NoticeDocComments} from a Notice class' {@link
 * TypeElement}.
 */
public class NoticeDocCommentsFactory {

  private CommentCleaner cleaner = new CommentCleaner();

  private final Elements elements;

  public NoticeDocCommentsFactory(Elements elements) {
    this.elements = elements;
  }

  /**
   * Returns document comments for the notice type element or `empty` if we were unable to extract
   * any useful comments.
   */
  public Optional<NoticeDocComments> create(TypeElement element) {
    NoticeDocComments comments = new NoticeDocComments();

    String noticeComment = elements.getDocComment(element);
    if (noticeComment != null) {
      List<String> lines = cleaner.cleanComment(noticeComment);
      SplitComment splitComment = cleaner.splitLinesIntoSummaryAndAdditionalDocumentation(lines);
      comments.setShortSummary(splitComment.shortSummary);
      comments.setAdditionalDocumentation(splitComment.additionalDocumentation);
    }

    for (VariableElement field : ElementFilter.fieldsIn(element.getEnclosedElements())) {
      String fieldName = field.getSimpleName().toString();
      String fieldComment = elements.getDocComment(field);
      if (fieldComment != null) {
        String cleanComment = String.join("\n", cleaner.cleanComment(fieldComment));
        comments.putFieldComment(fieldName, cleanComment);
      }
    }

    return comments.isEmpty() ? Optional.empty() : Optional.of(comments);
  }
}
