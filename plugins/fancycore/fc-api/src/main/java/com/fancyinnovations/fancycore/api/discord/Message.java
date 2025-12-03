package com.fancyinnovations.fancycore.api.discord;

import java.util.List;

public record Message(
        String content,
        List<Embed> embeds
) {

}
