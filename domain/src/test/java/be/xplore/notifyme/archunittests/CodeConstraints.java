package be.xplore.notifyme.archunittests;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("unused")
@AnalyzeClasses(packages = "be.xplore.notifyme", importOptions = {
    ImportOption.DoNotIncludeTests.class})
public class CodeConstraints {

  @ArchTest
  public static final ArchRule controllerNaming = classes()
      .that().resideInAPackage("..controller..")
      .should().haveSimpleNameContaining("Controller");
  @ArchTest
  public static final ArchRule serviceNaming = classes()
      .that().resideInAPackage("..service..")
      .should().haveSimpleNameContaining("Service").orShould().beInnerClasses();
  @ArchTest
  public static final ArchRule persistenceNaming = classes()
      .that().resideInAPackage("..persistence..")
      .should().haveSimpleNameContaining("Repo");

  @ArchTest
  public static final ArchRule dtoNaming = classes()
      .that().resideInAPackage("..dto..")
      .should().haveSimpleNameContaining("Dto");

  @ArchTest
  public static final ArchRule ControllerAnnotation = classes()
      .that().haveSimpleNameContaining("Controller").should().beAnnotatedWith(Controller.class)
      .orShould().beAnnotatedWith(
          RestController.class);
  @ArchTest
  public static final ArchRule ServiceAnnotation = classes()
      .that().haveSimpleNameContaining("Service").should().beAnnotatedWith(Service.class).orShould()
      .beInterfaces();
  @ArchTest
  public static final ArchRule RepositoryAnnotation = classes()
      .that().haveSimpleNameContaining("Repo").should().beAnnotatedWith(Repository.class).orShould()
      .beInterfaces();

  @ArchTest
  public static final ArchRule InterfaceNaming = classes()
      .that().areInterfaces().should().haveSimpleNameStartingWith("I");
}
