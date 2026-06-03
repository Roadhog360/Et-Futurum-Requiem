# Et Futurum Requiem

**Download: [CurseForge](https://www.curseforge.com/minecraft/mc-mods/et-futurum-requiem/files)
| [Modrinth](https://modrinth.com/mod/etfuturum/versions)**

## [Legacy Modding Discord Server!](https://discord.gg/jBHQn3Nmsh)
(check out the _Roadhog360's mods_ category)

WARNING!

Though this project is licensed under the LGPL-3.0 a lot of the code featured here is a direct copy or adaptation of
Mojangs original code. So be careful with what you do with it.

This project is jss2a98aj's fork of KryptonCaptain's Et Futurum build, as well as a merge of my own (Roadhog460's)
changes I made to Et Futurum and never published anywhere. Thanks to other incredible notable contributors such as
makamys among various things, including helping setup mixins, contributed hugely useful changes like porting the
expanded container code for Iron Shulker Boxes, and CI + nomixin to the gradle and GitHub scripts. And embeddedt who
contributed some incredibly large-scale backports like the Backlytra port and spectator mode, the F3 gamemode switcher
and other really good contributions.

The mod uses MCLib's AssetDirector module to download assets from Mojang's servers.
Check [its wiki page](https://github.com/makamys/MCLib/wiki/AssetDirector) for more information.

![JProfiler](https://www.ej-technologies.com/images/product_banners/jprofiler_large.png)  
This mod is tested and profiled with JProfiler! JProfiler combines high-level analytics with low-level JVM data to pinpoint performance bottlenecks, memory leaks, slow JDBC queries, costly HTTP calls, and much more. Free open-source projects may be eligible for a **free** license. You can learn more about JProfiler here:  
https://www.ej-technologies.com/jprofiler

## Dependencies

- [UniMixins](https://modrinth.com/mod/unimixins)
  - Specifically, the GTNH module.
- [HogUtils](https://github.com/Roadhog360/HogUtils)
  - Currently EFR is in a transitional period as I get ready to use my new API. If you intend on using nightlies of EFR you will also need to download HogUtils in the same way.

## Contributing

To enable incomplete test features, add `-Detfuturum.testing=true` to your JVM arguments. This also enables a debug item

## Contributors
<a href="https://github.com/Roadhog360/Et-Futurum-Requiem/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=Roadhog360/Et-Futurum-Requiem" />
</a>

*The below is legacy information and is only kept for documentation purposes.*

### About `nomixin` builds (Obsolete)

From versions 2.4.1 to 2.6.0, the mod came in two flavors:

* The regular version embeds Mixin 0.7.11, allowing the mod to run standalone. However, this makes the jar a bit larger,
  and can cause problems in certain use cases.
* The version marked with `nomixin` doesn't embed Mixin, which lets it avoid these problems. But it requires a
  separate [Mixin bootstrap mod](https://gist.github.com/makamys/7cb74cd71d93a4332d2891db2624e17c#mixin-bootstrap-mods)
  to be installed in order to run. If you have one installed already, getting this version is recommended.

From those versions, mixin code will also not work if you do not
add `--tweakClass org.spongepowered.asm.launch.MixinTweaker --mixin mixins.etfuturum.json` to your program arguments.
