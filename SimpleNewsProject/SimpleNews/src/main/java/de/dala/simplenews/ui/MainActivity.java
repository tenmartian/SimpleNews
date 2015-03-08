package de.dala.simplenews.ui;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import org.json.JSONObject;

import java.util.List;

import de.dala.simplenews.R;
import de.dala.simplenews.utilities.BaseNavigation;
import de.dala.simplenews.utilities.PrefUtilities;

public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, FragmentManager.OnBackStackChangedListener {
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Fragment currentFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setTitle(getString(R.string.simple_news_title));
        }
        setupDrawer();
        overridePendingTransition(R.anim.open_translate, R.anim.close_scale);

        RateMyApp.appLaunched(this);

        if (savedInstanceState == null) {
            if (getIntent().getDataString() != null) {
                changeColor(Color.parseColor("#ff33b5e5"));
                String path = getIntent().getDataString();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                currentFragment = CategoryModifierFragment.getInstance(path);
                transaction.replace(R.id.container, currentFragment).commit();
            } else {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                currentFragment = NewsOverViewFragment.getInstance(NewsOverViewFragment.ALL);
                transaction.replace(R.id.container, currentFragment).commit();
            }
        }else{
            if (savedInstanceState.containsKey("actionbar_color")) {
                int color = savedInstanceState.getInt("actionbar_color");
                changeColor(color);
            }else{
                changeColor(Color.parseColor("#ff33b5e5"));
            }
        }
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        updateNavigation();
        initDonationsButton();
    }

    @Override
    public boolean onSupportNavigateUp() {
        currentFragment = getVisibleFragment();
        if (currentFragment != null && currentFragment.getChildFragmentManager().getBackStackEntryCount() > 0){
            currentFragment.getChildFragmentManager().popBackStackImmediate();
        }else{
            getSupportFragmentManager().popBackStackImmediate();
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        currentFragment = getVisibleFragment();
        boolean popped;
        if (currentFragment != null && currentFragment.getChildFragmentManager().getBackStackEntryCount() > 0){
            popped = currentFragment.getChildFragmentManager().popBackStackImmediate();
        }else{
            popped = getSupportFragmentManager().popBackStackImmediate();
        }
        if (!popped) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.open_scale, R.anim.close_translate);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.menu_main_donate).setVisible(mService != null && !PrefUtilities.getInstance().shouldHideDonationButton());
        return super.onCreateOptionsMenu(menu);
    }

    protected void setupDrawer() {
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int item) {
        switch (item) {
            case NavigationDrawerFragment.HOME:
                clearBackStackKeep(0);
                currentFragment = NewsOverViewFragment.getInstance(NewsOverViewFragment.ALL);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, currentFragment).commit();
                break;
            case NavigationDrawerFragment.FAVORITE:
                clearBackStackKeep(0);
                currentFragment = NewsOverViewFragment.getInstance(NewsOverViewFragment.FAV);
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, currentFragment).commit();
                break;
            case NavigationDrawerFragment.RECENT:
                clearBackStackKeep(0);
                currentFragment = NewsOverViewFragment.getInstance(NewsOverViewFragment.RECENT);
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, currentFragment).commit();
                break;
            case NavigationDrawerFragment.UNREAD:
                clearBackStackKeep(0);
                currentFragment = NewsOverViewFragment.getInstance(NewsOverViewFragment.UNREAD);
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, currentFragment).commit();
                break;
            case NavigationDrawerFragment.CATEGORIES:
                clearBackStackKeep(1);
                currentFragment = CategoryModifierFragment.getInstance();
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
                transaction.replace(R.id.container, currentFragment).addToBackStack(null).commit();
                break;
            case NavigationDrawerFragment.SETTINGS:
                clearBackStackKeep(1);
                currentFragment = PrefFragment.getInstance();
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
                transaction.replace(R.id.container, currentFragment).addToBackStack(null).commit();
                break;
            case NavigationDrawerFragment.RATING:
                RateMyApp.showRateDialog(this);
                break;
            case NavigationDrawerFragment.IMPORT:
                clearBackStackKeep(1);
                currentFragment = OpmlFragment.getInstance();
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
                transaction.replace(R.id.container, currentFragment).addToBackStack(null).commit();
                break;
            case NavigationDrawerFragment.DONATION:
                showCoffeeDialog();
                break;
        }
        updateNavigation();
    }

    private void showCoffeeDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                this);
        builderSingle.setIcon(R.drawable.ic_coffee_red);
        builderSingle.setTitle(getResources().getString(R.string.menu_coffee));
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add(getResources().getString(R.string.menu_coffee));
        arrayAdapter.add(getResources().getString(R.string.menu_coffee2));
        arrayAdapter.add(getResources().getString(R.string.menu_coffee3));
        arrayAdapter.add(getResources().getString(R.string.menu_coffee4));
        builderSingle.setNegativeButton(getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        buy(which);
                    }
                });
        builderSingle.show();
    }

    private void clearBackStackKeep(int toKeep){
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > toKeep) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(toKeep);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        updateNavigation();
    }

    public void updateNavigation() {
        currentFragment = getVisibleFragment();
        if (currentFragment != null && currentFragment instanceof BaseNavigation){
            BaseNavigation navigation = (BaseNavigation) currentFragment;
            mNavigationDrawerFragment.checkItem(navigation.getNavigationDrawerId());
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(navigation.getTitle());
            }
        }
    }

    public Fragment getVisibleFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for(Fragment fragment : fragments){
            if(fragment != null && fragment.isAdded() && !(fragment instanceof NavigationDrawerFragment)) {
                return fragment;
            }
        }
        return null;
    }

    @Override
    public void onBackStackChanged() {
        Fragment visibleFragment = getVisibleFragment();
        if (visibleFragment instanceof NewsOverViewFragment){
            ((NewsOverViewFragment)visibleFragment).onBackStackChanged();
        }
        updateNavigation();
    }

    public void updateNavigation(int navigationDrawerId, String title) {
        mNavigationDrawerFragment.checkItem(navigationDrawerId);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("actionbar_color", lastColor);
    }

    private Drawable oldBackground = null;
    private int lastColor;

    public void changeColor(int color) {
        ColorDrawable colorDrawable = new ColorDrawable(color);
        lastColor = color;
        if (oldBackground == null) {
            getSupportActionBar().setBackgroundDrawable(colorDrawable);
        } else {
            //getSupportActionBar().setBackgroundDrawable(ld); //BUG otherwise
            TransitionDrawable td = new TransitionDrawable(new Drawable[]{oldBackground, colorDrawable});
            getSupportActionBar().setBackgroundDrawable(td);
            td.startTransition(400);
        }
        oldBackground = colorDrawable;
        changeDrawerColor(color);
    }

    private void changeDrawerColor(int newColor) {
        mNavigationDrawerFragment.changeColor(newColor);
    }


    /**
     * Donation things
     */

    private final ServiceConnection mServiceConn = new MyServiceConnection();
    private IInAppBillingService mService;

    void initDonationsButton(){
        Intent intent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        intent.setPackage("com.android.vending");
        bindService(intent, mServiceConn, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConn);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Toast.makeText(this, getString(R.string.thanks), Toast.LENGTH_LONG).show();

            new Thread(new ConsumePurchaseRunnable(data)).start();
        }
    }

    private void buy(final int id){
        switch (id){
            case 0:
                buy("coffee");
                break;
            case 1:
                buy("coffee2");
                break;
            case 2:
                buy("coffee3");
                break;
            case 3:
                buy("coffee4");
                break;
            default:
                buy("coffee");
                break;
        }
    }

    private void buy(final String sku) {
        try {
            Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(), sku, "inapp", "JbDGoa+HjaKaö3042kl42()&da/4pJ7g/KwqqXvuf+");
            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
            if (pendingIntent != null) {
                startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), 0, 0, 0);
            }
        } catch (Exception ignored) {
            Toast.makeText(this, getString(R.string.exception), Toast.LENGTH_LONG).show();
        }
    }

    private class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceDisconnected(final ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(final ComponentName name, final IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);

            new Thread(new RetrievePurchasesRunnable()).start();
        }
    }

    private class RetrievePurchasesRunnable implements Runnable {
        @Override
        public void run() {
            try {
                Bundle ownedItems = mService.getPurchases(3, getPackageName(), "inapp", null);

                int response = ownedItems.getInt("RESPONSE_CODE");
                if (response == 0) {
                    Iterable<String> purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");

                    if (purchaseDataList != null) {
                        for (String purchaseData : purchaseDataList) {
                            JSONObject json = new JSONObject(purchaseData);
                            mService.consumePurchase(3, getPackageName(), json.getString("purchaseToken"));
                        }
                    }
                }
            } catch (Exception ignored) {
                Toast.makeText(MainActivity.this, getString(R.string.exception), Toast.LENGTH_LONG).show();
            }
        }
    }

    private class ConsumePurchaseRunnable implements Runnable {
        private final Intent mData;

        ConsumePurchaseRunnable(final Intent data) {
            mData = data;
        }

        @Override
        public void run() {
            try {
                JSONObject json = new JSONObject(mData.getStringExtra("INAPP_PURCHASE_DATA"));
                mService.consumePurchase(3, getPackageName(), json.getString("purchaseToken"));
            } catch (Exception ignored) {
                Toast.makeText(MainActivity.this, getString(R.string.exception), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_coffee:
                buy("coffee");
                return true;
            case R.id.menu_main_coffee2:
                buy("coffee2");
                return true;
            case R.id.menu_main_coffee3:
                buy("coffee3");
                return true;
            case R.id.menu_main_coffee4:
                buy("coffee4");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
