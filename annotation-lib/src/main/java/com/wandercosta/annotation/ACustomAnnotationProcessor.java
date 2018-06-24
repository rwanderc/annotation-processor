package com.wandercosta.annotation;

import static java.util.stream.Collectors.toSet;

import com.google.auto.service.AutoService;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("com.wandercosta.annotation.ACustomAnnotation")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class ACustomAnnotationProcessor extends AbstractProcessor {

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
                    writeBuilderFile(tel.getQualifiedName().toString());
                } catch (Exception e) {
                }
            }
        }
        return true;
    }

    private void writeBuilderFile(String className) throws IOException {
        String packageName = null;
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = className.substring(0, lastDot);
        }

        String simpleClassName = className.substring(lastDot + 1);
        String beanClassName = className + "Bean";
        String beanSimpleClassName = beanClassName.substring(lastDot + 1);

        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(beanClassName);
        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            if (packageName != null) {
                out.print("package ");
                out.print(packageName);
                out.println(";");
                out.println();
            }

            out.println("import org.aeonbits.owner.ConfigFactory;");
            out.println("import org.springframework.context.annotation.Bean;");
            out.println("import org.springframework.context.annotation.Configuration;");
            out.println();

            out.println("@Configuration");
            out.println("class Annotated" + simpleClassName + " {");
            out.println();

            out.println("    @Bean");
            out.println("    " + simpleClassName + " annotated" + simpleClassName + "Bean() {");
            out.println("        return ConfigFactory.create(" + simpleClassName + ".class);");
            out.println("    }");
            out.println();

            out.println("}");
        }
    }

}
