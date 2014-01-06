package survivalgame;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Config {

    private static final HashMap<String,String> config = new HashMap<String,String>();
    private static final Matcher keyMatcher   = Pattern.compile("[a-z0-9.-]+").matcher("");
    private static final Matcher cliKVMatcher = Pattern.compile("--(.+?)=(.+)").matcher("");

    public static void parseCommandLineArguments( String[] args ) {

        for(String arg : args) {

            cliKVMatcher.reset(arg);
            if(cliKVMatcher.matches()) {
                final String key = cliKVMatcher.group(1);
                final String value = cliKVMatcher.group(2);

                keyMatcher.reset(key);
                if(!keyMatcher.matches()) {
                    System.out.printf("Config key %s does not match '%s'.\n", key, keyMatcher.pattern().pattern());
                    continue;
                }

                setValue(key, value);
            }
        }
    }

    private static void setValue( String key, String value ) {

        if(!value.isEmpty()) {
            config.put(key.toLowerCase(), value);
            System.out.printf("%s = %s\n", key.toLowerCase(), value);
        }
    }

    private static String getValueOrNull( String key ) {

        final String lowerKey = key.toLowerCase();
        return config.containsKey(lowerKey) ? config.get(lowerKey) : null;
    }

    public static String getString( String key, String defaultValue ) {

        final String value = getValueOrNull(key);
        return (value != null) ? value : defaultValue;
    }

    public static int getInt( String key, int defaultValue ) {

        final String value = getValueOrNull(key);
        return (value != null) ? Integer.valueOf(value) : defaultValue;
    }

    public static double getFloat( String key, double defaultValue ) {

        final String value = getValueOrNull(key);
        return (value != null) ? Double.valueOf(value) : defaultValue;
    }

    public static boolean getBoolean( String key, boolean defaultValue ) {

        final String value = getValueOrNull(key);

        if(value == null || value.isEmpty()) {
            return defaultValue;
        }

        switch(value.charAt(0)) {
            case '0':
            case 'n': // no
            case 'N':
            case 'f': // false
            case 'F':
                return false;

            case '1':
            case 'y': // yes
            case 'Y':
            case 't': // true
            case 'T':
                return true;

            default:
                return defaultValue; // Should not happen.
        }
    }
}
