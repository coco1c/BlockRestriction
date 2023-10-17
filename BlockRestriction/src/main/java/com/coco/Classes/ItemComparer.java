package com.coco.Classes;

import java.util.Objects;

import javax.annotation.Nonnull;

import org.bukkit.inventory.meta.ItemMeta;

public class ItemComparer {
	
	public enum ItemMetaType {
		DISPLAY_NAME,
		ENCHANTMENTS,
		ITEM_LORE
	}
	
	@Nonnull
	public static final boolean isItemMetaSame(ItemMeta itemMeta1, ItemMeta itemMeta2, ItemMetaType itemMetaType) {
		
		switch (itemMetaType) {
	        case DISPLAY_NAME:
	            return Objects.equals(itemMeta1.getDisplayName(), itemMeta2.getDisplayName());
	        case ENCHANTMENTS:
	            return Objects.equals(itemMeta1.getEnchants(), itemMeta2.getEnchants());
	        case ITEM_LORE:
	            return Objects.equals(itemMeta1.getLore(), itemMeta2.getLore());
	        default:
	            return false;
		}
	}
}
