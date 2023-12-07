package ru.gb.lesson2.hw;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class ListAnnForTest {
    public List<Method> methodsWithAnnotations(Class <?> objClass, Class<? extends Annotation> annotationClass) {
        return Arrays.stream(objClass.getDeclaredMethods())
                .filter(x -> x.isAnnotationPresent(annotationClass))
                .toList();
    }
}
