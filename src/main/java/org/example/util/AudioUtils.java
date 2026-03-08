package org.example.util;

import javax.sound.sampled.AudioFormat;

public class AudioUtils {

    public static AudioFormat getFormat(){

        return new AudioFormat(
                44100,
                16,
                1,
                true,
                false
        );

    }

}