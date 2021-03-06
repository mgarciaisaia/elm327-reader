package elm327.reader;


class Parsers {

    private Parsers() { }

    static short[] toBytes(String data, int bytesSize) {

        if (bytesSize <= 0) {
            throw new IllegalArgumentException("illegal bytesSize");
        }

        short[] bytes = new short[bytesSize];
        int b = bytesSize;
        int index = data.length() -2;

        for (; b > 0 && index > 0; index -= 2, b -= 1) {

            while (data.charAt(index + 1) == ' ') {
                index -= 1;
            }

            String hex = data.substring(index, index + 2);
            bytes[b - 1] = Short.parseShort(hex, 16);
        }

        if (b > 0) {
            throw new NumberFormatException();
        }

        return bytes;
    };

    // A*100/255
    static final Parser<Double> PERCENT = new Parser<Double>(0.0, 100.0) {
        @Override public Double parse(String data) {
            short[] bytes = toBytes(data, 1);
            return (bytes[0] * 100) / 255.0;
        }
    };

    // (A-128) * 100/128
    static final Parser<Double> PERCENT_TRIM = new Parser<Double>(-100.0, 99.22) {
        @Override public Double parse(String data) {
            short[] bytes = toBytes(data, 1);
            return ((bytes[0] - 128) * 100) / 128.0;
        }
    };

    // (B-128)*100/128
    public static Parser<Double> PERCENT_TRIM_B = new Parser<Double>(-100.0, 99.22) {
        @Override Double parse(String data) {
            short[] bytes = toBytes(data, 2);
            return (bytes[1] - 128) * 100 / 128.0;
        }
    };

    // A-125
    static final Parser<Integer> TORQUE = new Parser<Integer>(-125, 125) {
        @Override Integer parse(String data) {
            short[] bytes = toBytes(data, 1);
            return bytes[0] - 125;
        }
    };

    // A-40
    static final Parser<Integer> TEMPERATURE = new Parser<Integer>(-40, 215) {
        @Override Integer parse(String data) {
            short[] bytes = toBytes(data, 1);
            return bytes[0] - 40;
        }
    };

    // A
    static final Parser<Integer> SPEED = new Parser<Integer>(0, 255) {
        @Override Integer parse(String data) {
            short[] bytes = toBytes(data, 1);
            return (int) bytes[0];
        }
    };

    // A*3
    public static final Parser<Integer> KPA_GAUGE = new Parser<Integer>(0, 765) {
        @Override Integer parse(String data) {
            short[] bytes = toBytes(data, 1);
            return bytes[0] * 3;
        }
    };

    // ((A*256)+B)/4
    public static final Parser<Double> RPM = new Parser<Double>(0.0, 16383.75) {
        @Override Double parse(String data) {
            short[] bytes = toBytes(data, 2);
            return ((bytes[0] * 256) + bytes[1]) / 4.0;
        }
    };

    // FIRST_BYTE
    public static final Parser<Integer> FIRST_BYTE = new Parser<Integer>(0, 255) {
        @Override Integer parse(String data) {
            short[] bytes = toBytes(data, 1);
            return (int) bytes[0];
        }
    };

    // (A*256)+B
    public static final Parser<Integer> TO_SHORT = new Parser<Integer>(0, 65535) {
        @Override Integer parse(String data) {
            short[] bytes = toBytes(data, 2);
            return bytes[0] * 256 + bytes[1];
        }
    };

    // ((A*256)+B)*10
    public static final Parser<Integer> TO_SHORT_TIMES_10 = new Parser<Integer>(0, 655350) {
        @Override Integer parse(String data) {
            return TO_SHORT.parse(data) * 10;
        }
    };

    // ((A*256)+B)/10 - 40
    public static final Parser<Double> TEMPERATURE_CATALYST = new Parser<Double>(-40.0, 6513.5) {
        @Override Double parse(String data) {
            return TO_SHORT.parse(data) / 10.0 - 40.0;
        };
    };
}
