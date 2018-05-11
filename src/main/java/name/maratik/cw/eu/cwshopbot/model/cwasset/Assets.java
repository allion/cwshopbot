//    cwshopbot
//    Copyright (C) 2018  Marat Bukharov.
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Affero General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Affero General Public License for more details.
//
//    You should have received a copy of the GNU Affero General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.
package name.maratik.cw.eu.cwshopbot.model.cwasset;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collector;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static com.google.common.collect.Maps.toImmutableEnumMap;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class Assets {
    private final Map<String, Item> allItems;
    private final Map<ItemLocation, Set<Item>> itemsByItemLocation;
    private final Map<BodyPart, Set<WearableItem>> itemsByBodyPart;
    private final Map<ItemType, Set<WearableItem>> itemsByItemType;
    private final Map<Craftbook, Set<CraftableItem>> itemsByCraftbook;
    private final Map<String, CraftableItem> craftableItems;
    private final Map<String, WearableItem> wearableItems;
    private final Map<String, Set<CraftableItem>> craftableItemsByRecipe;

    protected Assets(Map<String, Item> allItems, Map<ItemLocation, Set<Item>> itemsByItemLocation, Map<BodyPart, Set<WearableItem>> itemsByBodyPart, Map<ItemType, Set<WearableItem>> itemsByItemType, Map<Craftbook, Set<CraftableItem>> itemsByCraftbook, Map<String, CraftableItem> craftableItems, Map<String, WearableItem> wearableItems, Map<String, Set<CraftableItem>> craftableItemsByRecipe) {
        this.allItems = Objects.requireNonNull(allItems);
        this.itemsByItemLocation = Objects.requireNonNull(itemsByItemLocation);
        this.itemsByBodyPart = Objects.requireNonNull(itemsByBodyPart);
        this.itemsByItemType = Objects.requireNonNull(itemsByItemType);
        this.itemsByCraftbook = Objects.requireNonNull(itemsByCraftbook);
        this.craftableItems = Objects.requireNonNull(craftableItems);
        this.wearableItems = Objects.requireNonNull(wearableItems);
        this.craftableItemsByRecipe = Objects.requireNonNull(craftableItemsByRecipe);
    }

    public Map<String, Item> getAllItems() {
        return allItems;
    }

    public Map<ItemLocation, Set<Item>> getItemsByItemLocation() {
        return itemsByItemLocation;
    }

    public Map<BodyPart, Set<WearableItem>> getItemsByBodyPart() {
        return itemsByBodyPart;
    }

    public Map<ItemType, Set<WearableItem>> getItemsByItemType() {
        return itemsByItemType;
    }

    public Map<Craftbook, Set<CraftableItem>> getItemsByCraftbook() {
        return itemsByCraftbook;
    }

    public Map<String, CraftableItem> getCraftableItems() {
        return craftableItems;
    }

    public Map<String, WearableItem> getWearableItems() {
        return wearableItems;
    }

    public Map<String, Set<CraftableItem>> getCraftableItemsByRecipe() {
        return craftableItemsByRecipe;
    }

    @Override
    public String toString() {
        return "Assets{" +
            "allItems=" + allItems +
            '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Collection<? extends Item> allItemList;

        public Builder setAllItemList(Collection<? extends Item> items) {
            allItemList = items;
            return this;
        }

        public Assets build() {
            Map<String, Item> allItems = allItemList.stream()
                .collect(toImmutableMap(Item::getId, item -> item));
            Map<ItemLocation, ImmutableSet.Builder<Item>> itemsByItemLocationBuilder = new EnumMap<>(ItemLocation.class);
            Map<BodyPart, ImmutableSet.Builder<WearableItem>> itemsByBodyPartBuilder = new EnumMap<>(BodyPart.class);
            Map<ItemType, ImmutableSet.Builder<WearableItem>> itemsByItemTypeBuilder = new EnumMap<>(ItemType.class);
            Map<Craftbook, ImmutableSet.Builder<CraftableItem>> itemsByCraftbookBuilder = new EnumMap<>(Craftbook.class);
            ImmutableMap.Builder<String, CraftableItem> craftableItemsBuilder = ImmutableMap.builder();
            ImmutableMap.Builder<String, WearableItem> wearableItemsBuilder = ImmutableMap.builder();
            allItems.forEach((id, item) -> item.apply(
                new BuilderFiller(id, itemsByItemLocationBuilder, itemsByCraftbookBuilder, itemsByBodyPartBuilder, itemsByItemTypeBuilder, craftableItemsBuilder,
                    wearableItemsBuilder
                )
            ));
            Map<String, CraftableItem> craftableItems = craftableItemsBuilder.build();
            Map<String, Set<CraftableItem>> craftableItemsByRecipe = ImmutableMap.copyOf(craftableItems.values().stream()
                .flatMap(craftableItem -> craftableItem.getRecipe().keySet().stream()
                    .map(recipePart -> new AbstractMap.SimpleImmutableEntry<>(recipePart, craftableItem))
                ).collect(groupingBy(
                    Map.Entry::getKey,
                    mapping(Map.Entry::getValue, toImmutableSet())
                ))
            );
            return new Assets(
                allItems,
                itemsByItemLocationBuilder.entrySet().stream()
                    .collect(createImmutableMapCollector()),
                itemsByBodyPartBuilder.entrySet().stream()
                    .collect(createImmutableMapCollector()),
                itemsByItemTypeBuilder.entrySet().stream()
                    .collect(createImmutableMapCollector()),
                itemsByCraftbookBuilder.entrySet().stream()
                    .collect(createImmutableMapCollector()),
                craftableItems,
                wearableItemsBuilder.build(),
                craftableItemsByRecipe);
        }

        private static <K extends Enum<K>, V, T extends Map.Entry<K, ImmutableSet.Builder<V>>>
        Collector<T, ?, ImmutableMap<K, Set<V>>> createImmutableMapCollector() {
            return toImmutableEnumMap(Map.Entry::getKey, entry -> entry.getValue().build());
        }

        private static class BuilderFiller implements Item.Visitor {
            private final String id;
            private final Map<ItemLocation, ImmutableSet.Builder<Item>> itemsByItemLocationBuilder;
            private final Map<Craftbook, ImmutableSet.Builder<CraftableItem>> itemsByCraftbookBuilder;
            private final Map<BodyPart, ImmutableSet.Builder<WearableItem>> itemsByBodyPartBuilder;
            private final Map<ItemType, ImmutableSet.Builder<WearableItem>> itemsByItemTypeBuilder;
            private final ImmutableMap.Builder<String, CraftableItem> craftableItemsBuilder;
            private final ImmutableMap.Builder<String, WearableItem> wearableItemsBuilder;

            private BuilderFiller(String id, Map<ItemLocation, ImmutableSet.Builder<Item>> itemsByItemLocationBuilder,
                                  Map<Craftbook, ImmutableSet.Builder<CraftableItem>> itemsByCraftbookBuilder,
                                  Map<BodyPart, ImmutableSet.Builder<WearableItem>> itemsByBodyPartBuilder,
                                  Map<ItemType, ImmutableSet.Builder<WearableItem>> itemsByItemTypeBuilder,
                                  ImmutableMap.Builder<String, CraftableItem> craftableItemsBuilder,
                                  ImmutableMap.Builder<String, WearableItem> wearableItemsBuilder) {
                this.id = id;
                this.itemsByItemLocationBuilder = itemsByItemLocationBuilder;
                this.itemsByCraftbookBuilder = itemsByCraftbookBuilder;
                this.itemsByBodyPartBuilder = itemsByBodyPartBuilder;
                this.itemsByItemTypeBuilder = itemsByItemTypeBuilder;
                this.craftableItemsBuilder = craftableItemsBuilder;
                this.wearableItemsBuilder = wearableItemsBuilder;
            }

            @Override
            public void visit(Item item) {
                itemsByItemLocationBuilder.computeIfAbsent(
                    item.getItemLocation(),
                    itemLocation -> ImmutableSet.builder()
                ).add(item);
            }

            @Override
            public void visit(CraftableItem craftableItem) {
                visit((Item) craftableItem);

                itemsByCraftbookBuilder.computeIfAbsent(
                    craftableItem.getCraftbook(),
                    craftbook -> ImmutableSet.builder()
                ).add(craftableItem);
                craftableItemsBuilder.put(id, craftableItem);
            }

            @Override
            public void visit(WearableItem wearableItem) {
                visit((CraftableItem) wearableItem);

                itemsByBodyPartBuilder.computeIfAbsent(
                    wearableItem.getBodyPart(),
                    bodyPart -> ImmutableSet.builder()
                ).add(wearableItem);
                itemsByItemTypeBuilder.computeIfAbsent(
                    wearableItem.getItemType(),
                    itemType -> ImmutableSet.builder()
                ).add(wearableItem);
                wearableItemsBuilder.put(id, wearableItem);
            }
        }
    }
}
