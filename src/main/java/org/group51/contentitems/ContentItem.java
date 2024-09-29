package org.group51.contentitems;

import javax.swing.*;

/**
 * Defines common behaviors for content items in a UI, such as rendering and
 * Z-index management.
 */
public interface ContentItem {
    /**
     * Renders this item onto a given panel.
     *
     * @param panel The panel to render the item on.
     */
    void paint(JPanel panel);

    /**
     * Gets the item's Z-index.
     *
     * @return The Z-index value.
     */
    int getZIndex();

    /**
     * Sets the item's Z-index.
     *
     * @param zIndex The new Z-index value.
     */
    void setZIndex(int zIndex);
}
