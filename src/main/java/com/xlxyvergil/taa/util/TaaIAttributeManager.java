package com.xlxyvergil.taa.util;

import org.jetbrains.annotations.ApiStatus;

/**
 * A manager for handling TACZ attribute logic within Minecraft.
 */
@ApiStatus.Internal
public interface TaaIAttributeManager {

    /**
     * {@return whether the attributes are being updated, instead of added or removed}
     */
    boolean taaAreAttributesUpdating();

    /**
     * Sets whether the attributes are being updated, instead of added or removed.
     *
     * @param updating whether the attributes are being updated, instead of added or removed
     */
    void taaSetAttributesUpdating(boolean updating);
}