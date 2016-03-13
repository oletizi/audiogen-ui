package com.orionletizi.audiogen.ui;

import javafx.scene.input.KeyCode;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit.ApplicationTest;

public abstract class AbstractFXTester extends ApplicationTest {
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
