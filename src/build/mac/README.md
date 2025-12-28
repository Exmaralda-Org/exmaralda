# macOS Entitlements for EXMARaLDA

This directory contains the entitlements plist file used for code signing macOS applications.

## Background

Starting with newer Java versions (JDK 21+), macOS requires proper entitlements to be set during code signing for Java applications to run correctly on Apple Silicon (M1/M2) Macs. Without these entitlements, applications may crash on launch with cryptic error messages.

## The Problem

The jpackage tool in JDK versions prior to 25.0.2 has a known bug (JDK-8358723) where the `--mac-entitlements` flag is ignored during the signing process. This causes applications to crash with errors like:

```
Exception Type:        EXC_BREAKPOINT (SIGTRAP)
Termination Reason:    Namespace SIGNAL, Code 5 Trace/BPT trap: 5
```

## The Solution

The build process now includes a workaround:

1. jpackage creates the app image without proper entitlements
2. The existing signature is removed from the launcher executable
3. The launcher is re-signed with the correct entitlements using codesign

## Entitlements File

The `entitlements.plist` file contains the following required entitlements for Java applications:

- **com.apple.security.cs.allow-jit**: Allows Just-In-Time compilation for the Java VM
- **com.apple.security.cs.allow-unsigned-executable-memory**: Allows Java to use executable memory
- **com.apple.security.cs.allow-dyld-environment-variables**: Allows dynamic library loading
- **com.apple.security.cs.disable-library-validation**: Disables validation for JNI libraries

## Build Process

The M1 build script (`build_mac_m1.xml`) now includes:

1. **prepare_jpackage**: Prepares the build environment
2. **mac_executables_jpackage**: Runs jpackage to create app images
3. **remove_signatures**: Removes faulty signatures and re-signs with entitlements
4. **make_dmgs**: Creates disk images with the properly signed apps

## Future

When JDK 25.0.2 or later is widely available from Temurin/Adoptium, the `--mac-entitlements` flag should work correctly, and this workaround can potentially be simplified or removed.

## References

- OpenJDK Bug: https://bugs.openjdk.org/browse/JDK-8358723
- Related Issues: #339, #546
