package org.mobilitydata.gtfsvalidator.processor.notices;

import java.util.Optional;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import org.mobilitydata.gtfsvalidator.notice.NoticeDocComments;

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
      comments.setDocComment(cleaner.cleanComment(noticeComment));
    }

    for (VariableElement field : ElementFilter.fieldsIn(element.getEnclosedElements())) {
      String fieldName = field.getSimpleName().toString();
      String fieldComment = elements.getDocComment(field);
      if (fieldComment != null) {
        comments.putFieldComment(fieldName, cleaner.cleanComment(fieldComment));
      }
    }

    return comments.isEmpty() ? Optional.empty() : Optional.of(comments);
  }
}
