# Modding Guide: Behaviors, Events, and Renderers

This guide covers how to plug custom logic into data-driven Sonics, Daleks, and Cybermen using the public API.

At a glance:
- Register behaviors per definition ID using Factory.
- Listen to provided events to intercept or augment actions.
- Use the base renderers for seamless visuals with your textures.

## Behavior registration

Each definition (sonic/dalek/cyberman) is identified by a ResourceLocation (namespace:path) derived from the datapack file path. Register your behavior for that ID.

Example registrations:
