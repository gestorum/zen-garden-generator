# zen-garden-generator

This project aims to easily create 2D & 3D animated environments with or without sound effects.

Main features:
1) Particle system with event handling & basic collision management.
2) Built-in shorcuts to capture snapshots [c], pause [p] and record [r] your animations.
3) Use built-in instruments or create your own to generate sounds.

Use ZenGardenGenerator main class to launch Processing sketches (i.e. PApplet).

ZenGardenGenerator [SketchClassName] [SketchArguments]

Built-in Sketches:
1) RSSSketch: Rideable Share System simulation using real-time data from GBFS (https://github.com/MobilityData/gbfs) compatible servers. E.g. ZenGardenGenerator RSSSketch bixi. See gbfs_hosts.properties in the resources folder.
2) A few 2D & 3D sketches in the tuto package. E.g. ZenGardenGenerator SphereCollisionSketch.

Dependencies:

https://github.com/projectlombok/lombok

https://github.com/FasterXML/jackson

https://github.com/benfry/processing4

https://github.com/philburk/jsyn

![bixi2024-04-27_loop](https://github.com/gestorum/zen-garden-generator/assets/96925948/7547cb51-46cf-42d4-8f5e-1026ae78d67f)

Rideable Share System - BIXI Montreal

https://natureofcode.com is my main source of inspiration for this project.
