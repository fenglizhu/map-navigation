package com.personal.mapnavigation;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

public class MapNavigation extends CordovaPlugin {
    //高德、百度包名
    public static final String GAODE_MAP_PACKAGE = "com.autonavi.minimap";
    public static final String BAIDU_MAP_PACKAGE = "com.baidu.BaiduMap";
    public static final String TENCENT_MAP_PACKAGE = "com.tencent.map";


    public static final int GAODE = 0;
    public static final int BAIDU = 1;
    public static final int TENCENT = 2;

    public static int PACKAGER_INSTALL_CODE = 1;

    //导航地图类型
    public static int navigationType = GAODE;

    //起点经纬度地址
    public static double sLon = 0;
    public static double sLat = 0;
    public static String sAddress = "";

    //终点经纬度地址
    public static double eLon = 0;
    public static double eLat = 0;
    public static String eAddress = "";

    public static int installCode  = 0;

    private CallbackContext callbackContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        if(action.equals("coolMethod")){
            if(args.length() > 0){
                navigationType = args.getInt(0);
            }
            if(args.length() > 1){
                sLon = args.getDouble(1);
            }
            if(args.length() > 2){
                sLat = args.getDouble(2);
            }
            if(args.length() > 3){
                sAddress = args.getString(3);
            }
            if(args.length() > 4){
                eLon = args.getDouble(4);
            }
            if(args.length() > 5){
                eLat = args.getDouble(5);
            }
            if(args.length() > 6){
                eAddress = args.getString(6);
            }
            if(args.length() > 7){
                installCode = args.getInt(7);
            }

            if(navigationType == GAODE){
                openGaodeNavi();
            } else if (navigationType == BAIDU) {
                openBaiDuNavi();
            } else if (navigationType == TENCENT) {
                openTencentNavi();
            }
            return true;
        }
        return super.execute(action, args, callbackContext);
    }

    /**
     * 打开高德导航
     */
    private void openGaodeNavi() {

        Boolean installed = isMapInstalled(cordova.getContext(),GAODE_MAP_PACKAGE);
        if(installed){
            String uriString = null;
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setPackage(GAODE_MAP_PACKAGE);
            StringBuilder builder = new StringBuilder("amapuri://route/plan/?sourceApplication=高德地图");
            if(sLat != 0 && sLon != 0){
                //起点
                builder.append("&slon=")
                        .append(sLon)
                        .append("&slat=")
                        .append(sLat)
                        .append("&sname=")
                        .append(sAddress);
            }
            //终点
            builder.append("&dlat=")
                    .append(eLat)
                    .append("&dlon=")
                    .append(eLon)
                    .append("&dname=")
                    .append(eAddress)
                    .append("&dev=0&t=0");

            uriString = builder.toString();
            intent.setData(Uri.parse(uriString));
            try{
                cordova.getContext().startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
                sendErrorMessage("调起高德地图异常");
            }
        } else {
            noInstalledResult(GAODE_MAP_PACKAGE);
        }
    }

    /**
     * 打开百度导航
     */
    private void openBaiDuNavi() {
        Boolean installed = isMapInstalled(cordova.getContext(),BAIDU_MAP_PACKAGE);
        if(installed){
            String uriString = null;
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setPackage(BAIDU_MAP_PACKAGE);
            StringBuilder builder = new StringBuilder("baidumap://map/direction?mode=driving");
            if(sLon != 0 && sLat != 0){
                //起点
                builder.append("&origin=latlng:")
                        .append(sLat)
                        .append(",")
                        .append(sLon)
                        .append("|name:")
                        .append(sAddress);
            }
            //终点
            builder.append("&destination=latlng:")
                    .append(eLat)
                    .append(",")
                    .append(eLon)
                    .append("|name:")
                    .append(eAddress);
            uriString = builder.toString();
            intent.setData(Uri.parse(uriString));
            try {
                cordova.getContext().startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
                sendErrorMessage("调起百度地图异常");
            }
        } else {
            noInstalledResult(BAIDU_MAP_PACKAGE);
        }
    }

    /**
     * 打开腾讯地图
     */
    private void openTencentNavi() {
        Boolean installed = isMapInstalled(cordova.getContext(),TENCENT_MAP_PACKAGE);
        if(installed){
            String uriString = null;
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setPackage(TENCENT_MAP_PACKAGE);

            StringBuilder builder = new StringBuilder("qqmap://map/routeplan?type=drive&policy=0&referer=zhufengli");

            if(sLon != 0 && sLat != 0){
                    builder.append("&from=")
                            .append(sAddress)
                            .append("&fromcoord=")
                            .append(sLat)
                            .append(",")
                            .append(sLon);
            }
            builder.append("&to")
                    .append(eAddress)
                    .append("&tocoord=")
                    .append(eLat)
                    .append(",")
                    .append(eLon);
            uriString = builder.toString();
            intent.setData(Uri.parse(uriString));
            cordova.getContext().startActivity(intent);
        }else{
            noInstalledResult(TENCENT_MAP_PACKAGE);
        }
    }

    /**
     * 向JS回调插件执行异常的数据
     * @param message
     * @param keep
     */
    private void sendResultForError(String message, boolean keep) {
        PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR,message);
        pluginResult.setKeepCallback(keep);
        callbackContext.sendPluginResult(pluginResult);
    }

    /**
     * 检测有没有安装包
     * @param context
     * @param MapPackage    包名
     * @return
     */
    public static boolean isMapInstalled(Context context, String MapPackage){
        Boolean isInstalled;
        PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getPackageInfo(MapPackage,PackageManager.GET_ACTIVITIES);
            isInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            isInstalled = false;
        }
        return isInstalled;
    }

    /**
     * 没有安装该地图时的操作
     * 1.跳到商城下载
     * 2.直接返回错误
     * @param packName
     */
    private void noInstalledResult(String packName) {
        if(installCode == PACKAGER_INSTALL_CODE){
            Uri uri = Uri.parse("market://details?id=" + packName);
            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            cordova.getContext().startActivity(intent);
            sendErrorMessage("已为你跳到指定商城，请下载对应的软件");
        }else {
            sendErrorMessage( packName == BAIDU_MAP_PACKAGE ? "未检到百度地图" :
                    packName == GAODE_MAP_PACKAGE ?  "未检到高德地图" : "未检到腾讯地图");
        }
    }

    /**
     * 向JS执行插件的错误回调
     * @param message
     */
    private void sendErrorMessage(String message) {
        callbackContext.error(message);
    }
}