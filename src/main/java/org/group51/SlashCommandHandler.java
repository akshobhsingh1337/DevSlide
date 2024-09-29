package org.group51;

import javax.swing.*;
import java.awt.*;

/**
 * Class that provides / command actions to the user
 */
public class SlashCommandHandler {
    private String helpString;

    /**
     * Constructor for the class
     * implements all nessesary functions
     *
     * @param command
     */
    public SlashCommandHandler(String command) {
        if (command == null || command.isEmpty()) {
            return;
        }

        if (command.contains("exit")) {
            System.out.println("Exiting program");
            System.exit(0);
        } else if (command.contains("help")) {
            this.setHelpString();
            JOptionPane.showMessageDialog(UI.getInstance().getFrame(), this.helpString);
        } else if (command.contains("add slide")) {
            UI.getInstance().getFrame().addSlide();
        } else if (command.contains("text")) {
            UI.getInstance().getFrame().addText();
        } else if (command.contains("remove slide")) {
            UI.getInstance().getFrame().removeSlide();
        } else if (command.contains("image")) {
            UI.getInstance().getFrame().addImage();
        } else if (command.contains("format left")) {
            UI.getInstance().getFrame().formatText(FlowLayout.LEFT);
        } else if (command.contains("format right")) {
            UI.getInstance().getFrame().formatText(FlowLayout.RIGHT);
        } else if (command.contains("format centre")) {
            UI.getInstance().getFrame().formatText(FlowLayout.CENTER);
        } else if (command.contains("comment")) {
            UI.getInstance().getFrame().addComment();
        } else if (command.contains("code")) {
            UI.getInstance().getFrame().addCodebox();
        } else if (command.contains("bg")) {
            // Checks if the user had used a colour command
            if (command.contains("red")) {
                setSlideBackground(new Color(255, 0, 0));
            } else if (command.contains("green")) {
                setSlideBackground(new Color(0, 128, 0));
            } else if (command.contains("blue")) {
                setSlideBackground(new Color(0, 0, 255));
            } else if (command.contains("yellow")) {
                setSlideBackground(new Color(255, 255, 0));
            } else if (command.contains("orange")) {
                setSlideBackground(new Color(255, 165, 0));
            } else if (command.contains("purple")) {
                setSlideBackground(new Color(75, 0, 130));
            }
            // If no colour is listed then the colour picker will be brought up
            else {
                UI.getInstance().getFrame().changeBackground();
            }

        }
    }

    /**
     * When the user runs the bg <colour> command then this will set the bg colour
     *
     * @param commandColour
     */
    private void setSlideBackground(Color commandColour) {
        int index = UI.getInstance().getFrame().getRenderer().getCurrentSlideIndex();
        UI.getInstance().getPresentation().getSlide(index).setBackground(commandColour);
        UI.getInstance().callRefresh();
    }

    /**
     * Defines help message
     */
    public void setHelpString() {
        this.helpString = "Slash Commands Help Guide\r\n" + //
                "\r\n" + //
                "exit - this closes the program without saving\r\n" + //
                "\r\n" + //
                "help - this will print a shortened version of this document to remind you of the commands\r\n" + //
                "\r\n" + //
                "add slide - adds a slide to the presentation\r\n" + //
                "\r\n" + //
                "remove slide - removes the currently selected slide from the presentation\r\n" + //
                "\r\n" + //
                "text - adds a text box to the screen\r\n" + //
                "\r\n" + //
                "image - prompts you to select an image to add to the presentation\r\n" + //
                "\r\n" + //
                "format left - formats the currently selected text box to align text to the left\r\n" + //
                "\r\n" + //
                "format right - formats the currently selected text box to align text to the right\r\n" + //
                "\r\n" + //
                "format centre - formats the currently selected text box to align text to the centre\r\n" + //
                "\r\n" + //
                "comment - adds a comment box to the screen\r\n" + //
                "\r\n" + //
                "code - adds a code box to the screen\r\n" + //
                "\r\n" + //
                "bg - brings up the colour selecter \r\n" + //
                "\r\n" + //
                "bg <colour> - sets the bg to the colour you have selected. Supported colours\r\n" + //
                "\tred, green, blue, yellow, orange, purple";
    }
}