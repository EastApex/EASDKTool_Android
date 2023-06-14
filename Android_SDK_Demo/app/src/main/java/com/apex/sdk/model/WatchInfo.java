package com.apex.sdk.model;

import com.apex.bluetooth.model.EABleWatchInfo;

import java.io.Serializable;

public class WatchInfo implements Serializable {
    public String watchId;                                 //Watch ID: supports utf8 strings of up to 32 bytes
    public String watchType;                               //Watch type: supports up to 32 bytes of UTF8 string
    public String firmwareVersion;                         //Firmware version: supports up to 64 bytes of UTF8 string
    public EABleWatchInfo.BindingInfo bindingInfo;                        //Binding status
    public String userId;                                  //User ID
    public long agps_update_timestamp;                     //The last update time of watch AGPs, 0 is not updated
    public String ble_mac_addr;
    public int is_wait_for_binding;                        //Whether to wait for the device to confirm the binding
    public int proj_settings;                              //Project function list: 0 not supported 1: supported (1 can only be written after the firmware implements the id=44 protocol)
    public int lcd_full_w;                                 //LCD dial width
    public int lcd_full_h;                                 //LCD dial height
    public int lcd_full_type;                              //LCD dial type: 0: square screen 1: round screen
    public int lcd_preview_w;                              //LCD dial thumbnail width
    public int lcd_preview_h;                              //LCD dial thumbnail height
    public int lcd_preview_radius;                         //LCD dial thumbnail fillet radius
    public int lcd_pixel_type;
}
