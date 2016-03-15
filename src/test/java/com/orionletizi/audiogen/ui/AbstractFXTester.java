package com.orionletizi.audiogen.ui;

import javafx.scene.input.KeyCode;
import org.junit.After;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit.ApplicationTest;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractFXTester extends ApplicationTest {

  private static final Map<String, String> headlessProps = new HashMap<>();

  static {
    headlessProps.put("testfx.robot", "glass");
    headlessProps.put("testfx.headless", "true");
    headlessProps.put("prism.order", "sw");
    headlessProps.put("prism.text", "t2k");
    headlessProps.put("glass.platform", "Monocle");
    headlessProps.put("monocle.platform", "Headless");
  }

  private Map<String, String> preHeadlessProps = new HashMap<>();

  public void capturePreHeadlessProps() throws Exception {
    preHeadlessProps.clear();
    for (String key : headlessProps.keySet()) {
      final String value = System.getProperty(key);
      System.out.println("Storing pre-headless property: " + key + "=" + value);
      if (value != null) {
        preHeadlessProps.put(key, value);
      }
    }
  }

  @After
  public void restoreHeadlessProps() throws Exception {
    for (Map.Entry<String, String> entry : preHeadlessProps.entrySet()) {
      System.out.println("Restoring pre-headless property: " + entry.getKey() + "=" + entry.getValue());
      System.setProperty(entry.getKey(), entry.getValue());
    }
  }

  protected void headless() {
    for (Map.Entry<String, String> entry : headlessProps.entrySet()) {
      System.setProperty(entry.getKey(), entry.getValue());
      System.out.println("Set headless property: " + entry.getKey() + "=" + System.getProperty(entry.getKey()));
    }
  }

  protected FxRobot typeString(final String string) {
    FxRobot rv = null;
    for (char c : string.toCharArray()) {
      switch (c) {
        case 'a':
          rv = type(KeyCode.A);
          break;
        case 'b':
          rv = type(KeyCode.B);
          break;
        case 'c':
          rv = type(KeyCode.C);
          break;
        case 'd':
          rv = type(KeyCode.D);
          break;
        case 'e':
          rv = type(KeyCode.E);
          break;
        case 'f':
          rv = type(KeyCode.F);
          break;
        case 'g':
          rv = type(KeyCode.G);
          break;
        case 'h':
          rv = type(KeyCode.H);
          break;
        case 'i':
          rv = type(KeyCode.I);
          break;
        case 'j':
          rv = type(KeyCode.J);
          break;
        case 'k':
          rv = type(KeyCode.K);
          break;
        case 'l':
          rv = type(KeyCode.L);
          break;
        case 'm':
          rv = type(KeyCode.M);
          break;
        case 'n':
          rv = type(KeyCode.N);
          break;
        case 'o':
          rv = type(KeyCode.O);
          break;
        case 'p':
          rv = type(KeyCode.P);
          break;
        case 'q':
          rv = type(KeyCode.Q);
          break;
        case 'r':
          rv = type(KeyCode.R);
          break;
        case 's':
          rv = type(KeyCode.S);
          break;
        case 't':
          rv = type(KeyCode.T);
          break;
        case 'u':
          rv = type(KeyCode.U);
          break;
        case 'v':
          rv = type(KeyCode.V);
          break;
        case 'w':
          rv = type(KeyCode.W);
          break;
        case 'x':
          rv = type(KeyCode.X);
          break;
        case 'y':
          rv = type(KeyCode.Y);
          break;
        case 'z':
          rv = type(KeyCode.Z);
          break;
        case '/':
          rv = type(KeyCode.SLASH);
          break;
        default:
          break;
      }
    }
    return rv;
  }
}
