package com.keepnote.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.keepnote.R
import com.keepnote.utils.Constants
import com.paypal.android.sdk.payments.*
import kotlinx.android.synthetic.main.activity_upgrade_payment.*
import org.json.JSONException
import java.math.BigDecimal


class UpgradePayment : AppCompatActivity() {
    companion object {
        private val TAG = "@@@@"
        /**
         * - Set to PayPalConfiguration.ENVIRONMENT_PRODUCTION to move real money.

         * - Set to PayPalConfiguration.ENVIRONMENT_SANDBOX to use your test credentials
         * from https://developer.paypal.com

         * - Set to PayPalConfiguration.ENVIRONMENT_NO_NETWORK to kick the tires
         * without communicating to PayPal's servers.
         */
        private val CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK

        // note that these credentials will differ between live & sandbox environments.
        private val CONFIG_CLIENT_ID = "Ae7y2E3xMuiAoaTxMy-UUJK7B09mK9bQlp71OOZ7n34yF95JdACdESObhjrP2Q8RTvNSLHbj4d36IBsI"

        private val REQUEST_CODE_PAYMENT = 1
        private val REQUEST_CODE_FUTURE_PAYMENT = 2
        private val REQUEST_CODE_PROFILE_SHARING = 3

        private val config = PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            .merchantName("KeepNote")
//            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
//            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upgrade_payment)

        val intent = Intent(this, PayPalService::class.java)
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
        startService(intent)

        upgradepayment.setOnClickListener {
            upgradepaymentSingle()
        }
    }

    private fun upgradepaymentSingle() {
        val thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE)
        val intent = Intent(this, PaymentActivity::class.java)

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

        startActivityForResult(intent, REQUEST_CODE_PAYMENT)
    }

    private fun getThingToBuy(paymentIntent: String): PayPalPayment {
        return PayPalPayment(BigDecimal("3.75"), "USD", "Lifetime free access",
            paymentIntent)
    }

    override fun onDestroy() {
        stopService(Intent(this, PayPalService::class.java))
        super.onDestroy()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                val confirm = data?.getParcelableExtra<PaymentConfirmation>(PaymentActivity.EXTRA_RESULT_CONFIRMATION)
                if (confirm != null) {
                    try {
                        Log.i(TAG, confirm.toJSONObject().toString(4))
                        Log.i(TAG, confirm.payment.toJSONObject().toString(4))
                        /**
                         * TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
                         * or consent completion.
                         * See https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                         * for more details.

                         * For sample mobile backend interactions, see
                         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
                         */
                        displayResultText("PaymentConfirmation info received from PayPal")


                    } catch (e: JSONException) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ", e)
                    }

                }
            }
        }  else if (resultCode == Activity.RESULT_CANCELED) {
        Log.i(TAG, "The user canceled.")
    } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
        Log.i(TAG, "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.")
    }
    }

    private fun displayResultText(s: String) {
        Constants.showToast(s,this)
    }
}
