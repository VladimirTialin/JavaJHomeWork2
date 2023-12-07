package ru.gb.lesson2.hw;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class TestProcessor {

  /**
   * Данный метод находит все void методы без аргументов в классе, и запускеет их.
   * <p>
   * Для запуска создается тестовый объект с помощью конструткора без аргументов.
   */
  public static void runTest(Class<?> testClass) {
    final Constructor<?> declaredConstructor;
    try {
      declaredConstructor = testClass.getDeclaredConstructor();
    } catch (NoSuchMethodException e) {
      throw new IllegalStateException("Для класса \"" + testClass.getName() + "\" не найден конструктор без аргументов");
    }

    final Object testObj;
    try {
      testObj = declaredConstructor.newInstance();
    } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
      throw new RuntimeException("Не удалось создать объект класса \"" + testClass.getName() + "\"");
    }

    List<Method> before = new ListAnnForTest().methodsWithAnnotations(testClass, BeforeEach.class);
    List<Method> after = new ListAnnForTest().methodsWithAnnotations(testClass, AfterEach.class);

    List<Method> methods = Arrays.stream(testClass.getDeclaredMethods())
            .filter(x -> x.isAnnotationPresent(Test.class)&& !x.isAnnotationPresent(Skip.class))
            .peek(TestProcessor::checkTestMethod)
            .sorted(Comparator.comparingInt(x->x.getAnnotation(Test.class).order())).
            toList();

    methods.forEach((x)-> {
      printMethod(before,testObj);
      runTest(x,testObj);
      printMethod(after,testObj);});
  }

  private static void checkTestMethod(Method method) {
    if (!method.getReturnType().isAssignableFrom(void.class) || method.getParameterCount() != 0) {
      throw new IllegalArgumentException("Метод \"" + method.getName() + "\" должен быть void и не иметь аргументов");
    }
  }

  private static void runTest(Method testMethod, Object testObj) {
    try {
      testMethod.invoke(testObj);
    } catch (InvocationTargetException | IllegalAccessException e) {
      throw new RuntimeException("Не удалось запустить тестовый метод \"" + testMethod.getName() + "\"");
    } catch (AssertionError e) {
    }
  }
  private static void printMethod(List<Method> methods,Object obj){
    methods.forEach(x -> runTest(x,obj));
  }

}
