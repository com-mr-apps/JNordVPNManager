/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.geotools;

import org.geotools.api.style.Style;
import org.geotools.feature.FeatureCollection;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapLayerEvent;

public class UpdatableLayer extends FeatureLayer
{

   public UpdatableLayer(FeatureCollection<?, ?> collection, Style style)
   {
      super(collection, style);
   }

   public void updated()
   {
      fireMapLayerListenerLayerChanged(MapLayerEvent.DATA_CHANGED);
   }
}
