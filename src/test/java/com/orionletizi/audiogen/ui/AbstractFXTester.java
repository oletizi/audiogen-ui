package com.orionletizi.audiogen.ui;

import com.orionletizi.audiogen.io.DataStore;
import com.orionletizi.audiogen.io.DefaultFileTool;
import com.orionletizi.audiogen.io.JacksonSerializer;
import com.orionletizi.audiogen.midi.JavaMidiSystem;
import com.orionletizi.audiogen.ui.controller.AbstractController;
import com.orionletizi.audiogen.ui.view.AlertFactory;
import com.orionletizi.audiogen.ui.view.DChooser;
import com.orionletizi.audiogen.ui.view.FChooser;
import com.orionletizi.audiogen.ui.view.IAlert;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit.ApplicationTest;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.orionletizi.audiogen.ui.controller.AbstractController.dataStore;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.mock;

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

  protected DChooser dchooser;
  protected FXMLLoader loader;

  private Map<String, String> preHeadlessProps = new HashMap<>();
  private FChooser fchooser;
  private IAlert mockAlert;

  @Rule
  public TemporaryFolder tmp = new TemporaryFolder();
  protected File localLib;

  @Before
  public void before() throws Exception {
    localLib = tmp.newFolder();
    dataStore = new DataStore(new JavaMidiSystem(), new JacksonSerializer(), localLib, new DefaultFileTool());
    System.out.println("Setting up mocks...");
    setupMocks();
  }

  public DataStore getDataStore() {
    return dataStore;
  }

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
        case '0':
          rv = type(KeyCode.DIGIT0);
          break;
        case '1':
          rv = type(KeyCode.DIGIT1);
          break;
        case '2':
          rv = type(KeyCode.DIGIT2);
          break;
        case '3':
          rv = type(KeyCode.DIGIT3);
          break;
        case '4':
          rv = type(KeyCode.DIGIT4);
          break;
        case '5':
          rv = type(KeyCode.DIGIT5);
          break;
        case '6':
          rv = type(KeyCode.DIGIT6);
          break;
        case '7':
          rv = type(KeyCode.DIGIT7);
          break;
        case '8':
          rv = type(KeyCode.DIGIT8);
          break;
        case '9':
          rv = type(KeyCode.DIGIT9);
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

  protected void setupMocks() {
    // Set up the mock infrastructure
    fchooser = mock(FChooser.class);
    dchooser = mock(DChooser.class);
    mockAlert = mock(IAlert.class);
    final AlertFactory alertFactory = type -> mockAlert;
    AbstractController.dataStore = dataStore;
    AbstractController.fchooser = fchooser;
    AbstractController.dchooser = dchooser;
    AbstractController.alertFactory = alertFactory;
  }

  public void start(final Stage stage) throws Exception {
    final URL fxmlUrl = ClassLoader.getSystemResource(getFxmlPath());
    assertNotNull(fxmlUrl);
    loader = new FXMLLoader(fxmlUrl);
    final Parent root = loader.load();
    Scene scene = new Scene(root);
    stage.setTitle("Audiogen UI Java FX Test");
    stage.setScene(scene);
    stage.show();
  }

  protected abstract String getFxmlPath();
}
