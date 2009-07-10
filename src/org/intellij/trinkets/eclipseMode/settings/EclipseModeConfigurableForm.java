package org.intellij.trinkets.eclipseMode.settings;

import javax.swing.*;

/**
 * UI form for {@link org.intellij.trinkets.eclipseMode.settings.EclipseModeConfigurable}.
 *
 * @author Alexey Efimov
 */
public class EclipseModeConfigurableForm {
    private JCheckBox enableIncrementalCompilationOnCheckBox;
    private JPanel rootPanel;

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public void setIncrementalCompilationEnabled(boolean value) {
        enableIncrementalCompilationOnCheckBox.setSelected(value);
    }

    public boolean isIncrementalCompilationEnabled() {
        return enableIncrementalCompilationOnCheckBox.isSelected();
    }
}
