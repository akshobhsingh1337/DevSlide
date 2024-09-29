package org.group51;

import org.group51.contentitems.ContentItem;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Class works as a datastructure to store slide data
 */
public class Slide implements Serializable {
    private final ArrayList<ContentItem> items;
    private int intialZIndex = 0;
    private Color background;

    /**
     * Constructor
     * has no params to allow for serialiseation
     */
    public Slide() {
        items = new ArrayList<>();
        background = new Color(255, 255, 255);
    }

    /**
     * getter for all content items on a slide
     *
     * @return
     */
    public ArrayList<ContentItem> getItems() {
        return items;
    }

    /**
     * adds a content item to the slide list
     * items include
     * text, video, image, comment, codebox
     *
     * @param item
     */
    public void addItem(ContentItem item) {
        item.setZIndex(intialZIndex);
        intialZIndex++;
        items.add(item);
    }

    /**
     * removes item from content list
     *
     * @param item
     */
    public void deleteItem(ContentItem item) {
        items.remove(item);
    }

    /**
     * getter for the colour of the bg on that slide
     */
    public Color getBackground() {
        return background;
    }

    /**
     * setter for the colour of a bg on that slide
     */
    public void setBackground(Color colour) {
        this.background = colour;
    }

    /**
     * Call the paint method on all the contentitems in a slide to paint them to the screen
     *
     * @param slidePanel JPanel to paint the slide to
     */
    public void paint(JPanel slidePanel) {
        slidePanel.setBackground(background);

        // Sort the items based on Z-index in descending order
        Collections.sort(items, (item1, item2) -> Integer.compare(item2.getZIndex(), item1.getZIndex()));

        // Paint the items after sorting
        for (ContentItem item : items) {
            item.paint(slidePanel);
        }
    }
}
