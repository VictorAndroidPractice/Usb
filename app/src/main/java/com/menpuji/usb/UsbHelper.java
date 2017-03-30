package com.menpuji.usb;


import android.app.PendingIntent;
import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class UsbHelper {
    private static final int TRANSFER_TIME_OUT = 6000;

    private static UsbHelper instance;
    private Context mContext;
    private UsbManager mUsbManager;
    private Map<String, UsbInfo> mUsbInfos = new HashMap<String, UsbInfo>();

    public UsbHelper(Context context) {
        mContext = context;
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }

    public static synchronized UsbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new UsbHelper(context);
        }
        return instance;
    }

    public void getPermission(UsbDevice usbDevice) {
        if (mUsbManager.hasPermission(usbDevice)) {
            System.out.println("已经授权");
        } else {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, null, 0);
            mUsbManager.requestPermission(usbDevice, pendingIntent);
        }
    }

    public Map<String, UsbDevice> getDevices() {
        return mUsbManager.getDeviceList();
    }

    public UsbInfo connect(UsbDevice usbDevice) {
        if (usbDevice != null && usbDevice.getInterfaceCount() != 0) {
            UsbInterface usbInterface = usbDevice.getInterface(0);
            if (usbInterface != null) {
                UsbInfo usbInfo = new UsbInfo();
                for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
                    if (usbInterface.getEndpoint(i).getType() == 2) {
                        if (usbInterface.getEndpoint(i).getDirection() == UsbConstants.USB_DIR_OUT) {
                            usbInfo.setOutUsbEndpoint(usbInterface.getEndpoint(i));
                        } else if (usbInterface.getEndpoint(i).getDirection() == UsbConstants.USB_DIR_IN) {
                            usbInfo.setInUsbEndpoint(usbInterface.getEndpoint(i));
                        }
                    }
                }
                UsbDeviceConnection usbDeviceConnection = mUsbManager.openDevice(usbDevice);
                if (usbDeviceConnection != null && usbDeviceConnection.claimInterface(usbInterface, true)) {
                    usbInfo.setUsbDeviceConnection(usbDeviceConnection);
                    mUsbInfos.put(usbDevice.getVendorId() + "" + usbDevice.getProductId(), usbInfo);
                    return usbInfo;
                }
            }
        }
        return null;
    }

    public boolean isConnected(String address) {
        UsbInfo usbInfo = mUsbInfos.get(address);
        return usbInfo != null;
    }

    public void write(String address, byte[] data) {
        UsbInfo usbInfo = mUsbInfos.get(address);
        if (usbInfo != null && usbInfo.getOutUsbEndpoint() != null) {
            int length = data.length;
            int size = usbInfo.getOutUsbEndpoint().getMaxPacketSize();
            int count = 0;
            while (count < length) {
                byte[] temp = Arrays.copyOfRange(data, count, count + Math.min(size, length - count));
                usbInfo.getUsbDeviceConnection().bulkTransfer(usbInfo.getOutUsbEndpoint(), temp, temp.length, TRANSFER_TIME_OUT);
                count = count + size;
            }
        }
    }

    public byte[] read(String address, int length) {
        UsbInfo usbInfo = mUsbInfos.get(address);
        if (usbInfo != null && usbInfo.getInUsbEndpoint() != null) {
            byte[] data = new byte[length];
            usbInfo.getUsbDeviceConnection().bulkTransfer(usbInfo.getInUsbEndpoint(), data, data.length, TRANSFER_TIME_OUT);
            return data;
        }
        return null;
    }

    public void close(String address) {
        UsbInfo usbInfo = mUsbInfos.get(address);
        if (usbInfo != null) {
            UsbDeviceConnection usbDeviceConnection = usbInfo.getUsbDeviceConnection();
            if (usbDeviceConnection != null) {
                usbInfo.getUsbDeviceConnection().close();
                mUsbInfos.remove(address);
            }
        }
    }
}
