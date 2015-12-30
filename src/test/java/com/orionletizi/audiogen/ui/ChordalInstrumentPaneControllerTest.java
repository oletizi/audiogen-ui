package com.orionletizi.audiogen.ui;

import com.orionletizi.audiogen.ui.controller.ChordalInstrumentPaneController;
import com.orionletizi.audiogen.ui.proxy.AccordionProxy;
import com.orionletizi.audiogen.ui.proxy.FileChooserProxy;
import com.orionletizi.audiogen.ui.proxy.TextFieldProxy;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.concurrent.Executor;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChordalInstrumentPaneControllerTest {

  @Test
  public void test() throws Exception {

    final URL programURL = ClassLoader.getSystemResource("sfz/guitar3/guitar3.sfz");
    assertNotNull(programURL);

    final Executor exec = command -> {
      command.run();
    };
    //when(exec.submit(any(Runnable.class))).then()

    final FileChooserProxy fileChooserProxy = mock(FileChooserProxy.class);
    when(fileChooserProxy.getFile()).thenReturn(new File(programURL.getFile()));

    final TextFieldProxy instrumentPath = mock(TextFieldProxy.class);

    final AccordionProxy keyStack = mock(AccordionProxy.class);

    final ChordalInstrumentPaneController controller = new ChordalInstrumentPaneController(exec, fileChooserProxy, instrumentPath, keyStack);
    controller.chooseInstrument();

  }

}