# SkyGuard

[![Version](https://img.shields.io/badge/version-1.0-blue.svg)](https://github.com/yourusername/SkyGuard)
[![Minecraft](https://img.shields.io/badge/minecraft-1.18--1.21+-green.svg)](https://www.minecraft.net)
[![Paper](https://img.shields.io/badge/paper-1.18--1.21+-orange.svg)](https://papermc.io)

> **"Lock the skies, balance the fight"**
> 
> Advanced aerial combat control that finally stops the Mace+Fly one-shot meta.

---

## 📋 Table of Contents

- [The Problem](#the-problem)
- [The Solution](#the-solution)
- [Features](#features)
- [Screenshots](#screenshots)
- [Installation](#installation)
- [Configuration](#configuration)
- [Commands & Permissions](#commands--permissions)
- [How It Works](#how-it-works)
- [Compatibility](#compatibility)
- [Building from Source](#building-from-source)
- [Support](#support)

---

## 🚨 The Problem

You're running a PvP server. Players complain about this:

1. Player equips **Wings** enchantment (AdvancedEnchantments) or uses `/fly`
2. Player grabs a **Mace** (1.21+) 
3. Player flies 50 blocks up
4. Player drops down and **one-shots** anyone instantly
5. **Zero counterplay. Zero skill. 100% abuse.**

Existing plugins?
- WorldGuard's `fly` flag? **Bypassed by Wings enchantment.**
- EssentialsX `/fly` toggle? **Doesn't prevent re-enabling during combat.**
- Other combat plugins? **Don't detect the Mace+Fly combo.**

**Your players are frustrated. You need SkyGuard.**

---

## ✅ The Solution

SkyGuard is a **brutally effective** flight control system that:

| What It Does | Why It Matters |
|-------------|----------------|
| **Detects Mace hits from flyers** | Catches the exact abuse pattern |
| **Tags BOTH players for combat** | Attacker can't escape, victim gets fair fight |
| **Force-disables flight** | Overrides WorldGuard, EssentialsX, Wings, Creative - everything |
| **Blocks Mace while flying** | Prevents the abuse before it starts |
| **15-second combat timer** | Long enough to finish the fight |

**Result:** Fair aerial combat. No more drop-kills. Happy players.

---

## ✨ Features

### 🔒 Absolute Flight Lock
- Uses `EventPriority.HIGHEST` to process **after** all other plugins
- Disables flight from **any source**: `/fly`, Wings enchantment, Creative mode, other plugins
- Prevents re-enabling until combat ends

### ⚔️ Smart Combat Tagging
- Triggered only by **Mace hits from flying players**
- Tags **both** attacker and victim
- Visual action-bar countdown
- Clear enter/leave messages

### 🚫 Mace Restriction
- Cannot equip Mace while flying
- Cannot use Mace while flying
- Prevents inventory hotbar swaps to Mace

### 🔍 Wings Detection
- Automatically detects AdvancedEnchantments Wings enchantment
- Checks item lore for enchantment name
- Configurable enchantment name

### 🎨 Customizable
- All messages fully configurable with color codes
- Adjustable combat duration
- World/region blacklist support
- Debug mode for troubleshooting

---

## 📸 Screenshots

*Coming soon - show your plugin in action!*

Suggested screenshots:
- Action bar timer during combat
- Message when entering combat
- Attempting to fly during combat (blocked)

---

## 📥 Installation

### Quick Install (Recommended)

1. Download `SkyGuard-1.0.jar` from [Releases](../../releases)
2. Drop into `plugins/` folder
3. Restart server
4. Done! (Default config works out-of-box)

### First-Time Setup

```bash
# After first restart, edit config
nano plugins/SkyGuard/config.yml

# Make your changes, then reload in-game
/cfc reload
