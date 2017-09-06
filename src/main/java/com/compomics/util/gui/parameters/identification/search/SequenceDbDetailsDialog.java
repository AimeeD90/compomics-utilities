package com.compomics.util.gui.parameters.identification.search;

import com.compomics.util.Util;
import com.compomics.util.examples.BareBonesBrowserLaunch;
import com.compomics.util.experiment.biology.proteins.Protein;
import com.compomics.util.experiment.biology.taxonomy.SpeciesFactory;
import com.compomics.util.experiment.identification.protein_sequences.ProteinUtils;
import com.compomics.util.experiment.io.biology.protein.FastaParameters;
import com.compomics.util.experiment.io.biology.protein.FastaSummary;
import com.compomics.util.experiment.io.biology.protein.Header;
import com.compomics.util.experiment.io.biology.protein.ProteinDatabase;
import com.compomics.util.experiment.io.biology.protein.ProteinIterator;
import com.compomics.util.experiment.io.biology.protein.iterators.FastaIterator;
import com.compomics.util.gui.JOptionEditorPane;
import com.compomics.util.gui.protein.AdvancedProteinDatabaseDialog;
import com.compomics.util.gui.waiting.waitinghandlers.ProgressDialogX;
import com.compomics.util.io.file.LastSelectedFolder;
import com.compomics.util.parameters.tools.UtilitiesUserParameters;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SpinnerListModel;
import javax.swing.filechooser.FileFilter;

/**
 * This dialog displays information about a sequence database.
 *
 * @author Marc Vaudel
 */
public class SequenceDbDetailsDialog extends javax.swing.JDialog {

    /**
     * A simple progress dialog.
     */
    private static ProgressDialogX progressDialog;
    /**
     * The last selected folder.
     */
    private LastSelectedFolder lastSelectedFolder = null;
    /**
     * boolean indicating whether the db can be changed.
     */
    private boolean dbEditable = true;
    /**
     * The icon to display when waiting.
     */
    private Image waitingImage;
    /**
     * The normal icon.
     */
    private Image normalImange;
    /**
     * The parent frame.
     */
    private Frame parentFrame;
    /**
     * The utilities user parameters.
     */
    private UtilitiesUserParameters utilitiesUserParameters;
    /**
     * The key to use to store FASTA files paths.
     */
    public static final String lastFolderKey = "fastaFile";
    /**
     * The selected fasta file.
     */
    private File selectedFastaFile = null;
    /**
     * The parameters used to parse the fasta file.
     */
    private FastaParameters fastaParameters = null;
    /**
     * Summary information on the fasta file content.
     */
    private FastaSummary fastaSummary = null;
    /**
     * The batch size of proteins to sample.
     */
    public static final int sampleBatchSize = 100;
    /**
     * Accessions of the sampled proteins.
     */
    private ArrayList<String> accessionsSample = new ArrayList<String>(sampleBatchSize);
    /**
     * Sample of proteins from the database.
     */
    private HashMap<String, Protein> proteinsSample = new HashMap<String, Protein>(sampleBatchSize);
    /**
     * Sample of protein headers from the database.
     */
    private HashMap<String, Header> headersSample = new HashMap<String, Header>(sampleBatchSize);
    /**
     * A protein iterator to fill the sample.
     */
    private FastaIterator proteinIterator;

    /**
     * Creates a new SequenceDbDetailsDialog with a dialog as owner.
     *
     * @param owner the dialog owner
     * @param parent the parent frame
     * @param selectedFastaFile the selected fasta file
     * @param fastaParameters the parameters used to parse the fasta file
     * @param fastaSummary summary information on the fasta file content
     * @param lastSelectedFolder the last selected folder
     * @param dbEditable if the database is editable
     * @param normalImange the normal icon
     * @param waitingImage the waiting icon
     */
    public SequenceDbDetailsDialog(Dialog owner, Frame parent, File selectedFastaFile, FastaParameters fastaParameters, FastaSummary fastaSummary, LastSelectedFolder lastSelectedFolder, boolean dbEditable, Image normalImange, Image waitingImage) {

        super(owner, true);
        initComponents();
        this.parentFrame = parent;
        this.lastSelectedFolder = lastSelectedFolder;
        this.dbEditable = dbEditable;
        this.waitingImage = waitingImage;
        this.normalImange = normalImange;

        this.selectedFastaFile = selectedFastaFile;
        this.fastaParameters = fastaParameters;

        this.utilitiesUserParameters = UtilitiesUserParameters.loadUserParameters();

        setUpGUI();
        setLocationRelativeTo(owner);

    }

