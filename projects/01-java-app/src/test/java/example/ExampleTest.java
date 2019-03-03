package example;


import org.junit.Test;

public class ExampleTest {

  @Test
  public void test1() {
    App app = new App();
    String g = app.getGreeting();
    assert "Hello world.".equals(g);
  }
}
