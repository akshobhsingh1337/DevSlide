package org.group51;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BarChartMain extends JFrame implements ActionListener {

    private final JPanel mainPanel = new JPanel();// hold toolbar and chart panel
    private final JPanel toolPanel = new JPanel(); // holds the components for customising bar chart
    private final ChartPanel chartPanel; // for holding the customised chart
    private final DefaultCategoryDataset dataset;
    private final File file = new File("chartImage.png");
    private final JTextField titleText = new JTextField();
    private final JLabel titleTextLabel = new JLabel("Chart Title:");
    private final JTextField categoryAxisTitle = new JTextField();
    private final JLabel categoryAxisL = new JLabel("X-Axis Title");
    private final JTextField valueAxisTitle = new JTextField();
    private final JLabel valueAxisL = new JLabel("Y-Axis Title");
    private final JTextField itemLabelText = new JTextField();
    private final JLabel itemLabel = new JLabel("Item Label:");
    private final JTextField itemCategory = new JTextField();
    private final JLabel itemCategoryLabel = new JLabel("Item Category:");
    private final JTextField itemValue = new JTextField();
    private final JLabel itemValueLabel = new JLabel("Item Value:");
    private final JButton categoryColour = new JButton("Select Category Colour");
    private final JButton addItem = new JButton("Add Item");
    private final JButton deleteItem = new JButton("Delete Item");
    private final JButton clearChart = new JButton("Clear Chart");
    private final JButton setTitleChart = new JButton("Set Title");
    // private JLabel chartCreatedChecker = new JLabel();
    private final JButton saveChart = new JButton("Save Chart");
    private final CategoryPlot plot;
    private final JFreeChart barChart;
    private final List<ColorChangedListener> listeners = new ArrayList<>();
    private String itemCategoryValue;
    private String itemLabelTextValue;
    private BufferedImage chartImage;// holds image of chart
    private Color currentCategoryColour = Color.BLACK;

    /**
     * Executes all nessesary functions for the user to create a barchart
     */
    public BarChartMain(Object obj) {
        this.setTitle("Bar Chart Customiser");
        this.setSize(700, 580);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        this.setContentPane(mainPanel);
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setSize(500, 400);
        GridBagConstraints gbc = new GridBagConstraints();// for constraining size of panels

        // Builds the GUI using various pannels
        JPanel innerToolPanel1 = new JPanel();
        innerToolPanel1.add(titleTextLabel);
        innerToolPanel1.add(titleText);

        JPanel innerToolPanel2 = new JPanel();
        innerToolPanel2.add(categoryAxisL);
        innerToolPanel2.add(categoryAxisTitle);

        JPanel innerToolPanel3 = new JPanel();
        innerToolPanel3.add(valueAxisL);
        innerToolPanel3.add(valueAxisTitle);

        JPanel innerToolPanel4 = new JPanel();
        innerToolPanel4.add(itemLabel);
        innerToolPanel4.add(itemLabelText);

        JPanel innerToolPanel5 = new JPanel();
        innerToolPanel5.add(itemCategoryLabel);
        innerToolPanel5.add(itemCategory);

        JPanel innerToolPanel6 = new JPanel();
        innerToolPanel6.add(itemValueLabel);
        innerToolPanel6.add(itemValue);

        JPanel innerToolPanel7 = new JPanel(new GridLayout(6, 1));
        innerToolPanel7.add(categoryColour);
        innerToolPanel7.add(setTitleChart);
        innerToolPanel7.add(Box.createHorizontalStrut(10));
        innerToolPanel7.add(Box.createVerticalStrut(10));
        innerToolPanel7.add(addItem);
        innerToolPanel7.add(deleteItem);
        innerToolPanel7.add(clearChart);
        innerToolPanel7.add(saveChart);

        toolPanel.setLayout(new BoxLayout(toolPanel, BoxLayout.Y_AXIS));
        toolPanel.add(innerToolPanel1);
        toolPanel.add(innerToolPanel2);
        toolPanel.add(innerToolPanel3);
        toolPanel.add(innerToolPanel4);
        toolPanel.add(innerToolPanel5);
        toolPanel.add(innerToolPanel6);
        toolPanel.add(innerToolPanel7);
        // maybe add jlabel to confirm bar chart is created.updated

        titleText.addActionListener(this);
        categoryAxisTitle.addActionListener(this);
        valueAxisTitle.addActionListener(this);
        itemLabelText.addActionListener(this);
        itemCategory.addActionListener(this);
        itemValue.addActionListener(this);

        categoryColour.addActionListener(this);
        addItem.addActionListener(this);
        deleteItem.addActionListener(this);
        clearChart.addActionListener(this);
        setTitleChart.addActionListener(this);
        saveChart.addActionListener(this);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(toolPanel, gbc);

        /**
         * Allows the window to detect when it is being closed
         */
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to close the Bar Chart Creator",
                        "Back to slides?", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    BarChartMain.this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                } else {
                    BarChartMain.this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                }
            }
        });

        // create dataset
        dataset = new DefaultCategoryDataset();
        // dataset.addValue(10, "Category 1", "Label 1");
        // dataset.addValue(20, "Category 1", "Label 2");
        // dataset.addValue(30, "Category 3", "Sonic");
        // dataset.addValue(90, "Category 2", "Label 1");
        // dataset.addValue(22.2,"","g");

        // create chart
        barChart = ChartFactory.createBarChart(
                "Chart Title",
                "Category Axis Table",
                "Value Axis Table",
                dataset
        );

        plot = (CategoryPlot) barChart.getPlot();

        // reatec Chart Panel
        chartPanel = new ChartPanel(barChart);
        //chartPanel.setPreferredSize(new java.awt.Dimension(2000, 1000));
        chartPanel.setPreferredSize(new java.awt.Dimension(700, 350));

        // add chart Panel
        // mainComp.add(chartPanel);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.9; // Wider width
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(chartPanel, gbc);

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel((LookAndFeel) obj);
                SwingUtilities.updateComponentTreeUI(BarChartMain.this);
            } catch (UnsupportedLookAndFeelException ex) {
                throw new RuntimeException(ex);
            }
        });
        this.pack();
        this.setVisible(true);

        setSelectedColor(currentCategoryColour);

    }

    /**
     * Allows the image to scale correctly
     *
     * @param image
     * @param newWidth
     * @param newHeight
     * @return
     */
    private static BufferedImage resizeImage(BufferedImage image, int newWidth, int newHeight) {
        // Create a new scaled instance of the image
        Image temp = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        // Create a new BufferedImage with the desired dimensions
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        // Draw the scaled image onto the new BufferedImage
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(temp, 0, 0, null);
        g2d.dispose();
        return resizedImage;
    }

    /**
     * for placing a icon on the colour JButton
     *
     * @param main
     * @param width
     * @param height
     * @return
     */
    public static ImageIcon createIcon(Color main, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(main);
        graphics.fillRect(0, 0, width, height);
        graphics.setXORMode(Color.DARK_GRAY);
        graphics.drawRect(0, 0, width - 1, height - 1);
        image.flush();
        ImageIcon icon = new ImageIcon(image);
        return icon;
    }

    /**
     * Colours
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == categoryColour) {
            Color selectedColour = JColorChooser.showDialog(BarChartMain.this, "Select Category Colour",
                    currentCategoryColour);
            setSelectedColor(selectedColour);

            int categoryIndex = -1;
            for (int i = 0; i < dataset.getRowCount(); i++) {
                if (dataset.getRowKey(i).equals(itemCategory.getText())) {
                    categoryIndex = i;
                    break;
                }
            }

            if (categoryIndex != -1) {
                // Set the color of the category
                BarRenderer renderer = (BarRenderer) plot.getRenderer();
                renderer.setSeriesPaint(categoryIndex, selectedColour); // Change Color.RED to desired color

                // Update the chart
                chartPanel.repaint();
            } else {
                // Category not found
                JOptionPane.showMessageDialog(BarChartMain.this, "Category not found.");
            }
        }

        if (e.getSource() == addItem) {
            int intVal = 0;
            boolean intFlag = false;

            double doubleVal = 0;
            boolean doubleFlag = false;

            try {
                intVal = Integer.parseInt(itemValue.getText());
                intFlag = true;
            } catch (Exception e1) {
                // intFlag = false;
                try {
                    doubleVal = Double.parseDouble(itemValue.getText());
                    doubleFlag = true;
                } catch (NumberFormatException e2) {
                }
            }

            if (intFlag) {
                if (itemCategory.getText().equals("") || itemLabelText.getText().equals("")) {
                    JOptionPane.showMessageDialog(this, "Error: Please make item entries are filled");
                } else {
                    dataset.addValue(intVal, itemCategory.getText(), itemLabelText.getText());
                    //save data to attributes to allow for deleteion
                    this.itemCategoryValue = itemCategory.getText();
                    this.itemLabelTextValue = itemLabelText.getText();
                    //clear boxes
                    itemValue.setText("");
                    itemCategory.setText("");
                    itemLabelText.setText("");
                }
            } else if (doubleFlag) {
                if (itemCategory.getText().equals("") || itemLabelText.getText().equals("")) {
                    JOptionPane.showMessageDialog(this, "Error: Please ensure item entries are filled");
                } else {
                    dataset.addValue(doubleVal, itemCategory.getText(), itemLabelText.getText());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Error: Please enter a numerical value in 'Item Value'");
            }
        }
        if (e.getSource() == deleteItem) {
            if (dataset.getValue(itemCategoryValue, itemLabelTextValue) != null) {
                // Item exists, so remove it from the dataset
                dataset.removeValue(itemCategoryValue, itemLabelTextValue);
                JOptionPane.showMessageDialog(this, "Item removed successfully.");
            } else {
                // Item does not exist in the dataset
                JOptionPane.showMessageDialog(this,
                        "Item does not exist in the dataset. Please ensure the item's Category and Label are correct");
            }
        }
        if (e.getSource() == clearChart) {
            dataset.clear();
        }
        if (e.getSource() == setTitleChart) {
            barChart.setTitle(titleText.getText());
            plot.getDomainAxis().setLabel(categoryAxisTitle.getText());
            plot.getRangeAxis().setLabel(valueAxisTitle.getText());
        }
        if (e.getSource() == saveChart) {
            BufferedImage img = new BufferedImage(chartPanel.getWidth(), chartPanel.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = img.createGraphics();
            chartPanel.printAll(g2d);
            g2d.dispose();
            Utils.saveImageAndAddToSlide(img);
        }
    }

    /*
     *  getter for file object
     */
    public File getFile() {
        return file;
    }

    /**
     * Getter for colour select
     *
     * @return
     */
    public Color getSelectedColor() {
        return currentCategoryColour;
    }

    /**
     * setter for colour select
     *
     * @param newCatgeoryColor
     */
    public void setSelectedColor(Color newCatgeoryColor) {
        setSelectedColor(newCatgeoryColor, true);
    }

    /**
     * Variation on colour select with additional params
     *
     * @param newCategoryColour
     * @param notify
     */
    public void setSelectedColor(Color newCategoryColour, boolean notify) {

        if (newCategoryColour == null) {
            return;
        }

        currentCategoryColour = newCategoryColour;
        categoryColour.setIcon(createIcon(currentCategoryColour, 16, 16));
        repaint();

        if (notify) {
            // Notify everybody that may be interested.
            for (ColorChangedListener l : listeners) {
                l.colorChanged(newCategoryColour);
            }
        }
    }

    /**
     * Add listeners to the colour changer
     *
     * @param toAdd
     */
    public void addColorChangedListener(ColorChangedListener toAdd) {
        listeners.add(toAdd);
    }

    public interface ColorChangedListener {
        void colorChanged(Color newCategoryColour);
    }

}
