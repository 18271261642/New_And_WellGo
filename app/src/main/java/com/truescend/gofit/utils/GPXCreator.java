package com.truescend.gofit.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 作者:东芝(2018/7/23).
 * 功能:
 */

public class GPXCreator {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
    private List<GPX> localGpxs = new ArrayList<>();
    private String name;
    private int type;
    private long createTime;

    public void add(GPX gpx) {
        if (createTime == 0) {
            createTime = gpx.time;
            localGpxs.clear();
        }
        localGpxs.add(gpx);
    }

    public void addAll(List<GPX> gpxs) {
        if (createTime == 0) {
            if (!gpxs.isEmpty()) {
                createTime = gpxs.get(0).time;
                localGpxs.clear();
            }
        }
        localGpxs.addAll(gpxs);
    }

    public int size() {
        return localGpxs.size();
    }

    public boolean isEmpty() {
        return localGpxs.isEmpty();
    }

    public boolean save(File outFile, String name, int type) {
        this.name = name;
        this.type = type;
        String xml = buildXml();
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(outFile);
            fileWriter.write(xml);
            fileWriter.flush();
            createTime = 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

    private String buildXml() {
        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append("\n")
                .append("<gpx creator=\"StravaGPX Android\" version=\"1.1\" xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">").append("\n")
                .append("<metadata>")
                .append("<time>")
                .append(sdf.format(createTime))
                .append("</time>")
                .append("</metadata>")
                .append("<trk>")

                .append("<name>")
                .append(name)
                .append("</name>")

                .append("<type>")
                .append(type)
                .append("</type>")

                .append("<trkseg>");
        for (GPX gpx : localGpxs) {
            builder.append("<trkpt ").append("lat=\"").append(gpx.lat).append("\" lon=\"").append(gpx.lon).append("\"").append(">")
                    .append("<ele>")
                    .append(gpx.elevation)
                    .append("</ele>")

                    .append("<time>")
                    .append(sdf.format(gpx.time))
                    .append("</time>")
                    .append("</trkpt>");
        }
        builder.append("</trkseg>")

                .append("</trk>").append("\n");
        builder.append("</gpx>");

        return builder.toString();
    }

    public static class GPX {
        public double lat;
        public double lon;
        public double elevation;
        public long time;

        public GPX(double lat, double lon, double elevation, long time) {
            this.lat = lat;
            this.lon = lon;
            this.elevation = elevation;
            this.time = time;
        }
    }
}