    /**
     * Creates a new SequenceDbDetailsDialog.
     *
     * @param parent the parent frame
     * @param selectedFastaFile the selected fasta file
     * @param fastaParameters the parameters used to parse the fasta file
     * @param lastSelectedFolder the last selected folder
     * @param fastaSummary summary information on the fasta file content
     * @param dbEditable if the database is editable
     * @param normalImange the normal icon
     * @param waitingImage the waiting icon
     */
    public SequenceDbDetailsDialog(Frame parent, File selectedFastaFile, FastaParameters fastaParameters, FastaSummary fastaSummary, LastSelectedFolder lastSelectedFolder, boolean dbEditable, Image normalImange, Image waitingImage) {

        super(parent, true);

        initComponents();

        this.parentFrame = parent;
        this.lastSelectedFolder = lastSelectedFolder;
        this.dbEditable = dbEditable;
        this.waitingImage = waitingImage;
        this.normalImange = normalImange;

        this.selectedFastaFile = selectedFastaFile;
        this.fastaParameters = fastaParameters;

        this.utilitiesUserParameters = UtilitiesUserParameters.loadUserParameters();

        setUpGUI();
        setLocationRelativeTo(parent);
    }

    /**
     * Set up the GUI.
     */
    private void setUpGUI() {

        if (selectedFastaFile != null) {

            fileTxt.setText(selectedFastaFile.getAbsolutePath());

            dbNameTxt.setText(fastaParameters.getName());

// Show the species present in the database
            speciesJTextField.setText(SpeciesFactory.getSpeciesDescription(fastaSummary.speciesOccurrence));

            // show the database type information
            HashMap<ProteinDatabase, Integer> databaseType = fastaSummary.databaseType;

            if (databaseType.size() == 1) {

                ProteinDatabase proteinDatabase = databaseType.keySet().stream().findFirst().get();
                typeJTextField.setText(proteinDatabase.getFullName());

            } else {

                TreeMap<Integer, TreeSet<ProteinDatabase>> occurrenceToDBMap = databaseType.entrySet().stream()
                        .collect(Collectors.groupingBy(Entry::getValue, TreeMap::new, Collectors.mapping(Entry::getKey, TreeSet::new)));

                String dbOccurrenceText = occurrenceToDBMap.descendingMap().values().stream()
                        .flatMap(dbs -> dbs.stream())
                        .map(db -> db.getFullName() + " (" + databaseType.get(db) + ")")
                        .collect(Collectors.joining(", "));

                typeJTextField.setText(dbOccurrenceText);

            }

            versionTxt.setText(fastaParameters.getVersion());
            lastModifiedTxt.setText(new Date(selectedFastaFile.lastModified()).toString());
            String nSequences = fastaSummary.nSequences + " sequences";

            if (fastaParameters.isTargetDecoy()) {

                nSequences += " (" + fastaSummary.nTarget + " target)";

                decoyFlagTxt.setEditable(true);
                decoyFlagTxt.setText(fastaParameters.getDecoyFlag());

            } else {

                decoyFlagTxt.setText("");
                decoyFlagTxt.setEditable(false);

            }

            sizeTxt.setText(nSequences);

            decoyButton.setEnabled(!fastaParameters.isTargetDecoy() && dbEditable);
            browseButton.setEnabled(dbEditable);
            decoyFlagTxt.setEditable(dbEditable);

            if (selectedFastaFile.exists()) {

                try {

                    proteinIterator = new FastaIterator(selectedFastaFile);
                    bufferProteins();

                    accessionsSpinner.setEnabled(true);

                    updateSequence();

                } catch (Exception e) {

                    JOptionPane.showMessageDialog(this, "An error occurred while reading the fasta file.",
                            "Import error", JOptionPane.WARNING_MESSAGE);
                    e.printStackTrace();

                }

            } else {

                accessionsSpinner.setEnabled(false);

            }
        }
    }

    /**
     * Updates the displayed sequence.
     */
    private void updateSequence() {

        String accession = accessionsSpinner.getValue().toString();
        Header header = headersSample.get(accession);
        Protein protein = proteinsSample.get(accession);

        proteinTxt.setText(header.getRawHeader() + System.getProperty("line.separator") + protein.getSequence());
        proteinTxt.setCaretPosition(0);

        if (fastaParameters.isTargetDecoy()) {
            if (ProteinUtils.isDecoy(accession, fastaParameters)) {
                targetDecoyTxt.setText("(Decoy)");
            } else {
                targetDecoyTxt.setText("(Target)");
            }
        } else {
            targetDecoyTxt.setText("");
        }
    }

