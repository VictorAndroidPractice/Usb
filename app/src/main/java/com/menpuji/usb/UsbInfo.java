package com.menpuji.usb;

import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;

public class UsbInfo {
    private UsbDeviceConnection usbDeviceConnection;
    private UsbEndpoint outUsbEndpoint;
    private UsbEndpoint inUsbEndpoint;

    public UsbDeviceConnection getUsbDeviceConnection() {
        return usbDeviceConnection;
    }

    public void setUsbDeviceConnection(UsbDeviceConnection usbDeviceConnection) {
        this.usbDeviceConnection = usbDeviceConnection;
    }

    public UsbEndpoint getOutUsbEndpoint() {
        return outUsbEndpoint;
    }

    public void setOutUsbEndpoint(UsbEndpoint outUsbEndpoint) {
        this.outUsbEndpoint = outUsbEndpoint;
    }

    public UsbEndpoint getInUsbEndpoint() {
        return inUsbEndpoint;
    }

    public void setInUsbEndpoint(UsbEndpoint inUsbEndpoint) {
        this.inUsbEndpoint = inUsbEndpoint;
    }

    @Override
    public String toString() {
        return "UsbInfo{" +
                "usbDeviceConnection=" + usbDeviceConnection +
                ", outUsbEndpoint=" + outUsbEndpoint +
                ", inUsbEndpoint=" + inUsbEndpoint +
                '}';
    }
}
