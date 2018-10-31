package swipedelete.swipedelete;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

import java.io.Serializable;


public class SubBillingProcessor extends BillingProcessor implements Serializable
{
    public SubBillingProcessor(Context context, String licenseKey, IBillingHandler handler)
    {
        super(context, licenseKey, handler);
    }

    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details)
    {

    }

    public void onPurchaseHistoryRestored()
    {

    }

    public void onBillingError(int errorCode, @Nullable Throwable error)
    {

    }

    public void onBillingInitialized()
    {

    }
}
