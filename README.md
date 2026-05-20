<p align="center">
  <img src="src/main/resources/icons/logo.png" alt="EncSeal" width="96"/>
</p>

<h1 align="center">Javafx Crypt Tool</h1>

<p align="center">
  A desktop utility for encrypting and decrypting text using strong password-based encryption.<br/>
  Runs on Windows and Linux with no installation required.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white" alt="Java 21"/>
  <img src="https://img.shields.io/badge/JavaFX-21.0.2-blue?logo=java&logoColor=white" alt="JavaFX"/>
  <img src="https://img.shields.io/badge/Bouncy_Castle-1.84-green" alt="Bouncy Castle"/>
  <img src="https://img.shields.io/badge/platform-Windows%20%7C%20Linux-lightgrey" alt="Platform"/>
  <img src="https://img.shields.io/badge/license-Unlicense-blue" alt="License"/>
</p>

---

<p align="center">
  <img src="docs/screenshot/app-screenshot.png" alt="App screenshot" width="680"/>
</p>

---

## ⬇️ Download

Download the latest release from the [Releases page](https://github.com/ValerioGc/java-crypt-tool/releases):

| Platform | File | How to run |
|---|---|---|
| 🪟 Windows | `javafx-crypt-tool-windows.zip` | Extract → run `javafx-crypt-tool.exe` |
| 🐧 Linux | `javafx-crypt-tool-linux.tar.gz` | Extract → run `javafx-crypt-tool` |

> No Java installation required — the runtime is bundled inside the package.

---

## 🚀 How To Use

1. Select an **encryption algorithm** from the dropdown
2. Enter a **salting key** — a secret string used as part of the key derivation
3. Enter the **text** to encrypt or decrypt
4. Select **Encrypt** or **Decrypt** mode
5. Click **Run**
6. Use the 📋 copy button to copy the result

> ⚠️ The same salting key and algorithm used to encrypt must be used to decrypt.

### 🔄 Jasypt compatibility mode

Enable the **Jasypt mode** checkbox to decrypt (or encrypt) text produced by Jasypt's `StandardPBEStringEncryptor`. Select the algorithm and iterations count that match your Jasypt configuration.

---

## 🔐 Supported Algorithms

### EncSeal native

| Algorithm | Key size | Iterations |
|---|---|---|
| SHA256 + AES (128-bit) | 128 bit | 600,000 |
| SHA256 + AES (192-bit) | 192 bit | 600,000 |
| SHA256 + AES (256-bit) | 256 bit | 600,000 |

### Jasypt compatibility mode

Exposes additional algorithms for interoperability with Jasypt `StandardPBEStringEncryptor`, including MD5+DES, SHA1+DESede, SHA256+AES variants and more. Unsupported algorithms are hidden automatically based on the current JVM.

---

## 🌐 Supported Languages

🇬🇧 English &nbsp;·&nbsp; 🇮🇹 Italian &nbsp;·&nbsp; 🇫🇷 French &nbsp;·&nbsp; 🇪🇸 Spanish &nbsp;·&nbsp; 🇩🇪 German

Switchable at runtime from the header — no restart required.

---

## 🔗 Source

[https://github.com/ValerioGc/java-crypt-tool](https://github.com/ValerioGc/java-crypt-tool)

For build and development instructions see [DEVELOPMENT.md](DEVELOPMENT.md).
