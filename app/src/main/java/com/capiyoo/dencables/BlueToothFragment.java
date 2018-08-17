package com.capiyoo.dencables;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hoin.btsdk.BluetoothService;
import com.hoin.btsdk.PrintPic;

public class BlueToothFragment extends Fragment {
    Button btnSearch;
    Button btnSendDraw;
    Button btnSend;
    Button btnClose;
    EditText edtContext;
    EditText edtPrint;
    private static final int REQUEST_ENABLE_BT = 2;
    BluetoothService mService = null;
    BluetoothDevice con_dev = null;
    private View qrCodeBtnSend;
    private static final int REQUEST_CONNECT_DEVICE = 1;  //Get device message

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bluetooth_fragment, null);
        btnSendDraw = (Button) view.findViewById(R.id.btn_test);
        btnSendDraw.setOnClickListener(new ClickEvent());
        btnSearch = (Button) view.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new ClickEvent());
        btnSend = (Button) view.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new ClickEvent());
        qrCodeBtnSend = (Button) view.findViewById(R.id.qr_code_Send);
        qrCodeBtnSend.setOnClickListener(new ClickEvent());
        btnClose = (Button) view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new ClickEvent());
        edtContext = (EditText) view.findViewById(R.id.txt_content);
        btnClose.setEnabled(false);
        btnSend.setEnabled(false);
        qrCodeBtnSend.setEnabled(false);
        btnSendDraw.setEnabled(false);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mService = new BluetoothService(getActivity(), mHandler);
        //Bluetooth is not available to exit the program
        if (!mService.isAvailable()) {
            Toast.makeText(getActivity(), "Bluetooth is not available", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mService.isBTopen()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null)
            mService.stop();
            mService = null;
    }

    class ClickEvent implements View.OnClickListener {
        public void onClick(View v) {
            String msg = "";
            switch (v.getId()) {
                case R.id.btn_test:
                    String lang = getString(R.string.bluetooth_strLang);
                    printImage();

                    byte[] cmd = new byte[3];
                    cmd[0] = 0x1b;
                    cmd[1] = 0x21;
                    if ((lang.compareTo("en")) == 0) {
                        cmd[2] |= 0x10;
                        mService.write(cmd);
                        mService.sendMessage(formatString("CapiYoo Infotech Pvt Ltd."), "GBK");
                        cmd[2] &= 0xEF;
                        mService.write(cmd);


                        /**
                         * Bill Format
                         *
                         * 			CapiYoo Infotech Pvt Ltd.
                         115/8 AwasVikas Sector -J
                         Keshavpuram Kalyanpur Kanpur
                         Uttar Pradesh

                         Invoice
                         Crew: XXXX					Id:XXXX
                         ----------------------------------------------------------
                         *
                         *
                         *
                         *
                         *
                         *
                         *
                         *
                         *
                         *
                         *
                         *
                         *
                         *
                         *
                         */
                        msg =
                                " 115/8 AwasVikas Sector -J \n"
                                        + "  Keshavpuram Kalyanpur Kanpur\n"
                                        + "     Uttar Pradesh\n\n"
                                        + "GSTIN-            ABCDEFG123456\n\n"
                                        + "         INVOICE\n\n"
                                        + " Crew: XXXX          Id:XXXX \n"
                                        + "--------------------------------"
                                        + "BillNO:		 234XXXXXX\n"
                                        + "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456 ";
                        mService.sendMessage(msg, "GBK");
                    } else if ((lang.compareTo("ch")) == 0) {
                        cmd[2] |= 0x10;
                        mService.write(cmd);           //±¶¿í¡¢±¶¸ßÄ£Ê½
                        mService.sendMessage("\n" +
                                "congratulations!\n", "GBK");
                        cmd[2] &= 0xEF;
                        mService.write(cmd);
                        msg = "  \n" +
                                "You have successfully connected to our Bluetooth printer!\n\n"
                                + "  Our company is a high-tech enterprise specializing in R&D, production " +
                                "and sales of commercial receipt printers and barcode scanning equipment..\n\n";
                        mService.sendMessage(msg, "GBK");
                    }
                    break;
                case R.id.btnSearch:
                    Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);      //ÔËÐÐÁíÍâÒ»¸öÀàµÄ»î¶¯
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                    break;
                case R.id.btnSend:
                    msg = edtContext.getText().toString();
                    if (msg.length() > 0) {
                        mService.sendMessage(msg, "GBK");
                    }
                    break;
                case R.id.qr_code_Send:
                    cmd = new byte[7];
                    cmd[0] = 0x1B;
                    cmd[1] = 0x5A;
                    cmd[2] = 0x00;
                    cmd[3] = 0x02;
                    cmd[4] = 0x07;
                    cmd[5] = 0x17;
                    cmd[6] = 0x00;
                    msg = getResources().getString(R.string.bluetooth_qr_code_Send_string);
                    if (msg.length() > 0) {
                        mService.write(cmd);
                        mService.sendMessage(msg, "GBK");
                    }
                    break;
                case R.id.btnClose:
                    mService.stop();
                    break;
            }
        }
    }

    /**
     * Create a Handler instance to receive the message returned by the BluetoothService class
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:   //ÒÑÁ¬½Ó
                            Toast.makeText(getActivity(), "Connect successful",
                                    Toast.LENGTH_SHORT).show();
                            btnClose.setEnabled(true);
                            btnSend.setEnabled(true);
                            qrCodeBtnSend.setEnabled(true);
                            btnSendDraw.setEnabled(true);
                            break;
                        case BluetoothService.STATE_CONNECTING:  //ÕýÔÚÁ¬½Ó
                            Log.d("À¶ÑÀµ÷ÊÔ", "ÕýÔÚÁ¬½Ó.....");
                            break;
                        case BluetoothService.STATE_LISTEN:     //¼àÌýÁ¬½ÓµÄµ½À´
                        case BluetoothService.STATE_NONE:
                            Log.d("À¶ÑÀµ÷ÊÔ", "µÈ´ýÁ¬½Ó.....");
                            break;
                    }
                    break;
                case BluetoothService.MESSAGE_CONNECTION_LOST:    //À¶ÑÀÒÑ¶Ï¿ªÁ¬½Ó
                    Toast.makeText(getActivity(), "Device connection was lost",
                            Toast.LENGTH_SHORT).show();
                    btnClose.setEnabled(false);
                    btnSend.setEnabled(false);
                    qrCodeBtnSend.setEnabled(false);
                    btnSendDraw.setEnabled(false);
                    break;
                case BluetoothService.MESSAGE_UNABLE_CONNECT:     //ÎÞ·¨Á¬½ÓÉè±¸
                    Toast.makeText(getActivity(), "Unable to connect device",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                //Request to turn on Bluetooth
                if (resultCode == Activity.RESULT_OK) {
                    //Bluetooth is turned on
                    Toast.makeText(getActivity(), "Bluetooth open successful", Toast.LENGTH_LONG).show();
                }
                break;
            case REQUEST_CONNECT_DEVICE:     //Request to connect to a Bluetooth device
                if (resultCode == Activity.RESULT_OK) {
                    //	A device item in the search list has been clicked

                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);  //Get the mac address of the device in the list item
                    con_dev = mService.getDevByMac(address);

                    mService.connect(con_dev);
                }
                break;
        }
    }

    //Print graphics
    @SuppressLint("SdCardPath")
    private void printImage() {
        byte[] sendData = null;
        PrintPic pg = new PrintPic();
        pg.initCanvas(576);
        pg.initPaint();
        pg.drawImage(0, 0, "/mnt/sdcard/icon.jpg");
        //
        sendData = pg.printDraw();
        mService.write(sendData);   //Print byte stream data
        Log.d("Bluetooth debugging", "" + sendData.length);
    }

    String formatString(String str) {

        String spacedCha = "";
        int length = str.length();
        int maxSpaces = 32;
        int leftSpaces = maxSpaces - length;
        int remainingSpaces = leftSpaces / 2;

        for (int i = 0; i < remainingSpaces; i++) {
            spacedCha += " ";
        }
        spacedCha += str;
        return spacedCha+'\n';

    }
}
