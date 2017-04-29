package org.intellij.trinkets.eclipseMode.actions;

import org.intellij.trinkets.eclipseMode.EclipseMode;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ex.CheckboxAction;

public class ToggleCompileOnSaveAction extends CheckboxAction {
    public boolean isSelected(AnActionEvent e) {
        EclipseMode eclipseMode = EclipseMode.getInstance();
        return eclipseMode.getSettings().INCREMENTAL_COMPILATION_ENABLED;
    }

    public void setSelected(AnActionEvent e, boolean state) {
        EclipseMode eclipseMode = EclipseMode.getInstance();
        boolean prev = eclipseMode.getSettings().INCREMENTAL_COMPILATION_ENABLED;
        eclipseMode.getSettings().INCREMENTAL_COMPILATION_ENABLED = !prev;
    }
}