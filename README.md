# SociaKeyboard

SociaKeyboard is a privacy-conscious, customizable, open-source keyboard based on [HeliBoard](https://github.com/Helium314/HeliBoard) (which itself is derived from AOSP / OpenBoard).
Unlike the original project, **SociaKeyboard includes optional online features**, offering seamless **text translation** through a **server-based translation system** powered by the **Mistral-Nemo LLM**.

While the keyboard itself remains open source, **the translation backend is proprietary** and **internet access is required** for online features.
SociaKeyboard is distributed as a **paid app** on the Android marketplace to support server costs.

[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png" alt="Get it on F-Droid" height="80">](https://f-droid.org/packages/helium314.keyboard/)
[<img src="https://user-images.githubusercontent.com/663460/26973090-f8fdc986-4d14-11e7-995a-e7c5e79ed925.png" alt="Get APK from GitHub" height="80">](https://github.com/bcornec44/SociaKeyboard/releases/latest)
[<img src="https://gitlab.com/IzzyOnDroid/repo/-/raw/master/assets/IzzyOnDroid.png" alt="Get it on IzzyOnDroid" height="80">](https://apt.izzysoft.de/fdroid/index/apk/helium314.keyboard)

---

## Table of Contents

* [Features](#features)
* [Online Translation](#online-translation)
* [Contributing](#contributing-)

  * [Reporting Issues](#reporting-issues)
  * [Translations](#translations)
  * [To Community Creation](#to-community)
  * [Code Contribution](CONTRIBUTING.md)
* [License](#license)
* [Credits](#credits)

---

# Features

<ul>
  <li>Add dictionaries for suggestions and spell check</li>
  <ul>
    <li>build your own, or get them <a href="https://codeberg.org/Helium314/aosp-dictionaries#dictionaries">here</a>, or in the <a href="https://codeberg.org/Helium314/aosp-dictionaries#experimental-dictionaries">experimental</a> section</li>
  </ul>
  <li>Customizable keyboard themes (style, colors, background image)</li>
  <ul>
    <li>supports system day/night mode (Android 10+)</li>
    <li>supports dynamic colors (Android 12+)</li>
  </ul>
  <li>Customizable keyboard <a href="https://github.com/bcornec44/SociaKeyboard/blob/main/layouts.md">layouts</a></li>
  <li>Multilingual typing</li>
  <li>Clipboard history</li>
  <li>One-handed mode</li>
  <li>Split keyboard and number pad</li>
  <li>Backup and restore for settings and user data</li>
</ul>

---

# Online Translation

SociaKeyboard introduces a **unique translation feature** not available in the original HeliBoard:

* 🌍 **Translation Flags in the Suggestion Bar**:
  New flag icons appear in the word suggestion bar, allowing you to instantly translate the current text input into the selected language.

* ⚙️ **Server-Side Translation**:
  Translation requests are securely sent to a proprietary **translation server** powered by **Mistral-Nemo**, a high-performance large language model.

* 🔒 **Privacy Note**:
  Only the text being translated is sent to the server. No keystrokes, history, or personal data are shared.

* ☁️ **Internet Required**:
  This feature requires internet access, unlike the rest of the keyboard which remains fully functional offline.

* 💰 **Usage Model**:
  The keyboard app remains open source, but access to the translation service is provided as part of a **paid app** on the Android marketplace.

---

For [FAQ](https://github.com/bcornec44/SociaKeyboard/wiki/FAQ) and more details, visit the [wiki](https://github.com/bcornec44/SociaKeyboard/wiki)

---

# Contributing ❤

## Reporting Issues

Report bugs or request features [here](https://github.com/bcornec44/SociaKeyboard/issues).
Before opening a new issue:

* Check if it already exists
* Ensure it’s still relevant with the latest version
* Focus on one topic per issue
* Follow the issue template

Read more on [effective bug reporting](https://www.chiark.greenend.org.uk/~sgtatham/bugs.html)

## Translations

Help translate the app via [Weblate](https://translate.codeberg.org/projects/sociakeyboard/).
PRs for translations are not accepted to avoid conflicts.

## To Community

Share your themes, layouts, and dictionaries:

* Themes can be exported and shared
* Layouts are simple text files (see [layouts.md](layouts.md))
* Dictionaries can be built with [aosp-dictionary-tools](https://github.com/remi0s/aosp-dictionary-tools)

## Code Contribution

See [Contribution Guidelines](CONTRIBUTING.md)

---

# License

SociaKeyboard (fork of [HeliBoard](https://github.com/Helium314/HeliBoard)) is licensed under **GNU GPL v3.0**.
The translation server and its code are **not open source**.

> This strong copyleft license requires making available complete source code of licensed works and modifications under the same license.

See [LICENSE](/LICENSE) for details.
The app includes components under [Apache 2.0](LICENSE-Apache-2.0).
Icons are under [CC BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/).

---

# Credits

* [HeliBoard](https://github.com/Helium314/HeliBoard)
* [OpenBoard](https://github.com/openboard-team/openboard)
* [AOSP Keyboard](https://android.googlesource.com/platform/packages/inputmethods/LatinIME/)
* [LineageOS LatinIME](https://review.lineageos.org/admin/repos/LineageOS/android_packages_inputmethods_LatinIME)
* [Simple Keyboard](https://github.com/rkkr/simple-keyboard)
* [Indic Keyboard](https://gitlab.com/indicproject/indic-keyboard)
* [FlorisBoard](https://github.com/florisboard/florisboard/)
* Icon by [Fabian OvrWrt](https://github.com/FabianOvrWrt) and [The Eclectic Dyslexic](https://github.com/the-eclectic-dyslexic)
* [Contributors](https://github.com/bcornec44/SociaKeyboard/graphs/contributors)