    /**
     * Returns the last selected folder.
     *
     * @return the last selected folder
     */
    public String getLastSelectedFolder() {
        if (lastSelectedFolder == null) {
            return null;
        }
        String folder = lastSelectedFolder.getLastSelectedFolder(lastFolderKey);
        if (folder == null) {
            folder = lastSelectedFolder.getLastSelectedFolder();
        }
        return folder;
    }

    /**
     * Allows the user to select a fasta file, loads its information, and
     * returns a boolean indicating whether the process loading was successful.
     *
     * @param userCanDispose if true, the dialog is closed if the user cancels
     * the selection
     *
     * @return a boolean indicating whether a valid fasta file was selected
     */
    public boolean selectDB(boolean userCanDispose) {

        File startLocation = null;

        if (utilitiesUserParameters.getDbFolder() != null && utilitiesUserParameters.getDbFolder().exists()) {

            startLocation = utilitiesUserParameters.getDbFolder();

        }

        if (startLocation == null) {

            startLocation = new File(getLastSelectedFolder());

        }

        JFileChooser fc = new JFileChooser(startLocation);

        FileFilter filter = new FileFilter() {

            @Override
            public boolean accept(File myFile) {

                return myFile.getName().toLowerCase().endsWith("fasta")
                        || myFile.isDirectory();
            }

            @Override
            public String getDescription() {
                return "FASTA (.fasta)";
            }

        };

        fc.setFileFilter(filter);
        int result = fc.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {

            File file = fc.getSelectedFile();
            File folder = file.getParentFile();
            utilitiesUserParameters.setDbFolder(folder);
            lastSelectedFolder.setLastSelectedFolder(lastFolderKey, folder.getAbsolutePath());

            if (file.getName().contains(" ")) {

                file = renameFastaFileName(file);

                if (file == null) {

                    return false;

                }
            }

            loadFastaFile(file);

            return true;

        } else if (userCanDispose) {

            dispose();

        }

        return false;
    }

    /**
     * Loads the given fasta file and updates the GUI.
     *
     * @param fastaFile a fasta file
     */
    private void loadFastaFile(File fastaFile) {

        this.selectedFastaFile = fastaFile;

        progressDialog = new ProgressDialogX(this, parentFrame,
                normalImange,
                waitingImage,
                true);
        progressDialog.setPrimaryProgressCounterIndeterminate(true);

        progressDialog.setTitle("Inferring Database Format. Please Wait...");
        inferParameters();

        if (!progressDialog.isRunCanceled()) {

            progressDialog.setTitle("Getting Summary Data. Please Wait...");
            getSummaryData();

        }

        if (!progressDialog.isRunCanceled()) {

            setUpGUI();

        }

        progressDialog.setRunFinished();

    }

    /**
     * Infers parsing parameters from the selected fasta file.
     */
    private void inferParameters() {

        new Thread(new Runnable() {
            public void run() {
                try {
                    progressDialog.setVisible(true);
                } catch (IndexOutOfBoundsException e) {
                    // ignore
                }
            }
        }, "ProgressDialog").start();

        new Thread("importThread") {
            public void run() {

                try {

                    fastaParameters = FastaParameters.inferParameters(selectedFastaFile, progressDialog);

                } catch (IOException e) {
                    progressDialog.setRunFinished();
                    JOptionPane.showMessageDialog(SequenceDbDetailsDialog.this,
                            "File " + finalFile.getAbsolutePath() + " not found.",
                            "FASTA Import Error", JOptionPane.WARNING_MESSAGE);
                    e.printStackTrace();
                    return;
                } catch (Exception e) {
                    progressDialog.setRunFinished();
                    JOptionPane.showMessageDialog(SequenceDbDetailsDialog.this, JOptionEditorPane.getJOptionEditorPane(
                            "There was an error importing the FASTA file:<br>"
                            + e.getMessage() + "<br>"
                            + "See <a href=\"http://compomics.github.io/projects/searchgui/wiki/databasehelp.html\">DatabaseHelp</a> for help."),
                            "FASTA Import Error", JOptionPane.WARNING_MESSAGE);
                    e.printStackTrace();
                    return;
                }

                if (!progressDialog.isRunCanceled()) {
                    
                    setUpGUI();
                    
                }
                progressDialog.setRunFinished();
            }
        }.start();
    }

