## Thaumic Speedup

A lightweight, drop-in performance mod for Thaumcraft 6 on Minecraft 1.12.2. It targets the slow, allocation-heavy paths in Thaumcraft's aspect registration system. It scales badly in large modpacks with thousands of recipes and items.

### What it does

- Reverse recipe index for aspect generation
  - Thaumcraft derives an item's aspects by scanning the entire crafting registry once per item: O(items * recipes) and this does not scale well at all.
  - Thaumic Speedup builds an output stack -> recipes index so each subsequent lookup is a fetch instead of a full-registry walk. The index is built only for the startup pass and dropped afterward, rebuilding lazily if needed at runtime.
- Allocation-free item identity hashing
  - Thaumcraft's `generateUniqueItemstackId` copies the ItemStack, serializes it to NBT, stringifies it, and hashes it on every aspect lookup.
  - Thaumic Speedup replaces this with a direct hash of the item id + item damage (+ NBT when need be), allocating a lot less.
- Cleaner deprecated API routing
  - Routes Thaumcraft's deprecated static `registerObjectTag` calls through a singleton event proxy.