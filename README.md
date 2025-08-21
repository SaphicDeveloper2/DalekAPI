
# Whoniverse API

<div align="center">The ultimate framework for creating addons for Teab's Doctor Who Mod!</div>  

## About The Project

Have you ever wanted to add your own custom Daleks to Teab's Doctor Who Mod? Or perhaps create a new faction of Cybermen?

The **Whoniverse API** is a powerful framework that makes it incredibly simple to create your own addons, without needing to be an expert modder.

This API handles all the complicated code for you, letting you focus on what's important: bringing your ideas to life!

### Key Features

* 🚀 **Streamlined Entity Creation**: Add a new Dalek with a custom texture using the `DalekFactory`.
* 🧠 **Built-in AI & Logic**: Your custom entities automatically inherit the complex AI, sounds, loot tables, and attributes from the base mod, thanks to the `AbstractDalekEntity`.
* 🛡️ **Stable & Compatible**: This API is built to be future-proof. By providing a stable "front door" to the mod's features, it avoids the need for fragile Mixins, ensuring your addons are less likely to break when the base mod updates.

---

## Getting Started

This guide will walk you through setting up your development environment and creating your first custom Dalek addon from start to finish.

### 1. Project Setup

Before you can use the API, you need to add it to your mod's development environment using **CurseMaven**.

#### Add the API as a Dependency

**Find the Project Slug and File ID**:

1. Go to the Whoniverse API CurseForge page.
2. The Project Slug is the part of the URL after `/mc-mods/`. For this project, it's `whoniverse-api`.
3. Click on the **Files** tab, then click on the version you want to use.
4. The **File ID** will be listed on that page (e.g., `4567890`).

**Update `build.gradle`:**
Open your `build.gradle` file and add the CurseForge maven repository and the API as a dependency:

```gradle
// In your build.gradle file
repositories {
    // ... other repositories

    // Add the CurseForge maven repository
    maven {
        url "https://www.cursemaven.com"
        content {
            includeGroup "curse.maven"
        }
    }
}

dependencies {
    // ... other dependencies

    // Add the Whoniverse API as a dependency
    // Replace the slug and file ID with the ones you found on CurseForge
    implementation fg.deobf("curse.maven:whoniverse-api-543210") // Example: "curse.maven:whoniverse-api-4567890"
}
```

**Important Note:**
The project slug (`whoniverse-api`) and especially the file ID (`543210` in the example) will change with every new version of the API. Always check the official CurseForge page for the correct and up-to-date dependency string.

**Refresh Gradle:**
After saving the file, refresh your Gradle project in your IDE.

---

## Tutorial: Creating Your First Dalek

This tutorial will guide you through creating a **Supreme Dalek** addon.

### Step 1: Create the Entity Class

Your new entity must extend `AbstractDalekEntity`.

`SupremeDalekEntity.java`

```java
package com.youraddon.example.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import wcore.sapphic.ai.AbstractDalekEntity; // <-- Import the base class

public class SupremeDalekEntity extends AbstractDalekEntity {
    public SupremeDalekEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }
}
```

---

### Step 2: Use the Dalek Factory

Register your Dalek entity in your main mod class.

`YourAddonMod.java`

```java
package com.youraddon.example;

import com.youraddon.example.entity.SupremeDalekEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import wcore.sapphic.entities.DalekFactory;

@Mod(YourAddonMod.MODID)
public class YourAddonMod {
    public static final String MODID = "youraddon";

    // 1. Create an instance of the factory
    private static final DalekFactory DALEK_FACTORY = new DalekFactory(MODID);

    // 2. Register your Dalek Entity
    public static final RegistryObject<EntityType<SupremeDalekEntity>> SUPREME_DALEK =
            DALEK_FACTORY.registerDalek(
                    "supreme_dalek",         // The unique name for your entity
                    SupremeDalekEntity::new, // The constructor
                    "supreme_dalek.png"      // The texture file
            );

    public YourAddonMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 3. Register the factory to the event bus
        DALEK_FACTORY.register(modEventBus);
    }
}
```

---

### Step 3: Register Entity Attributes

Create an event handler to assign attributes.

`ModEventBusEvents.java`

```java
package com.youraddon.example.events;

import com.youraddon.example.YourAddonMod;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import wcore.sapphic.ai.AbstractDalekEntity;

@Mod.EventBusSubscriber(modid = YourAddonMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {

    @SubscribeEvent
    public static void onAttributeCreate(EntityAttributeCreationEvent event) {
        // Assign the default Dalek attributes
        event.put(YourAddonMod.SUPREME_DALEK.get(), AbstractDalekEntity.createAttributes().build());
    }
}
```

At this point, your Dalek is fully functional and can be spawned with:

```
/summon youraddon:supreme_dalek
```

---

## Creating a Spawn Egg

### Step 1: Register the Item

`ItemInit.java`

```java
package com.youraddon.example.init;

import com.youraddon.example.YourAddonMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, YourAddonMod.MODID);

    public static final RegistryObject<Item> SUPREME_DALEK_SPAWN_EGG = ITEMS.register("supreme_dalek_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    YourAddonMod.SUPREME_DALEK,
                    0xBFA500, // Primary color (gold)
                    0x1E1E1E, // Secondary color (black)
                    new Item.Properties()
            )
    );
    
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
```

Then call this from your mod’s constructor:

```java
ItemInit.register(modEventBus);
```

---

### Step 2: Add to Creative Tab

In `ModEventBusEvents.java`:

```java
import com.youraddon.example.init.ItemInit;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

@SubscribeEvent
public static void addCreative(BuildCreativeModeTabContentsEvent event) {
    if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
        event.accept(ItemInit.SUPREME_DALEK_SPAWN_EGG);
    }
}
```

---

### Step 3: Create Asset Files

**Item Model**
Path: `assets/youraddon/models/item/supreme_dalek_spawn_egg.json`

```json
{
  "parent": "item/template_spawn_egg"
}
```

**Language File**
Path: `assets/youraddon/lang/en_us.json`

```json
{
  "entity.youraddon.supreme_dalek": "Supreme Dalek",
  "item.youraddon.supreme_dalek_spawn_egg": "Supreme Dalek Spawn Egg"
}
```

---

## 🎉 Congratulations!

You have now created a complete, spawnable custom Dalek with a working spawn egg.
