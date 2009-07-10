package org.intellij.trinkets.eclipseMode.settings;

import javax.swing.*;

/**
 * UI form for {@link org.intellij.trinkets.eclipseMode.settings.EclipseModeConfigurable}.
 *
 * @author Alexey Efimov
 */
public class EclipseModeConfigurableForm {
    private JCheckBox enableIncrementalCompilationOnCheckBox;
    private JCheckBox preventMessagesWindowFromGrabbingFocusOnErrors;
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
    public void setPreventMessagesWindowFromGrabbingFocusOnErrors(boolean value) {
        preventMessagesWindowFromGrabbingFocusOnErrors.setSelected(value);
    }

    public boolean isPreventMessagesWindowFromGrabbingFocusOnErrors() {
        return preventMessagesWindowFromGrabbingFocusOnErrors.isSelected();
    }
}
