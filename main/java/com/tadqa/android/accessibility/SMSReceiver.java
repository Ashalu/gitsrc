package com.tadqa.android.accessibility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.tadqa.android.activity.GeneratePasswordFragment;
import com.tadqa.android.activity.UserCheckoutActivity;

/**
 * Created by AW-04 on 5/4/2016.
 */
public class SMSReceiver extends BroadcastReceiver {


    String From="";
    GeneratePasswordFragment genpswd=null;
    UserCheckoutActivity usrChkout=null;

    public SMSReceiver()
    {
    }

    public SMSReceiver(String from, GeneratePasswordFragment generatePasswordFragment,UserCheckoutActivity user) {
        From=from;
        genpswd=generatePasswordFragment;
        usrChkout=user;

    }


    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs;
            String msg_from;

            if (bundle != null) {

                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();

                        if (msg_from.contains("MTADQA")) {
                            String code = msgBody.substring(0, 4);
                            //setCode(code);

                            if(From=="GeneratePassword") {

                                genpswd.setCode(code);
//                                Intent intent1 = new Intent(context, GeneratePasswordFragment.class);
//                                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                intent1.putExtra("SMS", code);
//                                context.startActivity(intent1);
                            }
                            else if(From=="CheckOut")
                            {
                                usrChkout.setCode(code);
                                System.out.println(code);
                            }

                        }
                    }

                } catch (Exception e) {

                    //Log.d("Exception caught",e.getMessage());
                }
            }
        }

    }
}