/*
 * Copyright 2006-2008 The MZmine Development Team
 * 
 * This file is part of MZmine.
 * 
 * MZmine is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package net.sf.mzmine.modules.peakpicking.deisotoper;

import java.util.Arrays;
import java.util.Vector;

import net.sf.mzmine.data.Peak;
import net.sf.mzmine.data.PeakList;
import net.sf.mzmine.data.impl.SimpleIsotopePattern;
import net.sf.mzmine.data.impl.SimplePeakList;
import net.sf.mzmine.data.impl.SimplePeakListRow;
import net.sf.mzmine.io.RawDataFile;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.project.MZmineProject;
import net.sf.mzmine.taskcontrol.Task;

/**
 * 
 */
class IsotopeGrouperTask implements Task {

    /**
     * The isotopeDistance constant defines expected distance between isotopes.
     * Actual weight of 1 neutron is 1.008665 Da, but part of this mass is
     * consumed as binding energy to other protons/neutrons. Actual mass
     * increase of isotopes depends on chemical formula of the molecule. Since
     * we don't know the formula, we can assume the distance to be ~1 Da, with
     * user-defined tolerance.
     */
    private static final float isotopeDistance = 1f;

    private PeakList peaklist;

    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;

    // peaks counter
    private int processedPeaks, totalPeaks;

    // parameter values
    private String suffix;
    private float mzTolerance, rtTolerance;
    private boolean monotonicShape, removeOriginal, chooseMostIntense;
    private int maximumCharge;

    /**
     * @param rawDataFile
     * @param parameters
     */
    IsotopeGrouperTask(PeakList peaklist, IsotopeGrouperParameters parameters) {

        this.peaklist = peaklist;

        // Get parameter values for easier use
        suffix = (String) parameters.getParameterValue(IsotopeGrouperParameters.suffix);
        mzTolerance = (Float) parameters.getParameterValue(IsotopeGrouperParameters.mzTolerance);
        rtTolerance = (Float) parameters.getParameterValue(IsotopeGrouperParameters.rtTolerance);
        monotonicShape = (Boolean) parameters.getParameterValue(IsotopeGrouperParameters.monotonicShape);
        maximumCharge = (Integer) parameters.getParameterValue(IsotopeGrouperParameters.maximumCharge);
        chooseMostIntense = (parameters.getParameterValue(IsotopeGrouperParameters.representativeIsotope) == IsotopeGrouperParameters.ChooseTopIntensity);
        removeOriginal = (Boolean) parameters.getParameterValue(IsotopeGrouperParameters.autoRemove);

    }

    /**
     * @see net.sf.mzmine.taskcontrol.Task#getTaskDescription()
     */
    public String getTaskDescription() {
        return "Isotopic peaks grouper on " + peaklist;
    }

    /**
     * @see net.sf.mzmine.taskcontrol.Task#getFinishedPercentage()
     */
    public float getFinishedPercentage() {
        if (totalPeaks == 0)
            return 0.0f;
        return (float) processedPeaks / (float) totalPeaks;
    }

    /**
     * @see net.sf.mzmine.taskcontrol.Task#getStatus()
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * @see net.sf.mzmine.taskcontrol.Task#getErrorMessage()
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    public PeakList getPeakList() {
        return peaklist;
    }

    /**
     * @see net.sf.mzmine.taskcontrol.Task#cancel()
     */
    public void cancel() {
        status = TaskStatus.CANCELED;
    }

