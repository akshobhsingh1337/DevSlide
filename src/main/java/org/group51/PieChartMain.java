package org.group51;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class allows user to insert pie charts into the presenation
 */
public class PieChartMain extends JFrame implements ActionListener {
    private final JPanel mainPanel = new JPanel();// hold toolbar and chart panel
    private final JPanel toolPanel = new JPanel(); // holds the components for customising pie chart
    private final ChartPanel chartPanel; // for holding the customised chart
    private final DefaultPieDataset dataset;
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
    private final PiePlot plot;
    private final JFreeChart pieChart;
    private final List<ColorChangedListener> listeners = new ArrayList<>();
    private String itemCategoryValue;
    private BufferedImage chartImage;// holds image of chart
    private Color currentCategoryColour = Color.BLACK;

    /**
     * Class that allows the user to generate pie charts
     *
     * @param obj
     */
    public PieChartMain(Object obj) {
        this.setTitle("Pie Chart Customiser");
        this.setSize(700, 580);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        this.setContentPane(mainPanel);
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();// for constraining size of panels

        //Adds several panels to the UI to facilitate better UX
        JPanel innerToolPanel1 = new JPanel();
        innerToolPanel1.add(titleTextLabel);
        innerToolPanel1.add(titleText);

        JPanel innerToolPanel5 = new JPanel();
        innerToolPanel5.add(itemCategoryLabel);
        innerToolPanel5.add(itemCategory);

        JPanel innerToolPanel6 = new JPanel();
        innerToolPanel6.add(itemValueLabel);
        innerToolPanel6.add(itemValue);

        JPanel innerToolPanel7 = new JPanel(new GridLayout(6, 1));
        innerToolPanel7.add(categoryColour);
        innerToolPanel7.add(setTitleChart);
        innerToolPanel7.add(Box.createHorizontalStrut(10)); // provide separation of elements for better UX
        innerToolPanel7.add(Box.createVerticalStrut(10));
        innerToolPanel7.add(addItem);
        innerToolPanel7.add(deleteItem);
        innerToolPanel7.add(clearChart);
        innerToolPanel7.add(saveChart);

        toolPanel.setLayout(new BoxLayout(toolPanel, BoxLayout.Y_AXIS));
        toolPanel.add(innerToolPanel1);
        // toolPanel.add(innerToolPanel2);
        // toolPanel.add(innerToolPanel3);
        // toolPanel.add(innerToolPanel4);
        toolPanel.add(innerToolPanel5);
        toolPanel.add(innerToolPanel6);
        toolPanel.add(innerToolPanel7);
        // maybe add jlabel to confirm pie chart is created.updated

        titleText.addActionListener(this);
        // categoryAxisTitle.addActionListener(this);
        // valueAxisTitle.addActionListener(this);
        // itemLabelText.addActionListener(this);
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
         * Allows the window to detect when it is being closed to alter the user
         */
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to close the Pie Chart Creator",
                        "Back to slides?", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    PieChartMain.this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                } else {
                    PieChartMain.this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            }
        });

        // create dataset
        dataset = new DefaultPieDataset();

        // create chart
        pieChart = ChartFactory.createPieChart(
                "Chart Title",
                dataset,
                true,
                true,
                false);

        plot = (PiePlot) pieChart.getPlot();

        HashMap<String, Color> pieCategoryColour = new HashMap<>();

        // reatec Chart Panel
        chartPanel = new ChartPanel(pieChart);
        chartPanel.setPreferredSize(new Dimension(550, 375));

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
                SwingUtilities.updateComponentTreeUI(PieChartMain.this);
            } catch (UnsupportedLookAndFeelException ex) {
                throw new RuntimeException(ex);
            }
        });
        this.pack();
        this.setVisible(true);

        setSelectedColor(currentCategoryColour);

    }


    /**
     * for placing an icon on the colour JButton
     *
     * @param main
     * @param width
     * @param height
     * @return
     */
    public static ImageIcon createIcon(Color main, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(main);
        graphics.fillRect(0, 0, width, height);
        graphics.setXORMode(Color.DARK_GRAY);
        graphics.drawRect(0, 0, width - 1, height - 1);
        image.flush();
        return new ImageIcon(image);
    }

    /**
     * Colours
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == categoryColour) {
            Color selectedColour = JColorChooser.showDialog(PieChartMain.this, "Select Category Colour",
                    currentCategoryColour);
            setSelectedColor(selectedColour);

            try {
                plot.setSectionPaint(itemCategory.getText(), selectedColour);

                // Update the chart
                chartPanel.repaint();
            } catch (Exception exc) {
                JOptionPane.showMessageDialog(PieChartMain.this, "Category not found.");
            }

        }

        if (e.getSource() == addItem) {
            int intVal = 0;
            boolean intFlag = false;
            System.out.println("add button was clicked");

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
                if (itemCategory.getText().equals("")) {
                    JOptionPane.showMessageDialog(this, "Error: Please make item entries are filled");
                } else {
                    dataset.setValue(itemCategory.getText(), intVal);
                    //save data
                    this.itemCategoryValue = itemCategory.getText();
                    //clear boxes
                    itemValue.setText("");
                    itemCategory.setText("");
                }
            } else if (doubleFlag) {
                if (itemCategory.getText().equals("")) {
                    JOptionPane.showMessageDialog(this, "Error: Please ensure item entries are filled");
                } else {
                    dataset.setValue(itemCategory.getText(), doubleVal);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Error: Please enter a numerical value in 'Item Value'");
            }
        }
        if (e.getSource() == deleteItem) {
            if (dataset.getValue(itemCategoryValue) != null) {
                // Item exists, so remove it from the dataset
                dataset.remove(itemCategoryValue);
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
            pieChart.setTitle(titleText.getText());
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

    /**
     * getter for file type
     *
     * @return
     */
    public File getFile() {
        return file;
    }

    /**
     * getter for selected colour
     *
     * @return
     */
    public Color getSelectedColor() {
        return currentCategoryColour;
    }

    /**
     * setter for the new category section
     * Utilsed by the colour picker UI element
     *
     * @param newCatgeoryColor
     */
    public void setSelectedColor(Color newCatgeoryColor) {
        setSelectedColor(newCatgeoryColor, true);
    }

    /**
     * Alterntive way to set colour
     * Allows the listeners to be pinged
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

    public interface ColorChangedListener {
        void colorChanged(Color newCategoryColour);
    }

}