    /**
     * Gets summary information on the selected fasta file.
     */
    private void getSummaryData() {

        new Thread(new Runnable() {
            public void run() {
                try {
                    progressDialog.setVisible(true);
                } catch (IndexOutOfBoundsException e) {
                    // ignore
                }
            }
        }, "ProgressDialog").start();

        new Thread("importThread") {
            public void run() {

                try {

                    fastaSummary = FastaSummary.getSummary(selectedFastaFile, fastaParameters, progressDialog);

                } catch (IOException e) {
                    progressDialog.setRunFinished();
                    JOptionPane.showMessageDialog(SequenceDbDetailsDialog.this,
                            "File " + finalFile.getAbsolutePath() + " not found.",
                            "FASTA Import Error", JOptionPane.WARNING_MESSAGE);
                    e.printStackTrace();
                    return;
                } catch (Exception e) {
                    progressDialog.setRunFinished();
                    JOptionPane.showMessageDialog(SequenceDbDetailsDialog.this, JOptionEditorPane.getJOptionEditorPane(
                            "There was an error importing the FASTA file:<br>"
                            + e.getMessage() + "<br>"
                            + "See <a href=\"http://compomics.github.io/projects/searchgui/wiki/databasehelp.html\">DatabaseHelp</a> for help."),
                            "FASTA Import Error", JOptionPane.WARNING_MESSAGE);
                    e.printStackTrace();
                    return;
                }
            }
        }.start();
    }

