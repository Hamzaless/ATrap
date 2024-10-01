package mrhamzaless.com.atrap.listeners;

import mrhamzaless.com.atrap.Atrap;
import mrhamzaless.com.atrap.managers.CacheManager;
import mrhamzaless.com.atrap.managers.TrapManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static mrhamzaless.com.atrap.managers.TrapManager.colorize;
import static mrhamzaless.com.atrap.managers.TrapManager.languageConfig;

public class PlayerActionListener implements Listener {

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        GameMode gameMode = player.getGameMode();
        if (gameMode != GameMode.CREATIVE) {
            String prefix = languageConfig.getString("prefix", ":server_prefix: §x&c&6&d&e&f&1›> ");
            Location blockLocation = event.getBlockClicked().getLocation();

            String trap = TrapManager.getTrapAtLocation(blockLocation);

            UUID trapOwner = CacheManager.getTrapOwner(trap);

            if (trap != null) {
                if (trapOwner != null && !Objects.equals(trapOwner.toString(), player.getUniqueId().toString())) {
                    if (!CheckPerm(event, "use_bucket", player)) {
                        player.sendMessage(colorize("\n" + prefix + languageConfig.getString("error.you_cant_do_that", "&cBunu yapmak için iznin yok!") + "\n&r"));
                        event.setCancelled(true);
                    }
                } else {
                    if (trapOwner != null && Objects.equals(trapOwner.toString(), player.getUniqueId().toString())) {
                        event.setCancelled(false);
                    } else {
                        if (trapOwner == null) {
                            if (!CheckPerm(event, "use_bucket", player)) {
                                player.sendMessage(colorize("\n" + prefix + languageConfig.getString("error.you_cant_do_that", "&cBunu yapmak için iznin yok!") + "\n&r"));
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean CheckPerm(PlayerBucketEmptyEvent event, String perm, Player player) {

        String trap = TrapManager.getTrapAtLocation(event.getBlockClicked().getLocation().toBlockLocation());
        UUID uuid = player.getUniqueId();
        UUID trapOwner = CacheManager.getTrapOwner(trap);

        if (trap != null) {
            if (trapOwner != null){
                if (!Objects.equals(trapOwner.toString(), uuid.toString())) {

                    String tperm = CacheManager.getPermissionByPermissionName(trap, perm);
                    String uperm = CacheManager.getPlayerPermissionByUUID(uuid);

                    Integer upermission = Integer.parseInt(uperm);
                    Integer tpermission = Integer.parseInt(tperm);

                    if (tpermission <= upermission) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
            else {
                String tperm = CacheManager.getPermissionByPermissionName(trap, perm);
                String uperm = CacheManager.getPlayerPermissionByUUID(uuid);

                Integer upermission = Integer.parseInt(uperm);
                Integer tpermission = Integer.parseInt(tperm);

                if (tpermission <= upermission) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;

    }


    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        GameMode gameMode = player.getGameMode();
        if (gameMode != GameMode.CREATIVE) {
            String prefix = languageConfig.getString("prefix", ":server_prefix: §x&c&6&d&e&f&1›> ");

            Location blockLocation = event.getBlock().getLocation();

            String trap = TrapManager.getTrapAtLocation(blockLocation);
            UUID trapOwner = CacheManager.getTrapOwner(trap);

            if (trap != null) {

                if (trapOwner != null && !Objects.equals(trapOwner.toString(), player.getUniqueId().toString())) {
                    if (!CheckPerm(event, "block_place", player)) {
                        player.sendMessage(colorize("\n" + prefix + languageConfig.getString("error.you_cant_do_that", "&cBunu yapmak için iznin yok!") + "\n&r"));
                        event.setCancelled(true);
                    }
                } else {
                    if (trapOwner != null && Objects.equals(trapOwner.toString(), player.getUniqueId().toString())) {
                        event.setCancelled(false);
                    } else {
                        if (trapOwner == null) {
                            if (!CheckPerm(event, "block_place", player)) {
                                player.sendMessage(colorize("\n" + prefix + languageConfig.getString("error.you_cant_do_that", "&cBunu yapmak için iznin yok!") + "\n&r"));
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer() != null){

            Player player = event.getPlayer();
            GameMode gameMode = player.getGameMode();
            if (gameMode != GameMode.CREATIVE) {
            Block block = event.getBlock();
            Material blockType = block.getType();
            Location blockLocation = block.getLocation();
            ItemStack breakTool = player.getInventory().getItemInMainHand();
            event.setCancelled(true);

                Bukkit.getScheduler().runTaskAsynchronously(Atrap.getInstance(), () -> {
                    String trap = TrapManager.getTrapAtLocation(blockLocation);
                    UUID trapOwner = CacheManager.getTrapOwner(trap);

                    Bukkit.getScheduler().runTask(Atrap.getInstance(), () -> {
                        if (trap != null && (trapOwner == null || !trapOwner.equals(player.getUniqueId()))) {
                            if (!CheckPerm(event, "block_break", player)) {
                                String message = colorize("\n" + languageConfig.getString("prefix", ":server_prefix: §x&c&6&d&e&f&1›> ")
                                        + languageConfig.getString("error.you_cant_do_that", "&cBunu yapmak için iznin yok!") + "\n&r");
                                player.sendMessage(message);
                            }
                            else{
                                event.setCancelled(false);
                                block.breakNaturally(breakTool);
                            }
                        } else {
                            if (blockType == Material.DIAMOND_ORE || blockType == Material.EMERALD_ORE || blockType == Material.LAPIS_ORE) {
                                event.setDropItems(false);
                                block.getDrops().clear();
                                block.setType(Material.AIR);
                            }
                            block.breakNaturally(breakTool);
                            event.setCancelled(false);
                        }
                    });
                });
            }
        }
    }

    @EventHandler
    public void PlayerDropItemEvent(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        GameMode gameMode = player.getGameMode();
        if (gameMode != GameMode.CREATIVE) {
            String prefix = languageConfig.getString("prefix", ":server_prefix: §x&c&6&d&e&f&1›> ");

            Location itemLocation = event.getItemDrop().getLocation();
            String trap = TrapManager.getTrapAtLocation(itemLocation);
            UUID trapOwner = CacheManager.getTrapOwner(trap);

            if (trap != null) {
                if (trapOwner != null && Objects.equals(trapOwner.toString(), player.getUniqueId().toString())) {
                    event.setCancelled(false);
                } else {
                    if (!CheckPerm(event, "drop_items", player)) {
                        player.sendMessage(colorize("\n" + prefix + languageConfig.getString("error.you_cant_do_that", "&cBunu yapmak için iznin yok!") + "\n&r"));
                        event.setCancelled(true);
                    }
                }
            }
        }
    }


    @EventHandler
    public void PlayerInteractionEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        GameMode gameMode = player.getGameMode();
        if (gameMode != GameMode.CREATIVE) {
            String prefix = colorize("\n" + languageConfig.getString("prefix", ":server_prefix: §x&c&6&d&e&f&1›> "));

            Location playerLocation = event.getClickedBlock() != null ? event.getClickedBlock().getLocation().toBlockLocation() : null;

            String trap = playerLocation != null ? TrapManager.getTrapAtLocation(playerLocation) : null;
            UUID trapOwner = trap != null ? CacheManager.getTrapOwner(trap) : null;

            if (trap != null && (trapOwner == null || !trapOwner.equals(player.getUniqueId()))) {
                Action action = event.getAction();
                Material clickedType = event.getClickedBlock() != null ? event.getClickedBlock().getType() : null;

                if (action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK || action == Action.PHYSICAL) {
                    if (clickedType != null && !isAllowedToInteract(action, clickedType, player, event)) {
                        player.sendMessage(prefix + colorize(languageConfig.getString("error.you_cant_do_that", "&cBunu yapmak için iznin yok!")) + "\n&r");
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    private boolean isAllowedToInteract(Action action, Material type, Player player, PlayerInteractEvent event) {
        String permission = null;

        switch (type) {
            case LEVER: permission = "use_lever"; break;
            case ITEM_FRAME: permission = "use_item_frame"; break;
            case ARMOR_STAND: permission = "use_armor_stand"; break;
            case ANVIL: permission = "use_anvil"; break;
            case CHEST: permission = "chest_access"; break;
            case FURNACE: permission = "use_furnace"; break;
            case CRAFTING_TABLE: permission = "use_crafting"; break;
            case ENCHANTING_TABLE: permission = "use_enchanting_table"; break;
            case BREWING_STAND: permission = "use_brewing_stand"; break;
            case ENDER_CHEST: permission = "use_ender_chest"; break;
            case HOPPER: permission = "use_hopper"; break;
            case DROPPER: permission = "use_dropper"; break;
            case DISPENSER: permission = "use_dispenser"; break;
            case BEACON: permission = "use_beacon"; break;
            case STONE_PRESSURE_PLATE: case LIGHT_WEIGHTED_PRESSURE_PLATE:
            case HEAVY_WEIGHTED_PRESSURE_PLATE: case ACACIA_PRESSURE_PLATE:
            case BIRCH_PRESSURE_PLATE: case CRIMSON_PRESSURE_PLATE:
            case DARK_OAK_PRESSURE_PLATE: case JUNGLE_PRESSURE_PLATE:
            case OAK_PRESSURE_PLATE: case SPRUCE_PRESSURE_PLATE:
            case WARPED_PRESSURE_PLATE: permission = "use_pressure_plate"; break;
            default:
                if (action == Action.RIGHT_CLICK_BLOCK) {
                    if (checkForButtonUse(event)) permission = "use_button";
                    else if (checkForDoorUse(event)) permission = "use_door";
                }
                break;
        }

        return permission == null || CheckPerm(event, permission, player);
    }


    public static boolean checkForDoorUse(PlayerInteractEvent event){
        List<Material> doors = List.of(Material.ACACIA_DOOR, Material.BIRCH_DOOR, Material.CRIMSON_DOOR, Material.DARK_OAK_DOOR, Material.IRON_DOOR, Material.JUNGLE_DOOR, Material.OAK_DOOR, Material.SPRUCE_DOOR, Material.WARPED_DOOR, Material.WARPED_TRAPDOOR, Material.CRIMSON_TRAPDOOR, Material.ACACIA_TRAPDOOR, Material.BIRCH_TRAPDOOR, Material.DARK_OAK_TRAPDOOR, Material.IRON_TRAPDOOR, Material.JUNGLE_TRAPDOOR, Material.OAK_TRAPDOOR, Material.SPRUCE_TRAPDOOR, Material.OAK_FENCE_GATE, Material.ACACIA_FENCE_GATE, Material.BIRCH_FENCE_GATE, Material.CRIMSON_FENCE_GATE, Material.DARK_OAK_FENCE_GATE, Material.JUNGLE_FENCE_GATE, Material.SPRUCE_FENCE_GATE, Material.WARPED_FENCE_GATE);
        return doors.contains((Objects.requireNonNull(event.getClickedBlock())).getType());
    }

    public static boolean checkForButtonUse(PlayerInteractEvent event){
        List<Material> buttons = List.of(Material.STONE_BUTTON, Material.POLISHED_BLACKSTONE_BUTTON, Material.ACACIA_BUTTON, Material.BIRCH_BUTTON, Material.CRIMSON_BUTTON, Material.DARK_OAK_BUTTON, Material.JUNGLE_BUTTON, Material.OAK_BUTTON, Material.SPRUCE_BUTTON, Material.WARPED_BUTTON);
        return buttons.contains((Objects.requireNonNull(event.getClickedBlock())).getType());
    }

    public static boolean CheckPerm(PlayerInteractEvent event, String perm, Player player) {
        String trap = TrapManager.getTrapAtLocation(Objects.requireNonNull(event.getClickedBlock()).getLocation().toBlockLocation());
        UUID uuid = player.getUniqueId();
        UUID trapOwner = CacheManager.getTrapOwner(trap);

        if (trap != null) {
            if (trapOwner != null){
                if (!Objects.equals(trapOwner.toString(), uuid.toString())) {

                    String tperm = CacheManager.getPermissionByPermissionName(trap, perm);
                    String uperm = CacheManager.getPlayerPermissionByUUID(uuid);

                    Integer upermission = Integer.parseInt(uperm);
                    Integer tpermission = Integer.parseInt(tperm);

                    if (tpermission <= upermission) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
            else {
                String tperm = CacheManager.getPermissionByPermissionName(trap, perm);
                String uperm = CacheManager.getPlayerPermissionByUUID(uuid);

                Integer upermission = Integer.parseInt(uperm);
                Integer tpermission = Integer.parseInt(tperm);

                if (tpermission <= upermission) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public static boolean CheckPerm(BlockPlaceEvent event, String perm, Player player) {
        String trap = TrapManager.getTrapAtLocation(event.getBlock().getLocation().toBlockLocation());
        UUID uuid = player.getUniqueId();
        UUID trapOwner = CacheManager.getTrapOwner(trap);

        if (trap != null) {
            if (trapOwner != null){
                if (!Objects.equals(trapOwner.toString(), uuid.toString())) {

                    String tperm = CacheManager.getPermissionByPermissionName(trap, perm);
                    String uperm = CacheManager.getPlayerPermissionByUUID(uuid);

                    Integer upermission = Integer.parseInt(uperm);
                    Integer tpermission = Integer.parseInt(tperm);

                    if (tpermission <= upermission) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
            else {
                String tperm = CacheManager.getPermissionByPermissionName(trap, perm);
                String uperm = CacheManager.getPlayerPermissionByUUID(uuid);

                Integer upermission = Integer.parseInt(uperm);
                Integer tpermission = Integer.parseInt(tperm);

                if (tpermission <= upermission) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public static boolean CheckPerm(BlockBreakEvent event, String perm, Player player) {
        String trap = TrapManager.getTrapAtLocation(event.getBlock().getLocation().toBlockLocation());
        UUID uuid = player.getUniqueId();
        UUID trapOwner = CacheManager.getTrapOwner(trap);

        if (trap != null) {
            if (trapOwner != null)
            {
                if (!Objects.equals(trapOwner.toString(), uuid.toString())) {



                    String tperm = CacheManager.getPermissionByPermissionName(trap, perm);
                    String uperm = CacheManager.getPlayerPermissionByUUID(uuid);

                    Integer upermission = Integer.parseInt(uperm);
                    Integer tpermission = Integer.parseInt(tperm);

                    if (tpermission <= upermission) {
                        return true;
                    } else {
                        return false;
                    }

                }
            }
            else{
                String tperm = CacheManager.getPermissionByPermissionName(trap, perm);
                String uperm = CacheManager.getPlayerPermissionByUUID(uuid);

                Integer upermission = Integer.parseInt(uperm);
                Integer tpermission = Integer.parseInt(tperm);

                if (tpermission <= upermission) {
                    return true;
                } else {
                    return false;
                }

            }
        }
        return false;
    }

    public static boolean CheckPerm(PlayerDropItemEvent event, String perm, Player player) {
        String trap = TrapManager.getTrapAtLocation(event.getPlayer().getLocation());
        UUID uuid = player.getUniqueId();
        UUID trapOwner = CacheManager.getTrapOwner(trap);

        if (trap != null) {
            if (trapOwner != null){

                if (!Objects.equals(Objects.requireNonNull(CacheManager.getTrapOwner(trap)).toString(), uuid.toString())) {

                    String tperm = CacheManager.getPermissionByPermissionName(trap, perm);
                    String uperm = CacheManager.getPlayerPermissionByUUID(uuid);

                    Integer upermission = Integer.parseInt(uperm);
                    Integer tpermission = Integer.parseInt(tperm);

                    if (tpermission <= upermission) {
                        return true;
                    } else {
                        return false;
                    }

                }
            }
        }
        else{

            String tperm = CacheManager.getPermissionByPermissionName(trap, perm);
            String uperm = CacheManager.getPlayerPermissionByUUID(uuid);

            Integer upermission = Integer.parseInt(uperm);
            Integer tpermission = Integer.parseInt(tperm);

            if (tpermission <= upermission) {
                return true;
            } else {
                return false;
            }

        }
        return false;
    }
}