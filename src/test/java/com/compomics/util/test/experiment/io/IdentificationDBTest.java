package com.compomics.util.test.experiment.io;

import com.compomics.util.Util;
import com.compomics.util.db.object.ObjectsDB;
import com.compomics.util.experiment.ProjectParameters;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.Advocate;
import com.compomics.util.experiment.identification.spectrum_assumptions.PeptideAssumption;
import com.compomics.util.experiment.identification.SpectrumIdentificationAssumption;
import com.compomics.util.experiment.identification.identifications.Ms2Identification;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.matches.PeptideMatch;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.experiment.massspectrometry.spectra.Spectrum;
import com.compomics.util.experiment.refinementparameters.PepnovoAssumptionDetails;
import junit.framework.Assert;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import junit.framework.TestCase;

/**
 *
 * @author Marc Vaudel
 */
public class IdentificationDBTest extends TestCase {

    public void testDB() throws SQLException, IOException, ClassNotFoundException, SQLException, ClassNotFoundException, InterruptedException {

        String path = this.getClass().getResource("IdentificationDBTest.class").getPath();
        path = path.substring(1, path.indexOf("/target/"));
        path += "/src/test/resources/experiment/identificationDB";
        File dbFolder = new File(path);
        if (!dbFolder.exists()) {
            dbFolder.mkdir();
        }

        try {
            ObjectsDB objectsDB = new ObjectsDB(path, "experimentTestDB.zdb", true);

            Ms2Identification idDB = new Ms2Identification("the reference", objectsDB);
            try {
                String parametersKey = "pepnovo_assumption_details";
                String spectrumFile = "spectrum_file";
                String spectrumTitle = "spectrum_title";
                String projectParametersTitle = "project_parameters_title";
                String spectrumKey = Spectrum.getSpectrumKey(spectrumFile, spectrumTitle);
                String peptideKey = "PEPTIDE";
                String proteinKey = "test_protein";
                Assert.assertTrue(objectsDB.createLongKey(peptideKey) != objectsDB.createLongKey(proteinKey));

                ArrayList<String> testProteins = new ArrayList<>();
                testProteins.add("test protein1");
                testProteins.add("test protein2");

                Peptide peptide = new Peptide(peptideKey, new ArrayList<>());
                SpectrumMatch testSpectrumMatch = new SpectrumMatch(spectrumFile, spectrumTitle);
                testSpectrumMatch.addHit(Advocate.mascot.getIndex(), new PeptideAssumption(peptide, 1, Advocate.mascot.getIndex(), new Charge(Charge.PLUS, 2), 0.1, "no file"), false);
                idDB.addObject(testSpectrumMatch.getKey(), testSpectrumMatch);

                peptide.setParentProteins(testProteins);
                PeptideMatch testPeptideMatch = new PeptideMatch(peptide, peptide.getKey());
                idDB.addObject(testPeptideMatch.getKey(), testPeptideMatch);

                ProteinMatch testProteinMatch = new ProteinMatch(proteinKey);
                idDB.addObject(testProteinMatch.getKey(), testProteinMatch);

                ProjectParameters projectParameters = new ProjectParameters(projectParametersTitle);
                idDB.addObject(ProjectParameters.nameForDatabase, projectParameters);

                idDB.getObjectsDB().dumpToDB();
                idDB.close();

                objectsDB = new ObjectsDB(path, "experimentTestDB.zdb", false);
                idDB = new Ms2Identification("the reference", objectsDB);

                ProjectParameters retrieve = (ProjectParameters) idDB.retrieveObject(ProjectParameters.nameForDatabase);
                Assert.assertTrue(retrieve != null);
                Assert.assertTrue(retrieve.getProjectUniqueName().equals(projectParametersTitle));

                testSpectrumMatch = (SpectrumMatch) idDB.retrieveObject(spectrumKey);
                Assert.assertTrue(testSpectrumMatch.getKey().equals(spectrumKey));

                HashMap<Integer, HashMap<Double, ArrayList<SpectrumIdentificationAssumption>>> assumptionsMap = testSpectrumMatch.getAssumptionsMap();
                HashMap<Double, ArrayList<SpectrumIdentificationAssumption>> mascotAssumptions = assumptionsMap.get(Advocate.mascot.getIndex());
                Assert.assertTrue(mascotAssumptions.size() == 1);
                ArrayList<Double> mascotScores = new ArrayList<>(mascotAssumptions.keySet());
                Assert.assertTrue(mascotScores.size() == 1);
                double bestScore = mascotScores.get(0);
                Assert.assertTrue(bestScore == 0.1);
                ArrayList<SpectrumIdentificationAssumption> bestAssumptions = mascotAssumptions.get(bestScore);
                PeptideAssumption bestAssumption = (PeptideAssumption) bestAssumptions.get(0);
                Peptide bestPeptide = bestAssumption.getPeptide();
                Assert.assertTrue(bestPeptide.getParentProteinsNoRemapping().size() == 2);
                Assert.assertTrue(bestPeptide.getParentProteinsNoRemapping().get(0).equals(testProteins.get(0)));
                Assert.assertTrue(bestPeptide.getParentProteinsNoRemapping().get(1).equals(testProteins.get(1)));
                ArrayList<String> proteins = new ArrayList<>();
                proteins.add(proteinKey);
                bestPeptide.setParentProteins(proteins);

                testSpectrumMatch = (SpectrumMatch) idDB.retrieveObject(spectrumKey);
                assumptionsMap = testSpectrumMatch.getAssumptionsMap();
                mascotAssumptions = assumptionsMap.get(Advocate.mascot.getIndex());
                Assert.assertTrue(mascotAssumptions.size() == 1);
                mascotScores = new ArrayList<>(mascotAssumptions.keySet());
                Assert.assertTrue(mascotScores.size() == 1);
                bestScore = mascotScores.get(0);
                Assert.assertTrue(bestScore == 0.1);
                bestAssumptions = mascotAssumptions.get(bestScore);
                bestAssumption = (PeptideAssumption) bestAssumptions.get(0);
                bestPeptide = bestAssumption.getPeptide();
                Assert.assertTrue(bestPeptide.getParentProteinsNoRemapping().size() == 1);
                Assert.assertTrue(bestPeptide.getParentProteinsNoRemapping().get(0).equals(proteinKey));

                testPeptideMatch = (PeptideMatch) idDB.retrieveObject(peptideKey);
                Assert.assertTrue(testPeptideMatch.getKey().equals(peptideKey));

                testProteinMatch = (ProteinMatch) idDB.retrieveObject(proteinKey);
                Assert.assertTrue(testProteinMatch.getKey().equals(proteinKey));

                double testScore = 12.3;
                PepnovoAssumptionDetails testParameter = new PepnovoAssumptionDetails();
                testParameter.setRankScore(testScore);
                idDB.addObject(parametersKey, testParameter);
                testParameter = (PepnovoAssumptionDetails) idDB.retrieveObject(parametersKey);
                Assert.assertTrue(testParameter.getRankScore() == testScore);
                idDB.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            Util.deleteDir(dbFolder);
        }
    }
}
