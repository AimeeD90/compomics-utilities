package com.compomics.util.experiment.biology.modifications;

/**
 * Enum for the different types of modifications supported.
 *
 * @author Marc Vaudel
 */
public enum ModificationType {

    /**
     * Modification at particular amino acids.
     */
    modaa(0, "Modification at particular amino acids."),
    /**
     * Modification at the N-terminus of a protein.
     */
    modn_protein(1, "Modification at the N-terminus of a protein"),
    /**
     * Modification at the N-terminus of a protein at particular amino acids.
     */
    modnaa_protein(2, "Modification at the N-terminus of a protein at particular amino acids."),
    /**
     * Modification at the C-terminus of a protein.
     */
    modc_protein(3, "Modification at the C-terminus of a protein."),
    /**
     * Modification at the C-terminus of a protein at particular amino acids.
     */
    modcaa_protein(4, "Modification at the C-terminus of a protein at particular amino acids."),
    /**
     * Modification at the N-terminus of a peptide.
     */
    modn_peptide(5, "Modification at the N-terminus of a peptide"),
    /**
     * Modification at the N-terminus of a peptide at particular amino acids.
     */
    modnaa_peptide(6, "Modification at the N-terminus of a peptide at particular amino acids."),
    /**
     * Modification at the C-terminus of a peptide.
     */
    modc_peptide(7, "Modification at the C-terminus of a peptide."),
    /**
     * Modification at the C-terminus of a peptide at particular amino acids.
     */
    modcaa_peptide(8, "Modification at the C-terminus of a peptide at particular amino acids.");

    /**
     * The index of the type, must be the index in the values array.
     */
    public final int index;
    /**
     * The description of the type.
     */
    public final String description;
    
    /**
     * Constructor.
     * 
     * @param index the index of the option
     * @param description the description of the option
     */
    private ModificationType(int index, String description) {
        this.index = index;
        this.description = description;
    }

}
