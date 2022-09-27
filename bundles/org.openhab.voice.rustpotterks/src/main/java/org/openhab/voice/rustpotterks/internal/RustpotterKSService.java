/**
 * Copyright (c) 2010-2022 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.voice.rustpotterks.internal;

import static org.openhab.voice.rustpotterks.internal.RustpotterKSConstants.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.OpenHAB;
import org.openhab.core.audio.AudioFormat;
import org.openhab.core.audio.AudioStream;
import org.openhab.core.common.ThreadPoolManager;
import org.openhab.core.config.core.ConfigurableService;
import org.openhab.core.config.core.Configuration;
import org.openhab.core.voice.KSErrorEvent;
import org.openhab.core.voice.KSException;
import org.openhab.core.voice.KSListener;
import org.openhab.core.voice.KSService;
import org.openhab.core.voice.KSServiceHandle;
import org.openhab.core.voice.KSpottedEvent;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.givimad.rustpotter_java.Endianness;
import io.github.givimad.rustpotter_java.NoiseDetectionMode;
import io.github.givimad.rustpotter_java.RustpotterJava;
import io.github.givimad.rustpotter_java.RustpotterJavaBuilder;
import io.github.givimad.rustpotter_java.VadMode;

/**
 * The {@link RustpotterKSService} is a keyword spotting implementation based on rustpotter.
 *
 * @author Miguel Álvarez - Initial contribution
 */
@NonNullByDefault
@Component(configurationPid = SERVICE_PID, property = Constants.SERVICE_PID + "=" + SERVICE_PID)
@ConfigurableService(category = SERVICE_CATEGORY, label = SERVICE_NAME
        + " Keyword Spotter", description_uri = SERVICE_CATEGORY + ":" + SERVICE_ID)
public class RustpotterKSService implements KSService {
    private static final String RUSTPOTTER_FOLDER = Path.of(OpenHAB.getUserDataFolder(), "rustpotter").toString();
    private final Logger logger = LoggerFactory.getLogger(RustpotterKSService.class);
    private final ScheduledExecutorService executor = ThreadPoolManager.getScheduledPool("OH-voice-rustpotterks");
    private RustpotterKSConfiguration config = new RustpotterKSConfiguration();
    static {
        Logger logger = LoggerFactory.getLogger(RustpotterKSService.class);
        File directory = new File(RUSTPOTTER_FOLDER);
        if (!directory.exists()) {
            if (directory.mkdir()) {
                logger.info("rustpotter dir created {}", RUSTPOTTER_FOLDER);
            }
        }
    }

    @Activate
    protected void activate(ComponentContext componentContext, Map<String, Object> config) {
        modified(config);
    }

    @Modified
    protected void modified(Map<String, Object> config) {
        this.config = new Configuration(config).as(RustpotterKSConfiguration.class);
    }

    @Override
    public String getId() {
        return SERVICE_ID;
    }

    @Override
    public String getLabel(@Nullable Locale locale) {
        return SERVICE_NAME;
    }

    @Override
    public Set<Locale> getSupportedLocales() {
        return Set.of();
    }

    @Override
    public Set<AudioFormat> getSupportedFormats() {
        return Set
                .of(new AudioFormat(AudioFormat.CONTAINER_WAVE, AudioFormat.CODEC_PCM_SIGNED, null, null, null, null));
    }

    @Override
    public KSServiceHandle spot(KSListener ksListener, AudioStream audioStream, Locale locale, String keyword)
            throws KSException {
        logger.debug("Loading library");
        try {
            RustpotterJava.loadLibrary();
        } catch (IOException e) {
            throw new KSException("Unable to load rustpotter lib: " + e.getMessage());
        }
        var audioFormat = audioStream.getFormat();
        var frequency = audioFormat.getFrequency();
        var bitDepth = audioFormat.getBitDepth();
        var channels = audioFormat.getChannels();
        var isBigEndian = audioFormat.isBigEndian();
        if (frequency == null || bitDepth == null || channels == null || isBigEndian == null) {
            throw new KSException(
                    "Missing stream metadata: frequency, bit depth, channels and endianness must be defined.");
        }
        var endianness = isBigEndian ? Endianness.BIG : Endianness.LITTLE;
        logger.debug("Audio wav spec: frequency '{}', bit depth '{}', channels '{}', '{}'", frequency, bitDepth,
                channels, audioFormat.isBigEndian() ? "big-endian" : "little-endian");
        RustpotterJava rustpotter = initRustpotter(frequency, bitDepth, channels, endianness);
        var modelName = keyword.replaceAll("\\s", "_") + ".rpw";
        var modelPath = Path.of(RUSTPOTTER_FOLDER, modelName);
        if (!modelPath.toFile().exists()) {
            throw new KSException("Missing model " + modelName);
        }
        try {
            rustpotter.addWakewordModelFile(modelPath.toString());
        } catch (Exception e) {
            throw new KSException("Unable to load wake word model: " + e.getMessage());
        }
        logger.debug("Model '{}' loaded", modelPath);
        AtomicBoolean aborted = new AtomicBoolean(false);
        executor.submit(() -> processAudioStream(rustpotter, ksListener, audioStream, aborted));
        return new KSServiceHandle() {
            @Override
            public void abort() {
                logger.debug("Stopping service");
                aborted.set(true);
            }
        };
    }

