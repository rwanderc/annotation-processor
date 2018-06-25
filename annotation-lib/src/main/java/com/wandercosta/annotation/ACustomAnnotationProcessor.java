package com.wandercosta.annotation;

import static java.util.stream.Collectors.toSet;

import com.google.auto.service.AutoService;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SupportedAnnotationTypes("com.wandercosta.annotation.ACustomAnnotation")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class ACustomAnnotationProcessor extends AbstractProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(ACustomAnnotationProcessor.class);

    private static final String OWNER_MODEL_FILE = "com/wandercosta/annotation/ownerBeanModel";
    private static final String SEPARATOR = System.getProperty("line.separator", "\n");
    private static final String PACKAGE_PATH_SEPARATOR = ".";
    private static final String BEAN_MODEL_NAME = "Annotated%sBean";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);

            Set<? extends Element> classInterfaceAnnotatedElements = annotatedElements.stream()
                .filter(el -> el.getKind().isClass() || el.getKind().isInterface())
                .collect(toSet());

            for (Element el : classInterfaceAnnotatedElements) {
                TypeElement tel = (TypeElement) el;
                try {
                    processClass(tel.getQualifiedName().toString());
                } catch (Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw new RuntimeException(ex);
                }
            }
        }
        return true;
    }

    private void processClass(String className) throws IOException, URISyntaxException {
        int lastDot = className.lastIndexOf(PACKAGE_PATH_SEPARATOR);
        String packagePath = lastDot > 0 ? className.substring(0, lastDot) : null;
        String shortClassName = className.substring(lastDot + 1);
        String shortBeanClassName = String.format(BEAN_MODEL_NAME, shortClassName);

        String model = readFile(OWNER_MODEL_FILE);
        String code = modelWithValues(model, packagePath, shortBeanClassName, shortClassName);

        String fullBeanClassName = packagePath + PACKAGE_PATH_SEPARATOR + shortBeanClassName;
        writeFile(fullBeanClassName, code);
    }

    private String modelWithValues(String model, String packagePath, String shortBeanClassName, String shortClassName) {
        return model
            .replaceAll("%packageName%", packagePath)
            .replaceAll("%simpleBeanClassName%", shortBeanClassName)
            .replaceAll("%simpleClassName%", shortClassName);
    }

    private String readFile(String fileName) throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getClassLoader().getResource(fileName).toURI());
        try (Stream<String> lines = Files.lines(path)) {
            return lines
                .reduce((s1, s2) -> s1.concat(SEPARATOR).concat(s2))
                .orElseThrow(() -> new IllegalStateException(String.format("Model file '%s' was not found.", fileName)));
        }
    }

    private void writeFile(String path, String code) throws IOException {
        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(path);
        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            out.print(code);
        }
    }

}