    /**
     * Appends decoy sequences to the given target database file.
     *
     * @param targetFile the target database file
     * @param progressDialog the progress dialog
     */
    public void generateTargetDecoyDatabase(File targetFile, ProgressDialogX progressDialog) {

        String fastaInput = targetFile.getAbsolutePath();

        // set up the new fasta file name
        String newFasta = fastaInput;

        // remove the ending .fasta (if there)
        if (fastaInput.lastIndexOf(".") != -1) {
            newFasta = fastaInput.substring(0, fastaInput.lastIndexOf("."));
        }

        // add the target decoy tag
        newFasta += utilitiesUserParameters.getTargetDecoyFileNameSuffix() + ".fasta";

        try {
            File newFile = new File(newFasta);
            progressDialog.setTitle("Appending Decoy Sequences. Please Wait...");
            sequenceFactory.appendDecoySequences(newFile, progressDialog);
            sequenceFactory.clearFactory();
            progressDialog.setTitle("Getting Database Details. Please Wait...");
            sequenceFactory.loadFastaFile(newFile, progressDialog);
        } catch (OutOfMemoryError error) {
            Runtime.getRuntime().gc();
            JOptionPane.showMessageDialog(SequenceDbDetailsDialog.this,
                    "The tool used up all the available memory and had to be stopped.\n"
                    + "Memory boundaries are set in the Edit menu (Edit > Java Options).",
                    "Out Of Memory Error",
                    JOptionPane.ERROR_MESSAGE);
            System.out.println("Ran out of memory!");
            error.printStackTrace();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(SequenceDbDetailsDialog.this,
                    new String[]{"FASTA Import Error.", "File " + fastaInput + " not found."},
                    "FASTA Import Error", JOptionPane.WARNING_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(SequenceDbDetailsDialog.this,
                    new String[]{"FASTA Import Error.", "File " + fastaInput + " could not be imported."},
                    "FASTA Import Error", JOptionPane.WARNING_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Copies the content of the FASTA file to a new file and replaces any white
     * space in the file name with '_' instead. Returns the new file, null if an
     * error occurred.
     *
     * @param file the FASTA file to rename
     * @return the renamed FASTA file
     */
    public File renameFastaFileName(File file) {
        
        String tempName = file.getName();
        tempName = tempName.replaceAll(" ", "_");

        File renamedFile = new File(file.getParentFile().getAbsolutePath() + File.separator + tempName);

        boolean success = false;

        try {
            
            success = renamedFile.createNewFile();
            
            if (success) {
                
                Util.copyFile(file, renamedFile);
                
            }
            
        } catch (IOException e) {
            
            JOptionPane.showMessageDialog(this, "An error occurred while renaming the file.",
                    "Please Rename File", JOptionPane.WARNING_MESSAGE);
            e.printStackTrace();
            success = false;
            
        }

        if (success) {
            
            JOptionPane.showMessageDialog(this, "Your FASTA file name contained white space and has been renamed to:\n"
                    + file.getParentFile().getAbsolutePath() + File.separator + tempName, "Renamed File", JOptionPane.WARNING_MESSAGE);
            
            return renamedFile;
            
        }
        
        return null;
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
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        databaseInformationPanel = new javax.swing.JPanel();
        nameLabel = new javax.swing.JLabel();
        dbNameTxt = new javax.swing.JTextField();
        typeLabel = new javax.swing.JLabel();
        fileTxt = new javax.swing.JTextField();
        decoyFlagTxt = new javax.swing.JTextField();
        decoyTagLabel = new javax.swing.JLabel();
        versionLabel = new javax.swing.JLabel();
        versionTxt = new javax.swing.JTextField();
        lastModifiedLabel = new javax.swing.JLabel();
        lastModifiedTxt = new javax.swing.JTextField();
        sizeLabel = new javax.swing.JLabel();
        sizeTxt = new javax.swing.JTextField();
        decoyButton = new javax.swing.JButton();
        browseButton = new javax.swing.JButton();
        fileLabel = new javax.swing.JLabel();
        advancedButton = new javax.swing.JButton();
        typeJTextField = new javax.swing.JTextField();
        speciesJTextField = new javax.swing.JTextField();
        speciesLabel = new javax.swing.JLabel();
        previewPanel = new javax.swing.JPanel();
        proteinYxtScrollPane = new javax.swing.JScrollPane();
        proteinTxt = new javax.swing.JTextArea();
        proteinLabel = new javax.swing.JLabel();
        accessionsSpinner = new javax.swing.JSpinner();
        targetDecoyTxt = new javax.swing.JLabel();
        databaseHelpSettingsJLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Database");
        setMinimumSize(new java.awt.Dimension(500, 500));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        backgroundPanel.setBackground(new java.awt.Color(230, 230, 230));

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

        databaseInformationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Database Details"));
        databaseInformationPanel.setOpaque(false);

        nameLabel.setText("Name");

        dbNameTxt.setEditable(false);
        dbNameTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        typeLabel.setText("Type(s)");

        fileTxt.setEditable(false);
        fileTxt.setHorizontalAlignment(javax.swing.JTextField.LEFT);

        decoyFlagTxt.setEditable(false);
        decoyFlagTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        decoyTagLabel.setText("Decoy Tag");

        versionLabel.setText("Version");

        versionTxt.setEditable(false);
        versionTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        lastModifiedLabel.setText("Modified");

        lastModifiedTxt.setEditable(false);
        lastModifiedTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        sizeLabel.setText("Size");

        sizeTxt.setEditable(false);
        sizeTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        decoyButton.setText("Decoy");
        decoyButton.setPreferredSize(new java.awt.Dimension(75, 25));
        decoyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                decoyButtonActionPerformed(evt);
            }
        });

        browseButton.setText("Browse");
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        fileLabel.setText("File");

        advancedButton.setText("Advanced");
        advancedButton.setPreferredSize(new java.awt.Dimension(90, 25));
        advancedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                advancedButtonActionPerformed(evt);
            }
        });

        typeJTextField.setEditable(false);
        typeJTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        speciesJTextField.setEditable(false);
        speciesJTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        speciesLabel.setText("Species");

        javax.swing.GroupLayout databaseInformationPanelLayout = new javax.swing.GroupLayout(databaseInformationPanel);
        databaseInformationPanel.setLayout(databaseInformationPanelLayout);
        databaseInformationPanelLayout.setHorizontalGroup(
            databaseInformationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(databaseInformationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(databaseInformationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, databaseInformationPanelLayout.createSequentialGroup()
                        .addComponent(fileLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fileTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(browseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(decoyButton, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(advancedButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(databaseInformationPanelLayout.createSequentialGroup()
                        .addComponent(sizeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sizeTxt))
                    .addGroup(databaseInformationPanelLayout.createSequentialGroup()
                        .addComponent(typeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(typeJTextField))
                    .addGroup(databaseInformationPanelLayout.createSequentialGroup()
                        .addComponent(decoyTagLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(decoyFlagTxt))
                    .addGroup(databaseInformationPanelLayout.createSequentialGroup()
                        .addComponent(versionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(versionTxt))
                    .addGroup(databaseInformationPanelLayout.createSequentialGroup()
                        .addComponent(nameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dbNameTxt))
                    .addGroup(databaseInformationPanelLayout.createSequentialGroup()
                        .addComponent(lastModifiedLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lastModifiedTxt))
                    .addGroup(databaseInformationPanelLayout.createSequentialGroup()
                        .addComponent(speciesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(speciesJTextField)))
                .addContainerGap())
        );

        databaseInformationPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {decoyTagLabel, fileLabel, lastModifiedLabel, nameLabel, sizeLabel, typeLabel, versionLabel});

        databaseInformationPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {advancedButton, browseButton, decoyButton});

        databaseInformationPanelLayout.setVerticalGroup(
            databaseInformationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(databaseInformationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(databaseInformationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fileTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(decoyButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseButton)
                    .addComponent(fileLabel)
                    .addComponent(advancedButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(databaseInformationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(dbNameTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(databaseInformationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(speciesLabel)
                    .addComponent(speciesJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(databaseInformationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(typeLabel)
                    .addComponent(typeJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(databaseInformationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(versionTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(versionLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(databaseInformationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(decoyTagLabel)
                    .addComponent(decoyFlagTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(databaseInformationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sizeLabel)
                    .addComponent(sizeTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(databaseInformationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lastModifiedLabel)
                    .addComponent(lastModifiedTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        databaseInformationPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {advancedButton, browseButton, decoyButton});

        previewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Preview"));
        previewPanel.setOpaque(false);

        proteinTxt.setEditable(false);
        proteinTxt.setColumns(20);
        proteinTxt.setLineWrap(true);
        proteinTxt.setRows(5);
        proteinTxt.setWrapStyleWord(true);
        proteinYxtScrollPane.setViewportView(proteinTxt);

        proteinLabel.setText("Protein");

        accessionsSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                accessionsSpinnerStateChanged(evt);
            }
        });

        targetDecoyTxt.setText("(target/decoy)");

        javax.swing.GroupLayout previewPanelLayout = new javax.swing.GroupLayout(previewPanel);
        previewPanel.setLayout(previewPanelLayout);
        previewPanelLayout.setHorizontalGroup(
            previewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(previewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(previewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(proteinYxtScrollPane)
                    .addGroup(previewPanelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(proteinLabel)
                        .addGap(18, 18, 18)
                        .addComponent(accessionsSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(targetDecoyTxt)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        previewPanelLayout.setVerticalGroup(
            previewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(previewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(proteinYxtScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(previewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(proteinLabel)
                    .addComponent(accessionsSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(targetDecoyTxt))
                .addContainerGap())
        );

        databaseHelpSettingsJLabel.setForeground(new java.awt.Color(0, 0, 255));
        databaseHelpSettingsJLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        databaseHelpSettingsJLabel.setText("<html><u><i>Database?</i></u></html>");
        databaseHelpSettingsJLabel.setToolTipText("Open Database Help");
        databaseHelpSettingsJLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                databaseHelpSettingsJLabelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                databaseHelpSettingsJLabelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                databaseHelpSettingsJLabelMouseExited(evt);
            }
        });

        javax.swing.GroupLayout backgroundPanelLayout = new javax.swing.GroupLayout(backgroundPanel);
        backgroundPanel.setLayout(backgroundPanelLayout);
        backgroundPanelLayout.setHorizontalGroup(
            backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backgroundPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(backgroundPanelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(databaseHelpSettingsJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(previewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(databaseInformationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        backgroundPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

        backgroundPanelLayout.setVerticalGroup(
            backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backgroundPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(databaseInformationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(previewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton)
                    .addComponent(databaseHelpSettingsJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
     * Saves changes and closes the dialog
     *
     * @param evt the action event
     */
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        
        UtilitiesUserParameters.saveUserParameters(utilitiesUserParameters);
            dispose();
            
    }//GEN-LAST:event_okButtonActionPerformed

    /**
     * Close the dialog.
     *
     * @param evt the action event
     */
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * Open a file chooser to select a FASTA file.
     *
     * @param evt the action event
     */
    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        selectDB(false);
    }//GEN-LAST:event_browseButtonActionPerformed

    /**
     * Add decoys.
     *
     * @param evt the action event
     */
    private void decoyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_decoyButtonActionPerformed

        progressDialog = new ProgressDialogX(this, parentFrame,
                normalImange,
                waitingImage,
                true);
        progressDialog.setPrimaryProgressCounterIndeterminate(true);
        progressDialog.setTitle("Creating Decoy. Please Wait...");

        new Thread(new Runnable() {
            public void run() {
                try {
                    progressDialog.setVisible(true);
                } catch (IndexOutOfBoundsException e) {
                    // ignore
                }
            }
        }, "ProgressDialog").start();

        new Thread("DecoyThread") {
            public void run() {
                
                generateTargetDecoyDatabase(selectedFastaFile, progressDialog);
                progressDialog.setRunFinished();
                
            }
        }.start();

    }//GEN-LAST:event_decoyButtonActionPerformed

    /**
     * Update the sequence.
     *
     * @param evt the change event
     */
    private void accessionsSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_accessionsSpinnerStateChanged
        updateSequence();
    }//GEN-LAST:event_accessionsSpinnerStateChanged

    /**
     * Open the database help page.
     *
     * @param evt the mouse event
     */
    private void databaseHelpSettingsJLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_databaseHelpSettingsJLabelMouseClicked
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        BareBonesBrowserLaunch.openURL("http://compomics.github.io/projects/searchgui/wiki/databasehelp.html");
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_databaseHelpSettingsJLabelMouseClicked

    /**
     * Change the cursor to a hand cursor.
     *
     * @param evt the mouse event
     */
    private void databaseHelpSettingsJLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_databaseHelpSettingsJLabelMouseEntered
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }//GEN-LAST:event_databaseHelpSettingsJLabelMouseEntered

    /**
     * Change cursor back to the default cursor.
     *
     * @param evt the mouse event
     */
    private void databaseHelpSettingsJLabelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_databaseHelpSettingsJLabelMouseExited
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_databaseHelpSettingsJLabelMouseExited

    /**
     * Show the AdvancedProteinDatabaseDialog.
     *
     * @param evt the action event
     */
    private void advancedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_advancedButtonActionPerformed
        new AdvancedProteinDatabaseDialog(parentFrame);
        utilitiesUserParameters = UtilitiesUserParameters.loadUserParameters();
    }//GEN-LAST:event_advancedButtonActionPerformed

    /**
     * Close the dialog.
     *
     * @param evt the action event
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        cancelButtonActionPerformed(null);
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner accessionsSpinner;
    private javax.swing.JButton advancedButton;
    private javax.swing.JPanel backgroundPanel;
    private javax.swing.JButton browseButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel databaseHelpSettingsJLabel;
    private javax.swing.JPanel databaseInformationPanel;
    private javax.swing.JTextField dbNameTxt;
    private javax.swing.JButton decoyButton;
    private javax.swing.JTextField decoyFlagTxt;
    private javax.swing.JLabel decoyTagLabel;
    private javax.swing.JLabel fileLabel;
    private javax.swing.JTextField fileTxt;
    private javax.swing.JLabel lastModifiedLabel;
    private javax.swing.JTextField lastModifiedTxt;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel previewPanel;
    private javax.swing.JLabel proteinLabel;
    private javax.swing.JTextArea proteinTxt;
    private javax.swing.JScrollPane proteinYxtScrollPane;
    private javax.swing.JLabel sizeLabel;
    private javax.swing.JTextField sizeTxt;
    private javax.swing.JTextField speciesJTextField;
    private javax.swing.JLabel speciesLabel;
    private javax.swing.JLabel targetDecoyTxt;
    private javax.swing.JTextField typeJTextField;
    private javax.swing.JLabel typeLabel;
    private javax.swing.JLabel versionLabel;
    private javax.swing.JTextField versionTxt;
    // End of variables declaration//GEN-END:variables

    /**
     * Buffers proteins sampled from the database.
     *
     * @throws IOException exception thrown if an error occurred while reading
     * the fasta file
     */
    private void bufferProteins() throws IOException {

        int i = 0, previousSize = proteinsSample.size();

        Protein protein;

        while (i < sampleBatchSize && (protein = proteinIterator.getNextProtein()) != null) {

            String accession = protein.getAccession();
            accessionsSample.add(accession);
            proteinsSample.put(accession, protein);
            headersSample.put(accession, proteinIterator.getLastHeader());

        }

        accessionsSpinner.setModel(new SpinnerListModel(accessionsSample));
        accessionsSpinner.setValue(accessionsSample.get(previousSize));

    }
}
