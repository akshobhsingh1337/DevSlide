package org.group51;

import javax.swing.*;

/**
 * Class that does the painting of the slide panel, queries the data from the slides and then paints it
 * In addition it holds the source of truth for the current slide index
 */
public class SlideManager {
    private final PresentationFrame presentationFrame;
    private int currentSlideIndex;

    public SlideManager(PresentationFrame frame) {
        this.presentationFrame = frame;
        currentSlideIndex = 0;
    }

    public int getCurrentSlideIndex() {
        return currentSlideIndex;
    }

    public void setCurrentSlideIndex(int index) {
        if (UI.getInstance().getPresentation() == null) {
            throw new RuntimeException("No presentation loaded");
        }

        if (UI.getInstance().getPresentation().getSlideCount() - 1 < index || index < 0) {
            // if we've reached the end of the presentation, lets go back into editing mode
            if (UI.getInstance().isPresentationMode()) {
                UI.getInstance().leavePresentationMode();
                setCurrentSlideIndex(0);
                return;
            }
            System.out.println("index out of range, ignoring request to set slide index");
            return;
        }

        if (currentSlideIndex == index) {
            // don't rerender, cuz nothing has changed
            return;
        }

        currentSlideIndex = index;


        presentationFrame.refreshSlide();
    }

    public void progressToNextSlide() {
        setCurrentSlideIndex(currentSlideIndex + 1);
    }

    public void progressToPreviousSlide() {
        setCurrentSlideIndex(currentSlideIndex - 1);
    }

    /**
     * Rerender the content from the current slide
     *
     * @return JPanel that can be put into a JFrame
     */
    public JPanel paint() {
        Slide currentSlide = UI.getInstance().getPresentation().getSlide(currentSlideIndex);
        if (currentSlide == null) {
            throw new IndexOutOfBoundsException("Something went horribly wrong and the slideindex is out of bounds");
        }

        //create the slide
        JPanel mainBoardPanel = presentationFrame.createPanel("Slide " + currentSlideIndex);
        //remove all layout from the panel to allow for free movement
        mainBoardPanel.setLayout(null);
        mainBoardPanel.setBackground(currentSlide.getBackground());

        // Paint slide content onto mainpanel
        currentSlide.paint(mainBoardPanel);

        // Repaint the main panel once all the content has been painted
        mainBoardPanel.revalidate();
        mainBoardPanel.repaint();

        return mainBoardPanel;
    }
}
