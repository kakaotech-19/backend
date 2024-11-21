package com.heartsave.todaktodak_api;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TodaktodakApiApplicationTests {

  @Autowired ApplicationContext context;

  @Test
  void contextLoads() {
    System.out.println("context.getBeanDefinitionCount() = " + context.getBeanDefinitionCount());
    String[] beanNames = context.getBeanDefinitionNames();
    System.out.println("beanNames.length = " + beanNames.length);

    printTodaktodak(beanNames);
    printSpring(beanNames);
  }

  private void printSpring(String[] beanNames) {
    // Spring class
    Arrays.stream(beanNames)
        .sorted()
        .filter(name -> !context.getBean(name).getClass().getName().contains("com.heartsave"))
        .forEach(
            name -> {
              Object bean = context.getBean(name);
              System.out.println("Spring Bean Class = " + bean.getClass());
            });

    // Spring bean count
    long count =
        Arrays.stream(beanNames)
            .sorted()
            .filter(name -> !context.getBean(name).getClass().getName().contains("com.heartsave"))
            .count();
    System.out.println("spring Bean Count = " + count);
  }

  private void printTodaktodak(String[] beanNames) {
    // TodakTdoak class
    Arrays.stream(beanNames)
        .sorted()
        .filter(name -> context.getBean(name).getClass().getName().contains("com.heartsave"))
        .forEach(
            name -> {
              Object bean = context.getBean(name);
              System.out.println("Todaktodak Bean Class = " + bean.getClass());
            });

    // TodakTdoak bean count
    long count =
        Arrays.stream(beanNames)
            .sorted()
            .filter(name -> context.getBean(name).getClass().getName().contains("com.heartsave"))
            .count();
    System.out.println("Todaktodak Bean Count= " + count);
  }
}
