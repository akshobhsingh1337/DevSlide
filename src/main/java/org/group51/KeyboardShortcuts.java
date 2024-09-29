package org.group51;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * Class to facilitate industry standard shortcuts
 */
public class KeyboardShortcuts {

    public KeyboardShortcuts(JComponent component) {
        /////////////////////////////////////////////////////////////////////////////
        // Create a keyboard shortcut for Ctrl + S
        KeyStroke ctrlSKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK);


        // Create an action for the shortcut
        Action saveAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UI.getInstance().savePresentation();
            }
        };

        // Map the key stroke to the action
        InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(ctrlSKeyStroke, "saveAction");

        // Add the action to the input map
        component.getActionMap().put("saveAction", saveAction);

        /////////////////////////////////////////////////////////////////////////////

        // Create a keyboard shortcut for Ctrl + O (Open)
        KeyStroke ctrlOKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK);

        // Create an action for the Open shortcut
        Action openAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UI.getInstance().openFile();
            }
        };

        // Map the key stroke to the Open action
        inputMap.put(ctrlOKeyStroke, "openAction");

        // Add the Open action to the input map
        component.getActionMap().put("openAction", openAction);
        /////////////////////////////////////////////////////////////////////////////

        // Create a keyboard shortcut for Ctrl + n (New)
        KeyStroke ctrlNKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK);

        // Create an action for the New shortcut
        Action newAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UI.getInstance().openFile();
            }
        };

        // Map the key stroke to the New action
        inputMap.put(ctrlNKeyStroke, "newAction");

        // Add the new action to the input map
        component.getActionMap().put("newAction", newAction);
        /////////////////////////////////////////////////////////////////////////////

        // Create a keyboard shortcut for Ctrl + Shift + S (Save As)
        KeyStroke ctrlShiftSKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK);

        // Create an action for the Save As shortcut
        Action saveAsAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UI.getInstance().savePresentationTo();
            }
        };

        // Map the key stroke to the Save As action
        inputMap.put(ctrlShiftSKeyStroke, "saveAsAction");

        // Add the Save As action to the input map
        component.getActionMap().put("saveAsAction", saveAsAction);

        /////////////////////////////////////////////////////////////////////////////

        // Create a keyboard shortcut for Ctrl + M (New Slide)
        KeyStroke ctrlMKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK);

        // Create an action for the New Slide shortcut
        Action newSlideAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UI.getInstance().getFrame().addSlide();
            }
        };

        // Map the key stroke to the New Slide action
        inputMap.put(ctrlMKeyStroke, "newSlideAction");

        // Add the New Slide action to the input map
        component.getActionMap().put("newSlideAction", newSlideAction);
        /////////////////////////////////////////////////////////////////////////////

        // Create a keyboard shortcut for Ctrl + P (Presentation Mode)
        KeyStroke ctrlPKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK);

        // Create an action for the New Slide shortcut
        Action presentationModeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("ctrl p clicked");
                UI.getInstance().startPresentation();
            }
        };

        // Map the key stroke to the New Slide action
        inputMap.put(ctrlPKeyStroke, "presentationMode");

        // Add the New Slide action to the input map
        component.getActionMap().put("presentationMode", presentationModeAction);
        /////////////////////////////////////////////////////////////////////////////

        // Create a keyboard shortcut for Ctrl + T (New Text Item)
        KeyStroke ctrlTKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_DOWN_MASK);

        // Create an action for the New Text shortcut
        Action newTextAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UI.getInstance().getFrame().addText();
            }
        };

        // Map the key stroke to the New Text action
        inputMap.put(ctrlTKeyStroke, "newTextAction");

        // Add the New Text action to the input map
        component.getActionMap().put("newTextAction", newTextAction);
        /////////////////////////////////////////////////////////////////////////////

        // Create a keyboard shortcut for Ctrl + I (New Image Item)
        KeyStroke ctrlIKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK);

        // Create an action for the New Image shortcut
        Action newImageAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UI.getInstance().getFrame().addImage();
            }
        };

        // Map the key stroke to the New Image action
        inputMap.put(ctrlIKeyStroke, "newImageAction");

        // Add the New Image action to the input map
        component.getActionMap().put("newImageAction", newImageAction);
        /////////////////////////////////////////////////////////////////////////////

    }
}
