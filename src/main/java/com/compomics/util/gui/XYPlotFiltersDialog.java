package com.compomics.util.gui;

import java.util.HashMap;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;

/**
 * Dialog for editing the data filters for an XYPlottingDialog.
 * 
 * @author Harald Barsnes
 */
public class XYPlotFiltersDialog extends javax.swing.JDialog {

    /**
     * The XYPlottingDialog parent.
     */
    private XYPlottingDialog xyPlottingDialog;

    /**
     * Creates a new XYPlotFiltersDialog.
     *
     * @param xyPlottingDialog the XYPlottingDialog parent
     * @param modal if the dialog is to be modal or not
     */
    public XYPlotFiltersDialog(XYPlottingDialog xyPlottingDialog, boolean modal) {
        super(xyPlottingDialog, modal);
        initComponents();

        this.xyPlottingDialog = xyPlottingDialog;

        setUpGUI();
        insertData();

        setLocationRelativeTo(xyPlottingDialog);
        setVisible(true);
    }

    /**
     * Set up the GUI.
     */
    private void setUpGUI() {
        filtersTable.getTableHeader().setReorderingAllowed(false);

        JPanel proteinCorner = new JPanel();
        proteinCorner.setBackground(filtersTable.getTableHeader().getBackground());
        filtersScrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, proteinCorner);

        filtersScrollPane.getViewport().setOpaque(false);

        filtersTable.getColumn(" ").setMaxWidth(50);
        filtersTable.getColumn(" ").setMinWidth(50);
    }

    /**
     * Insert the data.
     */
    private void insertData() {

        Vector<String> columnNames = xyPlottingDialog.getColummnNames();
        HashMap<String, String> dataFiltersInclude = xyPlottingDialog.getDataFilters();

        for (String columnName : columnNames) {
            ((DefaultTableModel) filtersTable.getModel()).addRow(new Object[]{
                        filtersTable.getRowCount() + 1,
                        columnName,
                        dataFiltersInclude.get(columnName)
                    });
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        backgroundPanel = new javax.swing.JPanel();
        filtersScrollPane = new javax.swing.JScrollPane();
        filtersTable = new javax.swing.JTable();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        filterTypesLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Data Filters");

        backgroundPanel.setBackground(new java.awt.Color(230, 230, 230));

        filtersTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                " ", "Column", "Include"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        filtersScrollPane.setViewportView(filtersTable);

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        filterTypesLabel.setFont(filterTypesLabel.getFont().deriveFont((filterTypesLabel.getFont().getStyle() | java.awt.Font.ITALIC)));
        filterTypesLabel.setText("Supported filter types: >number, =number, <number, =text.");

        javax.swing.GroupLayout backgroundPanelLayout = new javax.swing.GroupLayout(backgroundPanel);
        backgroundPanel.setLayout(backgroundPanelLayout);
        backgroundPanelLayout.setHorizontalGroup(
            backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backgroundPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filtersScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 628, Short.MAX_VALUE)
                    .addGroup(backgroundPanelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(filterTypesLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(okButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton)))
                .addContainerGap())
        );

        backgroundPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

        backgroundPanelLayout.setVerticalGroup(
            backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backgroundPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(filtersScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton)
                    .addComponent(filterTypesLabel))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backgroundPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backgroundPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Save the filters and close the dialog.
     *
     * @param evt
     */
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed

        HashMap<String, String> dataFiltersInclude = new HashMap<>();

        for (int i = 0; i < filtersTable.getRowCount(); i++) {

            String dataFilter = null;

            if (filtersTable.getValueAt(i, 2) != null) {

                dataFilter = ((String) filtersTable.getValueAt(i, 2)).trim();

                if (dataFilter.length() > 0) {
                    if (dataFilter.startsWith(">") || dataFilter.startsWith("=") || dataFilter.startsWith("<")) {
                        if (dataFilter.length() == 1) {
                            JOptionPane.showMessageDialog(this, "One of the filters \'" + dataFilter + "\' seems to be empty.", "Filter Error", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "The filters have to start with >,= or <.", "Filter Error", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }
            }

            dataFiltersInclude.put((String) filtersTable.getValueAt(i, 1), dataFilter);
        }

        xyPlottingDialog.setDataFilters(dataFiltersInclude);
        xyPlottingDialog.updatePlot();

        dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    /**
     * Close the dialog.
     *
     * @param evt
     */
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel backgroundPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel filterTypesLabel;
    private javax.swing.JScrollPane filtersScrollPane;
    private javax.swing.JTable filtersTable;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables
}
