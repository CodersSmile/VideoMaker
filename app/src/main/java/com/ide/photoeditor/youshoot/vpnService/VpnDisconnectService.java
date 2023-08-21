package com.ide.photoeditor.youshoot.vpnService;

//
//import static com.video.music.downloader.vpn.VpnConnected.stopVpn;
//
//import android.app.Service;
//import android.content.Intent;
//import android.os.IBinder;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import unified.vpn.sdk.Callback;
//import unified.vpn.sdk.CompletableCallback;
//import unified.vpn.sdk.TrackingConstants;
//import unified.vpn.sdk.UnifiedSdk;
//import unified.vpn.sdk.VpnException;
//import unified.vpn.sdk.VpnState;

//public class VpnDisconnectService extends Service {
//    private static final String TAG = "VpnDisconnectService";
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    @Override
//    public void onTaskRemoved(Intent rootIntent) {
//        super.onTaskRemoved(rootIntent);
//        UnifiedSdk.getVpnState(new Callback<VpnState>() {
//            @Override
//            public void success(@NonNull VpnState vpnState) {
//                if (vpnState == VpnState.CONNECTED) {
//                    UnifiedSdk.getInstance().getVpn().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
//                        @Override
//                        public void complete() {
//                            Log.e(TAG, "complete: vpn disconnect successfully");
//                        }
//
//                        @Override
//                        public void error(@NonNull VpnException e) {
//
//
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void failure(@NonNull VpnException e) {
//
//            }
//        });
//    }
//
//    @Override
//    public void onDestroy() {
//        stopVpn();
//    }
//}
