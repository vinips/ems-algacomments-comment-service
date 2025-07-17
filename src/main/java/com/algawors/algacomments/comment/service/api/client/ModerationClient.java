package com.algawors.algacomments.comment.service.api.client;

import com.algawors.algacomments.comment.service.api.model.ModerationInput;
import com.algawors.algacomments.comment.service.api.model.ModerationOutput;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/api/moderate")
public interface ModerationClient {

    @PostExchange
    public ModerationOutput approve(@RequestBody ModerationInput moderationInput);

}
