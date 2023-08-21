package com.ide.photoeditor.youshoot.vpnService;

public class VpnConnected {

//    public static ClientInfo clientInfo;
//    public static List<Country> list_c;
//    public static String selected = "";
//    public static ArrayList<Country_Code> list_country;
//
//
//
//
//
//    public static void getCountry(Context context, VpnConnectListener listener) {
//       /* try {
//            UnifiedSdk.getInstance().getBackend().countries(new Callback<AvailableCountries>() {
//                @Override
//                public void success(@NonNull AvailableCountries availableCountries) {
//                    try {
//                        Content.list = new ArrayList<>();
//                        list_country = new ArrayList<>();
//                        list_c = availableCountries.getCountries();
//                        Content.list = availableCountries.getCountries();
//                        if (Content.list_Country.size() > 0) {
//                            for (Country country : Content.list) {
//                                for (Country_Code code : Content.list_Country) {
//                                    if (country.getCountry().equalsIgnoreCase(code.getCode())) {
//                                        Country_Code new_code = new Country_Code();
//                                        new_code.setCode(code.getCode());
//                                        list_country.add(new_code);
//                                    }
//                                }
//                            }
//                            if (!list_country.isEmpty()) {
//                                selected = getRandomString(list_country);
//                            } else {
//                                selected = "";
//                            }
//                        } else {
//                            if (!list_c.isEmpty()) {
//                                selected = getRandomStringValue(list_c);
//                            } else {
//                                selected = "";
//                            }
//                        }
//                        Log.e("Random_country ", selected);
//                        connectToVpn(selected, context, listener);
//                    } catch (Exception e) {
//                        Log.e("Random_country ", e.getMessage());
//                        Log.e("Random_country ", selected);
//                        connectToVpn(selected, context, listener);
//                    }
//                    connectToVpn("", context, listener);
//                }
//
//                @Override
//                public void failure(@NonNull VpnException e) {
//                    listener.Connected(false);
//                    Log.e("Error_Vpn", e.getMessage());
//                    //listener.connectFail();
//                }
//            });
//        } catch (Exception e) {
//            listener.Connected(true);
//            Log.e("Error_Vpn", e.getMessage());
//            //  listener.connectFail();
//        }*/
//
//        UnifiedSdk.clearInstances();
//
//
//    }
//
//    public static void connectToVpn(String country, Context context, VpnConnectListener listener) {
//        isLoggedIn(new Callback<Boolean>() {
//            @Override
//            public void success(@NonNull Boolean aBoolean) {
//                if (aBoolean) {
//                    List<String> fallbackOrder = new ArrayList<>();
//                    fallbackOrder.add(HydraTransport.TRANSPORT_ID);
//                    fallbackOrder.add(OpenVpnTransport.TRANSPORT_ID_TCP);
//                    fallbackOrder.add(OpenVpnTransport.TRANSPORT_ID_UDP);
//                    List<String> bypassDomains = new LinkedList<>();
//                    UnifiedSdk.getInstance().getVpn().start(new SessionConfig.Builder()
//                            .withReason(TrackingConstants.GprReasons.M_UI)
//                            .withTransportFallback(fallbackOrder)
//                            .withTransport(HydraTransport.TRANSPORT_ID)
//                            .withVirtualLocation(country)
//                            .addDnsRule(TrafficRule.Builder.bypass().fromDomains(bypassDomains))
//                            .build(), new CompletableCallback() {
//                        @Override
//                        public void complete() {
//                            updateUi();
//                            listener.Connected(true);
//                            Intent intent = new Intent(context, VpnDisconnectService.class);
//                            context.startService(intent);
//
//                        }
//
//                        @Override
//                        public void error(@NonNull VpnException e) {
//
//                            listener.Connected(true);
//                            updateUi();
//                        }
//                    });
//
//                } else {
//                    listener.Connected(true);
//                    updateUi();
//
//                }
//            }
//
//            @Override
//            public void failure(@NonNull VpnException e) {
//                listener.Connected(true);
//                updateUi();
//            }
//        });
//    }
//
//    private static void updateUi() {
//
//        UnifiedSdk.getVpnState(new Callback<VpnState>() {
//            @Override
//            public void success(@NonNull VpnState vpnState) {
//                if (vpnState == VpnState.CONNECTED) {
//
//                } else {
//
//                }
//
//                switch (vpnState) {
//                    case IDLE:
//                        // connectionStatus.setText("Status: " + getString(R.string.disconnected));
//                        // connect.setImageResource(R.drawable.ic_off);
//                        break;
//                    case CONNECTED:
//                        // connectionStatus.setText("Status: " + getString(R.string.connected));
//                        //connect.setImageResource(R.drawable.ic_on);
//                        break;
//                    case CONNECTING_VPN:
//                    case CONNECTING_CREDENTIALS:
//                    case CONNECTING_PERMISSIONS:
//                        // connectionStatus.setText("Status: " + getString(R.string.connecting));
//                        //  connect.setImageResource(R.drawable.ic_off);
//                        break;
//                    case PAUSED:
//                        // connectionStatus.setText("Status: " + getString(R.string.paused));
//                        //  connect.setImageResource(R.drawable.ic_off);
//                        break;
//                    case DISCONNECTING:
//                        //  connectionStatus.setText("Status: " + getString(R.string.disconnecting));
//                        //  connect.setImageResource(R.drawable.ic_off);
//                }
//            }
//
//            @Override
//            public void failure(@NonNull VpnException e) {
//
//
//            }
//        });
//        getCurrentServer(new Callback<String>() {
//            @Override
//            public void success(@NonNull String s) {
//                if (!s.equals("")) {
//                }
//
//            }
//
//            @Override
//            public void failure(@NonNull VpnException e) {
//
//            }
//        });
//    }
//
//    protected static void getCurrentServer(final Callback<String> callback) {
//        UnifiedSdk.getVpnState(new Callback<VpnState>() {
//            @Override
//            public void success(@NonNull VpnState state) {
//                if (state == VpnState.CONNECTED) {
//                    UnifiedSdk.getStatus(new Callback<SessionInfo>() {
//                        @Override
//                        public void success(@NonNull SessionInfo sessionInfo) {
//                            callback.success(sessionInfo.getCredentials().getFirstServerIp());
//
//                        }
//
//                        @Override
//                        public void failure(@NonNull VpnException e) {
//                            callback.success("");
//
//                        }
//                    });
//                } else {
//                    callback.success("");
//
//                }
//            }
//
//            @Override
//            public void failure(@NonNull VpnException e) {
//                callback.failure(e);
//
//            }
//        });
//    }
//
//    private static String getRandomString(ArrayList<Country_Code> list) {
//        int min = 0;
//        int max = list.size();
//        return list.get(new Random().nextInt(((max - min) + 1) + min)).getCode();
//    }
//
//    private static String getRandomStringValue(List<Country> list) {
//        int min = 0;
//        int max = list.size();
//        return list.get(new Random().nextInt(((max - min) + 1) + min)).getCountry();
//    }
//
//    protected static void isLoggedIn(Callback<Boolean> callback) {
//        UnifiedSdk.getInstance().getBackend().isLoggedIn(callback);
//    }
//
//    public static void stopVpn() {
//        UnifiedSdk.getVpnState(new Callback<VpnState>() {
//            @Override
//            public void success(@NonNull VpnState vpnState) {
//                if (vpnState == VpnState.CONNECTED) {
//                    UnifiedSdk.getInstance().getVpn().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
//                        @Override
//                        public void complete() {
//                        }
//
//                        @Override
//                        public void error(@NonNull VpnException e) {
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
//    public static void loginClientInfo(ClientInfo clientInfo, LoginListener listener) {
//        UnifiedSdk unifiedSDK = UnifiedSdk.getInstance(clientInfo);
//        AuthMethod authMethod = AuthMethod.anonymous();
//        unifiedSDK.getBackend().login(authMethod, new Callback<User>() {
//            @Override
//            public void success(@NonNull User user) {
//                Log.e("TAG", "success: " + user.getAccessToken() + " " + user.getSubscriber());
//                listener.loginSuccess();
//            }
//
//            @Override
//            public void failure(@NonNull VpnException e) {
//                listener.loginFaild();
//                Log.e("TAG", "failure: ", e);
//            }
//        });
//    }
}