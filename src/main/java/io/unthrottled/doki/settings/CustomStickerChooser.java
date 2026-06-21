package io.unthrottled.doki.settings;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.ui.components.fields.ExtendableTextField;
import io.unthrottled.doki.promotions.MessageBundle;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CustomStickerChooser extends DialogWrapper {
  private JPanel contentPane;
  private JTextField textField1;

  public CustomStickerChooser(Project project, String path) {
    super(project, true);

    setTitle(MessageBundle.message("settings.general.content.custom.sticker.modal.title"));
    textField1.setText(path);

    init();
    pack();
  }


  @Override
  protected @Nullable JComponent createCenterPanel() {
    return contentPane;
  }

  private void createUIComponents() {
    ExtendableTextField extendableTextField = new ExtendableTextField();
    extendableTextField.addBrowseExtension(
      () -> {
        FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, false);
        descriptor.setTitle(MessageBundle.message("settings.general.content.custom.sticker.modal.chooser.title"));
        descriptor.setDescription(MessageBundle.message("settings.general.content.custom.sticker.modal.chooser.description"));
        VirtualFile file = FileChooser.chooseFile(descriptor, null, null);
        if (file != null) {
          extendableTextField.setText(file.getPath());
        }
      }, null
    );
    textField1 = extendableTextField;
  }

  public String getPath() {
    return textField1.getText();
  }
}
