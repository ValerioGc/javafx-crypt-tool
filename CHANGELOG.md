# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [1.0.0] - 2026-05-20

### Added
- Password-based text encryption and decryption (AES-CBC via Bouncy Castle 1.84)
- Three EncSeal native PBE algorithms: SHA256 + AES 128-bit, 192-bit, 256-bit — with 600,000 KDF iterations
- Jasypt compatibility mode: decrypt and encrypt text produced by Jasypt `StandardPBEStringEncryptor`; configurable iterations count and 15+ algorithms (MD5+DES, SHA1+DESede, SHA1+AES, MD5+AES OpenSSL variants, SHA+RC4, and more), unsupported algorithms filtered at runtime
- Random 16-byte salt embedded in Base64 output — self-contained format, no separate salt storage required
- Password strength indicator with visual progress bar (weak / fair / strong / very strong)
- Show/hide toggle for the salting key field with eye/eye-slash icon
- Multi-language UI: English, Italian, French, Spanish, German — switchable from the header
- Light and dark theme toggle
- Clipboard copy button for the result field
- Portable builds for Windows and Linux via jpackage (no JRE installation required)
- GitHub Actions CI pipeline with test matrix (Windows + Linux, Liberica Full JDK 21, Xvfb on Linux)
- GitHub Actions release pipeline: automated portable builds and GitHub Release on tag push, release notes extracted from CHANGELOG.md
