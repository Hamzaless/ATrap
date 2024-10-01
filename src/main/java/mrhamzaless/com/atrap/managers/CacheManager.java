package mrhamzaless.com.atrap.managers;

import mrhamzaless.com.atrap.cache.TrapCache;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CacheManager {

    public static HashMap<String, String> playerPermissionCache = new HashMap<>();
    public static HashMap<String, List<TrapCache>> trapPermissionsCache = new HashMap<>();
    public static HashMap<String, UUID> trapOwnerCache = new HashMap<>();

    public static void CreateCache() {
        reloadCache();
    }

    public static void clearCache() {
        clearPlayerCache();
        clearTrapCache();
    }

    public static void clearPlayerCache() {
        playerPermissionCache.clear();
    }

    public static void clearTrapCache() {
        trapPermissionsCache.clear();
    }

    public static void clearOwnerCache() {
        trapOwnerCache.clear();
    }

    public static void reloadPlayerCache() {
        playerPermissionCache.clear();
        playerPermissionCache = TrapManager.getAllPlayersWithTheirPermissions();
    }

    public static void reloadTrapCache() {
        trapPermissionsCache.clear();
        trapPermissionsCache = TrapManager.getAllTrapsAndAllPermissions();
    }

    public static void reloadOwnerCache() {
        trapOwnerCache.clear();
        trapOwnerCache = TrapManager.getAllTrapsAndTheirOwners();
    }

    public static void reloadCache() {
        clearCache();
        reloadPlayerCache();
        reloadTrapCache();
        reloadOwnerCache();
    }

    public static String getPlayerPermissionByUUID(UUID uuid) {
        return playerPermissionCache.getOrDefault(uuid.toString(), "0");
    }
    public static String getPermissionByPermissionName(String trapName, String permission) {

        List<TrapCache> permissionsList = trapPermissionsCache.get(trapName);
        if (permissionsList != null) {
            for (TrapCache trapCache : permissionsList) {
                String key = trapCache.getKey1();
                String value = trapCache.getValue();

                if (key.contains(permission.replace("permissions.", ""))) {
                    return value;
                }
            }
        } else {
        }
        return "3";
    }
    public static UUID getTrapOwner(String trapName) {
        if (trapOwnerCache.get(trapName) == null || trapOwnerCache.get(trapName).toString().isEmpty() || trapOwnerCache.get(trapName).toString().equals("null")) {
            return null;
        }
        return trapOwnerCache.get(trapName);
    }
    public static boolean checkPlayerPermission(UUID uuid, String permission, String trapName) {
        Integer perm = Integer.parseInt(getPlayerPermissionByUUID(uuid));
        if (perm != 0) {
            if (trapPermissionsCache.get(trapName) != null) {
                for (TrapCache trapCache : trapPermissionsCache.get(trapName)) {
                    if (trapCache.getKey1().contains(permission)) {
                        if (trapCache.getValue().contains(perm.toString()))
                            return true;
                    }
                    else{
                        return false;
                    }
                }
            }
        }
        return false;
    }



}
