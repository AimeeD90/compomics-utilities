package com.compomics.util.experiment.biology.aminoacids;

import com.compomics.util.experiment.biology.AminoAcid;

/**
 * Glu or Gln: Glx (Mascot).
 *
 * @author Harald Barsnes
 */
public class Z extends AminoAcid {

    /**
     * Serial number for backward compatibility.
     */
    static final long serialVersionUID = -7714841171012071337L;

    /**
     * Constructor.
     */
    public Z() {
        singleLetterCode = "Z";
        threeLetterCode = "Glx";
        name = "Glutamine or Glutamic Acid";
        averageMass = 128.6216;
        monoisotopicMass = 128.5505855;
        subAminoAcidsWithoutCombination = new char[]{'Q', 'E'};
        subAminoAcidsWithCombination = subAminoAcidsWithoutCombination;
        aminoAcidCombinations = new char[]{'X'};
        standardGeneticCode = getStandardGeneticCodeForCombination();
    }

    @Override
    public boolean iscombination() {
        return true;
    }

    @Override
    public double getHydrophobicity() {
        throw new UnsupportedOperationException("Not supported for amino acid combinations.");
    }

    @Override
    public double getHelicity() {
        throw new UnsupportedOperationException("Not supported for amino acid combinations.");
    }

    @Override
    public double getBasicity() {
        throw new UnsupportedOperationException("Not supported for amino acid combinations.");
    }

    @Override
    public double getPI() {
        throw new UnsupportedOperationException("Not supported for amino acid combinations.");
    }

    @Override
    public double getPK1() {
        throw new UnsupportedOperationException("Not supported for amino acid combinations.");
    }

    @Override
    public double getPK2() {
        throw new UnsupportedOperationException("Not supported for amino acid combinations.");
    }

    @Override
    public double getPKa() {
        throw new UnsupportedOperationException("Not supported for amino acid combinations.");
    }

    @Override
    public int getVanDerWaalsVolume() {
        throw new UnsupportedOperationException("Not supported for amino acid combinations.");
    }
}