    /**
     * @see Runnable#run()
     */
    public void run() {

        status = TaskStatus.PROCESSING;

        // We assume source peaklist contains one datafile
        RawDataFile dataFile = peaklist.getRawDataFile(0);

        // Create new deisotoped peaklist
        SimplePeakList deisotopedPeakList = new SimplePeakList(peaklist + " "
                + suffix);

        // Collect all selected charge states
        int charges[] = new int[maximumCharge];
        for (int i = 0; i < maximumCharge; i++)
            charges[i] = (i + 1);

        // Sort peaks
        Peak[] sortedPeaks = peaklist.getPeaks(dataFile);
        Arrays.sort(sortedPeaks, new PeakSorterByDescendingHeight());

        // Loop through all peaks in the order of descending intensity
        totalPeaks = sortedPeaks.length;

        for (int ind = 0; ind < totalPeaks; ind++) {

            if (status == TaskStatus.CANCELED)
                return;

            Peak aPeak = sortedPeaks[ind];

            // Check if peak was already deleted
            if (aPeak == null) {
                processedPeaks++;
                continue;
            }

            // Check which charge state fits best around this peak
            int bestFitCharge = 0;
            int bestFitScore = -1;
            Vector<Peak> bestFitPeaks = null;
            for (int charge : charges) {

                Vector<Peak> fittedPeaks = new Vector<Peak>();
                fittedPeaks.add(aPeak);
                fitPattern(fittedPeaks, aPeak, charge, sortedPeaks);

                int score = fittedPeaks.size();
                if ((score > bestFitScore)
                        || ((score == bestFitScore) && (bestFitCharge > charge))) {
                    bestFitScore = score;
                    bestFitCharge = charge;
                    bestFitPeaks = fittedPeaks;
                }

            }

            // Assign peaks in best fitted pattern to same isotope pattern
            SimpleIsotopePattern isotopePattern = new SimpleIsotopePattern();
            isotopePattern.setCharge(bestFitCharge);

            float maxHeight = 0, minMZ = Float.MAX_VALUE;

            for (Peak p : bestFitPeaks) {
                isotopePattern.addPeak(p);
                if (p.getHeight() > maxHeight) {
                    if (chooseMostIntense)
                        isotopePattern.setRepresentativePeak(p);
                    maxHeight = p.getHeight();
                }
                if (p.getMZ() < minMZ) {
                    if (!chooseMostIntense)
                        isotopePattern.setRepresentativePeak(p);
                    minMZ = p.getMZ();
                }
            }

            // keep old ID
            int oldID = peaklist.getPeakRow(
                    isotopePattern.getRepresentativePeak()).getID();
            SimplePeakListRow newRow = new SimplePeakListRow(oldID);
            newRow.addPeak(dataFile, isotopePattern, isotopePattern);

            deisotopedPeakList.addRow(newRow);

            // remove all peaks already assigned to isotope pattern
            for (int i = 0; i < sortedPeaks.length; i++) {
                if (bestFitPeaks.contains(sortedPeaks[i]))
                    sortedPeaks[i] = null;
            }

            // Update completion rate
            processedPeaks++;

        }

        // Add new peaklist to the project
        MZmineProject currentProject = MZmineCore.getCurrentProject();
        currentProject.addPeakList(deisotopedPeakList);

        // Remove the original peaklist if requested
        if (removeOriginal)
            currentProject.removePeakList(peaklist);

        status = TaskStatus.FINISHED;

    }

    
    /**
     * Fits isotope pattern around one peak.
     * 
     * @param p Pattern is fitted around this peak
     * @param charge Charge state of the fitted pattern
     */
    private void fitPattern(Vector<Peak> fittedPeaks, Peak p, int charge,
            Peak[] sortedPeaks) {

        if (charge == 0) {
            return;
        }

        // Search for peaks before the start peak
        if (!monotonicShape) {
            fitHalfPattern(p, charge, -1, fittedPeaks, sortedPeaks);
        }

        // Search for peaks after the start peak
        fitHalfPattern(p, charge, 1, fittedPeaks, sortedPeaks);

    }

    /**
     * Helper method for fitPattern. Fits only one half of the pattern.
     * 
     * @param p Pattern is fitted around this peak
     * @param charge Charge state of the fitted pattern
     * @param direction Defines which half to fit: -1=fit to peaks before start
     *            M/Z, +1=fit to peaks after start M/Z
     * @param fittedPeaks All matching peaks will be added to this set
     */
    private void fitHalfPattern(Peak p, int charge, int direction,
            Vector<Peak> fittedPeaks, Peak[] sortedPeaks) {

        // Use M/Z and RT of the strongest peak of the pattern (peak 'p')
        float mainMZ = p.getMZ();
        float mainRT = p.getRT();

        // Variable n is the number of peak we are currently searching. 1=first
        // peak before/after start peak, 2=peak before/after previous, 3=...
        boolean followingPeakFound;
        int n = 1;
        do {

            // Assume we don't find match for n:th peak in the pattern (which
            // will end the loop)
            followingPeakFound = false;

            // Loop through all peaks, and collect candidates for the n:th peak
            // in the pattern
            Vector<Peak> goodCandidates = new Vector<Peak>();
            for (int ind = 0; ind < sortedPeaks.length; ind++) {

                Peak candidatePeak = sortedPeaks[ind];

                if (candidatePeak == null)
                    continue;

                // Get properties of the candidate peak
                float candidatePeakMZ = candidatePeak.getMZ();
                float candidatePeakRT = candidatePeak.getRT();

                // Does this peak fill all requirements of a candidate?
                // - within tolerances from the expected location (M/Z and RT)
                // - not already a fitted peak (only necessary to avoid
                // conflicts when parameters are set too wide)

                if ((Math.abs((candidatePeakMZ - isotopeDistance * direction
                        * n / (float) charge)
                        - mainMZ) <= mzTolerance)
                        && (Math.abs(candidatePeakRT - mainRT) < rtTolerance)
                        && (!fittedPeaks.contains(candidatePeak))) {
                    goodCandidates.add(candidatePeak);

                }

            }

            // If there are some candidates for n:th peak, then select the one
            // with biggest intensity
            // We collect all candidates, because we might want to do something
            // more sophisticated at this step. For example, we might want to
            // remove all other candidates. However, currently nothing is done
            // with other candidates.
            Peak bestCandidate = null;
            for (Peak candidatePeak : goodCandidates) {
                if (bestCandidate != null) {
                    if (bestCandidate.getHeight() < candidatePeak.getHeight()) {
                        bestCandidate = candidatePeak;
                    }
                } else {
                    bestCandidate = candidatePeak;
                }

            }

            // If best candidate was found, then assign it to this isotope
            // pattern
            if (bestCandidate != null) {

                // Add best candidate to fitted peaks of the pattern
                fittedPeaks.add(bestCandidate);

                // n:th peak was found, so let's move on to n+1
                n++;
                followingPeakFound = true;

            }

        } while (followingPeakFound);

    }

}
