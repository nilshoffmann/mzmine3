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

package io.github.mzmine.datamodel.featuredata;

import com.google.common.collect.Streams;
import io.github.mzmine.datamodel.impl.CollisionEnergyDataPoint;
import io.github.mzmine.util.MemoryMapStorage;
import java.nio.DoubleBuffer;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Stores collision energy, intensity and mz values.
 *
 * @author https://github.com/nilshoffmann
 */
public interface CollisionEnergyIonSeries extends Iterable<CollisionEnergyDataPoint>, CollisionEnergySeries, IntensitySeries, MzSeries {

  @Override
  default int getNumberOfValues() {
    return getCollisionEnergyValues().capacity();
  }

  @Override
  default Iterator<CollisionEnergyDataPoint> iterator() {
    return new CollisionEnergyDataPointIterator(this.getMZValues(), this.getIntensityValues(), this.getCollisionEnergyValues());
  }

  default Stream<CollisionEnergyDataPoint> stream() {
    return Streams.stream(this);
  }

  CollisionEnergyIonSeries copy(MemoryMapStorage storage);

  static class CollisionEnergyDataPointIterator implements Iterator<CollisionEnergyDataPoint> {

    private final DoubleBuffer mzBuffer;
    private final DoubleBuffer intensityBuffer;
    private final DoubleBuffer collisionEnergyBuffer;
    private int cursor = -1;

    CollisionEnergyDataPointIterator(DoubleBuffer mzBuffer, DoubleBuffer intensityBuffer, DoubleBuffer collisionEnergyBuffer) {
      this.mzBuffer = mzBuffer;
      this.intensityBuffer = intensityBuffer;
      this.collisionEnergyBuffer = collisionEnergyBuffer;
    }

    @Override
    public boolean hasNext() {
      return (cursor + 1) < collisionEnergyBuffer.capacity();
    }

    @Override
    public CollisionEnergyDataPoint next() {
      cursor++;
      return new CollisionEnergyDataPoint(this.mzBuffer.get(cursor), this.intensityBuffer.get(cursor), this.collisionEnergyBuffer.get(cursor), cursor);
    }
  }

}
