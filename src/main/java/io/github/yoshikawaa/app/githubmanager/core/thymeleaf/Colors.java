package io.github.yoshikawaa.app.githubmanager.core.thymeleaf;

public class Colors {

    private static final String COLOR_BLACK = "57607c";
    private static final String COLOR_WHITE = "fff";

    public String letterColor(String backgroundColor) {
        if (backgroundColor == null || backgroundColor.length() != 6) {
            return COLOR_BLACK;
        }

        int red = Integer.parseInt(backgroundColor.substring(0, 2), 16);
        int green = Integer.parseInt(backgroundColor.substring(2, 4), 16);
        int blue = Integer.parseInt(backgroundColor.substring(4, 6), 16);
        int brightness = (red * 299 + green * 587 + blue * 114) / 1000;

        return brightness > 125 ? COLOR_BLACK : COLOR_WHITE;
    }
}
