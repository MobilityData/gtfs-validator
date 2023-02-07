package org.mobilitydata.gtfsvalidator.noticeschemagenerator;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.List;

class NoticeNodesCollector extends VoidVisitorAdapter<List<ClassOrInterfaceDeclaration>> {

  private static final SimpleName VALIDATION_NOTICE_PARENT_TYPE =
      new SimpleName("ValidationNotice");

  @Override
  public void visit(ClassOrInterfaceDeclaration cid, List<ClassOrInterfaceDeclaration> arg) {
    super.visit(cid, arg);
    if (cid.getExtendedTypes().stream()
        .anyMatch(cit -> cit.getName().equals(VALIDATION_NOTICE_PARENT_TYPE))) {
      arg.add(cid);
      System.out.println(cid.getName());
    }
  }
}
