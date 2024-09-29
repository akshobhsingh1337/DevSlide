package org.group51.contentitems;

import org.group51.UI;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Represents a comment item, extending the functionality of a TextItem to be
 * used specifically for comments.
 * This class customizes the appearance and behavior of TextItem to better fit
 * comment use cases.
 */
public class CommentItem extends TextItem {
    /**
     * Initializes a new instance of CommentItem with predefined dimensions.
     */
    public CommentItem() {
        super();
        width = 150;
        height = 35;
    }

    /**
     * Renders the comment item onto a specified JPanel. This implementation
     * modifies the
     * appearance of the text area to distinguish it as a comment. Rendering is
     * skipped
     * in presentation mode to hide comments.
     *
     * @param panel The JPanel on which the comment item is to be rendered.
     */
    @Override
    public void paint(JPanel panel) {
        if (UI.getInstance().isPresentationMode()) {
            return;
        }

        JPanel textAreaSurrounding = new JPanel();
        textAreaSurrounding.setBounds(super.x, y, width, height);

        textAreaSurrounding.setLayout(new FlowLayout(FlowLayout.LEFT));

        JTextArea textArea = new JTextArea();

        // make the background of the boxes transparent such that we dont end up with
        // weird artifacts around the text
        textArea.setOpaque(false);
        textAreaSurrounding.setOpaque(false);

        textAreaSurrounding.setLayout(new FlowLayout(alignment));
        textArea.setText(text);

        if (!UI.getInstance().isPresentationMode()) {
            Border border = BorderFactory.createLineBorder(Color.GREEN, 2);
            textAreaSurrounding.setBorder(border);
        } else {
            textArea.setEditable(false);
            textArea.setFocusable(false);
            textAreaSurrounding.setFocusable(false);
        }

        setUpEventListeners(textArea);
        textAreaSurrounding.add(textArea);

        textArea.setForeground(colour);
        Font textFont = new Font(font, textArea.getFont().getStyle(), fontSize);
        textArea.setFont(textFont);
        addActionListeners(textAreaSurrounding, panel);
        panel.add(textAreaSurrounding);
    }
}
