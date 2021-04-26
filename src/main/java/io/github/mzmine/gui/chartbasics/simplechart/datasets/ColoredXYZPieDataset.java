/*
 *  Copyright 2006-2020 The MZmine Development Team
 *
 *  This file is part of MZmine.
 *
 *  MZmine is free software; you can redistribute it and/or modify it under the terms of the GNU
 *  General Public License as published by the Free Software Foundation; either version 2 of the
 *  License, or (at your option) any later version.
 *
 *  MZmine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 *  the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 *  Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with MZmine; if not,
 *  write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301
 *  USA
 */

package io.github.mzmine.gui.chartbasics.simplechart.datasets;

import com.google.common.collect.Range;
import io.github.mzmine.gui.chartbasics.chartutils.XYBlockPixelSizeRenderer;
import io.github.mzmine.gui.chartbasics.simplechart.providers.PieXYZDataProvider;
import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.taskcontrol.TaskStatus;
import java.awt.Color;
import javafx.application.Platform;
import javax.annotation.Nonnull;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.data.xy.XYZDataset;

/**
 * Used to plot XYZ datasets in a scatterplot-type of plot. Used to display spatial distribution in
 * imaging and ion mobility heatmaps.
 *
 * @author https://github.com/SteffenHeu
 */
public class ColoredXYZPieDataset<T> extends ColoredXYDataset implements XYZDataset {

  private final PieXYZDataProvider<T> pieDataProvider;
  protected AbstractXYItemRenderer renderer;
  protected double[] summedZValues;
  protected T[] sliceIdentifiers;
  private Range<Double> zRange;

  public ColoredXYZPieDataset(@Nonnull PieXYZDataProvider<T> dataProvider) {
    this(dataProvider, true);
  }

  public ColoredXYZPieDataset(@Nonnull PieXYZDataProvider<T> dataProvider,
      final boolean autocompute) {
    super(dataProvider, false);
    this.pieDataProvider = dataProvider;
    renderer = new XYBlockPixelSizeRenderer();
    if(autocompute) {
      MZmineCore.getTaskController().addTask(this);
    }
  }

  public PieXYZDataProvider<T> getPieDataProvider() {
    return pieDataProvider;
  }

  @Override
  public Number getZ(int series, int item) {
    if (!valuesComputed) {
      return 0.0;
    }
    return pieDataProvider.getZValue(item);
  }

  public double getZValue(int item) {
    if (!valuesComputed) {
      return 0.0;
    }
    return pieDataProvider.getZValue(item);
  }

  @Override
  public double getZValue(int series, int item) {
    return pieDataProvider.getZValue(series, item);
  }

  @Override
  public int getSeriesCount() {
    return sliceIdentifiers != null ? sliceIdentifiers.length : 0;
  }

  @Override
  public Comparable<?> getSeriesKey() {
    return "Pie data set";
  }

  @Override
  public Comparable<?> getSeriesKey(int series) {
    return pieDataProvider.getLabelForSeries(series);
  }

  public double getPieDiameter(int index) {
    return pieDataProvider.getPieDiameter(index);
  }

  @Override
  public int getValueIndex(final double domainValue, final double rangeValue) {
    for (int i = 0; i < computedItemCount; i++) {
      if (Double.compare(domainValue, getX(0, i).doubleValue()) == 0
          && Double.compare(rangeValue, getY(0, i).doubleValue()) == 0) {
        return i;
      }
    }
    return -1;
  }

  public Color getSliceColor(int series) {
    return pieDataProvider.getSliceColor(series);
  }

  @Override
  public void run() {
    status.set(TaskStatus.PROCESSING);
    xyValueProvider.computeValues(status);

    if (status.get() != TaskStatus.PROCESSING) {
      return;
    }

    computedItemCount = xyValueProvider.getValueCount();
    sliceIdentifiers = pieDataProvider.getSliceIdentifiers();
    valuesComputed = true;
    summedZValues = new double[computedItemCount];

    double minDomain = Double.POSITIVE_INFINITY;
    double maxDomain = Double.NEGATIVE_INFINITY;
    double minRange = Double.POSITIVE_INFINITY;
    double maxRange = Double.NEGATIVE_INFINITY;
    double minZ = Double.POSITIVE_INFINITY;
    double maxZ = Double.NEGATIVE_INFINITY;

    for (int i = 0; i < computedItemCount; i++) {
      final double rangeValue = xyValueProvider.getRangeValue(i);
      final double domainValue = xyValueProvider.getDomainValue(i);
      final double zValue = pieDataProvider.getZValue(i);

      minDomain = Math.min(domainValue, minDomain);
      maxDomain = Math.max(domainValue, maxDomain);
      minRange = Math.min(rangeValue, minRange);
      maxRange = Math.max(rangeValue, maxRange);
      minZ = Math.min(zValue, minZ);
      maxZ = Math.max(zValue, maxZ);

      for(int j = 0; j < sliceIdentifiers.length; j++) {
        summedZValues[i] += getZValue(j, i);
      }
    }

    domainRange = Range.closed(minDomain, maxDomain);
    rangeRange = Range.closed(minRange, maxRange);
    zRange = Range.closed(minZ, maxZ);

    computed = true;
    status.set(TaskStatus.FINISHED);

    if(!(this instanceof FastColoredXYZPieDataset)) { // no need to notify then, dataset will be up to date
      if (Platform.isFxApplicationThread()) {
        fireDatasetChanged();
      } else {
        Platform.runLater(this::fireDatasetChanged);
      }
    }
  }

  public Range<Double> getZValueRange() {
    return zRange;
  }

  // Makes protected method public // TODO: possible alternatives?
  @Override
  protected void fireDatasetChanged() {
    super.fireDatasetChanged();
  }

}
