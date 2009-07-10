package org.intellij.trinkets.eclipseMode;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.intellij.trinkets.eclipseMode.incrementalCompiler.SaveListener;
import org.intellij.trinkets.eclipseMode.settings.EclipseModeSettings;
import org.jetbrains.annotations.NotNull;

/**
 * Plugin entry point.
 *
 * @author Alexey Efimov
 */
public class EclipseMode implements ApplicationComponent {
    private final VirtualFileListener saveListener = new SaveListener();

    public static EclipseMode getInstance() {
        return ApplicationManager.getApplication().getComponent(EclipseMode.class);
    }

    public EclipseModeSettings getSettings() {
        return ServiceManager.getService(EclipseModeSettings.class);
    }

    @NotNull
    public String getComponentName() {
        return "EclipseMode";
    }

    public void initComponent() {
        VirtualFileManager.getInstance().addVirtualFileListener(saveListener);
    }

    public void disposeComponent() {
        VirtualFileManager.getInstance().removeVirtualFileListener(saveListener);
    }
}
