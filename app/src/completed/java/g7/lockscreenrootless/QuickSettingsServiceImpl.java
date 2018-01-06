package g7.lockscreenrootless;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import g7.lockscreenrootless.deviceAdminReceiver;

/*
 * Created by Administrator on 2017/12/28.
 */

public interface QuickSettingsServiceImpl {

    public class Lock extends AppCompatActivity {
        // REQUEST_CODE 是之後在接 onActivityResult 所需要的常數
        // devicePolicyManager 是實際上執行鎖屏的實體
        private final int REQUEST_CODE = 100;
        DevicePolicyManager devicePolicyManager;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // setContentView(R.layout.activity_lock_screen_and_off);

            ComponentName componentName = new ComponentName(getApplicationContext(), deviceAdminReceiver.class);
            devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

            // 偵測裝置管理員是否被勾選
            boolean isAdminActive = devicePolicyManager.isAdminActive(componentName);
            if (!isAdminActive) {
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,  "若要解除安裝此程式，請至 設定 > 安全性 > 裝置管理員 內取消此 App 的勾選");
                startActivityForResult(intent, REQUEST_CODE);
            } else {
                // 鎖屏
                devicePolicyManager.lockNow();
                finish();
            }
        }

    /*
    如果只有上面那段程式碼
    在第一次啟用時並不會進行鎖屏
    所以我們要實作 onActivityResult
    利用前面宣告的 REQUEST_CODE 來判斷是從原來的 Activity 發出 intent 回來的
    如果使用者確認啟用就執行鎖屏
    同樣的最後執行 finish() 來關閉此 Activity
    */

        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    devicePolicyManager.lockNow();
                }
                finish();
            }
        }
    }
}
