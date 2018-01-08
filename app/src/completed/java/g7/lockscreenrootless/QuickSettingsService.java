// Copyright 2016 Google Inc.
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//      http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package g7.lockscreenrootless;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

@SuppressLint("Override")
@TargetApi(Build.VERSION_CODES.N)
public class QuickSettingsService extends TileService implements QuickSettingsServiceImpl{

    private static final String SERVICE_STATUS_FLAG = "serviceStatus";
    private static final String PREFERENCES_KEY = "com.google.android_quick_settings";

    /*
     * Called when the tile is added to the Quick Settings.
     * @return TileService constant indicating tile state
     */

    @Override
    public void onTileAdded() {
        Log.d("QS", "Tile added");
    }

    /*
     * Called when this tile begins listening for events.
     */
    @Override
    public void onStartListening() {
        Log.d("QS", "Start listening");
    }

    /*
     * Called when the user taps the tile.
     */
    @Override
    public void onClick() {
        Log.d("QS", "Tile tapped");
        Lock();
        updateTile();
    }

    /*//實作下拉Lock的部分
    public void Lock(){
        DevicePolicyManager devicePolicyManager;
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        devicePolicyManager.lockNow();
    }*/


    public void Lock() {
        // REQUEST_CODE 是之後在接 onActivityResult 所需要的常數
        // devicePolicyManager 是實際上執行鎖屏的實體
        final int REQUEST_CODE = 100;
        DevicePolicyManager devicePolicyManager;

        ComponentName componentName = new ComponentName(getApplicationContext(), deviceAdminReceiver.class);
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        // 偵測裝置管理員是否被勾選
        boolean isAdminActive = devicePolicyManager.isAdminActive(componentName);
        if (!isAdminActive) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "若要解除安裝此程式，請至 設定 > 安全性 > 裝置管理員 內取消此 App 的勾選");
            startActivityForResult(intent, REQUEST_CODE);
        } else {
            // 鎖屏
            devicePolicyManager.lockNow();
        }
    }

    private void startActivityForResult(Intent intent, int request_code) {
    }

    /*
     * Called when this tile moves out of the listening state.
     */
    @Override
    public void onStopListening() {
        Log.d("QS", "Stop Listening");
    }

    /*
     * Called when the user removes this tile from Quick Settings.
     */
    @Override
    public void onTileRemoved() {
        Log.d("QS", "Tile removed");
    }

    // Changes the appearance of the tile.
    private void updateTile() {

        Tile tile = this.getQsTile();
        boolean isActive = getServiceStatus();

        Icon newIcon;
        String newLabel;
        int newState;

        // Change the tile to match the service status.
        if (isActive) {

            /*newLabel = String.format(Locale.US,
                    "%s %s",
                    getString(R.string.tile_label),
                    getString(R.string.service_active));*/

            /*newIcon = Icon.createWithResource(getApplicationContext(),
                    ic_android_black_24dp);*/

            newState = Tile.STATE_ACTIVE;

        } /*else {
            newLabel = String.format(Locale.US,
                    "%s %s",
                    getString(R.string.tile_label),
                    getString(R.string.service_inactive));

            newIcon =
                    Icon.createWithResource(getApplicationContext(),
                            android.R.drawable.ic_dialog_alert);

            newState = Tile.STATE_INACTIVE;
        }

        // Change the UI of the tile.
        tile.setLabel(newLabel);
        tile.setIcon(newIcon);
        tile.setState(newState);*/

        // Need to call updateTile for the tile to pick up changes.
        tile.updateTile();
    }

    // Access storage to see how many times the tile
    // has been tapped.
    private boolean getServiceStatus() {

        SharedPreferences prefs =
                getApplicationContext()
                        .getSharedPreferences(PREFERENCES_KEY,
                                MODE_PRIVATE);

        boolean isActive = prefs.getBoolean(SERVICE_STATUS_FLAG, false);
        isActive = !isActive;

        prefs.edit().putBoolean(SERVICE_STATUS_FLAG, isActive).apply();

        return isActive;
    }
}
