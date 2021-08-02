/*
 * Copyright 2006-2021 The MZmine Development Team
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
 *
 */

package io.github.mzmine.datamodel.impl;

import io.github.mzmine.datamodel.DataPoint;

public class CollisionEnergyDataPoint implements DataPoint {

  private final double mz;
  private final double intensity;
  private final double collisionEnergy;
  private final int scanNum;

  public CollisionEnergyDataPoint(double mz, double intensity, double collisionEnergy, int scanNum) {
    this.mz = mz;
    this.intensity = intensity;
    this.collisionEnergy = collisionEnergy;
    this.scanNum = scanNum;
  }

  public double getCollisionEnergy() {
    return collisionEnergy;
  }

  @Override
  public double getMZ() {
    return mz;
  }

  @Override
  public double getIntensity() {
    return intensity;
  }

  public int getScanNum() {
    return scanNum;
  }
}