    private RustpotterJava initRustpotter(long frequency, int bitDepth, int channels, Endianness endianness) {
        var rustpotterBuilder = new RustpotterJavaBuilder();
        // audio configs
        rustpotterBuilder.setBitsPerSample(bitDepth);
        rustpotterBuilder.setSampleRate(frequency);
        rustpotterBuilder.setChannels(channels);
        rustpotterBuilder.setEndianness(endianness);
        // detector configs
        rustpotterBuilder.setThreshold(config.threshold);
        rustpotterBuilder.setAveragedThreshold(config.averagedThreshold);
        rustpotterBuilder.setComparatorRef(config.comparatorRef);
        rustpotterBuilder.setComparatorBandSize(config.comparatorBandSize);
        @Nullable
        VadMode vadMode = getVADMode(config.vadMode);
        if (vadMode != null) {
            rustpotterBuilder.setVADMode(vadMode);
            rustpotterBuilder.setVADSensitivity(config.vadSensitivity);
            rustpotterBuilder.setVADDelay(config.vadDelay);
        }
        @Nullable
        NoiseDetectionMode noiseDetectionMode = getNoiseMode(config.noiseDetectionMode);
        if (noiseDetectionMode != null) {
            rustpotterBuilder.setNoiseMode(noiseDetectionMode);
            rustpotterBuilder.setNoiseSensitivity(config.noiseSensitivity);
        }
        rustpotterBuilder.setEagerMode(config.eagerMode);
        // init the detector
        var rustpotter = rustpotterBuilder.build();
        rustpotterBuilder.delete();
        return rustpotter;
    }

    private void processAudioStream(RustpotterJava rustpotter, KSListener ksListener, AudioStream audioStream,
            AtomicBoolean aborted) {
        int numBytesRead;
        var bufferSize = (int) rustpotter.getBytesPerFrame();
        byte[] audioBuffer = new byte[bufferSize];
        int remaining = bufferSize;
        while (!aborted.get()) {
            try {
                numBytesRead = audioStream.read(audioBuffer, bufferSize - remaining, remaining);
                if (aborted.get()) {
                    break;
                }
                if (numBytesRead != remaining) {
                    remaining = remaining - numBytesRead;
                    Thread.sleep(100);
                    continue;
                }
                remaining = bufferSize;
                var result = rustpotter.processBuffer(audioBuffer);
                if (result.isPresent()) {
                    var detection = result.get();
                    logger.debug("keyword '{}' detected with score {}!", detection.getName(), detection.getScore());
                    detection.delete();
                    ksListener.ksEventReceived(new KSpottedEvent());
                }
            } catch (IOException | InterruptedException e) {
                String errorMessage = e.getMessage();
                ksListener.ksEventReceived(new KSErrorEvent(errorMessage != null ? errorMessage : "Unexpected error"));
            }
        }
        rustpotter.delete();
        logger.debug("rustpotter stopped");
    }

    private @Nullable VadMode getVADMode(String mode) {
        switch (mode) {
            case "low-bitrate":
                return VadMode.LOW_BITRATE;
            case "quality":
                return VadMode.QUALITY;
            case "aggressive":
                return VadMode.AGGRESSIVE;
            case "very-aggressive":
                return VadMode.VERY_AGGRESSIVE;
            default:
                return null;
        }
    }

    private @Nullable NoiseDetectionMode getNoiseMode(String mode) {
        switch (mode) {
            case "easiest":
                return NoiseDetectionMode.EASIEST;
            case "easy":
                return NoiseDetectionMode.EASY;
            case "normal":
                return NoiseDetectionMode.NORMAL;
            case "hard":
                return NoiseDetectionMode.HARD;
            case "hardest":
                return NoiseDetectionMode.HARDEST;
            default:
                return null;
        }
    }
}
