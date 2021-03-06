// **********************************************************************
//
// Copyright (c) 2003-2017 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************
//
// Ice version 3.6.4
//
// <auto-generated>
//
// Generated from file `Metrics.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package IceMX;

/**
 * The metrics administrative facet interface. This interface allows
 * remote administrative clients to access metrics of an application
 * that enabled the Ice administrative facility and configured some
 * metrics views.
 *
 **/
public interface _MetricsAdminOperationsNC
{
    /**
     * Get the names of enabled and disabled metrics.
     *
     * @param disabledViews The names of the disabled views.
     *
     * @return The name of the enabled views.
     *
     **/
    String[] getMetricsViewNames(Ice.StringSeqHolder disabledViews);

    /**
     * Enables a metrics view.
     *
     * @param name The metrics view name.
     *
     * @throws UnknownMetricsView Raised if the metrics view cannot be
     * found.
     *
     **/
    void enableMetricsView(String name)
        throws UnknownMetricsView;

    /**
     * Disable a metrics view.
     *
     * @param name The metrics view name.
     *
     * @throws UnknownMetricsView Raised if the metrics view cannot be
     * found.
     *
     **/
    void disableMetricsView(String name)
        throws UnknownMetricsView;

    /**
     * Get the metrics objects for the given metrics view. This
     * returns a dictionnary of metric maps for each metrics class
     * configured with the view. The timestamp allows the client to
     * compute averages which are not dependent of the invocation
     * latency for this operation.
     *
     * @param view The name of the metrics view.
     *
     * @param timestamp The local time of the process when the metrics
     * object were retrieved.
     *
     * @return The metrics view data.
     *
     * @throws UnknownMetricsView Raised if the metrics view cannot be
     * found.
     *
     **/
    java.util.Map<java.lang.String, Metrics[]> getMetricsView(String view, Ice.LongHolder timestamp)
        throws UnknownMetricsView;

    /**
     * Get the metrics failures associated with the given view and map.
     *
     * @param view The name of the metrics view.
     *
     * @param map The name of the metrics map.
     *
     * @return The metrics failures associated with the map.
     *
     * @throws UnknownMetricsView Raised if the metrics view cannot be
     * found.
     *
     **/
    MetricsFailures[] getMapMetricsFailures(String view, String map)
        throws UnknownMetricsView;

    /**
     * Get the metrics failure associated for the given metrics.
     *
     * @param view The name of the metrics view.
     *
     * @param map The name of the metrics map.
     *
     * @param id The ID of the metrics.
     *
     * @return The metrics failures associated with the metrics.
     *
     * @throws UnknownMetricsView Raised if the metrics view cannot be
     * found.
     *
     **/
    MetricsFailures getMetricsFailures(String view, String map, String id)
        throws UnknownMetricsView;
}
