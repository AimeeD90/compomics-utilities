package com.compomics.util.gui.parameters;

import com.compomics.util.gui.renderers.AlignedListCellRenderer;
import com.compomics.util.preferences.ProcessingPreferences;
import java.awt.Dialog;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingConstants;

/**
 * Dialog to edit the processing preferences.
 *
 * @author Marc Vaudel
 */
public class ProcessingPreferencesDialog extends javax.swing.JDialog {

    /**
     * The parent frame.
     */
    private java.awt.Frame parentFrame;
    /**
     * Boolean indicating whether the user canceled the editing.
     */
    private boolean canceled = false;
    /**
     * Boolean indicating whether the processing and identification parameters
     * should be edited upon clicking on OK.
     */
    private boolean editable;

    /**
     * Creates a new ProcessingPreferencesDialog with a frame as owner.
     *
     * @param parentFrame a parent frame
     * @param processingPreferences the processing preferences to display
     * @param editable boolean indicating whether the settings can be edited
     */
    public ProcessingPreferencesDialog(java.awt.Frame parentFrame, ProcessingPreferences processingPreferences, boolean editable) {
        super(parentFrame, true);
        this.parentFrame = parentFrame;
        initComponents();
        setUpGui();
        populateGUI(processingPreferences);
        setLocationRelativeTo(parentFrame);
        setVisible(true);
    }

    /**
     * Creates a new ProcessingPreferencesDialog with a dialog as owner.
     *
     * @param owner the dialog owner
     * @param parentFrame a parent frame
     * @param processingPreferences the processing preferences to display
     * @param editable boolean indicating whether the settings can be edited
     */
    public ProcessingPreferencesDialog(Dialog owner, java.awt.Frame parentFrame, ProcessingPreferences processingPreferences, boolean editable) {
        super(owner, true);
        this.parentFrame = parentFrame;
        initComponents();
        setUpGui();
        populateGUI(processingPreferences);
        setLocationRelativeTo(owner);
        setVisible(true);
    }

    /**
     * Set up the GUI.
     */
    private void setUpGui() {
        processingTypeCmb.setRenderer(new AlignedListCellRenderer(SwingConstants.CENTER));
        processingTypeCmb.setEnabled(editable);
        nThreadsSpinner.setEnabled(editable);
    }

    /**
     * Fills the GUI with the given settings.
     *
     * @param processingPreferences the processing preferences to display
     */
    private void populateGUI(ProcessingPreferences processingPreferences) {
        processingTypeCmb.setSelectedItem(processingPreferences.getProcessingType());
        nThreadsSpinner.setValue(processingPreferences.getnThreads());
    }

    /**
     * Indicates whether the user canceled the editing.
     *
     * @return a boolean indicating whether the user canceled the editing
     */
    public boolean isCanceled() {
        return canceled;
    }

    /**
     * Validates the user input.
     *
     * @return a boolean indicating whether the user input is valid
     */
    public boolean validateInput() {
        return true;
    }

    /**
     * Returns the processing preferences as set by the user.
     *
     * @return the processing preferences as set by the user
     */
    public ProcessingPreferences getProcessingPreferences() {
        ProcessingPreferences processingPreferences = new ProcessingPreferences();
        processingPreferences.setProcessingType((ProcessingPreferences.ProcessingType) processingTypeCmb.getSelectedItem());
        processingPreferences.setnThreads((Integer) nThreadsSpinner.getValue());
        return processingPreferences;
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
        performancePanel = new javax.swing.JPanel();
        performanceLbl = new javax.swing.JLabel();
        nThreadsSpinner = new javax.swing.JSpinner();
        processingTypePanel = new javax.swing.JPanel();
        processingTypeLbl = new javax.swing.JLabel();
        processingTypeCmb = new javax.swing.JComboBox();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        backgroundPanel.setBackground(new java.awt.Color(230, 230, 230));

        performancePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Performance Settings"));
        performancePanel.setOpaque(false);

        performanceLbl.setText("Number of Threads");

        nThreadsSpinner.setRequestFocusEnabled(false);

        javax.swing.GroupLayout performancePanelLayout = new javax.swing.GroupLayout(performancePanel);
        performancePanel.setLayout(performancePanelLayout);
        performancePanelLayout.setHorizontalGroup(
            performancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(performancePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(performanceLbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
                .addComponent(nThreadsSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        performancePanelLayout.setVerticalGroup(
            performancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(performancePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(performancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(performanceLbl)
                    .addComponent(nThreadsSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        processingTypePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Processing Type"));
        processingTypePanel.setOpaque(false);

        processingTypeLbl.setText("Execution");

        processingTypeCmb.setModel(new DefaultComboBoxModel(ProcessingPreferences.ProcessingType.values()));

        javax.swing.GroupLayout processingTypePanelLayout = new javax.swing.GroupLayout(processingTypePanel);
        processingTypePanel.setLayout(processingTypePanelLayout);
        processingTypePanelLayout.setHorizontalGroup(
            processingTypePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(processingTypePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(processingTypeLbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(processingTypeCmb, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        processingTypePanelLayout.setVerticalGroup(
            processingTypePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(processingTypePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(processingTypePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(processingTypeLbl)
                    .addComponent(processingTypeCmb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout backgroundPanelLayout = new javax.swing.GroupLayout(backgroundPanel);
        backgroundPanel.setLayout(backgroundPanelLayout);
        backgroundPanelLayout.setHorizontalGroup(
            backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backgroundPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(processingTypePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(performancePanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backgroundPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton)))
                .addContainerGap())
        );
        backgroundPanelLayout.setVerticalGroup(
            backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backgroundPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(processingTypePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(performancePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        canceled = true;
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        dispose();
    }//GEN-LAST:event_okButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel backgroundPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JSpinner nThreadsSpinner;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel performanceLbl;
    private javax.swing.JPanel performancePanel;
    private javax.swing.JComboBox processingTypeCmb;
    private javax.swing.JLabel processingTypeLbl;
    private javax.swing.JPanel processingTypePanel;
    // End of variables declaration//GEN-END:variables

}
