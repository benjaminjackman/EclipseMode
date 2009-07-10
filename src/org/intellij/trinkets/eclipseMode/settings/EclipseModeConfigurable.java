package org.intellij.trinkets.eclipseMode.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.intellij.trinkets.eclipseMode.EclipseMode;
import org.jetbrains.annotations.Nls;

import javax.swing.*;

/**
 * Configurable settings manager.
 *
 * @author Alexey Efimov
 */
public class EclipseModeConfigurable implements Configurable {
    private EclipseModeConfigurableForm form;

    @Nls
    public String getDisplayName() {
        return "Eclipse Mode";
    }

    public Icon getIcon() {
        return null;
    }

    public String getHelpTopic() {
        return null;
    }

    public JComponent createComponent() {
        if (form == null) {
            form = new EclipseModeConfigurableForm();
        }
        return form.getRootPanel();
    }

    public boolean isModified() {
        if (form != null) {
            EclipseModeSettings settings = EclipseMode.getInstance().getSettings();
            return settings.INCREMENTAL_COMPILATION_ENABLED != form.isIncrementalCompilationEnabled();
        }
        return false;
    }

    public void apply() throws ConfigurationException {
        if (form != null) {
            EclipseModeSettings settings = EclipseMode.getInstance().getSettings();
            settings.INCREMENTAL_COMPILATION_ENABLED = form.isIncrementalCompilationEnabled();
        }
    }

    public void reset() {
        if (form != null) {
            EclipseModeSettings settings = EclipseMode.getInstance().getSettings();
            form.setIncrementalCompilationEnabled(settings.INCREMENTAL_COMPILATION_ENABLED);
        }
    }

    public void disposeUIResources() {
    }
}
