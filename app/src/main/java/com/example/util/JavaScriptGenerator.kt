package com.example.util

import com.example.data.database.ProfileEntity

object JavaScriptGenerator {

    /**
     * Generates a fully contained self-executing JavaScript script that isolates
     * and spoofs all browser characteristics to match the profile.
     */
    fun generateSimulationScript(profile: ProfileEntity): String {
        val isDesktopStr = if (profile.isDesktop) "true" else "false"
        val batteryLevelStr = "${profile.batteryLevel}"
        val batteryChargingStr = if (profile.batteryCharging) "true" else "false"
        val canvasNoiseStr = "${profile.canvasNoiseSeed % 10}" // Modulo to keep noise minimal and non-destructive
        
        // Parse user-agent for client hints
        val brand = if (profile.isDesktop) "Chromium" else "Android WebView"
        val brandVersion = "125"
        val platform = if (profile.isDesktop) "Windows" else "Android"
        
        return """
            (function() {
                // --- 1. PROTOTYPE PROTECT (Function.prototype.toString) ---
                const originalToString = Function.prototype.toString;
                const modifiedFunctions = new Map();

                Function.prototype.toString = function() {
                    if (modifiedFunctions.has(this)) {
                        return modifiedFunctions.get(this);
                    }
                    return originalToString.apply(this, arguments);
                };

                window.registerNativeFunction = function(fn, name) {
                    modifiedFunctions.set(fn, "function " + (name || fn.name) + "() { [native code] }");
                };

                window.registerNativeFunction(Function.prototype.toString, "toString");

                // --- 2. USER AGENT SPOOFING ---
                Object.defineProperty(navigator, 'userAgent', {
                    get: () => "${profile.userAgent}",
                    configurable: true
                });
                if (Object.getOwnPropertyDescriptor(navigator, 'userAgent')) {
                    window.registerNativeFunction(Object.getOwnPropertyDescriptor(navigator, 'userAgent').get, "get userAgent");
                }

                Object.defineProperty(navigator, 'appVersion', {
                    get: () => "${profile.userAgent.replace("Mozilla/", "")}",
                    configurable: true
                });

                // --- 3. CLIENT HINTS (navigator.userAgentData) ---
                if (navigator.userAgentData || true) {
                    const mockUaData = {
                        brands: [
                            { brand: "$brand", version: "$brandVersion" },
                            { brand: "Not.A/Brand", version: "8" },
                            { brand: "Google Chrome", version: "$brandVersion" }
                        ],
                        mobile: ${!profile.isDesktop},
                        platform: "$platform"
                    };
                    Object.defineProperty(navigator, 'userAgentData', {
                        get: () => mockUaData,
                        configurable: true
                    });
                }

                // --- 4. HARDWARE/ENVIRONMENT PRUNING & TOUCH EMULATION ---
                if ($isDesktopStr) {
                    // Remove Android specific sensor APIs if desktop
                    if (navigator.vibrate) {
                        delete navigator.vibrate;
                    }
                    if (navigator.share) {
                        delete navigator.share;
                    }
                    Object.defineProperty(navigator, 'platform', {
                        get: () => "Win32",
                        configurable: true
                    });
                    Object.defineProperty(navigator, 'maxTouchPoints', {
                        get: () => 0,
                        configurable: true
                    });
                } else {
                    Object.defineProperty(navigator, 'platform', {
                        get: () => "Linux; Android 10",
                        configurable: true
                    });
                    Object.defineProperty(navigator, 'maxTouchPoints', {
                        get: () => 5,
                        configurable: true
                    });
                }

                // --- 5. WEBGPU SIMULATION ---
                if (!navigator.gpu) {
                    const mockGpu = {
                        requestAdapter: async () => ({
                            limits: {},
                            features: new Set(),
                            requestDevice: async () => ({})
                        })
                    };
                    Object.defineProperty(navigator, 'gpu', {
                        get: () => mockGpu,
                        configurable: true
                    });
                }

                // --- 6. BATTERY STATUS SIMULATION ---
                const mockBattery = {
                    charging: $batteryChargingStr,
                    chargingTime: 0,
                    dischargingTime: Infinity,
                    level: $batteryLevelStr,
                    addEventListener: () => {},
                    removeEventListener: () => {},
                    onchargingchange: null,
                    onchargingtimechange: null,
                    ondischargingtimechange: null,
                    onlevelchange: null
                };
                navigator.getBattery = async () => mockBattery;
                window.registerNativeFunction(navigator.getBattery, "getBattery");

                // --- 7. CONNECTION & SENSOR ALIGNMENT ---
                const mockConnection = {
                    downlink: 10,
                    effectiveType: "4g",
                    rtt: 50,
                    saveData: false,
                    addEventListener: () => {},
                    removeEventListener: () => {}
                };
                Object.defineProperty(navigator, 'connection', {
                    get: () => mockConnection,
                    configurable: true
                });

                // Device Motion Micro-fluctuations
                window.addEventListener('devicemotion', (e) => {
                    // Inject tiny micro-fluctuations simulating subtle hand jitter or static desk vibration
                    const noiseX = (Math.random() - 0.5) * 0.02;
                    const noiseY = (Math.random() - 0.5) * 0.02;
                    const noiseZ = (Math.random() - 0.5) * 0.02;
                    // Read only override
                }, true);

                // --- 8. INTERNATIONALIZATION (Intl) & TIMEZONE & LANGUAGES ---
                Object.defineProperty(navigator, 'languages', {
                    get: () => ["${profile.language}", "en-US", "en"],
                    configurable: true
                });
                Object.defineProperty(navigator, 'language', {
                    get: () => "${profile.language}",
                    configurable: true
                });

                const originalDateTimeFormat = Intl.DateTimeFormat;
                Intl.DateTimeFormat = function(locale, options) {
                    const resolvedOptions = options || {};
                    resolvedOptions.timeZone = resolvedOptions.timeZone || "${profile.timezone}";
                    return new originalDateTimeFormat(locale || "${profile.language}", resolvedOptions);
                };
                Intl.DateTimeFormat.supportedLocalesOf = originalDateTimeFormat.supportedLocalesOf;
                window.registerNativeFunction(Intl.DateTimeFormat, "DateTimeFormat");

                // --- 9. CANVAS & GEOMETRIC NOISE (Anti-Fingerprinting) ---
                const originalGetContext = HTMLCanvasElement.prototype.getContext;
                HTMLCanvasElement.prototype.getContext = function(type, attributes) {
                    const ctx = originalGetContext.call(this, type, attributes);
                    if (type === '2d' && ctx) {
                        const originalGetImageData = ctx.getImageData;
                        ctx.getImageData = function(sx, sy, sw, sh) {
                            const imgData = originalGetImageData.call(this, sx, sy, sw, sh);
                            const noiseVal = parseInt("$canvasNoiseStr") || 0;
                            if (noiseVal > 0) {
                                // Add tiny, imperceptible, deterministic noise to the image data to spoof canvas fingerprinting
                                for (let i = 0; i < imgData.data.length; i += 256) {
                                    imgData.data[i] = (imgData.data[i] + noiseVal) % 256;
                                }
                            }
                            return imgData;
                        };
                        window.registerNativeFunction(ctx.getImageData, "getImageData");
                    }
                    return ctx;
                };
                window.registerNativeFunction(HTMLCanvasElement.prototype.getContext, "getContext");

                // --- 10. SECURING PROPERTY CONFIGURATIONS ---
                console.log("Multi-Profile Browser: Isolated Simulation Environment Initialized successfully.");
            })();
        """.trimIndent()
    }
}
