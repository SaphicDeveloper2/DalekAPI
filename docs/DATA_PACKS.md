# Datapacks: Adding Sonics, Daleks, and Cybermen

This guide explains how to define new content using resource/datapacks with JSON files. The system loads your definitions during resource reloads and makes them available to gameplay and registered behaviors.

## Folder structure

Place JSON files in these folders inside your datapack:

- data/<your_namespace>/sonics/*.json
- data/<your_namespace>/daleks/*.json
- data/<your_namespace>/cybermen/*.json

The file path determines the definition's ID (resource location). For example:
- data/example/sonics/prototype.json -> example:prototype
- data/example/daleks/bronze.json -> example:bronze
- data/example/cybermen/cybus.json -> example:cybus

## Reloading

Resource reload (e.g., F3+T in dev or reload via menu) will refresh all definitions. Your changes are applied immediately without restarting the game.

## JSON basics

There is no hard limit on additional fields: your behaviors can read and interpret whatever structure you include. The ID is inferred from the file path; other common fields are shown below as examples.

### Sonic example (sonics/prototype.json)
