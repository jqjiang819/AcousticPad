package com.bigrats.acpadlib;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Created by jqjiang on 2017/5/1.
 */
public class Demo {
    public static void main(String[] args) {
        AcPadHelper acHelper = new AcPadHelper("/record.pcm","LEVD");
        acHelper.run();
        double[][] data = acHelper.getDistData();
        plot(data[0], "Time (s)", data[1], "Length (cm)", "Move Distance Plot", "LEVD");
    }

    private static void plot(double[] data, String label, String title, String key) {
        XYSeriesCollection collection = new XYSeriesCollection(getSeries(data, key));
        JFreeChart chart = ChartFactory.createXYLineChart(title,"X", label, collection);
        ChartFrame frame = new ChartFrame(title,chart);
        frame.pack();
        frame.setVisible(true);
    }

    private static void plot(double[] x, String xlabel, double[] y, String ylabel, String title, String key) {
        XYSeriesCollection collection = new XYSeriesCollection(getSeries(x, y, key));
        JFreeChart chart = ChartFactory.createXYLineChart(title,xlabel, ylabel,collection);
        ChartFrame frame = new ChartFrame(title,chart);
        frame.pack();
        frame.setVisible(true);
    }

    private static XYSeries getSeries(double[] x ,double[] y, String key) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("X and Y must be the same size.");
        }
        if (key == null) {
            key = "Data-0";
        }
        int len = x.length;
        XYSeries series = new XYSeries(key);
        for (int i = 0; i < len; i++) {
            series.add(x[i], y[i]);
        }
        return series;

    }

    private static XYSeries getSeries(double[] data, String key) {
        if (key == null) {
            key = "Data-0";
        }
        int len = data.length;
        XYSeries series = new XYSeries(key);
        for (int i = 0; i < len; i++) {
            series.add(i, data[i]);
        }
        return series;

    }
}
