/*
 * Copyright 2006-2022 The MZmine Development Team
 *
 * This file is part of MZmine.
 *
 * MZmine is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * MZmine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MZmine; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package io.github.mzmine.modules.visualization.massvoltammogram;

import io.github.mzmine.parameters.Parameter;
import io.github.mzmine.parameters.impl.SimpleParameterSet;
import io.github.mzmine.parameters.parametertypes.DoubleParameter;
import io.github.mzmine.parameters.parametertypes.ranges.DoubleRangeParameter;
import io.github.mzmine.parameters.parametertypes.ranges.MZRangeParameter;
import io.github.mzmine.parameters.parametertypes.selectors.RawDataFilesParameter;
import java.awt.Button;
import java.text.DecimalFormat;

public class MassvoltammogramParameters extends SimpleParameterSet {

  public static final RawDataFilesParameter files = new RawDataFilesParameter(1, 1);

  public static final DoubleParameter tubingLengthMM = new DoubleParameter("Tubing length / mm",
      "Tubing length between EC-Cell and ESI-Needle.", new DecimalFormat("0.0"), 750d);

  public static final DoubleParameter tubingIdMM = new DoubleParameter("Tubing inner diameter / mm",
      "Inner diameter of the tubing.", new DecimalFormat("0.000"), 0.127d);

  public static final DoubleParameter flowRateMicroLiterPerMin = new DoubleParameter(
      "Flow rate / μL/min", "Tubing length between EC-Cell and ESI-Needle.", new DecimalFormat("0.0"), 20d);

  public static final DoubleParameter potentialRampSpeed = new DoubleParameter(
      "Potential ramp / mV/s", "Potential ramp speed in mV/s.", new DecimalFormat("0.0"), 10d);

  public static final DoubleParameter stepSize = new DoubleParameter("Potential steps / mV",
      "Potential step between drawn Spectra.", new DecimalFormat("0.0"), 100d);

  public static final DoubleRangeParameter potentialRange = new DoubleRangeParameter(
      "Potential range / mV", "Minimal and maximal potential of ramp.", new DecimalFormat("0.0"));

  public static final MZRangeParameter mzRange = new MZRangeParameter("m/z Range", "Minimal and maximal m/z");


  public MassvoltammogramParameters() {
    super(new Parameter[]{files, tubingLengthMM, tubingIdMM, flowRateMicroLiterPerMin,
        potentialRampSpeed, potentialRange, stepSize, mzRange,});
  }
}

