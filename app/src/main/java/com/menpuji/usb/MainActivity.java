package com.menpuji.usb;

import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Map<String, UsbDevice> devices = UsbHelper.getInstance(this).getDevices();
        for (final UsbDevice device : devices.values()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(device);
                    String address = "26728512";
                    if (address.equals(device.getVendorId() + "" + device.getProductId())) {
                        UsbHelper.getInstance(MainActivity.this).getPermission(device);
                        UsbHelper.getInstance(MainActivity.this).connect(device);
                        UsbHelper.getInstance(MainActivity.this).write(address, new byte[]{0x0A, 0x0A, 0x0A, 0x0A, 0x0A});
                        UsbHelper.getInstance(MainActivity.this).close(address);
                    }
                }
            }).start();
        }
    }
}
