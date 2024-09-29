package org.group51;

import org.group51.contentitems.TextItem;

import java.awt.*;
import java.util.ArrayList;

/**
 * Class acts as a data structure for saving presentation data
 */
public class Presentation {
    private final ArrayList<Slide> slides;

    /**
     * Constructor for the class
     */
    public Presentation() {
        slides = new ArrayList<>();
        // Add empty slide, so that we never run into problems with currentSlideIndex being set to 0
        // but not having a slide at that index
        TextItem helloWorld = new TextItem();
        helloWorld.setText("Hello World");

        Slide firstSlide = new Slide();
        firstSlide.addItem(helloWorld);

        addSlide(firstSlide);
    }

    /*
     * Sets all backgrounds a given colour
     */
    public void setAllBackgrounds(Color newColour) {
        for (Slide s : slides) {
            s.setBackground(newColour);
        }
    }

    /**
     * returns the slide at a given index
     *
     * @param index
     * @return
     */
    public Slide getSlide(int index) {
        return slides.get(index);
    }

    /**
     * Adds a slide to the slide list
     *
     * @param slide
     */
    public void addSlide(Slide slide) {
        this.slides.add(slide);
    }

    /**
     * Removes a slide from the slide list using a given index
     *
     * @param index
     * @return
     */
    public Slide removeSlide(int index) {
        return this.slides.remove(index);
    }

    /**
     * returns the whole list of slides
     *
     * @return
     */
    public ArrayList<Slide> getSlides() {
        return slides;
    }

    /**
     * allows for the moving of slides withing the presenation
     *
     * @param originalIndex
     * @param targetIndex
     */
    public void reorderSlides(int originalIndex, int targetIndex) {
        if (targetIndex >= this.slides.size() - 1 || targetIndex <= -1) {
            return;
        }
        Slide s = this.slides.get(originalIndex);
        this.slides.remove(s);
        this.slides.add(targetIndex, s);
        UI.getInstance().callRefresh();
    }

    public int getSlideCount() {
        return slides.size();
    }

    /**
     * Add a new empty slide and return the index
     *
     * @return index of the new slide
     */
    public int addSlide() {
        slides.add(new Slide());
        return slides.size();
    }
}