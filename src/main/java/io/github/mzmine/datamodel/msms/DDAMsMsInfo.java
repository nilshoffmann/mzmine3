/*
 * Copyright (c) 2004-2022 The MZmine Development Team
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package io.github.mzmine.datamodel.msms;

import io.github.mzmine.datamodel.Scan;
import org.jetbrains.annotations.Nullable;

public interface DDAMsMsInfo extends MsMsInfo {

  String XML_PRECURSOR_MZ_ATTR = "precursormz";
  String XML_PRECURSOR_CHARGE_ATTR = "charge";
  String XML_FRAGMENT_SCAN_ATTR = "fragmentscan";
  String XML_PARENT_SCAN_ATTR = "parentscan";
  String XML_ACTIVATION_ENERGY_ATTR = "energy";
  String XML_ACTIVATION_TYPE_ATTR = "activationtype";
  String XML_MSLEVEL_ATTR = "mslevel";
  String XML_ISOLATION_WINDOW_ATTR = "isolationwindow";

  @Nullable Float getActivationEnergy();

  double getIsolationMz();

  @Nullable Integer getPrecursorCharge();

  @Nullable Scan getParentScan();

  @Nullable Scan getMsMsScan();

}
