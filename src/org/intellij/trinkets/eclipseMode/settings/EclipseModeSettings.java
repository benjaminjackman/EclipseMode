package org.intellij.trinkets.eclipseMode.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;

/**
 * Settings.
 *
 * @author Alexey Efimov
 */
@State(
        name = "EclipseModeSettings",
        storages = {
                @Storage(
                        id = "eclipseMode",
                        file = "$APP_CONFIG$/eclipseMode.xml"
                )}
)
public class EclipseModeSettings implements PersistentStateComponent<EclipseModeSettings> {
    public boolean INCREMENTAL_COMPILATION_ENABLED = true;

    public EclipseModeSettings getState() {
        return this;
    }

    public void loadState(EclipseModeSettings object) {
        XmlSerializerUtil.copyBean(object, this);
    }
}