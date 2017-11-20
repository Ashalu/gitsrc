package com.tadqa.android.fragment;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.paytm.pgsdk.PaytmMerchant;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.tadqa.android.R;

public class PaymentFragment extends Fragment {

	private int randomInt = 0;
	private PaytmPGService Service = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.payment, container, false);

		Random randomGenerator = new Random();
		randomInt = randomGenerator.nextInt(1000);
		// for testing environment
		Service = PaytmPGService.getStagingService();
		// for production environment
		/* Service = PaytmPGService.getProductionService(); */

		/*
		 * PaytmMerchant constructor takes two parameters 1) Checksum generation
		 * url 2) Checksum verification url Merchant should replace the below
		 * values with his values
		 */

		PaytmMerchant Merchant = new PaytmMerchant(
				"http://43.252.91.23:71/GenerateChecksum.aspx",
				"http://43.252.91.23:71/VerifyChecksum.aspx");
//				"https://pguat.paytm.com/merchant-chksum/ChecksumGenerator",
//				"https://pguat.paytm.com/merchant-chksum/ValidateChksum");

		// below parameter map is required to construct PaytmOrder object,
		// Merchant should replace below map values with his own values

		Map<String, String> paramMap = new HashMap<String, String>();

		// these are mandatory parameters


		paramMap.put("ORDER_ID", "123456");
		paramMap.put("MID", getResources().getString(R.string.test_staging_merchantID));
		paramMap.put("CUST_ID", getResources().getString(R.string.customer_id));
		paramMap.put("CHANNEL_ID", getResources().getString(R.string.test_channel_id));
		paramMap.put("INDUSTRY_TYPE_ID", getResources().getString(R.string.test_industrytype_id));
		paramMap.put("WEBSITE", getResources().getString(R.string.test_website));
		paramMap.put("TXN_AMOUNT", "119.25");
		paramMap.put("THEME", getResources().getString(R.string.test_theme));
		paramMap.put("EMAIL", getResources().getString(R.string.email_id));
		paramMap.put("MOBILE_NO", "9718264335");


		PaytmOrder Order = new PaytmOrder(paramMap);

		Service.initialize(Order, Merchant, null);
		Service.startPaymentTransaction(getActivity(), false, false,
				new PaytmPaymentTransactionCallback() {
					@Override
					public void onTransactionSuccess(Bundle bundle) {
						// app.getLogger().error("Transaction Success :" +
						// bundle);
					}

					@Override
					public void onTransactionFailure(String s, Bundle bundle) {
						// app.getLogger().error("Transaction Failure :" + s +
						// "\n" + bundle);
					}

					@Override
					public void networkNotAvailable() {
						// app.getLogger().error("network unavailable :");
					}

					@Override
					public void clientAuthenticationFailed(String s) {
						// /
						// app.getLogger().error("clientAuthenticationFailed :"
						// + s);
					}//

					@Override
					public void someUIErrorOccurred(String s) {
						// app.getLogger().error("someUIErrorOccurred :" + s);
					}

					@Override
					public void onErrorLoadingWebPage(int i, String s, String s2) {
						// app.getLogger().error("errorLoadingWebPage :" + i +
						// "\n" + s + "\n" + s2);
					}

					@Override
					public void onBackPressedCancelTransaction() {

					}
				});

		// Inflate the layout for this fragment
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}
}
