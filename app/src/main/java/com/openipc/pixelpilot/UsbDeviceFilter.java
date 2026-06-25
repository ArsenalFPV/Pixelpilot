package com.openipc.pixelpilot;

import android.content.Context;
import android.content.res.XmlResourceParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UsbDeviceFilter {
    public int vendorId;
    public int productId;

    public UsbDeviceFilter(int vid, int pid) {
        vendorId = vid;
        productId = pid;
    }

    public static List<UsbDeviceFilter> parseXml(Context context, int resourceId)
            throws XmlPullParserException, IOException {
        List<UsbDeviceFilter> devices = new ArrayList<>();
        XmlResourceParser parser = context.getResources().getXml(resourceId);

        int eventType = parser.getEventType();
        while (eventType != XmlResourceParser.END_DOCUMENT) {
            if (eventType == XmlResourceParser.START_TAG) {
                String tag = parser.getName();
                if (tag.equals("usb-device")) {
                    String vendorIdString = null;
                    String productIdString = null;

                    for (int i = 0; i < parser.getAttributeCount(); i++) {
                        String attrName = parser.getAttributeName(i);
                        if ("vendor-id".equals(attrName)) {
                            vendorIdString = parser.getAttributeValue(i);
                        } else if ("product-id".equals(attrName)) {
                            productIdString = parser.getAttributeValue(i);
                        }
                    }

                    if (vendorIdString != null && productIdString != null) {
                        try {
                            int vendorId = parseHexOrDec(vendorIdString);
                            int productId = parseHexOrDec(productIdString);
                            devices.add(new UsbDeviceFilter(vendorId, productId));
                        } catch (NumberFormatException e) {
                            android.util.Log.e("pixelpilot", "Failed to parse USB filter: VID=" + vendorIdString + " PID=" + productIdString);
                        }
                    }
                }
            }
            eventType = parser.next();
        }
        return devices;
    }

    private static int parseHexOrDec(String s) {
        if (s.startsWith("0x") || s.startsWith("0X")) {
            return Integer.parseInt(s.substring(2), 16);
        } else {
            try {
                return Integer.parseInt(s, 16);
            } catch (NumberFormatException e) {
                return Integer.parseInt(s);
            }
        }
    }
}
