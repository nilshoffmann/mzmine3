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

package io.github.mzmine.modules.visualization.image;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

/*
 * @author Ansgar Korf (ansgar.korf@uni-muenster.de)
 */
public class ImageVisualizerPaneController {

  @FXML
  private BorderPane plotPane;

  @FXML
  private BorderPane spectrumPlotPane;

  @FXML
  private GridPane rawDataInfoGridPane;

  @FXML
  private GridPane imagingParameterInfoGridPane;

  @FXML
  private BorderPane settingsBorderPane;

  public BorderPane getPlotPane() {
    return plotPane;
  }

  public GridPane getRawDataInfoGridPane() {
    return rawDataInfoGridPane;
  }

  public GridPane getImagingParameterInfoGridPane() {
    return imagingParameterInfoGridPane;
  }

  public BorderPane getSpectrumPlotPane() {
    return spectrumPlotPane;
  }

  public BorderPane getSettingsBorderPane() {
    return settingsBorderPane;
  }

  public void setSettingsBorderPane(BorderPane settingsBorderPane) {
    this.settingsBorderPane = settingsBorderPane;
  }

  public void setRawDataInfoGridPane(GridPane rawDataInfoGridPane) {
    this.rawDataInfoGridPane = rawDataInfoGridPane;
  }

  public void setImagingParameterInfoGridPane(GridPane imagingParameterInfoGridPane) {
    this.imagingParameterInfoGridPane = imagingParameterInfoGridPane;
  }
}
