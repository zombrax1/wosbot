# Whiteout Survival Bot (WIP)

A powerful automation bot for Whiteout Survival, featuring multi-profile management, daily task automation, Discord notifications, and a modern JavaFX UI. Built with Java, Maven, and OpenCV/Tesseract for OCR.

## Features

- **Multi-profile management** – easily switch between multiple game profiles.
- **Emulator support** – works with MuMu, LDPlayer, Memu and other Android emulators.
- **OpenCV & Tesseract integration** – image recognition and OCR for in-game text.
- **Modern JavaFX UI** – configuration screens, console log viewer and task manager.
- **Comprehensive automation modules**:
  - Hero Recruitment
  - Nomadic Merchant
  - War Academy Shards
  - Crystal Laboratory
  - VIP Points
  - Pet Adventure
  - Exploration Chest
  - Life Essence & Caring
  - Mail Rewards
  - Daily Missions
  - Storehouse Chest & Stamina
  - Intel Missions
  - Alliance: Autojoin, Help, Tech, Pet Treasure, Chests, Triumph
  - City Upgrades (Furnace)
  - Bank
  - Gathering: Meat, Wood, Stone, Iron, Speed Boost
  - Pet Skills: Stamina, Food, Treasure, Gathering
  - Training: Infantry, Lancer, Marksman
  - Shop Automation
  - Discord Notifications
- **Alliance automation** – auto join, help, tech contributions and chest claiming.
- **Pet management** – automate pet adventures and skill usage.
- **City upgrades & gathering** – schedule resource tasks and building upgrades.
- **Robust logging and error handling** – detailed logs and recovery on failures.
- **Local database persistence** – profiles and settings are stored automatically.

## Installation

1. **Clone the repository**
2. **Install Java 21+ and Maven**
3. **Build the project:**
   ```
   mvn clean package -DskipTests
   ```
4. **Run the bot:**
   ```
   java -jar wos-hmi/target/wos-bot-1.5.0.jar
   ```

## Usage

- Launch the bot and configure your emulator paths and profiles.
- Enable/disable automation modules as needed.
- View logs and task status in the UI.
- Configure Discord integration for notifications.

## Supported Tasks (Automation Modules)

- Hero Recruitment
- Nomadic Merchant
- War Academy Shards
- Crystal Laboratory
- VIP Points
- Pet Adventure
- Exploration Chest
- Life Essence & Caring
- Mail Rewards
- Daily Missions
- Storehouse Chest & Stamina
- Intel Missions
- Alliance: Autojoin, Help, Tech, Pet Treasure, Chests, Triumph
- City Upgrades (Furnace)
- Bank
- Gathering: Meat, Wood, Stone, Iron, Speed Boost
- Pet Skills: Stamina, Food, Treasure, Gathering
- Training: Infantry, Lancer, Marksman
- Shop Automation
- Discord Notifications

## Configuration

- All settings are managed via the JavaFX UI.
- Emulator paths, Discord tokens, and task preferences can be set per profile.
- Advanced users can edit the configuration files or database directly.

## ⚙️ Configuration

The bot is designed to run on **Mumu Player** with the following settings:

- **Resolution:** 720x1280 (320 DPI)  
- **CPU:** 2 Cores  
- **RAM:** 2GB 
- **Lang:** ENGLISH

---

## 🛠️ How to Compile & Run

### 🔧 To Compile:

```sh
mvn clean install package
```

### ▶️ To Run:

#### With Logs:
```sh
java -jar wos-bot-x.x.x.jar
```

#### Without Logs
Simply double-click `wos-bot-x.x.x.jar`.

---

## 📸 Screenshots

![image1](https://raw.githubusercontent.com/camoloqlo/wosbot/refs/heads/master/images/picture1.png)  
![image2](https://raw.githubusercontent.com/camoloqlo/wosbot/refs/heads/master/images/picture2.png)  
![image3](https://raw.githubusercontent.com/camoloqlo/wosbot/refs/heads/master/images/picture3.png)
![image4](https://raw.githubusercontent.com/camoloqlo/wosbot/refs/heads/master/images/picture4.png)  
![image5](https://raw.githubusercontent.com/camoloqlo/wosbot/refs/heads/master/images/picture5.png)  
![image6](https://raw.githubusercontent.com/camoloqlo/wosbot/refs/heads/master/images/picture6.png)
![image7](https://raw.githubusercontent.com/camoloqlo/wosbot/refs/heads/master/images/picture7.png)
![image8](https://raw.githubusercontent.com/camoloqlo/wosbot/refs/heads/master/images/picture8.png)
![image9](https://raw.githubusercontent.com/camoloqlo/wosbot/refs/heads/master/images/picture9.png)

---

### 🚀 Future Features (Planned)
🔹 **Arena Battles** – Manage arena battles automatically.  
🔹 **Beast Slay** – Implement automatic beast hunting.  
🔹 **and more ofc** 🔥

## Troubleshooting

- **JAR not found or locked**: Ensure no other process is using the JAR. Delete it manually if needed before rebuilding.
- **Emulator not detected**: Check emulator path settings and ensure the emulator is running.
- **OCR issues**: Make sure tessdata is present and templates match your game resolution.
- **Discord not connecting**: Verify your bot token and channel ID.
- **General errors**: Check the logs tab in the UI for detailed error messages.

## License

MIT License - feel free to modify and use as needed!
